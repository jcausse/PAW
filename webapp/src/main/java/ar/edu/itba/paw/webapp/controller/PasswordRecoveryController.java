package ar.edu.itba.paw.webapp.controller;

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

        passwordRecoveryService.start(form.getUsernameOrEmail());

        return new ModelAndView("recovery/request")
                .addObject("codeSent", true)
                .addObject("usernameOrEmail", form.getUsernameOrEmail().trim().toLowerCase());
    }

    @GetMapping("/recovery/verification")
    public ModelAndView passwordRecoveryVerificationGET(
            @ModelAttribute("passwordRecoveryVerificationForm") PasswordRecoveryVerificationForm form
    ) {
        if (form.getUsernameOrEmail() == null || form.getUsernameOrEmail().isBlank()) {
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

        var usernameOrEmail = form.getUsernameOrEmail().trim().toLowerCase();
        var result = passwordRecoveryService.verify(form.getUsernameOrEmail(), form.getOtp().trim());

        if (result == OneTimePasswordVerificationResult.REJECTED) {
            errors.rejectValue("otp", "recovery.verification.error.invalidOtp");
            return new ModelAndView("recovery/verification");
        } else if (result == OneTimePasswordVerificationResult.EXPIRED) {
            errors.rejectValue("otp", "recovery.verification.error.expiredOtp");
            return new ModelAndView("recovery/verification");
        }

        var user = userService.getByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userService.update(new UserEditDto(
                user,
                null,
                form.getPassword(),
                null
        ));

        return new ModelAndView("redirect:/login");
    }
}
