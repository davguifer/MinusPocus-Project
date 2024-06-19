package org.springframework.samples.minuspocus.stats;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.samples.minuspocus.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;

@Service
public class AchievementService {

    AchievementRepository repo;
    UserRepository usrepo;

    @Autowired
    public AchievementService(AchievementRepository repo, UserRepository usrepo){
        this.repo = repo;
        this.usrepo = usrepo;
    }

    @Transactional(readOnly = true)    
    public List<Achievement> getAchievements(){
        return repo.findAll();
    }

    @Transactional(readOnly = true)    
    public Achievement findAchievementById(int id){
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));
    }

    @Transactional
    public Achievement saveAchievement(@Valid Achievement newAchievement) {
        return repo.save(newAchievement);
    }


    @Transactional
    public void deleteAchievementById(int id){
        repo.deleteById(id);
    }


    @Transactional
    public void claimAchievement(Achievement achievement, User player, Stat stat) throws UnfeasibleAchievementClaim {
        Boolean pass = true;
        switch(achievement.getMetric()){
            case GAMES_PLAYED:
                pass = pass && (stat.getGamesPlayed() >= achievement.getThreshold());
            break;
            case VICTORIES:
                pass = pass && (stat.getVictories() >= achievement.getThreshold());
            break;
            case LOSES:
                pass = pass && ((stat.getGamesPlayed() - stat.getVictories()) >= achievement.getThreshold());
            break;
            case TOTAL_PLAY_TIME:
                pass = pass && (stat.getTimePlayed() >= achievement.getThreshold());
            break;
        }
        if(!pass){
            throw new UnfeasibleAchievementClaim();
        }else{
            List<Achievement> achievements = player.getAchievement();
            achievements.add(achievement);
            player.setAchievement(achievements);
            usrepo.save(player);
        }
    }

}
