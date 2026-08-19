package ar.edu.itba.paw.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloWorldController {

    @GetMapping("/")
    public ModelAndView helloWorld() {
        final ModelAndView mav = new ModelAndView("tpe0");
        mav.addObject("username", "PAW");
        return mav;
    }
}
