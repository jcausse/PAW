package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Role;
import ar.edu.itba.paw.model.User;

public interface AdminService {

    boolean takeDownListing(Long listingId);

    boolean takeDownOffer(Long offerId);

    User grantRole(String usernameOrEmail, Role role);

    User suspendUser(String usernameOrEmail);
}
