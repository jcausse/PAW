package ar.edu.itba.paw.model;

import lombok.*;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Builder
@ToString
public final class Province {

    @EqualsAndHashCode.Include
    private final @NonNull Long id;
    private final @NonNull String name;
}
