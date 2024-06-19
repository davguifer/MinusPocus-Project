package org.springframework.samples.minuspocus.game;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.samples.minuspocus.card.Ingredient;
import org.springframework.samples.minuspocus.card.Spell;
import org.springframework.samples.minuspocus.model.NamedEntity;
import org.springframework.samples.minuspocus.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "games")
@EqualsAndHashCode(of = "id")
public class Game extends NamedEntity {

    @NotNull
    @Size(max = 8)
    String code;

    @NotNull
    @Min(1) 
    @Max(3)
    Integer round;

    LocalDateTime start;

    LocalDateTime finish;

    @NotNull
    LocalDateTime create;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "games_appusers", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "user_id"), uniqueConstraints = {
        @UniqueConstraint(columnNames = { "game_id", "user_id" }) })
    List<User> users;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    List<Spell> spellsDeck;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    List<Ingredient> ingredientsDeck;

    @ManyToMany(fetch = FetchType.EAGER)
    List<User> invitations;


}
