package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.ListingSort;
import ar.edu.itba.paw.model.Province;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.EmailVerificationService;
import ar.edu.itba.paw.service.ListingService;
import ar.edu.itba.paw.service.ProvinceService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.service.dto.ImageData;
import ar.edu.itba.paw.service.dto.ListingFilterDto;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.service.dto.UserEditDto;
import ar.edu.itba.paw.webapp.auth.AuthHelper;
import ar.edu.itba.paw.webapp.auth.CurrentUser;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.SelectOption;
import ar.edu.itba.paw.webapp.form.UserEditForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ListingService listingService;
    private final EmailVerificationService emailVerificationService;
    private final ProvinceService provinceService;
    private final AuthHelper authHelper;
    private final MessageSource messageSource;

    private static final int PROFILE_LISTINGS_PAGE_SIZE = 5;

    /* PROFILE */

    @GetMapping("/profile/{id}")
    public ModelAndView profile(@PathVariable Long id, @CurrentUser(required = false) User currentUser) {
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
                null,
                null
        );

        final var listingPage = listingService.search(filter);
        final var listings = listingPage.getContent();

        return new ModelAndView("profile")
                .addObject("user", user)
                .addObject("allowEdit", isSelfRequest)
                .addObject("listings", listings)
                .addObject("listingPage", listingPage);
    }

    @GetMapping("/profile")
    public ModelAndView currentUserProfile(@CurrentUser User currentUser) {
        LOGGER.debug("Accessing current user profile for user {}", currentUser.getId());
        return new ModelAndView("profile")
                .addObject("user", currentUser)
                .addObject("allowEdit", true);
    }

    /* PROFILE EDIT */

    @GetMapping("/profile/edit")
    public ModelAndView editProfileForm(@CurrentUser User currentUser, @ModelAttribute("userEditForm") UserEditForm form) {
        LOGGER.debug("Accessing profile edit form for user {}", currentUser.getId());
        form.setDisplayName(currentUser.getDisplayName());
        form.setProvinceId(currentUser.getProvince().map(Province::getId).orElse(null));
        form.setLocationDetail(currentUser.getLocationDetail().orElse(null));
        return profileEditView(currentUser);
    }

    private ModelAndView profileEditView(final User currentUser) {
        return new ModelAndView("profileEdit")
                .addObject("user", currentUser)
                .addObject("provinces", buildProvinceOptions());
    }

    private List<SelectOption> buildProvinceOptions() {
        return provinceService.getAll().stream()
                .map(province -> new SelectOption(
                        province.getId(),
                        messageSource.getMessage("province." + province.getName(), null, LocaleContextHolder.getLocale())
                ))
                .toList();
    }

    @PostMapping("/profile/edit")
    public ModelAndView editProfile(
            @CurrentUser User currentUser,
            @Valid @ModelAttribute("userEditForm") UserEditForm form,
            BindingResult errors
    ) {
        if (errors.hasErrors()) {
            LOGGER.debug("Validation failed for user {} profile edit", currentUser.getId());
            return profileEditView(currentUser);
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
                LOGGER.error("Failed to read profile picture for user {}", currentUser.getId(), e);
                errors.rejectValue("profilePicture", "error.image.upload");
                return profileEditView(currentUser);
            }
        }

        LOGGER.info("User {} profile edit submitted", currentUser.getId());
        LOGGER.debug("User {} updating profile", currentUser.getUsername());
        userService.update(new UserEditDto(
            currentUser,
            form.getDisplayName(),
            form.getPassword(),
            imageData
        ));
        User updatedUser = userService.updateLocation(currentUser, form.getProvinceId(), form.getLocationDetail());

        authHelper.update(updatedUser);

        return new ModelAndView("redirect:/profile");
    }

    /* REGISTER */

    @GetMapping("/register")
    public ModelAndView registerForm(@ModelAttribute("userForm") UserForm form) {
        LOGGER.debug("Accessing register form");
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("userForm") UserForm form, BindingResult errors) {
        if (errors.hasErrors()) {
            LOGGER.debug("Validation failed for registration form");
            return registerForm(form);
        }

        LOGGER.info("User registration submitted: {}", form.getUsername());
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
                LOGGER.error("Failed to read profile picture for registration of username {}", form.getUsername(), e);
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
        LOGGER.debug("Accessing login form");
        return new ModelAndView("login");
    }
}
