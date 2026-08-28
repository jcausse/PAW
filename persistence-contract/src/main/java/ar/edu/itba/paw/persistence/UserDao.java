package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import java.util.Optional;

public interface UserDao {
    Optional<User> getById(Long id);
    Optional<User> getByUsername(String username);
    Optional<User> getByEmail(String email);

    User create(
        String username,
        String displayName,
        String email,
        String password
    );

    boolean isUsernameTaken(String username);
    boolean isEmailTaken(String email);

    /**
     * Get a {@link User} via their username and password.
     * @param username User's username.
     * @param password User's password.
     * @return An {@link Optional} that is empty if no user matches the given credentials.
     */
    Optional<User> getByCredentials(String username, String password);
}
