package ar.edu.itba.paw.webapp.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@NoArgsConstructor
@Getter
@Setter
public class AdminTakeDownOfferForm {

    @NotNull
    @Positive
    private Long offerId;
}
