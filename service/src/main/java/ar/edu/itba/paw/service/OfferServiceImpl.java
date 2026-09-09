package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.OfferDao;
import ar.edu.itba.paw.service.dto.OfferCreationDto;
import ar.edu.itba.paw.service.exception.BadParameterException;
import ar.edu.itba.paw.service.exception.NotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OfferServiceImpl implements OfferService {

    private final OfferDao offerDao;
    private final UserService userService;
    private final ListingService listingService;

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
    @Transactional
    public Offer create(OfferCreationDto dto) {
        // TODO: Send email notification to seller about the new offer
        Objects.requireNonNull(dto, "OfferCreationDto cannot be null");

        final Listing listing = listingService.getById(dto.listingId());
        if (Objects.equals(dto.buyerId(), listing.getCreator().getId())) {
            throw BadParameterException.create("buyerId", "User cannot buy their own listing");
        }

        final User buyer = userService.getById(dto.buyerId())
                .orElseThrow(() -> new BadParameterException("Invalid buyerId"));

        return offerDao.create(dto.listingId(), buyer, dto.amount(), dto.isFullPrice(), OfferStatus.PENDING, dto.message());
    }

    @Override
    @Transactional
    public Offer accept(Long offerId) {
        // TODO: Send email notification to buyer about offer acceptance
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));
        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new IllegalStateException("Offer is not pending");
        }
        offerDao.updateStatus(offerId, OfferStatus.ACCEPTED);
        return offerDao.getById(offerId).orElseThrow();
    }

    @Override
    @Transactional
    public Offer reject(Long offerId) {
        // TODO: Send email notification to buyer about offer rejection
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));
        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new IllegalStateException("Offer is not pending");
        }
        offerDao.updateStatus(offerId, OfferStatus.REJECTED);
        return offerDao.getById(offerId).orElseThrow();
    }
}
