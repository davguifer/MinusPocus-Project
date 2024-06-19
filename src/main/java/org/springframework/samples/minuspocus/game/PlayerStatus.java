package org.springframework.samples.minuspocus.game;


import java.util.List;


import org.springframework.samples.minuspocus.card.Ingredient;
import org.springframework.samples.minuspocus.game.color.Color;
import org.springframework.samples.minuspocus.model.BaseEntity;
import org.springframework.samples.minuspocus.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "status")
@EqualsAndHashCode(of = "id")
public class PlayerStatus extends BaseEntity {

    @NotNull
    Color color;

    @NotNull
    @Min(0)
    @Max(2)
    Integer w1;

    @NotNull
    @Min(0)
    @Max(2)
    Integer w2;

    @NotNull
    @Min(0)
    @Max(2)
    Integer w3;

    @NotNull
    @Min(0)
    @Max(2)
    Integer w4;

    @NotNull
    @Min(0)
    Integer rabbits;

    @NotNull
    @Min(0)
    Integer barrier;

    @NotNull
    Boolean vote;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    @Size(max = 7)
    List<Ingredient> hand;

    @OneToOne
    @JoinColumn(name = "user_id")
    User player;

}
