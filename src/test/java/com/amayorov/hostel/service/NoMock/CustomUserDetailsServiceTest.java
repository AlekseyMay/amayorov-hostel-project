package com.amayorov.hostel.service.NoMock;

import com.amayorov.hostel.AbstractHostelTest;
import com.amayorov.hostel.domain.entity.security.Role;
import com.amayorov.hostel.domain.entity.security.User;
import com.amayorov.hostel.repository.security.RoleRepo;
import com.amayorov.hostel.repository.security.UserRepo;
import com.amayorov.hostel.service.security.CustomUserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CustomUserDetailsServiceTest extends AbstractHostelTest {

	@Autowired
	private CustomUserDetailsServiceImpl userDetailsService;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Before
	public void clearDb() {
		userRepo.deleteAll();
	}

	@Test
	public void loadUserByUsername_NotFound() {
		assertThatThrownBy(() -> userDetailsService.loadUserByUsername("Test"))
				.isInstanceOf(UsernameNotFoundException.class)
				.hasMessageContaining("No user with ");
	}

	@Test
	public void loadUSerByUsername() {
		Role role = new Role();
		role.setRoleName("ADMIN");
		Role savedRole = roleRepo.save(role);
		User user = new User();
		user.setUserName("test");
		user.setPassword("test");
		user.setRoles(Set.of(savedRole));
		userRepo.save(user);

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());

		assertThat(userDetails.getUsername()).isEqualTo(user.getUserName());
		assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
		assertThat(userDetails.getAuthorities()).isEqualTo(user.getRoles()
															.stream()
															.map((r -> new SimpleGrantedAuthority("ROLE_" + r.getRoleName())))
															.collect(Collectors.toSet()));
		assertThat(userDetails.isEnabled()).isTrue();
		assertThat(userDetails.isAccountNonExpired()).isTrue();
		assertThat(userDetails.isCredentialsNonExpired()).isTrue();
		assertThat(userDetails.isAccountNonLocked()).isTrue();
	}
}

