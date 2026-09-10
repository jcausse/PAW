package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.service.dto.ImageData;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.webapp.auth.CurrentUser;
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

import java.util.Objects;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /* PROFILE */

    @GetMapping("/profile/{id}")
    public ModelAndView profile(@PathVariable Long id, @CurrentUser(required = false) User currentUser) {
        final User user = userService.getById(id).orElseThrow(() -> UserNotFoundException.byId(id));
        final boolean isSelfRequest = currentUser != null && Objects.equals(id, currentUser.getId());
        return new ModelAndView("profile")
                .addObject("user", user)
                .addObject("allowEdit", isSelfRequest);
    }

    @GetMapping("/profile")
    public ModelAndView currentUserProfile(@CurrentUser User currentUser) {
        return new ModelAndView("profile")
                .addObject("user", currentUser)
                .addObject("allowEdit", true);
    }

    /* REGISTER */

    @GetMapping("/register")
    public ModelAndView registerForm(@ModelAttribute("userForm") UserForm form) {
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("userForm") UserForm form, BindingResult errors) {
        if (errors.hasErrors()) {
            return registerForm(form);
        }

        // Extract image from form
        ImageData imageData = null;
        if (form.getProfilePicture() != null && !form.getProfilePicture().isEmpty()) {
            try {
                imageData = new ImageData(
                    form.getProfilePicture().getBytes(),
                    form.getProfilePicture().getOriginalFilename(),
                    form.getProfilePicture().getContentType()
                );
            } catch (java.io.IOException e) {
                errors.rejectValue("profilePicture", "error.image.upload");
                return registerForm(form);
            }
        }

        userService.create(new UserCreationDto(
            form.getUsername(),
            form.getDisplayName(),
            form.getEmail(),
            form.getPassword(),
            imageData
        ));

        loginAfterRegister(form.getUsername(), form.getPassword());

        return new ModelAndView("redirect:/");
    }

    /* LOGIN */

    @GetMapping("/login")
    public ModelAndView loginForm() {
        return new ModelAndView("login");
    }

    private void loginAfterRegister(final String username, final String password) {
        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        ));
    }
}
