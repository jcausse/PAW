package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.UserForm;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/profile/{id}")
    public ModelAndView profile(@PathVariable Long id) {
        return new ModelAndView("profile")
                .addObject("user", userService.getById(id).orElseThrow(() -> UserNotFoundException.byId(id)));
    }

    /* REGISTER */

    @GetMapping("/register")
    public ModelAndView registerForm(@ModelAttribute("userForm") UserForm form) {
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public ModelAndView register(
        @Valid @ModelAttribute("userForm") UserForm form,
        BindingResult errors
    ) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "error.password.mismatch");
        }

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
            form.getPassword()
        );
        userService.create(dto);

        /* Auto-Login */
        SecurityContextHolder.getContext().setAuthentication(
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    form.getUsername(),
                    form.getPassword()
            )
        ));

        return new ModelAndView("redirect:/");
    }

    /* LOGIN */

    @GetMapping("/login")
    public ModelAndView loginForm() {
        return new ModelAndView("login");
    }
}
