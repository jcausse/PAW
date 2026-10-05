package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Language;
import ar.edu.itba.paw.model.Role;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.service.dto.UserEditDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getById(Long id);
    Optional<User> getByUsername(String username);
    Optional<User> getByEmail(String email);

    /* Get a user by either their username (does not contain '@') or email (contains '@') */
    Optional<User> getByUsernameOrEmail(String usernameOrEmail);

    User create(UserCreationDto dto);
    User update(UserEditDto dto);

    User updateLocation(User user, Long provinceId, String locationDetail);
    User updatePreferredLanguage(User user, Language preferredLanguage);
    User updateEmail(User user, String email);
    User markEmailAsVerified(User user);

    boolean isUsernameTaken(String username);
    boolean isEmailTaken(String email);

    void addRole(User user, Role role);
    List<Role> getRoles(User user);
}
