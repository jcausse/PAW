package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserService userService;
    private final OneTimePasswordService otpService;
    private final MailingService mailingService;

    @Override
    @Transactional
    public OneTimePassword sendVerificationEmail(@NonNull User user) {
        final var otp = otpService.create(user);
        mailingService.sendVerificationEmail(user, otp.getOtpValue(), LocaleContextHolder.getLocale());
        return otp;
    }

    @Override
    @Transactional
    public Optional<OneTimePassword> resendVerificationEmail(@NonNull String usernameOrEmail) {
        final var maybeUser = userService.getByUsernameOrEmail(usernameOrEmail);
        if (maybeUser.isEmpty()) {
            return Optional.empty();
        }

        final var user = maybeUser.get();
        if (user.isVerified()) {
            return Optional.empty();
        }

        final var otp = otpService.create(user);
        mailingService.sendVerificationEmail(user, otp.getOtpValue(), LocaleContextHolder.getLocale());
        return Optional.of(otp);
    }

    @Override
    @Transactional
    public OneTimePasswordVerificationResult verifyEmail(@NonNull String usernameOrEmail, @NonNull String otpValue) {
        final var maybeUser = userService.getByUsernameOrEmail(usernameOrEmail);
        if (maybeUser.isEmpty()) {
            return OneTimePasswordVerificationResult.REJECTED;
        }

        final var user = maybeUser.get();
        if (user.isVerified()) {
            return OneTimePasswordVerificationResult.ACCEPTED;
        }

        final var result = otpService.verify(user, otpValue);
        if (result == OneTimePasswordVerificationResult.ACCEPTED) {
            userService.markEmailAsVerified(user);
            mailingService.sendWelcomeEmail(user, LocaleContextHolder.getLocale());
        }

        return result;
    }
}
