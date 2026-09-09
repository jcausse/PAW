package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.service.dto.UserCreationDto;
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
    public Image updateImage(User user, Image image) {
        return userDao.updateImage(user, image);
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
}
