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
    private final @NonNull String brand;
    private final @NonNull String model;
    private final @NonNull Integer year;
    private final @NonNull Subcategory subcategory;
}
