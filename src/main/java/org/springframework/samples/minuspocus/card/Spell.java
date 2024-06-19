package org.springframework.samples.minuspocus.card;

import org.springframework.samples.minuspocus.card.effect.Effect;

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
@Table(name = "spells")
public class Spell extends Card{

    @NotNull
    Effect effect1;
    
    Effect effect2;
    
    @NotNull
    Boolean target;
}