package com.amayorov.hostel.service.Mock;

import com.amayorov.hostel.AbstractHostelTestMockito;
import com.amayorov.hostel.domain.dto.security.RoleDTO;
import com.amayorov.hostel.domain.entity.security.Role;
import com.amayorov.hostel.domain.entity.security.User;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.security.RoleRepo;
import com.amayorov.hostel.service.security.RoleServiceImpl;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RoleServiceTestMockito extends AbstractHostelTestMockito {

	@InjectMocks
	private RoleServiceImpl roleService;

	@Mock
	private RoleRepo roleRepo;


	@Test
	public void createRole_AlreadyExists() {
		String anyRole = "test";
		RoleDTO roleDTO = new RoleDTO(anyRole.toUpperCase());
		when(roleRepo.findByRoleName(anyString())).thenReturn(Optional.of(new Role()));
		assertThatThrownBy(() -> roleService.createRole(roleDTO))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("Role with this name >>> \"" + roleDTO.getRoleName() + "\" already exists!");
	}

	@Test
	public void createRole_Success() {
		RoleDTO roleDTO = new RoleDTO("test");
		when(roleRepo.findByRoleName(anyString())).thenReturn(Optional.empty());
		when(roleRepo.save(any(Role.class))).thenReturn(roleDTO.toEntity());

		Role result = roleService.createRole(roleDTO);

		assertThat(result.getRoleName()).isEqualTo(roleDTO.getRoleName().toUpperCase());
	}

	@Test
	public void deleteRole_NoSuchRole() {
		String anyRole = "test";
		when(roleRepo.findByRoleName(anyString().toUpperCase())).thenReturn(Optional.empty());
		assertThatThrownBy(() -> roleService.deleteRole(anyRole))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("Check the spelling, there is no such role >>> " + anyRole + " in system");
	}

	@Test
	public void deleteRole_IfUserHasRole() {
		String anyRole = "test";
		Role role = mock(Role.class);

		when(roleRepo.findByRoleName(anyString().toUpperCase())).thenReturn(Optional.of(role));
		when(role.getUsers()).thenReturn(Set.of(new User()));

		assertThatThrownBy(() -> roleService.deleteRole(anyRole))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("You can't delete the ROLE if any USER has that role, ");
	}

	@Test
	public void deleteRole_Success() {
		String anyRole = "test";

		Role role = mock(Role.class);

		when(roleRepo.findByRoleName(anyString().toUpperCase())).thenReturn(Optional.of(role));
		when(role.getUsers()).thenReturn(new HashSet<>());

		roleService.deleteRole(anyRole);

		verify(roleRepo, times(1)).delete(any(Role.class));
	}

	@Test
	public void findAllByRoleNames_NoRolesFound() {
		assertThatThrownBy(() -> roleService.findAllByRoleNames(Set.of("test")))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No roles found with inserted values!");
	}

	@Test
	public void findAllByRoleNames_NonValid() {
		Role role= mock(Role.class);
		when(role.getRoleName()).thenReturn("TEST");
		when(roleRepo.findAll()).thenReturn(List.of(role));
		String nonValidRole = "NONVALID";
		Set<String> roleSet = Set.of(role.getRoleName(), nonValidRole);
		when(roleRepo.findByRoleName(anyString()))
				.thenReturn(Optional.empty())
				.thenReturn(Optional.of(new Role()));
		assertThatThrownBy(() -> roleService.findAllByRoleNames(roleSet))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("These roles: [" + nonValidRole + "] are not in system, " +
						"so you can`t add it to User.");
	}

	@Test
	public void findAllByRoleNames_Success() {
		Role roleFirst = mock(Role.class);
		Role roleSecond = mock(Role.class);
		when(roleFirst.getRoleName()).thenReturn("TEST");
		when(roleSecond.getRoleName()).thenReturn("TESTTEST");
		when(roleRepo.findAll()).thenReturn(List.of(roleFirst, roleSecond));
		when(roleRepo.findByRoleName(anyString())).thenReturn(Optional.of(new Role()));

		Set<Role> roleSet = roleService.findAllByRoleNames(Set.of("test"));

		assertThat(roleSet).hasSize(1).contains(roleFirst);
	}

}
