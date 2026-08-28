package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.service.dto.ProductCreationDto;

public interface ProductService {
    Product getById(Long id);
    Product getByName(String name);

    Product create(ProductCreationDto dto);
}
