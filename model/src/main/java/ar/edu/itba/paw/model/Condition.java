package ar.edu.itba.paw.model;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Condition {
    LIKE_NEW("LIKE_NEW"),
    GOOD("GOOD"),
    FAIR("FAIR"),
    POOR("POOR");

    @Getter private final String condition;

    public static Optional<Condition> fromString(final String condition) {
        return Arrays.stream(Condition.values())
            .filter(c -> c.condition.equalsIgnoreCase(condition))
            .findFirst();
    }
}
