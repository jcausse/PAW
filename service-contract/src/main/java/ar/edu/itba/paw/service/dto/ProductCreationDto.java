package ar.edu.itba.paw.service.dto;

public record ProductCreationDto(
    String brand,
    String model,
    Integer year,
    Long subcategoryId
) {}
