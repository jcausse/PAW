package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.ListingDao;
import ar.edu.itba.paw.service.dto.ImageData;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.service.exception.BadParameterException;
import ar.edu.itba.paw.service.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ListingServiceImpl implements ListingService {

    private final ListingDao listingDao;

    private final UserService userService;
    private final ProductService productService;
    private final ImageService imageService;

    @Override
    public Listing getById(Long id) {
        return listingDao
            .getById(id)
            .orElseThrow(() ->
                NotFoundException.createFor("Listing with ID " + id)
            );
    }

    @Override
    @Transactional
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

        List<Long> imageIds = new ArrayList<>();
        if (dto.images() != null) {
            for (int i = 0; i < dto.images().size(); i++) {
                ImageData imageData = dto.images().get(i);
                if (imageData != null && imageData.imageBytes() != null && imageData.imageBytes().length > 0) {
                    String alt = "Image " + (i + 1) + " for listing: " + dto.title();
                    Image image = imageService.create(imageData.imageFilename(), alt, imageData.imageContentType(), imageData.imageBytes());
                    imageIds.add(image.getId());
                }
            }
        }

        return listingDao.create(dto.title(), dto.price(), creator, product, imageIds);
    }
}
