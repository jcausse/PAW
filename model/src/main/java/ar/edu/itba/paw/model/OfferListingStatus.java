package ar.edu.itba.paw.model;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OfferListingStatus {
    AVAILABLE("available"),
    SOLD("sold");

    @Getter private final String status;

    public static Optional<OfferListingStatus> fromString(final String status) {
        return Arrays.stream(OfferListingStatus.values())
            .filter(s -> s.status.equalsIgnoreCase(status))
            .findFirst();
    }
}