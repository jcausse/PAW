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
import java.util.HashMap;
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
        Product product
    ) {
        final Map<String, Object> values = new HashMap<>();
        values.put(ListingSchema.TITLE, title);
        values.put(ListingSchema.CREATOR_ID, creator.getId());
        values.put(ListingSchema.PRODUCT_ID, product.getId());
        values.put(ListingSchema.PRICE, price.getAmount());

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();
        return Listing.builder()
            .id(key)
            .title(title)
            .creator(creator)
            .product(product)
            .price(price)
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
                    .name(rs.getString(ProductSchema.NAME))
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
                                    .name(rs.getString(CategorySchema.NAME))
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build();
    };

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
            "p." + ProductSchema.NAME,
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
            CategorySchema.TABLE_NAME + "." + CategorySchema.NAME
        );

        private static final String BASE_FROM =
            " FROM " +
            ListingSchema.TABLE_NAME +
            " JOIN " + UserSchema.TABLE_NAME + " AS c ON c." + UserSchema.ID + " = " + ListingSchema.CREATOR_ID +
            " JOIN " + ProductSchema.TABLE_NAME + " AS p ON p." + ProductSchema.ID + " = " + ListingSchema.TABLE_NAME + "." + ListingSchema.PRODUCT_ID +
            " LEFT JOIN " + SubcategorySchema.TABLE_NAME + " ON " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.ID + " = p." + ProductSchema.SUBCATEGORY_ID +
            " LEFT JOIN " + CategorySchema.TABLE_NAME + " ON " + CategorySchema.TABLE_NAME + "." + CategorySchema.ID + " = " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.CATEGORY_ID;

        private static final String GET_BY_ID =
            "SELECT " + FIELDS + ", " + SUBCATEGORY_FIELDS +
            BASE_FROM +
            " WHERE " + ListingSchema.ID + " = ?";
    }
}
