package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.model.User;

import java.time.Instant;
import java.util.Optional;

public interface OneTimePasswordDao {
    Optional<OneTimePassword> getByUser(User requester);

    OneTimePassword create(Long requesterId, String otpValue, Instant createdAt);

    void deleteIfPresentByUser(User requester);

    boolean existsByUser(User requester);
}
