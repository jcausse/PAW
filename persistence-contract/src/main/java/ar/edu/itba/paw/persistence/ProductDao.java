package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Optional<Product> getById(Long id);

    List<Product> getByCategory(Long categoryId);

    List<Product> getBySubcategory(Long subcategoryId);
    List<Product> getBySubcategoryBrandModel(Long subcategoryId, String brand, String model);

    Optional<Product> getByBrandModelYearSubcategory(String brand, String model, Integer year, Long subcategoryId);

    List<String> getBrandsBySubcategory(Long subcategoryId);
    List<String> getModelsBySubcategoryAndBrand(Long subcategoryId, String brand);
    List<Integer> getYearsBySubcategoryAndBrandAndModel(Long subcategoryId, String brand, String model);

    Product create(String brand, String model, Integer year, Long subcategoryId);
}
