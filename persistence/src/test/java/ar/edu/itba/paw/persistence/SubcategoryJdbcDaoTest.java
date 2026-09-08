package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Subcategory;
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
public class SubcategoryJdbcDaoTest {

    private static final long SUBCATEGORY_ID = 1L;
    private static final String SUBCATEGORY_NAME = "Laptops";
    private static final long CATEGORY_ID = 1L;
    private static final long NON_EXISTING_ID = 9000L;

    @Autowired
    private SubcategoryDao subcategoryDao;

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
        final Optional<Subcategory> maybeSubcategory = subcategoryDao.getById(SUBCATEGORY_ID);

        // Assert
        Assert.assertTrue(maybeSubcategory.isPresent());
        Assert.assertEquals(SUBCATEGORY_ID, (long) maybeSubcategory.get().getId());
        Assert.assertEquals(SUBCATEGORY_NAME, maybeSubcategory.get().getName());
        Assert.assertEquals(CATEGORY_ID, (long) maybeSubcategory.get().getCategory().getId());
    }

    @Test
    public void testGetByIdDoesNotExist() {
        // Act
        final Optional<Subcategory> maybeSubcategory = subcategoryDao.getById(NON_EXISTING_ID);

        // Assert
        Assert.assertFalse(maybeSubcategory.isPresent());
    }

    @Test
    public void testGetByIdInvalidId() {
        // Act
        final Optional<Subcategory> maybeSubcategory = subcategoryDao.getById(-1L);

        // Assert
        Assert.assertFalse(maybeSubcategory.isPresent());
    }

    @Test
    public void testGetByNameExists() {
        // Act
        final Optional<Subcategory> maybeSubcategory = subcategoryDao.getByName(SUBCATEGORY_NAME);

        // Assert
        Assert.assertTrue(maybeSubcategory.isPresent());
        Assert.assertEquals(SUBCATEGORY_ID, (long) maybeSubcategory.get().getId());
    }

    @Test
    public void testGetByNameDoesNotExist() {
        // Act
        final Optional<Subcategory> maybeSubcategory = subcategoryDao.getByName("Nonexistent Subcategory");

        // Assert
        Assert.assertFalse(maybeSubcategory.isPresent());
    }

    @Test
    public void testGetAll() {
        // Act
        final List<Subcategory> subcategories = subcategoryDao.getAll();

        // Assert
        Assert.assertEquals(2, subcategories.size());
    }

    @Test
    public void testGetByCategoryIdExists() {
        // Act
        final List<Subcategory> subcategories = subcategoryDao.getByCategoryId(CATEGORY_ID);

        // Assert
        Assert.assertEquals(1, subcategories.size());
        Assert.assertEquals(SUBCATEGORY_ID, (long) subcategories.get(0).getId());
    }

    @Test
    public void testGetByCategoryIdDoesNotExist() {
        // Act
        final List<Subcategory> subcategories = subcategoryDao.getByCategoryId(NON_EXISTING_ID);

        // Assert
        Assert.assertTrue(subcategories.isEmpty());
    }

    @Test
    public void testCreate() {
        // Act
        final Subcategory created = subcategoryDao.create("Monitors", CATEGORY_ID);

        // Assert
        Assert.assertNotNull(created.getId());
        Assert.assertEquals("Monitors", created.getName());
        Assert.assertEquals(CATEGORY_ID, (long) created.getCategory().getId());

        final int count = JdbcTestUtils.countRowsInTableWhere(
            jdbcTemplate,
            "subcategories",
            "name = 'Monitors' AND category_id = " + CATEGORY_ID
        );
        Assert.assertEquals(1, count);
    }
}