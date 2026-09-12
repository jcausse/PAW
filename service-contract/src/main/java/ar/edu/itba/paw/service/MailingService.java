package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.User;
import java.util.Locale;

public interface MailingService {

    /** Send a welcome email to a newly registered user. */
    void sendWelcomeEmail(User user, Locale locale);

    /** Notify the seller that their listing has been published. */
    void sendListingPublishedEmail(User seller, Listing listing, Locale locale);

    /** Notify the seller that their listing was purchased, including the buyer contact email and message. */
    void sendPurchaseSellerEmail(User seller, User buyer, Listing listing, String message, Locale locale);

    /** Confirm the purchase to the buyer, including the seller contact email. */
    void sendPurchaseBuyerEmail(User buyer, User seller, Listing listing, Locale locale);

    /** Notify the seller that they received a new offer on their listing. */
    void sendNewOfferEmail(User seller, User buyer, Listing listing, Offer offer, Locale locale);

    /** Notify the buyer that their offer was accepted. */
    void sendOfferAcceptedEmail(User buyer, User seller, Listing listing, Offer offer, Locale locale);

    /** Notify the buyer that their offer was rejected. */
    void sendOfferRejectedEmail(User buyer, Listing listing, Offer offer, Locale locale);
}
