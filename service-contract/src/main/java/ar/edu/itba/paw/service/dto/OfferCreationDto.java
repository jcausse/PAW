package ar.edu.itba.paw.service.dto;

import java.math.BigDecimal;

public record OfferCreationDto(
    Long listingId,
    Long buyerId,
    BigDecimal amount,
    Boolean isFullPrice,
    String message
) {}