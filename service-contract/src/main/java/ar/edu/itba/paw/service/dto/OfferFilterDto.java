package ar.edu.itba.paw.service.dto;

public record OfferFilterDto(
    Long sellerId,
    Long buyerId,
    String statusGroup,
    Integer page,
    Integer pageSize
) {}
