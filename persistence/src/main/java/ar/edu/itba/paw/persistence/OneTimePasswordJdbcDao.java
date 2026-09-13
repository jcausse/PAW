package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.OneTimePassword;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.schema.OneTimePasswordSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class OneTimePasswordJdbcDao implements OneTimePasswordDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public OneTimePasswordJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds).withTableName(OneTimePasswordSchema.TABLE_NAME);
    }

    @Override
    public Optional<OneTimePassword> getByUser(User requester) {
        return jdbcTemplate
                .query(Queries.GET_BY_USER, ROW_MAPPER, requester.getId())
                .stream()
                .findFirst();
    }

    @Override
    public OneTimePassword create(Long requesterId, String otpValue, Instant createdAt) {
        final Map<String, Object> values = new HashMap<>();
        values.put(OneTimePasswordSchema.REQUESTER_ID, requesterId);
        values.put(OneTimePasswordSchema.OTP_VALUE, otpValue);
        values.put(OneTimePasswordSchema.CREATED_AT, Timestamp.from(createdAt));

        jdbcInsert.execute(values);

        return OneTimePassword.builder()
                .requesterId(requesterId)
                .otpValue(otpValue)
                .createdAt(createdAt)
                .build();
    }

    @Override
    public void deleteIfPresentByUser(User requester) {
        jdbcTemplate.update(
                Queries.DELETE_BY_USER,
                requester.getId()
        );
    }

    @Override
    public boolean existsByUser(User requester) {
        return jdbcTemplate.queryForObject(
                Queries.EXISTS_BY_USER,
                Boolean.class,
                requester.getId()
        );
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<OneTimePassword> ROW_MAPPER = (rs, rowNum) -> OneTimePassword.builder()
            .requesterId(rs.getLong(OneTimePasswordSchema.REQUESTER_ID))
            .otpValue(rs.getString(OneTimePasswordSchema.OTP_VALUE))
            .createdAt(rs.getTimestamp(OneTimePasswordSchema.CREATED_AT).toInstant())
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(", ",
            OneTimePasswordSchema.REQUESTER_ID,
            OneTimePasswordSchema.OTP_VALUE,
            OneTimePasswordSchema.CREATED_AT
        );

        private static final String GET_BY_USER =
            "SELECT " + FIELDS +
            " FROM " + OneTimePasswordSchema.TABLE_NAME +
            " WHERE " + OneTimePasswordSchema.REQUESTER_ID + " = ?";

        private static final String DELETE_BY_USER =
            "DELETE FROM " + OneTimePasswordSchema.TABLE_NAME +
            " WHERE " + OneTimePasswordSchema.REQUESTER_ID + " = ?";

        private static final String EXISTS_BY_USER =
            "SELECT EXISTS(SELECT 1 FROM " + OneTimePasswordSchema.TABLE_NAME +
            " WHERE " + OneTimePasswordSchema.REQUESTER_ID + " = ?)";
    }
}
