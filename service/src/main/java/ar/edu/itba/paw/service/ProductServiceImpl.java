package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Subcategory;
import ar.edu.itba.paw.persistence.CategoryDao;
import ar.edu.itba.paw.persistence.ProductDao;
import ar.edu.itba.paw.persistence.SubcategoryDao;
import ar.edu.itba.paw.service.dto.ProductCreationDto;
import ar.edu.itba.paw.service.exception.NotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;
    private final CategoryDao categoryDao;
    private final SubcategoryDao subcategoryDao;

    @Override
    public Product getById(Long id) {
        return productDao
            .getById(id)
            .orElseThrow(() ->
                NotFoundException.createFor("Product with ID " + id)
            );
    }

    @Override
    public List<Product> getByCategory(Long categoryId) {
        return productDao.getByCategory(categoryId);
    }

    @Override
    public List<Product> getBySubcategory(Long subcategoryId) {
        return productDao.getBySubcategory(subcategoryId);
    }

    @Override
    public List<Product> getBySubcategoryBrandModel(Long subcategoryId, String brand, String model) {
        return productDao.getBySubcategoryBrandModel(subcategoryId, brand, model);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDao.getAll();
    }

    @Override
    public List<Subcategory> getSubcategoriesByCategory(Long categoryId) {
        return subcategoryDao.getByCategoryId(categoryId);
    }

    @Override
    public List<String> getBrandsBySubcategory(Long subcategoryId) {
        return productDao.getBrandsBySubcategory(subcategoryId);
    }

    @Override
    public List<String> getModelsBySubcategoryAndBrand(Long subcategoryId, String brand) {
        return productDao.getModelsBySubcategoryAndBrand(subcategoryId, brand);
    }

    @Override
    @Transactional
    public Product findOrCreateByBrandModelYear(String brand, String model, Integer year, Long subcategoryId) {
        Objects.requireNonNull(brand, "Brand cannot be null");
        Objects.requireNonNull(model, "Model cannot be null");
        Objects.requireNonNull(year, "Year cannot be null");
        Objects.requireNonNull(subcategoryId, "SubcategoryId cannot be null");

        return productDao.getByBrandModelYearSubcategory(brand, model, year, subcategoryId)
            .orElseGet(() -> productDao.create(brand, model, year, subcategoryId));
    }

    @Override
    @Transactional
    public Product create(ProductCreationDto dto) {
        Objects.requireNonNull(dto, "ProductCreationDto cannot be null");
        return productDao.create(dto.brand(), dto.model(), dto.year(), dto.subcategoryId());
    }
}
