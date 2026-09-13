package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.OneTimePasswordDao;
import lombok.RequiredArgsConstructor;
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
    public boolean verify(User user, String otpValue) {
        if (user == null || otpValue == null || otpValue.isBlank()) {
            return false;
        }

        Optional<OneTimePassword> maybeOtp = otpDao.getByUser(user);
        if (maybeOtp.isEmpty()) {
            return false;
        }

        OneTimePassword otp = maybeOtp.get();
        if (otp.getCreatedAt().plus(OTP_EXPIRATION).isBefore(Instant.now())) {
            otpDao.deleteIfPresentByUser(user);
            return false;
        }

        return passwordEncoder.matches(otpValue, otp.getOtpValue());
    }

    @Override
    @Transactional
    public OneTimePassword create(User requester) {
        Objects.requireNonNull(requester, "Requester cannot be null");

        otpDao.deleteIfPresentByUser(requester);

        String plainOtp = generateRandomOneTimePassword();
        Instant now = Instant.now();

        otpDao.create(requester.getId(), passwordEncoder.encode(plainOtp), now);

        return OneTimePassword.builder()
                .requesterId(requester.getId())
                .otpValue(plainOtp)
                .createdAt(now)
                .build();
    }

    private String generateRandomOneTimePassword() {
        StringBuilder sb = new StringBuilder(OneTimePasswordServiceImpl.OTP_LENGTH);
        for (int i = 0; i < OneTimePasswordServiceImpl.OTP_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
