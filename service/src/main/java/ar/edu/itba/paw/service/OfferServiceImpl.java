package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.persistence.OfferDao;
import ar.edu.itba.paw.service.exception.NotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OfferServiceImpl implements OfferService {

    private final OfferDao offerDao;

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
    public Offer create(Long listingId, Long buyerId, BigDecimal amount, Boolean isFullPrice, String message) {
        return offerDao.create(listingId, buyerId, amount, isFullPrice, OfferStatus.PENDING, message);
    }

    @Override
    @Transactional
    public Offer accept(Long offerId) {
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
        final Offer offer = offerDao.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer with ID " + offerId));
        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new IllegalStateException("Offer is not pending");
        }
        offerDao.updateStatus(offerId, OfferStatus.REJECTED);
        return offerDao.getById(offerId).orElseThrow();
    }
}