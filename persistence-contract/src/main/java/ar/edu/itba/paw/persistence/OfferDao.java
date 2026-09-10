package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OfferDao {

    Optional<Offer> getById(Long id);

    List<Offer> getByListingId(Long listingId);

    List<Offer> getByBuyerId(Long buyerId);

    List<Offer> getByCreatorId(Long creatorId);

    Offer create(Long listingId, User buyer, BigDecimal amount, Boolean isFullPrice, OfferStatus status, String message);

    boolean updateStatus(Long offerId, OfferStatus status);
}
