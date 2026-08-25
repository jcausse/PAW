package ar.edu.itba.paw.webapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class ListingController {

    // TODO add the service

    @GetMapping("/listing/{id}")
    public ModelAndView profile(@PathVariable Long id) {
        final var mav = new ModelAndView("listing");
        // mav.addObject("user", userService.getById(userId));
        return mav;
    }
}
