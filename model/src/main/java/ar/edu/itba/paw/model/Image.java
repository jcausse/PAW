package ar.edu.itba.paw.model;

import lombok.*;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
@ToString
public final class Image {

    private final @NonNull Long id;
    private final @NonNull String filename;
    private final @NonNull String alt;
    private final byte[] data;
}
