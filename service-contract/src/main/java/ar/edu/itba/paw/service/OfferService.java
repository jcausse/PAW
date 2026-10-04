package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.File;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.dto.OfferCreationDto;
import ar.edu.itba.paw.service.dto.OfferFilterDto;
import ar.edu.itba.paw.model.OfferRating;

import java.util.List;
import java.util.Optional;

public interface OfferService {

    Optional<Offer> getById(Long id);

    Optional<Offer> getByListingAndBuyer(Listing listing, User buyer);

    Page<Offer> get(OfferFilterDto filter);

    Offer create(OfferCreationDto dto);

    Offer accept(Long offerId, Long currentUserId);

    Offer reject(Long offerId, Long currentUserId);

    Offer withdraw(Long offerId, Long currentUserId);

    List<Offer> rejectPendingOffersForListing(Long listingId, Long exceptOfferId);

    int getPendingOffersCount(User seller);

    Offer uploadProofOfPayment(Long offerId, Long buyerId, String filename, String alt, String contentType, byte[] data);

    Offer uploadProofOfShipping(Long offerId, Long sellerId, String filename, String alt, String contentType, byte[] data, String trackingNumber);

    Offer confirmPayment(Long offerId, Long sellerId);

    Optional<File> getProofOfPaymentFile(Long offerId);

    Optional<File> getProofOfShippingFile(Long offerId);

    Offer rate(Long offerId, User currentUser, OfferRating rating);

    @SuppressWarnings("unused") 
    void autoRatePendingOffers();

    void cancel(Long offerId);
}
