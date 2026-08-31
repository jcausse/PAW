package ar.edu.itba.paw.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LanguageController {
    @GetMapping("/language")
    public ModelAndView changeLanguage(HttpServletRequest request) {
        final var referer = request.getHeader("Referer");
        final var target = (referer != null && !referer.isEmpty()) ? referer : "/";
        return new ModelAndView("redirect:" + target);
    }
}
