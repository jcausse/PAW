package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Offer;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OfferService {

    Optional<Offer> getById(Long id);

    List<Offer> getByListingId(Long listingId);

    List<Offer> getByBuyerId(Long buyerId);

    Offer create(Long listingId, Long buyerId, BigDecimal amount, Boolean isFullPrice, String message);

    Offer accept(Long offerId);

    Offer reject(Long offerId);
}
