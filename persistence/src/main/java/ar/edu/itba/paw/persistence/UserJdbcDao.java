package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.schema.UserSchema;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
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
public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
            .usingGeneratedKeyColumns(UserSchema.ID)
            .withTableName(UserSchema.TABLE_NAME);
    }

    @Override
    public Optional<User> getById(Long id) {
        return jdbcTemplate
            .query(Queries.GET_BY_ID, ROW_MAPPER, id)
            .stream()
            .findFirst();
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return jdbcTemplate
            .query(Queries.GET_BY_USERNAME, ROW_MAPPER, username)
            .stream()
            .findFirst();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return jdbcTemplate
            .query(Queries.GET_BY_EMAIL, ROW_MAPPER, email)
            .stream()
            .findFirst();
    }

    @Override
    public User create(User user) {
        final Long imageId = user.getImageId().orElse(null);

        final Map<String, Object> values = new HashMap<>();
        values.put(UserSchema.USERNAME, user.getUsername());
        values.put(UserSchema.DISPLAY_NAME, user.getDisplayName());
        values.put(UserSchema.EMAIL, user.getEmail());
        values.put(UserSchema.PASSWORD, user.getPassword());
        values.put(UserSchema.IMAGE_ID, imageId);
        values.put(UserSchema.JOINED_AT, Timestamp.from(user.getJoinedAt()));

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();

        return User.builder()
                .id(key)
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .password(user.getPassword())
                .imageId(imageId)
                .joinedAt(user.getJoinedAt())
                .build();
    }

    @Override
    public void update(Long userId, String displayName, String email, String password, Long imageId) {
        var setClauses = new ArrayList<String>();
        var params = new ArrayList<>();

        if (displayName != null) {
            setClauses.add(UserSchema.DISPLAY_NAME + " = ?");
            params.add(displayName);
        }
        if (email != null) {
            setClauses.add(UserSchema.EMAIL + " = ?");
            params.add(email);
        }
        if (password != null) {
            setClauses.add(UserSchema.PASSWORD + " = ?");
            params.add(password);
        }
        if (imageId != null) {
            setClauses.add(UserSchema.IMAGE_ID + " = ?");
            params.add(imageId);
        }

        if (setClauses.isEmpty()) {
            return;
        }

        var sql = "UPDATE " + UserSchema.TABLE_NAME +
            " SET " + String.join(", ", setClauses) +
            " WHERE " + UserSchema.ID + " = ?";
        params.add(userId);

        jdbcTemplate.update(sql, params.toArray());
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return jdbcTemplate.queryForObject(
                Queries.IS_USERNAME_TAKEN,
                Boolean.class,
                username
        );
    }

    @Override
    public boolean isEmailTaken(String email) {
        return jdbcTemplate.queryForObject(
                Queries.IS_EMAIL_TAKEN,
                Boolean.class,
                email
        );
    }

    @Override
    public boolean isEmailTakenByAnother(String email, Long excludeUserId) {
        return jdbcTemplate.queryForObject(
                Queries.IS_EMAIL_TAKEN_BY_ANOTHER,
                Boolean.class,
                email, excludeUserId
        );
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> User.builder()
            .id(rs.getLong(UserSchema.ID))
            .username(rs.getString(UserSchema.USERNAME))
            .displayName(rs.getString(UserSchema.DISPLAY_NAME))
            .email(rs.getString(UserSchema.EMAIL))
            .password(rs.getString(UserSchema.PASSWORD))
            .imageId(
                    Optional.ofNullable(rs.getObject(UserSchema.IMAGE_ID, Integer.class))
                            .map(Integer::longValue)
                            .orElse(null)
            )
            .joinedAt(rs.getTimestamp(UserSchema.JOINED_AT).toInstant())
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(", ",
            UserSchema.ID,
            UserSchema.USERNAME,
            UserSchema.DISPLAY_NAME,
            UserSchema.EMAIL,
            UserSchema.PASSWORD,
            UserSchema.IMAGE_ID,
            UserSchema.JOINED_AT
        );

        private static final String GET_BY_ID =
            "SELECT " + FIELDS +
            " FROM " + UserSchema.TABLE_NAME +
            " WHERE " + UserSchema.ID + " = ?";

        private static final String GET_BY_USERNAME =
            "SELECT " + FIELDS +
            " FROM " + UserSchema.TABLE_NAME +
            " WHERE " + UserSchema.USERNAME + " = ?";

        private static final String GET_BY_EMAIL =
            "SELECT " + FIELDS +
            " FROM " + UserSchema.TABLE_NAME +
            " WHERE " + UserSchema.EMAIL + " = ?";

        private static final String IS_USERNAME_TAKEN =
            "SELECT EXISTS(SELECT 1 FROM " + UserSchema.TABLE_NAME +
            " WHERE " + UserSchema.USERNAME + " = ?)";

        private static final String IS_EMAIL_TAKEN =
            "SELECT EXISTS(SELECT 1 FROM " + UserSchema.TABLE_NAME +
            " WHERE " + UserSchema.EMAIL + " = ?)";

        private static final String IS_EMAIL_TAKEN_BY_ANOTHER =
            "SELECT EXISTS(SELECT 1 FROM " + UserSchema.TABLE_NAME +
            " WHERE " + UserSchema.EMAIL + " = ? AND " + UserSchema.ID + " <> ?)";
    }
}
