package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.dto.OfferCreationDto;
import ar.edu.itba.paw.service.dto.OfferFilterDto;

import java.util.List;
import java.util.Optional;

public interface OfferService {

    Optional<Offer> getById(Long id);

    Optional<Offer> getByListingAndBuyer(Listing listing, User buyer);

    Page<Offer> get(OfferFilterDto filter);

    Offer create(OfferCreationDto dto);

    Offer accept(Long offerId);

    Offer reject(Long offerId);

    Offer withdraw(Long offerId, Long buyerId);

    List<Offer> rejectPendingOffersForListing(Long listingId, Long exceptOfferId);

    int getPendingOffersCount(User seller);
}
