package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.ListingSort;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.OfferService;
import ar.edu.itba.paw.service.dto.ListingFilterDto;
import ar.edu.itba.paw.webapp.auth.AuthUserDetails;
import ar.edu.itba.paw.webapp.form.ListingFilterForm;
import ar.edu.itba.paw.webapp.form.StringSelectOption;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/account")
public class AccountController {

    private static final int ACCOUNT_LISTINGS_PAGE_SIZE = 5;

    private final ListingService listingService;
    private final OfferService offerService;
    private final MessageSource messageSource;

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
        final var user = currentUser.orElseThrow();

        return new ModelAndView("account/index")
                .addObject("user", user)
                .addObject("currentUser", currentUser)
                .addObject("pendingOffersCount", getPendingOffersCount(user));
    }

    @GetMapping("/listings")
    public ModelAndView listings(@ModelAttribute("filterForm") ListingFilterForm filterForm) {
        final var currentUser = getCurrentUser();
        final var user = currentUser.orElseThrow();

        final var filter = new ListingFilterDto(
            null, null, null, null, null, null,
            filterForm.getQuery(),
            filterForm.getSort(),
            user.getId(),
            filterForm.getStatus(),
            filterForm.getPage(),
            ACCOUNT_LISTINGS_PAGE_SIZE
        );

        final var listingPage = listingService.search(filter);
        final var listings = listingPage.getContent();

        final var locale = LocaleContextHolder.getLocale();
        final var statusOptions = Arrays.stream(ListingStatus.values())
            .map(s -> new StringSelectOption(s.name(), messageSource.getMessage("listing.status." + s.name(), null, locale)))
            .collect(Collectors.toList());

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
            .collect(Collectors.toList());

        return new ModelAndView("account/listings")
                .addObject("listingPage", listingPage)
                .addObject("listings", listings)
                .addObject("user", user)
                .addObject("currentUser", currentUser)
                .addObject("statusOptions", statusOptions)
                .addObject("sortOptions", sortOptions)
                .addObject("pendingOffersCount", getPendingOffersCount(user));
    }

    @GetMapping("/incoming-offers")
    public ModelAndView incomingOffers() {
        final var currentUser = getCurrentUser();
        final var user = currentUser.orElseThrow();
        final var offers = offerService.getIncomingOffersForUser(user.getId());

        return new ModelAndView("account/incomingOffers")
                .addObject("pendingOffers", offers.pending())
                .addObject("resolvedOffers", offers.resolved())
                .addObject("user", user)
                .addObject("currentUser", currentUser)
                .addObject("pendingOffersCount", offers.pending().size());
    }

    @GetMapping("/my-offers")
    public ModelAndView myOffers() {
        final var currentUser = getCurrentUser();
        final var user = currentUser.orElseThrow();
        final var offers = offerService.getMyOffersForUser(user.getId());

        return new ModelAndView("account/myOffers")
                .addObject("pendingOffers", offers.pending())
                .addObject("resolvedOffers", offers.resolved())
                .addObject("user", user)
                .addObject("currentUser", currentUser)
                .addObject("pendingOffersCount", getPendingOffersCount(user));
    }

	private int getPendingOffersCount(final User user) {
		return offerService.getIncomingOffersForUser(user.getId()).pending().size();
	}
}
