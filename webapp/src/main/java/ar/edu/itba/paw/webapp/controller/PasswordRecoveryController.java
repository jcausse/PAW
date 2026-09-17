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

        String identifier = form.getEmail() != null && !form.getEmail().isBlank()
                ? form.getEmail()
                : form.getUsername();

        passwordRecoveryService.startRecovery(identifier);

        var mav = new ModelAndView("recovery/request");
        mav.addObject("codeSent", true);
        mav.addObject("identifierParam", identifier);
        mav.addObject("identifierType", identifier.contains("@") ? "email" : "username");
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

        String identifier = form.getEmail() != null && !form.getEmail().isBlank()
                ? form.getEmail()
                : form.getUsername();

        if (identifier == null || identifier.isBlank()) {
            return new ModelAndView("redirect:/recovery/request");
        }

        OneTimePasswordVerificationResult result = passwordRecoveryService.verifyRecovery(identifier, form.getOtp().trim());

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

        var maybeUser = identifier.contains("@")
                ? userService.getByEmail(identifier)
                : userService.getByUsername(identifier);

        userService.update(new UserEditDto(
                maybeUser.orElseThrow(() -> new UserNotFoundException("User not found")), // Should never throw
                null,
                null,
                form.getPassword(),
                null
        ));

        return new ModelAndView("redirect:/login");
    }
}
