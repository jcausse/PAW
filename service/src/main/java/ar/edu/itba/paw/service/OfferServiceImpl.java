package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferFilter;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.model.OfferStatusGroup;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.OfferDao;
import ar.edu.itba.paw.service.dto.OfferCreationDto;
import ar.edu.itba.paw.service.dto.OfferFilterDto;
import ar.edu.itba.paw.service.exception.BadParameterException;
import ar.edu.itba.paw.service.exception.NotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;


@Service
@Transactional(readOnly = true)
public class OfferServiceImpl implements OfferService {

    private final OfferDao offerDao;
    private final UserService userService;
    private final ListingService listingService;
    private final MailingService mailingService;

    @Autowired
    public OfferServiceImpl(OfferDao offerDao, UserService userService,
                             @Lazy ListingService listingService, MailingService mailingService) {
        this.offerDao = offerDao;
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
    public Offer accept(Long offerId) {
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new BadParameterException("Offer is not pending");
        }

        listingService.purchase(offer.getListing().getId(), offer.getBuyer().getId(), offer.getMessage());
        rejectPendingOffersForListing(offer.getListing().getId(), offerId);
        offerDao.updateStatus(offerId, OfferStatus.ACCEPTED);

        // Send email notification to buyer about offer acceptance
        mailingService.sendOfferAcceptedEmail(offer.getBuyer(), offer.getListing().getCreator(), offer.getListing(), offer, LocaleContextHolder.getLocale());

        return offerDao.getById(offerId).orElseThrow();
    }

    @Override
    @Transactional
    public Offer reject(Long offerId) {
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new BadParameterException("Offer is not pending");
        }

        offerDao.updateStatus(offerId, OfferStatus.REJECTED);

        // Send email notification to buyer about offer rejection
        mailingService.sendOfferRejectedEmail(offer.getBuyer(), offer.getListing(), offer, LocaleContextHolder.getLocale());

        return offerDao.getById(offerId).orElseThrow();
    }

    @Override
    @Transactional
    public Offer withdraw(Long offerId, Long buyerId) {
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));

        if (!Objects.equals(offer.getBuyer().getId(), buyerId)) {
            throw new BadParameterException("Only the buyer can withdraw this offer");
        }

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new BadParameterException("Offer is not pending");
        }

        offerDao.withdraw(offerId, buyerId);

        // Send email notification to seller about offer withdrawal
        mailingService.sendOfferWithdrawnEmail(offer.getListing().getCreator(), offer.getBuyer(), offer.getListing(), offer, LocaleContextHolder.getLocale());

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
    public int getPendingOffersCount(User seller) {
        return offerDao.countPendingBySeller(seller);
    }
}
