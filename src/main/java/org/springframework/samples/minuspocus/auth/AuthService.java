package org.springframework.samples.minuspocus.auth;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.samples.minuspocus.user.Authorities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.minuspocus.auth.payload.request.SignupRequest;
import org.springframework.samples.minuspocus.user.AuthoritiesService;
import org.springframework.samples.minuspocus.user.User;
import org.springframework.samples.minuspocus.user.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final PasswordEncoder encoder;
	private final AuthoritiesService authoritiesService;
	private final UserService userService;

	@Autowired
	public AuthService(PasswordEncoder encoder, AuthoritiesService authoritiesService, UserService userService) {
		this.encoder = encoder;
		this.authoritiesService = authoritiesService;
		this.userService = userService;
	}

	@Transactional
	public void createUser(@Valid SignupRequest request) {
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(encoder.encode(request.getPassword()));
		Authorities role;
		role = authoritiesService.findByAuthority("PLAYER");
		user.setAuthority(role);
		userService.saveUser(user);
	}

}
