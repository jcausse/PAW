package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getById(Long id) {
        return userDao.getById(id);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userDao.getByUsername(username.toLowerCase());
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userDao.getByEmail(email.toLowerCase());
    }

    @Override
    @Transactional
    public User create(UserCreationDto dto) {
        Objects.requireNonNull(dto, "UserCreationDto cannot be null");
        return userDao.create(
            dto.username().toLowerCase(),   // Unique
            dto.displayName(),
            dto.email().toLowerCase(),      // Unique
            passwordEncoder.encode(dto.password())
        );
    }

    @Override
    @Transactional
    public void updateImage(User user, Image image) {
        userDao.updateImage(user, image);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userDao.isUsernameTaken(username.toLowerCase());
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userDao.isEmailTaken(email.toLowerCase());
    }
}
