package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferFilter;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.model.User;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OfferDao {

    Optional<Offer> getById(Long id);

    Optional<Offer> getByListingAndBuyer(Listing listing, User buyer);

    Page<Offer> search(OfferFilter filter);

    Offer create(Long listingId, User buyer, BigDecimal amount, Boolean isFullPrice, OfferStatus status, String message, Instant createdAt);

    boolean updateStatus(Long offerId, OfferStatus status);

    boolean withdraw(Long offerId, Long buyerId);

    List<Offer> rejectPendingOffers(Long listingId, Long exceptOfferId);

    int countPendingBySeller(User seller);
}
