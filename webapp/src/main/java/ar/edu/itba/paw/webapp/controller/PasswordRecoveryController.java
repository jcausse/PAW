package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.PasswordRecoveryService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.service.dto.UserEditDto;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;
import ar.edu.itba.paw.webapp.exception.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.PasswordRecoveryRequestForm;
import ar.edu.itba.paw.webapp.form.PasswordRecoveryVerificationForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;
    private final UserService userService;

    @GetMapping("/recovery/request")
    public ModelAndView passwordRecoveryRequestGET(
            @ModelAttribute("passwordRecoveryRequestForm") PasswordRecoveryRequestForm form
    ) {
        return new ModelAndView("recovery/request");
    }

    @PostMapping("/recovery/request")
    public ModelAndView passwordRecoveryRequestPOST(
            @Valid @ModelAttribute("passwordRecoveryRequestForm") PasswordRecoveryRequestForm form,
            BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return new ModelAndView("recovery/request");
        }

        /*
         * Password recovery requests can be started either with an email or with a username. This is left to
         * user's convenience.
         * In order to be able to call the service to the right method, we must first determine whether the user
         * entered an email or a username, and then call the adequate service method.
         */
        String identifierParam;
        String identifierType;

        if (form.getEmail() != null && !form.getEmail().isBlank()) {
            String email = form.getEmail().trim().toLowerCase();
            passwordRecoveryService.startByEmail(email);
            identifierParam = email;
            identifierType = "email";
        }
        else {
            String username = form.getUsername().trim().toLowerCase();
            passwordRecoveryService.startByUsername(username);
            identifierParam = username;
            identifierType = "username";
        }

        var mav = new ModelAndView("recovery/request");
        mav.addObject("codeSent", true);
        mav.addObject("identifierParam", identifierParam);
        mav.addObject("identifierType", identifierType);
        return mav;
    }

    @GetMapping("/recovery/verification")
    public ModelAndView passwordRecoveryVerificationGET(
            @ModelAttribute("passwordRecoveryVerificationForm") PasswordRecoveryVerificationForm form
    ) {
        if ((form.getEmail() == null || form.getEmail().isBlank()) && (form.getUsername() == null || form.getUsername().isBlank())) {
            return new ModelAndView("redirect:/recovery/request");
        }
        return new ModelAndView("recovery/verification");
    }

    @PostMapping("/recovery/verification")
    public ModelAndView passwordRecoveryVerificationPOST(
            @Valid @ModelAttribute("passwordRecoveryVerificationForm") PasswordRecoveryVerificationForm form,
            BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return new ModelAndView("recovery/verification");
        }

        OneTimePasswordVerificationResult result;
        Optional<User> maybeUser;

        /*
         * Verify OTP according to the user's input (either email or username)
         */
        if (form.getEmail() != null && !form.getEmail().isBlank()) {
            result = passwordRecoveryService.verifyByEmail(form.getEmail(), form.getOtp().trim());
            maybeUser = userService.getByEmail(form.getEmail());
        }
        else if (form.getUsername() != null && !form.getUsername().isBlank()) {
            result = passwordRecoveryService.verifyByUsername(form.getUsername(), form.getOtp().trim());
            maybeUser = userService.getByUsername(form.getUsername());
        }
        else {
            return new ModelAndView("redirect:/recovery/request");
        }

        /*
         * Inform errors to the user if the OTP is invalid or has expired
         */
        if (result == OneTimePasswordVerificationResult.REJECTED) {
            errors.rejectValue("otp", "recovery.verification.error.invalidOtp");
            return new ModelAndView("recovery/verification");
        } else if (result == OneTimePasswordVerificationResult.EXPIRED) {
            errors.rejectValue("otp", "recovery.verification.error.expiredOtp");
            return new ModelAndView("recovery/verification");
        }

        userService.update(new UserEditDto(
                maybeUser.orElseThrow(() -> new UserNotFoundException("User not found")), // Should never throw
                null,
                form.getPassword(),
                null
        ));

        return new ModelAndView("redirect:/login");
    }
}
