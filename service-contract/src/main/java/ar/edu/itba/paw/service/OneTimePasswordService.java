package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.model.User;

public interface OneTimePasswordService {

    boolean verify(User user, String otpValue);

    OneTimePassword create(User requester);
}
