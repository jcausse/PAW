package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.service.EmailVerificationService;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;
import ar.edu.itba.paw.webapp.form.EmailVerificationForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Controller
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @GetMapping("/verify")
    public ModelAndView verifyForm(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "resent", required = false) Boolean resent,
            @ModelAttribute("emailVerificationForm") EmailVerificationForm form
    ) {
        if (email != null && !email.isBlank() && (form.getUsernameOrEmail() == null || form.getUsernameOrEmail().isBlank())) {
            form.setUsernameOrEmail(email.trim());
        }
        return new ModelAndView("verify")
                .addObject("resent", Boolean.TRUE.equals(resent));
    }

    @PostMapping("/verify")
    public ModelAndView verify(
            @Valid @ModelAttribute("emailVerificationForm") EmailVerificationForm form,
            BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return new ModelAndView("verify");
        }

        final var result = emailVerificationService.verifyEmail(form.getUsernameOrEmail(), form.getOtp());
        if (result == OneTimePasswordVerificationResult.ACCEPTED) {
            return new ModelAndView("redirect:/login?verified=true");
        }

        errors.rejectValue("otp", result == OneTimePasswordVerificationResult.EXPIRED
                ? "verify.error.expiredOtp"
                : "verify.error.invalidOtp"
        );
        return new ModelAndView("verify");
    }

    @PostMapping("/verify/resend")
    public ModelAndView resendVerificationCode(@RequestParam("usernameOrEmail") String usernameOrEmail) {
        if (usernameOrEmail != null && !usernameOrEmail.isBlank()) {
            emailVerificationService.resendVerificationEmail(usernameOrEmail);
            return new ModelAndView("redirect:/verify?email=" +
                            URLEncoder.encode(usernameOrEmail.trim(), StandardCharsets.UTF_8) +
                            "&resent=true"
            );
        }
        return new ModelAndView("redirect:/verify");
    }
}
