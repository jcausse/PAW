package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.service.dto.ImageData;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.webapp.auth.AuthUserDetails;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.UserForm;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
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

    public static final String TEMPORARY_PASSWORD = "jT3F@d2KX93Dbtp#kU&$9e";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private final UserService userService;
    private final UserDetailsService userDetailsService;

    /* PROFILE */

    @GetMapping("/profile/{id}")
    public ModelAndView profile(@PathVariable Long id) {
        return new ModelAndView("profile")
                .addObject("user", userService.getById(id).orElseThrow(() -> UserNotFoundException.byId(id)));
    }

    /* IDENTIFY / REGISTER */

    @GetMapping("/register")
    public ModelAndView registerForm(@ModelAttribute("userForm") UserForm form) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthUserDetails) {
            return new ModelAndView("redirect:/");
        }
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public ModelAndView register(
            @Valid @ModelAttribute("userForm") UserForm form,
            BindingResult errors,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (errors.hasErrors()) {
            return registerForm(form);
        }

        final var existingUser = userService.getByUsername(form.getUsername());

        if (existingUser.isPresent()) {
            // Existing user: log in immediately without password
            authenticateUser(form.getUsername(), request);
            return redirectToSavedRequestOrDefault(request, response);
        }

        // New user: enforce required fields (displayName and email)
        form.setFirstTime(true);
        if (form.getDisplayName() == null || form.getDisplayName().trim().isEmpty()) {
            errors.rejectValue("displayName", "NotEmpty.userForm.displayName");
        } else if (form.getDisplayName().length() > 50) {
            errors.rejectValue("displayName", "Size.userForm.displayName", new Object[]{50}, null);
        }

        if (form.getEmail() == null || form.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "NotEmpty.userForm.email");
        } else if (!EMAIL_PATTERN.matcher(form.getEmail()).matches()) {
            errors.rejectValue("email", "Email.userForm.email");
        } else if (form.getEmail().length() > 254) {
            errors.rejectValue("email", "Size.userForm.email", new Object[]{254}, null);
        } else if (userService.isEmailTaken(form.getEmail())) {
            errors.rejectValue("email", "error.email.taken");
        }

        if (errors.hasErrors()) {
            return registerForm(form);
        }

        // Extract image from form if provided
        ImageData imageData = null;
        if (form.getProfilePicture() != null && !form.getProfilePicture().isEmpty()) {
            try {
                imageData = new ImageData(
                    form.getProfilePicture().getBytes(),
                    form.getProfilePicture().getOriginalFilename(),
                    form.getProfilePicture().getContentType()
                );
            } catch (IOException e) {
                errors.rejectValue("profilePicture", "error.image.upload");
                return registerForm(form);
            }
        }

        userService.create(new UserCreationDto(
            form.getUsername(),
            form.getDisplayName(),
            form.getEmail(),
            TEMPORARY_PASSWORD,
            imageData
        ));

        authenticateUser(form.getUsername(), request);

        return redirectToSavedRequestOrDefault(request, response);
    }

    /* LOGIN REDIRECT (so /login is not used directly) */

    @GetMapping("/login")
    public ModelAndView loginForm() {
        return new ModelAndView("redirect:/register");
    }

    private void authenticateUser(final String username, final HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
    }

    private ModelAndView redirectToSavedRequestOrDefault(HttpServletRequest request, HttpServletResponse response) {
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        if (savedRequest != null && savedRequest.getRedirectUrl() != null) {
            return new ModelAndView("redirect:" + savedRequest.getRedirectUrl());
        }
        return new ModelAndView("redirect:/");
    }
}
