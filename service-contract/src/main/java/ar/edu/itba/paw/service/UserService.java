package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.dto.UserCreationDto;

import java.util.Optional;

public interface UserService {
    Optional<User> getById(Long id);
    Optional<User> getByUsername(String username);
    Optional<User> getByEmail(String email);

    User create(UserCreationDto dto);
    // TODO: Add an option to create a new User with an image already in it

    void updateImage(User user, Image image);

    boolean isUsernameTaken(String username);
    boolean isEmailTaken(String email);
}
