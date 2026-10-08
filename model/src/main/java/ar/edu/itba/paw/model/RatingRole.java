package ar.edu.itba.paw.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public enum RatingRole {
    BUYER("BUYER"),
    SELLER("SELLER");

    @Getter
    private final String role;

    public static Optional<RatingRole> fromString(final String role) {
        return Arrays.stream(RatingRole.values())
                .filter(r -> r.role.equalsIgnoreCase(role) || r.name().equalsIgnoreCase(role))
                .findFirst();
    }
}
