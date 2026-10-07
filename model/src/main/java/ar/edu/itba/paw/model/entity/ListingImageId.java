package ar.edu.itba.paw.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class ListingImageId implements Serializable {


    @Serial
    private static final long serialVersionUID = 2817646393166039994L;
}