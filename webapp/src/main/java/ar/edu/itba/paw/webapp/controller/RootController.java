package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.ListingSort;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.service.dto.ListingFilterDto;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class RootController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);

    private static final int LANDING_LIST_SIZE = 12;

    private final ListingService listingService;
    private final ProductService productService;

    public RootController(ListingService listingService, ProductService productService) {
        this.listingService = listingService;
        this.productService = productService;
    }

    @GetMapping("/")
    public ModelAndView landing() {
        LOGGER.debug("Accessing landing page");
        // Hot items: active listings with most active offers
        var hotItemsFilter = new ListingFilterDto(
            null, null, null, null, null, null, null,
            ListingSort.MOST_OFFERS.getKey(),
            null, ListingStatus.ACTIVE.getStatus(),
            1, LANDING_LIST_SIZE, true, null, null
        );
        Page<Listing> hotItemsPage = listingService.search(hotItemsFilter);

        // Trending items: active listings with most recent active offers
        var trendingItemsFilter = new ListingFilterDto(
            null, null, null, null, null, null, null,
            ListingSort.RECENT_OFFERS.getKey(),
            null, ListingStatus.ACTIVE.getStatus(),
            1, LANDING_LIST_SIZE, true, null, null
        );
        Page<Listing> trendingItemsPage = listingService.search(trendingItemsFilter);

        // New items: most recently created active listings
        var newItemsFilter = new ListingFilterDto(
            null, null, null, null, null, null, null,
            ListingSort.RECENT.getKey(),
            null, ListingStatus.ACTIVE.getStatus(),
            1, LANDING_LIST_SIZE, null, null, null
        );
        Page<Listing> newItemsPage = listingService.search(newItemsFilter);

        // Categories for the grid
        List<Category> categories = productService.getAllCategories();

        var mav = new ModelAndView("landing");
        mav.addObject("hotItems", hotItemsPage.getContent());
        mav.addObject("trendingItems", trendingItemsPage.getContent());
        mav.addObject("newItems", newItemsPage.getContent());
        mav.addObject("categories", categories);
        return mav;
    }
}
