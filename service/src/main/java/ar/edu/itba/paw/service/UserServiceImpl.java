package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.service.exception.UserNotFoundException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User getById(Long id) {
        return userDao
            .getById(id)
            .orElseThrow(() -> UserNotFoundException.byId(id));
    }

    @Override
    public User getByUsername(String username) {
        return userDao
            .getByUsername(username.toLowerCase())
            .orElseThrow(() -> UserNotFoundException.byUsername(username));
    }

    @Override
    public User getByEmail(String email) {
        return userDao
            .getByEmail(email.toLowerCase())
            .orElseThrow(() -> UserNotFoundException.byEmail(email));
    }

    @Override
    public User create(UserCreationDto dto) {
        Objects.requireNonNull(dto, "UserCreationDto cannot be null");

        var hashedPassword = passwordEncoder.encode(dto.password());
        return userDao.create(
            dto.username().toLowerCase(), // Unique
            dto.displayName(),
            dto.email().toLowerCase(), // Unique
            hashedPassword
        );
    }

    @Override
    public User login(String username, String password) {
        // TODO: merge these two queries into one
        var storedHashedPassword = userDao
            .getPasswordByUsername(username.toLowerCase())
            .orElseThrow(() -> new BadCredentialsException(""));

        var user = userDao
            .getByUsername(username.toLowerCase())
            .orElseThrow(() -> new BadCredentialsException(""));

        if (!passwordEncoder.matches(password, storedHashedPassword)) {
            throw new BadCredentialsException("");
        }

        return user;
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
