package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface UserDao {
    /**
     * Get a {@link User} via their ID.
     * @param id User's ID.
     * @return An {@link Optional} that is empty if no user is found.
     */
    Optional<User> getById(Long id);

    /**
     * Get a {@link User} via their username (unique).
     * @param username User's username.
     * @return An {@link Optional} that is empty if no user is found.
     */
    Optional<User> getByUsername(String username);

    /**
     * Get a {@link User} via their email (unique).
     * @param email User's email.
     * @return An {@link Optional} that is empty if no user is found.
     */
    Optional<User> getByEmail(String email);

    /**
     * Create and persist a new {@link User}.
     * @param username User's unique username.
     * @param displayName User's display name.
     * @param email User's unique email.
     * @param password User's password (or password hash).
     * @return The persisted {@link User} entity.
     */
    User create(String username, String displayName, String email, String password);

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
