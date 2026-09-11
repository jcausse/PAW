package ar.edu.itba.paw.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class ListingFilter {

    private final Long categoryId;
    private final Long subcategoryId;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final Condition condition;
    private final Boolean acceptsTrade;
    private final String query;
    private final ListingSort sort;
    private final int page;
    private final int pageSize;
}
