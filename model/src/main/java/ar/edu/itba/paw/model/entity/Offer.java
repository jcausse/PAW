package ar.edu.itba.paw.model.entity;

import ar.edu.itba.paw.model.OfferRating;
import ar.edu.itba.paw.model.OfferStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offer_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Column(name = "amount", nullable = false, precision = 100, scale = 2)
    private BigDecimal amount;

    @Column(name = "is_full_price", nullable = false)
    private Boolean isFullPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OfferStatus status;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proof_of_payment_id")
    private File proofOfPayment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proof_of_shipping_id")
    private File proofOfShipping;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offered_listing_id")
    private Listing offeredListing;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "buyer_rating", length = 10)
    private OfferRating buyerRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "seller_rating", length = 10)
    private OfferRating sellerRating;
}
