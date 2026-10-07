public void setId1(java.lang.Integer id1) {
  this.id1 = id1;
}package ar.edu.itba.paw.model;

import lombok.*;

import javax.persistence.*;
import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Builder
@ToString
@Entity
@Table(name = "files")
public final class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    private Integer id1;@EqualsAndHashCode.Include
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