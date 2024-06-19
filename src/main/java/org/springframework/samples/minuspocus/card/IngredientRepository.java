package org.springframework.samples.minuspocus.card;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface IngredientRepository  extends CrudRepository<Ingredient, Integer>{
    List<Ingredient> findAll();
}
