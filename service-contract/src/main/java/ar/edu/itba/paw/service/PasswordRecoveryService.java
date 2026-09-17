package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;

import java.util.Optional;

public interface PasswordRecoveryService {
    Optional<OneTimePassword> startByUsername(String username);
    Optional<OneTimePassword> startByEmail(String email);
    
    OneTimePasswordVerificationResult verifyByUsername(String username, String otpValue);
    OneTimePasswordVerificationResult verifyByEmail(String email, String otpValue);

    Optional<OneTimePassword> startRecovery(String identifier);
    OneTimePasswordVerificationResult verifyRecovery(String identifier, String otpValue);
}
