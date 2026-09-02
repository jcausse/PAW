package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
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
    User create(
            String username,
            String displayName,
            String email,
            String password,
            Image image
    );

    void updateImage(User user, Image image);

    boolean isUsernameTaken(String username);
    boolean isEmailTaken(String email);
}
