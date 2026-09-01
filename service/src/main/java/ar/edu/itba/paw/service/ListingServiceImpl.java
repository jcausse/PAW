package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.ListingDao;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.service.exception.BadParameterException;
import ar.edu.itba.paw.service.exception.NotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// @Transactional(readOnly = true)
public class ListingServiceImpl implements ListingService {

    private final ListingDao listingDao;

    private final UserService userService;
    private final ProductService productService;

    @Override
    public Listing getById(Long id) {
        return listingDao
            .getById(id)
            .orElseThrow(() ->
                NotFoundException.createFor("Listing with ID " + id)
            );
    }

    @Override
    // @Transactional
    public Listing create(ListingCreationDto dto) {
        Objects.requireNonNull(dto, "ListingCreationDto cannot be null");

        User creator = userService.getById(dto.creatorId())
                .orElseThrow(() -> new BadParameterException("Invalid creatorId"));

        Product product;
        try {
            product = productService.getById(dto.productId());
        } catch (NotFoundException e) {
            throw BadParameterException.create("productId", e.getMessage());
        }

        return listingDao.create(dto.title(), dto.price(), creator, product);
    }
}
