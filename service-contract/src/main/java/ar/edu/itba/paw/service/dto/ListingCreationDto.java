package ar.edu.itba.paw.service.dto;

import ar.edu.itba.paw.model.Price;

public record ListingCreationDto(
    String title,
    Price price,
    Long creatorId,
    Long productId
) {}
