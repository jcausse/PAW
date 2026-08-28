package ar.edu.itba.paw.model;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
@ToString
public final class Product {

    private final @NonNull Long id;
    private final @NonNull String name;

    // TODO brand, model, year, etc
}
