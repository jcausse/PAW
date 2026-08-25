package ar.edu.itba.paw.service.dto;

public record UserCreationDto(
        String username,
        String firstName,
        String lastName,
        String email,
        String password
) {}
