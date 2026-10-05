package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OfferFilterForm {

    private String statusGroup;
    @Min(1)
    private Integer page;
}
