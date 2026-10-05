package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Role;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.exception.BadParameterException;
import ar.edu.itba.paw.service.exception.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final ListingService listingService;
    private final OfferService offerService;
    private final UserService userService;

    @Override
    @Transactional
    public boolean takeDownListing(@NonNull Long listingId) {
        LOGGER.info("Admin taking down listing id={}", listingId);
        try {
            listingService.cancelByAdmin(listingId);
            return true;
        } catch (NotFoundException e) {
            LOGGER.warn("Listing {} not found for takedown", listingId);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean takeDownOffer(@NonNull Long offerId) {
        LOGGER.info("Admin taking down offer id={}", offerId);
        try {
            offerService.cancelByAdmin(offerId);
            return true;
        } catch (NotFoundException | BadParameterException e) {
            LOGGER.warn("Offer {} cannot be taken down: {}", offerId, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public User grantRole(@NonNull String usernameOrEmail, @NonNull Role role) {
        LOGGER.info("Admin granting role {} to user '{}'", role, usernameOrEmail);
        final var user = userService.getByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> {
                    LOGGER.warn("User '{}' not found for granting role {}", usernameOrEmail, role);
                    return NotFoundException.createFor("User");
                });

        userService.addRole(user, role);
        return user;
    }

    @Override
    @Transactional
    public User suspendUser(@NonNull String usernameOrEmail) {
        LOGGER.info("Admin suspending user '{}'", usernameOrEmail);
        final var user = userService.getByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> {
                    LOGGER.warn("User '{}' not found for suspension", usernameOrEmail);
                    return NotFoundException.createFor("User");
                });

        // TODO: Call UserService.suspend(user) here once implemented: boolean suspended = userService.suspend(user);

        return user;
    }
}
