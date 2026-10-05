package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.OfferRating;
import ar.edu.itba.paw.model.Language;
import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@Sql({ "classpath:sql/user-data.sql" })
@Transactional
@Rollback
public class UserJdbcDaoTest {

    // Fixtures: user 1 from initial-data.sql, users 2-3 from sql/user-data.sql
    private static final long FAKE_USER_ID = 1L;            // fake_user, unverified, no province
    private static final long VERIFIED_USER_ID = 2L;        // verified_user, image 10, CABA + "Belgrano", ratings
    private static final long PLAIN_USER_ID = 3L;           // plain_user, unverified, no image, no province
    private static final long NON_EXISTING_USER_ID = 9000L;

    private static final String FAKE_USERNAME = "fake_user";
    private static final String FAKE_EMAIL = "fake@example.com";
    private static final String VERIFIED_USERNAME = "verified_user";
    private static final String VERIFIED_EMAIL = "verified@example.com";

    private static final long AVATAR_IMAGE_ID = 10L;
    private static final int TOTAL_USERS = 3;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private int countUsersWhere(final String whereClause) {
        return JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "users", whereClause);
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* getById                                                                                         */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testGetByIdExists() {
        final Optional<User> maybeUser = userDao.getById(FAKE_USER_ID);

        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(FAKE_USER_ID, (long) maybeUser.get().getId());
        Assert.assertEquals(FAKE_USERNAME, maybeUser.get().getUsername());
        Assert.assertEquals(FAKE_EMAIL, maybeUser.get().getEmail());
    }

    @Test
    public void testGetByIdDoesNotExist() {
        final Optional<User> maybeUser = userDao.getById(NON_EXISTING_USER_ID);

        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testGetByIdInvalidId() {
        final Optional<User> maybeUser = userDao.getById(-1L);

        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testGetByIdUnverifiedUserHasNoVerifiedAt() {
        final User user = userDao.getById(FAKE_USER_ID).orElseThrow();

        Assert.assertFalse(user.isVerified());
        Assert.assertTrue(user.getEmailVerifiedAt().isEmpty());
    }

    @Test
    public void testGetByIdVerifiedUserHasVerifiedAt() {
        final User user = userDao.getById(VERIFIED_USER_ID).orElseThrow();

        Assert.assertTrue(user.isVerified());
        Assert.assertTrue(user.getEmailVerifiedAt().isPresent());
    }

    @Test
    public void testGetByIdLoadsProfileImage() {
        final User user = userDao.getById(VERIFIED_USER_ID).orElseThrow();

        Assert.assertTrue(user.getImageId().isPresent());
        Assert.assertEquals(AVATAR_IMAGE_ID, (long) user.getImageId().get());
    }

    @Test
    public void testGetByIdWithoutImageHasEmptyImageId() {
        final User user = userDao.getById(PLAIN_USER_ID).orElseThrow();

        Assert.assertTrue(user.getImageId().isEmpty());
    }

    @Test
    public void testGetByIdLoadsProvinceAndDetail() {
        final User user = userDao.getById(VERIFIED_USER_ID).orElseThrow();

        Assert.assertTrue(user.getProvince().isPresent());
        Assert.assertEquals("caba", user.getProvince().get().getName());
        Assert.assertTrue(user.getLocationDetail().isPresent());
        Assert.assertEquals("Belgrano", user.getLocationDetail().get());
    }

    @Test
    public void testGetByIdWithoutProvinceHasEmptyLocation() {
        final User user = userDao.getById(PLAIN_USER_ID).orElseThrow();

        Assert.assertTrue(user.getProvince().isEmpty());
        Assert.assertTrue(user.getLocationDetail().isEmpty());
    }

    @Test
    public void testGetByIdLoadsRatingCounters() {
        final User user = userDao.getById(VERIFIED_USER_ID).orElseThrow();

        Assert.assertEquals(3, user.getSellerPositiveRatings());
        Assert.assertEquals(1, user.getSellerNeutralRatings());
        Assert.assertEquals(0, user.getSellerNegativeRatings());
        Assert.assertEquals(2, user.getBuyerPositiveRatings());
        Assert.assertEquals(0, user.getBuyerNeutralRatings());
        Assert.assertEquals(1, user.getBuyerNegativeRatings());
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* getByUsername / getByEmail                                                                      */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testGetByUsernameExists() {
        final Optional<User> maybeUser = userDao.getByUsername(FAKE_USERNAME);

        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(FAKE_USER_ID, (long) maybeUser.get().getId());
    }

    @Test
    public void testGetByUsernameDoesNotExist() {
        final Optional<User> maybeUser = userDao.getByUsername("ghost");

        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testGetByEmailExists() {
        final Optional<User> maybeUser = userDao.getByEmail(VERIFIED_EMAIL);

        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(VERIFIED_USER_ID, (long) maybeUser.get().getId());
    }

    @Test
    public void testGetByEmailDoesNotExist() {
        final Optional<User> maybeUser = userDao.getByEmail("ghost@example.com");

        Assert.assertFalse(maybeUser.isPresent());
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* create                                                                                          */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testCreateReturnsUserWithId() {
        final User created = userDao.create(
            "new_user", "New User", "new@example.com", "secret", null, Instant.now(), Language.ENGLISH);

        Assert.assertNotNull(created.getId());
        Assert.assertEquals("new_user", created.getUsername());
        Assert.assertEquals("new@example.com", created.getEmail());
        Assert.assertFalse(created.isVerified());
    }

    @Test
    public void testCreatePersistsUser() {
        userDao.create("new_user", "New User", "new@example.com", "secret", null, Instant.now(), Language.ENGLISH);

        final int count = countUsersWhere(
            "username = 'new_user' AND email = 'new@example.com'");
        Assert.assertEquals(1, count);
    }

    @Test
    public void testCreateInitializesRatingCountersToZero() {
        final User created = userDao.create(
            "new_user", "New User", "new@example.com", "secret", null, Instant.now(), Language.ENGLISH);

        Assert.assertEquals(0, created.getSellerTotalRatings());
        Assert.assertEquals(0, created.getBuyerTotalRatings());
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* update                                                                                          */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testUpdateDisplayNameOnly() {
        final Optional<User> updated = userDao.update(FAKE_USER_ID, "Renamed", null, null, null);

        Assert.assertTrue(updated.isPresent());
        Assert.assertEquals("Renamed", updated.get().getDisplayName());
        // email untouched
        Assert.assertEquals(FAKE_EMAIL, updated.get().getEmail());
    }

    @Test
    public void testUpdatePersistsDisplayName() {
        userDao.update(FAKE_USER_ID, "Renamed", null, null, null);

        Assert.assertEquals(1, countUsersWhere(
            "user_id = " + FAKE_USER_ID + " AND display_name = 'Renamed'"));
    }

    @Test
    public void testUpdateNonExistingReturnsEmpty() {
        final Optional<User> updated = userDao.update(NON_EXISTING_USER_ID, "Nope", null, null, null);

        Assert.assertFalse(updated.isPresent());
    }

    @Test
    public void testUpdateWithNoFieldsReturnsEmpty() {
        final Optional<User> updated = userDao.update(FAKE_USER_ID, null, null, null, null);

        Assert.assertFalse(updated.isPresent());
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* verifyEmail                                                                                     */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testVerifyEmailMarksUserVerified() {
        final Instant now = Instant.now();

        final Optional<User> verified = userDao.verifyEmail(FAKE_USER_ID, now);

        Assert.assertTrue(verified.isPresent());
        Assert.assertTrue(verified.get().isVerified());
    }

    @Test
    public void testVerifyEmailPersists() {
        userDao.verifyEmail(FAKE_USER_ID, Instant.now());

        Assert.assertEquals(1, countUsersWhere(
            "user_id = " + FAKE_USER_ID + " AND email_verified_at IS NOT NULL"));
    }

    @Test
    public void testVerifyEmailNonExistingReturnsEmpty() {
        final Optional<User> verified = userDao.verifyEmail(NON_EXISTING_USER_ID, Instant.now());

        Assert.assertFalse(verified.isPresent());
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* updateLocation                                                                                  */
    /* ---------------------------------------------------------------------------------------------- */

    private Long provinceId(final String name) {
        return jdbcTemplate.queryForObject(
            "SELECT province_id FROM provinces WHERE name = ?", Long.class, name);
    }

    @Test
    public void testUpdateLocationSetsProvinceAndDetail() {
        final Long cordoba = provinceId("cordoba");

        final Optional<User> updated = userDao.updateLocation(FAKE_USER_ID, cordoba, "Nueva Cordoba");

        Assert.assertTrue(updated.isPresent());
        Assert.assertEquals("cordoba", updated.get().getProvince().orElseThrow().getName());
        Assert.assertEquals("Nueva Cordoba", updated.get().getLocationDetail().orElseThrow());
    }

    @Test
    public void testUpdateLocationPersists() {
        final Long cordoba = provinceId("cordoba");

        userDao.updateLocation(FAKE_USER_ID, cordoba, "Nueva Cordoba");

        Assert.assertEquals(1, countUsersWhere(
            "user_id = " + FAKE_USER_ID + " AND province_id = " + cordoba
            + " AND location_detail = 'Nueva Cordoba'"));
    }

    @Test
    public void testUpdateLocationWithNullClearsBothColumns() {
        // User 2 starts with CABA + "Belgrano"; clearing must null BOTH columns.
        final Optional<User> updated = userDao.updateLocation(VERIFIED_USER_ID, null, null);

        Assert.assertTrue(updated.isPresent());
        Assert.assertTrue(updated.get().getProvince().isEmpty());
        Assert.assertTrue(updated.get().getLocationDetail().isEmpty());
        Assert.assertEquals(1, countUsersWhere(
            "user_id = " + VERIFIED_USER_ID + " AND province_id IS NULL AND location_detail IS NULL"));
    }

    @Test
    public void testUpdateLocationNonExistingReturnsEmpty() {
        final Long cordoba = provinceId("cordoba");

        final Optional<User> updated = userDao.updateLocation(NON_EXISTING_USER_ID, cordoba, null);

        Assert.assertFalse(updated.isPresent());
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* isUsernameTaken / isEmailTaken                                                                  */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testIsUsernameTakenTrue() {
        Assert.assertTrue(userDao.isUsernameTaken(FAKE_USERNAME));
    }

    @Test
    public void testIsUsernameTakenFalse() {
        Assert.assertFalse(userDao.isUsernameTaken("ghost"));
    }

    @Test
    public void testIsEmailTakenTrue() {
        Assert.assertTrue(userDao.isEmailTaken(FAKE_EMAIL));
    }

    @Test
    public void testIsEmailTakenFalse() {
        Assert.assertFalse(userDao.isEmailTaken("ghost@example.com"));
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* rating counters                                                                                 */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testIncrementSellerRatingCounter() {
        userDao.incrementSellerRatingCounter(VERIFIED_USER_ID, OfferRating.POSITIVE);

        // started at 3 positive seller ratings
        Assert.assertEquals(1, countUsersWhere(
            "user_id = " + VERIFIED_USER_ID + " AND seller_positive_ratings = 4"));
    }

    @Test
    public void testIncrementBuyerRatingCounter() {
        userDao.incrementBuyerRatingCounter(VERIFIED_USER_ID, OfferRating.NEGATIVE);

        // started at 1 negative buyer rating
        Assert.assertEquals(1, countUsersWhere(
            "user_id = " + VERIFIED_USER_ID + " AND buyer_negative_ratings = 2"));
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* preferredLanguage                                                                               */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testCreatePersistsPreferredLanguage() {
        final User created = userDao.create(
            "es_user", "ES User", "es@example.com", "secret", null, Instant.now(), Language.SPANISH);

        Assert.assertEquals(Language.SPANISH, created.getPreferredLanguage());
        Assert.assertEquals(1, countUsersWhere(
            "user_id = " + created.getId() + " AND preferred_language = 'es'"));
    }

    @Test
    public void testGetByIdLoadsPreferredLanguage() {
        final User created = userDao.create(
            "es_user", "ES User", "es@example.com", "secret", null, Instant.now(), Language.SPANISH);

        final User reloaded = userDao.getById(created.getId()).orElseThrow();
        Assert.assertEquals(Language.SPANISH, reloaded.getPreferredLanguage());
    }

    @Test
    public void testUpdatePreferredLanguagePersists() {
        // fake_user (id 1) defaults to 'en'; switch to 'es'
        final Optional<User> updated = userDao.updatePreferredLanguage(FAKE_USER_ID, Language.SPANISH);

        Assert.assertTrue(updated.isPresent());
        Assert.assertEquals(Language.SPANISH, updated.get().getPreferredLanguage());
        Assert.assertEquals(1, countUsersWhere(
            "user_id = " + FAKE_USER_ID + " AND preferred_language = 'es'"));
    }

    @Test
    public void testUpdatePreferredLanguageNonExistingReturnsEmpty() {
        final Optional<User> updated = userDao.updatePreferredLanguage(NON_EXISTING_USER_ID, Language.SPANISH);

        Assert.assertFalse(updated.isPresent());
    }
}
