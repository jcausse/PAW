package ar.edu.itba.paw.service.dto;

import lombok.Builder;

@Builder
public record OfferFilterDto(
    Long sellerId,
    Long buyerId,
    String statusGroup,
    Integer page,
    Integer pageSize
) {}
