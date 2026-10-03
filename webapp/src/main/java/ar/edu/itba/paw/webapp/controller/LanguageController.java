package ar.edu.itba.paw.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LanguageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageController.class);
    @GetMapping("/language")
    public ModelAndView changeLanguage(HttpServletRequest request) {
        final var referer = request.getHeader("Referer");
        final var target = (referer != null && !referer.isEmpty()) ? referer : "/";
        LOGGER.debug("Changing language, redirecting to {}", target);
        return new ModelAndView("redirect:" + target);
    }
}
