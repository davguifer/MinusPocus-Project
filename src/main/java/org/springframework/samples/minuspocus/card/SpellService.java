package org.springframework.samples.minuspocus.card;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.game.Game;
import org.springframework.samples.minuspocus.game.GameRepository;
import org.springframework.samples.minuspocus.game.PlayerStatus;
import org.springframework.samples.minuspocus.game.PlayerStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpellService {

    private SpellRepository spellRepository;
    private GameRepository gameRepository;
    private PlayerStatusRepository playerStatusRepository;

    @Autowired
    public SpellService(SpellRepository spellRepository, GameRepository gameRepository, PlayerStatusRepository playerStatusRepository){
        this.spellRepository = spellRepository;
        this.gameRepository = gameRepository;
        this.playerStatusRepository = playerStatusRepository;
    }

    @Transactional(readOnly = true )
    public List<Spell> getAllSpells(){
        return spellRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Spell findSpellById(Integer id){
        return spellRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Spell", "id", id));
    }

    @Transactional
    public PlayerStatus discardSpell(Spell spell, Game game, PlayerStatus ps, List<Ingredient> ingredients){
        List<Ingredient> deck = game.getIngredientsDeck();
        List<Ingredient> hand = ps.getHand();
        List<Ingredient> nHand = new ArrayList<>();
        for(int i = 0; i < hand.size(); i++){
            Ingredient ing = hand.get(i);
            if(ingredients.stream().allMatch(in -> in.getId() != ing.getId())){
                nHand.add(ing);
            }
        }

        Integer max = 7 - ps.getRabbits();
        if(max < 4){
            max = 4;
        }
        while(nHand.size() < max && deck.size() > 0){
            nHand.add(deck.get(0));
            deck.remove(0);
        }
        ps.setHand(nHand);
        playerStatusRepository.save(ps);
        game.setIngredientsDeck(deck);
        game.getSpellsDeck().removeIf(s -> s.getId() == spell.getId());
        gameRepository.save(game);
        return ps;
    }

}
