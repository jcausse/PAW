package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.ListingSort;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.RatingRole;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.EmailVerificationService;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.RatingService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.service.dto.ImageData;
import ar.edu.itba.paw.service.dto.ListingFilterDto;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.service.dto.UserEditDto;
import ar.edu.itba.paw.webapp.auth.AuthHelper;
import ar.edu.itba.paw.webapp.auth.CurrentUser;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.RatingFilterForm;
import ar.edu.itba.paw.webapp.form.UserEditForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RequiredArgsConstructor
@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ListingService listingService;
    private final RatingService ratingService;
    private final EmailVerificationService emailVerificationService;
    private final AuthHelper authHelper;

    private static final int PROFILE_LISTINGS_PAGE_SIZE = 5;
    private static final int PROFILE_RATINGS_PAGE_SIZE = 5;

    /* PROFILE */

    @GetMapping("/profile/{id}")
    public ModelAndView profile(@PathVariable Long id, @CurrentUser(required = false) User currentUser,
                                @ModelAttribute("ratingFilterForm") RatingFilterForm ratingFilterForm) {
        LOGGER.debug("Accessing profile for user id: {}", id);
        final User user = userService.getById(id).orElseThrow(() -> UserNotFoundException.byId(id));
        final boolean isSelfRequest = currentUser != null && Objects.equals(id, currentUser.getId());

        final var filter = new ListingFilterDto(
                null, null, null, null, null, null,
                null,
                ListingSort.RECENT.getKey(),
                user.getId(),
                ListingStatus.ACTIVE.name(),
                1,
                PROFILE_LISTINGS_PAGE_SIZE,
                null
        );

        final var listingPage = listingService.search(filter);
        final var listings = listingPage.getContent();

        // Default role to SELLER if not provided
        String role = ratingFilterForm.getRole();
        if (role == null || role.isBlank()) {
            role = RatingRole.SELLER.getRole();
            ratingFilterForm.setRole(role);
        }

        // Default type to null (all) if not provided
        String type = ratingFilterForm.getType();
        if (type != null && type.isBlank()) {
            type = null;
        }

        int page = ratingFilterForm.getPage() != null ? ratingFilterForm.getPage() : 1;

        var ratingFilter = ar.edu.itba.paw.model.RatingFilter.builder()
                .ratedId(user.getId())
                .roles(role != null ? java.util.List.of(RatingRole.fromString(role).orElse(RatingRole.SELLER)) : null)
                .types(type != null ? java.util.List.of(ar.edu.itba.paw.model.OfferRating.fromString(type).orElse(null)) : null)
                .page(page)
                .pageSize(PROFILE_RATINGS_PAGE_SIZE)
                .build();

        final var ratingPage = ratingService.get(ratingFilter);
        final var ratings = ratingPage.getContent();

        return new ModelAndView("profile")
                .addObject("user", user)
                .addObject("allowEdit", isSelfRequest)
                .addObject("listings", listings)
                .addObject("listingPage", listingPage)
                .addObject("ratingPage", ratingPage)
                .addObject("ratings", ratings);
    }

    @GetMapping("/profile")
    public ModelAndView currentUserProfile(@CurrentUser User currentUser) {
        return new ModelAndView("profile")
                .addObject("user", currentUser)
                .addObject("allowEdit", true);
    }

    /* PROFILE EDIT */

    @GetMapping("/profile/edit")
    public ModelAndView editProfileForm(@CurrentUser User currentUser, @ModelAttribute("userEditForm") UserEditForm form) {
        form.setDisplayName(currentUser.getDisplayName());
        return new ModelAndView("profileEdit")
                .addObject("user", currentUser);
    }

    @PostMapping("/profile/edit")
    public ModelAndView editProfile(
            @CurrentUser User currentUser,
            @Valid @ModelAttribute("userEditForm") UserEditForm form,
            BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return new ModelAndView("profileEdit")
                    .addObject("user", currentUser);
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
                return new ModelAndView("profileEdit")
                        .addObject("user", currentUser);
            }
        }

        LOGGER.debug("User {} updating profile", currentUser.getUsername());
        User updatedUser = userService.update(new UserEditDto(
            currentUser,
            form.getDisplayName(),
            form.getPassword(),
            imageData
        ));

        authHelper.update(updatedUser);

        return new ModelAndView("redirect:/profile");
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

        LOGGER.debug("Registering new user with username: {}", form.getUsername());

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

        User user = userService.create(new UserCreationDto(
            form.getUsername(),
            form.getDisplayName(),
            form.getEmail(),
            form.getPassword(),
            imageData
        ));

        emailVerificationService.sendVerificationEmail(user);

        return new ModelAndView("redirect:/verify?email=" + URLEncoder.encode(form.getEmail().trim().toLowerCase(), StandardCharsets.UTF_8));
    }

    /* LOGIN */

    @GetMapping("/login")
    public ModelAndView loginForm() {
        return new ModelAndView("login");
    }
}
