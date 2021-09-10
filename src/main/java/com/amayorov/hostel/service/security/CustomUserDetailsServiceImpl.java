package com.amayorov.hostel.service.security;

import com.amayorov.hostel.repository.security.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

	private final UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Entering method loadUserByUserName");
		log.debug("Authenticating user with username: {}", username);
		return userRepo
				.findByUserName(username)
				.map(u -> {
					log.info("User with name \"{}\" has been found", username);
					return new org.springframework.security.core.userdetails.User(
						u.getUserName(),
						u.getPassword(),
						true,
						true,
						true,
						true,
						u.getRoles()
								.stream()
								.map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRoleName()))
								.collect(Collectors.toSet()));
				})
				.orElseThrow(() -> {
					log.error("Error in loadUserByUsername() method. Cause: user not found");
					return new UsernameNotFoundException("No user with "
							+ "the name " + username + " was found in the database");
				});
	}
}
