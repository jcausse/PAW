package ar.edu.itba.paw.service.dto;
import java.math.BigDecimal;

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
    String status,
    Integer page
) {}
