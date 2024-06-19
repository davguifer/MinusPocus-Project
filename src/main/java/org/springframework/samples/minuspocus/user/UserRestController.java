/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.minuspocus.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.minuspocus.auth.payload.response.MessageResponse;
import org.springframework.samples.minuspocus.exceptions.AccessDeniedException;
import org.springframework.samples.minuspocus.game.Game;
import org.springframework.samples.minuspocus.game.GameService;
import org.springframework.samples.minuspocus.game.PlayerStatus;
import org.springframework.samples.minuspocus.game.PlayerStatusService;
import org.springframework.samples.minuspocus.stats.Achievement;
import org.springframework.samples.minuspocus.stats.Stat;
import org.springframework.samples.minuspocus.stats.StatService;
import org.springframework.samples.minuspocus.util.RestPreconditions;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Minuspocus´users")
@SecurityRequirement(name = "bearerAuth")
class UserRestController {

	private final UserService userService;
	private final AuthoritiesService authService;
	private final PasswordEncoder encoder;
	private final StatService statService;
	private final GameService gameService;
	private final PlayerStatusService playerStatusService;

	@Autowired
	public UserRestController(UserService userService, AuthoritiesService authService, PasswordEncoder encoder,
			StatService statService, GameService gameService, PlayerStatusService playerStatusService) {
		this.userService = userService;
		this.authService = authService;
		this.encoder = encoder;
		this.statService = statService;
		this.gameService = gameService;
		this.playerStatusService = playerStatusService;
	}

	@GetMapping
	public ResponseEntity<List<User>> findAll(@RequestParam(required = false) String auth) {
		List<User> res;
		if (auth != null) {
			res = (List<User>) userService.findAllByAuthority(auth);
		} else
			res = (List<User>) userService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping("authorities")
	public ResponseEntity<List<Authorities>> findAllAuths() {
		List<Authorities> res = (List<Authorities>) authService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping(value = "{id}")
	public ResponseEntity<User> findById(@PathVariable("id") Integer id) {
		return new ResponseEntity<>(userService.findUser(id), HttpStatus.OK);
	}

	@GetMapping(path = "/friends/{username}")
	public ResponseEntity<List<User>> findAllFriends(@PathVariable("username") String username) {
		return new ResponseEntity<List<User>>(userService.findAllFriends(username), HttpStatus.OK);
	}

	@GetMapping("/{userId}/achievements")
	public ResponseEntity<List<Achievement>> findAchievementsByPlayer(@PathVariable("userId") int userId) {
		User user = userService.findUser(userId);
		if (user != null) {
			return new ResponseEntity<>(userService.getAchievementsByPlayer(userId), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping(path = "/friends/{username}")
	public ResponseEntity<List<User>> createFriend(@RequestBody @Valid User user,
			@PathVariable("username") String username) {
		return new ResponseEntity<List<User>>(userService.createFriend(user, username), HttpStatus.CREATED);
	}

	@DeleteMapping(path = "/friends/{username}")
	public ResponseEntity<List<User>> deleteFriend(@RequestBody @Valid User friend,
			@PathVariable("username") String username) {
		return new ResponseEntity<List<User>>(userService.deleteFriend(friend, username), HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> create(@RequestBody @Valid User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		User savedUser = userService.saveUser(user);
		if (savedUser.getAuthority().getId() == 2) {
			statService.newStat(savedUser);
		}
		return new ResponseEntity<>(savedUser.getAuthority().getAuthority(), HttpStatus.CREATED);
	}

	@PutMapping(value = "{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<User> update(@PathVariable("userId") Integer id, @RequestBody @Valid User user) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
		return new ResponseEntity<>(this.userService.updateUser(user, id), HttpStatus.OK);
	}

	@DeleteMapping(value = "{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("userId") int id) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
		User userToDelete = userService.findUser(id);
		User currentUser = userService.findCurrentUser();

		if (currentUser.getId() == id) {
			throw new AccessDeniedException("You can't delete yourself!");
		}
		for (User otherUser : userService.findAll()) {
			List<User> friends = otherUser.getFriends();
			List<User> newFriends = new ArrayList<>();
			if (otherUser.getId() != id) {
				for (int m = 0; m < friends.size(); m++) {
					if (!(friends.get(m).getId().equals(userToDelete.getId()))) {
						newFriends.add(friends.get(m));
					}

				}
				otherUser.setFriends(newFriends);
				userService.saveUser(otherUser);
			}
		}
		for (Game game : gameService.getAllGames()) {
			List<User> users = game.getUsers();
			List<User> newUsers = new ArrayList<>();
			for (int u = 0; u < users.size(); u++) {
				if (!(users.get(u).getId().equals(userToDelete.getId()))) {
					newUsers.add(users.get(u));
				}
			}
			game.setUsers(newUsers);
			gameService.saveGame(game);
		}
		for (Stat stat : statService.getAllStats()) {
			if (stat.getUser() == userToDelete) {
				statService.deleteStat(stat);
			}
		}
		for (PlayerStatus playerStatus : playerStatusService.getAllPlayerStatus()) {
			if (playerStatus.getPlayer() == userToDelete) {
				playerStatusService.deletePlayerStatus(playerStatus);
			}
		}
		userService.deleteUser(id);
		return new ResponseEntity<>(new MessageResponse("User deleted!"), HttpStatus.OK);
	}

}
