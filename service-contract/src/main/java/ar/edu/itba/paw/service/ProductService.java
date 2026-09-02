package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Subcategory;
import ar.edu.itba.paw.service.dto.ProductCreationDto;
import java.util.List;

public interface ProductService {
    Product getById(Long id);
    Product getByName(String name);

    List<Product> getByCategory(Long categoryId);
    List<Product> getBySubcategory(Long subcategoryId);
    List<Product> getBySubcategoryBrandModel(Long subcategoryId, String brand, String model);

    List<Category> getAllCategories();
    List<Subcategory> getSubcategoriesByCategory(Long categoryId);
    List<String> getBrandsBySubcategory(Long subcategoryId);
    List<String> getModelsBySubcategoryAndBrand(Long subcategoryId, String brand);

    Product create(ProductCreationDto dto);
}
