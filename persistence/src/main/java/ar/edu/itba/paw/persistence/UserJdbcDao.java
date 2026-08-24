package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserJdbcDao implements UserDao {

    @Override
    public User getById(Long id) {
        return new User(id, "User " + id);
    }
}
