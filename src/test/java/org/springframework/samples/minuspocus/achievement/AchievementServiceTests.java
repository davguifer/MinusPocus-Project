package org.springframework.samples.minuspocus.achievement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.stats.Achievement;
import org.springframework.samples.minuspocus.stats.AchievementService;
import org.springframework.samples.minuspocus.stats.Metric;
import org.springframework.samples.minuspocus.stats.Stat;
import org.springframework.samples.minuspocus.stats.UnfeasibleAchievementClaim;
import org.springframework.samples.minuspocus.user.AuthoritiesService;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.samples.minuspocus.user.UserService;
import org.springframework.transaction.TransactionSystemException;

@SpringBootTest
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AchievementServiceTests {
    @Autowired
    private AchievementService achievementService;

    @Autowired
	private UserService userService;

	@Autowired
	private AuthoritiesService authService;

    @Test
    @Order(1)
    void shouldFindAllAchievements(){
        List<Achievement> achievements = (List<Achievement>) this.achievementService.getAchievements();
        assertEquals(20, achievements.size());
    }

    @Test
    @Order(2)
    void shouldFindAchievementById(){
        Achievement achievement = this.achievementService.findAchievementById(1);
        assertEquals("Aprendiz", achievement.getName());
    }

    @Test
    @Order(3)
    void shouldNotFindAchievementById(){
        assertThrows(ResourceNotFoundException.class, () -> this.achievementService.findAchievementById(0));
    }

    @Test
    @Order(4)
    void shouldInsertAchievement(){
        int count = ((Collection<Achievement>) this.achievementService.getAchievements()).size();

        Achievement achievement = new Achievement();
        achievement.setName("Esta prueba");
        achievement.setDescription("Funciona");
        achievement.setThreshold(20.0);
        achievement.setBadgeImage("https://cdn-icons-png.flaticon.com/512/5243/5243423.png");
        achievement.setMetric(Metric.GAMES_PLAYED);

        this.achievementService.saveAchievement(achievement);
        assertNotEquals(0, achievement.getId().longValue());
		assertNotNull(achievement.getId());

		int finalCount = ((Collection<Achievement>) this.achievementService.getAchievements()).size();
		assertEquals(count + 1, finalCount);

    }

    @Test
    @Order(5)
    void shouldInsertAchievementNoMetric(){
        Achievement achievement = new Achievement();
        achievement.setName("Esta prueba");
        achievement.setDescription("Funciona");
        achievement.setThreshold(20.0);
        achievement.setBadgeImage("https://cdn-icons-png.flaticon.com/512/5243/5243423.png");
        achievement.setMetric(null);

        assertThrows(TransactionSystemException.class, () -> this.achievementService.saveAchievement(achievement));

    }

    @Test
    @Order(6)
    void shouldInsertAchievementNoDescription(){
        Achievement achievement = new Achievement();
        achievement.setName("Esta prueba");
        achievement.setDescription(null);
        achievement.setThreshold(20.0);
        achievement.setBadgeImage("https://cdn-icons-png.flaticon.com/512/5243/5243423.png");
        achievement.setMetric(Metric.GAMES_PLAYED);

        assertThrows(TransactionSystemException.class, () -> this.achievementService.saveAchievement(achievement));

    }

    @Test
    @Order(7)
    void shouldInsertAchievementThreshold(){
        Achievement achievement = new Achievement();
        achievement.setName("Esta prueba");
        achievement.setDescription("Funciona");
        achievement.setThreshold(-2);
        achievement.setBadgeImage("https://cdn-icons-png.flaticon.com/512/5243/5243423.png");
        achievement.setMetric(Metric.GAMES_PLAYED);

        assertThrows(TransactionSystemException.class, () -> this.achievementService.saveAchievement(achievement));

    }

    @Test
    @Order(8)
    void shouldDeleteAchievementById(){

        Integer firstCount = ((Collection<Achievement>) achievementService.getAchievements()).size();

        Achievement achievement = new Achievement();
        achievement.setName("Esta prueba");
        achievement.setDescription("Funciona");
        achievement.setThreshold(20.0);
        achievement.setBadgeImage("https://cdn-icons-png.flaticon.com/512/5243/5243423.png");
        achievement.setMetric(Metric.GAMES_PLAYED);

        this.achievementService.saveAchievement(achievement);

        Integer secondCount = ((Collection<Achievement>) achievementService.getAchievements()).size();
		assertEquals(firstCount + 1, secondCount);
		achievementService.deleteAchievementById(achievement.getId());
		Integer lastCount = ((Collection<Achievement>) achievementService.getAchievements()).size();
		assertEquals(firstCount, lastCount);

    }

    @Test
    @Order(9)
    void shouldNotDeleteAchievementById(){
        Achievement achievement = new Achievement();
        achievement.setName("Esta prueba");
        achievement.setDescription("Funciona");
        achievement.setThreshold(20.0);
        achievement.setBadgeImage("https://cdn-icons-png.flaticon.com/512/5243/5243423.png");
        achievement.setMetric(null);

        assertThrows(DataIntegrityViolationException.class, () -> this.achievementService.deleteAchievementById(1));

    }

    @Test
    @Order(10)
    void testClaimAchievement() throws UnfeasibleAchievementClaim {
        Achievement achievement = achievementService.findAchievementById(1);
		User user = userService.findPlayerByUser(8);

        Stat stat = new Stat();
		stat.setId(2);
		stat.setGamesPlayed(10);
		stat.setVictories(8);
		stat.setTimePlayed(10.);
		stat.setUser(user);

        achievementService.claimAchievement(achievement, user, stat);

        List<Achievement> achievements = user.getAchievement();
        assertEquals(1, achievements.size());
        assertEquals(achievement, achievements.get(0));
    }

    @Test
    @Order(11)
    void testNotClaimAchievement() throws UnfeasibleAchievementClaim {
        Achievement achievement = achievementService.findAchievementById(1);
		User user = userService.findPlayerByUser(8);

        Stat stat = new Stat();
		stat.setId(2);
		stat.setGamesPlayed(9);
		stat.setVictories(8);
		stat.setTimePlayed(10.);
		stat.setUser(user);

        assertThrows(UnfeasibleAchievementClaim.class, () -> achievementService.claimAchievement(achievement, user, stat));
    }

    @Test
    @Order(12)
    void testNotClaimAchievementVictories() throws UnfeasibleAchievementClaim {
        Achievement achievement = achievementService.findAchievementById(7);
		User user = userService.findPlayerByUser(8);

        Stat stat = new Stat();
		stat.setId(2);
		stat.setGamesPlayed(9);
		stat.setVictories(8);
		stat.setTimePlayed(10.);
		stat.setUser(user);

        assertThrows(UnfeasibleAchievementClaim.class, () -> achievementService.claimAchievement(achievement, user, stat));
    }

    @Test
    @Order(13)
    void testNotClaimAchievementLoses() throws UnfeasibleAchievementClaim {
        Achievement achievement = achievementService.findAchievementById(13);
		User user = userService.findPlayerByUser(8);

        Stat stat = new Stat();
		stat.setId(2);
		stat.setGamesPlayed(9);
		stat.setVictories(8);
		stat.setTimePlayed(10.);
		stat.setUser(user);

        assertThrows(UnfeasibleAchievementClaim.class, () -> achievementService.claimAchievement(achievement, user, stat));
    }

    @Test
    @Order(14)
    void testNotClaimAchievementTotalPlayTime() throws UnfeasibleAchievementClaim {
        Achievement achievement = achievementService.findAchievementById(19);
		User user = userService.findPlayerByUser(8);

        Stat stat = new Stat();
		stat.setId(2);
		stat.setGamesPlayed(9);
		stat.setVictories(8);
		stat.setTimePlayed(10.);
		stat.setUser(user);

        assertThrows(UnfeasibleAchievementClaim.class, () -> achievementService.claimAchievement(achievement, user, stat));
    }

}
