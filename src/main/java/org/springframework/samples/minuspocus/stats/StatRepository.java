package org.springframework.samples.minuspocus.stats;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.stereotype.Repository;

@Repository
public interface StatRepository extends CrudRepository<Stat,Integer> {
    List<Stat> findAll();

    @Query("SELECT s FROM Stat s WHERE s.user = :user")
    Stat findByPlayer(User user);

	Optional<Stat> findById(Integer id);
}
