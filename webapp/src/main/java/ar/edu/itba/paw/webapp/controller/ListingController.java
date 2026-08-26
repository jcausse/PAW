package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.webapp.form.ListingForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class ListingController {

    private final ListingService listingService;
    private final UserService userService;

    @GetMapping("/listing/{id}")
    public ModelAndView listing(@PathVariable Long id) {
        final var mav = new ModelAndView("listing/index");
        mav.addObject("listing", listingService.getById(id));
        return mav;
    }

    @GetMapping("/listing/new")
    public ModelAndView listingNew() {
        final var mav = new ModelAndView("listing/new");
        return mav;
    }

    @PostMapping(
        value = "/listing",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ModelAndView createListing(@RequestBody ListingForm listingForm) {
        final var creator = userService.getById(listingForm.getCreatorId());
        final var product = new Product(
            listingForm.getProductId(),
            "null product"
        ); // TODO product service

        final var dto = new ListingCreationDto(
            listingForm.getTitle(),
            new Price(listingForm.getPrice()),
            creator,
            product
        );
        listingService.create(dto);

        return null;
    }
}
