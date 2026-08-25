package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// TODO: This is just a dummy DAO right now, as no database connection has been configured. Refactor this after
//      implementing db connection

@Repository
public class UserJdbcDao implements UserDao {

    @Override
    public Optional<User> getById(Long id) {
        return Optional.of(
                new User(id, "user-" + id, "John", "Doe", "johndoe@example.com")
        );
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public User create(String username, String firstName, String lastName, String email, String password) {
        return new User(1L, username, firstName, lastName, email);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return false;
    }

    @Override
    public boolean isEmailTaken(String email) {
        return false;
    }
}
