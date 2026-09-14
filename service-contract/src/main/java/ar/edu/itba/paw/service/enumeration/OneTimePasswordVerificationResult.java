package ar.edu.itba.paw.service.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OneTimePasswordVerificationResult {
    ACCEPTED("Ok"),
    REJECTED("The password recovery code you entered is not valid. Please check the code or create a new one."),
    EXPIRED("The password recovery code you entered has expired. Please create a new one.");

    @Getter
    private final String message;
}
