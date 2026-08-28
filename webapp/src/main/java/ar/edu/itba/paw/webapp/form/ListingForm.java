package ar.edu.itba.paw.webapp.form;

import java.math.BigDecimal;
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
    private BigDecimal price;
}
