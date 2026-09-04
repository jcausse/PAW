package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;

import javax.sql.DataSource;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@Transactional
@Rollback
public class UserJdbcDaoTest {

    private static final long USER_ID = 1L;
    private static final long NON_EXISTING_USER_ID = 9000L;

    private static final String USER_USERNAME = "fake_user";
    private static final String USER_DISPLAY_NAME = "Fake User";
    private static final String USER_EMAIL = "fake@example.com";
    private static final String USER_PASSWORD = "fake_password";

    @Autowired
    private UserJdbcDao userDao;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void testGetByIdExists() {
        // Act
        final Optional<User> maybeUser = userDao.getById(USER_ID);

        // Assert
        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(USER_ID, (long) maybeUser.get().getId());
        Assert.assertEquals(USER_USERNAME, maybeUser.get().getUsername());
        Assert.assertEquals(USER_EMAIL, maybeUser.get().getEmail());
    }

    @Test
    public void testGetByIdDoesNotExist() {
        // Act
        final Optional<User> maybeUser = userDao.getById(NON_EXISTING_USER_ID);

        // Assert
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testGetByIdInvalidId() {
        // Act
        final Optional<User> maybeUser = userDao.getById(-1L);

        // Assert
        Assert.assertFalse(maybeUser.isPresent());
    }
}