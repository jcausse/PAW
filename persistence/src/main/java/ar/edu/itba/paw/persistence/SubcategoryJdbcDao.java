package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Subcategory;
import ar.edu.itba.paw.persistence.schema.CategorySchema;
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
public class SubcategoryJdbcDao implements SubcategoryDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public SubcategoryJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(SubcategorySchema.ID)
            .withTableName(SubcategorySchema.TABLE_NAME);
    }

    @Override
    public Optional<Subcategory> getById(Long id) {
        return jdbcTemplate
            .query(Queries.GET_BY_ID, ROW_MAPPER, id)
            .stream()
            .findFirst();
    }

    @Override
    public Optional<Subcategory> getByName(String name) {
        return jdbcTemplate
            .query(Queries.GET_BY_NAME, ROW_MAPPER, name)
            .stream()
            .findFirst();
    }

    @Override
    public List<Subcategory> getAll() {
        return jdbcTemplate.query(Queries.GET_ALL, ROW_MAPPER);
    }

    @Override
    public List<Subcategory> getByCategoryId(Long categoryId) {
        return jdbcTemplate.query(Queries.GET_BY_CATEGORY_ID, ROW_MAPPER, categoryId);
    }

    @Override
    public Subcategory create(String name, Long categoryId) {
        final Map<String, Object> values = new HashMap<>();
        values.put(SubcategorySchema.NAME, name);
        values.put(SubcategorySchema.CATEGORY_ID, categoryId);

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();
        return Subcategory.builder()
            .id(key)
            .name(name)
            .category(Category.builder().id(categoryId).build())
            .build();
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<Subcategory> ROW_MAPPER = (rs, rowNum) ->
        Subcategory.builder()
            .id(rs.getLong(SubcategorySchema.ID))
            .name(rs.getString(SubcategorySchema.NAME))
            .category(Category.builder()
                .id(rs.getLong(CategorySchema.ID))
                .name(rs.getString(CategorySchema.NAME))
                .build())
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(
            ", ",
            SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.ID,
            SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.NAME,
            CategorySchema.TABLE_NAME + "." + CategorySchema.ID,
            CategorySchema.TABLE_NAME + "." + CategorySchema.NAME
        );

        private static final String GET_BY_ID =
            "SELECT " +
            FIELDS +
            " FROM " +
            SubcategorySchema.TABLE_NAME +
            " JOIN " +
            CategorySchema.TABLE_NAME +
            " ON " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.CATEGORY_ID + " = " + CategorySchema.TABLE_NAME + "." + CategorySchema.ID +
            " WHERE " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.ID + " = ?";

        private static final String GET_BY_NAME =
            "SELECT " +
            FIELDS +
            " FROM " +
            SubcategorySchema.TABLE_NAME +
            " JOIN " +
            CategorySchema.TABLE_NAME +
            " ON " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.CATEGORY_ID + " = " + CategorySchema.TABLE_NAME + "." + CategorySchema.ID +
            " WHERE " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.NAME + " = ?";

        private static final String GET_ALL =
            "SELECT " +
            FIELDS +
            " FROM " +
            SubcategorySchema.TABLE_NAME +
            " JOIN " +
            CategorySchema.TABLE_NAME +
            " ON " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.CATEGORY_ID + " = " + CategorySchema.TABLE_NAME + "." + CategorySchema.ID;

        private static final String GET_BY_CATEGORY_ID =
            "SELECT " +
            FIELDS +
            " FROM " +
            SubcategorySchema.TABLE_NAME +
            " JOIN " +
            CategorySchema.TABLE_NAME +
            " ON " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.CATEGORY_ID + " = " + CategorySchema.TABLE_NAME + "." + CategorySchema.ID +
            " WHERE " + SubcategorySchema.TABLE_NAME + "." + SubcategorySchema.CATEGORY_ID + " = ?";
    }
}