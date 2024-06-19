package org.springframework.samples.minuspocus.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.stats.Achievement;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

//@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@SpringBootTest
@AutoConfigureTestDatabase
class UserServiceTests {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthoritiesService authService;

	@Test
	@WithMockUser(username = "Mario", password = "4dm1n")
	void shouldFindCurrentUser() {
		User user = this.userService.findCurrentUser();
		assertEquals("Mario", user.getUsername());
	}

	@Test
	@WithMockUser(username = "prueba")
	void shouldNotFindCorrectCurrentUser() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
	}

	@Test
	void shouldNotFindAuthenticated() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
	}

	@Test
	void shouldFindAllUsers() {
		List<User> users = (List<User>) this.userService.findAll();
		assertEquals(11, users.size());
	}

	@Test
	void shouldFindUsersByUsername() {
		User user = this.userService.findUser("Mario");
		assertEquals("Mario", user.getUsername());
	}

	@Test
	void shouldFindUsersByAuthority() {
		List<User> players = (List<User>) this.userService.findAllByAuthority("PLAYER");
		assertEquals(5, players.size());

		List<User> admins = (List<User>) this.userService.findAllByAuthority("ADMIN");
		assertEquals(6, admins.size());

		
	}

	@Test
	void shouldNotFindUserByIncorrectUsername() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findUser("usernotexists"));
	}



	@Test
	void shouldNotFindSinglePlayerWithBadUsername() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findPlayerByUser("badusername"));
	}

	@Test
	void shouldFindSinglePlayerByUserId() {
		User user = this.userService.findPlayerByUser(7);
		assertEquals("Mario", user.getUsername());
	}

	@Test
	void shouldNotFindSingleUserPlayerWithBadUserId() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findPlayerByUser(100));
	}

	@Test
	void shouldFindSingleUser() {
		User user = this.userService.findUser(7);
		assertEquals("Mario", user.getUsername());
	}

	@Test
	void shouldNotFindSingleUserWithBadID() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findUser(100));
	}

	@Test
	void shouldExistUser() {
		assertEquals(true, this.userService.existsUser("Mario"));
	}

	@Test
	void shouldNotExistUser() {
		assertEquals(false, this.userService.existsUser("player10000"));
	}

	@Test
	@Transactional
	void shouldUpdateUser() {
		User user = this.userService.findUser(2);
		user.setUsername("Change");
		userService.updateUser(user, 2);
		user = this.userService.findUser(2);
		assertEquals("Change", user.getUsername());
	}

	@Test
	@Transactional
	void shouldInsertUser() {
		int count = ((Collection<User>) this.userService.findAll()).size();
		User friend1 = userService.findUser(1);
		User friend2 = userService.findUser(2);

		User user = new User();
		user.setAge("20");
		user.setEmail("sam@gmail.com");
		user.setUsername("Sam");
		user.setFirstName("Luque");
		user.setLastName("Gonzalez");
		user.setPassword("password");
		user.setAuthority(authService.findByAuthority("ADMIN"));
		user.setFriends(List.of(friend1,friend2));

		this.userService.saveUser(user);
		assertNotEquals(0, user.getId().longValue());
		assertNotNull(user.getId());

		int finalCount = ((Collection<User>) this.userService.findAll()).size();
		assertEquals(count + 1, finalCount);
	}
	

	@Test
	@Transactional
	void shouldDeleteUserWithoutPlayer() {
		Integer firstCount = ((Collection<User>) userService.findAll()).size();
		User user = new User();
		User friend1 = userService.findUser(1);
		User friend2 = userService.findUser(2);
		user.setAge("20");
		user.setEmail("sam@gmail.com");
		user.setUsername("Sam");
		user.setFirstName("Luque");
		user.setLastName("Gonzalez");
		user.setPassword("password");
		user.setFriends(List.of(friend1,friend2));

		Authorities auth = authService.findByAuthority("PLAYER");
		user.setAuthority(auth);
		this.userService.saveUser(user);

		Integer secondCount = ((Collection<User>) userService.findAll()).size();
		assertEquals(firstCount + 1, secondCount);
		userService.deleteUser(user.getId());
		Integer lastCount = ((Collection<User>) userService.findAll()).size();
		assertEquals(firstCount, lastCount);
	}



	@Test
	void shouldGetAchievementsByPlayer() {
		Achievement achievement = new Achievement();
		achievement.setId(1);

		User user = userService.findUser(2);
		user.setAge("20");
		user.setEmail("sam@gmail.com");
		user.setUsername("Sam");
		user.setFirstName("Luque");
		user.setLastName("Gonzalez");
		user.setPassword("password");
		user.setAchievement(List.of(achievement));
		userService.saveUser(user);

		List<Achievement> achievements = userService.getAchievementsByPlayer(user.getId());
	
		assertEquals(1, achievements.size());
	}
	
	


}
