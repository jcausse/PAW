package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.persistence.schema.ImageSchema;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class ImageJdbcDao implements ImageDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ImageJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(ImageSchema.ID)
            .withTableName(ImageSchema.TABLE_NAME);
    }

    @Override
    public Optional<Image> getById(Long id) {
        return jdbcTemplate.query(Queries.GET_BY_ID, ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Image create(String filename, String alt, String contentType, byte[] data) {
        final Map<String, Object> values = Map.ofEntries(
                Map.entry(ImageSchema.FILENAME, filename),
                Map.entry(ImageSchema.ALT, alt),
                Map.entry(ImageSchema.CONTENT_TYPE, contentType),
                Map.entry(ImageSchema.DATA, data)
        );
        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();
        return Image.builder()
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

    private static final RowMapper<Image> ROW_MAPPER = (rs, rowNum) ->
        Image.builder()
            .id(rs.getLong(ImageSchema.ID))
            .filename(rs.getString(ImageSchema.FILENAME))
            .alt(rs.getString(ImageSchema.ALT))
            .contentType(rs.getString(ImageSchema.CONTENT_TYPE))
            .data(rs.getBytes(ImageSchema.DATA))
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(
            ", ",
            ImageSchema.ID,
            ImageSchema.FILENAME,
            ImageSchema.ALT,
            ImageSchema.CONTENT_TYPE,
            ImageSchema.DATA
        );

        private static final String GET_BY_ID =
            "SELECT " + FIELDS +
            " FROM " + ImageSchema.TABLE_NAME +
            " WHERE " + ImageSchema.ID + " = ?";

        private static final String DELETE_BY_ID =
            "DELETE FROM " + ImageSchema.TABLE_NAME +
            " WHERE " + ImageSchema.ID + " = ?";
    }
}
