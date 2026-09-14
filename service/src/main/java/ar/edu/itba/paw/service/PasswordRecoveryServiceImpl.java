package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private final UserService userService;
    private final OneTimePasswordService otpService;


    @Override
    public Optional<OneTimePassword> startByUsername(String username) {
        return userService.getByUsername(username).map(otpService::create);
    }

    @Override
    public Optional<OneTimePassword> startByEmail(String email) {
        return userService.getByEmail(email).map(otpService::create);
    }

    @Override
    public OneTimePasswordVerificationResult verifyByUsername(String username, String otpValue) {
        return userService.getByUsername(username)
                .map(u -> otpService.verify(u, otpValue))
                .orElse(OneTimePasswordVerificationResult.REJECTED);
    }

    @Override
    public OneTimePasswordVerificationResult verifyByEmail(String email, String otpValue) {
        return userService.getByEmail(email)
                .map(u -> otpService.verify(u, otpValue))
                .orElse(OneTimePasswordVerificationResult.REJECTED);
    }
}
