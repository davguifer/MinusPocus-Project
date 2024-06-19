package org.springframework.samples.minuspocus.card;

import org.springframework.samples.minuspocus.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class Card extends BaseEntity{

    @NotNull
    @Column(name = "valuable")
    Integer valuable;

    public Integer getValuable() {
		return this.valuable;
	}

	@Override
	public String toString() {
		return this.getValuable().toString();
	}
}