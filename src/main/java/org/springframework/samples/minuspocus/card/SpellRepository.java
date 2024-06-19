package org.springframework.samples.minuspocus.card;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SpellRepository  extends CrudRepository<Spell, Integer>{
    List<Spell> findAll();
}
