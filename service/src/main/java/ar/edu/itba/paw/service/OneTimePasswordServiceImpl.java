package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.OneTimePasswordDao;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OneTimePasswordServiceImpl implements OneTimePasswordService {

    private static final int OTP_LENGTH = 10;
    private static final Duration OTP_EXPIRATION = Duration.ofMinutes(15);
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OneTimePasswordDao otpDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OneTimePasswordVerificationResult verify(User user, String otpValue) {

        // Parameter validation
        if (user == null || otpValue == null || otpValue.isBlank()) {
            return OneTimePasswordVerificationResult.REJECTED;
        }

        // User did not request a One Time Password
        Optional<OneTimePassword> maybeOtp = otpDao.getByUser(user);
        if (maybeOtp.isEmpty()) {
            return OneTimePasswordVerificationResult.REJECTED;
        }

        // User did request a One Time Password, but it expired
        OneTimePassword otp = maybeOtp.get();
        if (otp.getCreatedAt().plus(OTP_EXPIRATION).isBefore(Instant.now())) {
            otpDao.deleteIfPresentByUser(user);
            return OneTimePasswordVerificationResult.EXPIRED;
        }

        // OTP did not expire yet, and user entered it correctly
        if (passwordEncoder.matches(otpValue, otp.getOtpValue())) {
            otpDao.deleteIfPresentByUser(user);
            return OneTimePasswordVerificationResult.ACCEPTED;
        }

        // OTP did not expire yet, and user failed to verify (wrong OTP entered)
        return OneTimePasswordVerificationResult.REJECTED;
    }

    @Override
    @Transactional
    public OneTimePassword create(User requester) {
        Objects.requireNonNull(requester, "Requester cannot be null");

        otpDao.deleteIfPresentByUser(requester);            // Only one OTP is allowed per User

        String plainOtp = generateRandomOneTimePassword();
        Instant now = Instant.now();

        otpDao.create(requester.getId(), passwordEncoder.encode(plainOtp), now);    // OTPs are stored encoded

        return OneTimePassword.builder()
                .requesterId(requester.getId())
                .otpValue(plainOtp)                 // Return the OTP un-encoded so it can be sent via e-mail
                .createdAt(now)
                .build();
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void clearUnused() {
        otpDao.deleteOlderThan(Instant.now().minus(OTP_EXPIRATION));
    }

    private String generateRandomOneTimePassword() {
        StringBuilder sb = new StringBuilder(OneTimePasswordServiceImpl.OTP_LENGTH);
        for (int i = 0; i < OneTimePasswordServiceImpl.OTP_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
