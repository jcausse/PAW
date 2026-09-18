package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;

import java.util.Optional;

public interface PasswordRecoveryService {

    Optional<OneTimePassword> start(String usernameOrEmail);

    OneTimePasswordVerificationResult verify(String usernameOrEmail, String otpValue);
}
