package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.webapp.form.ListingForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class ListingController {

    private final ListingService listingService;

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

    @PostMapping("/listing")
    public ModelAndView createListing(@ModelAttribute ListingForm listingForm) {
        final var dto = new ListingCreationDto(
            listingForm.getTitle(),
            new Price(listingForm.getPrice()),
            listingForm.getCreatorId(),
            listingForm.getProductId()
        );
        final var newListing = listingService.create(dto);

        return new ModelAndView("redirect:/listing/" + newListing.getId());
    }
}
