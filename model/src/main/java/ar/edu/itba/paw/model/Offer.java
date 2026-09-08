package ar.edu.itba.paw.model;

import lombok.*;

import java.math.BigDecimal;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@Builder
@ToString
public final class Offer {
    private final @NonNull Long id;
    private final @NonNull Long listingId;
    private final @NonNull User buyer;
    private final @NonNull BigDecimal amount;
    private final @NonNull Boolean isFullPrice;
    private final @NonNull OfferStatus status;
    private final String message;
}