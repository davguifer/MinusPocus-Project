package org.springframework.samples.minuspocus.stats;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.minuspocus.exceptions.BadRequestException;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.samples.minuspocus.user.UserService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/achievements")
@Tag(name = "Achievements", description = "The Achievements management API")
@SecurityRequirement(name = "BearerAuth")
public class AchievementController {

    private final AchievementService achievementService;
	private final UserService userService;
	private final StatService statService;

    @Autowired
	public AchievementController(AchievementService achievementService, UserService userService, StatService statService) {
		this.achievementService = achievementService;
		this.userService = userService;
		this.statService = statService;
	}

    @GetMapping
	public ResponseEntity<List<Achievement>> findAll() {
		return new ResponseEntity<>((List<Achievement>) achievementService.getAchievements(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Achievement> findAchievement(@PathVariable("id") int id){
		Achievement achievement = achievementService.findAchievementById(id);
		return new ResponseEntity<Achievement>(achievement, HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Achievement> createAchievement(@RequestBody @Valid Achievement newAchievement, BindingResult br){ 
		Achievement result=null;
		if(!br.hasErrors())
			result=achievementService.saveAchievement(newAchievement);
		else
			throw new BadRequestException(br.getAllErrors());
		return new ResponseEntity<>(result,HttpStatus.CREATED);	
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Void> modifyAchievement(@RequestBody @Valid Achievement newAchievement, BindingResult br,@PathVariable("id") int id) {
		Achievement achievementToUpdate = achievementService.findAchievementById(id);
		if(br.hasErrors())
			throw new BadRequestException(br.getAllErrors());		
		else if(newAchievement.getId()==null || !newAchievement.getId().equals(id))
			throw new BadRequestException("Achievement id is not consistent with resource URL:"+id);
		else{
			BeanUtils.copyProperties(newAchievement, achievementToUpdate, "id");
			achievementService.saveAchievement(achievementToUpdate);
		}			
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Void> deleteAchievement(@PathVariable("id") int id){
		List<User> users = userService.findAll();
		for(User user : users){
			if(user.getAchievement().stream().map(a -> a.getId()).toList().contains(id)){
				List<Achievement> achievements = user.getAchievement();
				achievements.removeIf(a -> a.getId()==id);
				user.setAchievement(achievements);
				userService.saveUser(user);
			}
		}
		achievementService.deleteAchievementById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/{id}/claim")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Void> claimAchievement(@PathVariable("id") int id, @RequestBody Integer playerId) throws UnfeasibleAchievementClaim {
		Achievement achievement = achievementService.findAchievementById(id);
		User player = userService.findUser(playerId);
		if(player.getAuthority().getAuthority() == "ADMIN" || player.getAchievement().stream().anyMatch(a -> a.getId() == id)){
			throw new UnfeasibleAchievementClaim();
		}

		List<Stat> statList = statService.getAllStats();
		Stat stat = statList.stream().filter(x -> x.getUser().getId() == playerId).findFirst().get();
		achievementService.claimAchievement(achievement, player, stat);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
