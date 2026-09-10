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

        BigDecimal amount;
        boolean isFullPrice;

        if ("full".equals(form.getOfferType())) {
         amount = listing.getPrice().getAmount();
         isFullPrice = true;
        } else {
         if (form.getCustomAmount() == null || form.getCustomAmount().compareTo(BigDecimal.ZERO) <= 0) {
             bindingResult.rejectValue("customAmount", "NotNull.checkoutForm.customAmount");
             return new ModelAndView("checkout/index").addObject("listing", listing);
         }
         if (form.getCustomAmount().compareTo(listing.getPrice().getAmount()) > 0) {
             bindingResult.rejectValue("customAmount", "Max.checkoutForm.customAmount", new Object[]{listing.getPrice().getAmount()}, "Offer amount cannot exceed listing price");
             return new ModelAndView("checkout/index").addObject("listing", listing);
         }
         amount = form.getCustomAmount();
         isFullPrice = false;
        }

        OfferCreationDto offerDto = new OfferCreationDto(listing.getId(), buyerId, amount, isFullPrice, form.getMessage());
        offerService.create(offerDto);
        listingService.purchase(form.getListingId(), buyerId, form.getMessage());

        return new ModelAndView("redirect:/listing/" + form.getListingId());
    }
}
