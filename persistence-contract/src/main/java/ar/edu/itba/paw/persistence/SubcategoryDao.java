package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Subcategory;
import java.util.List;
import java.util.Optional;

public interface SubcategoryDao {
    Optional<Subcategory> getById(Long id);

    Optional<Subcategory> getByName(String name);

    List<Subcategory> getAll();

    List<Subcategory> getByCategoryId(Long categoryId);

    Subcategory create(String name, Long categoryId);
}