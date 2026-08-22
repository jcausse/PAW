package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.serviceInterface.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{userId}")
    public ModelAndView helloWorld(@PathVariable Long userId) {
        final ModelAndView mav = new ModelAndView("profile");
        mav.addObject("username", userService.getById(userId).username());
        return mav;
    }
}
