package org.springframework.samples.minuspocus.stats;

import org.springframework.samples.minuspocus.model.BaseEntity;
import org.springframework.samples.minuspocus.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of="id")
@Table(name = "stats")
public class Stat extends BaseEntity {

    @NotNull
    @Min(0)
    Integer gamesPlayed;
    
    @NotNull
    @Min(0)
    Integer victories;
    
    @NotNull
    @Min(0)
    Double timePlayed;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

}
