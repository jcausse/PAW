package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.User;

import java.time.Instant;
import java.util.Optional;

public interface UserDao {
    Optional<User> getById(Long id);
    Optional<User> getByUsername(String username);
    Optional<User> getByEmail(String email);

    User create(
            String username,
            String displayName,
            String email,
            String password,
            Image image,
            Instant joinedAt
    );

    void update(Long userId, String displayName, String email, String password, Long imageId);

    boolean isUsernameTaken(String username);
    boolean isEmailTaken(String email);
}
