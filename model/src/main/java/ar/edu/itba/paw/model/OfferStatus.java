package ar.edu.itba.paw.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public enum OfferStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    @Getter
    private final String status;

    public static Optional<OfferStatus> fromString(final String status) {
        return Arrays.stream(OfferStatus.values())
                .filter(s -> s.status.equalsIgnoreCase(status))
                .findFirst();
    }
}
