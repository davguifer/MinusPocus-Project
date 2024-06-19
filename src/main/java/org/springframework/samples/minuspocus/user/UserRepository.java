package org.springframework.samples.minuspocus.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface UserRepository extends  CrudRepository<User, Integer>{
	
    List<User> findAll();

	@Query("SELECT u FROM User u WHERE u.username = :username")
	Optional<User> findPlayerByUser(String username);
	
	@Query("SELECT u FROM User u WHERE u.id = :id")
	Optional<User> findPlayerByUser(int id);


	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Optional<User> findById(Integer id);
	
	@Query("SELECT u FROM User u WHERE u.authority.authority = :auth")
	Iterable<User> findAllByAuthority(String auth);

	
}


