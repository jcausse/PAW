package ar.edu.itba.paw.model;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
@ToString
public final class User {
    private final @NonNull Long id;
    private final @NonNull String username;
    private final @NonNull String firstName;
    private final @NonNull String lastName;
    private final @NonNull String email;
}
