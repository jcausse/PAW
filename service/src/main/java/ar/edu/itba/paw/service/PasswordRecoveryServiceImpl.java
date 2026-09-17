package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private final UserService userService;
    private final OneTimePasswordService otpService;
    private final MailingService mailingService;

    @Override
    @Transactional
    public Optional<OneTimePassword> startByUsername(String username) {
        Objects.requireNonNull(username, "username cannot be null");

        return userService.getByUsername(username).map(user -> {
            var otp = otpService.create(user);
            mailingService.sendPasswordRecoveryEmail(user, otp.getOtpValue(), LocaleContextHolder.getLocale());
            return otp;
        });
    }

    @Override
    @Transactional
    public Optional<OneTimePassword> startByEmail(String email) {
        Objects.requireNonNull(email, "email cannot be null");

        return userService.getByEmail(email).map(user -> {
            var otp = otpService.create(user);
            mailingService.sendPasswordRecoveryEmail(user, otp.getOtpValue(), LocaleContextHolder.getLocale());
            return otp;
        });
    }

    @Override
    @Transactional
    public OneTimePasswordVerificationResult verifyByUsername(String username, String otpValue) {
        Objects.requireNonNull(username, "username cannot be null");
        Objects.requireNonNull(otpValue, "otpValue cannot be null");

        return userService.getByUsername(username)
                .map(user -> otpService.verify(user, otpValue))
                .orElse(OneTimePasswordVerificationResult.REJECTED);
    }

    @Override
    @Transactional
    public OneTimePasswordVerificationResult verifyByEmail(String email, String otpValue) {
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(otpValue, "otpValue cannot be null");

        return userService.getByEmail(email)
                .map(user -> otpService.verify(user, otpValue))
                .orElse(OneTimePasswordVerificationResult.REJECTED);
    }

    @Override
    @Transactional
    public Optional<OneTimePassword> startRecovery(String identifier) {
        Objects.requireNonNull(identifier, "identifier cannot be null");
        String trimmed = identifier.trim().toLowerCase();

        if (trimmed.contains("@")) {
            return startByEmail(trimmed);
        } else {
            return startByUsername(trimmed);
        }
    }

    @Override
    @Transactional
    public OneTimePasswordVerificationResult verifyRecovery(String identifier, String otpValue) {
        Objects.requireNonNull(identifier, "identifier cannot be null");
        Objects.requireNonNull(otpValue, "otpValue cannot be null");
        String trimmed = identifier.trim().toLowerCase();

        if (trimmed.contains("@")) {
            return verifyByEmail(trimmed, otpValue);
        } else {
            return verifyByUsername(trimmed, otpValue);
        }
    }
}
