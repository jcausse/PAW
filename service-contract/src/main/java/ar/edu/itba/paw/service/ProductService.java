package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.service.dto.ProductCreationDto;
import java.util.List;

public interface ProductService {
    Product getById(Long id);
    Product getByName(String name);

    List<Product> getByCategory(Long categoryId);
    List<Product> getBySubcategory(Long subcategoryId);

    Product create(ProductCreationDto dto);
}
