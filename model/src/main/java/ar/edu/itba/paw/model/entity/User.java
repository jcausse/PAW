package ar.edu.itba.paw.model.entity;

import ar.edu.itba.paw.model.Language;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Column(name = "seller_positive_ratings", nullable = false)
    private Integer sellerPositiveRatings;

    @Column(name = "seller_neutral_ratings", nullable = false)
    private Integer sellerNeutralRatings;

    @Column(name = "seller_negative_ratings", nullable = false)
    private Integer sellerNegativeRatings;

    @Column(name = "buyer_positive_ratings", nullable = false)
    private Integer buyerPositiveRatings;

    @Column(name = "buyer_neutral_ratings", nullable = false)
    private Integer buyerNeutralRatings;

    @Column(name = "buyer_negative_ratings", nullable = false)
    private Integer buyerNegativeRatings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private Province province;

    @Column(name = "location_detail", length = 100)
    private String locationDetail;

    @Column(name = "preferred_language", nullable = false, length = 8)
    private Language preferredLanguage;

    @Column(name = "suspended_at")
    private Instant suspendedAt;


}