package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.dto.UserCreationDto;

public interface UserService {
    /**
     * Get a {@link User} via their ID.
     * @param id User's ID.
     * @return Entity representing the user.
     * @throws ar.edu.itba.paw.service.exception.UserNotFoundException if not found.
     */
    User getById(Long id);

    /**
     * Get a {@link User} via their username (unique).
     * @param username User's username.
     * @return Entity representing the user.
     * @throws ar.edu.itba.paw.service.exception.UserNotFoundException if not found.
     */
    User getByUsername(String username);

    /**
     * Get a {@link User} via their email (unique).
     * @param email User's email.
     * @return Entity representing the user.
     * @throws ar.edu.itba.paw.service.exception.UserNotFoundException if not found.
     */
    User getByEmail(String email);

    /**
     * Create a {@link User}.
     * @param dto {@link UserCreationDto} containing values needed to register a user.
     * @return The persisted {@link User} entity.
     */
    User create(UserCreationDto dto);

    /**
     * Check whether a username is already taken.
     * @param username The username to check.
     * @return Whether a user has registered using that username.
     */
    boolean isUsernameTaken(String username);

    /**
     * Check whether an email is already taken.
     * @param email The email to check.
     * @return Whether a user has taken that email.
     */
    boolean isEmailTaken(String email);
}
