package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.service.dto.ImageData;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.service.dto.UserEditDto;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getById(@NonNull Long id) {
        return userDao.getById(id);
    }

    @Override
    public Optional<User> getByUsername(@NonNull String username) {
        return userDao.getByUsername(username.trim().toLowerCase());
    }

    @Override
    public Optional<User> getByEmail(@NonNull String email) {
        return userDao.getByEmail(email.trim().toLowerCase());
    }

    @Override
    public Optional<User> getByUsernameOrEmail(@NonNull String usernameOrEmail) {
        return usernameOrEmail.contains("@")
                ? getByEmail(usernameOrEmail)
                : getByUsername(usernameOrEmail);
    }

    @Override
    @Transactional
    public User create(@NonNull UserCreationDto dto) {
        if (dto.username().contains("@")) {
            throw new IllegalArgumentException("UserCreationDto.username cannot contain @");
        }

        return userDao.create(
            dto.username().trim().toLowerCase(),
            dto.displayName().trim(),
            dto.email().trim().toLowerCase(),
            passwordEncoder.encode(dto.password()),
            saveUserImage(dto.image(), dto.username()),
            Instant.now()
        );
    }

    @Override
    @Transactional
    public User update(@NonNull UserEditDto dto) {
        Objects.requireNonNull(dto.user(), "User cannot be null");
        final var user = dto.user();

        /* Prepare User properties to be updated */
        var displayName = (dto.newDisplayName() != null && !dto.newDisplayName().isBlank())
                ? dto.newDisplayName()
                : null;
        var encodedPassword = (dto.newPassword() != null && !dto.newPassword().isBlank())
                ? passwordEncoder.encode(dto.newPassword())
                : null;
        Optional<Image> maybeNewImage = Optional.ofNullable(saveUserImage(
                dto.newImageData(),
                dto.user().getUsername()
        ));

        /* Update User */
        final var updateResult = userDao.update(
                user.getId(),
                displayName,
                null,
                encodedPassword,
                maybeNewImage.map(Image::getId).orElse(null)
        );

        /* Delete the old image to prevent orphans, if a new one was set and the user previously had one */
        if (maybeNewImage.isPresent() && user.getImageId().isPresent()) {
            imageService.delete(user.getImageId().get());
        }

        return updateResult
                .orElseThrow(() -> new IllegalArgumentException("Non-valid User received"));
    }

    @Override
    @Transactional
    public User updateEmail(@NonNull User user, @NonNull String email) {
        return userDao.update(user.getId(), null, email.trim().toLowerCase(), null, null)
                .orElseThrow(() -> new IllegalArgumentException("Non-valid User received"));
    }

    @Override
    @Transactional
    public User markEmailAsVerified(@NonNull User user) {
        return userDao.verifyEmail(user.getId(), Instant.now())
                .orElseThrow(() -> new IllegalArgumentException("Non-valid User received"));
    }

    @Override
    public boolean isUsernameTaken(@NonNull String username) {
        return userDao.isUsernameTaken(username.trim().toLowerCase());
    }

    @Override
    public boolean isEmailTaken(@NonNull String email) {
        return userDao.isEmailTaken(email.trim().toLowerCase());
    }

    private Image saveUserImage(ImageData imageData, String username) {
        if (imageData != null && imageData.imageBytes() != null && imageData.imageBytes().length > 0) {
            return imageService.create(
                    imageData.imageFilename(),
                    username + "'s profile picture",
                    imageData.imageContentType(),
                    imageData.imageBytes()
            );
        }
        return null;
    }
}
