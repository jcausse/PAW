package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.persistence.schema.CategorySchema;
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
public class CategoryJdbcDao implements CategoryDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public CategoryJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(CategorySchema.ID)
            .withTableName(CategorySchema.TABLE_NAME);
    }

    @Override
    public Optional<Category> getById(Long id) {
        return jdbcTemplate
            .query(Queries.GET_BY_ID, ROW_MAPPER, id)
            .stream()
            .findFirst();
    }

    @Override
    public Optional<Category> getByName(String name) {
        return jdbcTemplate
            .query(Queries.GET_BY_NAME, ROW_MAPPER, name)
            .stream()
            .findFirst();
    }

    @Override
    public List<Category> getAll() {
        return jdbcTemplate.query(Queries.GET_ALL, ROW_MAPPER);
    }

    @Override
    public Category create(String name) {
        final Map<String, Object> values = new HashMap<>();
        values.put(CategorySchema.NAME, name);

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();
        return Category.builder().id(key).name(name).build();
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<Category> ROW_MAPPER = (rs, rowNum) ->
        Category.builder()
            .id(rs.getLong(CategorySchema.ID))
            .name(rs.getString(CategorySchema.NAME))
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(
            ", ",
            CategorySchema.ID,
            CategorySchema.NAME
        );

        private static final String GET_BY_ID =
            "SELECT " +
            FIELDS +
            " FROM " +
            CategorySchema.TABLE_NAME +
            " WHERE " +
            CategorySchema.ID +
            " = ?";

        private static final String GET_BY_NAME =
            "SELECT " +
            FIELDS +
            " FROM " +
            CategorySchema.TABLE_NAME +
            " WHERE " +
            CategorySchema.NAME +
            " = ?";

        private static final String GET_ALL =
            "SELECT " +
            FIELDS +
            " FROM " +
            CategorySchema.TABLE_NAME;
    }
}