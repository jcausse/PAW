package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Optional<Product> getById(Long id);

    Optional<Product> getByName(String name);

    List<Product> getByCategory(Long categoryId);

    List<Product> getBySubcategory(Long subcategoryId);

    Product create(String name, String brand, String model, Integer year, Long subcategoryId);
}
