package ar.edu.itba.paw.model;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
@ToString
public final class Subcategory {

    private final @NonNull Long id;
    private final @NonNull String name;
    private final @NonNull Category category;
}
