package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@Transactional
@Rollback
public class CategoryJdbcDaoTest {

    private static final long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "Electronics";
    private static final long NON_EXISTING_ID = 9000L;

    @Autowired
    private CategoryDao categoryDao;

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
        final Optional<Category> maybeCategory = categoryDao.getById(CATEGORY_ID);

        // Assert
        Assert.assertTrue(maybeCategory.isPresent());
        Assert.assertEquals(CATEGORY_ID, (long) maybeCategory.get().getId());
        Assert.assertEquals(CATEGORY_NAME, maybeCategory.get().getName());
    }

    @Test
    public void testGetByIdDoesNotExist() {
        // Act
        final Optional<Category> maybeCategory = categoryDao.getById(NON_EXISTING_ID);

        // Assert
        Assert.assertFalse(maybeCategory.isPresent());
    }

    @Test
    public void testGetByIdInvalidId() {
        // Act
        final Optional<Category> maybeCategory = categoryDao.getById(-1L);

        // Assert
        Assert.assertFalse(maybeCategory.isPresent());
    }

    @Test
    public void testGetByNameExists() {
        // Act
        final Optional<Category> maybeCategory = categoryDao.getByName(CATEGORY_NAME);

        // Assert
        Assert.assertTrue(maybeCategory.isPresent());
        Assert.assertEquals(CATEGORY_ID, (long) maybeCategory.get().getId());
    }

    @Test
    public void testGetByNameDoesNotExist() {
        // Act
        final Optional<Category> maybeCategory = categoryDao.getByName("Nonexistent Category");

        // Assert
        Assert.assertFalse(maybeCategory.isPresent());
    }

    @Test
    public void testGetAll() {
        // Act
        final List<Category> categories = categoryDao.getAll();

        // Assert
        Assert.assertEquals(2, categories.size());
    }

    @Test
    public void testCreate() {
        // Act
        final Category created = categoryDao.create("Tablets");

        // Assert
        Assert.assertNotNull(created.getId());
        Assert.assertEquals("Tablets", created.getName());

        final int count = JdbcTestUtils.countRowsInTableWhere(
            jdbcTemplate,
            "categories",
            "name = 'Tablets'"
        );
        Assert.assertEquals(1, count);
    }
}