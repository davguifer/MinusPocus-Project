package org.springframework.samples.minuspocus.stats;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.minuspocus.configuration.SecurityConfiguration;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.samples.minuspocus.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = StatController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
class StatControllerTests {
	private static final String BASE_URL = "/api/v1/stats";


	@Autowired
	private MockMvc mockMvc;

	@SuppressWarnings("unused")
	@Autowired
	private StatController statController;

	@MockBean
	private StatService statService;

	@MockBean
    private UserService userService;

	@Test
	@WithMockUser("Mario")
	void shouldFindAll() throws Exception {
		Stat round1 = new Stat();
		round1.setId(1);

		when(this.statService.getAllStats()).thenReturn(List.of(round1));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$[?(@.id == 1)].id").value(1)); 
	}
	
	@Test
    @WithMockUser("admin")
    public void shouldFindStatByPlayer() throws Exception {
		User user = new User();

        Stat stat = new Stat();
        stat.setUser(user); 
        stat.getUser().setId(9);

		when(this.statService.getAllStats()).thenReturn(List.of(stat));

        mockMvc.perform(get(BASE_URL + "/{id}", 9))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.user.id").value(9)); 
    }

	@WithMockUser("admin")
    @Test
    public void shouldNotFoundStatByNonExistingPlayer() throws Exception {

        User user = userService.findUser(20);

        Stat stat = new Stat();
        stat.setUser(user); 

        when(statService.getAllStats()).thenThrow(ResourceNotFoundException.class);
        
        mockMvc.perform(get(BASE_URL + "/{id}", 20))
			.andExpect(status().isNotFound());

    }

	@Test
    @WithMockUser("player")
    public void shouldReturnRanking() throws Exception {
        Map<Integer, User> ranking = new TreeMap<>();

        User user1 = new User(); 
        user1.setId(1);
        User user2 = new User();
        user2.setId(2);

        ranking.put(100, user1);
        ranking.put(90, user2);

        when(statService.getRanking()).thenReturn(ranking);

        mockMvc.perform(get(BASE_URL + "/ranking"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.100.id").value(1))
               .andExpect(jsonPath("$.90.id").value(2));
    }

}
