package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.OfferService;
import ar.edu.itba.paw.service.dto.OfferCreationDto;
import ar.edu.itba.paw.webapp.auth.CurrentUser;
import ar.edu.itba.paw.webapp.form.CheckoutForm;
import java.math.BigDecimal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final ListingService listingService;
    private final OfferService offerService;

    @GetMapping
    public ModelAndView checkout(@RequestParam("listingId") Long listingId, @ModelAttribute("checkoutForm") CheckoutForm form) {
        Listing listing = listingService.getById(listingId);
        form.setListingId(listingId);
        form.setOfferType("full");

        var mav = new ModelAndView("checkout/index");
        mav.addObject("listing", listing);
        return mav;
    }

    @PostMapping
    public ModelAndView checkoutPost(
            @Valid @ModelAttribute("checkoutForm") CheckoutForm form,
            BindingResult bindingResult,
            @CurrentUser User currentUser
            ) {
        Listing listing = listingService.getById(form.getListingId());

        if (bindingResult.hasErrors()) {
            return new ModelAndView("checkout/index")
                    .addObject("listing", listing);
        }

        Long buyerId = currentUser.getId();
        BigDecimal amount = "full".equals(form.getOfferType()) ? listing.getPrice().getAmount() : form.getCustomAmount();
        Boolean isFullPrice = "full".equals(form.getOfferType());

        try {
            offerService.createOffer(listing.getId(), buyerId, amount, isFullPrice, form.getMessage());
        } catch (ar.edu.itba.paw.service.exception.BadParameterException e) {
            bindingResult.rejectValue("customAmount", "customAmount", e.getMessage());
            return new ModelAndView("checkout/index").addObject("listing", listing);
        }

        return new ModelAndView("redirect:/listing/" + form.getListingId());
    }
}
