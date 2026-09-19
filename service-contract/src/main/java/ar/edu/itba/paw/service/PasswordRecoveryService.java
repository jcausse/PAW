package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;

import java.util.Optional;

public interface PasswordRecoveryService {

    Optional<OneTimePassword> startAndSendRecoveryEmail(String usernameOrEmail);

    OneTimePasswordVerificationResult verifyAndUpdatePassword(String usernameOrEmail, String password, String otpValue);
}
