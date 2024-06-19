package org.springframework.samples.minuspocus.spell;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import org.springframework.http.MediaType;
import org.springframework.samples.minuspocus.card.CastDTO;
import org.springframework.samples.minuspocus.card.DiscardDTO;
import org.springframework.samples.minuspocus.card.Ingredient;
import org.springframework.samples.minuspocus.card.Spell;
import org.springframework.samples.minuspocus.card.SpellController;
import org.springframework.samples.minuspocus.card.SpellService;
import org.springframework.samples.minuspocus.card.effect.Effect;
import org.springframework.samples.minuspocus.card.ingredientType.IngredientType;
import org.springframework.samples.minuspocus.configuration.SecurityConfiguration;
import org.springframework.samples.minuspocus.game.Game;
import org.springframework.samples.minuspocus.game.GameService;
import org.springframework.samples.minuspocus.game.PlayerStatus;
import org.springframework.samples.minuspocus.game.PlayerStatusService;
import org.springframework.samples.minuspocus.game.color.Color;
import org.springframework.samples.minuspocus.user.Authorities;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = SpellController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)

public class SpellControllerTests {
    
    private static final int TEST_SPELL_ID = 1;

	private static final String BASE_URL = "/api/v1/spells";

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("unused")
    @Autowired
	private SpellController spellController;

    @Autowired
	private ObjectMapper objectMapper;

    @MockBean
	private SpellService spellService;

    @MockBean
	private GameService gameService;

    @MockBean
	private PlayerStatusService playerStatusService;

    private Spell spell;
    private Ingredient i1;
    private Ingredient i2;
    private Ingredient i3;
    private User user1;
    private User user2;
    private PlayerStatus ps1;
    private PlayerStatus ps2;
    private CastDTO cast;
    private Game g;
    private DiscardDTO discard;

    @BeforeEach
    void setup(){
        spell = new Spell();
        spell.setId(TEST_SPELL_ID);
        spell.setValuable(10);
        spell.setEffect1(Effect.HURT);
        spell.setTarget(true);
        
        i1 = new Ingredient();
        i1.setId(1);
        i1.setValuable(2);
        i1.setType(IngredientType.BASE);
        i2 = new Ingredient();
        i2.setId(2);
        i2.setValuable(3);
        i2.setType(IngredientType.BASE);
        i3 = new Ingredient();
        i3.setId(3);
        i3.setValuable(5);
        i3.setType(IngredientType.BASE);

        Authorities auth = new Authorities();
		auth.setId(1);

        user1 = new User();
        user1.setId(1);
		user1.setAge("20");
		user1.setUsername("user1");
		user1.setFirstName("firstName");
		user1.setLastName("lastName");
		user1.setEmail("user@gmail.com");
		user1.setPassword("password");
        user1.setAchievement(List.of());
        user1.setAuthority(auth);

        user2 = new User();
        user2.setId(2);
		user2.setAge("20");
		user2.setUsername("user2");
		user2.setFirstName("firstName");
		user2.setLastName("lastName");
		user2.setEmail("user@gmail.com");
		user2.setPassword("password");
        user2.setAchievement(List.of());
        user2.setAuthority(auth);

        ps1 = new PlayerStatus();
        ps1.setId(1);
        ps1.setBarrier(0);
        ps1.setColor(Color.BLUE);
        ps1.setHand(List.of(i1,i2,i3));
        ps1.setPlayer(user1);
        ps1.setRabbits(0);
        ps1.setVote(false);
        ps1.setW1(2);
        ps1.setW2(2);
        ps1.setW3(2);
        ps1.setW4(2);

        ps2 = new PlayerStatus();
        ps2.setId(2);
        ps2.setBarrier(0);
        ps2.setColor(Color.BLUE);
        ps2.setHand(List.of());
        ps2.setPlayer(user2);
        ps2.setRabbits(0);
        ps2.setVote(false);
        ps2.setW1(2);
        ps2.setW2(2);
        ps2.setW3(2);
        ps2.setW4(2);

        cast = new CastDTO();
        cast.setIngredients(List.of(i1,i2,i3));
        cast.setPsIds(List.of(ps1.getId(), ps2.getId()));

        g = new Game();
        g.setId(1);
        g.setCode("");
        g.setCreate(LocalDateTime.now());
        g.setStart(LocalDateTime.now());
        g.setIngredientsDeck(List.of());
        g.setName("Game");
        g.setRound(1);
        g.setSpellsDeck(List.of(spell));
        g.setUsers(List.of(user1, user2));

        discard = new DiscardDTO();
        discard.setGameId(g.getId());
        discard.setIngredients(List.of(i1,i2,i3));
        discard.setPlayerStatusId(ps1.getId());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldCastSpellWithTarget() throws Exception {
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(ps1.getId())).thenReturn(ps1);
        when(this.playerStatusService.findPlayerStatusById(ps2.getId())).thenReturn(ps2);
        when(this.gameService.findCurrentGame(user1)).thenReturn(g);
        when(this.playerStatusService.castSpell(ps1, ps2, spell, g, cast.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/cast", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(cast))).andExpect(status().isOk())
			    .andExpect(jsonPath("$.id").value(ps1.getId()))
			    .andExpect(jsonPath("$.player").value(ps1.getPlayer()));
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldCastSpellWithoutTarget() throws Exception {
        cast.setPsIds(List.of(ps1.getId()));
        spell.setTarget(false);
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(ps1.getId())).thenReturn(ps1);
        when(this.gameService.findCurrentGame(user1)).thenReturn(g);
        when(this.playerStatusService.castSpell(ps1, ps2, spell, g, cast.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/cast", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(cast))).andExpect(status().isOk())
			    .andExpect(jsonPath("$.id").value(ps1.getId()))
			    .andExpect(jsonPath("$.player").value(ps1.getPlayer()));
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotCastSpellIfNotValidCast() throws Exception {
        cast.setIngredients(List.of());
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(ps1.getId())).thenReturn(ps1);
        when(this.playerStatusService.findPlayerStatusById(ps2.getId())).thenReturn(ps2);
        when(this.gameService.findCurrentGame(user1)).thenReturn(g);
        when(this.playerStatusService.castSpell(ps1, ps2, spell, g, cast.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/cast", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(cast))).andExpect(status().isBadRequest());

        cast.setPsIds(List.of());
        cast.setPsIds(List.of(user1.getId(), user2.getId()));
        mockMvc.perform(put(BASE_URL + "/{id}/cast", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(cast))).andExpect(status().isBadRequest());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotCastSpellWithTarget() throws Exception {
        cast.setPsIds(List.of(ps1.getId()));
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(ps1.getId())).thenReturn(ps1);
        when(this.playerStatusService.findPlayerStatusById(ps2.getId())).thenReturn(ps2);
        when(this.gameService.findCurrentGame(user1)).thenReturn(g);
        when(this.playerStatusService.castSpell(ps1, ps2, spell, g, cast.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/cast", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(cast))).andExpect(status().isInternalServerError());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotCastSpellWithoutTarget() throws Exception {
        spell.setTarget(false);
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(ps1.getId())).thenReturn(ps1);
        when(this.gameService.findCurrentGame(user1)).thenReturn(g);
        when(this.playerStatusService.castSpell(ps1, ps2, spell, g, cast.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/cast", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(cast))).andExpect(status().isInternalServerError());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotCastSpellNotInTable() throws Exception {
        g.setSpellsDeck(List.of());
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(ps1.getId())).thenReturn(ps1);
        when(this.playerStatusService.findPlayerStatusById(ps2.getId())).thenReturn(ps2);
        when(this.gameService.findCurrentGame(user1)).thenReturn(g);
        when(this.playerStatusService.castSpell(ps1, ps2, spell, g, cast.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/cast", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(cast))).andExpect(status().isInternalServerError());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotCastSpellIfGameNotStarted() throws Exception {
        g.setStart(null);
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(ps1.getId())).thenReturn(ps1);
        when(this.playerStatusService.findPlayerStatusById(ps2.getId())).thenReturn(ps2);
        when(this.gameService.findCurrentGame(user1)).thenReturn(g);
        when(this.playerStatusService.castSpell(ps1, ps2, spell, g, cast.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/cast", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(cast))).andExpect(status().isInternalServerError());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotCastSpellIfGameFinished() throws Exception {
        g.setFinish(LocalDateTime.now());
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(ps1.getId())).thenReturn(ps1);
        when(this.playerStatusService.findPlayerStatusById(ps2.getId())).thenReturn(ps2);
        when(this.gameService.findCurrentGame(user1)).thenReturn(g);
        when(this.playerStatusService.castSpell(ps1, ps2, spell, g, cast.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/cast", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(cast))).andExpect(status().isInternalServerError());
    }

    @Test
	@WithMockUser("PLAYER")
	void shouldNotCastSpellIfIngredientsNotInHand() throws Exception {
        ps1.setHand(List.of(i1,i2));
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(ps1.getId())).thenReturn(ps1);
        when(this.playerStatusService.findPlayerStatusById(ps2.getId())).thenReturn(ps2);
        when(this.gameService.findCurrentGame(user1)).thenReturn(g);
        when(this.playerStatusService.castSpell(ps1, ps2, spell, g, cast.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/cast", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(cast))).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldDiscardSpell() throws Exception {
        when(this.gameService.findGameById(discard.getGameId())).thenReturn(g);
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(discard.getPlayerStatusId())).thenReturn(ps1);
        when(this.spellService.discardSpell(spell, g, ps1, discard.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/discard", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(discard))).andExpect(status().isOk())
			    .andExpect(jsonPath("$.id").value(ps1.getId()))
			    .andExpect(jsonPath("$.player").value(ps1.getPlayer()));
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldNotDiscardSpellIfGameNotStarted() throws Exception {
        g.setStart(null);
        when(this.gameService.findGameById(discard.getGameId())).thenReturn(g);
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(discard.getPlayerStatusId())).thenReturn(ps1);
        when(this.spellService.discardSpell(spell, g, ps1, discard.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/discard", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(discard))).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldNotDiscardSpellIfGameFinished() throws Exception {
        g.setFinish(LocalDateTime.now());
        when(this.gameService.findGameById(discard.getGameId())).thenReturn(g);
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(discard.getPlayerStatusId())).thenReturn(ps1);
        when(this.spellService.discardSpell(spell, g, ps1, discard.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/discard", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(discard))).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldNotDiscardSpellNotInTable() throws Exception {
        g.setSpellsDeck(List.of());
        when(this.gameService.findGameById(discard.getGameId())).thenReturn(g);
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(discard.getPlayerStatusId())).thenReturn(ps1);
        when(this.spellService.discardSpell(spell, g, ps1, discard.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/discard", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(discard))).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("PLAYER")
    void shouldNotDiscardSpellIfIngredientsNotInHand() throws Exception {
        ps1.setHand(List.of(i1,i2));
        when(this.gameService.findGameById(discard.getGameId())).thenReturn(g);
        when(this.spellService.findSpellById(TEST_SPELL_ID)).thenReturn(spell);
        when(this.playerStatusService.findPlayerStatusById(discard.getPlayerStatusId())).thenReturn(ps1);
        when(this.spellService.discardSpell(spell, g, ps1, discard.getIngredients())).thenReturn(ps1);

        mockMvc.perform(put(BASE_URL + "/{id}/discard", TEST_SPELL_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(discard))).andExpect(status().isInternalServerError());
    }
}
