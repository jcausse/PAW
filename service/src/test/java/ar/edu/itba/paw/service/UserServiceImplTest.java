package ar.edu.itba.paw.service;

import org.junit.Test;
import org.junit.Before;
import java.util.Optional;
import org.junit.Assert;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.UserDao;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

	private static final long USER_ID = 1;
    private static final String USER_USERNAME = "fake_user";
    private static final String USER_DISPLAY_NAME = "Fake User";
	private static final String USER_EMAIL = "fake@example.com";
    private static final String USER_PASSWORD = "fake_password";
    
    @InjectMocks
	private UserServiceImpl userService;
	@Mock
	private UserDao userDao;
    @Mock
    private ImageService imageService;

    private User buildFakeUser() {
        return User.builder()
            .id(USER_ID)
            .username(USER_USERNAME)
            .displayName(USER_DISPLAY_NAME)
            .email(USER_EMAIL)
            .password(USER_PASSWORD)
            .build();
    }

	@Test
	public void testGetByIdUserExists() {
		// Arrange
        final User user = buildFakeUser();
		when(userDao.getById(eq(USER_ID))).thenReturn(Optional.of(user));
		
		// Act
		final Optional<User> maybeUser = userService.getById(USER_ID);
		
		// Assert
		Assert.assertTrue(maybeUser.isPresent());
		Assert.assertEquals(USER_ID, (long) maybeUser.get().getId());
		Assert.assertEquals(USER_USERNAME, maybeUser.get().getUsername());
		Assert.assertEquals(USER_EMAIL, maybeUser.get().getEmail());
	}
	
	@Test
	public void testGetByIdUserDoesNotExist() {
		// Arrange
		when(userDao.getById(eq(USER_ID))).thenReturn(Optional.empty());
	
		// Act
		final Optional<User> maybeUser = userService.getById(USER_ID);
	
		// Assert
		Assert.assertFalse(maybeUser.isPresent());
	}
	
	@Test
	public void testGetByIdUserInvalid() {
		// Arrange
		when(userDao.getById(eq(-1L))).thenReturn(Optional.empty());
	
		// Act
		final Optional<User> maybeUser = userService.getById(-1L);
	
		// Assert
		Assert.assertFalse(maybeUser.isPresent());
	}

    @Test
    public void testGetByUsernameUserExists() {
        // Arrange
        final User user = buildFakeUser();
        when(userDao.getByUsername(eq(USER_USERNAME))).thenReturn(Optional.of(user));

        // Act
        final Optional<User> maybeUser = userService.getByUsername(USER_USERNAME);

        // Assert
        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(USER_USERNAME, maybeUser.get().getUsername());
    }

    @Test
    public void testGetByUsernameUserDoesNotExist() {
        // Arrange
        when(userDao.getByUsername(eq(USER_USERNAME))).thenReturn(Optional.empty());

        // Act
        final Optional<User> maybeUser = userService.getByUsername(USER_USERNAME);

        // Assert
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testGetByEmailUserExists() {
        // Arrange
        final User user = buildFakeUser();
        when(userDao.getByEmail(eq(USER_EMAIL))).thenReturn(Optional.of(user));

        // Act
        final Optional<User> maybeUser = userService.getByEmail(USER_EMAIL);

        // Assert
        Assert.assertTrue(maybeUser.isPresent());
        Assert.assertEquals(USER_EMAIL, maybeUser.get().getEmail());
    }

    @Test
    public void testGetByEmailUserDoesNotExist() {
        // Arrange
        when(userDao.getByEmail(eq(USER_EMAIL))).thenReturn(Optional.empty());

        // Act
        final Optional<User> maybeUser = userService.getByEmail(USER_EMAIL);

        // Assert
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testIsUsernameTakenTrue() {
        // Arrange
        when(userDao.isUsernameTaken(eq(USER_USERNAME))).thenReturn(true);

        // Act
        final boolean taken = userService.isUsernameTaken(USER_USERNAME);

        // Assert
        Assert.assertTrue(taken);
    }

    @Test
    public void testIsUsernameTakenFalse() {
        // Arrange
        when(userDao.isUsernameTaken(eq(USER_USERNAME))).thenReturn(false);

        // Act
        final boolean taken = userService.isUsernameTaken(USER_USERNAME);

        // Assert
        Assert.assertFalse(taken);
    }

    @Test
    public void testIsEmailTakenTrue() {
        // Arrange
        when(userDao.isEmailTaken(eq(USER_EMAIL))).thenReturn(true);

        // Act
        final boolean taken = userService.isEmailTaken(USER_EMAIL);

        // Assert
        Assert.assertTrue(taken);
    }

    @Test
    public void testIsEmailTakenFalse() {
        // Arrange
        when(userDao.isEmailTaken(eq(USER_EMAIL))).thenReturn(false);

        // Act
        final boolean taken = userService.isEmailTaken(USER_EMAIL);

        // Assert
        Assert.assertFalse(taken);
    }
}