package ar.edu.itba.paw.service.enumeration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OneTimePasswordVerificationResult {
    ACCEPTED,
    REJECTED,
    EXPIRED
}
