package com.amayorov.hostel.service.Mock;

import com.amayorov.hostel.AbstractHostelTestMockito;
import com.amayorov.hostel.HostelBusinessLogicImpl;
import com.amayorov.hostel.domain.dto.GuestDTO;
import com.amayorov.hostel.domain.dto.PresenceDTO;
import com.amayorov.hostel.domain.dto.ValidateQuartersDTO;
import com.amayorov.hostel.domain.dto.security.UserDTO;
import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.domain.entity.Presence;
import com.amayorov.hostel.domain.entity.Quarters;
import com.amayorov.hostel.domain.entity.security.Role;
import com.amayorov.hostel.domain.entity.security.User;
import com.amayorov.hostel.service.GuestService;
import com.amayorov.hostel.service.PresenceService;
import com.amayorov.hostel.service.QuartersService;
import com.amayorov.hostel.service.security.RoleService;
import com.amayorov.hostel.service.security.UserService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class HostelBusinessLogicTestMockito extends AbstractHostelTestMockito {

	@InjectMocks
	private HostelBusinessLogicImpl hostelBusinessLogic;

	@Mock
	private GuestService guestService;

	@Mock
	private PresenceService presenceService;

	@Mock
	private QuartersService quartersService;

	@Mock
	private RoleService roleService;

	@Mock
	private UserService userService;


	@Test
	public void findGuestsOfQuarter_Success() {
		Set<Presence> presenceSet = new HashSet<>();
		Quarters quarters = mock(Quarters.class);
		when(quarters.getId()).thenReturn(88L);
		Guest guest = new Guest();
		presenceSet.add(new Presence(new Date(), new Date(), guest, quarters));

		when(quartersService.findByNumber(anyInt())).thenReturn(quarters);
		when(presenceService.findByQuarter(anyLong())).thenReturn(presenceSet);

		Set<Guest> result = hostelBusinessLogic.findGuestsOfQuarter(1111);
		assertThat(result).hasSize(1).contains(guest).isEqualTo(Set.of(guest));
	}

	@Test
	public void createGuestWithPresence_Success() {
		PresenceDTO presenceDTO = mock(PresenceDTO.class);
		when(presenceDTO.getCheckInDate()).thenReturn(new Date());
		when(presenceDTO.getQuartersNumber()).thenReturn(99);
		when(presenceDTO.getCheckOutDate()).thenReturn(new Date());
		GuestDTO validGuestDTO = new GuestDTO("test", "test", "test",
				"test", new byte[]{}, new Date(), presenceDTO);
		Guest guest = spy(Guest.class);
		guest.setLastName(validGuestDTO.getLastName());
		guest.setPassport(validGuestDTO.getPassport());
		guest.setFirstName(validGuestDTO.getFirstName());
		guest.setDateOfBirth(validGuestDTO.getDateOfBirth());

		when(quartersService.findByNumber(anyInt())).thenReturn(new Quarters());
		when(guestService.createGuest(any(GuestDTO.class))).thenReturn(guest);

		Guest result = hostelBusinessLogic.createGuest(validGuestDTO);

		verify(presenceService, times(1)).createPresence(any(Presence.class));
		verify(guest, times(1)).getPresences();
		assertThat(result.getPassport()).isEqualTo(validGuestDTO.getPassport());
		assertThat(result.getPresences()).hasSize(1);
	}


	@Test
	public void createPresenceAndAddToExistingGuest_Success() {
		PresenceDTO presenceAddDTO = new PresenceDTO("TEST", 11, new Date(), new Date());

		Guest guest = spy(Guest.class);
		guest.setLastName("test");
		guest.setPassport("TEST");
		guest.setFirstName("test");
		guest.setDateOfBirth(new Date());
		guest.setPresences(new HashSet<>());


		when(quartersService.findByNumber(anyInt())).thenReturn(new Quarters());
		when(guestService.getGuestByPassport(anyString())).thenReturn(guest);
		when(guestService.addPresence(any(Guest.class))).thenReturn(guest);
		Guest result = hostelBusinessLogic.addPresence(presenceAddDTO);

		assertThat(result.getPassport()).isEqualTo(guest.getPassport());
		verify(presenceService, times(1)).createPresence(any(Presence.class));
		verify(guest, times(1)).getPresences();
	}

	@Test
	public void checkIfQuartersAvailable_Success() {

		Set<Quarters> quarters = Set.of(new Quarters());
		ValidateQuartersDTO validateQuarters = mock(ValidateQuartersDTO.class);
		when(quartersService.findAllByCategories(anySet())).thenReturn(quarters);

		Set<Quarters> result = hostelBusinessLogic.checkQuarters(validateQuarters);

		assertThat(result).hasSize(1);

	}

	@Test
	public void createUserWithRoles_Success() {
		Set<Role> roleSet = new HashSet<>();
		Role role = mock(Role.class);
		when(role.getRoleName()).thenReturn("TESTTEST");
		roleSet.add(role);
		Set<String> stringRoleSet = new HashSet<>();
		stringRoleSet.add("TESTTEST");
		UserDTO userDTO = new UserDTO(null,"test", "test", stringRoleSet);
		User user = mock(User.class);
		when(roleService.findAllByRoleNames(anySet())).thenReturn(roleSet);
		when(userService.createUser(any(UserDTO.class))).thenReturn(user);

		User userWithRole = mock(User.class);

		when(userWithRole.getRoles()).thenReturn(roleSet);
		when(userWithRole.getUserName()).thenReturn(userDTO.getUsername());
		when(userWithRole.getPassword()).thenReturn(userDTO.getPassword());
		when(userService.saveUser(any(User.class))).thenReturn(userWithRole);

		UserDTO userOutputDTO = hostelBusinessLogic.createUser(userDTO);

		verify(user, times(1)).setRoles(anySet());
		assertThat(userWithRole.getPassword()).isEqualTo(userDTO.getPassword());
		assertThat(userOutputDTO.getUsername()).isEqualTo(userWithRole.getUserName());
		assertThat(userOutputDTO.getRoles()).isEqualTo(userDTO.getRoles());
	}

	@Test
	public void createUserWithRegistration_Success() {
		Set<Role> roleSet = new HashSet<>();
		Role role = mock(Role.class);
		when(role.getRoleName()).thenReturn("TEST");
		roleSet.add(role);
		UserDTO userDTO = new UserDTO(null, "test", "test", null);
		User user = mock(User.class);
		User finalUser = mock(User.class);

//		when(finalUser.getRoles()).thenReturn(roleSet);
		when(finalUser.getUserName()).thenReturn(userDTO.getUsername());
		when(finalUser.getPassword()).thenReturn(userDTO.getPassword());

		when(roleService.findAllByRoleNames(anySet())).thenReturn(roleSet);
		when(userService.createUser(any(UserDTO.class))).thenReturn(user);
		when(userService.saveUser(any(User.class))).thenReturn(finalUser);

		UserDTO outputUserDTO = new UserDTO(null, "test", "test",
				Set.of(role.getRoleName()));
		MockedStatic<UserDTO> userStaticDTO = mockStatic(UserDTO.class);
		userStaticDTO.when(() -> UserDTO.fromEntity(any(User.class))).thenReturn(outputUserDTO);

		UserDTO result = hostelBusinessLogic.registrateUser(userDTO);

		verify(user, times(1)).setRoles(anySet());
		assertThat(result.getPassword()).isEqualTo(finalUser.getPassword());
		assertThat(result.getUsername()).isEqualTo(finalUser.getUserName());
		System.out.println(result.getRoles());
		assertThat(result.getRoles()).isEqualTo(outputUserDTO.getRoles());
		userStaticDTO.close();
	}

	@Test
	public void addNewRolesToExisting_Success() {
		UserDTO userRoleAddDTO = new UserDTO(null, "test", null, Set.of("NEW_ROLE"));
		User user = spy(User.class);
		Role role = new Role();
		role.setRoleName("EXISTING_ROLE");
		Set<Role> roleFromDb = new HashSet<>();
		roleFromDb.add(role);
		user.setRoles(roleFromDb);
		user.setUserName(userRoleAddDTO.getUsername());
		user.setId(99L);

		Role roleNew = new Role();
		roleNew.setRoleName("NEW_ROLE");
		Set<Role> roleSet = new HashSet<>();
		roleSet.add(roleNew);

		User updatedUser = new User();
		updatedUser.setRoles(roleFromDb);
		updatedUser.getRoles().add(roleNew);
		updatedUser.setUserName(user.getUserName());
		updatedUser.setId(user.getId());

		UserDTO userDTO = mock(UserDTO.class);
		when(userDTO.getId()).thenReturn(updatedUser.getId());
		when(userDTO.getUsername()).thenReturn(updatedUser.getUserName());
		when(userDTO.getRoles()).thenReturn(updatedUser.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()));

		when(userService.findUserByName(anyString())).thenReturn(user);
		when(roleService.findAllByRoleNames(anySet())).thenReturn(roleSet);
		when(userService.saveUser(any(User.class))).thenReturn(updatedUser);

		MockedStatic<UserDTO> userStaticDTO = mockStatic(UserDTO.class);
		userStaticDTO.when(() -> UserDTO.fromEntity(any(User.class))).thenReturn(userDTO);

		UserDTO result = hostelBusinessLogic.addRoles(userRoleAddDTO);

		assertThat(result.getRoles()).contains(role.getRoleName());
		assertThat(result.getRoles()).hasSize(2);
		verify(user, times(1)).getRoles();
		assertThat(result.getId()).isEqualTo(userDTO.getId());
		assertThat(result.getUsername()).isEqualTo(userDTO.getUsername());
		userStaticDTO.close();
	}

	@Test
	public void overrideExistingRoles_Success() {
		UserDTO userRoleAddDTO = new UserDTO(null,"test", null, Set.of("NEW_ROLE"));
		User user = spy(User.class);
		Role role = new Role();
		role.setRoleName("EXISTING_ROLE");
		Set<Role> roleFromDb = new HashSet<>();
		roleFromDb.add(role);
		user.setRoles(roleFromDb);
		user.setUserName(userRoleAddDTO.getUsername());
		user.setId(99L);

		Role roleNew = new Role();
		roleNew.setRoleName("NEW_ROLE");
		Set<Role> roleSet = new HashSet<>();
		roleSet.add(roleNew);

		User updatedUser = new User();
		updatedUser.setRoles(roleSet);
		updatedUser.setUserName(user.getUserName());
		updatedUser.setId(user.getId());

		UserDTO userDTO = mock(UserDTO.class);
		when(userDTO.getId()).thenReturn(updatedUser.getId());
		when(userDTO.getUsername()).thenReturn(updatedUser.getUserName());
		when(userDTO.getRoles()).thenReturn(updatedUser.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()));

		when(userService.findUserByName(anyString())).thenReturn(user);
		when(roleService.findAllByRoleNames(anySet())).thenReturn(roleSet);
		when(userService.saveUser(any(User.class))).thenReturn(updatedUser);

		MockedStatic<UserDTO> userDTOMockedStatic = mockStatic(UserDTO.class);
		userDTOMockedStatic.when(() -> UserDTO.fromEntity(any(User.class))).thenReturn(userDTO);

		UserDTO result = hostelBusinessLogic.overrideRoles(userRoleAddDTO);

		assertThat(result.getRoles()).doesNotContain(role.getRoleName());
		assertThat(result.getRoles()).isEqualTo(userRoleAddDTO.getRoles());
		assertThat(result.getRoles()).hasSize(1);
		assertThat(result.getId()).isEqualTo(userDTO.getId());
		assertThat(result.getUsername()).isEqualTo(userDTO.getUsername());
		userDTOMockedStatic.close();
	}
}
