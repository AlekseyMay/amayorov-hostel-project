package com.amayorov.hostel.service.Mock;

import com.amayorov.hostel.AbstractHostelTestMockito;
import com.amayorov.hostel.domain.dto.security.UserDTO;
import com.amayorov.hostel.domain.entity.security.User;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.security.UserRepo;
import com.amayorov.hostel.service.security.UserServiceImpl;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTestMockito extends AbstractHostelTestMockito {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepo userRepo;

	@Mock
	private PasswordEncoder passwordEncoder;


	@Test
	public void createUser_AlreadyExists() {
		UserDTO userDTO = new UserDTO(null,"test", "test", new HashSet<>());
		when(userRepo.findByUserName(anyString())).thenReturn(Optional.of(new User()));
		assertThatThrownBy(() -> userService.createUser(userDTO))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("User with this name >>> ");
	}

	@Test
	public void createUser_Success() {
		UserDTO userDTO = new UserDTO(null,"test","test", new HashSet<>());

		when(userRepo.findByUserName(anyString())).thenReturn(Optional.empty());
		when(userRepo.save(any(User.class))).thenReturn(userDTO.toEntity());

		User result = userService.createUser(userDTO);
		assertThat(result).isEqualTo(userDTO.toEntity());
	}

	@Test
	public void saveUserAfterAddingRole_Success() {
		User user = new User();
		when(userRepo.save(any(User.class))).thenReturn(user);
		User result = userService.saveUser(user);
		assertThat(result).isEqualTo(user);
	}

	@Test
	public void findUserByName_NoUserFound() {
		String anyName = "test";
		when(userRepo.findByUserName(anyString())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.findUserByName(anyName))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No user with this name >>> ");
	}

	@Test
	public void findUserByName_Success() {
		String anyName = "test";
		User user = new User();
		when(userRepo.findByUserName(anyString())).thenReturn(Optional.of(user));

		User result = userService.findUserByName(anyName);
		assertThat(result).isEqualTo(user);
	}

	@Test
	public void deleteUser_LoggedInAccount() {
		String anyName = "test";
		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn(anyName);

		assertThatThrownBy(() -> userService.deleteUser(anyName, principal))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("You can't delete the account on which you logged in, ");
	}

	@Test
	public void deleteUser_NotFound() {
		String anyName = "test";
		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn("check");

		when(userRepo.findByUserName(anyString())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.deleteUser(anyName, principal))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No user with this username exists: ");
	}

	@Test
	public void deleteUser_Success() {
		String anyName = "test";
		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn("check");

		when(userRepo.findByUserName(anyString())).thenReturn(Optional.of(new User()));

		userService.deleteUser(anyName, principal);

		verify(userRepo, times(1)).delete(any(User.class));
	}
}
