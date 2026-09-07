package ar.edu.itba.paw.service.dto;

public record UserCreationDto(
        String username,
        String displayName,
        String email,
        String password,
        byte[] imageBytes,
        String imageFilename,
        String imageContentType
) {}
