package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.File;
import ar.edu.itba.paw.persistence.schema.FileSchema;
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
public class FileJdbcDao implements FileDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public FileJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(FileSchema.ID)
            .withTableName(FileSchema.TABLE_NAME);
    }

    @Override
    public Optional<File> getById(Long id) {
        return jdbcTemplate.query(Queries.GET_BY_ID, ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public File create(String filename, String alt, String contentType, byte[] data) {
        final Map<String, Object> values = new HashMap<>();
        values.put(FileSchema.FILENAME, filename);
        values.put(FileSchema.ALT, alt);
        values.put(FileSchema.CONTENT_TYPE, contentType);
        values.put(FileSchema.DATA, data);
        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();
        return File.builder()
                .id(key)
                .filename(filename)
                .alt(alt)
                .contentType(contentType)
                .data(data)
                .build();
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(Queries.DELETE_BY_ID, id);
    }

    private static final RowMapper<File> ROW_MAPPER = (rs, rowNum) ->
        File.builder()
            .id(rs.getLong(FileSchema.ID))
            .filename(rs.getString(FileSchema.FILENAME))
            .alt(rs.getString(FileSchema.ALT))
            .contentType(rs.getString(FileSchema.CONTENT_TYPE))
            .data(rs.getBytes(FileSchema.DATA))
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(
            ", ",
            FileSchema.ID,
            FileSchema.FILENAME,
            FileSchema.ALT,
            FileSchema.CONTENT_TYPE,
            FileSchema.DATA
        );

        private static final String GET_BY_ID =
            "SELECT " + FIELDS +
            " FROM " + FileSchema.TABLE_NAME +
            " WHERE " + FileSchema.ID + " = ?";

        private static final String DELETE_BY_ID =
            "DELETE FROM " + FileSchema.TABLE_NAME +
            " WHERE " + FileSchema.ID + " = ?";
    }
}