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
    private String offerType; // "full", "custom", or "trade"
    private BigDecimal customAmount;
    private Long offeredListingId;
    private BigDecimal tradeAmount;
    private String message;
}