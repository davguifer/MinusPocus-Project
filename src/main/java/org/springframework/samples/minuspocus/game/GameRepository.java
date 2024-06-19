package org.springframework.samples.minuspocus.game;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.minuspocus.user.User;

public interface GameRepository extends CrudRepository<Game,Integer>{
    List<Game> findAll();

    @Query("SELECT g FROM Game g WHERE g.finish = null and :user MEMBER OF g.users")
    Game findCurrentGame(User user);

    @Query("SELECT ps FROM PlayerStatus ps WHERE ps.player IN :players")
    List<PlayerStatus> findAllPlayerStatus(List<User> players);
    
}
