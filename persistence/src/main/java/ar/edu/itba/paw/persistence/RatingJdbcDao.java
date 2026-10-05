package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Condition;
import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.OfferRating;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.model.RatingFilter;
import ar.edu.itba.paw.model.RatingRole;
import ar.edu.itba.paw.model.Subcategory;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.schema.CategorySchema;
import ar.edu.itba.paw.persistence.schema.ListingSchema;
import ar.edu.itba.paw.persistence.schema.OfferSchema;
import ar.edu.itba.paw.persistence.schema.ProductSchema;
import ar.edu.itba.paw.persistence.schema.RatingSchema;
import ar.edu.itba.paw.persistence.schema.SubcategorySchema;
import ar.edu.itba.paw.persistence.schema.UserSchema;

import java.sql.Timestamp;
import java.time.Instant;
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
public class RatingJdbcDao implements RatingDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public RatingJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(RatingSchema.ID)
            .withTableName(RatingSchema.TABLE_NAME);
    }

    @Override
    public Optional<Rating> getById(Long id) {
        return jdbcTemplate
            .query(Queries.GET_BY_ID, ROW_MAPPER, id)
            .stream()
            .findFirst();
    }

    @Override
    public Page<Rating> search(RatingFilter filter) {
        final List<String> conditions = new ArrayList<>();
        final List<Object> params = new ArrayList<>();

        if (filter.getRatedId() != null) {
            conditions.add("r." + RatingSchema.RATED_ID + " = ?");
            params.add(filter.getRatedId());
        }

        if (filter.getRoles() != null && !filter.getRoles().isEmpty()) {
            conditions.add("r." + RatingSchema.ROLE + " IN (" + String.join(", ", filter.getRoles().stream().map((s) -> "?").toArray(String[]::new)) + ")");
            params.addAll(filter.getRoles().stream().map(RatingRole::getRole).toList());
        }

        if (filter.getTypes() != null && !filter.getTypes().isEmpty()) {
            conditions.add("r." + RatingSchema.TYPE + " IN (" + String.join(", ", filter.getTypes().stream().map((s) -> "?").toArray(String[]::new)) + ")");
            params.addAll(filter.getTypes().stream().map(OfferRating::getRating).toList());
        }

        final var whereClause = conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);

        final long totalCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM " + RatingSchema.TABLE_NAME + " r" + whereClause,
            Long.class,
            params.toArray()
        );

        final int page = filter.getPage();
        final int pageSize = filter.getPageSize();
        final int offset = (page - 1) * pageSize;
        final List<Object> idParams = new ArrayList<>(params);
        idParams.add(pageSize);
        idParams.add(offset);
        final var ids = jdbcTemplate.queryForList(
            "SELECT r." + RatingSchema.ID + " FROM " + RatingSchema.TABLE_NAME + " r" + whereClause
                + " ORDER BY r." + RatingSchema.CREATED_AT + " DESC"
                + " LIMIT ? OFFSET ?",
            Long.class,
            idParams.toArray()
        );

        if (ids.isEmpty()) {
            return new Page<>(List.of(), page, pageSize, totalCount);
        }

        final var inPlaceholders = String.join(", ", ids.stream().map(id -> "?").toArray(String[]::new));
        final var sql = Queries.BASE_SELECT
            + " WHERE r." + RatingSchema.ID + " IN (" + inPlaceholders + ")"
            + " ORDER BY r." + RatingSchema.CREATED_AT + " DESC";

        final var content = jdbcTemplate.query(sql, ROW_MAPPER, ids.toArray());
        return new Page<>(content, page, pageSize, totalCount);
    }

    @Override
    public Rating create(Long creatorId, Long ratedId, Long offerId, RatingRole role, OfferRating type, String reviewText, Instant createdAt) {
        final Map<String, Object> values = new java.util.HashMap<>();
        values.put(RatingSchema.CREATOR_ID, creatorId);
        values.put(RatingSchema.RATED_ID, ratedId);
        values.put(RatingSchema.OFFER_ID, offerId);
        values.put(RatingSchema.ROLE, role.getRole());
        values.put(RatingSchema.TYPE, type.getRating());
        values.put(RatingSchema.REVIEW_TEXT, reviewText);
        values.put(RatingSchema.CREATED_AT, Timestamp.from(createdAt));

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();

        return getById(key).orElseThrow();
    }

    private static final RowMapper<Rating> ROW_MAPPER = (rs, rowNum) -> {
        User creator = User.builder()
            .id(rs.getLong("creator_id"))
            .username(rs.getString("creator_username"))
            .displayName(rs.getString("creator_display_name"))
            .email(rs.getString("creator_email"))
            .password("<redacted>")
            .imageId(
                    Optional.ofNullable(rs.getObject("creator_image_id", Integer.class))
                            .map(Integer::longValue)
                            .orElse(null))
            .joinedAt(rs.getTimestamp("creator_joined_at").toInstant())
            .build();

        User rated = User.builder()
            .id(rs.getLong("rated_id"))
            .username(rs.getString("rated_username"))
            .displayName(rs.getString("rated_display_name"))
            .email(rs.getString("rated_email"))
            .password("<redacted>")
            .imageId(
                    Optional.ofNullable(rs.getObject("rated_image_id", Integer.class))
                            .map(Integer::longValue)
                            .orElse(null))
            .joinedAt(rs.getTimestamp("rated_joined_at").toInstant())
            .build();

        Listing listing = buildListing(rs);

        return Rating.builder()
            .id(rs.getLong(RatingSchema.ID))
            .creator(creator)
            .rated(rated)
            .listing(listing)
            .role(RatingRole.fromString(rs.getString(RatingSchema.ROLE)).orElseThrow())
            .type(OfferRating.fromString(rs.getString(RatingSchema.TYPE)).orElseThrow())
            .reviewText(rs.getString(RatingSchema.REVIEW_TEXT))
            .createdAt(rs.getTimestamp(RatingSchema.CREATED_AT).toInstant())
            .build();
    };

    private static Listing buildListing(java.sql.ResultSet rs) throws java.sql.SQLException {
        Long listingId = rs.getObject("listing_id", Integer.class) != null
                ? rs.getLong("listing_id")
                : null;
        if (listingId == null) {
            return null;
        }

        Long productId = rs.getObject("product_id", Integer.class) != null
                ? rs.getLong("product_id")
                : null;
        Long subcategoryId = rs.getObject("subcategory_id", Integer.class) != null
                ? rs.getLong("subcategory_id")
                : null;
        Long categoryId = rs.getObject("category_id", Integer.class) != null
                ? rs.getLong("category_id")
                : null;
        Long creatorId = rs.getObject("listing_creator_id", Integer.class) != null
                ? rs.getLong("listing_creator_id")
                : null;
        Long creatorImageId = rs.getObject("listing_creator_image_id", Integer.class) != null
                ? rs.getLong("listing_creator_image_id")
                : null;
        java.time.Instant creatorJoinedAt = rs.getTimestamp("listing_creator_joined_at") != null
                ? rs.getTimestamp("listing_creator_joined_at").toInstant()
                : null;

        User creator = null;
        if (creatorId != null) {
            creator = User.builder()
                    .id(creatorId)
                    .username(rs.getString("listing_creator_username"))
                    .displayName(rs.getString("listing_creator_display_name"))
                    .email(rs.getString("listing_creator_email"))
                    .password("<redacted>")
                    .imageId(creatorImageId)
                    .joinedAt(creatorJoinedAt)
                    .build();
        }

        return Listing.builder()
                .id(listingId)
                .title(rs.getString("listing_title"))
                .price(new Price(rs.getBigDecimal("listing_price")))
                .description(rs.getString("listing_description"))
                .status(ListingStatus.fromString(rs.getString("listing_status")).orElse(ListingStatus.ACTIVE))
                .condition(Condition.fromString(rs.getString("listing_condition")).orElse(Condition.GOOD))
                .acceptsTrade(rs.getBoolean("listing_accepts_trade"))
                .creator(creator)
                .product(
                        Product.builder()
                                .id(productId)
                                .brand(rs.getString("product_brand"))
                                .model(rs.getString("product_model"))
                                .year(rs.getObject("product_year", Integer.class))
                                .subcategory(
                                        Subcategory.builder()
                                                .id(subcategoryId)
                                                .name(rs.getString("subcategory_name"))
                                                .category(
                                                        Category.builder()
                                                                .id(categoryId)
                                                                .name(rs.getString("category_name"))
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    private static final class Queries {
        private static final String BASE_SELECT =
            "SELECT r." + RatingSchema.ID + ", r." + RatingSchema.CREATOR_ID + ", r." + RatingSchema.RATED_ID +
            ", r." + RatingSchema.OFFER_ID + ", r." + RatingSchema.ROLE + ", r." + RatingSchema.TYPE +
            ", r." + RatingSchema.REVIEW_TEXT + ", r." + RatingSchema.CREATED_AT +
            ", c." + UserSchema.ID + " as creator_id, c." + UserSchema.USERNAME + " as creator_username" +
            ", c." + UserSchema.DISPLAY_NAME + " as creator_display_name, c." + UserSchema.EMAIL + " as creator_email" +
            ", c." + UserSchema.IMAGE_ID + " as creator_image_id, c." + UserSchema.JOINED_AT + " as creator_joined_at" +
            ", rt." + UserSchema.ID + " as rated_id, rt." + UserSchema.USERNAME + " as rated_username" +
            ", rt." + UserSchema.DISPLAY_NAME + " as rated_display_name, rt." + UserSchema.EMAIL + " as rated_email" +
            ", rt." + UserSchema.IMAGE_ID + " as rated_image_id, rt." + UserSchema.JOINED_AT + " as rated_joined_at" +
            ", l." + ListingSchema.ID + " as listing_id" +
            ", l." + ListingSchema.TITLE + " as listing_title" +
            ", l." + ListingSchema.DESCRIPTION + " as listing_description" +
            ", l." + ListingSchema.PRICE + " as listing_price" +
            ", l." + ListingSchema.STATUS + " as listing_status" +
            ", l." + ListingSchema.CONDITION + " as listing_condition" +
            ", l." + ListingSchema.ACCEPTS_TRADE + " as listing_accepts_trade" +
            ", l." + ListingSchema.PRODUCT_ID + " as product_id" +
            ", l." + ListingSchema.CREATOR_ID + " as listing_creator_id" +
            ", p." + ProductSchema.ID + " as product_id" +
            ", p." + ProductSchema.BRAND + " as product_brand" +
            ", p." + ProductSchema.MODEL + " as product_model" +
            ", p." + ProductSchema.YEAR + " as product_year" +
            ", p." + ProductSchema.SUBCATEGORY_ID + " as subcategory_id" +
            ", s." + SubcategorySchema.ID + " as subcategory_id" +
            ", s." + SubcategorySchema.NAME + " as subcategory_name" +
            ", s." + SubcategorySchema.CATEGORY_ID + " as category_id" +
            ", cat." + CategorySchema.ID + " as category_id" +
            ", cat." + CategorySchema.NAME + " as category_name" +
            ", lc." + UserSchema.ID + " as listing_creator_id" +
            ", lc." + UserSchema.USERNAME + " as listing_creator_username" +
            ", lc." + UserSchema.DISPLAY_NAME + " as listing_creator_display_name" +
            ", lc." + UserSchema.EMAIL + " as listing_creator_email" +
            ", lc." + UserSchema.IMAGE_ID + " as listing_creator_image_id" +
            ", lc." + UserSchema.JOINED_AT + " as listing_creator_joined_at" +
            " FROM " + RatingSchema.TABLE_NAME + " r" +
            " JOIN " + UserSchema.TABLE_NAME + " c ON c." + UserSchema.ID + " = r." + RatingSchema.CREATOR_ID +
            " JOIN " + UserSchema.TABLE_NAME + " rt ON rt." + UserSchema.ID + " = r." + RatingSchema.RATED_ID +
            " LEFT JOIN " + OfferSchema.TABLE_NAME + " o ON o." + OfferSchema.ID + " = r." + RatingSchema.OFFER_ID +
            " LEFT JOIN " + ListingSchema.TABLE_NAME + " l ON l." + ListingSchema.ID + " = o." + OfferSchema.LISTING_ID +
            " LEFT JOIN " + UserSchema.TABLE_NAME + " lc ON lc." + UserSchema.ID + " = l." + ListingSchema.CREATOR_ID +
            " LEFT JOIN " + ProductSchema.TABLE_NAME + " p ON p." + ProductSchema.ID + " = l." + ListingSchema.PRODUCT_ID +
            " LEFT JOIN " + SubcategorySchema.TABLE_NAME + " s ON s." + SubcategorySchema.ID + " = p." + ProductSchema.SUBCATEGORY_ID +
            " LEFT JOIN " + CategorySchema.TABLE_NAME + " cat ON cat." + CategorySchema.ID + " = s." + SubcategorySchema.CATEGORY_ID;

        private static final String GET_BY_ID =
            BASE_SELECT +
            " WHERE r." + RatingSchema.ID + " = ?";
    }
}