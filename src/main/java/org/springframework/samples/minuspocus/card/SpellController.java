package org.springframework.samples.minuspocus.card;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.minuspocus.game.Game;
import org.springframework.samples.minuspocus.game.GameService;
import org.springframework.samples.minuspocus.game.PlayerStatus;
import org.springframework.samples.minuspocus.game.PlayerStatusService;
import org.springframework.samples.minuspocus.game.UnfeasibleGameState;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/spells")
@Tag(name="Spells", description = "API for the spells")
@SecurityRequirement(name = "BearerAuth")
public class SpellController {

    private SpellService spellService;
    private GameService gameService;
    private PlayerStatusService playerStatusService;

    @Autowired
    public SpellController(SpellService spellService, GameService gameService, PlayerStatusService playerStatusService){
        this.spellService = spellService;
        this.gameService = gameService;
        this.playerStatusService = playerStatusService;
    }

    @PutMapping("/{id}/cast")
    public ResponseEntity<PlayerStatus> castSpell(@PathVariable("id") int id, @RequestBody @Valid CastDTO cast) throws UnfeasibleCast {
        List<Integer> psIds = cast.getPsIds();
        List<Ingredient> ingredients = cast.getIngredients();
        Spell spell = spellService.findSpellById(id);
        if(!((psIds.size() == 2 && spell.getTarget()) || (psIds.size() == 1 && !spell.getTarget()))){
            throw new UnfeasibleCast();
        }
        PlayerStatus caster = playerStatusService.findPlayerStatusById(psIds.get(0));
        PlayerStatus target = psIds.size() == 2? playerStatusService.findPlayerStatusById(psIds.get(1)): caster;
        Game game = gameService.findCurrentGame(caster.getPlayer());

        Integer max = game.getSpellsDeck().size() >= 3? 3: game.getSpellsDeck().size();
        if(game.getId() != gameService.findCurrentGame(target.getPlayer()).getId() || !game.getSpellsDeck().subList(0,max).stream().map(s->s.getId()).toList().contains(spell.getId()) || 
            game.getStart() == null || game.getFinish() != null || !caster.getHand().stream().map(i->i.getId()).toList().containsAll(ingredients.stream().map(i->i.getId()).toList())){
            throw new UnfeasibleCast();
        }
        
        return new ResponseEntity<>(playerStatusService.castSpell(caster, target, spell, game, ingredients), HttpStatus.OK);
    }

    @PutMapping("/{id}/discard")
    public ResponseEntity<PlayerStatus> discardSpell(@PathVariable("id") int id, @RequestBody @Valid DiscardDTO discard) throws UnfeasibleGameState {
        Integer gameId = discard.getGameId();
        Integer playerStatusId = discard.getPlayerStatusId();
        List<Ingredient> ingredients = discard.getIngredients();

        Game game = gameService.findGameById(gameId);
        Spell spell = spellService.findSpellById(id);
        PlayerStatus status = playerStatusService.findPlayerStatusById(playerStatusId);

        Integer max = game.getSpellsDeck().size() >= 3? 3: game.getSpellsDeck().size();
        if(game.getStart() == null || game.getFinish() != null || !game.getSpellsDeck().subList(0,max).stream().map(s->s.getId()).toList().contains(spell.getId()) || 
            !status.getHand().stream().map(i->i.getId()).toList().containsAll(ingredients.stream().map(i->i.getId()).toList())){
            throw new UnfeasibleGameState();
        }
        
        return new ResponseEntity<>(spellService.discardSpell(spell, game, status, ingredients), HttpStatus.OK);
    }
    
}
