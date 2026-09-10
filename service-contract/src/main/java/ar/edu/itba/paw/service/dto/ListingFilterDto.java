package ar.edu.itba.paw.service.dto;
import java.math.BigDecimal;

import ar.edu.itba.paw.model.ListingStatus;

public record ListingFilterDto(
    Long categoryId,
    Long subcategoryId,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    String condition,
    Boolean acceptsTrade,
    String query,
    String sort,
    Long creatorId,
    String status
) {}
