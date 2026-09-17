package ar.edu.itba.paw.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public enum OfferStatusGroup {
    PENDING("pending"),
    RESOLVED("resolved");

    @Getter
    private final String status;

    public static Optional<OfferStatusGroup> fromString(final String status) {
        return Arrays.stream(OfferStatusGroup.values())
                .filter(s -> s.status.equalsIgnoreCase(status))
                .findFirst();
    }

    public List<OfferStatus> toStatusList() {
        return switch (this) {
            case PENDING -> List.of(OfferStatus.PENDING);
            case RESOLVED -> List.of(OfferStatus.ACCEPTED, OfferStatus.REJECTED, OfferStatus.WITHDRAWN);
        };
    }
}
