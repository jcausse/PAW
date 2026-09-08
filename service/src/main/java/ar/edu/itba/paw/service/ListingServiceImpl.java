package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Condition;
import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.ListingFilter;
import ar.edu.itba.paw.model.ListingSort;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.ListingDao;
import ar.edu.itba.paw.service.dto.ImageData;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.service.dto.ListingFilterDto;
import ar.edu.itba.paw.service.exception.BadParameterException;
import ar.edu.itba.paw.service.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ListingServiceImpl implements ListingService {

    private final ListingDao listingDao;
    private final OfferService offerService;

    private final UserService userService;
    private final ProductService productService;
    private final ImageService imageService;
    private final MailingService mailingService;

    @Override
    public Listing getById(Long id) {
        return listingDao
            .getById(id)
            .orElseThrow(() ->
                NotFoundException.createFor("Listing with ID " + id)
            );
    }

    @Override
    public List<Listing> search(ListingFilterDto dto) {
        Objects.requireNonNull(dto, "ListingFilterDto cannot be null");

        final ListingFilter filter = ListingFilter.builder()
            .categoryId(dto.categoryId())
            .subcategoryId(dto.subcategoryId())
            .minPrice(dto.minPrice())
            .maxPrice(dto.maxPrice())
            .condition(parseCondition(dto.condition()))
            .acceptsTrade(Boolean.TRUE.equals(dto.acceptsTrade()) ? Boolean.TRUE : null)
            .query(dto.query())
            .sort(parseSort(dto.sort()))
            .build();

        List<Listing> listings = listingDao.search(filter);

        // Filter out listings with pending full-price offers or accepted offers
        return listings.stream()
            .filter(listing -> !offerService.hasPendingFullPriceOrAcceptedOffer(listing.getId()))
            .toList();
    }

    private static Condition parseCondition(final String value) {
        return value == null || value.isBlank()
            ? null
            : Condition.fromString(value).orElse(null);
    }

    private static ListingSort parseSort(final String value) {
        return value == null || value.isBlank()
            ? null
            : ListingSort.fromString(value).orElse(null);
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

        var listing = listingDao.create(dto.title(), dto.price(), creator, product, imageIds);

        mailingService.sendListingPublishedEmail(creator, listing, LocaleContextHolder.getLocale());

        return listing;
    }
}
