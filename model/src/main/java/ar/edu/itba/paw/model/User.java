package ar.edu.itba.paw.model;

import lombok.*;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Builder
@ToString(exclude = {"password", "emailVerifiedAt"})
public final class User {

    @EqualsAndHashCode.Include
    private final @NonNull Long id;
    private final @NonNull String username;
    private final @NonNull String displayName;
    private final @NonNull String email;
    private final @NonNull String password;
    private final int sellerPositiveRatings;
    private final int sellerNeutralRatings;
    private final int sellerNegativeRatings;
    private final int buyerPositiveRatings;
    private final int buyerNeutralRatings;
    private final int buyerNegativeRatings;

    // Nullable! Users without a profile picture will have this set to null
    private final Long imageId;
    public Optional<Long> getImageId() {                    // Overrides Lombok's getter
        return Optional.ofNullable(imageId);
    }

    private final @NonNull Instant joinedAt;

    // Nullable! Users without a verified email will have this set to null
    private final Instant emailVerifiedAt;
    public Optional<Instant> getEmailVerifiedAt() {         // Overrides Lombok's getter
        return Optional.ofNullable(emailVerifiedAt);
    }

    public boolean isVerified() {
        return emailVerifiedAt != null;
    }

    private final Province province;
    public Optional<Province> getProvince() {              
        return Optional.ofNullable(province);
    }

    private final String locationDetail;
    public Optional<String> getLocationDetail() {          
        return Optional.ofNullable(locationDetail);
    }

    public int getSellerTotalRatings() {
        return sellerPositiveRatings + sellerNeutralRatings + sellerNegativeRatings;
    }

    public int getBuyerTotalRatings() {
        return buyerPositiveRatings + buyerNeutralRatings + buyerNegativeRatings;
    }

    public int getSellerRatingBalance() {
        return sellerPositiveRatings - sellerNegativeRatings;
    }

    public int getBuyerRatingBalance() {
        return buyerPositiveRatings - buyerNegativeRatings;
    }
}
