package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
// @Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public User getById(Long id) {
        return userDao.getById(id)
                .orElseThrow(() -> UserNotFoundException.byId(id));
    }

    @Override
    public User getByUsername(String username) {
        return userDao.getByUsername(username.toLowerCase())
                .orElseThrow(() -> UserNotFoundException.byUsername(username));
    }

    @Override
    public User getByEmail(String email) {
        return userDao.getByEmail(email.toLowerCase())
                .orElseThrow(() -> UserNotFoundException.byEmail(email));
    }

    @Override
    // @Transactional
    public User create(UserCreationDto dto) {
        Objects.requireNonNull(dto, "UserCreationDto cannot be null");
        return userDao.create(
                dto.username().toLowerCase(),   // Unique
                dto.displayName(),
                dto.email().toLowerCase(),      // Unique
                dto.password()
        );
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
