package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Role;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.schema.UserRoleSchema;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRoleJdbcDao implements UserRoleDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRoleJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public void addRole(@NonNull User user, @NonNull Role role) {
        jdbcTemplate.update(Queries.ADD_ROLE, user.getId(), role.getRoleName());
    }

    @Override
    public List<Role> getRoles(@NonNull User user) {
        return jdbcTemplate
                .query(Queries.GET_ROLES, ROW_MAPPER, user.getId())
                .stream()
                .map(Role::fromString)
                .flatMap(Optional::stream)
                .toList();
    }

    private static final RowMapper<String> ROW_MAPPER = (rs, rowNum) ->
            rs.getString(UserRoleSchema.ROLE_NAME);

    private static final class Queries {

        private static final String ADD_ROLE =
            "INSERT INTO " + UserRoleSchema.TABLE_NAME +
            " (" + UserRoleSchema.USER_ID + ", " + UserRoleSchema.ROLE_NAME + ")" +
            " VALUES (?, ?)" +
            " ON CONFLICT (" + UserRoleSchema.USER_ID + ", " + UserRoleSchema.ROLE_NAME + ") DO NOTHING";

        private static final String GET_ROLES =
            "SELECT " + UserRoleSchema.ROLE_NAME +
            " FROM " + UserRoleSchema.TABLE_NAME +
            " WHERE " + UserRoleSchema.USER_ID + " = ?";
    }
}
