package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.schema.UserSchema;
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
    public User create(
        String username,
        String firstName,
        String lastName,
        String email,
        String password
    ) {
        final Map<String, Object> values = new HashMap<>();
        values.put(UserSchema.USERNAME, username);
        values.put(UserSchema.FIRST_NAME, firstName);
        values.put(UserSchema.LAST_NAME, lastName);
        values.put(UserSchema.EMAIL, email);
        values.put(UserSchema.PASSWORD, password);

        final Long key = jdbcInsert.executeAndReturnKey(values).longValue();

        return User.builder()
            .id(key)
            .username(username)
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .build();
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

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) ->
        User.builder()
            .id(rs.getLong(UserSchema.ID))
            .username(rs.getString(UserSchema.USERNAME))
            .firstName(rs.getString(UserSchema.FIRST_NAME))
            .lastName(rs.getString(UserSchema.LAST_NAME))
            .email(rs.getString(UserSchema.EMAIL))
            .build();

    private static final class Queries {

        private static final String FIELDS = String.join(
            ", ",
            UserSchema.ID,
            UserSchema.USERNAME,
            UserSchema.FIRST_NAME,
            UserSchema.LAST_NAME,
            UserSchema.EMAIL
        );

        private static final String GET_BY_ID =
            "SELECT " +
            FIELDS +
            " FROM " +
            UserSchema.TABLE_NAME +
            " WHERE " +
            UserSchema.ID +
            " = ?";

        private static final String GET_BY_USERNAME =
            "SELECT " +
            FIELDS +
            " FROM " +
            UserSchema.TABLE_NAME +
            " WHERE " +
            UserSchema.USERNAME +
            " = ?";

        private static final String GET_BY_EMAIL =
            "SELECT " +
            FIELDS +
            " FROM " +
            UserSchema.TABLE_NAME +
            " WHERE " +
            UserSchema.EMAIL +
            " = ?";

        private static final String IS_USERNAME_TAKEN =
            "SELECT EXISTS(SELECT 1 FROM " +
            UserSchema.TABLE_NAME +
            " WHERE " +
            UserSchema.USERNAME +
            " = ?)";

        private static final String IS_EMAIL_TAKEN =
            "SELECT EXISTS(SELECT 1 FROM " +
            UserSchema.TABLE_NAME +
            " WHERE " +
            UserSchema.EMAIL +
            " = ?)";
    }
}
