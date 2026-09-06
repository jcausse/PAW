package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Subcategory;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.schema.CategorySchema;
import ar.edu.itba.paw.persistence.schema.ListingSchema;
import ar.edu.itba.paw.persistence.schema.ProductSchema;
import ar.edu.itba.paw.persistence.schema.SubcategorySchema;
import ar.edu.itba.paw.persistence.schema.UserSchema;
import java.util.ArrayList;
import java.util.HashMap;
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
        return jdbcTemplate
            .query(Queries.GET_BY_ID, ROW_MAPPER, id)
            .stream()
            .findFirst();
    }

    @Override
    public Listing create(
        String title,
        Price price,
        User creator,
        Product product,
        List<Long> imageIds
    ) {
        final Map<String, Object> values = new HashMap<>();
        values.put(ListingSchema.TITLE, title);
        values.put(ListingSchema.CREATOR_ID, creator.getId());
        values.put(ListingSchema.PRODUCT_ID, product.getId());
        values.put(ListingSchema.PRICE, price.getAmount());

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
            .creator(creator)
            .product(product)
            .price(price)
            .imageIds(imageIds != null ? imageIds : List.of())
            .build();
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<Listing> ROW_MAPPER = (rs, rowNum) -> {
        return Listing.builder()
            .id(rs.getLong(ListingSchema.ID))
            .title(rs.getString(ListingSchema.TITLE))
            .price(new Price(rs.getBigDecimal(ListingSchema.PRICE)))
            .creator(
                User.builder()
                    .id(rs.getLong(UserSchema.ID))
                    .username(rs.getString(UserSchema.USERNAME))
                    .displayName(rs.getString(UserSchema.DISPLAY_NAME))
                    .email(rs.getString(UserSchema.EMAIL))
                    .password("<redacted>")
                    .imageId(Optional.ofNullable(rs.getObject(UserSchema.IMAGE_ID, Integer.class))
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
            .imageIds(parseImageIds(rs.getString("image_ids")))
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

        private static final String FIELDS = String.join(
            ", ",
            ListingSchema.ID,
            ListingSchema.TITLE,
            ListingSchema.PRICE,
            "c." + UserSchema.ID,
            "c." + UserSchema.USERNAME,
            "c." + UserSchema.DISPLAY_NAME,
            "c." + UserSchema.EMAIL,
            "c." + UserSchema.IMAGE_ID,
            "p." + ProductSchema.ID,
            "p." + ProductSchema.BRAND,
            "p." + ProductSchema.MODEL,
            "p." + ProductSchema.YEAR,
            "p." + ProductSchema.SUBCATEGORY_ID
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
            " FROM " +
            ListingSchema.TABLE_NAME + " AS l" +
            " JOIN " + UserSchema.TABLE_NAME + " AS c ON c." + UserSchema.ID + " = l." + ListingSchema.CREATOR_ID +
            " JOIN " + ProductSchema.TABLE_NAME + " AS p ON p." + ProductSchema.ID + " = l." + ListingSchema.PRODUCT_ID +
            " LEFT JOIN " + SubcategorySchema.TABLE_NAME + " ON " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.ID + " = p." + ProductSchema.SUBCATEGORY_ID +
            " LEFT JOIN " + CategorySchema.TABLE_NAME + " ON " + CategorySchema.TABLE_NAME + "." + CategorySchema.ID + " = " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.CATEGORY_ID;

        private static final String IMAGE_IDS_SUBQUERY =
            "COALESCE((SELECT STRING_AGG(li.image_id::text, ',' ORDER BY li.display_order) " +
            " FROM listing_images li WHERE li.listing_id = l." + ListingSchema.ID + "), '')";

        private static final String GET_BY_ID =
            "SELECT " + FIELDS + ", " + SUBCATEGORY_FIELDS + ", " + IMAGE_IDS_SUBQUERY + " as image_ids" +
            BASE_FROM +
            " WHERE l." + ListingSchema.ID + " = ?";
    }
}