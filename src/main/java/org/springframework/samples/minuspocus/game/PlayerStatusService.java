package org.springframework.samples.minuspocus.game;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.minuspocus.card.Ingredient;
import org.springframework.samples.minuspocus.card.Spell;
import org.springframework.samples.minuspocus.card.effect.Effect;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class PlayerStatusService {

    private PlayerStatusRepository playerStatusRepository;
    private GameRepository gameRepository;

    @Autowired
    public PlayerStatusService(PlayerStatusRepository playerStatusRepository, GameRepository gameRepository){
        this.playerStatusRepository = playerStatusRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true)
    public List<PlayerStatus> getAllPlayerStatus(){
        return playerStatusRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PlayerStatus findPlayerStatusById(Integer id){
        return playerStatusRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PlayerStatus", "id", id));
    }

    @Transactional
    public PlayerStatus castSpell(PlayerStatus caster, PlayerStatus target, Spell spell, Game game, List<Ingredient> ingredients){

        List<Effect> effects = new ArrayList<>();
        effects.add(spell.getEffect1());
        if(spell.getEffect2() != null){
            effects.add(spell.getEffect2());
        }

        for(int i = 0; i < effects.size(); i++){
            Effect effect = effects.get(i);
            switch(effect){
                case STUN:
                    if(target.getBarrier() == 0){
                        if(target.getW1() == 1){
                            target.setW1(0);
                        }else if(target.getW2() == 1){
                            target.setW2(0);
                        }else if(target.getW3() == 1){
                            target.setW3(0);
                        }else if(target.getW4() == 1){
                            target.setW4(0);
                        }else if(target.getW1() == 2){
                            target.setW1(1);
                        }else if(target.getW2() == 2){
                            target.setW2(1);
                        }else if(target.getW3() == 2){
                            target.setW3(1);
                        }else if(target.getW4() == 2){
                            target.setW4(1);
                        }
                    }
                break;
                case HURT:
                    if(target.getBarrier() == 0){
                        if(target.getW1() == 2){
                            target.setW1(0);
                        }else if(target.getW2() == 2){
                            target.setW2(0);
                        }else if(target.getW3() == 2){
                            target.setW3(0);
                        }else if(target.getW4() == 2){
                            target.setW4(0);
                        }else if(target.getW1() == 1){
                            target.setW1(0);
                        }else if(target.getW2() == 1){
                            target.setW2(0);
                        }else if(target.getW3() == 1){
                            target.setW3(0);
                        }else if(target.getW4() == 1){
                            target.setW4(0);
                        }
                    }
                break;
                case HEAL:
                    if(caster.getW1() == 1){
                        caster.setW1(2);
                    }else if(caster.getW2() == 1){
                        caster.setW2(2);
                    }else if(caster.getW3() == 1){
                        caster.setW3(2);
                    }else if(caster.getW4() == 1){
                        caster.setW4(2);
                    }
                break;
                case RABBITS:
                    if(target.getBarrier() == 0){
                        target.setRabbits(target.getRabbits() + 1);
                    }
                break;
                case SHOO:
                    if(caster.getRabbits() > 0){
                        caster.setRabbits(caster.getRabbits() - 1);
                    }
                break;
                case BARRIER:
                    caster.setBarrier(caster.getBarrier() + 1);
                break;
            }
        }

        if(target.getBarrier() > 0 && spell.getTarget()){
            target.setBarrier(target.getBarrier() - 1);
        }

        List<Ingredient> deck = game.getIngredientsDeck();
        List<Ingredient> hand = caster.getHand();
        List<Ingredient> nHand = new ArrayList<>();
        for(int i = 0; i < hand.size(); i++){
            Ingredient ing = hand.get(i);
            if(ingredients.stream().allMatch(in -> in.getId() != ing.getId())){
                nHand.add(ing);
            }
        }

        Integer max = 7 - caster.getRabbits();
        if(max < 4){
            max = 4;
        }
        while(nHand.size() < max && deck.size() > 0){
            nHand.add(deck.get(0));
            deck.remove(0);
        }
        caster.setHand(nHand);
        playerStatusRepository.save(caster);
        playerStatusRepository.save(target);
        
        game.getSpellsDeck().removeIf(s -> s.getId() == spell.getId());
        game.setIngredientsDeck(deck);
        gameRepository.save(game);
        return caster;
    }

    @Transactional
    public PlayerStatus discardIngredients(Game game, PlayerStatus ps){
        List<Ingredient> deck = game.getIngredientsDeck();
        List<Ingredient> hand = new ArrayList<>();

        if(ps.getW1() == 2){
            ps.setW1(1);
        }else if(ps.getW2() == 2){
            ps.setW2(1);
        }else if(ps.getW3() == 2){
            ps.setW3(1);
        }else if(ps.getW4() == 2){
            ps.setW4(1);
        }else if(ps.getW1() == 1){
            ps.setW1(0);
        }else if(ps.getW2() == 1){
            ps.setW2(0);
        }else if(ps.getW3() == 1){
            ps.setW3(0);
        }else if(ps.getW4() == 1){
            ps.setW4(0);
        }

        Integer max = 7 - ps.getRabbits();
        if(max < 4){
            max = 4;
        }
        while(hand.size() < max && deck.size() > 0){
            hand.add(deck.get(0));
            deck.remove(0);
        }
        ps.setHand(hand);
        playerStatusRepository.save(ps);
        game.setIngredientsDeck(deck);
        gameRepository.save(game);
        return ps;
    }

    @Transactional
    public PlayerStatus punishPlayer(PlayerStatus ps){
        if(ps.getW1() == 2){
            ps.setW1(1);
        }else if(ps.getW2() == 2){
            ps.setW2(1);
        }else if(ps.getW3() == 2){
            ps.setW3(1);
        }else if(ps.getW4() == 2){
            ps.setW4(1);
        }else if(ps.getW1() == 1){
            ps.setW1(0);
        }else if(ps.getW2() == 1){
            ps.setW2(0);
        }else if(ps.getW3() == 1){
            ps.setW3(0);
        }else if(ps.getW4() == 1){
            ps.setW4(0);
        }

        playerStatusRepository.save(ps);
        return ps;
    }

    @Transactional
    public PlayerStatus voteDiscard(PlayerStatus ps){
        ps.setVote(!ps.getVote());
        playerStatusRepository.save(ps);
        return ps;
    }

    @Transactional
    public void deletePlayerStatus(PlayerStatus ps){
        playerStatusRepository.delete(ps);
    }
}
