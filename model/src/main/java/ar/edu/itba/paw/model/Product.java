package ar.edu.itba.paw.model;

import lombok.*;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Builder
@ToString
public final class Product {

    @EqualsAndHashCode.Include
    private final @NonNull Long id;
    private final @NonNull String brand;
    private final @NonNull String model;
    private final @NonNull Integer year;
    private final @NonNull Subcategory subcategory;
}
