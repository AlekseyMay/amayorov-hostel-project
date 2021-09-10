package com.amayorov.hostel.service.Mock;

import com.amayorov.hostel.AbstractHostelTestMockito;
import com.amayorov.hostel.domain.entity.security.Role;
import com.amayorov.hostel.domain.entity.security.User;
import com.amayorov.hostel.repository.security.UserRepo;
import com.amayorov.hostel.service.security.CustomUserDetailsServiceImpl;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTestMockito extends AbstractHostelTestMockito {

	@InjectMocks
	private CustomUserDetailsServiceImpl userDetailsService;

	@Mock
	private UserRepo userRepo;

	@Test
	public void loadUserByUsername_NotFound() {

		String randomUsername = "test";
		when(userRepo.findByUserName(anyString())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userDetailsService.loadUserByUsername(randomUsername))
				.isInstanceOf(UsernameNotFoundException.class)
				.hasMessage("No user with the name " + randomUsername + " was found in the database");
	}

	@Test
	public void loadUserByUsername_Success() {
		User user = mock(User.class);
		Role role = mock(Role.class);
		when(role.getRoleName()).thenReturn("ADMIN");
		when(user.getUserName()).thenReturn("test");
		when(user.getPassword()).thenReturn("test");
		when(user.getRoles()).thenReturn(Set.of(role));

		when(userRepo.findByUserName(anyString())).thenReturn(Optional.of(user));

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

