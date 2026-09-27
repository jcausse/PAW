package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.ListingSort;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.OfferStatusGroup;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.OfferService;
import ar.edu.itba.paw.service.dto.ListingFilterDto;
import ar.edu.itba.paw.service.dto.OfferFilterDto;
import ar.edu.itba.paw.webapp.form.ListingFilterForm;
import ar.edu.itba.paw.webapp.form.OfferFilterForm;
import ar.edu.itba.paw.webapp.form.StringSelectOption;
import ar.edu.itba.paw.webapp.auth.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@RequiredArgsConstructor
@Controller
@RequestMapping("/account")
public class AccountController {

    private static final int ACCOUNT_LISTINGS_PAGE_SIZE = 5;

    private final ListingService listingService;
    private final OfferService offerService;
    private final MessageSource messageSource;

    @GetMapping
    public ModelAndView index(@CurrentUser User currentUser) {
        return new ModelAndView("account/index")
                .addObject("user", currentUser)
                .addObject("pendingOffersCount", getPendingOffersCount(currentUser));
    }

    @GetMapping("/listings")
    public ModelAndView listings(@CurrentUser User currentUser, @ModelAttribute("filterForm") ListingFilterForm filterForm) {

        final var filter = new ListingFilterDto(
            null, null, null, null, null, null,
            filterForm.getQuery(),
            filterForm.getSort(),
            currentUser.getId(),
            filterForm.getStatus(),
            filterForm.getPage(),
            ACCOUNT_LISTINGS_PAGE_SIZE
        );

        final var listingPage = listingService.search(filter);
        final var listings = listingPage.getContent();

        final var locale = LocaleContextHolder.getLocale();
        final var statusOptions = Arrays.stream(ListingStatus.values())
            .map(s -> new StringSelectOption(s.name(), messageSource.getMessage("listing.status." + s.name(), null, locale)))
            .toList();

        final var sortOptions = Arrays.stream(ListingSort.values())
            .map(s -> {
                String key;
                if (s == ListingSort.RECENT) key = "discovery.sort.recent";
                else if (s == ListingSort.PRICE_ASC) key = "discovery.sort.price_asc";
                else if (s == ListingSort.PRICE_DESC) key = "discovery.sort.price_desc";
                else if (s == ListingSort.NAME_ASC) key = "account.listings.sort.name_asc";
                else if (s == ListingSort.NAME_DESC) key = "account.listings.sort.name_desc";
                else key = s.getKey();
                return new StringSelectOption(s.getKey(), messageSource.getMessage(key, null, locale));
            })
            .toList();

        return new ModelAndView("account/listings")
                .addObject("listingPage", listingPage)
                .addObject("listings", listings)
                .addObject("user", currentUser)
                .addObject("statusOptions", statusOptions)
                .addObject("sortOptions", sortOptions)
                .addObject("pendingOffersCount", getPendingOffersCount(currentUser));
    }

    @GetMapping("/incoming-offers")
    public ModelAndView incomingOffers(@CurrentUser User currentUser, @ModelAttribute("filterForm") OfferFilterForm filterForm) {
        if (filterForm.getStatusGroup() == null || filterForm.getStatusGroup().isBlank()) {
            filterForm.setStatusGroup(OfferStatusGroup.PENDING.getStatus());
        }

        final var filter = new OfferFilterDto(currentUser.getId(), null, filterForm.getStatusGroup(), filterForm.getPage(), 5);
        final var offerPage = offerService.get(filter);

        final var locale = LocaleContextHolder.getLocale();
        final var statusGroupOptions = Arrays.stream(OfferStatusGroup.values())
            .map(s -> new StringSelectOption(s.getStatus(), messageSource.getMessage("offer.statusGroup." + s.getStatus(), null, locale)))
            .toList();

        return new ModelAndView("account/incomingOffers")
                .addObject("offerPage", offerPage)
                .addObject("offers", offerPage.getContent())
                .addObject("statusGroupOptions", statusGroupOptions)
                .addObject("user", currentUser)
                .addObject("pendingOffersCount", getPendingOffersCount(currentUser));
    }

    @GetMapping("/my-offers")
    public ModelAndView myOffers(@CurrentUser User currentUser, @ModelAttribute("filterForm") OfferFilterForm filterForm) {
        if (filterForm.getStatusGroup() == null || filterForm.getStatusGroup().isBlank()) {
            filterForm.setStatusGroup(OfferStatusGroup.PENDING.getStatus());
        }

        final var filter = new OfferFilterDto(null, currentUser.getId(), filterForm.getStatusGroup(), filterForm.getPage(), 5);
        final var offerPage = offerService.get(filter);

        final var locale = LocaleContextHolder.getLocale();
        final var statusGroupOptions = Arrays.stream(OfferStatusGroup.values())
            .map(s -> new StringSelectOption(s.getStatus(), messageSource.getMessage("offer.statusGroup." + s.getStatus(), null, locale)))
            .toList();

        return new ModelAndView("account/myOffers")
                .addObject("offerPage", offerPage)
                .addObject("offers", offerPage.getContent())
                .addObject("statusGroupOptions", statusGroupOptions)
                .addObject("user", currentUser)
                .addObject("pendingOffersCount", getPendingOffersCount(currentUser));
    }

	private int getPendingOffersCount(final User user) {
		return offerService.getPendingOffersCount(user);
	}
}
