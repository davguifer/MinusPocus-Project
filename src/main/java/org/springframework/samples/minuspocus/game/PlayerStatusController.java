package org.springframework.samples.minuspocus.game;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/status")
@Tag(name = "Status", description = "API for the player status")
@SecurityRequirement(name = "BearerAuth")
public class PlayerStatusController {

    private PlayerStatusService playerStatusService;
    private GameService gameService;

    @Autowired
    public PlayerStatusController(PlayerStatusService playerStatusService, GameService gameService) {
        this.playerStatusService = playerStatusService;
        this.gameService = gameService;
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerStatus> getPlayerStatusByPlayer(@PathVariable("playerId") int playerId) {
        List<PlayerStatus> playerStatusList = playerStatusService.getAllPlayerStatus();
        Optional<PlayerStatus> playerStatusOptional = playerStatusList.stream().filter(x -> x.getPlayer().getId() == playerId).findFirst();
        return playerStatusOptional.map(playerStatus -> new ResponseEntity<>(playerStatus, HttpStatus.OK)).orElseThrow(() -> new ResourceNotFoundException("PlayerStatus not found for player with id: " + playerId));
    }

    @PutMapping("/{id}/discard")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PlayerStatus> discardIngredients(@PathVariable("id") int id) throws UnfeasibleGameState {
        PlayerStatus status = playerStatusService.findPlayerStatusById(id);
        Game game = gameService.findCurrentGame(status.getPlayer());
        if (game.getStart() == null || game.getFinish() != null) {
            throw new UnfeasibleGameState();
        }
        return new ResponseEntity<>(playerStatusService.discardIngredients(game, status), HttpStatus.OK);
    }

    @PutMapping("/{id}/punish")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PlayerStatus> punishPlayer(@PathVariable("id") int id) throws UnfeasibleGameState {
        PlayerStatus status = playerStatusService.findPlayerStatusById(id);
        Game game = gameService.findCurrentGame(status.getPlayer());
        if (game.getStart() == null || game.getFinish() != null) {
            throw new UnfeasibleGameState();
        }
        return new ResponseEntity<>(playerStatusService.punishPlayer(status), HttpStatus.OK);
    }

    @PutMapping("/{id}/vote")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PlayerStatus> voteDiscard(@PathVariable("id") int id) throws UnfeasibleGameState {
        PlayerStatus status = playerStatusService.findPlayerStatusById(id);
        Game game = gameService.findCurrentGame(status.getPlayer());
        if (game.getStart() == null || game.getFinish() != null) {
            throw new UnfeasibleGameState();
        }
        return new ResponseEntity<>(playerStatusService.voteDiscard(status), HttpStatus.OK);
    }
}
