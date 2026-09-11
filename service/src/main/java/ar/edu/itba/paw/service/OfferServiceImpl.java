package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.OfferDao;
import ar.edu.itba.paw.service.dto.IncomingOffersDto;
import ar.edu.itba.paw.service.dto.OfferCreationDto;
import ar.edu.itba.paw.service.exception.BadParameterException;
import ar.edu.itba.paw.service.exception.NotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OfferServiceImpl implements OfferService {

    private final OfferDao offerDao;
    private final UserService userService;
    private final ListingService listingService;
    private final MailingService mailingService;

    @Override
    public Optional<Offer> getById(Long id) {
        return offerDao.getById(id);
    }

    @Override
    public List<Offer> getByListingId(Long listingId) {
        return offerDao.getByListingId(listingId);
    }

    @Override
    public List<Offer> getByBuyerId(Long buyerId) {
        return offerDao.getByBuyerId(buyerId);
    }

    @Override
    public IncomingOffersDto getIncomingOffersForUser(Long userId) {
        final List<Offer> allOffers = offerDao.getByCreatorId(userId);
        final List<Offer> pending = allOffers.stream()
                .filter(o -> o.getStatus() == OfferStatus.PENDING)
                .toList();
        final List<Offer> resolved = allOffers.stream()
                .filter(o -> o.getStatus() != OfferStatus.PENDING)
                .toList();
        return new IncomingOffersDto(pending, resolved);
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

        final Offer offer = offerDao.create(dto.listingId(), buyer, dto.amount(), dto.isFullPrice(), OfferStatus.PENDING, dto.message());

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
        offerDao.rejectOtherOffers(offer.getListing().getId(), offerId);
        offerDao.updateStatus(offerId, OfferStatus.ACCEPTED);

        // Send email notification to buyer about offer acceptance
        mailingService.sendOfferAcceptedEmail(offer.getBuyer(), offer.getListing().getCreator(), offer.getListing(), offer, LocaleContextHolder.getLocale());

        // Send email notifications to buyers of other offers about offer rejection
        final List<Offer> otherOffers = offerDao.getByListingId(offer.getListing().getId());
        for (Offer otherOffer : otherOffers) {
            if (!Objects.equals(otherOffer.getId(), offerId) && otherOffer.getStatus() == OfferStatus.REJECTED) {
                mailingService.sendOfferRejectedEmail(otherOffer.getBuyer(), offer.getListing(), otherOffer, LocaleContextHolder.getLocale());
            }
        }

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
}
