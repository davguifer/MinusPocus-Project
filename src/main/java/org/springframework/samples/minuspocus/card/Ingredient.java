package org.springframework.samples.minuspocus.card;

import org.springframework.samples.minuspocus.card.ingredientType.IngredientType;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "ingredients")
public class Ingredient extends Card{
     
    @NotNull
    IngredientType type;
}