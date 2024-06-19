package org.springframework.samples.minuspocus.achievement;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.minuspocus.configuration.SecurityConfiguration;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.stats.Achievement;
import org.springframework.samples.minuspocus.stats.AchievementController;
import org.springframework.samples.minuspocus.stats.AchievementService;
import org.springframework.samples.minuspocus.stats.Metric;
import org.springframework.samples.minuspocus.stats.Stat;
import org.springframework.samples.minuspocus.stats.StatService;
import org.springframework.samples.minuspocus.user.Authorities;
import org.springframework.samples.minuspocus.user.UserService;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AchievementController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
class AchievementControllerTests {

    private static final int TEST_A_ID = 1;

	private static final String BASE_URL = "/api/v1/achievements";

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("unused")
    @Autowired
	private AchievementController achievementController;

    @Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AchievementService achievementService;

    @MockBean
    private UserService userService;

    @MockBean
    private StatService statService;

    private Achievement achievement;
    private User user;
    private Stat stat;

    @BeforeEach
    void setup(){
        achievement = new Achievement();
        achievement.setId(TEST_A_ID);
        achievement.setName("Logro 1");
        achievement.setDescription("Juega 10 partidas.");
        achievement.setThreshold(10.0);
        achievement.setBadgeImage("");
        achievement.setMetric(Metric.GAMES_PLAYED);

        Authorities auth = new Authorities();
		auth.setId(1);
        auth.setAuthority("PLAYER");

        user = new User();
        user.setId(1);
		user.setAge("20");
		user.setUsername("user");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("user@gmail.com");
		user.setPassword("password");
        user.setAchievement(List.of(achievement));
        user.setAuthority(auth);

        stat = new Stat();
        stat.setUser(user);
        stat.setGamesPlayed(10);
        stat.setVictories(5);
        stat.setTimePlayed(10000.0);
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldFindAll() throws Exception {

        Achievement achievement2 = new Achievement();
        achievement2.setId(2);
        achievement2.setName("Logro 2");
        achievement2.setDescription("Juega 20 partidas.");
        achievement2.setThreshold(20.0);
        achievement2.setBadgeImage("");
        achievement2.setMetric(Metric.GAMES_PLAYED);

		when(this.achievementService.getAchievements()).thenReturn(List.of(achievement, achievement2));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2))
			.andExpect(jsonPath("$[?(@.id == 1)].name").value("Logro 1"))
			.andExpect(jsonPath("$[?(@.id == 2)].name").value("Logro 2"));
	}

    @Test
	@WithMockUser("PLAYER")
    void shouldFindAchievementById() throws Exception{
		when(this.achievementService.findAchievementById(TEST_A_ID)).thenReturn(achievement);

		mockMvc.perform(get(BASE_URL + "/{id}", 1)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_A_ID))
				.andExpect(jsonPath("$.name").value("Logro 1"));
    }

    @Test
	@WithMockUser("PLAYER")
    void shouldNotFindAchievementById() throws Exception{
		when(this.achievementService.findAchievementById(0)).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(put(BASE_URL + "/{id}", 0).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(achievement))).andExpect(status().isNotFound());
    }

    @Test
	@WithMockUser("ADMIN")
	void shouldCreateAchievement() throws Exception {
        when(this.achievementService.saveAchievement(achievement)).thenReturn(achievement);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(achievement))).andExpect(status().isCreated());
	}

    @Test
	@WithMockUser("admin")
	void shouldNotCreateAchievementNoValid() throws Exception {
        achievement.setDescription("");

        mockMvc.perform(post(BASE_URL).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(achievement)))
            .andExpect(status().isInternalServerError());
        

        achievement.setDescription("Juega 10 partidas.");
        achievement.setThreshold(-1);

        mockMvc.perform(post(BASE_URL).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(achievement)))
            .andExpect(status().isInternalServerError());

        achievement.setMetric(null);
        achievement.setThreshold(10.0);

        mockMvc.perform(post(BASE_URL).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(achievement)))
            .andExpect(status().isInternalServerError());
	}

    @Test
	@WithMockUser("admin")
	void shouldDeleteAchievementById() throws Exception {
        when(this.userService.findAll()).thenReturn(List.of(user));
        user.setAchievement(List.of());
        when(this.userService.saveUser(user)).thenReturn(user);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_A_ID).with(csrf())).andExpect(status().isOk());
	}

    @Test
	@WithMockUser("admin")
	void shouldModifyAchievement() throws Exception {
        Achievement uachievement = new Achievement();
        uachievement.setId(TEST_A_ID);
        uachievement.setName("Logro 1");
        uachievement.setDescription("Juega 15 partidas.");
        uachievement.setThreshold(15.0);
        uachievement.setBadgeImage("");
        uachievement.setMetric(Metric.GAMES_PLAYED);

        when(this.achievementService.findAchievementById(TEST_A_ID)).thenReturn(achievement);
        when(this.achievementService.saveAchievement(uachievement)).thenReturn(uachievement);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_A_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(uachievement))).andExpect(status().isOk());
	}

    @Test
	@WithMockUser("admin")
	void shouldNotModifyAchievement() throws Exception {
        Achievement uachievement = new Achievement();
        uachievement.setId(2);
        uachievement.setName("Logro 1");
        uachievement.setDescription("Juega 15 partidas.");
        uachievement.setThreshold(15.0);
        uachievement.setBadgeImage("");
        uachievement.setMetric(Metric.GAMES_PLAYED);

        when(this.achievementService.findAchievementById(TEST_A_ID)).thenReturn(achievement);
        when(this.achievementService.saveAchievement(uachievement)).thenReturn(uachievement);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_A_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(uachievement))).andExpect(status().isInternalServerError());
	}

    @Test
	@WithMockUser("admin")
	void shouldClaimAchievement() throws Exception {
        user.setAchievement(List.of());
        when(this.achievementService.findAchievementById(TEST_A_ID)).thenReturn(achievement);
        when(this.userService.findUser(user.getId())).thenReturn(user);
        when(this.statService.getAllStats()).thenReturn(List.of(stat));

        mockMvc.perform(put(BASE_URL + "/{id}/claim", TEST_A_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(user.getId()))).andExpect(status().isOk());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotClaimAchievement() throws Exception {
        when(this.achievementService.findAchievementById(TEST_A_ID)).thenReturn(achievement);
        when(this.userService.findUser(user.getId())).thenReturn(user);
        when(this.statService.getAllStats()).thenReturn(List.of(stat));

        mockMvc.perform(put(BASE_URL + "/{id}/claim", TEST_A_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(user.getId()))).andExpect(status().isInternalServerError());

        
        Authorities auth = new Authorities();
		auth.setId(2);
        auth.setAuthority("ADMIN");
        user.setAuthority(auth);
        user.setAchievement(List.of());
        when(this.achievementService.findAchievementById(TEST_A_ID)).thenReturn(achievement);
        when(this.userService.findUser(user.getId())).thenReturn(user);
        when(this.statService.getAllStats()).thenReturn(List.of(stat));

        mockMvc.perform(put(BASE_URL + "/{id}/claim", TEST_A_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(user.getId()))).andExpect(status().isInternalServerError());
    }
}
