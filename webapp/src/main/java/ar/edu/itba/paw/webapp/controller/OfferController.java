package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.OfferService;
import ar.edu.itba.paw.webapp.auth.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
@RequestMapping("/offer")
public class OfferController {

    private final OfferService offerService;
    private final ListingService listingService;
    private final MessageSource messageSource;

    @GetMapping("/{offerId}")
    public ModelAndView viewOffer(@PathVariable Long offerId) {
        final Offer offer = offerService.getById(offerId)
            .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        final Listing listing = offer.getListing();
        final Long currentUserId = getCurrentUserId();

        // Verify the current user is the seller (listing creator)
        if (!listing.getCreator().getId().equals(currentUserId)) {
            throw new SecurityException("Not authorized to view this offer");
        }

        var mav = new ModelAndView("offer/decision");
        mav.addObject("offer", offer);
        return mav;
    }

    @PostMapping("/{offerId}/accept")
    public ModelAndView acceptOffer(@PathVariable Long offerId) {
        final Offer offer = offerService.getById(offerId)
            .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        final Listing listing = offer.getListing();
        final Long currentUserId = getCurrentUserId();

        if (!listing.getCreator().getId().equals(currentUserId)) {
            throw new SecurityException("Not authorized to accept this offer");
        }

        offerService.accept(offerId);

        // TODO: Send email notification to buyer about offer acceptance

        var locale = LocaleContextHolder.getLocale();
        var successMessage = messageSource.getMessage("offer.accepted", null, locale);
        return new ModelAndView("redirect:/listing/" + listing.getId() + "?success=" + successMessage);
    }

    @PostMapping("/{offerId}/reject")
    public ModelAndView rejectOffer(@PathVariable Long offerId) {
        final Offer offer = offerService.getById(offerId)
            .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        final Listing listing = offer.getListing();
        final Long currentUserId = getCurrentUserId();

        if (!listing.getCreator().getId().equals(currentUserId)) {
            throw new SecurityException("Not authorized to reject this offer");
        }

        offerService.reject(offerId);

        // TODO: Send email notification to buyer about offer rejection

        var locale = LocaleContextHolder.getLocale();
        var successMessage = messageSource.getMessage("offer.rejected", null, locale);
        return new ModelAndView("redirect:/listing/" + listing.getId() + "?success=" + successMessage);
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUserDetails userDetails) {
            return userDetails.getDomainUser().getId();
        }
        throw new IllegalStateException("No authenticated user");
    }
}