package ar.edu.itba.paw.webapp.form;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ListingFilterForm {

    private Long categoryId;
    private Long subcategoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String condition;
    private Boolean acceptsTrade;
    private String query;
    private String sort;
    private String status;
    private Integer page;
}
