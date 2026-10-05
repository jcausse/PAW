package ar.edu.itba.paw.service.dto;

import ar.edu.itba.paw.model.Language;
import ar.edu.itba.paw.model.User;
import lombok.Builder;

/**
 * Everything a single profile edit can change, applied as one transactional unit.
 *
 * <p>{@code updateLocationAndLanguage} is the intent flag: when {@code true} (the profile
 * edit form) the location and preferred language are applied — a null {@code newProvinceId}
 * then means "clear the location". When {@code false} (e.g. a password reset) those fields
 * are ignored, so unrelated flows never wipe the user's location by omission.
 */
@Builder
public record UserEditDto(
        User user,
        String newDisplayName,
        String newPassword,
        ImageData newImageData,
        boolean updateLocationAndLanguage,
        Long newProvinceId,
        String newLocationDetail,
        Language newPreferredLanguage
) {}
