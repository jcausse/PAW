package ar.edu.itba.paw.model;
import lombok.*;
import java.util.List;
@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
@ToString
public final class Listing {
    private final @NonNull Long id;
    private final @NonNull String title;
    private final @NonNull User creator;
    private final @NonNull Price price;
    private final Product product;
    private final String description;
    private final @NonNull ListingStatus status;
    private final @NonNull Condition condition;
    private final boolean acceptsTrade;
    private final List<Long> imageIds;
}
