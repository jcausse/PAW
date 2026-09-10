package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.service.dto.UserEditDto;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final ImageService imageService;
    private final MailingService mailingService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getById(Long id) {
        return userDao.getById(id);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        Objects.requireNonNull(username, "username cannot be null");
        return userDao.getByUsername(username.toLowerCase());
    }

    @Override
    public Optional<User> getByEmail(String email) {
        Objects.requireNonNull(email, "email cannot be null");
        return userDao.getByEmail(email.toLowerCase());
    }

    @Override
    @Transactional
    public User create(UserCreationDto dto) {
        Objects.requireNonNull(dto, "UserCreationDto cannot be null");

        Image image = null;
        if (dto.image() != null && dto.image().imageBytes() != null && dto.image().imageBytes().length > 0) {
            String alt = dto.username() + "'s profile picture";
            image = imageService.create(dto.image().imageFilename(), alt, dto.image().imageContentType(), dto.image().imageBytes());
        }

        var user = userDao.create(
            dto.username().toLowerCase(),
            dto.displayName(),
            dto.email().toLowerCase(),
            passwordEncoder.encode(dto.password()),
            image
        );

        mailingService.sendWelcomeEmail(user, LocaleContextHolder.getLocale());

        return user;
    }

    @Override
    @Transactional
    public User update(UserEditDto dto) {
        Objects.requireNonNull(dto, "UserEditDto cannot be null");
        Objects.requireNonNull(dto.user(), "User cannot be null");

        var user = dto.user();

        String displayName = (dto.newDisplayName() != null && !dto.newDisplayName().isBlank())
                ? dto.newDisplayName()
                : null;
        String email = (dto.newEmail() != null && !dto.newEmail().isBlank())
                ? dto.newEmail().toLowerCase()
                : null;
        String encodedPassword = (dto.newPassword() != null && !dto.newPassword().isBlank())
                ? passwordEncoder.encode(dto.newPassword())
                : null;

        Long imageId = null;
        if (dto.newImageData() != null && dto.newImageData().imageBytes() != null && dto.newImageData().imageBytes().length > 0) {
            String alt = user.getUsername() + "'s profile picture";
            var image = imageService.create(
                    dto.newImageData().imageFilename(),
                    alt,
                    dto.newImageData().imageContentType(),
                    dto.newImageData().imageBytes()
            );
            imageId = image.getId();
        }

        userDao.update(user.getId(), displayName, email, encodedPassword, imageId);

        // Delete the old image to prevent orphans, if a new one was set and the user previously had one
        if (imageId != null && user.getImageId().isPresent()) {
            imageService.delete(user.getImageId().get());
        }

        return userDao.getById(user.getId()).orElseThrow();
    }

    @Override
    public boolean isUsernameTaken(String username) {
        Objects.requireNonNull(username, "username cannot be null");
        return userDao.isUsernameTaken(username.toLowerCase());
    }

    @Override
    public boolean isEmailTaken(String email) {
        Objects.requireNonNull(email, "email cannot be null");
        return userDao.isEmailTaken(email.toLowerCase());
    }

    @Override
    public boolean isEmailTakenByAnother(String email, Long excludeUserId) {
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(excludeUserId, "excludeUserId cannot be null");
        return userDao.isEmailTakenByAnother(email.toLowerCase(), excludeUserId);
    }
}
