package org.springframework.samples.minuspocus.spell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.minuspocus.card.Ingredient;
import org.springframework.samples.minuspocus.card.IngredientService;
import org.springframework.samples.minuspocus.card.Spell;
import org.springframework.samples.minuspocus.card.SpellService;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.game.Game;
import org.springframework.samples.minuspocus.game.GameService;
import org.springframework.samples.minuspocus.game.PlayerStatus;

@SpringBootTest
@AutoConfigureTestDatabase
class SpellServiceTests {
    
    @Autowired
    private SpellService spellService;

    @Autowired
    private GameService gameService;

    @Autowired
    private IngredientService ingredientService;

    @Test
    void shouldFindAllSpells() {
        List<Spell> spells = spellService.getAllSpells();
        assertEquals(36, spells.size());
    }

    @Test
    void shouldFindSpellById() {
        Integer id = 5;
        Spell spell = spellService.findSpellById(id);
        assertEquals(id, spell.getId());
    }

    @Test
    void shouldNotFindSpellById() {
        Integer id = 40;
        assertThrows(ResourceNotFoundException.class, () -> spellService.findSpellById(id));
    }

    @Test
    void shouldDiscardSpellById() {
        Integer id = 3;
        gameService.startGame(gameService.findGameById(id), spellService.getAllSpells(), ingredientService.getAllIngredients());
        Game game = gameService.findGameById(id);
        assertEquals(36, game.getSpellsDeck().size());

        Spell spell = game.getSpellsDeck().get(0);
        PlayerStatus oldPs = gameService.findAllPlayerStatus(game.getUsers()).get(0);
        oldPs.setRabbits(2);
        List<Ingredient> oldHand = oldPs.getHand();
        List<Ingredient> usedIngredients = oldHand.subList(0, 3);
        PlayerStatus newPs = spellService.discardSpell(spell, game, oldPs, usedIngredients);

        assertEquals(35, game.getSpellsDeck().size());
        assertEquals(5, newPs.getHand().size());
        
        List<Integer> usedIds = usedIngredients.stream().map(ing -> ing.getId()).toList();
        for(int i = 0; i < newPs.getHand().size(); i++){
            Integer ing = newPs.getHand().get(i).getId();
            assertTrue(!usedIds.contains(ing));
        }

        newPs.setRabbits(4);
        usedIngredients = newPs.getHand().subList(0, 3);
        newPs = spellService.discardSpell(game.getSpellsDeck().get(0), game, oldPs, usedIngredients);
        assertEquals(4, newPs.getHand().size());
    }

}
