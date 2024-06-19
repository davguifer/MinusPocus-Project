package org.springframework.samples.minuspocus.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.minuspocus.card.Ingredient;
import org.springframework.samples.minuspocus.card.IngredientService;
import org.springframework.samples.minuspocus.card.Spell;
import org.springframework.samples.minuspocus.card.SpellService;
import org.springframework.samples.minuspocus.configuration.SecurityConfiguration;
import org.springframework.samples.minuspocus.user.Authorities;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.samples.minuspocus.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(controllers = GameController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
class GameControllerTests {

    private static final int TEST_GAME_ID = 1;

	private static final String BASE_URL = "/api/v1/game";

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("unused")
    @Autowired
	private GameController gameController;

    @Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private GameService gameService;

    @MockBean
    private UserService userService;

    @MockBean
    private SpellService spellService;

    @MockBean
    private IngredientService ingredientService;

    private Game game;

    @BeforeEach
    void setup(){
        LocalDateTime hoy =  LocalDateTime.now();

        Authorities auth = new Authorities();
		auth.setId(1);

        User fulano = new User();
        fulano.setId(1);
		fulano.setAge("20");
		fulano.setUsername("user");
		fulano.setFirstName("firstName");
		fulano.setLastName("lastName");
		fulano.setEmail("user@gmail.com");
		fulano.setPassword("password");
        fulano.setAchievement(List.of());
        fulano.setAuthority(auth);

        User mengano = new User();
        mengano.setId(1);
		mengano.setAge("20");
		mengano.setUsername("user");
		mengano.setFirstName("firstName");
		mengano.setLastName("lastName");
		mengano.setEmail("user@gmail.com");
		mengano.setPassword("password");
        mengano.setAchievement(List.of());
        mengano.setAuthority(auth);

        game = new Game();
        game.setId(1);
        game.setName("Game");
        game.setCode("");
        game.setRound(1);
        game.setCreate(hoy);
        game.setStart(null);
        game.setFinish(null);
        game.setSpellsDeck(List.of());
        game.setIngredientsDeck(List.of());
        game.setUsers(List.of(fulano,mengano));
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldFindAll() throws Exception {
        LocalDateTime hoy =  LocalDateTime.now();
        List<Spell> spells = spellService.getAllSpells();
        List<Ingredient> ingredients = ingredientService.getAllIngredients();

        Authorities auth = new Authorities();
		auth.setId(1);

        User fulanito = new User();
        fulanito.setId(1);
		fulanito.setAge("20");
		fulanito.setUsername("user");
		fulanito.setFirstName("firstName");
		fulanito.setLastName("lastName");
		fulanito.setEmail("user@gmail.com");
		fulanito.setPassword("password");

		Game game1 = new Game();
        game1.setId(2);
        game1.setName("Game One");
        game1.setCode("");
        game1.setRound(1);
        game1.setCreate(hoy);
        game1.setSpellsDeck(spells);
        game1.setIngredientsDeck(ingredients);
        game1.setUsers(List.of(fulanito));

		Game game2 = new Game();
        game2.setId(3);
        game2.setName("Game Two");
        game2.setCode("");
        game2.setRound(1);
        game2.setCreate(hoy);
        game2.setSpellsDeck(spells);
        game2.setIngredientsDeck(ingredients);
        game2.setUsers(List.of(fulanito));

		when(this.gameService.getAllGames()).thenReturn(List.of(game, game1, game2));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[?(@.id == 1)].name").value("Game"))
				.andExpect(jsonPath("$[?(@.id == 2)].name").value("Game One"))
				.andExpect(jsonPath("$[?(@.id == 3)].name").value("Game Two"));
	}

    @Test
	@WithMockUser("PLAYER")
    void shouldFindGameById() throws Exception{

		when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_GAME_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value(game.getName()));


    }

    @Test
	@WithMockUser("ADMIN")
	void shouldCreateGame() throws Exception {

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isCreated());
	}

    @Test
	@WithMockUser("ADMIN")
	void shouldNotAcceptableCreateGame() throws Exception {
        when(this.gameService.findCurrentGame(any(User.class))).thenReturn(game);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isNotAcceptable());
	}

	@Test
	@WithMockUser("ADMIN")
	void shouldUpdateGame() throws Exception {
        game.setName("Ha sido cambiada");
        
		when(this.gameService.findGameById(any(Integer.class))).thenReturn(game);
		when(this.gameService.updateGame(any(Game.class), any(Integer.class))).thenReturn(game);


        mockMvc.perform(put(BASE_URL + "/{gameId}", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(game))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ha sido cambiada"));
	}    

    @Test
	@WithMockUser("ADMIN")
	void shouldDeleteGame() throws Exception {

		when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);
		doNothing().when(this.gameService).deleteGame(TEST_GAME_ID);

		mockMvc.perform(delete(BASE_URL + "/{gameId}", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(game))).andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Game deleted!"));
	}

    @Test
    @WithMockUser("PLAYER")
    void shouldStartGame() throws Exception{

        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(put(BASE_URL+ "/{id}/start", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldSUnfeasibleGameStateStartGame() throws Exception{
        game.setStart(LocalDateTime.now());

        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(put(BASE_URL+ "/{id}/start", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldFindAllPlayerStatus() throws Exception{
        game.setStart(LocalDateTime.now());

        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(get(BASE_URL+ "/{id}/playerStatus", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldUnfeasibleGameStateFindAllPlayerStatus() throws Exception{
        game.setStart(null);

        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(get(BASE_URL+ "/{id}/playerStatus", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldFinishGame() throws Exception{
        game.setStart(LocalDateTime.now());
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(put(BASE_URL+ "/{id}/finish", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldUnfeasibleGameStateFinishGame() throws Exception{
        game.setStart(LocalDateTime.now());
        game.setFinish(LocalDateTime.now());
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(put(BASE_URL+ "/{id}/finish", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldAdvanceRound() throws Exception{
        game.setStart(LocalDateTime.now());
        game.setIngredientsDeck(this.ingredientService.getAllIngredients());
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(put(BASE_URL+ "/{id}/advance", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldUnfeasibleGameStateAdvanceRound() throws Exception{
        game.setStart(LocalDateTime.now());
        game.setIngredientsDeck(this.ingredientService.getAllIngredients());
        game.setRound(3);
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(put(BASE_URL+ "/{id}/advance", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldDiscardSpells() throws Exception{
        game.setStart(LocalDateTime.now());
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(put(BASE_URL+ "/{id}/discard", TEST_GAME_ID).with(csrf())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldUnfeasibleGameStateDiscardSpells() throws Exception{
        game.setStart(LocalDateTime.now());
        game.setFinish(LocalDateTime.now());
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(put(BASE_URL+ "/{id}/discard", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldShuffleSpells() throws Exception{
        game.setStart(LocalDateTime.now());
        game.setSpellsDeck(this.spellService.getAllSpells());
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(put(BASE_URL+ "/{id}/shuffle", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldUnfeasibleGameStateShuffleSpells() throws Exception{
        game.setStart(LocalDateTime.now());
        game.setFinish(LocalDateTime.now());
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game);

        mockMvc.perform(put(BASE_URL+ "/{id}/shuffle", TEST_GAME_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(game))).andExpect(status().isInternalServerError());
    }
    
    
}
