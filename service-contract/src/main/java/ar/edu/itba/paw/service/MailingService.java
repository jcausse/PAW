package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.User;
import java.util.Locale;

public interface MailingService {

    /** Send a welcome email to a newly registered user. */
    void sendWelcomeEmail(User user, Locale locale);

    /** Notify the seller that their listing has been published. */
    void sendListingPublishedEmail(User seller, Listing listing, Locale locale);
}
