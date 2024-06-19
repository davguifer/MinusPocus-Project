package org.springframework.samples.minuspocus.user;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.samples.minuspocus.configuration.SecurityConfiguration;
import org.springframework.samples.minuspocus.exceptions.AccessDeniedException;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.game.Game;
import org.springframework.samples.minuspocus.game.GameService;
import org.springframework.samples.minuspocus.game.PlayerStatus;
import org.springframework.samples.minuspocus.game.PlayerStatusService;
import org.springframework.samples.minuspocus.stats.Achievement;
import org.springframework.samples.minuspocus.stats.AchievementService;
import org.springframework.samples.minuspocus.stats.Metric;
import org.springframework.samples.minuspocus.stats.Stat;
import org.springframework.samples.minuspocus.stats.StatService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(controllers = UserRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class)  , excludeAutoConfiguration = SecurityConfiguration.class)  
class UserControllerTests {

	private static final int TEST_USER_ID = 1;
	private static final int TEST_AUTH_ID = 1;
	private static final String BASE_URL = "/api/v1/users";

	@SuppressWarnings("unused")
	@Autowired
	private UserRestController userController;

	@MockBean
	private UserService userService;

	@MockBean
	private AchievementService achievementService;

	@MockBean
	private AuthoritiesService authService;

	@MockBean
	private StatService statService;
	
	@MockBean
	private GameService gameService;

	@MockBean
	private PlayerStatusService playerStatusService;
	@MockBean
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Authorities auth;
	private User user, logged;


	@BeforeEach
	void setup() {
		auth = new Authorities();
		auth.setId(TEST_AUTH_ID);

		Achievement achiev1 = new Achievement();
		achiev1.setId(1);
		achiev1.setName("Aprendiz");
		achiev1.setDescription("Juega un total de 10 partidas");
		achiev1.setBadgeImage("https://cdn-icons-png.flaticon.com/512/5243/5243423.png");
		achiev1.setThreshold(10.00);
		achiev1.setMetric(Metric.GAMES_PLAYED);

		user = new User();
		user.setId(1);
		user.setAge("20");
		user.setUsername("user");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("user@gmail.com");
		user.setPassword("password");
		user.setAuthority(auth);
		user.setFriends(List.of());
		user.setAvatar("");
		user.setAchievement(List.of(achiev1));

		when(this.userService.findCurrentUser()).thenReturn(getUserFromDetails(
				(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
	}

	private User getUserFromDetails(UserDetails details) {
		logged = new User();
		logged.setUsername(details.getUsername());
		logged.setPassword(details.getPassword());
		Authorities aux = new Authorities();
		for (GrantedAuthority auth : details.getAuthorities()) {
			aux.setAuthority(auth.getAuthority());
		}
		logged.setAuthority(aux);
		return logged;
	}

	@Test
	@WithMockUser("admin")
	void shouldFindAll() throws Exception {

		Achievement achiev1 = new Achievement();
		achiev1.setId(1);
		achiev1.setName("Aprendiz");
		achiev1.setDescription("Juega un total de 10 partidas");
		achiev1.setBadgeImage("https://cdn-icons-png.flaticon.com/512/5243/5243423.png");
		achiev1.setThreshold(10.00);
		achiev1.setMetric(Metric.GAMES_PLAYED);

		User sara = new User();
		sara.setId(2);
		sara.setUsername("Sara");
		sara.setAge("20");
		sara.setEmail("sara@gmail.com");
		sara.setFirstName("");
		sara.setLastName("");
		sara.setPassword("password");
		sara.setAuthority(authService.findByAuthority("ADMIN"));
		sara.setFriends(List.of(user));
		sara.setAchievement(List.of(achiev1));
		

		User juan = new User();
		juan.setId(3);
		juan.setUsername("Juan");
		juan.setAge("20");
		juan.setEmail("juan@gmail.com");
		juan.setFirstName("");
		juan.setLastName("");
		juan.setPassword("password2");
		juan.setAuthority(authService.findByAuthority("ADMIN"));
		juan.setFriends(List.of(user));
		juan.setAchievement(List.of(achiev1));


		when(this.userService.findAll()).thenReturn(List.of(user, sara, juan));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].username").value("user"))
				.andExpect(jsonPath("$[?(@.id == 2)].username").value("Sara"))
				.andExpect(jsonPath("$[?(@.id == 3)].username").value("Juan"));
	}
	
	@Test
	@WithMockUser("admin")
	void shouldFindAllAuths() throws Exception {
		Authorities aux = new Authorities();
		aux.setId(2);
		aux.setAuthority("AUX");

		when(this.authService.findAll()).thenReturn(List.of(auth, aux));

		mockMvc.perform(get(BASE_URL + "/authorities")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[?(@.id == 2)].authority").value("AUX"));
	}

	@Test
    @WithMockUser("admin")
    void shouldFindAllAuthsByEmptyList() throws Exception {
        when(this.authService.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(BASE_URL + "/authorities")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(0));
    }

	@Test
	@WithMockUser("admin")
	void shouldReturnUser() throws Exception {
		when(this.userService.findUser(TEST_USER_ID)).thenReturn(user);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_USER_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_USER_ID))
				.andExpect(jsonPath("$.username").value(user.getUsername()))
				.andExpect(jsonPath("$.authority.authority").value(user.getAuthority().getAuthority()));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundUser() throws Exception {
		when(this.userService.findUser(TEST_USER_ID)).thenThrow(ResourceNotFoundException.class);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_USER_ID)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("user")
	void shouldFindAllFriends() throws Exception {
		User friend1 = new User();
		friend1.setId(2);
		friend1.setUsername("Friend1");

		User friend2 = new User();
		friend2.setId(3);
		friend2.setUsername("Friend2");

		when(userService.findAllFriends("user")).thenReturn(List.of(friend1, friend2));

		mockMvc.perform(get(BASE_URL + "/friends/{username}", "user"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(2))
			.andExpect(jsonPath("$[0].username").value("Friend1"))
			.andExpect(jsonPath("$[1].username").value("Friend2"));
	}
	@Test
	@WithMockUser("admin")
	void shouldReturnAchievementsByPlayer() throws Exception {
		int userId = 1;
		
		Achievement achievement = new Achievement();
		achievement.setId(1);
		achievement.setName("Aprendiz");
		achievement.setDescription("Juega un total de 10 partidas");
		achievement.setBadgeImage("https://cdn-icons-png.flaticon.com/512/5243/5243423.png");
		achievement.setThreshold(10.00);
		achievement.setMetric(Metric.GAMES_PLAYED);
	
		when(userService.findUser(userId)).thenReturn(user);
		when(userService.getAchievementsByPlayer(userId)).thenReturn(List.of(achievement));
	
		mockMvc.perform(get(BASE_URL + "/{userId}/achievements", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(1))
			.andExpect(jsonPath("$[0].name").value("Aprendiz"))
			.andExpect(jsonPath("$[0].description").value("Juega un total de 10 partidas"));
	}
	
	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundForNonexistentUserAchievements() throws Exception {
		int nonexistentUserId = 999;
	
		when(userService.findUser(nonexistentUserId)).thenReturn(null);
	
		mockMvc.perform(get(BASE_URL + "/{userId}/achievements", nonexistentUserId))
			.andExpect(status().isNotFound());
	}
	


	@Test
@WithMockUser("admin")
void shouldCreateFriend() throws Exception {
    String username = "friendUsername";

    User friend = new User();
    friend.setId(2);
    friend.setUsername(username);

    when(userService.createFriend(any(User.class), eq(username))).thenReturn(List.of(friend));

    mockMvc.perform(post(BASE_URL + "/friends/{username}", username).with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[0].username").value(username));
}

@Test
@WithMockUser("admin")
void shouldReturnBadRequestForInvalidFriendData() throws Exception {
    mockMvc.perform(post(BASE_URL + "/friends/{username}", "friendUsername").with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(new User())))
        .andExpect(status().isBadRequest());
}




	@Test
	@WithMockUser("user")
	void shouldReturnNotFoundForInvalidUsername() throws Exception {
		when(userService.findAllFriends("nonexistentuser")).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(get(BASE_URL + "/friends/{username}", "nonexistentuser"))
			.andExpect(status().isNotFound());
	}


	@Test
	@WithMockUser("user")
	void shouldReturnEmptyListForUserWithNoFriends() throws Exception {
		when(userService.findAllFriends("user")).thenReturn(Collections.emptyList());

		mockMvc.perform(get(BASE_URL + "/friends/{username}", "user"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(0));
	}


	@Test
	@WithMockUser("admin")
	void shouldCreateUser() throws Exception {
		when(userService.saveUser(user)).thenReturn(user);

		mockMvc.perform(post(BASE_URL).with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(user)))
			.andExpect(status().isCreated());

	}


	@Test
	@WithMockUser("admin")
	void shouldCreateUserWithRepeatUsername() throws Exception {
		when(this.userService.saveUser(any(User.class))).thenThrow(DataIntegrityViolationException.class);

		mockMvc.perform(post(BASE_URL).with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(user)))
			.andExpect(status().isInternalServerError()) // Espera un código de respuesta 500
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof DataIntegrityViolationException)); // Verifica que la excepción lanzada sea DataIntegrityViolationException
	}


	@Test
	@WithMockUser("admin")
	void shouldCreateUserWithIncompleteData() throws Exception {
		User incompleteUser = new User();
		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(incompleteUser))).andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser("admin")
	void shouldUpdateUser() throws Exception {
		user.setUsername("UPDATED");
		user.setPassword("CHANGED");

		when(this.userService.findUser(TEST_USER_ID)).thenReturn(user);
		when(this.userService.updateUser(any(User.class), any(Integer.class))).thenReturn(user);
			
		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(user))).andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("UPDATED")).andExpect(jsonPath("$.password").value("CHANGED"));
	}

	@Test
	@WithMockUser("admin")
	void shouldUpdateUserButUserNotExist() throws Exception {
		when(this.userService.findUser(TEST_USER_ID)).thenReturn(null);
		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(user))).andExpect(status().isNotFound());
	}



	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundUpdateUser() throws Exception {
		user.setUsername("UPDATED");
		user.setPassword("UPDATED");

		when(this.userService.findUser(TEST_USER_ID)).thenThrow(ResourceNotFoundException.class);
		when(this.userService.updateUser(any(User.class), any(Integer.class))).thenReturn(user);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(user))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnBadRequestForInvalidUserData() throws Exception {
		User invalidUser = new User();
		invalidUser.setId(TEST_USER_ID);
		invalidUser.setUsername(null);  

		when(userService.findUser(TEST_USER_ID)).thenReturn(user);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidUser)))
				.andExpect(status().isBadRequest()); 
	}
	
	@Test
	@WithMockUser("admin")
	void shouldDeleteOtherUser() throws Exception {
		logged.setId(2);
		User user = new User();
		user.setId(2);
		user.setAge("19");
		user.setUsername("Prueba");
		user.setFirstName("Manolo");
		user.setLastName("Lama");
		user.setEmail("manolo@gmail.com");
		user.setPassword("Prueba");
		
		User userToDelete = new User();
		userToDelete.setId(TEST_USER_ID);
		userToDelete.setUsername("usernameToDelete");
		userToDelete.setFriends(List.of(user));
		
		Stat userStat = new Stat();
		userStat.setId(1);

		Game game = new Game();
		game.setUsers(List.of(userToDelete));
		game.setId(1);

		PlayerStatus userPS = new PlayerStatus();
		userPS.setId(1);
		userPS.setPlayer(userToDelete);
		
		when(this.userService.findUser(TEST_USER_ID)).thenReturn(userToDelete);
		when(this.userService.findCurrentUser()).thenReturn(user);
		when(this.userService.findAll()).thenReturn(List.of(user, userToDelete));
		user.setFriends(List.of());
		when(this.userService.saveUser(user)).thenReturn(user);
		when(this.gameService.getAllGames()).thenReturn(List.of(game));
		game.setUsers(List.of());
		when(this.gameService.saveGame(game)).thenReturn(game);
		when(this.statService.getAllStats()).thenReturn(List.of(userStat));
		when(this.playerStatusService.getAllPlayerStatus()).thenReturn(List.of(userPS));
		
		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("User deleted!"));
	}

	@Test
	@WithMockUser("admin")
	void shouldNotDeleteLoggedUser() throws Exception {
		logged.setId(TEST_USER_ID);

		when(this.userService.findUser(TEST_USER_ID)).thenReturn(user);
		doNothing().when(this.userService).deleteUser(TEST_USER_ID);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_USER_ID).with(csrf())).andExpect(status().isForbidden())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundWhenDeletingNonexistentUser() throws Exception {
		int nonexistentUserId = 999; 

		when(userService.findUser(nonexistentUserId)).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(delete(BASE_URL + "/{id}", nonexistentUserId).with(csrf()))
			.andExpect(status().isNotFound()); 
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundForNonexistentUserId() throws Exception {
		int nonexistentUserId = 999; 

		when(userService.getAchievementsByPlayer(nonexistentUserId)).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(get(BASE_URL + "/{userId}/achievement", nonexistentUserId))
				.andExpect(status().isNotFound()); 
	}

}
