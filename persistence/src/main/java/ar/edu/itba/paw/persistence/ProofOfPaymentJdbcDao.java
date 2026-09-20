package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.ProofOfPayment;
import ar.edu.itba.paw.persistence.schema.ProofOfPaymentSchema;
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
public class ProofOfPaymentJdbcDao implements ProofOfPaymentDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ProofOfPaymentJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(ProofOfPaymentSchema.ID)
            .withTableName(ProofOfPaymentSchema.TABLE_NAME);
    }

    @Override
    public Optional<ProofOfPayment> getById(Long id) {
        return jdbcTemplate.query(Queries.GET_BY_ID, ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public ProofOfPayment create(String filename, String alt, String contentType, byte[] data) {
        final Map<String, Object> values = new HashMap<>();
        values.put(ProofOfPaymentSchema.FILENAME, filename);
        values.put(ProofOfPaymentSchema.ALT, alt);
        values.put(ProofOfPaymentSchema.CONTENT_TYPE, contentType);
        values.put(ProofOfPaymentSchema.DATA, data);
        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();
        return ProofOfPayment.builder()
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

    private static final RowMapper<ProofOfPayment> ROW_MAPPER = (rs, rowNum) ->
        ProofOfPayment.builder()
            .id(rs.getLong(ProofOfPaymentSchema.ID))
            .filename(rs.getString(ProofOfPaymentSchema.FILENAME))
            .alt(rs.getString(ProofOfPaymentSchema.ALT))
            .contentType(rs.getString(ProofOfPaymentSchema.CONTENT_TYPE))
            .data(rs.getBytes(ProofOfPaymentSchema.DATA))
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(
            ", ",
            ProofOfPaymentSchema.ID,
            ProofOfPaymentSchema.FILENAME,
            ProofOfPaymentSchema.ALT,
            ProofOfPaymentSchema.CONTENT_TYPE,
            ProofOfPaymentSchema.DATA
        );

        private static final String GET_BY_ID =
            "SELECT " + FIELDS +
            " FROM " + ProofOfPaymentSchema.TABLE_NAME +
            " WHERE " + ProofOfPaymentSchema.ID + " = ?";

        private static final String DELETE_BY_ID =
            "DELETE FROM " + ProofOfPaymentSchema.TABLE_NAME +
            " WHERE " + ProofOfPaymentSchema.ID + " = ?";
    }
}