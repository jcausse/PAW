package ar.edu.itba.paw.service.dto;

import ar.edu.itba.paw.model.User;

public record UserEditDto(
        User user,
        String newDisplayName,
        String newEmail,
        String newPassword,
        ImageData newImageData
) {}
