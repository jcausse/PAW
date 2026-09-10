package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.OfferService;
import ar.edu.itba.paw.webapp.auth.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/account")
public class AccountController {

    private final ListingService listingService;
    private final OfferService offerService;

    private Optional<User> getCurrentUser() {
        final var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthUserDetails) {
            return Optional.of(((AuthUserDetails) auth.getPrincipal()).getDomainUser());
        }
        return Optional.empty();
    }

    @GetMapping
    public ModelAndView index() {
        final var currentUser = getCurrentUser();
        final User user = currentUser.orElseThrow();
        return new ModelAndView("account/index")
                .addObject("user", user)
                .addObject("currentUser", currentUser);
    }

    @GetMapping("/listings")
    public ModelAndView listings() {
        final var currentUser = getCurrentUser();
        final User user = currentUser.orElseThrow();
        final List<Listing> listings = listingService.getListingsForUser(user.getId());
        return new ModelAndView("account/listings")
                .addObject("listings", listings)
                .addObject("user", user)
                .addObject("currentUser", currentUser);
    }

    @GetMapping("/incoming-offers")
    public ModelAndView incomingOffers() {
        final var currentUser = getCurrentUser();
        final User user = currentUser.orElseThrow();
        final List<Offer> offers = offerService.getIncomingOffersForUser(user.getId());
        return new ModelAndView("account/incomingOffers")
                .addObject("offers", offers)
                .addObject("user", user)
                .addObject("currentUser", currentUser);
    }
}