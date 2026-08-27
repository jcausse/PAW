package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.User;
import java.util.Optional;

public interface ListingDao {
    Optional<Listing> getById(Long id);

    Listing create(String name, Price price, User creator, Product product);
}
