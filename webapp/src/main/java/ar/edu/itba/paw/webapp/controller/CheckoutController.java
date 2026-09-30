package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.OfferService;
import ar.edu.itba.paw.service.dto.ListingFilterDto;
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
    public ModelAndView checkout(@RequestParam("listingId") Long listingId, @ModelAttribute("checkoutForm") CheckoutForm form, @CurrentUser(required = false) User currentUser) {
        Listing listing = listingService.getById(listingId);
        form.setListingId(listingId);
        form.setOfferType("full");

        var mav = new ModelAndView("checkout/index");
        mav.addObject("listing", listing);

        if (currentUser != null && listing.isAcceptsTrade()) {
            var filter = new ListingFilterDto(
                null, null, null, null, null, null,
                null, null,
                currentUser.getId(),
                ListingStatus.ACTIVE.name(),
                1,
                100,
                null
            );
            var userListings = listingService.search(filter).getContent();
            mav.addObject("userListings", userListings);
        }

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
            if (currentUser != null && listing.isAcceptsTrade()) {
                var filter = new ListingFilterDto(
                    null, null, null, null, null, null,
                    null, null,
                    currentUser.getId(),
                    ListingStatus.ACTIVE.name(),
                    1,
                    100,
                    null
                );
                var userListings = listingService.search(filter).getContent();
                return new ModelAndView("checkout/index")
                        .addObject("listing", listing)
                        .addObject("userListings", userListings);
            }
            return new ModelAndView("checkout/index")
                    .addObject("listing", listing);
        }

        Long buyerId = currentUser.getId();

        BigDecimal amount;
        boolean isFullPrice;
        Long offeredListingId = null;

        if ("full".equals(form.getOfferType())) {
            amount = listing.getPrice().getAmount();
            isFullPrice = true;
        } else if ("custom".equals(form.getOfferType())) {
            if (form.getCustomAmount() == null || form.getCustomAmount().compareTo(BigDecimal.ZERO) <= 0) {
                bindingResult.rejectValue("customAmount", "NotNull.checkoutForm.customAmount");
                return withUserListings(listing, currentUser);
            }
            if (form.getCustomAmount().compareTo(listing.getPrice().getAmount()) > 0) {
                bindingResult.rejectValue("customAmount", "Max.checkoutForm.customAmount", new Object[]{listing.getPrice().getAmount()}, "Offer amount cannot exceed listing price");
                return withUserListings(listing, currentUser);
            }
            amount = form.getCustomAmount();
            isFullPrice = false;
        } else if ("trade".equals(form.getOfferType())) {
            if (form.getOfferedListingId() == null) {
                bindingResult.rejectValue("offeredListingId", "NotNull.checkoutForm.offeredListingId");
                return withUserListings(listing, currentUser);
            }
            if (form.getTradeAmount() == null || form.getTradeAmount().compareTo(BigDecimal.ZERO) < 0) {
                bindingResult.rejectValue("tradeAmount", "NotNull.checkoutForm.tradeAmount");
                return withUserListings(listing, currentUser);
            }
            if (form.getTradeAmount().compareTo(listing.getPrice().getAmount()) > 0) {
                bindingResult.rejectValue("tradeAmount", "Max.checkoutForm.tradeAmount", new Object[]{listing.getPrice().getAmount()}, "Trade amount cannot exceed listing price");
                return withUserListings(listing, currentUser);
            }
            amount = form.getTradeAmount();
            isFullPrice = false;
            offeredListingId = form.getOfferedListingId();
        } else {
            bindingResult.rejectValue("offerType", "Invalid.checkoutForm.offerType");
            return withUserListings(listing, currentUser);
        }

        OfferCreationDto offerDto = new OfferCreationDto(listing.getId(), buyerId, amount, isFullPrice, form.getMessage(), offeredListingId);
        offerService.create(offerDto);

        return new ModelAndView("redirect:/listing/" + form.getListingId());
    }

    private ModelAndView withUserListings(Listing listing, User currentUser) {
        if (currentUser != null && listing.isAcceptsTrade()) {
            var filter = new ListingFilterDto(
                null, null, null, null, null, null,
                null, null,
                currentUser.getId(),
                ListingStatus.ACTIVE.name(),
                1,
                100,
                null
            );
            var userListings = listingService.search(filter).getContent();
            return new ModelAndView("checkout/index")
                    .addObject("listing", listing)
                    .addObject("userListings", userListings);
        }
        return new ModelAndView("checkout/index").addObject("listing", listing);
    }
}
