package ar.edu.itba.paw.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Builder
@ToString
public final class Offer {
    @EqualsAndHashCode.Include
    private final @NonNull Long id;
    private final @NonNull Listing listing;
    private final @NonNull User buyer;
    private final @NonNull BigDecimal amount;
    private final @NonNull Boolean isFullPrice;
    private final @NonNull OfferStatus status;
    private final String message;
    private final boolean hasOtherOffers;
    private final boolean hasBetterOffers;
    private final @NonNull Instant createdAt;
    private final Long proofOfPaymentId;
    private final String proofOfPaymentFilename;
    private final String proofOfPaymentContentType;
    private final Long proofOfPaymentSize;
    private final Long proofOfShippingId;
    private final String proofOfShippingFilename;
    private final String proofOfShippingContentType;
    private final Long proofOfShippingSize;
    private final String trackingNumber;
    private final Long offeredListingId;

    public String getProofOfPaymentExtension() {
        if (proofOfPaymentFilename == null || proofOfPaymentFilename.isBlank()) {
            return "";
        }
        int lastDot = proofOfPaymentFilename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == proofOfPaymentFilename.length() - 1) {
            return "";
        }
        return proofOfPaymentFilename.substring(lastDot + 1).toLowerCase();
    }

    public Long getProofOfPaymentSizeKb() {
        if (proofOfPaymentSize == null || proofOfPaymentSize <= 0) {
            return 0L;
        }
        return proofOfPaymentSize / 1024;
    }

    public boolean proofOfPaymentIsImage() {
        return proofOfPaymentContentType != null && proofOfPaymentContentType.startsWith("image/");
    }

    public String getProofOfShippingExtension() {
        if (proofOfShippingFilename == null || proofOfShippingFilename.isBlank()) {
            return "";
        }
        int lastDot = proofOfShippingFilename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == proofOfShippingFilename.length() - 1) {
            return "";
        }
        return proofOfShippingFilename.substring(lastDot + 1).toLowerCase();
    }

    public Long getProofOfShippingSizeKb() {
        if (proofOfShippingSize == null || proofOfShippingSize <= 0) {
            return 0L;
        }
        return proofOfShippingSize / 1024;
    }

    public boolean proofOfShippingIsImage() {
        return proofOfShippingContentType != null && proofOfShippingContentType.startsWith("image/");
    }
}
