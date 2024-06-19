package org.springframework.samples.minuspocus.stats;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatService {
    
    private StatRepository statRepository;

    @Autowired
    public StatService( StatRepository statRepository){
        this.statRepository=statRepository;
    }

    @Transactional(readOnly = true)
    public List<Stat> getAllStats(){
        return statRepository.findAll();
    }

    @Transactional
    public void newStat(User user){
        Stat stats = new Stat();
        stats.setGamesPlayed(0);
        stats.setVictories(0);
        stats.setTimePlayed(0.);
        stats.setUser(user);
        statRepository.save(stats);
    }

    @Transactional
    public Map<Integer,User> getRanking(){
        Map<Integer,User> dicc = new HashMap<>();
        List<Stat> stats = statRepository.findAll();
        for(Stat s: stats){
            if(s.getUser().getAuthority().getAuthority().equals("PLAYER")){
                if(s.gamesPlayed == 0){
                    dicc.put(0, s.getUser());
                }else{
                    Integer win = s.getVictories();
                    Integer lose = s.getGamesPlayed() - s.getVictories();
                    Integer punctuation = 5*win + 2*lose;
                    dicc.put(punctuation, s.getUser());
                }
                
            }
        }
        Map<Integer,User> sortedDicc = new TreeMap<>(dicc);
        return sortedDicc;
    }


    @Transactional
	public void deleteStat(Stat stat) {
		this.statRepository.delete(stat);
	}
    
}
