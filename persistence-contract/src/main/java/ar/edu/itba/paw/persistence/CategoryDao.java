package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Subcategory;
import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    Optional<Category> getById(Long id);

    Optional<Category> getByName(String name);

    List<Category> getAll();

    Category create(String name);

    List<Subcategory> getSubcategories(Long categoryId);
}
