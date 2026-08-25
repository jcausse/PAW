package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Price;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistence.schema.ProductSchema;
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
public class ProductJdbcDao implements ProductDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ProductJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(ProductSchema.ID)
            .withTableName(ProductSchema.TABLE_NAME);
    }

    @Override
    public Optional<Product> getById(Long id) {
        return jdbcTemplate
            .query(Queries.GET_BY_ID, ROW_MAPPER, id)
            .stream()
            .findFirst();
    }

    @Override
    public Optional<Product> getByName(String name) {
        return jdbcTemplate
            .query(Queries.GET_BY_NAME, ROW_MAPPER, name)
            .stream()
            .findFirst();
    }

    @Override
    public Product create(String name) {
        final Map<String, Object> values = new HashMap<>();
        values.put(ProductSchema.NAME, name);

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();
        return Product.builder().id(key).name(name).build();
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<Product> ROW_MAPPER = (rs, rowNum) ->
        Product.builder()
            .id(rs.getLong(ProductSchema.ID))
            .name(rs.getString(ProductSchema.NAME))
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(
            ", ",
            ProductSchema.ID,
            ProductSchema.NAME
        );

        private static final String GET_BY_ID =
            "SELECT " +
            FIELDS +
            " FROM " +
            ProductSchema.TABLE_NAME +
            " WHERE " +
            ProductSchema.ID +
            " = ?";

        private static final String GET_BY_NAME =
            "SELECT " +
            FIELDS +
            " FROM " +
            ProductSchema.TABLE_NAME +
            " WHERE " +
            ProductSchema.NAME +
            " = ?";
    }
}
