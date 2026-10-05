package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.Province;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.Language;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.persistence.UserRoleDao;
import ar.edu.itba.paw.service.dto.ImageData;
import ar.edu.itba.paw.service.dto.UserCreationDto;
import ar.edu.itba.paw.service.dto.UserEditDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final long USER_ID = 1L;
    private static final String USER_USERNAME = "fake_user";
    private static final String USER_DISPLAY_NAME = "Fake User";
    private static final String USER_EMAIL = "fake@example.com";
    private static final String USER_PASSWORD = "fake_password";
    private static final String ENCODED_PASSWORD = "encoded-password";

    private static final long PROVINCE_ID = 1L;
    private static final long NON_EXISTING_PROVINCE_ID = 9000L;
    private static final long IMAGE_ID = 50L;
    private static final long OLD_IMAGE_ID = 40L;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDao userDao;
    @Mock
    private UserRoleDao userRoleDao;
    @Mock
    private ImageService imageService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ProvinceService provinceService;
    @Mock
    private MailingService mailingService;


    private User buildFakeUser() {
        return User.builder()
            .id(USER_ID)
            .username(USER_USERNAME)
            .displayName(USER_DISPLAY_NAME)
            .email(USER_EMAIL)
            .password(USER_PASSWORD)
            .joinedAt(Instant.now())
            .build();
    }

    private User buildUserWithImage(final Long imageId) {
        return User.builder()
            .id(USER_ID)
            .username(USER_USERNAME)
            .displayName(USER_DISPLAY_NAME)
            .email(USER_EMAIL)
            .password(USER_PASSWORD)
            .imageId(imageId)
            .joinedAt(Instant.now())
            .build();
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* getById / getByUsername / getByEmail                                                            */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testGetByIdUserExists() {
        when(userDao.getById(eq(USER_ID))).thenReturn(Optional.of(buildFakeUser()));

        final Optional<User> maybeUser = userService.getById(USER_ID);

        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(USER_ID, (long) maybeUser.get().getId());
        Assert.assertEquals(USER_USERNAME, maybeUser.get().getUsername());
    }

    @Test
    public void testGetByIdUserDoesNotExist() {
        when(userDao.getById(eq(USER_ID))).thenReturn(Optional.empty());

        Assert.assertFalse(userService.getById(USER_ID).isPresent());
    }

    @Test
    public void testGetByUsernameUserExists() {
        when(userDao.getByUsername(eq(USER_USERNAME))).thenReturn(Optional.of(buildFakeUser()));

        Assert.assertTrue(userService.getByUsername(USER_USERNAME).isPresent());
    }

    @Test
    public void testGetByUsernameNormalizesInput() {
        when(userDao.getByUsername(eq(USER_USERNAME))).thenReturn(Optional.of(buildFakeUser()));

        // Mixed-case + spaces must be normalized before hitting the DAO
        userService.getByUsername("  Fake_User  ");

        verify(userDao).getByUsername(USER_USERNAME);
    }

    @Test
    public void testGetByEmailUserExists() {
        when(userDao.getByEmail(eq(USER_EMAIL))).thenReturn(Optional.of(buildFakeUser()));

        Assert.assertTrue(userService.getByEmail(USER_EMAIL).isPresent());
    }

    @Test
    public void testGetByUsernameOrEmailPicksEmailWhenHasAt() {
        when(userDao.getByEmail(eq(USER_EMAIL))).thenReturn(Optional.of(buildFakeUser()));

        userService.getByUsernameOrEmail(USER_EMAIL);

        verify(userDao).getByEmail(USER_EMAIL);
        verify(userDao, never()).getByUsername(any());
    }

    @Test
    public void testGetByUsernameOrEmailPicksUsernameWhenNoAt() {
        when(userDao.getByUsername(eq(USER_USERNAME))).thenReturn(Optional.of(buildFakeUser()));

        userService.getByUsernameOrEmail(USER_USERNAME);

        verify(userDao).getByUsername(USER_USERNAME);
        verify(userDao, never()).getByEmail(any());
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* isUsernameTaken / isEmailTaken                                                                  */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testIsUsernameTakenTrue() {
        when(userDao.isUsernameTaken(eq(USER_USERNAME))).thenReturn(true);

        Assert.assertTrue(userService.isUsernameTaken(USER_USERNAME));
    }

    @Test
    public void testIsUsernameTakenNormalizesInput() {
        when(userDao.isUsernameTaken(eq(USER_USERNAME))).thenReturn(false);

        userService.isUsernameTaken("  Fake_User  ");

        verify(userDao).isUsernameTaken(USER_USERNAME);
    }

    @Test
    public void testIsEmailTakenTrue() {
        when(userDao.isEmailTaken(eq(USER_EMAIL))).thenReturn(true);

        Assert.assertTrue(userService.isEmailTaken(USER_EMAIL));
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* create                                                                                          */
    /* ---------------------------------------------------------------------------------------------- */

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRejectsUsernameWithAt() {
        userService.create(new UserCreationDto("user@bad", USER_DISPLAY_NAME, USER_EMAIL, USER_PASSWORD, null));
    }

    @Test
    public void testCreateNormalizesAndEncodes() {
        // Arrange
        when(passwordEncoder.encode(eq(USER_PASSWORD))).thenReturn(ENCODED_PASSWORD);
        when(userDao.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(buildFakeUser());

        // Act: mixed case / spaces must be normalized; password encoded
        userService.create(new UserCreationDto("  Fake_User ", "  Fake User  ", "  FAKE@Example.com ", USER_PASSWORD, null));

        // Assert
        verify(userDao).create(
            eq(USER_USERNAME),              // trimmed + lowercased
            eq("Fake User"),                // trimmed display name
            eq(USER_EMAIL),                 // trimmed + lowercased
            eq(ENCODED_PASSWORD),           // encoded, never plain
            isNull(),                       // no image
            any(Instant.class),
            any(Language.class)              // preferred language (current request locale)
        );
    }

    @Test
    public void testCreateWithImageSavesImage() {
        // Arrange
        final ImageData imageData = new ImageData(new byte[]{1, 2, 3}, "pic.png", "image/png");
        final Image stored = Image.builder().id(IMAGE_ID).filename("pic.png").alt("alt").build();
        when(passwordEncoder.encode(any())).thenReturn(ENCODED_PASSWORD);
        when(imageService.create(any(), any(), any(), any())).thenReturn(stored);
        when(userDao.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(buildFakeUser());

        // Act
        userService.create(new UserCreationDto(USER_USERNAME, USER_DISPLAY_NAME, USER_EMAIL, USER_PASSWORD, imageData));

        // Assert
        verify(imageService).create(eq("pic.png"), any(), eq("image/png"), any(byte[].class));
        verify(userDao).create(any(), any(), any(), any(), eq(stored), any(Instant.class), any(Language.class));
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* update                                                                                          */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testUpdateDisplayNameOnlyLeavesOthersNull() {
        // Arrange
        final User user = buildFakeUser();
        when(userDao.update(eq(USER_ID), eq("New Name"), isNull(), isNull(), isNull()))
            .thenReturn(Optional.of(user));

        // Act
        userService.update(UserEditDto.builder().user(user).newDisplayName("New Name").build());

        // Assert: only display name set; email/password/image null
        verify(userDao).update(eq(USER_ID), eq("New Name"), isNull(), isNull(), isNull());
    }

    @Test
    public void testUpdateEncodesNewPassword() {
        // Arrange
        final User user = buildFakeUser();
        when(passwordEncoder.encode(eq("brandNew"))).thenReturn(ENCODED_PASSWORD);
        when(userDao.update(eq(USER_ID), isNull(), isNull(), eq(ENCODED_PASSWORD), isNull()))
            .thenReturn(Optional.of(user));

        // Act
        userService.update(UserEditDto.builder().user(user).newPassword("brandNew").build());

        // Assert
        verify(userDao).update(eq(USER_ID), isNull(), isNull(), eq(ENCODED_PASSWORD), isNull());
    }

    @Test
    public void testUpdateReplacesImageAndDeletesOld() {
        // Arrange: user already had an image, uploads a new one
        final User user = buildUserWithImage(OLD_IMAGE_ID);
        final ImageData imageData = new ImageData(new byte[]{9}, "new.png", "image/png");
        final Image newImage = Image.builder().id(IMAGE_ID).filename("new.png").alt("alt").build();
        when(imageService.create(any(), any(), any(), any())).thenReturn(newImage);
        when(userDao.update(eq(USER_ID), isNull(), isNull(), isNull(), eq(IMAGE_ID)))
            .thenReturn(Optional.of(user));

        // Act
        userService.update(UserEditDto.builder().user(user).newImageData(imageData).build());

        // Assert: new image saved, user updated with it, old image deleted
        verify(userDao).update(eq(USER_ID), isNull(), isNull(), isNull(), eq(IMAGE_ID));
        verify(imageService).delete(OLD_IMAGE_ID);
    }

    @Test
    public void testUpdateWithoutNewImageDoesNotDeleteOld() {
        // Arrange
        final User user = buildUserWithImage(OLD_IMAGE_ID);
        when(userDao.update(eq(USER_ID), eq("New Name"), isNull(), isNull(), isNull()))
            .thenReturn(Optional.of(user));

        // Act
        userService.update(UserEditDto.builder().user(user).newDisplayName("New Name").build());

        // Assert
        verify(imageService, never()).delete(any());
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* updateLocation                                                                                  */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testUpdateLocationNullProvinceClearsBoth() {
        // Arrange
        final User user = buildFakeUser();
        when(userDao.updateLocation(eq(USER_ID), isNull(), isNull())).thenReturn(Optional.of(user));

        // Act: no province -> both columns cleared, detail dropped
        userService.updateLocation(user, null, "ignored detail");

        // Assert
        verify(userDao).updateLocation(USER_ID, null, null);
        verify(provinceService, never()).getById(any());
    }

    @Test
    public void testUpdateLocationUnknownProvinceClearsBoth() {
        // Arrange
        final User user = buildFakeUser();
        when(provinceService.getById(eq(NON_EXISTING_PROVINCE_ID))).thenReturn(Optional.empty());
        when(userDao.updateLocation(eq(USER_ID), isNull(), isNull())).thenReturn(Optional.of(user));

        // Act: unknown province -> treated as no province (consistent with category filters)
        userService.updateLocation(user, NON_EXISTING_PROVINCE_ID, "Belgrano");

        // Assert
        verify(userDao).updateLocation(USER_ID, null, null);
    }

    @Test
    public void testUpdateLocationValidProvinceStoresNormalizedDetail() {
        // Arrange
        final User user = buildFakeUser();
        final Province province = Province.builder().id(PROVINCE_ID).name("caba").build();
        when(provinceService.getById(eq(PROVINCE_ID))).thenReturn(Optional.of(province));
        when(userDao.updateLocation(eq(USER_ID), eq(PROVINCE_ID), eq("Belgrano"))).thenReturn(Optional.of(user));

        // Act: detail has surrounding whitespace -> trimmed before persisting
        userService.updateLocation(user, PROVINCE_ID, "  Belgrano  ");

        // Assert
        verify(userDao).updateLocation(USER_ID, PROVINCE_ID, "Belgrano");
    }

    @Test
    public void testUpdateLocationValidProvinceBlankDetailStoredAsNull() {
        // Arrange
        final User user = buildFakeUser();
        final Province province = Province.builder().id(PROVINCE_ID).name("caba").build();
        when(provinceService.getById(eq(PROVINCE_ID))).thenReturn(Optional.of(province));
        when(userDao.updateLocation(eq(USER_ID), eq(PROVINCE_ID), isNull())).thenReturn(Optional.of(user));

        // Act: blank detail -> stored as null
        userService.updateLocation(user, PROVINCE_ID, "   ");

        // Assert
        verify(userDao).updateLocation(USER_ID, PROVINCE_ID, null);
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* updatePreferredLanguage                                                                         */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testUpdatePreferredLanguagePersistsChosenLanguage() {
        // Arrange
        final User user = buildFakeUser();
        when(userDao.updatePreferredLanguage(eq(USER_ID), eq(Language.SPANISH))).thenReturn(Optional.of(user));

        // Act
        userService.updatePreferredLanguage(user, Language.SPANISH);

        // Assert
        verify(userDao).updatePreferredLanguage(USER_ID, Language.SPANISH);
    }

    @Test
    public void testUpdatePreferredLanguageReturnsUpdatedUser() {
        // Arrange
        final User user = buildFakeUser();
        when(userDao.updatePreferredLanguage(eq(USER_ID), eq(Language.ENGLISH))).thenReturn(Optional.of(user));

        // Act
        final User result = userService.updatePreferredLanguage(user, Language.ENGLISH);

        // Assert
        Assert.assertSame(user, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdatePreferredLanguageNonExistingUserThrows() {
        // Arrange
        final User user = buildFakeUser();
        when(userDao.updatePreferredLanguage(eq(USER_ID), eq(Language.SPANISH))).thenReturn(Optional.empty());

        // Act
        userService.updatePreferredLanguage(user, Language.SPANISH);
    }

    /* ---------------------------------------------------------------------------------------------- */
    /* updateEmail / markEmailAsVerified                                                               */
    /* ---------------------------------------------------------------------------------------------- */

    @Test
    public void testUpdateEmailNormalizes() {
        // Arrange
        final User user = buildFakeUser();
        when(userDao.update(eq(USER_ID), isNull(), eq(USER_EMAIL), isNull(), isNull()))
            .thenReturn(Optional.of(user));

        // Act
        userService.updateEmail(user, "  FAKE@Example.com  ");

        // Assert: email trimmed + lowercased
        verify(userDao).update(eq(USER_ID), isNull(), eq(USER_EMAIL), isNull(), isNull());
    }

    @Test
    public void testMarkEmailAsVerified() {
        // Arrange
        final User user = buildFakeUser();
        when(userDao.verifyEmail(eq(USER_ID), any(Instant.class))).thenReturn(Optional.of(user));

        // Act
        userService.markEmailAsVerified(user);

        // Assert
        verify(userDao).verifyEmail(eq(USER_ID), any(Instant.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEmailNonExistingUserThrows() {
        final User user = buildFakeUser();
        when(userDao.update(eq(USER_ID), isNull(), any(), isNull(), isNull())).thenReturn(Optional.empty());

        userService.updateEmail(user, USER_EMAIL);
    }
}
