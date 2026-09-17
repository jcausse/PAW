package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.enumeration.OneTimePasswordVerificationResult;

public interface OneTimePasswordService {

    OneTimePasswordVerificationResult verify(User user, String otpValue);

    OneTimePassword create(User requester);

    @SuppressWarnings("unused")     // This is a scheduled method
    void clearUnused();
}
