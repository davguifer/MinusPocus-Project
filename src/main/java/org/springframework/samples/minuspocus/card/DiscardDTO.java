package org.springframework.samples.minuspocus.card;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscardDTO {

    @NotNull
    Integer gameId;

    @NotNull
    Integer playerStatusId;
    
    @NotNull
    @NotEmpty
    List<Ingredient> ingredients;

}
