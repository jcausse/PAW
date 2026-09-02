package ar.edu.itba.paw.model;

import lombok.*;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
@ToString
public final class Category {

    private final @NonNull Long id;
    private final @NonNull String name;
}
