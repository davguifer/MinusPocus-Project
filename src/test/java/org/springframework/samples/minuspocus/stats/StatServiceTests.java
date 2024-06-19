package org.springframework.samples.minuspocus.stats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.samples.minuspocus.user.Authorities;
import org.springframework.samples.minuspocus.user.User;

@SpringBootTest
@AutoConfigureTestDatabase
class StatServiceTests {

	private static final int TEST_AUTH_ID = 1;

    @Autowired
    private StatService statService;

	@Test
	void shouldFindAllStats() {
		List<Stat> stats = (List<Stat>) this.statService.getAllStats();
		assertEquals(5, stats.size());
	}
	
	@Test
	void shouldInsertNewStat() {
		Authorities auth = new Authorities();
		auth.setId(TEST_AUTH_ID);

		Achievement achiev1 = new Achievement();
		achiev1.setId(1);
		achiev1.setName("Aprendiz");
		achiev1.setDescription("Juega un total de 10 partidas");
		achiev1.setBadgeImage("https://cdn-icons-png.flaticon.com/512/5243/5243423.png");
		achiev1.setThreshold(10.00);
		achiev1.setMetric(Metric.GAMES_PLAYED);


		User user = new User();
		user.setId(1);
		user.setAge("20");
		user.setUsername("user");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("user@gmail.com");
		user.setPassword("password");
		user.setAuthority(auth);
		user.setAchievement(List.of(achiev1));

		int count = ((Collection<Stat>) this.statService.getAllStats()).size();

		
		this.statService.newStat(user);

		int finalCount = ((Collection<Stat>) this.statService.getAllStats()).size();
		assertEquals(count + 1, finalCount);
	}

	@Test
	void shouldNotInsertNewStatWithIncorrectUser() {


		User user = new User();

		
		assertThrows(InvalidDataAccessApiUsageException.class, () -> this.statService.newStat(user));

	}

	@Test
    void shouldFindRanking(){
        Map<Integer,User> ranking = this.statService.getRanking();
        assertEquals(5, ranking.size());
    }

	@Test
    void shouldFindRanking2(){
        Map<Integer,User> ranking = this.statService.getRanking();
        assertEquals("Luigi", ranking.entrySet()
		.stream()
		.max(Map.Entry.comparingByKey())
		.map(Map.Entry::getValue)
		.map(User::getUsername)
		.get());
    }

	@Test
	void shouldDeleteStat(){
        Integer firstCount = ((Collection<Stat>) statService.getAllStats()).size();
        
		Authorities auth = new Authorities();
		auth.setId(TEST_AUTH_ID);

		Achievement achiev1 = new Achievement();
		achiev1.setId(1);
		achiev1.setName("Aprendiz");
		achiev1.setDescription("Juega un total de 10 partidas");
		achiev1.setBadgeImage("https://cdn-icons-png.flaticon.com/512/5243/5243423.png");
		achiev1.setThreshold(10.00);
		achiev1.setMetric(Metric.GAMES_PLAYED);

		Stat stat = new Stat();

		User user = new User();
		user.setId(2);
		user.setAge("20");
		user.setUsername("user");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setEmail("user@gmail.com");
		user.setPassword("password");
		user.setAuthority(auth);
		user.setAchievement(List.of(achiev1));

		stat.setId(1);

		this.statService.newStat(user);

		Integer secondCount = ((Collection<Stat>) statService.getAllStats()).size();
		assertEquals(firstCount + 1, secondCount);
        Stat toDelete = statService.getAllStats().get(statService.getAllStats().size() - 1);
		statService.deleteStat(toDelete);
		Integer lastCount = ((Collection<Stat>) statService.getAllStats()).size();
		assertEquals(firstCount, lastCount);
    }

}
