package com.amayorov.hostel.service.NoMock;

import com.amayorov.hostel.AbstractHostelTest;
import com.amayorov.hostel.domain.dto.security.RoleDTO;
import com.amayorov.hostel.domain.entity.security.Role;
import com.amayorov.hostel.domain.entity.security.User;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.security.RoleRepo;
import com.amayorov.hostel.repository.security.UserRepo;
import com.amayorov.hostel.service.security.RoleService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
public class RoleServiceTest extends AbstractHostelTest {

	@Autowired
	private RoleService roleService;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private UserRepo userRepo;

	@Before
	public void clearDb() {
		roleRepo.deleteAll();
		userRepo.deleteAll();
	}

	@Test
	public void createRole_AlreadyExists() {
		String roleName = "test";
		Role role = new Role();
		role.setRoleName(roleName.toUpperCase());
		roleRepo.save(role);
		RoleDTO newRole = new RoleDTO(roleName);

		assertThatThrownBy(() -> roleService.createRole(newRole))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("Role with this name >>> ");
	}

	@Test
	public void createRole_Success() {
		String roleName = "test";
		RoleDTO roleDTO = new RoleDTO(roleName.toUpperCase());

		Role result = roleService.createRole(roleDTO);

		assertThat(result.getRoleName()).isEqualTo(roleDTO.getRoleName());
	}

	@Test
	public void deleteRole_NoSuchRole() {
		String roleName = "test";

		assertThatThrownBy(() -> roleService.deleteRole(roleName))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("Check the spelling, there is no such role");
	}

	@Test
	public void deleteRole_IfRoleHasUsers() {
		String roleName = "test";
		Role role = new Role();
		role.setRoleName(roleName.toUpperCase());
		Role roleSaved = roleRepo.save(role);
		User user = new User();
		user.setRoles(Set.of(roleSaved));
		userRepo.save(user);

		assertThatThrownBy(() -> roleService.deleteRole(roleName))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("You can't delete the ROLE if any USER has that role,");
	}

	@Test
	public void deleteRole_Success() {
		String roleName = "test";
		Role role = new Role();
		role.setRoleName(roleName.toUpperCase());
		Role roleSaved = roleRepo.save(role);

		assertThat(roleRepo.findByRoleName(roleSaved.getRoleName())).isPresent();

		roleService.deleteRole(roleName);

		assertThat(roleRepo.findByRoleName(roleSaved.getRoleName())).isEmpty();
	}

	@Test
	public void findAllByRoleNames_IsEmpty() {
		assertThatThrownBy(() -> roleService.findAllByRoleNames(Set.of("test")))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No roles found with inserted values!");
	}

	@Test
	public void findAllByRoleNames_NonValidRoles() {
		Role role = new Role();
		role.setRoleName("TEST");
		roleRepo.save(role);

		String nonValidRole = "NONVALID";
		Set<String> setRole = Set.of(role.getRoleName(), nonValidRole);

		assertThatThrownBy(() -> roleService.findAllByRoleNames(setRole))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("These roles: [" + nonValidRole + "] are not in system, so you can`t add it to User.");
	}

	@Test
	public void findAllByRoleNames_Success() {
		Role role = new Role();
		role.setRoleName("TEST");
		Role savedRole = roleRepo.save(role);

		Set<Role> rolesSet = roleService.findAllByRoleNames(Set.of("test"));

		assertThat(rolesSet).contains(savedRole);
	}
}

