package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.schema.ListingSchema;
import ar.edu.itba.paw.persistence.schema.ProductSchema;
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

    private static final RowMapper<Listing> ROW_MAPPER = (rs, rowNum) ->
        Listing.builder()
            .id(rs.getLong(ListingSchema.ID))
            .title(rs.getString(ListingSchema.TITLE))
            .price(new Price(rs.getBigDecimal(ListingSchema.PRICE)))
            .creator(
                User.builder()
                    .id(rs.getLong(UserSchema.ID))
                    .username(rs.getString(UserSchema.USERNAME))
                    .displayName(rs.getString(UserSchema.DISPLAY_NAME))
                    .email(rs.getString(UserSchema.EMAIL))
                    .build()
            )
            .product(
                Product.builder().name(rs.getString(ProductSchema.NAME)).build()
            )
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(
            ", ",
            ListingSchema.ID,
            ListingSchema.TITLE,
            ListingSchema.PRICE,
            "c." + UserSchema.USERNAME,
            "c." + UserSchema.DISPLAY_NAME,
            "c." + UserSchema.EMAIL,
            "p." + ProductSchema.NAME
        );

        private static final String GET_BY_ID =
            "SELECT " +
            FIELDS +
            " FROM " +
            ListingSchema.TABLE_NAME +
            " JOIN " +
            UserSchema.TABLE_NAME +
            " AS c ON c." +
            UserSchema.ID +
            " = " +
            ListingSchema.CREATOR_ID +
            " JOIN " +
            ProductSchema.TABLE_NAME +
            " AS p ON p." +
            ProductSchema.ID +
            " = " +
            ListingSchema.PRODUCT_ID +
            " WHERE " +
            ListingSchema.ID +
            " = ?";
    }
}
