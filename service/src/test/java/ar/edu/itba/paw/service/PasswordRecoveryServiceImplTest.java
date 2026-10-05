package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.dto.UserEditDto;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PasswordRecoveryServiceImplTest {

    private static final long USER_ID = 1L;
    private static final String USERNAME_OR_EMAIL = "fake@example.com";
    private static final String NEW_PASSWORD = "newSecret123";
    private static final String OTP_VALUE = "ABC1234567";

    @InjectMocks
    private PasswordRecoveryServiceImpl recoveryService;

    @Mock
    private UserService userService;
    @Mock
    private OneTimePasswordService otpService;
    @Mock
    private MailingService mailingService;

    private User buildUser() {
        return User.builder()
            .id(USER_ID)
            .username("fake_user")
            .displayName("Fake User")
            .email(USERNAME_OR_EMAIL)
            .password("oldSecret")
            .joinedAt(Instant.now())
            .build();
    }

    private OneTimePassword buildOtp() {
        return OneTimePassword.builder()
            .requesterId(USER_ID)
            .otpValue(OTP_VALUE)
            .createdAt(Instant.now())
            .build();
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* startAndSendRecoveryEmail                                                                        */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testStartUnknownUserReturnsEmptyAndSendsNothing() {
        // Arrange
        when(userService.getByUsernameOrEmail(eq(USERNAME_OR_EMAIL))).thenReturn(Optional.empty());

        // Act
        final Optional<OneTimePassword> result = recoveryService.startAndSendRecoveryEmail(USERNAME_OR_EMAIL);

        // Assert: no OTP created, no email sent
        Assert.assertTrue(result.isEmpty());
        verify(otpService, never()).create(any());
        verify(mailingService, never()).sendPasswordRecoveryEmail(any(), any(), any());
    }

    @Test
    public void testStartKnownUserCreatesOtpAndSendsEmail() {
        // Arrange
        final User user = buildUser();
        final OneTimePassword otp = buildOtp();
        when(userService.getByUsernameOrEmail(eq(USERNAME_OR_EMAIL))).thenReturn(Optional.of(user));
        when(otpService.create(eq(user))).thenReturn(otp);

        // Act
        final Optional<OneTimePassword> result = recoveryService.startAndSendRecoveryEmail(USERNAME_OR_EMAIL);

        // Assert
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(OTP_VALUE, result.get().getOtpValue());
        verify(otpService).create(user);
        verify(mailingService).sendPasswordRecoveryEmail(eq(user), eq(OTP_VALUE), any(Locale.class));
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* verifyAndUpdatePassword                                                                          */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testVerifyUnknownUserRejectedAndNoUpdate() {
        // Arrange
        when(userService.getByUsernameOrEmail(eq(USERNAME_OR_EMAIL))).thenReturn(Optional.empty());

        // Act
        final OneTimePasswordVerificationResult result =
            recoveryService.verifyAndUpdatePassword(USERNAME_OR_EMAIL, NEW_PASSWORD, OTP_VALUE);

        // Assert
        Assert.assertEquals(OneTimePasswordVerificationResult.REJECTED, result);
        verify(otpService, never()).verify(any(), any());
        verify(userService, never()).update(any());
    }

    @Test
    public void testVerifyAcceptedUpdatesPassword() {
        // Arrange
        final User user = buildUser();
        when(userService.getByUsernameOrEmail(eq(USERNAME_OR_EMAIL))).thenReturn(Optional.of(user));
        when(otpService.verify(eq(user), eq(OTP_VALUE)))
            .thenReturn(OneTimePasswordVerificationResult.ACCEPTED);

        // Act
        final OneTimePasswordVerificationResult result =
            recoveryService.verifyAndUpdatePassword(USERNAME_OR_EMAIL, NEW_PASSWORD, OTP_VALUE);

        // Assert
        Assert.assertEquals(OneTimePasswordVerificationResult.ACCEPTED, result);

        final ArgumentCaptor<UserEditDto> dtoCaptor = ArgumentCaptor.forClass(UserEditDto.class);
        verify(userService).update(dtoCaptor.capture());
        final UserEditDto dto = dtoCaptor.getValue();
        Assert.assertEquals(user, dto.user());
        Assert.assertEquals(NEW_PASSWORD, dto.newPassword());
    }

    @Test
    public void testVerifyRejectedDoesNotUpdatePassword() {
        // Arrange
        final User user = buildUser();
        when(userService.getByUsernameOrEmail(eq(USERNAME_OR_EMAIL))).thenReturn(Optional.of(user));
        when(otpService.verify(eq(user), eq(OTP_VALUE)))
            .thenReturn(OneTimePasswordVerificationResult.REJECTED);

        // Act
        final OneTimePasswordVerificationResult result =
            recoveryService.verifyAndUpdatePassword(USERNAME_OR_EMAIL, NEW_PASSWORD, OTP_VALUE);

        // Assert
        Assert.assertEquals(OneTimePasswordVerificationResult.REJECTED, result);
        verify(userService, never()).update(any());
    }

    @Test
    public void testVerifyExpiredDoesNotUpdatePassword() {
        // Arrange
        final User user = buildUser();
        when(userService.getByUsernameOrEmail(eq(USERNAME_OR_EMAIL))).thenReturn(Optional.of(user));
        when(otpService.verify(eq(user), eq(OTP_VALUE)))
            .thenReturn(OneTimePasswordVerificationResult.EXPIRED);

        // Act
        final OneTimePasswordVerificationResult result =
            recoveryService.verifyAndUpdatePassword(USERNAME_OR_EMAIL, NEW_PASSWORD, OTP_VALUE);

        // Assert
        Assert.assertEquals(OneTimePasswordVerificationResult.EXPIRED, result);
        verify(userService, never()).update(any());
    }
}
