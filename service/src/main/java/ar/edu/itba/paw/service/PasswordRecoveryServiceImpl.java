package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.service.dto.UserEditDto;
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
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private final UserService userService;
    private final OneTimePasswordService otpService;
    private final MailingService mailingService;

    @Override
    @Transactional
    public Optional<OneTimePassword> startAndSendRecoveryEmail(@NonNull String usernameOrEmail) {
        return userService.getByUsernameOrEmail(usernameOrEmail).map(user -> {
            final var otp = otpService.create(user);
            mailingService.sendPasswordRecoveryEmail(user, otp.getOtpValue(), LocaleContextHolder.getLocale());
            return otp;
        });
    }

    @Override
    @Transactional
    public OneTimePasswordVerificationResult verifyAndUpdatePassword(
            @NonNull String usernameOrEmail,
            @NonNull String password,
            @NonNull String otpValue
    ) {
        final var maybeUser = userService.getByUsernameOrEmail(usernameOrEmail);
        final var result = maybeUser.map(user -> otpService.verify(user, otpValue))
                .orElse(OneTimePasswordVerificationResult.REJECTED);

        if (result == OneTimePasswordVerificationResult.ACCEPTED) {
            userService.update(new UserEditDto(maybeUser.get(), null, password, null));
        }

        return result;
    }
}
