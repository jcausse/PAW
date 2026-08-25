package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("user_id")
                .withTableName("users");
    }

    @Override
    public Optional<User> getById(Long id) {
        return jdbcTemplate.query(Queries.GET_BY_ID, ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return jdbcTemplate.query(Queries.GET_BY_USERNAME, ROW_MAPPER, username).stream().findFirst();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return jdbcTemplate.query(Queries.GET_BY_EMAIL, ROW_MAPPER, email).stream().findFirst();
    }

    @Override
    public User create(String username, String firstName, String lastName, String email, String password) {
        final Map<String, Object> values = new HashMap<>();
        values.put("username", username);
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("email", email);
        values.put("password", password);

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
        return jdbcTemplate.queryForObject(Queries.IS_USERNAME_TAKEN, Boolean.class, username);
    }

    @Override
    public boolean isEmailTaken(String email) {
        return jdbcTemplate.queryForObject(Queries.IS_EMAIL_TAKEN, Boolean.class, email);
    }

    /* ---------------------------------------------------------------------------------------------- */

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> User.builder()
            .id(rs.getLong("user_id"))
            .username(rs.getString("username"))
            .firstName(rs.getString("first_name"))
            .lastName(rs.getString("last_name"))
            .email(rs.getString("email"))
            .build();

    private static final class Queries {
        private static final String FIELDS = "user_id, username, first_name, last_name, email";

        private static final String GET_BY_ID =
                "SELECT " + FIELDS + " FROM users WHERE user_id = ?";

        private static final String GET_BY_USERNAME =
                "SELECT " + FIELDS + " FROM users WHERE username = ?";

        private static final String GET_BY_EMAIL =
                "SELECT " + FIELDS + " FROM users WHERE email = ?";

        private static final String IS_USERNAME_TAKEN =
                "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)";

        private static final String IS_EMAIL_TAKEN =
                "SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)";

    }
}
