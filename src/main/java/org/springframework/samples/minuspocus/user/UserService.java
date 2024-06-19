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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.minuspocus.exceptions.ResourceNotFoundException;
import org.springframework.samples.minuspocus.stats.Achievement;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


	@Transactional
	public User saveUser(User user) throws DataAccessException {
		userRepository.save(user);
		return user;
	}

	@Transactional(readOnly = true)
	public User findUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
	}

	@Transactional(readOnly = true)
	public User findUser(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}


	@Transactional(readOnly = true)
	public User findPlayerByUser(String username) {
		return userRepository.findPlayerByUser(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
	}

	@Transactional(readOnly = true)
	public List<User> findAllFriends(String username){
		return userRepository.findByUsername(username).get().getFriends();
	}

	@Transactional()
	public List<User> createFriend(User user,String username){
		User concurrentUser = userRepository.findByUsername(username).get();
		concurrentUser.getFriends().add(user);
		userRepository.save(concurrentUser);
		return concurrentUser.getFriends();
	}

	@Transactional(readOnly = true)
    public List<Achievement> getAchievementsByPlayer(Integer userId){
		User user = findUser(userId);
        return user.getAchievement();
    }

	
	@Transactional()
	public List<User> deleteFriend(User friend,String username){
		User concurrentUser = userRepository.findByUsername(username).get();
		List<User> lista = concurrentUser.getFriends();
		List<User> newFriends = new ArrayList<>();
		for(int i = 0; i<lista.size(); i++){
			if (!(lista.get(i).getId().equals(friend.getId()))){
				newFriends.add(lista.get(i));
			}
		}
		concurrentUser.setFriends(newFriends);
		userRepository.save(concurrentUser);
		return concurrentUser.getFriends();
	}

	@Transactional(readOnly = true)
	public User findPlayerByUser(int id) {
		return userRepository.findPlayerByUser(id).orElseThrow(() -> new ResourceNotFoundException("Player", "ID", id));
	}

	@Transactional(readOnly = true)
	public User findCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			throw new ResourceNotFoundException("Nobody authenticated!");
		else
			return userRepository.findByUsername(auth.getName())
					.orElseThrow(() -> new ResourceNotFoundException("User", "Username", auth.getName()));
	}

	public Boolean existsUser(String username) {
		return userRepository.existsByUsername(username);
	}

	@Transactional(readOnly = true)
	public List<User> findAll() {
		return userRepository.findAll();
	}

	public Iterable<User> findAllByAuthority(String auth) {
		return userRepository.findAllByAuthority(auth);
	}

	@Transactional
	public User updateUser(@Valid User user, Integer idToUpdate) {
		User toUpdate = findUser(idToUpdate);
		BeanUtils.copyProperties(user, toUpdate, "id");
		userRepository.save(toUpdate);

		return toUpdate;
	}

	@Transactional
	public void deleteUser(Integer id) {
		User toDelete = findUser(id);
		this.userRepository.delete(toDelete);
	}

}









