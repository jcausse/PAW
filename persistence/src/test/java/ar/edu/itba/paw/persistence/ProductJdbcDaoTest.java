package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@Transactional
@Rollback
public class ProductJdbcDaoTest {

    private static final long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "Electronics";

    private static final long SUBCATEGORY_ID = 1L;
    private static final String SUBCATEGORY_NAME = "Laptops";

    private static final long PRODUCT_ID = 1L;
    private static final long NON_EXISTING_ID = 9000L;
    private static final String PRODUCT_BRAND = "Apple";
    private static final String PRODUCT_MODEL = "MacBook Pro";
    private static final int PRODUCT_YEAR = 2023;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private Category buildFakeCategory() {
        return Category.builder()
            .id(CATEGORY_ID)
            .name(CATEGORY_NAME)
            .build();
    }

    private Subcategory buildFakeSubcategory() {
        return Subcategory.builder()
            .id(SUBCATEGORY_ID)
            .name(SUBCATEGORY_NAME)
            .category(buildFakeCategory())
            .build();
    }

    @Test
    public void testGetByIdExists() {
        // Act
        final Optional<Product> maybeProduct = productDao.getById(PRODUCT_ID);

        // Assert
        Assert.assertTrue(maybeProduct.isPresent());
        Assert.assertEquals(PRODUCT_ID, (long) maybeProduct.get().getId());
        Assert.assertEquals(PRODUCT_BRAND, maybeProduct.get().getBrand());
        Assert.assertEquals(PRODUCT_MODEL, maybeProduct.get().getModel());
        Assert.assertEquals(PRODUCT_YEAR, (int) maybeProduct.get().getYear());
        Assert.assertEquals(SUBCATEGORY_ID, (long) maybeProduct.get().getSubcategory().getId());
        Assert.assertEquals(CATEGORY_ID, (long) maybeProduct.get().getSubcategory().getCategory().getId());
    }

    @Test
    public void testGetByIdDoesNotExist() {
        // Act
        final Optional<Product> maybeProduct = productDao.getById(NON_EXISTING_ID);

        // Assert
        Assert.assertFalse(maybeProduct.isPresent());
    }

    @Test
    public void testGetByIdInvalidId() {
        // Act
        final Optional<Product> maybeProduct = productDao.getById(-1L);

        // Assert
        Assert.assertFalse(maybeProduct.isPresent());
    }

    @Test
    public void testGetByCategoryExists() {
        // Act
        final List<Product> products = productDao.getByCategory(CATEGORY_ID);

        // Assert
        Assert.assertEquals(1, products.size());
        Assert.assertEquals(PRODUCT_ID, (long) products.get(0).getId());
    }

    @Test
    public void testGetByCategoryDoesNotExist() {
        // Act
        final List<Product> products = productDao.getByCategory(NON_EXISTING_ID);

        // Assert
        Assert.assertTrue(products.isEmpty());
    }

    @Test
    public void testGetBySubcategoryExists() {
        // Act
        final List<Product> products = productDao.getBySubcategory(SUBCATEGORY_ID);

        // Assert
        Assert.assertEquals(1, products.size());
        Assert.assertEquals(PRODUCT_ID, (long) products.get(0).getId());
    }

    @Test
    public void testGetBySubcategoryDoesNotExist() {
        // Act
        final List<Product> products = productDao.getBySubcategory(NON_EXISTING_ID);

        // Assert
        Assert.assertTrue(products.isEmpty());
    }

    @Test
    public void testGetBySubcategoryBrandModelMatches() {
        // Act
        final List<Product> products = productDao.getBySubcategoryBrandModel(SUBCATEGORY_ID, PRODUCT_BRAND, PRODUCT_MODEL);

        // Assert
        Assert.assertEquals(1, products.size());
        Assert.assertEquals(PRODUCT_ID, (long) products.get(0).getId());
    }

    @Test
    public void testGetBySubcategoryBrandModelEmptyFiltersMatchesAll() {
        // Act
        final List<Product> products = productDao.getBySubcategoryBrandModel(SUBCATEGORY_ID, "", "");

        // Assert
        Assert.assertEquals(1, products.size());
    }

    @Test
    public void testGetBySubcategoryBrandModelNoMatch() {
        // Act
        final List<Product> products = productDao.getBySubcategoryBrandModel(SUBCATEGORY_ID, "Samsung", PRODUCT_MODEL);

        // Assert
        Assert.assertTrue(products.isEmpty());
    }

    @Test
    public void testGetByBrandModelYearSubcategoryExists() {
        // Act
        final Optional<Product> maybeProduct = productDao.getByBrandModelYearSubcategory(
            PRODUCT_BRAND, PRODUCT_MODEL, PRODUCT_YEAR, SUBCATEGORY_ID
        );

        // Assert
        Assert.assertTrue(maybeProduct.isPresent());
        Assert.assertEquals(PRODUCT_ID, (long) maybeProduct.get().getId());
    }

    @Test
    public void testGetByBrandModelYearSubcategoryDoesNotExist() {
        // Act
        final Optional<Product> maybeProduct = productDao.getByBrandModelYearSubcategory(
            "Samsung", "Galaxy Book", 2023, SUBCATEGORY_ID
        );

        // Assert
        Assert.assertFalse(maybeProduct.isPresent());
    }

    @Test
    public void testGetBrandsBySubcategory() {
        // Act
        final List<String> brands = productDao.getBrandsBySubcategory(SUBCATEGORY_ID);

        // Assert
        Assert.assertEquals(1, brands.size());
        Assert.assertEquals(PRODUCT_BRAND, brands.get(0));
    }

    @Test
    public void testGetModelsBySubcategoryAndBrand() {
        // Act
        final List<String> models = productDao.getModelsBySubcategoryAndBrand(SUBCATEGORY_ID, PRODUCT_BRAND);

        // Assert
        Assert.assertEquals(1, models.size());
        Assert.assertEquals(PRODUCT_MODEL, models.get(0));
    }

    @Test
    public void testGetYearsBySubcategoryAndBrandAndModel() {
        // Act
        final List<Integer> years = productDao.getYearsBySubcategoryAndBrandAndModel(
            SUBCATEGORY_ID, PRODUCT_BRAND, PRODUCT_MODEL
        );

        // Assert
        Assert.assertEquals(1, years.size());
        Assert.assertEquals(PRODUCT_YEAR, (int) years.get(0));
    }

    @Test
    public void testCreate() {
        // Arrange
        final Subcategory subcategory = buildFakeSubcategory();

        // Act
        final Product created = productDao.create("Dell", "XPS 13", 2024, subcategory);

        // Assert
        Assert.assertNotNull(created.getId());
        Assert.assertEquals("Dell", created.getBrand());
        Assert.assertEquals("XPS 13", created.getModel());
        Assert.assertEquals(2024, (int) created.getYear());

        final int count = JdbcTestUtils.countRowsInTableWhere(
            jdbcTemplate,
            "products",
            "brand = 'Dell' AND model = 'XPS 13' AND year = 2024"
        );
        Assert.assertEquals(1, count);
    }
}