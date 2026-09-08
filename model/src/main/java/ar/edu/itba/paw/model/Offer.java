package ar.edu.itba.paw.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
@ToString
public final class Offer {
    private final @NonNull Long id;
    private final @NonNull Long listingId;
    private final @NonNull Long buyerId;
    private final @NonNull BigDecimal amount;
    private final @NonNull Boolean isFullPrice;
    private final @NonNull OfferStatus status;

    // Optional buyer info (populated when joining with users table)
    private final String buyerUsername;
    private final String buyerDisplayName;
    private final Long buyerImageId;

    // Optional message from buyer
    private final String message;

    public Optional<String> getBuyerUsernameOpt() {
        return Optional.ofNullable(buyerUsername);
    }

    public Optional<String> getBuyerDisplayNameOpt() {
        return Optional.ofNullable(buyerDisplayName);
    }

    public Optional<Long> getBuyerImageIdOpt() {
        return Optional.ofNullable(buyerImageId);
    }

    public Optional<String> getMessageOpt() {
        return Optional.ofNullable(message);
    }
}