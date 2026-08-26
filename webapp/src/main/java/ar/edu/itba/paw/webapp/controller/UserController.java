package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{id}")
    public ModelAndView profile(@PathVariable Long id) {
        final var mav = new ModelAndView("profile");
        mav.addObject("user", userService.getById(id));
        return mav;
    }

    @PostMapping("/register")
    public ModelAndView registerUser() {
        final var dto = new UserCreationDto(
            "johnDoe",
            "John",
            "Doe",
            "johndoe@example.com",
            "superSecretPassword"
        );
        userService.create(dto);

        return null;
    }
}
