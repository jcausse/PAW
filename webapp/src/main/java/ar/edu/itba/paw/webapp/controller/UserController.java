package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.webapp.form.LoginForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class UserController {

    private static final String USER_ID_ATTR = "userId";
    private static final String USERNAME_ATTR = "username";
    private static final String REDIRECT_AFTER_LOGIN_ATTR =
        "redirectAfterLogin";

    private final UserService userService;

    @GetMapping("/profile/{userId}")
    public ModelAndView profile(@PathVariable Long userId) {
        final var mav = new ModelAndView("profile");
        mav.addObject("user", userService.getById(userId));
        return mav;
    }

    /* REGISTER */

    @GetMapping("/register")
    public ModelAndView registerForm(
        @ModelAttribute("userForm") UserForm form
    ) {
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public ModelAndView register(
        @Valid @ModelAttribute("userForm") UserForm form,
        BindingResult errors,
        HttpSession session
    ) {
        if (userService.isUsernameTaken(form.getUsername())) {
            errors.rejectValue("username", "error.username.taken");
        }
        if (userService.isEmailTaken(form.getEmail())) {
            errors.rejectValue("email", "error.email.taken");
        }

        if (errors.hasErrors()) {
            return registerForm(form);
        }

        var dto = new UserCreationDto(
            form.getUsername(),
            form.getDisplayName(),
            form.getEmail(),
            "no-password" // MVP: no password auth
        );
        var user = userService.create(dto);

        logIn(session, user.getId(), user.getUsername());

        return new ModelAndView("redirect:/");
    }

    /* LOGIN */

    @GetMapping("/login")
    public ModelAndView loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return new ModelAndView("login");
    }

    @PostMapping("/login")
    public ModelAndView login(
        @Valid @ModelAttribute("loginForm") LoginForm form,
        BindingResult errors,
        HttpSession session
    ) {
        if (
            !errors.hasErrors() &&
            !userService.isUsernameTaken(form.getUsername())
        ) {
            errors.rejectValue("username", "error.username.notfound");
        }

        if (errors.hasErrors()) {
            return loginForm(form);
        }

        var user = userService.getByUsername(form.getUsername());
        logIn(session, user.getId(), user.getUsername());

        var redirect = (String) session.getAttribute(REDIRECT_AFTER_LOGIN_ATTR);
        if (redirect != null) {
            session.removeAttribute(REDIRECT_AFTER_LOGIN_ATTR);
            return new ModelAndView("redirect:" + redirect);
        }
        return new ModelAndView("redirect:/");
    }

    private void logIn(HttpSession session, Long userId, String username) {
        session.setAttribute(USER_ID_ATTR, userId);
        session.setAttribute(USERNAME_ATTR, username);
    }

    /* LOGOUT */

    @PostMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        session.invalidate();
        return new ModelAndView("redirect:/");
    }
}
