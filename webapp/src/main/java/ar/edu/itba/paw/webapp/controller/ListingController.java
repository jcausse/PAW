package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.webapp.form.ListingForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
@RequestMapping("/listing")
public class ListingController {

    private final ListingService listingService;

    @GetMapping("/{id}")
    public ModelAndView listing(@PathVariable Long id) {
        return new ModelAndView("listing/index")
                .addObject("listing", listingService.getById(id));
    }

    @GetMapping("/new")
    public ModelAndView listingNew(@ModelAttribute("listingForm") ListingForm form) {
        return new ModelAndView("listing/new");
    }

    @PostMapping("/new")
    public ModelAndView createListing(@ModelAttribute("listingForm") ListingForm form) {
        final var newListing = listingService.create(new ListingCreationDto(
                form.getTitle(),
                new Price(form.getPrice()),
                form.getCreatorId(),
                form.getProductId()
        ));

        return new ModelAndView("redirect:/listing/" + newListing.getId());
    }
}
