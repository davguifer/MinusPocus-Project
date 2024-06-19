package org.springframework.samples.minuspocus.ingredient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.minuspocus.card.Ingredient;
import org.springframework.samples.minuspocus.card.IngredientService;

@SpringBootTest
@AutoConfigureTestDatabase
class IngredientServiceTests {

    @Autowired
    private IngredientService ingredientService;

    @Test
    void shouldFindAllIngredients() {
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        assertEquals(72, ingredients.size());
    }
}
