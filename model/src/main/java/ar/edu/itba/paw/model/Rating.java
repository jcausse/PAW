package ar.edu.itba.paw.model;

import lombok.*;

import java.time.Instant;
import java.util.Optional;

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
    private final @NonNull Offer offer;
    private final @NonNull RatingRole role;
    private final @NonNull OfferRating type;
    private final String reviewText;
    private final @NonNull Instant createdAt;

    public Optional<String> getReviewText() {
        return Optional.ofNullable(reviewText);
    }
}