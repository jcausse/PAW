package ar.edu.itba.paw.model;

import lombok.*;

import java.time.Instant;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Builder
@ToString
public final class Rating {
    @EqualsAndHashCode.Include
    private final @NonNull Long id;
    private final @NonNull User creator;
    private final @NonNull User rated;
    private final @NonNull Listing listing;
    private final @NonNull RatingRole role;
    private final @NonNull OfferRating type;
    private final @NonNull String reviewText;
    private final @NonNull Instant createdAt;
}