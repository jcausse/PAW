package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Province;
import ar.edu.itba.paw.persistence.schema.ProvinceSchema;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProvinceJdbcDao implements ProvinceDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProvinceJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public Optional<Province> getById(Long id) {
        return jdbcTemplate
            .query(Queries.GET_BY_ID, ROW_MAPPER, id)
            .stream()
            .findFirst();
    }

    @Override
    public Optional<Province> getByName(String name) {
        return jdbcTemplate
            .query(Queries.GET_BY_NAME, ROW_MAPPER, name)
            .stream()
            .findFirst();
    }

    @Override
    public List<Province> getAll() {
        return jdbcTemplate.query(Queries.GET_ALL, ROW_MAPPER);
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<Province> ROW_MAPPER = (rs, rowNum) ->
        Province.builder()
            .id(rs.getLong(ProvinceSchema.ID))
            .name(rs.getString(ProvinceSchema.NAME))
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(
            ", ",
            ProvinceSchema.ID,
            ProvinceSchema.NAME
        );

        private static final String GET_BY_ID =
            "SELECT " + FIELDS +
            " FROM " + ProvinceSchema.TABLE_NAME +
            " WHERE " + ProvinceSchema.ID + " = ?";

        private static final String GET_BY_NAME =
            "SELECT " + FIELDS +
            " FROM " + ProvinceSchema.TABLE_NAME +
            " WHERE " + ProvinceSchema.NAME + " = ?";

        private static final String GET_ALL =
            "SELECT " + FIELDS +
            " FROM " + ProvinceSchema.TABLE_NAME +
            " ORDER BY " + ProvinceSchema.ID;
    }
}
