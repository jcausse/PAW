package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.OneTimePasswordDao;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OneTimePasswordServiceImplTest {

    private static final long USER_ID = 1L;
    private static final String PLAIN_OTP = "ABC1234567";
    private static final String ENCODED_OTP = "encoded-otp";

    @InjectMocks
    private OneTimePasswordServiceImpl otpService;

    @Mock
    private OneTimePasswordDao otpDao;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User buildUser() {
        return User.builder()
            .id(USER_ID)
            .username("fake_user")
            .displayName("Fake User")
            .email("fake@example.com")
            .password("secret")
            .joinedAt(Instant.now())
            .build();
    }

    private OneTimePassword buildOtp(final Instant createdAt) {
        return OneTimePassword.builder()
            .requesterId(USER_ID)
            .otpValue(ENCODED_OTP)
            .createdAt(createdAt)
            .build();
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* verify                                                                                          */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testVerifyNullUserRejected() {
        final OneTimePasswordVerificationResult result = otpService.verify(null, PLAIN_OTP);

        Assert.assertEquals(OneTimePasswordVerificationResult.REJECTED, result);
        verifyNoInteractions(otpDao);
    }

    @Test
    public void testVerifyBlankOtpRejected() {
        final OneTimePasswordVerificationResult result = otpService.verify(buildUser(), "   ");

        Assert.assertEquals(OneTimePasswordVerificationResult.REJECTED, result);
        verifyNoInteractions(otpDao);
    }

    @Test
    public void testVerifyNoStoredOtpRejected() {
        // Arrange
        final User user = buildUser();
        when(otpDao.getByUser(user)).thenReturn(Optional.empty());

        // Act
        final OneTimePasswordVerificationResult result = otpService.verify(user, PLAIN_OTP);

        // Assert
        Assert.assertEquals(OneTimePasswordVerificationResult.REJECTED, result);
        verify(otpDao, never()).deleteIfPresentByUser(any());
    }

    @Test
    public void testVerifyExpiredReturnsExpiredAndDeletes() {
        // Arrange: created 16 minutes ago (expiration is 15 min)
        final User user = buildUser();
        when(otpDao.getByUser(user)).thenReturn(
            Optional.of(buildOtp(Instant.now().minus(16, ChronoUnit.MINUTES))));

        // Act
        final OneTimePasswordVerificationResult result = otpService.verify(user, PLAIN_OTP);

        // Assert
        Assert.assertEquals(OneTimePasswordVerificationResult.EXPIRED, result);
        verify(otpDao).deleteIfPresentByUser(user);
    }

    @Test
    public void testVerifyWrongCodeRejectedWithoutDeleting() {
        // Arrange: valid (recent) OTP, but wrong code entered
        final User user = buildUser();
        when(otpDao.getByUser(user)).thenReturn(Optional.of(buildOtp(Instant.now())));
        when(passwordEncoder.matches(eq(PLAIN_OTP), eq(ENCODED_OTP))).thenReturn(false);

        // Act
        final OneTimePasswordVerificationResult result = otpService.verify(user, PLAIN_OTP);

        // Assert
        Assert.assertEquals(OneTimePasswordVerificationResult.REJECTED, result);
        verify(otpDao, never()).deleteIfPresentByUser(any());
    }

    @Test
    public void testVerifyCorrectCodeAcceptedAndDeletes() {
        // Arrange: valid (recent) OTP, correct code
        final User user = buildUser();
        when(otpDao.getByUser(user)).thenReturn(Optional.of(buildOtp(Instant.now())));
        when(passwordEncoder.matches(eq(PLAIN_OTP), eq(ENCODED_OTP))).thenReturn(true);

        // Act
        final OneTimePasswordVerificationResult result = otpService.verify(user, PLAIN_OTP);

        // Assert
        Assert.assertEquals(OneTimePasswordVerificationResult.ACCEPTED, result);
        verify(otpDao).deleteIfPresentByUser(user);
    }

    @Test
    public void testVerifyTrimsOtpBeforeMatching() {
        // Arrange
        final User user = buildUser();
        when(otpDao.getByUser(user)).thenReturn(Optional.of(buildOtp(Instant.now())));
        when(passwordEncoder.matches(eq(PLAIN_OTP), eq(ENCODED_OTP))).thenReturn(true);

        // Act: user enters the code with surrounding whitespace
        final OneTimePasswordVerificationResult result = otpService.verify(user, "  " + PLAIN_OTP + "  ");

        // Assert: trimmed value is what gets matched
        Assert.assertEquals(OneTimePasswordVerificationResult.ACCEPTED, result);
        verify(passwordEncoder).matches(eq(PLAIN_OTP), eq(ENCODED_OTP));
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* create                                                                                          */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testCreateDeletesPreviousOtpFirst() {
        // Arrange
        final User user = buildUser();
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_OTP);

        // Act
        otpService.create(user);

        // Assert: only one OTP per user -> previous one is removed before creating
        verify(otpDao).deleteIfPresentByUser(user);
    }

    @Test
    public void testCreateStoresEncodedOtp() {
        // Arrange
        final User user = buildUser();
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_OTP);

        // Act
        otpService.create(user);

        // Assert: the stored value is the ENCODED one, never the plain text
        verify(otpDao).create(eq(USER_ID), eq(ENCODED_OTP), any(Instant.class));
    }

    @Test
    public void testCreateReturnsPlainOtpToCaller() {
        // Arrange
        final User user = buildUser();
        final ArgumentCaptor<String> encodeArg = ArgumentCaptor.forClass(String.class);
        when(passwordEncoder.encode(encodeArg.capture())).thenReturn(ENCODED_OTP);

        // Act
        final OneTimePassword result = otpService.create(user);

        // Assert: returned value is the plain OTP (what was passed to encode), not the encoded one
        Assert.assertEquals(USER_ID, (long) result.getRequesterId());
        Assert.assertEquals(encodeArg.getValue(), result.getOtpValue());
        Assert.assertNotEquals(ENCODED_OTP, result.getOtpValue());
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* clearUnused                                                                                     */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testClearUnusedDeletesOldOtps() {
        // Act
        otpService.clearUnused();

        // Assert
        verify(otpDao).deleteOlderThan(any(Instant.class));
    }
}
