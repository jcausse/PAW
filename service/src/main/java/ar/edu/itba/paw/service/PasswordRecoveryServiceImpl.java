package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.service.dto.UserEditDto;
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
    public Optional<OneTimePassword> startAndSendRecoveryEmail(String usernameOrEmail) {
        Objects.requireNonNull(usernameOrEmail, "usernameOrEmail cannot be null");

        return userService.getByUsernameOrEmail(usernameOrEmail).map(user -> {
            final var otp = otpService.create(user);
            mailingService.sendPasswordRecoveryEmail(user, otp.getOtpValue(), LocaleContextHolder.getLocale());
            return otp;
        });
    }

    @Override
    public OneTimePasswordVerificationResult verifyAndUpdatePassword(
            String usernameOrEmail,
            String password,
            String otpValue
    ) {
        Objects.requireNonNull(usernameOrEmail, "usernameOrEmail cannot be null");
        Objects.requireNonNull(password, "password cannot be null");
        Objects.requireNonNull(otpValue, "otpValue cannot be null");

        final var maybeUser = userService.getByUsernameOrEmail(usernameOrEmail);
        final var result = maybeUser.map(user -> otpService.verify(user, otpValue))
                .orElse(OneTimePasswordVerificationResult.REJECTED);

        if (result == OneTimePasswordVerificationResult.ACCEPTED) {
            userService.update(new UserEditDto(
                    maybeUser.get(),
                    null,
                    password,
                    null
            ));
        }

        return result;
    }
}
