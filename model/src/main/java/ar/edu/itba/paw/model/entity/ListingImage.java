package ar.edu.itba.paw.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "listing_images")
public class ListingImage {
    @EmbeddedId
    private ListingImageId id;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @JoinColumn(name = "listing_id", nullable = false)
    private ar.edu.itba.paw.model.entity.Listing listing;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;


}