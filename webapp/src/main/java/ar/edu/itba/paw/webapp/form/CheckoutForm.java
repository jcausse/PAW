package ar.edu.itba.paw.webapp.form;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CheckoutForm {

    private Long listingId;
    private String offerType; // "full" or "custom"
    private BigDecimal customAmount;
    private String message;
}