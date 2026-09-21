package ar.edu.itba.paw.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {

    @RequestMapping("/403")
    public ModelAndView forbidden() {
        return new ModelAndView("error/forbidden");
    }

    @RequestMapping("/error/fileSizeExceeded")
    public ModelAndView fileSizeExceeded(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        String fallbackUrl = "/";
        String redirectUrl = (referer != null && !referer.isBlank()) ? referer : fallbackUrl;

        redirectAttributes.addFlashAttribute("fileSizeError", true);
        redirectAttributes.addFlashAttribute("fileSizeErrorMessage", "The uploaded file exceeds the maximum allowed size (5MB).");

        return new ModelAndView("redirect:" + redirectUrl);
    }
}