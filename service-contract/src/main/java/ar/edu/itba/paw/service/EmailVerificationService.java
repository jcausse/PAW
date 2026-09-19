package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;

import java.util.Optional;

public interface EmailVerificationService {

    OneTimePassword sendVerificationEmail(User user);

    Optional<OneTimePassword> resendVerificationEmail(String usernameOrEmail);

    OneTimePasswordVerificationResult verifyEmail(String usernameOrEmail, String otpValue);
}
