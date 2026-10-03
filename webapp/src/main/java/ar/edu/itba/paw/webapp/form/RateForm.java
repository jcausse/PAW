package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.OfferRating;
import ar.edu.itba.paw.model.RatingRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class RateForm {

    @NotNull
    private RatingRole role;

    @NotNull
    private OfferRating rating;

    private String reviewText;
}