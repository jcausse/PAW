package ar.edu.itba.paw.service;

import org.junit.Test;
import org.junit.Before;
import java.util.Optional;
import org.junit.Assert;
import static org.mockito.Mockito.*;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.UserDao;


public class UserServiceImplTest {

	private static final long USER_ID = 1;
    private static final String USER_USERNAME = "fake_user";
    private static final String USER_DISPLAY_NAME = "Fake User";
	private static final String USER_EMAIL = "fake@example.com";
    private static final String USER_PASSWORD = "fake_password";
    
	private UserServiceImpl userService;
	
	private UserDao userDao;
    private ImageService imageService;
	
	@Before
	public void setup() {
		this.userDao = mock(UserDao.class);
		this.imageService = mock(ImageService.class);
		this.userService = new UserServiceImpl(userDao, imageService);
	}

	@Test
	public void testGetByIdUserExists() {
		// Arrange
        final User user = User.builder()
            .id(USER_ID)
            .username(USER_USERNAME)
            .displayName(USER_DISPLAY_NAME)
            .email(USER_EMAIL)
            .password(USER_PASSWORD)
            .build();
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
}