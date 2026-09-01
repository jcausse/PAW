package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Subcategory;
import ar.edu.itba.paw.persistence.schema.CategorySchema;
import ar.edu.itba.paw.persistence.schema.ProductSchema;
import ar.edu.itba.paw.persistence.schema.SubcategorySchema;
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
    public List<Product> getByCategory(Long categoryId) {
        return jdbcTemplate.query(
            Queries.GET_BY_CATEGORY,
            ROW_MAPPER,
            categoryId
        );
    }

    @Override
    public List<Product> getBySubcategory(Long subcategoryId) {
        return jdbcTemplate.query(
            Queries.GET_BY_SUBCATEGORY,
            ROW_MAPPER,
            subcategoryId
        );
    }

    @Override
    public Product create(
        String name,
        String brand,
        String model,
        Integer year,
        Long subcategoryId
    ) {
        final Map<String, Object> values = new HashMap<>();
        values.put(ProductSchema.NAME, name);
        values.put(ProductSchema.BRAND, brand);
        values.put(ProductSchema.MODEL, model);
        values.put(ProductSchema.YEAR, year);
        values.put(ProductSchema.SUBCATEGORY_ID, subcategoryId);

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();
        return Product.builder()
            .id(key)
            .name(name)
            .brand(brand)
            .model(model)
            .year(year)
            .subcategory(
                subcategoryId != null
                    ? Subcategory.builder().id(subcategoryId).build()
                    : null
            )
            .build();
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<Product> ROW_MAPPER = (rs, rowNum) -> {
        Subcategory subcategory = null;
        subcategory = Subcategory.builder()
            .id(rs.getLong(SubcategorySchema.ID))
            .name(rs.getString(SubcategorySchema.NAME))
            .category(
                Category.builder()
                    .id(rs.getLong(CategorySchema.ID))
                    .name(rs.getString(CategorySchema.NAME))
                    .build()
            )
            .build();
        return Product.builder()
            .id(rs.getLong(ProductSchema.ID))
            .name(rs.getString(ProductSchema.NAME))
            .brand(rs.getString(ProductSchema.BRAND))
            .model(rs.getString(ProductSchema.MODEL))
            .year(rs.getInt(ProductSchema.YEAR))
            .subcategory(subcategory)
            .build();
    };

    private static final class Queries {

        private static final String FIELDS = String.join(
            ", ",
            ProductSchema.ID,
            ProductSchema.NAME,
            ProductSchema.BRAND,
            ProductSchema.MODEL,
            ProductSchema.YEAR,
            ProductSchema.SUBCATEGORY_ID
        );

        private static final String SUBCATEGORY_FIELDS = String.join(
            ", ",
            SubcategorySchema.ID,
            SubcategorySchema.NAME,
            SubcategorySchema.CATEGORY_ID,
            CategorySchema.ID,
            CategorySchema.NAME
        );

        private static final String BASE_FROM =
            " FROM " +
            ProductSchema.TABLE_NAME +
            " LEFT JOIN " +
            SubcategorySchema.TABLE_NAME +
            " ON " +
            ProductSchema.TABLE_NAME +
            "." +
            ProductSchema.SUBCATEGORY_ID +
            " = " +
            SubcategorySchema.TABLE_NAME +
            "." +
            SubcategorySchema.ID +
            " LEFT JOIN " +
            CategorySchema.TABLE_NAME +
            " ON " +
            SubcategorySchema.TABLE_NAME +
            "." +
            SubcategorySchema.CATEGORY_ID +
            " = " +
            CategorySchema.TABLE_NAME +
            "." +
            CategorySchema.ID;

        private static final String GET_BY_ID =
            "SELECT " +
            FIELDS +
            ", " +
            SUBCATEGORY_FIELDS +
            BASE_FROM +
            " WHERE " +
            ProductSchema.TABLE_NAME +
            "." +
            ProductSchema.ID +
            " = ?";

        private static final String GET_BY_NAME =
            "SELECT " +
            FIELDS +
            ", " +
            SUBCATEGORY_FIELDS +
            BASE_FROM +
            " WHERE " +
            ProductSchema.TABLE_NAME +
            "." +
            ProductSchema.NAME +
            " = ?";

        private static final String GET_BY_CATEGORY =
            "SELECT " +
            FIELDS +
            ", " +
            SUBCATEGORY_FIELDS +
            BASE_FROM +
            " WHERE " +
            CategorySchema.TABLE_NAME +
            "." +
            CategorySchema.ID +
            " = ?";

        private static final String GET_BY_SUBCATEGORY =
            "SELECT " +
            FIELDS +
            ", " +
            SUBCATEGORY_FIELDS +
            BASE_FROM +
            " WHERE " +
            SubcategorySchema.TABLE_NAME +
            "." +
            SubcategorySchema.ID +
            " = ?";
    }
}
