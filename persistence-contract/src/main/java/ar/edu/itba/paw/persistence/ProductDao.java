package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Product;
import java.util.Optional;

public interface ProductDao {
    Optional<Product> getById(Long id);

    Optional<Product> getByName(String name);

    Product create(String name);
}
