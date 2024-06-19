package org.springframework.samples.minuspocus.game;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.minuspocus.card.Ingredient;
import org.springframework.samples.minuspocus.card.IngredientService;
import org.springframework.samples.minuspocus.card.Spell;
import org.springframework.samples.minuspocus.card.SpellService;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.samples.minuspocus.user.UserService;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GameServiceTests {

    @Autowired
    private GameService gameService;

    @Autowired
	private UserService userService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private SpellService spellService;

    @Autowired
    private PlayerStatusService playerStatusService;

    @Test
    @Order(1)
    void shouldFindAllGames(){
        List<Game> games = (List<Game>) this.gameService.getAllGames();
        assertEquals(3, games.size());
    }

    @Test
    @Order(2)
    void shouldFindGameById(){
        Game game = this.gameService.findGameById(1);
        assertEquals("Los reales", game.getName());
    }

    @Test
    @Order(3)
    @Transactional
    void shouldFindUsersByParty(){
        User Mario = userService.findPlayerByUser(7);
        User Luigi = userService.findPlayerByUser(8);

        Game game = new Game();
        game.setName("Game");
        game.setCode("");
        game.setRound(1);
        game.setStart(null);
        game.setFinish(null);
        game.setCreate(LocalDateTime.now());
        game.setSpellsDeck(List.of());
        game.setIngredientsDeck(List.of());
        game.setUsers(List.of(Mario,Luigi));

        this.gameService.saveGame(game);
        Game createdGame = gameService.getAllGames().get(gameService.getAllGames().size() - 1);
        List<User> users = gameService.findUsersByParty(createdGame.getId());
        assertEquals(2, users.size());
    }

    @Test
    @Order(4)
    @Transactional
    void shouldInsertGame(){
        int count = ((Collection<Game>) this.gameService.getAllGames()).size();
        User Mario = userService.findPlayerByUser(7);
        User Luigi = userService.findPlayerByUser(8);

        Game game = new Game();
        game.setName("Game");
        game.setCode("");
        game.setRound(1);
        game.setStart(null);
        game.setFinish(null);
        game.setCreate(LocalDateTime.now());
        game.setSpellsDeck(List.of());
        game.setIngredientsDeck(List.of());
        game.setUsers(List.of(Mario,Luigi));

        this.gameService.saveGame(game);
        assertNotEquals(0, game.getId().longValue());
		assertNotNull(game.getId());

		int finalCount = ((Collection<Game>) this.gameService.getAllGames()).size();
		assertEquals(count + 1, finalCount);

    }

    @Test
    @Order(5)
	@Transactional
	void shouldUpdateGame() {
		Game game = this.gameService.findGameById(2);
		game.setName("Change");
		gameService.updateGame(game,2);
		game = this.gameService.findGameById(2);
		assertEquals("Change", game.getName());
	}

    @Test
    @Order(6)
    @Transactional
    void shouldDeleteGame(){
        Integer firstCount = ((Collection<Game>) gameService.getAllGames()).size();
        User Mario = userService.findPlayerByUser(7);
        User Luigi = userService.findPlayerByUser(8);

        Game game = new Game();
        game.setName("Game");
        game.setCode("");
        game.setRound(1);
        game.setStart(null);
        game.setFinish(null);
        game.setCreate(LocalDateTime.now());
        game.setSpellsDeck(List.of());
        game.setIngredientsDeck(List.of());
        game.setUsers(List.of(Mario,Luigi));

		this.gameService.saveGame(game);

		Integer secondCount = ((Collection<Game>) gameService.getAllGames()).size();
		assertEquals(firstCount + 1, secondCount);
        Game toDelete = gameService.getAllGames().get(gameService.getAllGames().size() - 1);
		gameService.deleteGame(toDelete.getId());
		Integer lastCount = ((Collection<Game>) gameService.getAllGames()).size();
		assertEquals(firstCount, lastCount);
    }

    @Test
    @Order(7)
    @Transactional
    void shouldFindCurrentGame(){
        User Wario = userService.findPlayerByUser(9);

        Game game = new Game();
        game.setName("Game");
        game.setCode("");
        game.setRound(1);
        game.setStart(null);
        game.setFinish(null);
        game.setCreate(LocalDateTime.now());
        game.setSpellsDeck(List.of());
        game.setIngredientsDeck(List.of());
        game.setUsers(List.of(Wario));

		this.gameService.saveGame(game);
        Game createdGame = gameService.getAllGames().get(gameService.getAllGames().size() - 1);
        Game userGame = gameService.findCurrentGame(Wario);
        assertEquals(createdGame, userGame);
    }
    
    @Test
    @Order(8)
    void shouldStartGame(){
        User Wario = userService.findPlayerByUser(9);

        Game game = new Game();
        game.setName("Game");
        game.setCode("");
        game.setRound(1);
        game.setStart(null);
        game.setFinish(null);
        game.setCreate(LocalDateTime.now());
        game.setSpellsDeck(List.of());
        game.setIngredientsDeck(List.of());
        game.setUsers(List.of(Wario));

		this.gameService.saveGame(game);
        List<Spell> spells = spellService.getAllSpells();
        List<Ingredient> ingredients = ingredientService.getAllIngredients();

        gameService.startGame(game, spells, ingredients);

        assertNotNull(game.getStart());
        assertEquals(0, game.getInvitations().size());
        assertEquals(65, game.getIngredientsDeck().size());
        assertEquals(spells.size(), game.getSpellsDeck().size());
        assertEquals(7, gameService.findAllPlayerStatus(game.getUsers()).get(0).getHand().size());
        assertEquals(game.getUsers().size(), gameService.findAllPlayerStatus(game.getUsers()).size());
    }

    @Test
    @Order(9)
    void shouldFindAllPlayerStatus(){
        User Wario = userService.findPlayerByUser(9);

        Game game = new Game();
        game.setName("Game");
        game.setCode("");
        game.setRound(1);
        game.setStart(null);
        game.setFinish(null);
        game.setCreate(LocalDateTime.now());
        game.setSpellsDeck(List.of());
        game.setIngredientsDeck(List.of());
        game.setUsers(List.of(Wario));

		this.gameService.saveGame(game);
        List<PlayerStatus> playerStatusList = playerStatusService.getAllPlayerStatus();
        PlayerStatus ps = playerStatusList.stream().filter(x -> x.getPlayer().getId().equals(Wario.getId())).findFirst().get();
        playerStatusService.deletePlayerStatus(ps);

        gameService.startGame(game, spellService.getAllSpells(), ingredientService.getAllIngredients());
        List<PlayerStatus> playerStatus = gameService.findAllPlayerStatus(game.getUsers());

        assertEquals(1, playerStatus.size());

    }

    @Test
    @Order(10)
    void shouldFinishGame(){
        User Wario = userService.findPlayerByUser(9);

        Game game = new Game();
        game.setName("Game");
        game.setCode("");
        game.setRound(1);
        game.setStart(null);
        game.setFinish(null);
        game.setCreate(LocalDateTime.now());
        game.setSpellsDeck(List.of());
        game.setIngredientsDeck(List.of());
        game.setUsers(List.of(Wario));

		this.gameService.saveGame(game);
        List<PlayerStatus> playerStatusList = playerStatusService.getAllPlayerStatus();
        PlayerStatus ps = playerStatusList.stream().filter(x -> x.getPlayer().getId().equals(Wario.getId())).findFirst().get();
        playerStatusService.deletePlayerStatus(ps);

        gameService.startGame(game, spellService.getAllSpells(), ingredientService.getAllIngredients());
        assertNotNull(gameService.finishGame(game).getFinish());

    }

    @Test
    @Order(11)
    void shouldAdvanceRound(){
        User Wario = userService.findPlayerByUser(9);

        Game game = new Game();
        game.setName("Game");
        game.setCode("");
        game.setRound(1);
        game.setStart(null);
        game.setFinish(null);
        game.setCreate(LocalDateTime.now());
        game.setSpellsDeck(List.of());
        game.setIngredientsDeck(List.of());
        game.setUsers(List.of(Wario));

		this.gameService.saveGame(game);
        
        gameService.advanceRound(game, spellService.getAllSpells(), ingredientService.getAllIngredients());
        assertEquals(2, game.getRound());
        assertEquals(36, game.getSpellsDeck().size());
        assertEquals(72, game.getIngredientsDeck().size());
    }

    @Test
    @Order(12)
    void shouldDiscardSpells(){
        User Wario = userService.findPlayerByUser(9);

        Game game = new Game();
        game.setName("Game");
        game.setCode("");
        game.setRound(1);
        game.setStart(null);
        game.setFinish(null);
        game.setCreate(LocalDateTime.now());
        game.setSpellsDeck(List.of());
        game.setIngredientsDeck(List.of());
        game.setUsers(List.of(Wario));

		this.gameService.saveGame(game);

        gameService.startGame(game, spellService.getAllSpells(), ingredientService.getAllIngredients());
        PlayerStatus ps = gameService.findAllPlayerStatus(game.getUsers()).get(0);
        playerStatusService.voteDiscard(ps);
        
        Integer count = game.getSpellsDeck().size();
        gameService.discardSpells(game, gameService.findAllPlayerStatus(game.getUsers()));
        assertFalse(gameService.findAllPlayerStatus(game.getUsers()).get(0).getVote());
        assertEquals(count-3, game.getSpellsDeck().size());
    }

    @Test
    @Order(13)
    void shouldShuffleSpells(){
        User Wario = userService.findPlayerByUser(9);

        Game game = new Game();
        game.setName("Game");
        game.setCode("");
        game.setRound(1);
        game.setStart(null);
        game.setFinish(null);
        game.setCreate(LocalDateTime.now());
        game.setSpellsDeck(List.of());
        game.setIngredientsDeck(List.of());
        game.setUsers(List.of(Wario));

        gameService.saveGame(game);
        List<PlayerStatus> playerStatusList = playerStatusService.getAllPlayerStatus();
        PlayerStatus ps = playerStatusList.stream().filter(x -> x.getPlayer().getId().equals(Wario.getId())).findFirst().get();
        playerStatusService.deletePlayerStatus(ps);

        gameService.startGame(game, spellService.getAllSpells(), ingredientService.getAllIngredients());

        Integer spellGame1 = game.getSpellsDeck().get(0).getId();
        Integer spellGame2 = game.getSpellsDeck().get(1).getId();
        Integer spellGame3 = game.getSpellsDeck().get(2).getId();
        List<Integer> spellList = List.of(spellGame1,spellGame2,spellGame3);
        
        gameService.shuffleSpells(game, spellService.getAllSpells());
        Integer spellShuffleGame1 = game.getSpellsDeck().get(0).getId();
        Integer spellShuffleGame2 = game.getSpellsDeck().get(1).getId();
        Integer spellShuffleGame3 = game.getSpellsDeck().get(2).getId();
        List<Integer> spellListShuffle = List.of(spellShuffleGame1,spellShuffleGame2,spellShuffleGame3);
        assertNotEquals(spellList, spellListShuffle);
    }
}
