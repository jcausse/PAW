package ar.edu.itba.paw.persistenceInterface;

import ar.edu.itba.paw.model.User;

public interface UserDao {
    User getById(Long id);
}
