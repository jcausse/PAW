package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Condition;
import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.ListingFilter;
import ar.edu.itba.paw.model.ListingSort;
import ar.edu.itba.paw.model.ListingStatus;
import ar.edu.itba.paw.model.Page;
import ar.edu.itba.paw.model.OfferStatus;
import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Province;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Subcategory;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.schema.CategorySchema;
import ar.edu.itba.paw.persistence.schema.ListingSchema;
import ar.edu.itba.paw.persistence.schema.OfferSchema;
import ar.edu.itba.paw.persistence.schema.ProductSchema;
import ar.edu.itba.paw.persistence.schema.ProvinceSchema;
import ar.edu.itba.paw.persistence.schema.SubcategorySchema;
import ar.edu.itba.paw.persistence.schema.UserSchema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class ListingJdbcDao implements ListingDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ListingJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(ListingSchema.ID)
            .withTableName(ListingSchema.TABLE_NAME);
    }

    @Override
    public Optional<Listing> getById(Long id) {
        final List<Long> imageIds = getImageIds(id);
        return jdbcTemplate
            .query(Queries.GET_BY_ID, (rs, rowNum) -> mapListing(rs, imageIds), id)
            .stream()
            .findFirst();
    }

    private List<Long> getImageIds(final Long listingId) {
        return jdbcTemplate.queryForList(Queries.GET_IMAGE_IDS_BY_LISTING_ID, Long.class, listingId);
    }

    @Override
    public Page<Listing> search(ListingFilter filter) {
        final List<String> conditions = new ArrayList<>();
        final List<Object> params = new ArrayList<>();

        if (filter.getCategoryId() != null) {
            conditions.add(CategorySchema.TABLE_NAME + "." + CategorySchema.ID + " = ?");
            params.add(filter.getCategoryId());
        }
        if (filter.getSubcategoryId() != null) {
            conditions.add(SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.ID + " = ?");
            params.add(filter.getSubcategoryId());
        }
        if (filter.getMinPrice() != null) {
            conditions.add("l." + ListingSchema.PRICE + " >= ?");
            params.add(filter.getMinPrice());
        }
        if (filter.getMaxPrice() != null) {
            conditions.add("l." + ListingSchema.PRICE + " <= ?");
            params.add(filter.getMaxPrice());
        }
        if (filter.getCondition() != null) {
            conditions.add("l." + ListingSchema.CONDITION + " = ?");
            params.add(filter.getCondition().getCondition());
        }
        if (filter.getAcceptsTrade() != null) {
            conditions.add("l." + ListingSchema.ACCEPTS_TRADE + " = ?");
            params.add(filter.getAcceptsTrade());
        }
        if (filter.getAcceptsShipping() != null) {
            conditions.add("l." + ListingSchema.ACCEPTS_SHIPPING + " = ?");
            params.add(filter.getAcceptsShipping());
        }
        if (filter.getStatus() != null) {
            conditions.add("l." + ListingSchema.STATUS + " = ?");
            params.add(filter.getStatus().getStatus());
        }
        if (filter.getCreatorId() != null) {
            conditions.add("l." + ListingSchema.CREATOR_ID + " = ?");
            params.add(filter.getCreatorId());
        }
        if (filter.getProvinceId() != null) {
            conditions.add("c." + UserSchema.PROVINCE_ID + " = ?");
            params.add(filter.getProvinceId());
        }
        if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
            conditions.add("(LOWER(" + "l." + ListingSchema.TITLE + ") LIKE ?"
                + " OR LOWER(" + "l." + ListingSchema.DESCRIPTION + ") LIKE ?)");
            final String like = "%" + filter.getQuery().toLowerCase() + "%";
            params.add(like);
            params.add(like);
        }
        if (Boolean.TRUE.equals(filter.getHasActiveOffers())) {
            conditions.add("EXISTS (SELECT 1 FROM " + OfferSchema.TABLE_NAME + " o WHERE o." + OfferSchema.LISTING_ID + " = l." + ListingSchema.ID + " AND o." + OfferSchema.STATUS + " = '" + OfferStatus.PENDING.getStatus() + "')");
        }

        final String whereClause = conditions.isEmpty()
            ? ""
            : " WHERE " + String.join(" AND ", conditions);
        final String orderBy = resolveOrderBy(filter.getSort());

        final long totalCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*)" + Queries.BASE_FROM + whereClause,
            Long.class,
            params.toArray()
        );

        final int page = filter.getPage();
        final int pageSize = filter.getPageSize();
        final int offset = (page - 1) * pageSize;

        final List<Object> idParams = new ArrayList<>(params);
        idParams.add(pageSize);
        idParams.add(offset);
        final List<Long> ids = jdbcTemplate.queryForList(
            "SELECT l." + ListingSchema.ID + Queries.BASE_FROM + whereClause
                + " ORDER BY " + orderBy + " LIMIT ? OFFSET ?",
            Long.class,
            idParams.toArray()
        );

        if (ids.isEmpty()) {
            return new Page<>(List.of(), page, pageSize, totalCount);
        }

        final String inPlaceholders = String.join(", ", ids.stream().map(id -> "?").toArray(String[]::new));
        final String sql = "SELECT " + Queries.FIELDS + ", " + Queries.SUBCATEGORY_FIELDS
            + ", " + Queries.COVER_IMAGE_ID_SUBQUERY + " as image_ids"
            + Queries.BASE_FROM
            + " WHERE l." + ListingSchema.ID + " IN (" + inPlaceholders + ")"
            + " ORDER BY " + orderBy;

        final List<Listing> content = jdbcTemplate.query(sql, ROW_MAPPER, ids.toArray());
        return new Page<>(content, page, pageSize, totalCount);
    }

    private static String resolveOrderBy(final ListingSort sort) {
        final String priceCol = "l." + ListingSchema.PRICE;
        final String titleCol = "l." + ListingSchema.TITLE;
        final String idCol = "l." + ListingSchema.ID;
        if (sort == null) {
            return idCol + " DESC";
        }
        return switch (sort) {
            case PRICE_ASC -> priceCol + " ASC, " + idCol + " DESC";
            case PRICE_DESC -> priceCol + " DESC, " + idCol + " DESC";
            case NAME_ASC -> titleCol + " ASC, " + idCol + " DESC";
            case NAME_DESC -> titleCol + " DESC, " + idCol + " DESC";
            case MOST_OFFERS -> "(SELECT COUNT(*) FROM " + OfferSchema.TABLE_NAME + " o WHERE o." + OfferSchema.LISTING_ID + " = l." + ListingSchema.ID + " AND o." + OfferSchema.STATUS + " = '" + OfferStatus.PENDING.getStatus() + "') DESC, " + idCol + " DESC";
            case RECENT_OFFERS -> "(SELECT MAX(o." + OfferSchema.CREATED_AT + ") FROM " + OfferSchema.TABLE_NAME + " o WHERE o." + OfferSchema.LISTING_ID + " = l." + ListingSchema.ID + " AND o." + OfferSchema.STATUS + " = '" + OfferStatus.PENDING.getStatus() + "') DESC NULLS LAST, " + idCol + " DESC";
            default -> idCol + " DESC";
        };
    }

    @Override
    public Listing create(
        String title,
        Price price,
        User creator,
        Product product,
        Condition condition,
        boolean acceptsTrade,
        boolean acceptsShipping,
        String description,
        List<Long> imageIds
    ) {
        final Map<String, Object> values = new HashMap<>();
        values.put(ListingSchema.TITLE, title);
        values.put(ListingSchema.DESCRIPTION, description);
        values.put(ListingSchema.CREATOR_ID, creator.getId());
        values.put(ListingSchema.PRODUCT_ID, product.getId());
        values.put(ListingSchema.PRICE, price.getAmount());
        values.put(ListingSchema.STATUS, ListingStatus.ACTIVE.getStatus());
        values.put(ListingSchema.CONDITION, condition.getCondition());
        values.put(ListingSchema.ACCEPTS_TRADE, acceptsTrade);
        values.put(ListingSchema.ACCEPTS_SHIPPING, acceptsShipping);

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();

        if (imageIds != null && !imageIds.isEmpty()) {
            final String sql = "INSERT INTO listing_images (listing_id, image_id, display_order) VALUES (?, ?, ?)";
            int order = 0;
            for (Long imageId : imageIds) {
                jdbcTemplate.update(sql, key, imageId, order++);
            }
        }

        return Listing.builder()
            .id(key)
            .title(title)
            .description(description)
            .creator(creator)
            .product(product)
            .price(price)
            .status(ListingStatus.ACTIVE)
            .condition(condition)
            .acceptsTrade(acceptsTrade)
            .acceptsShipping(acceptsShipping)
            .imageIds(imageIds != null ? imageIds : List.of())
            .pendingOffersCount(0)
            .build();
    }

    @Override
    public ListingStatus purchase(Long id, Long buyerId) {
        jdbcTemplate.update(Queries.UPDATE_STATUS_BY_ID, ListingStatus.SOLD.getStatus(), id);
        return ListingStatus.SOLD;
    }

    @Override
    public ListingStatus pendingTransaction(Long id, Long buyerId) {
        jdbcTemplate.update(Queries.UPDATE_STATUS_BY_ID, ListingStatus.PENDING_TRANSACTION.getStatus(), id);
        return ListingStatus.PENDING_TRANSACTION;
    }

    @Override
    public void cancel(Long id) {
        jdbcTemplate.update(Queries.UPDATE_STATUS_BY_ID, ListingStatus.CANCELED.getStatus(), id);
    }

    @Override
    public void updateStatus(Long id, ListingStatus status) {
        jdbcTemplate.update(Queries.UPDATE_STATUS_BY_ID, status.getStatus(), id);
    }

    @Override
    public void updateStatusBulk(List<Long> ids, ListingStatus status) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        String placeholders = String.join(",", ids.stream().map(i -> "?").toArray(String[]::new));
        String sql = "UPDATE " + ListingSchema.TABLE_NAME + " SET " + ListingSchema.STATUS + " = ? WHERE " + ListingSchema.ID + " IN (" + placeholders + ")";
        List<Object> params = new ArrayList<>();
        params.add(status.getStatus());
        params.addAll(ids);
        jdbcTemplate.update(sql, params.toArray());
    }

    @Override
    public Listing update(
        Long id,
        String title,
        Price price,
        Product product,
        Condition condition,
        boolean acceptsTrade,
        boolean acceptsShipping,
        String description
    ) {
        jdbcTemplate.update(
            Queries.UPDATE_BY_ID,
            title, description, product.getId(), price.getAmount(), condition.getCondition(), acceptsTrade, acceptsShipping, id
        );
        return getById(id).orElseThrow();
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<Listing> ROW_MAPPER = (rs, rowNum) ->
        mapListing(rs, parseImageIds(rs.getString("image_ids")));

    private static Listing mapListing(final ResultSet rs, final List<Long> imageIds) throws SQLException {
        return Listing.builder()
            .id(rs.getLong(ListingSchema.ID))
            .title(rs.getString(ListingSchema.TITLE))
            .price(new Price(rs.getBigDecimal(ListingSchema.PRICE)))
            .description(rs.getString(ListingSchema.DESCRIPTION))
            .status(ListingStatus.fromString(rs.getString(ListingSchema.STATUS)).orElse(ListingStatus.ACTIVE))
            .condition(Condition.fromString(rs.getString(ListingSchema.CONDITION)).orElse(Condition.GOOD))
            .acceptsTrade(rs.getBoolean(ListingSchema.ACCEPTS_TRADE))
            .acceptsShipping(rs.getBoolean(ListingSchema.ACCEPTS_SHIPPING))
            .creator(
                User.builder()
                    .id(rs.getLong(UserSchema.ID))
                    .username(rs.getString(UserSchema.USERNAME))
                    .displayName(rs.getString(UserSchema.DISPLAY_NAME))
                    .email(rs.getString(UserSchema.EMAIL))
                    .password("<redacted>")
                    .imageId(
                            Optional.ofNullable(rs.getObject(UserSchema.IMAGE_ID, Integer.class))
                                    .map(Integer::longValue)
                                    .orElse(null)
                    )
                    .joinedAt(rs.getTimestamp(UserSchema.JOINED_AT).toInstant())
                    .sellerPositiveRatings(rs.getInt(UserSchema.SELLER_POSITIVE_RATINGS))
                    .sellerNeutralRatings(rs.getInt(UserSchema.SELLER_NEUTRAL_RATINGS))
                    .sellerNegativeRatings(rs.getInt(UserSchema.SELLER_NEGATIVE_RATINGS))
                    .province(mapCreatorProvince(rs))
                    .locationDetail(rs.getString("creator_location_detail"))
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
            .pendingOffersCount(rs.getInt("pending_offers_count"))
            .build();
    }

    private static Province mapCreatorProvince(final java.sql.ResultSet rs) throws java.sql.SQLException {
        final Integer provinceId = rs.getObject("creator_province_id", Integer.class);
        if (provinceId == null) {
            return null;
        }
        return Province.builder()
            .id(provinceId.longValue())
            .name(rs.getString("creator_province_name"))
            .build();
    }

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

        private static final String FIELDS = String.join(
            ", ",
            ListingSchema.ID,
            ListingSchema.TITLE,
            ListingSchema.DESCRIPTION,
            ListingSchema.PRICE,
            ListingSchema.STATUS,
            ListingSchema.CONDITION,
            ListingSchema.ACCEPTS_TRADE,
            ListingSchema.ACCEPTS_SHIPPING,
            "c." + UserSchema.ID,
            "c." + UserSchema.USERNAME,
            "c." + UserSchema.DISPLAY_NAME,
            "c." + UserSchema.EMAIL,
            "c." + UserSchema.IMAGE_ID,
            "c." + UserSchema.JOINED_AT,
            "c." + UserSchema.SELLER_POSITIVE_RATINGS,
            "c." + UserSchema.SELLER_NEUTRAL_RATINGS,
            "c." + UserSchema.SELLER_NEGATIVE_RATINGS,
            "c." + UserSchema.PROVINCE_ID + " as creator_province_id",
            "c." + UserSchema.LOCATION_DETAIL + " as creator_location_detail",
            "cpr." + ProvinceSchema.NAME + " as creator_province_name",
            "p." + ProductSchema.ID,
            "p." + ProductSchema.BRAND,
            "p." + ProductSchema.MODEL,
            "p." + ProductSchema.YEAR,
            "p." + ProductSchema.SUBCATEGORY_ID,
            "(SELECT COUNT(*) FROM " + OfferSchema.TABLE_NAME + " o WHERE o." + OfferSchema.LISTING_ID + " = l." + ListingSchema.ID + " AND o." + OfferSchema.STATUS + " = '" + OfferStatus.PENDING.getStatus() + "') as pending_offers_count"
        );

        private static final String SUBCATEGORY_FIELDS = String.join(
            ", ",
            SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.ID,
            SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.NAME,
            SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.CATEGORY_ID,
            CategorySchema.TABLE_NAME + "." + CategorySchema.ID,
            CategorySchema.TABLE_NAME + "." + CategorySchema.NAME + " as category_name"
        );

        private static final String BASE_FROM =
            " FROM " + ListingSchema.TABLE_NAME + " AS l" +
            " JOIN " + UserSchema.TABLE_NAME + " AS c ON c." + UserSchema.ID + " = l." + ListingSchema.CREATOR_ID +
            " LEFT JOIN " + ProvinceSchema.TABLE_NAME + " AS cpr ON cpr." + ProvinceSchema.ID + " = c." + UserSchema.PROVINCE_ID +
            " JOIN " + ProductSchema.TABLE_NAME + " AS p ON p." + ProductSchema.ID + " = l." + ListingSchema.PRODUCT_ID +
            " LEFT JOIN " + SubcategorySchema.TABLE_NAME + " ON " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.ID + " = p." + ProductSchema.SUBCATEGORY_ID +
            " LEFT JOIN " + CategorySchema.TABLE_NAME + " ON " + CategorySchema.TABLE_NAME + "." + CategorySchema.ID + " = " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.CATEGORY_ID;

        private static final String COVER_IMAGE_ID_SUBQUERY =
            "COALESCE((SELECT CAST(li.image_id AS VARCHAR(20)) FROM listing_images li" +
            " WHERE li.listing_id = l." + ListingSchema.ID +
            " AND li.display_order = (SELECT MIN(li2.display_order) FROM listing_images li2" +
            " WHERE li2.listing_id = l." + ListingSchema.ID + ")), '')";

        private static final String GET_BY_ID =
            "SELECT " + FIELDS + ", " + SUBCATEGORY_FIELDS +
            BASE_FROM +
            " WHERE l." + ListingSchema.ID + " = ?";

        private static final String GET_IMAGE_IDS_BY_LISTING_ID =
            "SELECT image_id FROM listing_images WHERE listing_id = ? ORDER BY display_order";

        private static final String UPDATE_STATUS_BY_ID =
            "UPDATE " + ListingSchema.TABLE_NAME + " SET " + ListingSchema.STATUS + " = ? " +
            "WHERE " + ListingSchema.ID + " = ?";

        private static final String UPDATE_BY_ID =
            "UPDATE " + ListingSchema.TABLE_NAME + " SET " +
            ListingSchema.TITLE + " = ?, " +
            ListingSchema.DESCRIPTION + " = ?, " +
            ListingSchema.PRODUCT_ID + " = ?, " +
            ListingSchema.PRICE + " = ?, " +
            ListingSchema.CONDITION + " = ?, " +
            ListingSchema.ACCEPTS_TRADE + " = ?, " +
            ListingSchema.ACCEPTS_SHIPPING + " = ? " +
            "WHERE " + ListingSchema.ID + " = ?";
        
        private static final String DELETE_BY_ID =
            "DELETE FROM " + ListingSchema.TABLE_NAME + " WHERE " + ListingSchema.ID + " = ?";
    }
}
