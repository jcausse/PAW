package ar.edu.itba.paw.webapp.form;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ListingForm {

    private Integer step = 1;

    private Long categoryId;
    private Long subcategoryId;

    private Long existingProductId;
    private String newProductBrand;
    private String newProductModel;
    private Integer newProductYear;

    private String title;
    private BigDecimal price;
}