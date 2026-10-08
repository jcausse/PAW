package ar.edu.itba.paw.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public enum OfferRating {
    POSITIVE("POSITIVE"),
    NEUTRAL("NEUTRAL"),
    NEGATIVE("NEGATIVE");

    @Getter
    private final String rating;

    public static Optional<OfferRating> fromString(final String rating) {
        return Arrays.stream(OfferRating.values())
                .filter(r -> r.rating.equalsIgnoreCase(rating) || r.name().equalsIgnoreCase(rating))
                .findFirst();
    }
}