package ar.edu.itba.paw.model;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ListingStatus {
    ACTIVE("ACTIVE"),
    SOLD("SOLD");

    @Getter private final String status;

    public static Optional<ListingStatus> fromString(final String status) {
        return Arrays.stream(ListingStatus.values())
            .filter(s -> s.status.equalsIgnoreCase(status))
            .findFirst();
    }
}
