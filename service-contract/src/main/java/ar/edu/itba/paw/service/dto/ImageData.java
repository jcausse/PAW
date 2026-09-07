package ar.edu.itba.paw.service.dto;

public record ImageData(
    byte[] imageBytes,
    String imageFilename,
    String imageContentType
) {}