package org.springframework.samples.minuspocus.game;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.minuspocus.auth.payload.response.MessageResponse;
import org.springframework.samples.minuspocus.card.Ingredient;
import org.springframework.samples.minuspocus.card.IngredientService;
import org.springframework.samples.minuspocus.card.Spell;
import org.springframework.samples.minuspocus.card.SpellService;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.samples.minuspocus.util.RestPreconditions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/game")
@Tag(name="Games", description = "API for the management of Game")
@SecurityRequirement(name = "BearerAuth")
public class GameController { 
    
    private GameService gameService;
    private SpellService spellService;
    private IngredientService ingredientService;

    @Autowired
    public GameController(GameService gameService, SpellService spellService, IngredientService ingredientService){
        this.gameService = gameService;
        this.spellService = spellService;
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public List<Game> getAllGames(){
        return gameService.getAllGames();
    }

    @GetMapping(value = "{id}")
	public ResponseEntity<Game> findById(@PathVariable("id") Integer id) {
		return new ResponseEntity<>(gameService.findGameById(id), HttpStatus.OK);
	}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Game> create(@RequestBody @Valid Game game) {
        List<User> players = game.getUsers();
    
        for(int i = 0; i < players.size(); i++){
            User player = players.get(i);

            if(gameService.findCurrentGame(player) != null){
                return new ResponseEntity<>(game, HttpStatus.NOT_ACCEPTABLE);
            }
        }

		Game savedGame = gameService.saveGame(game);
		return new ResponseEntity<>(savedGame, HttpStatus.CREATED);
	}

    @PutMapping(value = "{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> update(@PathVariable("gameId") Integer id, @RequestBody @Valid Game game){
        RestPreconditions.checkNotNull(gameService.findGameById(id), "Game", "ID", game);
        return new ResponseEntity<>(this.gameService.updateGame(game, id), HttpStatus.OK);
    }

    @DeleteMapping(value = "{gameId}") 
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> delete(@PathVariable("gameId") Integer id, @RequestBody @Valid Game game){
        RestPreconditions.checkNotNull(gameService.findGameById(id), "Game", "ID", game);
        this.gameService.deleteGame(id);
        return new ResponseEntity<>(new MessageResponse("Game deleted!"),HttpStatus.OK);
    }

    @PutMapping("/{id}/start")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> startGame(@PathVariable("id") int id) throws UnfeasibleGameState {
        Game game = gameService.findGameById(id);
        if(game.getStart() != null || game.getFinish() != null || game.getUsers().size() < 2){
            throw new UnfeasibleGameState();
        }
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        List<Spell> spells = spellService.getAllSpells();
        return new ResponseEntity<>(gameService.startGame(game, spells, ingredients), HttpStatus.OK);
    }

    @GetMapping(value = "{id}/playerStatus")
    @ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<PlayerStatus>> findAllPlayerStatus(@PathVariable("id") Integer id) throws UnfeasibleGameState {
        Game game = gameService.findGameById(id);
        if(game.start == null || game.finish != null){
            throw new UnfeasibleGameState();
        }
		return new ResponseEntity<>(gameService.findAllPlayerStatus(game.getUsers()), HttpStatus.OK);
	}

    @PutMapping("/{id}/finish")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> finishGame(@PathVariable("id") int id) throws UnfeasibleGameState {
        Game game = gameService.findGameById(id);
        if(game.start == null || game.finish != null){
            throw new UnfeasibleGameState();
        }

        return new ResponseEntity<>(gameService.finishGame(game), HttpStatus.OK);
    }

    @PutMapping("/{id}/advance") 
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> advanceRound(@PathVariable("id") int id) throws UnfeasibleGameState {
        Game game = gameService.findGameById(id);
        if(game.start == null || game.finish != null || game.round > 2 || game.getIngredientsDeck().size() > 0){
            throw new UnfeasibleGameState();
        }
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        List<Spell> spells = spellService.getAllSpells();
        return new ResponseEntity<>(gameService.advanceRound(game, spells, ingredients), HttpStatus.OK);
    } 

    @PutMapping("/{id}/discard")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> discardSpells(@PathVariable("id") int id) throws UnfeasibleGameState {
        Game game = gameService.findGameById(id);
        List<PlayerStatus> status = gameService.findAllPlayerStatus(game.getUsers());
        if(game.start == null || game.finish != null || (status.stream().anyMatch(ps -> ps.getW1()+ps.getW2()+ps.getW3()+ps.getW4()>0 && !ps.getVote())) ){
            throw new UnfeasibleGameState();
        }

        return new ResponseEntity<>(gameService.discardSpells(game, status), HttpStatus.OK);
    }

    @PutMapping("/{id}/shuffle")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> shuffleSpells(@PathVariable("id") int id) throws UnfeasibleGameState {
        Game game = gameService.findGameById(id);
        if(game.start == null || game.finish != null || game.getSpellsDeck().size() > 0){
            throw new UnfeasibleGameState();
        }
        List<Spell> spells = spellService.getAllSpells();
        return new ResponseEntity<>(gameService.shuffleSpells(game, spells), HttpStatus.OK);
    }
    
}
