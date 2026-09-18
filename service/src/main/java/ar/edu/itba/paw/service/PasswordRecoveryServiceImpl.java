package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private final UserService userService;
    private final OneTimePasswordService otpService;
    private final MailingService mailingService;

    @Override
    public Optional<OneTimePassword> start(String usernameOrEmail) {
        Objects.requireNonNull(usernameOrEmail, "usernameOrEmail cannot be null");

        return userService.getByUsernameOrEmail(usernameOrEmail).map(user -> {
            var otp = otpService.create(user);
            mailingService.sendPasswordRecoveryEmail(user, otp.getOtpValue(), LocaleContextHolder.getLocale());
            return otp;
        });
    }

    @Override
    public OneTimePasswordVerificationResult verify(String usernameOrEmail, String otpValue) {
        Objects.requireNonNull(usernameOrEmail, "usernameOrEmail cannot be null");
        Objects.requireNonNull(otpValue, "otpValue cannot be null");

        return userService.getByUsernameOrEmail(usernameOrEmail)
                .map(user -> otpService.verify(user, otpValue))
                .orElse(OneTimePasswordVerificationResult.REJECTED);
    }
}
