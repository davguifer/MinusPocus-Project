package org.springframework.samples.minuspocus.playerStatus;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
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
import org.springframework.samples.minuspocus.card.effect.Effect;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.game.Game;
import org.springframework.samples.minuspocus.game.GameService;
import org.springframework.samples.minuspocus.game.PlayerStatus;
import org.springframework.samples.minuspocus.game.PlayerStatusService;

@SpringBootTest
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlayerStatusServiceTests {
    
    @Autowired
    private PlayerStatusService playerStatusService;

    @Autowired
    private GameService gameService;

    @Autowired
    private IngredientService ingredientService;

    @Test
    @Order(1)
    void shouldFindAllPlayerStatus() {
        gameService.startGame(gameService.findGameById(3), List.of(), ingredientService.getAllIngredients());
        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        assertEquals(4,pss.size());
    }

    @Test
    @Order(2)
    void shouldFindPlayerStatusById() {
        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        Integer id = pss.get(0).getId();
        assertEquals(id, playerStatusService.findPlayerStatusById(id).getId());
    }

    @Test
    @Order(3)
    void shouldNotFindPlayerStatusById() {
        
        assertThrows(ResourceNotFoundException.class, () -> playerStatusService.findPlayerStatusById(0));
    }

    @Test
    @Order(4)
    void shouldVoteDiscard() {
        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus ps = pss.get(0);
        assertEquals(false, ps.getVote());
        playerStatusService.voteDiscard(ps);
        assertEquals(true, ps.getVote());
        playerStatusService.voteDiscard(ps);
        assertEquals(false, ps.getVote());
    }

    @Test
    @Order(5)
    void shouldPunishPlayer() {
        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus ps = pss.get(0);
        playerStatusService.punishPlayer(ps);
        assertEquals(1, ps.getW1());
        playerStatusService.punishPlayer(ps);
        assertEquals(1, ps.getW2());
        playerStatusService.punishPlayer(ps);
        assertEquals(1, ps.getW3());
        playerStatusService.punishPlayer(ps);
        assertEquals(1, ps.getW4());
        playerStatusService.punishPlayer(ps);
        assertEquals(0, ps.getW1());
        playerStatusService.punishPlayer(ps);
        assertEquals(0, ps.getW2());
        playerStatusService.punishPlayer(ps);
        assertEquals(0, ps.getW3());
        playerStatusService.punishPlayer(ps);
        assertEquals(0, ps.getW4());
    }

    @Test
    @Order(6)
    void shouldDiscardIngredients() {
        Game game = gameService.findGameById(3);
        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus ps = pss.get(1);
        ps.setRabbits(2);
        List<Integer> oldHand = ps.getHand().stream().map(i -> i.getId()).toList();

        playerStatusService.discardIngredients(game, ps);
        assertEquals(1, ps.getW1());
        assertEquals(5, ps.getHand().size());
        assertEquals(39, game.getIngredientsDeck().size());

        List<Integer> newHand = ps.getHand().stream().map(i -> i.getId()).toList();
        List<Integer> common = new ArrayList<>(oldHand);
        common.retainAll(newHand);
        assertEquals(0,common.size());

        ps.setRabbits(4);
        playerStatusService.discardIngredients(game, ps);
        assertEquals(1, ps.getW2());
        assertEquals(4, ps.getHand().size());
    }

    @Test
    @Order(7)
    void shouldCastHeal() {
        Spell spell = new Spell();
        spell.setEffect1(Effect.HEAL);
        spell.setId(4);
        spell.setTarget(false);
        spell.setValuable(4);
        Game game = gameService.findGameById(3);
        List<Spell> deck = new ArrayList<>();
        deck.add(spell);
        game.setSpellsDeck(deck);

        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus ps = pss.get(2);
        ps.setW1(1);
        List<Ingredient> usedIngredients = ps.getHand().subList(0, 1);
        Integer ingId = usedIngredients.get(0).getId();

        playerStatusService.castSpell(ps, ps, spell, game, usedIngredients);
        assertEquals(2, ps.getW1());
        assertEquals(7, ps.getHand().size());
        assertTrue(!ps.getHand().stream().map(i -> i.getId()).toList().contains(ingId));
        assertEquals(0, game.getSpellsDeck().size());
    }

    @Test
    @Order(8)
    void shouldCastShoo() {
        Spell spell = new Spell();
        spell.setEffect1(Effect.SHOO);
        spell.setId(2);
        spell.setTarget(false);
        spell.setValuable(2);
        Game game = gameService.findGameById(3);
        List<Spell> deck = new ArrayList<>();
        deck.add(spell);
        game.setSpellsDeck(deck);

        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus ps = pss.get(2);
        ps.setRabbits(2);
        List<Ingredient> usedIngredients = ps.getHand().subList(0, 1);
        Integer ingId = usedIngredients.get(0).getId();

        playerStatusService.castSpell(ps, ps, spell, game, usedIngredients);
        assertEquals(1, ps.getRabbits());
        assertEquals(6, ps.getHand().size());
        assertTrue(!ps.getHand().stream().map(i -> i.getId()).toList().contains(ingId));
        assertEquals(0, game.getSpellsDeck().size());
    }

    @Test
    @Order(9)
    void shouldCastBarrier() {
        Spell spell = new Spell();
        spell.setEffect1(Effect.BARRIER);
        spell.setId(1);
        spell.setTarget(false);
        spell.setValuable(1);
        Game game = gameService.findGameById(3);
        List<Spell> deck = new ArrayList<>();
        deck.add(spell);
        game.setSpellsDeck(deck);

        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus ps = pss.get(2);
        List<Ingredient> usedIngredients = ps.getHand().subList(0, 1);
        Integer ingId = usedIngredients.get(0).getId();

        playerStatusService.castSpell(ps, ps, spell, game, usedIngredients);
        assertEquals(1, ps.getBarrier());
        assertEquals(6, ps.getHand().size());
        assertTrue(!ps.getHand().stream().map(i -> i.getId()).toList().contains(ingId));
        assertEquals(0, game.getSpellsDeck().size());
    }

    @Test
    @Order(10)
    void shouldCastStun() {
        Spell spell = new Spell();
        spell.setEffect1(Effect.STUN);
        spell.setId(6);
        spell.setTarget(true);
        spell.setValuable(6);
        Game game = gameService.findGameById(3);
        List<Spell> deck = new ArrayList<>();
        deck.add(spell);
        game.setSpellsDeck(deck);

        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus psc = pss.get(2);
        List<Ingredient> usedIngredients = psc.getHand().subList(0, 1);
        Integer ingId = usedIngredients.get(0).getId();
        PlayerStatus pst = pss.get(3);

        playerStatusService.castSpell(psc, pst, spell, game, usedIngredients);
        assertEquals(1, pst.getW1());
        assertEquals(6, psc.getHand().size());
        assertTrue(!psc.getHand().stream().map(i -> i.getId()).toList().contains(ingId));
        assertEquals(0, game.getSpellsDeck().size());
    }

    @Test
    @Order(11)
    void shouldCastHurt() {
        Spell spell = new Spell();
        spell.setEffect1(Effect.HURT);
        spell.setId(5);
        spell.setTarget(true);
        spell.setValuable(5);
        Game game = gameService.findGameById(3);
        List<Spell> deck = new ArrayList<>();
        deck.add(spell);
        game.setSpellsDeck(deck);

        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus psc = pss.get(2);
        List<Ingredient> usedIngredients = psc.getHand().subList(0, 1);
        Integer ingId = usedIngredients.get(0).getId();
        PlayerStatus pst = pss.get(3);

        playerStatusService.castSpell(psc, pst, spell, game, usedIngredients);
        assertEquals(0, pst.getW2());
        assertEquals(6, psc.getHand().size());
        assertTrue(!psc.getHand().stream().map(i -> i.getId()).toList().contains(ingId));
        assertEquals(0, game.getSpellsDeck().size());
    }

    @Test
    @Order(12)
    void shouldCastRabbits() {
        Spell spell = new Spell();
        spell.setEffect1(Effect.RABBITS);
        spell.setId(3);
        spell.setTarget(true);
        spell.setValuable(3);
        Game game = gameService.findGameById(3);
        List<Spell> deck = new ArrayList<>();
        deck.add(spell);
        game.setSpellsDeck(deck);

        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus psc = pss.get(2);
        List<Ingredient> usedIngredients = psc.getHand().subList(0, 1);
        Integer ingId = usedIngredients.get(0).getId();
        PlayerStatus pst = pss.get(3);

        playerStatusService.castSpell(psc, pst, spell, game, usedIngredients);
        assertEquals(1, pst.getRabbits());
        assertEquals(6, psc.getHand().size());
        assertTrue(!psc.getHand().stream().map(i -> i.getId()).toList().contains(ingId));
        assertEquals(0, game.getSpellsDeck().size());
    }

    @Test
    @Order(13)
    void shouldCastDoubleEffect() {
        Spell spell = new Spell();
        spell.setEffect1(Effect.STUN);
        spell.setEffect2(Effect.BARRIER);
        spell.setId(30);
        spell.setTarget(true);
        spell.setValuable(52);
        Game game = gameService.findGameById(3);
        List<Spell> deck = new ArrayList<>();
        deck.add(spell);
        game.setSpellsDeck(deck);

        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus psc = pss.get(2);
        List<Ingredient> usedIngredients = psc.getHand().subList(0, 1);
        Integer ingId = usedIngredients.get(0).getId();
        PlayerStatus pst = pss.get(3);

        playerStatusService.castSpell(psc, pst, spell, game, usedIngredients);
        assertEquals(0, pst.getW1());
        assertEquals(2, psc.getBarrier());
        assertEquals(6, psc.getHand().size());
        assertTrue(!psc.getHand().stream().map(i -> i.getId()).toList().contains(ingId));
        assertEquals(0, game.getSpellsDeck().size());
    }

    @Test
    @Order(14)
    void shouldCastDontAffectBarrier() {
        Spell spell = new Spell();
        spell.setEffect1(Effect.HURT);
        spell.setEffect2(Effect.HURT);
        spell.setId(32);
        spell.setTarget(true);
        spell.setValuable(59);
        Game game = gameService.findGameById(3);
        List<Spell> deck = new ArrayList<>();
        deck.add(spell);
        game.setSpellsDeck(deck);

        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus psc = pss.get(3);
        List<Ingredient> usedIngredients = psc.getHand().subList(0, 1);
        Integer ingId = usedIngredients.get(0).getId();
        PlayerStatus pst = pss.get(2);

        playerStatusService.castSpell(psc, pst, spell, game, usedIngredients);
        assertEquals(2, pst.getW1());
        assertEquals(2, pst.getW2());
        assertEquals(2, pst.getW3());
        assertEquals(2, pst.getW4());
        assertEquals(1, pst.getBarrier());
        assertEquals(6, psc.getHand().size());
        assertTrue(!psc.getHand().stream().map(i -> i.getId()).toList().contains(ingId));
        assertEquals(0, game.getSpellsDeck().size());
    }

    @Test
    @Order(15)
    void shouldDeletePlayerStatus(){
        List<PlayerStatus> pss = playerStatusService.getAllPlayerStatus();
        PlayerStatus ps = pss.get(0);
        Integer id = ps.getId();
        playerStatusService.deletePlayerStatus(ps);
        pss = playerStatusService.getAllPlayerStatus();
        assertNotEquals(id,pss.get(0).getId());
    }
}
