package ar.edu.itba.paw.model.entity;

import ar.edu.itba.paw.model.Condition;
import ar.edu.itba.paw.model.ListingStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "listings")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private ar.edu.itba.paw.model.entity.User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ar.edu.itba.paw.model.entity.Product product;

    @Column(name = "price", nullable = false, precision = 100, scale = 2)
    private BigDecimal price;

    @Column(name = "status", nullable = false, length = 20)
    private ListingStatus status;

    @Column(name = "condition", nullable = false, length = 20)
    private Condition condition;

    @Column(name = "accepts_trade", nullable = false)
    private Boolean acceptsTrade;

    @Column(name = "accepts_shipping", nullable = false)
    private Boolean acceptsShipping;

    @OneToMany(mappedBy = "listing")
    private Set<ListingImage> listingImages = new LinkedHashSet<>();


}