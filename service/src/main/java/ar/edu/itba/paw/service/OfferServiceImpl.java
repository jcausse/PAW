package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferFilter;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.model.OfferStatusGroup;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.model.File;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.OfferRating;
import ar.edu.itba.paw.persistence.OfferDao;
import ar.edu.itba.paw.persistence.FileDao;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.service.dto.OfferCreationDto;
import ar.edu.itba.paw.service.dto.OfferFilterDto;
import ar.edu.itba.paw.service.exception.BadParameterException;
import ar.edu.itba.paw.service.exception.ForbiddenException;
import ar.edu.itba.paw.service.exception.NotFoundException;

import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;


@Service
@Transactional(readOnly = true)
public class OfferServiceImpl implements OfferService {

    private final OfferDao offerDao;
    private final FileDao fileDao;
    private final UserDao userDao;
    private final UserService userService;
    private final ListingService listingService;
    private final MailingService mailingService;
    private static final Duration RATING_AUTO_ASSIGN_DELAY = Duration.ofDays(14);

    @Autowired
    public OfferServiceImpl(OfferDao offerDao, FileDao fileDao, UserDao userDao,
                            UserService userService, @Lazy ListingService listingService, MailingService mailingService) {
        this.offerDao = offerDao;
        this.fileDao = fileDao;
        this.userDao = userDao;
        this.userService = userService;
        this.listingService = listingService;
        this.mailingService = mailingService;
    }

    @Override
    public Optional<Offer> getById(Long id) {
        return offerDao.getById(id);
    }

    @Override
    public Optional<Offer> getByListingAndBuyer(Listing listing, User buyer) {
        if (buyer == null) return Optional.empty();
        return offerDao.getByListingAndBuyer(listing, buyer);
    }

    @Override
    public Page<Offer> get(OfferFilterDto dto) {
        Objects.requireNonNull(dto, "OfferFilterDto cannot be null");

        final var filter = OfferFilter.builder()
            .page(sanitizePage(dto.page()))
            .pageSize(dto.pageSize())
            .sellerId(dto.sellerId())
            .buyerId(dto.buyerId())
            .status(parseStatusGroup(dto.statusGroup()))
            .build();

        return offerDao.search(filter);
    }

    private static int sanitizePage(final Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private static List<OfferStatus> parseStatusGroup(final String value) {
        final var statusGroup = value == null || value.isBlank()
            ? null
            : OfferStatusGroup.fromString(value).orElse(null);

        return statusGroup == null ? OfferStatusGroup.PENDING.toStatusList() : statusGroup.toStatusList();
    }

    @Override
    @Transactional
    public Offer create(OfferCreationDto dto) {
        Objects.requireNonNull(dto, "OfferCreationDto cannot be null");

        final Listing listing = listingService.getById(dto.listingId());
        if (Objects.equals(dto.buyerId(), listing.getCreator().getId())) {
            throw BadParameterException.create("buyerId", "User cannot buy their own listing");
        }

        final User buyer = userService.getById(dto.buyerId())
                .orElseThrow(() -> new BadParameterException("Invalid buyerId"));

        final Offer offer = offerDao.create(
            dto.listingId(),
            buyer,
            dto.amount(),
            dto.isFullPrice(),
            OfferStatus.PENDING,
            dto.message(),
            Instant.now()
        );

        // Send email notification to seller about the new offer
        mailingService.sendNewOfferEmail(listing.getCreator(), buyer, listing, offer, LocaleContextHolder.getLocale());

        return offer;
    }

    @Override
    @Transactional
    public Offer accept(Long offerId, Long currentUserId) {
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));

        if (!offer.getListing().getCreator().getId().equals(currentUserId)) {
            throw new ForbiddenException("Not authorized to accept this offer");
        }

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new BadParameterException("Offer is not pending");
        }

        listingService.pendingTransaction(offer.getListing().getId(), offer.getBuyer().getId(), offer.getMessage());
        rejectPendingOffersForListing(offer.getListing().getId(), offerId);
        offerDao.updateStatus(offerId, OfferStatus.PENDING_PAYMENT);

        // Send email notification to buyer about offer acceptance (pending payment)
        mailingService.sendOfferPendingPaymentEmail(offer.getBuyer(), offer.getListing().getCreator(), offer.getListing(), offer, LocaleContextHolder.getLocale());
        // Send email notification to seller about pending transaction
        mailingService.sendPendingTransactionEmail(offer.getListing().getCreator(), offer.getBuyer(), offer.getListing(), offer, LocaleContextHolder.getLocale());

        // FIXME double query
        return offerDao.getById(offerId).orElseThrow();
    }

    @Override
    @Transactional
    public Offer reject(Long offerId, Long currentUserId) {
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));

        if (!offer.getListing().getCreator().getId().equals(currentUserId)) {
            throw new ForbiddenException("Not authorized to reject this offer");
        }

        switch (offer.getStatus()) {
           	case OfferStatus.PENDING -> {}
            case OfferStatus.PENDING_PAYMENT -> {
                // Reset listing to ACTIVE
                listingService.updateStatus(offer.getListing().getId(), ListingStatus.ACTIVE);
            }
            default -> throw new BadParameterException("Offer cannot be rejected in its current state");
        };

        offerDao.updateStatus(offerId, OfferStatus.REJECTED);

        // Send email notification to buyer about offer rejection
        mailingService.sendOfferRejectedEmail(offer.getBuyer(), offer.getListing(), offer, LocaleContextHolder.getLocale());

        // FIXME double query
        return offerDao.getById(offerId).orElseThrow();
    }

    @Override
    @Transactional
    public Offer withdraw(Long offerId, Long currentUserId) {
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));

        if (!offer.getBuyer().getId().equals(currentUserId)) {
            throw new BadParameterException("Not authorized to withdraw this offer");
        }

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new BadParameterException("Offer is not pending");
        }

        offerDao.withdraw(offerId, currentUserId);

        // Send email notification to seller about offer withdrawal
        mailingService.sendOfferWithdrawnEmail(offer.getListing().getCreator(), offer.getBuyer(), offer.getListing(), offer, LocaleContextHolder.getLocale());

        // FIXME double query
        return offerDao.getById(offerId).orElseThrow();
    }

    @Override
    @Transactional
    public List<Offer> rejectPendingOffersForListing(Long listingId, Long exceptOfferId) {
        final List<Offer> rejected = offerDao.rejectPendingOffers(listingId, exceptOfferId);
        final Locale locale = LocaleContextHolder.getLocale();
        for (Offer offer : rejected) {
            mailingService.sendOfferRejectedEmail(offer.getBuyer(), offer.getListing(), offer, locale);
        }
        return rejected;
    }

    @Override
    @Transactional
    public Offer uploadProofOfPayment(Long offerId, Long buyerId, String filename, String alt, String contentType, byte[] data) {
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));

        if (!offer.getBuyer().getId().equals(buyerId)) {
            throw new BadParameterException("Only the buyer can upload proof of payment");
        }

        if (offer.getStatus() != OfferStatus.PENDING_PAYMENT) {
            throw new BadParameterException("Offer is not pending payment");
        }

        if (data == null || data.length == 0) {
            throw new BadParameterException("File is empty");
        }

        if (contentType == null || (!contentType.startsWith("image/") && !"application/pdf".equals(contentType))) {
            throw new BadParameterException("Only image and PDF files are allowed");
        }

        final File file = fileDao.create(filename, alt, contentType, data);
        offerDao.updateProofOfPaymentId(offerId, file.getId());

        // Notify seller that proof of payment was uploaded
        mailingService.sendProofOfPaymentUploadedEmail(offer.getListing().getCreator(), offer.getBuyer(), offer.getListing(), offer, LocaleContextHolder.getLocale());

        return offerDao.getById(offerId).orElseThrow();
    }

    @Override
    @Transactional
    public Offer uploadProofOfShipping(Long offerId, Long sellerId, String filename, String alt, String contentType, byte[] data, String trackingNumber) {
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));

        if (!offer.getListing().getCreator().getId().equals(sellerId)) {
            throw new BadParameterException("Only the seller can upload proof of shipping");
        }

        if (offer.getStatus() != OfferStatus.PENDING_PAYMENT) {
            throw new BadParameterException("Offer is not pending payment");
        }

        if ((data == null || data.length == 0) && (trackingNumber == null || trackingNumber.isBlank())) {
            throw new BadParameterException("Either a file or a tracking number must be provided");
        }

        if (data != null && data.length > 0) {
            if (contentType == null || (!contentType.startsWith("image/") && !"application/pdf".equals(contentType))) {
                throw new BadParameterException("Only image and PDF files are allowed");
            }
        }

        Long proofOfShippingId = null;
        if (data != null && data.length > 0) {
            final File file = fileDao.create(filename, alt, contentType, data);
            proofOfShippingId = file.getId();
        }

        offerDao.updateProofOfShipping(offerId, proofOfShippingId, trackingNumber);

        // Notify buyer that proof of shipping was uploaded
        mailingService.sendProofOfShippingUploadedEmail(offer.getBuyer(), offer.getListing().getCreator(), offer.getListing(), offer, LocaleContextHolder.getLocale());

        return offerDao.getById(offerId).orElseThrow();
    }

    @Override
    @Transactional
    public Offer confirmPayment(Long offerId, Long sellerId) {
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));

        if (!offer.getListing().getCreator().getId().equals(sellerId)) {
            throw new BadParameterException("Only the seller can confirm payment");
        }

        if (offer.getStatus() != OfferStatus.PENDING_PAYMENT) {
            throw new BadParameterException("Offer is not pending payment");
        }

        listingService.purchase(offer.getListing().getId(), offer.getBuyer().getId(), offer.getMessage());
        offerDao.markAccepted(offerId, Instant.now());

        return offerDao.getById(offerId).orElseThrow();
    }

    @Override
    public Optional<File> getProofOfPaymentFile(Long offerId) {
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));

        if (offer.getProofOfPaymentId() == null) {
            return Optional.empty();
        }

        return fileDao.getById(offer.getProofOfPaymentId());
    }

    @Override
    public Optional<File> getProofOfShippingFile(Long offerId) {
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));

        if (offer.getProofOfShippingId() == null) {
            return Optional.empty();
        }

        return fileDao.getById(offer.getProofOfShippingId());
    }

    @Override
    public int getPendingOffersCount(User seller) {
        return offerDao.countPendingBySeller(seller);
    }

    @Override
    @Transactional
    public Offer rate(Long offerId, User currentUser, OfferRating rating) {
        final Offer offer = getById(offerId)
                .orElseThrow(() -> NotFoundException.createFor("Offer"));

        if (offer.getStatus() != OfferStatus.ACCEPTED) {
            throw new BadParameterException("Solo se pueden calificar ofertas aceptadas");
        }

        final boolean isBuyer = offer.getBuyer().getId().equals(currentUser.getId());
        final boolean isSeller = offer.getListing().getCreator().getId().equals(currentUser.getId());

        if (!isBuyer && !isSeller) {
            throw new ForbiddenException("No participaste en esta oferta");
        }

        if (isBuyer) {
            final boolean updated = offerDao.setSellerRating(offerId, rating);
            if (!updated) {
                throw new BadParameterException("Ya calificaste esta oferta");
            }
            userDao.incrementSellerRatingCounter(offer.getListing().getCreator().getId(), rating);
        } else {
            final boolean updated = offerDao.setBuyerRating(offerId, rating);
            if (!updated) {
                throw new BadParameterException("Ya calificaste esta oferta");
            }
            userDao.incrementBuyerRatingCounter(offer.getBuyer().getId(), rating);
        }

        return getById(offerId).orElseThrow();
    }

    @Override
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void autoRatePendingOffers() {
        final Instant cutoff = Instant.now().minus(RATING_AUTO_ASSIGN_DELAY);
        final List<Offer> pending = offerDao.getAcceptedUnratedBefore(cutoff);

        for (Offer offer : pending) {
            if (offer.getSellerRating().isEmpty()) {
                final boolean updated = offerDao.setSellerRating(offer.getId(), OfferRating.POSITIVE);
                if (updated) {
                    userDao.incrementSellerRatingCounter(offer.getListing().getCreator().getId(), OfferRating.POSITIVE);
                }
            }
            if (offer.getBuyerRating().isEmpty()) {
                final boolean updated = offerDao.setBuyerRating(offer.getId(), OfferRating.POSITIVE);
                if (updated) {
                    userDao.incrementBuyerRatingCounter(offer.getBuyer().getId(), OfferRating.POSITIVE);
                }
            }
        }
    }
}
