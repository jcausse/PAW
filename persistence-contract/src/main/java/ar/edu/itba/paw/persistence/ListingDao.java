package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Price;
import java.util.Optional;

public interface ListingDao {
    Optional<Listing> getById(Long id);

    Listing create(String name, Price price, Long creatorId);
}
