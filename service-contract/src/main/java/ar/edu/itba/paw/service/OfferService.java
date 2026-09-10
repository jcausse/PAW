package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.service.dto.OfferCreationDto;
import java.util.List;
import java.util.Optional;

public interface OfferService {

    Optional<Offer> getById(Long id);

    List<Offer> getByListingId(Long listingId);

    List<Offer> getByBuyerId(Long buyerId);

    List<Offer> getIncomingOffersForUser(Long userId);

    Offer create(OfferCreationDto dto);

    Offer accept(Long offerId);

    Offer reject(Long offerId);
}
