package ar.edu.itba.paw.webapp.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ListingForm {

    private Long creatorId;
    private Long productId;
    private String title;
    private Long price;
}
