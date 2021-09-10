package com.amayorov.hostel.service.NoMock;

import com.amayorov.hostel.AbstractHostelTest;
import com.amayorov.hostel.domain.dto.security.UserDTO;
import com.amayorov.hostel.domain.entity.security.User;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.security.UserRepo;
import com.amayorov.hostel.service.security.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserServiceTest extends AbstractHostelTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepo userRepo;

	@Before
	public void clearDb() {
		userRepo.deleteAll();
	}

	@Test
	public void createUser_AlreadyExists() {
		User user = new User();
		user.setUserName("test");
		userRepo.save(user);

		UserDTO userDTO = new UserDTO(null, user.getUserName(), "test", new HashSet<>());
		assertThatThrownBy(() -> userService.createUser(userDTO))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("User with this name >>> ");
	}

	@Test
	public void createUser_Success() {
		UserDTO userDTO = new UserDTO(null, "test", "test", new HashSet<>());

		User user = userService.createUser(userDTO);

		assertThat(userRepo.findById(user.getId())).isPresent();
		assertThat(user.getUserName()).isEqualTo(userDTO.getUsername());
	}

	@Test
	public void saveUserAfterAddingRole() {
		User user = new User();
		userRepo.save(user);
		User savedUser = userService.saveUser(user);
		assertThat(savedUser).isEqualTo(user);
	}

	@Test
	public void findUserByName_NoUserFound() {
		assertThatThrownBy(() -> userService.findUserByName("test"))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No user with this name >>> ");
	}

	@Test
	public void findUserByName_Success() {
		User user = new User();
		user.setUserName("TestTest");
		userRepo.save(user);

		User result = userService.findUserByName(user.getUserName());
		assertThat(result).isEqualTo(user);
	}

	@Test
	public void deleteUser_LoggedInAccount() {
		Principal principal = () -> "test";
		String anyName = "test";

		assertThatThrownBy(() -> userService.deleteUser(anyName, principal))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("You can't delete the account on which you logged in, " );
	}

	@Test
	public void deleteUser_NoUserFound() {
		Principal principal = () -> "check";
		String anyName = "test";

		assertThatThrownBy(() -> userService.deleteUser(anyName, principal))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No user with this username exists: ");
	}

	@Test
	public void deleteUser_Success() {
		Principal principal = () -> "check";
		String anyName = "test";
		User user = new User();
		user.setUserName(anyName);
		User savedUser = userRepo.save(user);

		assertThat(userRepo.findById(savedUser.getId())).isPresent();
		assertThat(userRepo.findById(savedUser.getId())).contains(savedUser);

		userService.deleteUser(anyName, principal);

		assertThat(userRepo.findById(savedUser.getId())).isEmpty();
	}
}
