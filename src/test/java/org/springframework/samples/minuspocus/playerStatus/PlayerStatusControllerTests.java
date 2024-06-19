package org.springframework.samples.minuspocus.playerStatus;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.minuspocus.configuration.SecurityConfiguration;
import org.springframework.samples.minuspocus.game.Game;
import org.springframework.samples.minuspocus.game.GameService;
import org.springframework.samples.minuspocus.game.PlayerStatus;
import org.springframework.samples.minuspocus.game.PlayerStatusController;
import org.springframework.samples.minuspocus.game.PlayerStatusService;
import org.springframework.samples.minuspocus.game.color.Color;
import org.springframework.samples.minuspocus.user.Authorities;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PlayerStatusController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class PlayerStatusControllerTests {

    private static final int TEST_PS_ID = 1;

	private static final String BASE_URL = "/api/v1/status";

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("unused")
    @Autowired
	private PlayerStatusController playerStatusController;

    @MockBean
	private PlayerStatusService playerStatusService;

    @MockBean
	private GameService gameService;

    private PlayerStatus ps;
    private User user;
    private Game g;

    @BeforeEach
    void setup(){
        Authorities auth = new Authorities();
		auth.setId(1);

        user = new User();
        user.setId(1);
		user.setAge("20");
		user.setUsername("user");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("user@gmail.com");
		user.setPassword("password");
        user.setAchievement(List.of());
        user.setAuthority(auth);

        ps = new PlayerStatus();
        ps.setId(TEST_PS_ID);
        ps.setBarrier(0);
        ps.setColor(Color.BLUE);
        ps.setHand(List.of());
        ps.setPlayer(user);
        ps.setRabbits(0);
        ps.setVote(false);
        ps.setW1(2);
        ps.setW2(2);
        ps.setW3(2);
        ps.setW4(2);

        g = new Game();
        g.setCode("");
        g.setCreate(LocalDateTime.now());
        g.setId(1);
        g.setStart(LocalDateTime.now());
        g.setIngredientsDeck(List.of());
        g.setName("Game");
        g.setRound(1);
        g.setSpellsDeck(List.of());
        g.setUsers(List.of(user));
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldFindPlayerStatusByPlayerId() throws Exception {
        when(this.playerStatusService.getAllPlayerStatus()).thenReturn(List.of(ps));

		mockMvc.perform(get(BASE_URL + "/{playerId}", user.getId())).andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(TEST_PS_ID))
			.andExpect(jsonPath("$.player").value(user));
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotFindPlayerStatusByPlayerId() throws Exception {
        when(this.playerStatusService.getAllPlayerStatus()).thenReturn(List.of(ps));

		mockMvc.perform(get(BASE_URL + "/{playerId}", TEST_PS_ID+1)).andExpect(status().isNotFound());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldDiscardIngredients() throws Exception {
        when(this.playerStatusService.findPlayerStatusById(TEST_PS_ID)).thenReturn(ps);
        when(this.gameService.findCurrentGame(user)).thenReturn(g);
        when(this.playerStatusService.discardIngredients(g,ps)).thenReturn(ps);

		mockMvc.perform(put(BASE_URL + "/{id}/discard", TEST_PS_ID).with(csrf())).andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(TEST_PS_ID))
			.andExpect(jsonPath("$.player").value(ps.getPlayer()));
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotDiscardIngredientsIfGameNotStarted() throws Exception {
        g.setStart(null);
        when(this.playerStatusService.findPlayerStatusById(TEST_PS_ID)).thenReturn(ps);
        when(this.gameService.findCurrentGame(user)).thenReturn(g);
        when(this.playerStatusService.discardIngredients(g,ps)).thenReturn(ps);

		mockMvc.perform(put(BASE_URL + "/{id}/discard", TEST_PS_ID).with(csrf())).andExpect(status().isInternalServerError());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotDiscardIngredientsIfGameFinished() throws Exception {
        g.setFinish(LocalDateTime.now());
        when(this.playerStatusService.findPlayerStatusById(TEST_PS_ID)).thenReturn(ps);
        when(this.gameService.findCurrentGame(user)).thenReturn(g);
        when(this.playerStatusService.discardIngredients(g,ps)).thenReturn(ps);

		mockMvc.perform(put(BASE_URL + "/{id}/discard", TEST_PS_ID).with(csrf())).andExpect(status().isInternalServerError());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldPunish() throws Exception {
        when(this.playerStatusService.findPlayerStatusById(TEST_PS_ID)).thenReturn(ps);
        when(this.gameService.findCurrentGame(user)).thenReturn(g);
        when(this.playerStatusService.punishPlayer(ps)).thenReturn(ps);

		mockMvc.perform(put(BASE_URL + "/{id}/punish", TEST_PS_ID).with(csrf())).andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(TEST_PS_ID))
			.andExpect(jsonPath("$.player").value(ps.getPlayer()));
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotPunishIfGameNotStarted() throws Exception {
        g.setStart(null);
        when(this.playerStatusService.findPlayerStatusById(TEST_PS_ID)).thenReturn(ps);
        when(this.gameService.findCurrentGame(user)).thenReturn(g);
        when(this.playerStatusService.punishPlayer(ps)).thenReturn(ps);

		mockMvc.perform(put(BASE_URL + "/{id}/punish", TEST_PS_ID).with(csrf())).andExpect(status().isInternalServerError());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotPunishIfGameFinished() throws Exception {
        g.setFinish(LocalDateTime.now());
        when(this.playerStatusService.findPlayerStatusById(TEST_PS_ID)).thenReturn(ps);
        when(this.gameService.findCurrentGame(user)).thenReturn(g);
        when(this.playerStatusService.punishPlayer(ps)).thenReturn(ps);

		mockMvc.perform(put(BASE_URL + "/{id}/punish", TEST_PS_ID).with(csrf())).andExpect(status().isInternalServerError());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldVote() throws Exception {
        when(this.playerStatusService.findPlayerStatusById(TEST_PS_ID)).thenReturn(ps);
        when(this.gameService.findCurrentGame(user)).thenReturn(g);
        when(this.playerStatusService.voteDiscard(ps)).thenReturn(ps);

		mockMvc.perform(put(BASE_URL + "/{id}/vote", TEST_PS_ID).with(csrf())).andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(TEST_PS_ID))
			.andExpect(jsonPath("$.player").value(ps.getPlayer()));
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotVoteIfGameNotStarted() throws Exception {
        g.setStart(null);
        when(this.playerStatusService.findPlayerStatusById(TEST_PS_ID)).thenReturn(ps);
        when(this.gameService.findCurrentGame(user)).thenReturn(g);
        when(this.playerStatusService.voteDiscard(ps)).thenReturn(ps);

		mockMvc.perform(put(BASE_URL + "/{id}/vote", TEST_PS_ID).with(csrf())).andExpect(status().isInternalServerError());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotVoteIfGameFinished() throws Exception {
        g.setFinish(LocalDateTime.now());
        when(this.playerStatusService.findPlayerStatusById(TEST_PS_ID)).thenReturn(ps);
        when(this.gameService.findCurrentGame(user)).thenReturn(g);
        when(this.playerStatusService.voteDiscard(ps)).thenReturn(ps);

		mockMvc.perform(put(BASE_URL + "/{id}/vote", TEST_PS_ID).with(csrf())).andExpect(status().isInternalServerError());
    }
}
