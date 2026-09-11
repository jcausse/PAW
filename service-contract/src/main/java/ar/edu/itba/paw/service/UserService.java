package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.service.dto.UserEditDto;

import java.util.Optional;

public interface UserService {
    Optional<User> getById(Long id);
    Optional<User> getByUsername(String username);
    Optional<User> getByEmail(String email);

    User create(UserCreationDto dto);
    User update(UserEditDto dto);

    boolean isUsernameTaken(String username);
    boolean isEmailTaken(String email);
    boolean isEmailTakenByAnother(String email, Long excludeUserId);
}
