package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.OfferService;
import ar.edu.itba.paw.service.exception.NotFoundException;
import ar.edu.itba.paw.webapp.auth.CurrentUser;
import ar.edu.itba.paw.webapp.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
@RequestMapping("/offer")
public class OfferController {

    private final OfferService offerService;
    private final MessageSource messageSource;

    @GetMapping("/{offerId}")
    public ModelAndView viewOffer(@PathVariable Long offerId, @CurrentUser User currentUser) {
        final Offer offer = offerService.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer"));

        final Listing listing = offer.getListing();
        final Long currentUserId = currentUser.getId();

        // Verify the current user is the seller (listing creator)
        if (!listing.getCreator().getId().equals(currentUserId)) {
            throw new ForbiddenException("Not authorized to view this offer");
        }

        var mav = new ModelAndView("offer/decision");
        mav.addObject("offer", offer);
        mav.addObject("currentUser", currentUser);
        return mav;
    }

    @PostMapping("/{offerId}/accept")
    public ModelAndView acceptOffer(@PathVariable Long offerId, @CurrentUser User currentUser) {
        final Offer offer = offerService.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer"));

        final Listing listing = offer.getListing();
        final Long currentUserId = currentUser.getId();

        if (!listing.getCreator().getId().equals(currentUserId)) {
            throw new ForbiddenException("Not authorized to accept this offer");
        }

        offerService.accept(offerId);

        var locale = LocaleContextHolder.getLocale();
        var successMessage = messageSource.getMessage("offer.accepted", null, locale);
        return new ModelAndView("redirect:/listing/" + listing.getId() + "?success=" + successMessage);
    }

    @PostMapping("/{offerId}/reject")
    public ModelAndView rejectOffer(@PathVariable Long offerId, @CurrentUser User currentUser) {
        final Offer offer = offerService.getById(offerId)
            .orElseThrow(() -> NotFoundException.createFor("Offer"));

        final Listing listing = offer.getListing();
        final Long currentUserId = currentUser.getId();

        if (!listing.getCreator().getId().equals(currentUserId)) {
            throw new ForbiddenException("Not authorized to reject this offer");
        }

        offerService.reject(offerId);

        var locale = LocaleContextHolder.getLocale();
        var successMessage = messageSource.getMessage("offer.rejected", null, locale);
        return new ModelAndView("redirect:/listing/" + listing.getId() + "?success=" + successMessage);
    }
}
