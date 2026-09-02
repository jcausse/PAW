package ar.edu.itba.paw.model;

import lombok.*;

import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
@ToString
public final class User {

    private final @NonNull Long id;
    private final @NonNull String username;
    private final @NonNull String displayName;
    private final @NonNull String email;
    private final @NonNull String password;

    // Nullable! Users without a profile picture will have this set to null
    private final Long imageId;

    // Overrides Lombok's getter
    public Optional<Long> getImageId() {
        return Optional.ofNullable(imageId);
    }
}
