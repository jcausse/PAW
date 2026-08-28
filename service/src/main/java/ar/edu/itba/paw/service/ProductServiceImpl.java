package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistence.ProductDao;
import ar.edu.itba.paw.service.dto.ProductCreationDto;
import ar.edu.itba.paw.service.exception.NotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// @Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;

    @Override
    public Product getById(Long id) {
        return productDao
            .getById(id)
            .orElseThrow(() ->
                NotFoundException.createFor("Product with ID " + id)
            );
    }

    @Override
    public Product getByName(String name) {
        return productDao
            .getByName(name)
            .orElseThrow(() ->
                NotFoundException.createFor("Product '" + name + "'")
            );
    }

    @Override
    // @Transactional
    public Product create(ProductCreationDto dto) {
        Objects.requireNonNull(dto, "ProductCreationDto cannot be null");
        return productDao.create(dto.name());
    }
}
