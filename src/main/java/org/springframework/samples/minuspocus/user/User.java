package org.springframework.samples.minuspocus.user;

import java.util.List;

import jakarta.persistence.*;
import org.springframework.samples.minuspocus.model.Person;
import org.springframework.samples.minuspocus.stats.Achievement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "appusers")
public class User extends Person {

    @NotNull
	@Min(0)
	String age;
    @NotNull
	String email;
    @NotNull
	String password;
    String avatar;

    @NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "authority")
	Authorities authority;

	@ManyToMany(fetch = FetchType.EAGER)
	List<Achievement> achievement;

	public Boolean hasAuthority(String auth) {
		return authority.getAuthority().equals(auth);
	}

	public Boolean hasAnyAuthority(String... authorities) {
		Boolean cond = false;
		for (String auth : authorities) {
			if (auth.equals(authority.getAuthority()))
				cond = true;
		}
		return cond;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "friends", joinColumns = @JoinColumn(name = "user1_id"), inverseJoinColumns = @JoinColumn(name = "user2_id"), uniqueConstraints = {
			@UniqueConstraint(columnNames = { "user1_id", "user2_id" }) })
	@JsonIgnore
	private List<User> friends;



}
