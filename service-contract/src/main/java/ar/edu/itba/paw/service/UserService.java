package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.dto.UserCreationDto;

import java.util.Optional;

public interface UserService {
    User getById(Long id);
    User getByUsername(String username);
    User getByEmail(String email);

    User create(UserCreationDto dto);

    Optional<User> login(String username, String password);

    boolean isUsernameTaken(String username);
    boolean isEmailTaken(String email);
}
