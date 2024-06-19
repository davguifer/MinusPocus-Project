package org.springframework.samples.minuspocus.card;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientService {

    private IngredientRepository ingredientRepository;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository){
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional(readOnly = true )
    public List<Ingredient> getAllIngredients(){
        return ingredientRepository.findAll();
    }

    @Transactional
    public void actualizaCard(Ingredient ingredient){
        ingredientRepository.save(ingredient);
    }

}
