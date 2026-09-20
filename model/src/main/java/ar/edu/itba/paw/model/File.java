package ar.edu.itba.paw.model;

import lombok.*;

import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
@ToString
public final class File {

    private final @NonNull Long id;
    private final @NonNull String filename;
    private final @NonNull String alt;

    // Nullable if not provided when creating file
    private final String contentType;

    private final byte[] data;

    public Optional<String> getContentType() {
        return Optional.ofNullable(contentType);
    }
}