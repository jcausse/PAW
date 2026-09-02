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
    public List<Product> getBySubcategoryAndFilters(Long subcategoryId, String brand, String model, Integer year) {
        return jdbcTemplate.query(
            Queries.GET_BY_SUBCATEGORY_AND_FILTERS,
            ROW_MAPPER,
            subcategoryId,
            brand,
            model,
            year
        );
    }

    @Override
    public List<String> getBrandsBySubcategory(Long subcategoryId) {
        return jdbcTemplate.queryForList(Queries.GET_BRANDS_BY_SUBCATEGORY, String.class, subcategoryId);
    }

    @Override
    public List<String> getModelsBySubcategoryAndBrand(Long subcategoryId, String brand) {
        return jdbcTemplate.queryForList(Queries.GET_MODELS_BY_SUBCATEGORY_AND_BRAND, String.class, subcategoryId, brand);
    }

    @Override
    public List<Integer> getYearsBySubcategoryAndBrandAndModel(Long subcategoryId, String brand, String model) {
        return jdbcTemplate.queryForList(Queries.GET_YEARS_BY_SUBCATEGORY_AND_BRAND_AND_MODEL, Integer.class, subcategoryId, brand, model);
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
            ProductSchema.TABLE_NAME + "." + ProductSchema.ID,
            ProductSchema.TABLE_NAME + "." + ProductSchema.NAME,
            ProductSchema.TABLE_NAME + "." + ProductSchema.BRAND,
            ProductSchema.TABLE_NAME + "." + ProductSchema.MODEL,
            ProductSchema.TABLE_NAME + "." + ProductSchema.YEAR,
            ProductSchema.TABLE_NAME + "." + ProductSchema.SUBCATEGORY_ID
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

        private static final String GET_BY_SUBCATEGORY_AND_FILTERS =
            "SELECT " +
            FIELDS +
            ", " +
            SUBCATEGORY_FIELDS +
            BASE_FROM +
            " WHERE " +
            SubcategorySchema.TABLE_NAME +
            "." +
            SubcategorySchema.ID +
            " = ?" +
            " AND " + ProductSchema.TABLE_NAME + "." + ProductSchema.BRAND + " = ?" +
            " AND " + ProductSchema.TABLE_NAME + "." + ProductSchema.MODEL + " = ?" +
            " AND " + ProductSchema.TABLE_NAME + "." + ProductSchema.YEAR + " = ?";

        private static final String GET_BRANDS_BY_SUBCATEGORY =
            "SELECT DISTINCT " + ProductSchema.BRAND +
            " FROM " + ProductSchema.TABLE_NAME +
            " WHERE " + ProductSchema.SUBCATEGORY_ID + " = ?" +
            " ORDER BY " + ProductSchema.BRAND;

        private static final String GET_MODELS_BY_SUBCATEGORY_AND_BRAND =
            "SELECT DISTINCT " + ProductSchema.MODEL +
            " FROM " + ProductSchema.TABLE_NAME +
            " WHERE " + ProductSchema.SUBCATEGORY_ID + " = ?" +
            " AND " + ProductSchema.BRAND + " = ?" +
            " ORDER BY " + ProductSchema.MODEL;

        private static final String GET_YEARS_BY_SUBCATEGORY_AND_BRAND_AND_MODEL =
            "SELECT DISTINCT " + ProductSchema.YEAR +
            " FROM " + ProductSchema.TABLE_NAME +
            " WHERE " + ProductSchema.SUBCATEGORY_ID + " = ?" +
            " AND " + ProductSchema.BRAND + " = ?" +
            " AND " + ProductSchema.MODEL + " = ?" +
            " ORDER BY " + ProductSchema.YEAR + " DESC";
    }
}
