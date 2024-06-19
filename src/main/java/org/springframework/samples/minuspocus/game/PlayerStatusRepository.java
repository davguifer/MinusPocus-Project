package org.springframework.samples.minuspocus.game;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.minuspocus.user.User;

public interface PlayerStatusRepository  extends CrudRepository<PlayerStatus, Integer>{
    List<PlayerStatus> findAll();

    @Query("SELECT ps FROM PlayerStatus ps WHERE ps.player = :user")
    PlayerStatus findByPlayer(User user);
}
