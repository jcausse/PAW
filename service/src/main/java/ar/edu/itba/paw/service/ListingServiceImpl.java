package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Condition;
import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.ListingFilter;
import ar.edu.itba.paw.model.ListingSort;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.ListingDao;
import ar.edu.itba.paw.service.dto.ImageData;
import ar.edu.itba.paw.service.dto.ListingCreationDto;
import ar.edu.itba.paw.service.dto.ListingFilterDto;
import ar.edu.itba.paw.service.exception.BadParameterException;
import ar.edu.itba.paw.service.exception.NotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    private final UserService userService;
    private final ProductService productService;
    private final ImageService imageService;
    private final MailingService mailingService;

    @Override
    public Listing getById(Long id) {
        return listingDao
            .getById(id)
            .orElseThrow(() -> NotFoundException.createFor("Listing with ID " + id));
    }

    @Override
    public List<Listing> search(ListingFilterDto dto) {
        Objects.requireNonNull(dto, "ListingFilterDto cannot be null");

        final ListingFilter filter = ListingFilter.builder()
            .categoryId(dto.categoryId())
            .subcategoryId(dto.subcategoryId())
            .minPrice(sanitizePrice(dto.minPrice()))
            .maxPrice(sanitizePrice(dto.maxPrice()))
            .condition(parseCondition(dto.condition()))
            .acceptsTrade(Boolean.TRUE.equals(dto.acceptsTrade()) ? Boolean.TRUE : null)
            .query(dto.query())
            .sort(parseSort(dto.sort()))
            .creatorId(dto.creatorId())
            .status(parseStatus(dto.status()))
            .build();

        return listingDao.search(filter);
    }

    private static ListingStatus parseStatus(final String value) {
        return value == null || value.isBlank()
            ? null
            : ListingStatus.fromString(value).orElse(null);
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

    private static BigDecimal sanitizePrice(final BigDecimal value) {
        return value != null && value.signum() >= 0 ? value : null;
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

        if (dto.condition() == null || dto.condition().isBlank()) {
            throw BadParameterException.create("condition", "Condition is required");
        }
        final Condition condition = Condition.fromString(dto.condition())
            .orElseThrow(() -> BadParameterException.create("condition", "Invalid condition value"));

        var listing = listingDao.create(
            dto.title(),
            dto.price(),
            creator,
            product,
            condition,
            dto.acceptsTrade(),
            dto.description(),
            imageIds
        );

        mailingService.sendListingPublishedEmail(creator, listing, LocaleContextHolder.getLocale());

        return listing;
    }

    @Override
    @Transactional
    public Listing purchase(Long id, Long buyerId, String message) {
        var listing = listingDao
            .getById(id)
            .orElseThrow(() -> NotFoundException.createFor("Listing with ID " + id));

        listingDao.purchase(id, buyerId);

        final User buyer = userService.getById(buyerId)
            .orElseThrow(() -> new BadParameterException("Invalid buyerId"));
        final User seller = listing.getCreator();
        final Locale locale = LocaleContextHolder.getLocale();

        mailingService.sendPurchaseSellerEmail(seller, buyer, listing, message, locale);
        mailingService.sendPurchaseBuyerEmail(buyer, seller, listing, locale);

        return listing;
    }
}
