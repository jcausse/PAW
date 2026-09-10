package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Condition;
import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.Offer;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Subcategory;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.schema.CategorySchema;
import ar.edu.itba.paw.persistence.schema.ListingSchema;
import ar.edu.itba.paw.persistence.schema.OfferSchema;
import ar.edu.itba.paw.persistence.schema.ProductSchema;
import ar.edu.itba.paw.persistence.schema.SubcategorySchema;
import ar.edu.itba.paw.persistence.schema.UserSchema;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class OfferJdbcDao implements OfferDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public OfferJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(OfferSchema.ID)
            .withTableName(OfferSchema.TABLE_NAME);
    }

    @Override
    public Optional<Offer> getById(Long id) {
        return jdbcTemplate
            .query(Queries.GET_BY_ID, ROW_MAPPER, id)
            .stream()
            .findFirst();
    }

    @Override
    public List<Offer> getByListingId(Long listingId) {
        return jdbcTemplate.query(Queries.GET_BY_LISTING_ID, ROW_MAPPER, listingId);
    }

    @Override
    public List<Offer> getByBuyerId(Long buyerId) {
        return jdbcTemplate.query(Queries.GET_BY_BUYER_ID, ROW_MAPPER, buyerId);
    }

    @Override
    public List<Offer> getByCreatorId(Long creatorId) {
        return jdbcTemplate.query(Queries.GET_BY_CREATOR_ID, ROW_MAPPER, creatorId);
    }

    @Override
    public Offer create(Long listingId, User buyer, BigDecimal amount, Boolean isFullPrice, OfferStatus status, String message) {
        final Map<String, Object> values = new java.util.HashMap<>();
        values.put(OfferSchema.LISTING_ID, listingId);
        values.put(OfferSchema.BUYER_ID, buyer.getId());
        values.put(OfferSchema.AMOUNT, amount);
        values.put(OfferSchema.IS_FULL_PRICE, isFullPrice);
        values.put(OfferSchema.STATUS, status.getStatus());
        values.put(OfferSchema.MESSAGE, message);

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();

        return getById(key).orElseThrow();
    }

    private static final RowMapper<Offer> ROW_MAPPER = (rs, rowNum) -> {
        User buyer = User.builder()
            .id(rs.getLong(UserSchema.ID))
            .username(rs.getString(UserSchema.USERNAME))
            .displayName(rs.getString(UserSchema.DISPLAY_NAME))
            .email(rs.getString(UserSchema.EMAIL))
            .password("<redacted>")
            .imageId(Optional.ofNullable(rs.getObject(UserSchema.IMAGE_ID, Integer.class))
                            .map(Integer::longValue)
                            .orElse(null))
            .build();

        String imageIdsStr = rs.getString("image_ids");
        List<Long> imageIds = parseImageIds(imageIdsStr);

        Listing listing = Listing.builder()
            .id(rs.getLong(ListingSchema.ID))
            .title(rs.getString(ListingSchema.TITLE))
            .price(new Price(rs.getBigDecimal(ListingSchema.PRICE)))
            .description(rs.getString(ListingSchema.DESCRIPTION))
            .status(ListingStatus.fromString(rs.getString(ListingSchema.STATUS)).orElse(ListingStatus.ACTIVE))
            .condition(Condition.fromString(rs.getString(ListingSchema.CONDITION)).orElse(Condition.GOOD))
            .acceptsTrade(rs.getBoolean(ListingSchema.ACCEPTS_TRADE))
            .creator(
                User.builder()
                    .id(rs.getLong("creator_id"))
                    .username(rs.getString("creator_username"))
                    .displayName(rs.getString("creator_display_name"))
                    .email(rs.getString("creator_email"))
                    .password("<redacted>")
                    .imageId(Optional.ofNullable(rs.getObject("creator_image_id", Integer.class))
                                    .map(Integer::longValue)
                                    .orElse(null))
                    .build()
            )
            .product(
                Product.builder()
                    .id(rs.getLong(ProductSchema.ID))
                    .brand(rs.getString(ProductSchema.BRAND))
                    .model(rs.getString(ProductSchema.MODEL))
                    .year(rs.getInt(ProductSchema.YEAR))
                    .subcategory(
                        Subcategory.builder()
                            .id(rs.getLong(SubcategorySchema.ID))
                            .name(rs.getString(SubcategorySchema.NAME))
                            .category(
                                Category.builder()
                                    .id(rs.getLong(CategorySchema.ID))
                                    .name(rs.getString("category_name"))
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .imageIds(imageIds)
            .build();

        return Offer.builder()
            .id(rs.getLong(OfferSchema.ID))
            .listing(listing)
            .buyer(buyer)
            .amount(rs.getBigDecimal(OfferSchema.AMOUNT))
            .isFullPrice(rs.getBoolean(OfferSchema.IS_FULL_PRICE))
            .status(OfferStatus.fromString(rs.getString(OfferSchema.STATUS)).orElse(OfferStatus.PENDING))
            .message(rs.getString(OfferSchema.MESSAGE))
            .build();
    };

    private static List<Long> parseImageIds(String imageIdsStr) {
        if (imageIdsStr == null || imageIdsStr.isEmpty()) {
            return List.of();
        }
        String[] parts = imageIdsStr.split(",");
        List<Long> ids = new ArrayList<>(parts.length);
        for (String part : parts) {
            ids.add(Long.parseLong(part.trim()));
        }
        return ids;
    }

    private static final class Queries {
        // here be dragons
        private static final String BASE_SELECT =
            "SELECT o." + OfferSchema.ID + ", o." + OfferSchema.LISTING_ID + ", o." + OfferSchema.BUYER_ID +
            ", o." + OfferSchema.AMOUNT + ", o." + OfferSchema.IS_FULL_PRICE + ", o." + OfferSchema.STATUS +
            ", o." + OfferSchema.MESSAGE +
            ", u." + UserSchema.ID + ", u." + UserSchema.USERNAME + ", u." + UserSchema.DISPLAY_NAME +
            ", u." + UserSchema.EMAIL + ", u." + UserSchema.IMAGE_ID +
            ", l." + ListingSchema.ID + ", l." + ListingSchema.TITLE + ", l." + ListingSchema.DESCRIPTION +
            ", l." + ListingSchema.PRICE + ", l." + ListingSchema.STATUS + ", l." + ListingSchema.CONDITION +
            ", l." + ListingSchema.ACCEPTS_TRADE + ", l." + ListingSchema.CREATOR_ID + ", l." + ListingSchema.PRODUCT_ID +
            ", c." + UserSchema.ID + " as creator_id, c." + UserSchema.USERNAME + " as creator_username" +
            ", c." + UserSchema.DISPLAY_NAME + " as creator_display_name, c." + UserSchema.EMAIL + " as creator_email" +
            ", c." + UserSchema.IMAGE_ID + " as creator_image_id" +
            ", p." + ProductSchema.ID + ", p." + ProductSchema.BRAND + ", p." + ProductSchema.MODEL +
            ", p." + ProductSchema.YEAR + ", p." + ProductSchema.SUBCATEGORY_ID +
            ", s." + SubcategorySchema.ID + ", s." + SubcategorySchema.NAME +
            ", cat." + CategorySchema.ID + ", cat." + CategorySchema.NAME + " as category_name" +
            ", COALESCE((SELECT li.image_id::text FROM listing_images li " +
            " WHERE li.listing_id = l." + ListingSchema.ID + " ORDER BY li.display_order LIMIT 1), '') as image_ids" +
            " FROM " + OfferSchema.TABLE_NAME + " o" +
            " JOIN " + UserSchema.TABLE_NAME + " u ON u." + UserSchema.ID + " = o." + OfferSchema.BUYER_ID +
            " JOIN " + ListingSchema.TABLE_NAME + " l ON l." + ListingSchema.ID + " = o." + OfferSchema.LISTING_ID +
            " JOIN " + UserSchema.TABLE_NAME + " c ON c." + UserSchema.ID + " = l." + ListingSchema.CREATOR_ID +
            " JOIN " + ProductSchema.TABLE_NAME + " p ON p." + ProductSchema.ID + " = l." + ListingSchema.PRODUCT_ID +
            " LEFT JOIN " + SubcategorySchema.TABLE_NAME + " s ON s." + SubcategorySchema.ID + " = p." + ProductSchema.SUBCATEGORY_ID +
            " LEFT JOIN " + CategorySchema.TABLE_NAME + " cat ON cat." + CategorySchema.ID + " = s." + SubcategorySchema.CATEGORY_ID;

        private static final String GET_BY_ID =
            BASE_SELECT +
            " WHERE o." + OfferSchema.ID + " = ?";

        private static final String GET_BY_LISTING_ID =
            BASE_SELECT +
            " WHERE o." + OfferSchema.LISTING_ID + " = ?" +
            " ORDER BY o." + OfferSchema.ID + " DESC";

        private static final String GET_BY_BUYER_ID =
            BASE_SELECT +
            " WHERE o." + OfferSchema.BUYER_ID + " = ?" +
            " ORDER BY o." + OfferSchema.ID + " DESC";

        private static final String GET_BY_CREATOR_ID =
            BASE_SELECT +
            " WHERE l." + ListingSchema.CREATOR_ID + " = ?" +
            " ORDER BY o." + OfferSchema.ID + " DESC";

        private static final String UPDATE_STATUS =
            "UPDATE " + OfferSchema.TABLE_NAME +
            " SET " + OfferSchema.STATUS + " = ?" +
            " WHERE " + OfferSchema.ID + " = ?";
    }

    @Override
    public boolean updateStatus(Long offerId, OfferStatus status) {
        return jdbcTemplate.update(Queries.UPDATE_STATUS, status.getStatus(), offerId) > 0;
    }
}
