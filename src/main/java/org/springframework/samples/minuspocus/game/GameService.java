package org.springframework.samples.minuspocus.game;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.minuspocus.card.Ingredient;
import org.springframework.samples.minuspocus.card.Spell;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.game.color.Color;
import org.springframework.samples.minuspocus.stats.Stat;
import org.springframework.samples.minuspocus.stats.StatRepository;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;

@Service
public class GameService {
    
    private GameRepository gameRepository;
    private PlayerStatusRepository playerStatusRepository;
    private StatRepository statRepository;

    @Autowired
    public GameService(GameRepository gameRepository, PlayerStatusRepository playerStatusRepository, StatRepository statRepository){
        this.gameRepository = gameRepository;
        this.playerStatusRepository = playerStatusRepository;
        this.statRepository = statRepository;
    }

    @Transactional(readOnly = true )
    public List<Game> getAllGames(){
        return gameRepository.findAll();
    }

    @Transactional
    public Game saveGame(Game game) throws DataAccessException{
        gameRepository.save(game);
        return game;
    }
    
    @Transactional(readOnly = true)
    public List<User> findUsersByParty(Integer id){
        Game game = gameRepository.findById(id).get();
        return game.getUsers();
    }

    @Transactional
    public Game updateGame(@Valid Game game, Integer idToUpdate) {
		Game toUpdate = findGameById(idToUpdate);
		BeanUtils.copyProperties(game, toUpdate, "id");
        if(toUpdate.getUsers().isEmpty()){
            gameRepository.delete(toUpdate);
        }else{
            gameRepository.save(toUpdate);
        }
		
		return toUpdate;
	}

    @Transactional(readOnly = true)
    public Game findGameById(Integer id){
        return gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game", "id", id));
    }

    @Transactional
    public void deleteGame(Integer id){
        Game toDelete = gameRepository.findById(id).get();
        gameRepository.delete(toDelete);
    }

    @Transactional(readOnly = true)
    public Game findCurrentGame(User user){
        return gameRepository.findCurrentGame(user);
    }

    @Transactional
    public Game startGame(Game game, List<Spell> spells, List<Ingredient> ingredients){
        List<Color> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.GREEN);
        
        game.setStart(LocalDateTime.now());
        Collections.shuffle(ingredients);
        Collections.shuffle(spells);

        List<User> players = game.getUsers();
        for(int i = 0; i < players.size(); i++){
            User player = players.get(i);
            PlayerStatus ps = new PlayerStatus();
            ps.setW1(2);
            ps.setW2(2);
            ps.setW3(2);
            ps.setW4(2);
            ps.setRabbits(0);
            ps.setBarrier(0);
            ps.setVote(false);
            ps.setColor(colors.get(i));
            ps.setPlayer(player);
            List<Ingredient> hand = new ArrayList<>();
            for(int j = 0; j < 7; j++){
                hand.add(ingredients.get(0));
                ingredients.remove(0);
            }
            ps.setHand(hand);
            playerStatusRepository.save(ps);
        }
        game.setIngredientsDeck(ingredients);
        game.setSpellsDeck(spells);
        game.setInvitations(new ArrayList<>());
        return gameRepository.save(game);
    }

    @Transactional(readOnly = true)
    public List<PlayerStatus> findAllPlayerStatus(List<User> players){
        return gameRepository.findAllPlayerStatus(players);
    }

    @Transactional
    public Game finishGame(Game game){
        game.setFinish(LocalDateTime.now());
        game.setSpellsDeck(new ArrayList<>());
        game.setIngredientsDeck(new ArrayList<>());

        List<User> players = game.getUsers();
        List<PlayerStatus> status = gameRepository.findAllPlayerStatus(players);
        Integer winnerWizards = Collections.max(status.stream().map(ps -> (ps.getW1() > 0? 1 : 0) + (ps.getW1() > 0? 1 : 0) + (ps.getW2() > 0? 1 : 0) + (ps.getW3() > 0? 1 : 0) + (ps.getW4() > 0? 1 : 0)).toList());

        for(int i = 0; i < players.size(); i++){
            User player = players.get(i);
            PlayerStatus ps = playerStatusRepository.findByPlayer(player);
            Stat stat = statRepository.findByPlayer(player);
            stat.setGamesPlayed(stat.getGamesPlayed() + 1);
            stat.setTimePlayed(stat.getTimePlayed() + Duration.between(game.getStart(), game.getFinish()).getSeconds());
            if((ps.getW1() > 0? 1 : 0) + (ps.getW1() > 0? 1 : 0) + (ps.getW2() > 0? 1 : 0) + (ps.getW3() > 0? 1 : 0) + (ps.getW4() > 0? 1 : 0) == winnerWizards){
                stat.setVictories(stat.getVictories() + 1);
            }
            statRepository.save(stat);
            playerStatusRepository.delete(playerStatusRepository.findByPlayer(player));
        }

        return gameRepository.save(game);
    }

    @Transactional
    public Game advanceRound(Game game, List<Spell> spells, List<Ingredient> ingredients){
        List<PlayerStatus> psl = gameRepository.findAllPlayerStatus(game.getUsers());
        game.setRound(game.getRound()+1);

        Collections.shuffle(ingredients);
        Collections.shuffle(spells); 

        for(int i = 0; i < psl.size(); i++){
            PlayerStatus ps = psl.get(i);
            List<Ingredient> hand = new ArrayList<>();

            if(ps.getW1() + ps.getW2() + ps.getW3() + ps.getW4() > 0){
                Integer max = 7 - ps.getRabbits();
                if(max < 4){
                    max = 4;
                } 
                while(hand.size() < max){
                    hand.add(ingredients.get(0));
                    ingredients.remove(0);
                }
            }
            ps.setVote(false);
            ps.setHand(hand);
            playerStatusRepository.save(ps);
        }
        game.setIngredientsDeck(ingredients);
        game.setSpellsDeck(spells);
        return gameRepository.save(game);
    }

    @Transactional
    public Game discardSpells(Game game, List<PlayerStatus> status){
        List<Spell> deck = game.getSpellsDeck();
        for(int i = 0; i < 3 && deck.size() > 0; i++){
            deck.remove(0);
        }
        game.setSpellsDeck(deck);
        for(int i = 0; i < status.size(); i++){
            PlayerStatus ps = status.get(i);
            ps.setVote(false); 
            playerStatusRepository.save(ps);
        }
        return gameRepository.save(game);
    }

    @Transactional
    public Game shuffleSpells(Game game, List<Spell> spells){
        Collections.shuffle(spells);
        game.setSpellsDeck(spells);
        return gameRepository.save(game);
    }

}