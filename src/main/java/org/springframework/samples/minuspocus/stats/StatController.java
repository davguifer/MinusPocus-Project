package org.springframework.samples.minuspocus.stats;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/stats")
@Tag(name = "Stats", description = "API for stats management")
@SecurityRequirement(name = "bearerAuth")
public class StatController {

    private StatService statService;

    @Autowired
    public StatController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping
    public List<Stat> getAllStats() {
        return statService.getAllStats();
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<Stat> getStatByPlayer(@PathVariable("playerId") int playerId) {
        List<Stat> statList = statService.getAllStats();
        Optional<Stat> statOptional = statList.stream().filter(x -> x.getUser().getId() == playerId).findFirst();
        return statOptional.map(stat -> new ResponseEntity<>(stat, HttpStatus.OK)).orElseThrow(() -> new ResourceNotFoundException("Stat not found for player with id: " + playerId));
    }

    @GetMapping("/ranking")
    public Map<Integer, User> getRanking() {
        return statService.getRanking();
    }

}
