package com.amayorov.hostel.service.NoMock;

import com.amayorov.hostel.AbstractHostelTest;
import com.amayorov.hostel.HostelBusinessLogic;
import com.amayorov.hostel.domain.dto.*;
import com.amayorov.hostel.domain.dto.security.RoleDTO;
import com.amayorov.hostel.domain.dto.security.UserDTO;
import com.amayorov.hostel.domain.entity.Category;
import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.domain.entity.Presence;
import com.amayorov.hostel.domain.entity.Quarters;
import com.amayorov.hostel.domain.entity.security.Role;
import com.amayorov.hostel.domain.entity.security.User;
import com.amayorov.hostel.domain.enums.CategoryEnum;
import com.amayorov.hostel.repository.GuestRepo;
import com.amayorov.hostel.repository.PresenceRepo;
import com.amayorov.hostel.repository.QuartersRepo;
import com.amayorov.hostel.repository.security.RoleRepo;
import com.amayorov.hostel.repository.security.UserRepo;
import com.amayorov.hostel.service.CategoryService;
import com.amayorov.hostel.service.GuestService;
import com.amayorov.hostel.service.PresenceService;
import com.amayorov.hostel.service.QuartersService;
import com.amayorov.hostel.service.security.RoleService;
import com.amayorov.hostel.service.security.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class HostelBusinessLogicTest extends AbstractHostelTest {

	@Autowired
	private HostelBusinessLogic hostelBusinessLogic;
	@Autowired
	private GuestService guestService;
	@Autowired
	private PresenceService presenceService;
	@Autowired
	private QuartersService quartersService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private GuestRepo guestRepo;
	@Autowired
	private PresenceRepo presenceRepo;
	@Autowired
	private QuartersRepo quartersRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private RoleRepo roleRepo;

	@Before
	public void clearDb() {
		guestRepo.deleteAll();
		presenceRepo.deleteAll();
		quartersRepo.deleteAll();
		userRepo.deleteAll();
		roleRepo.deleteAll();
	}

	@Transactional
	@Test
	public void findGuestsOfQuarter() {
		int quarterNumber = 99;
		Quarters quarters = new Quarters();
		quarters.setQuarterNumber(quarterNumber);
		quartersRepo.save(quarters);
		Guest guest = new Guest();
		guest.setFirstName("test");
		guestRepo.save(guest);
		Presence presence = new Presence();
		presence.setQuarters(quarters);
		presence.setGuest(guest);
		presenceRepo.save(presence);

		Set<Guest> guestSet = hostelBusinessLogic.findGuestsOfQuarter(quarterNumber);

		assertThat(guestSet).contains(guest);
	}

	@Test
	public void createGuest_Success() {
		Quarters quarters = new Quarters();
		quarters.setQuarterNumber(111);
		quartersRepo.save(quarters);
		PresenceDTO presenceDTO = new PresenceDTO(null, quarters.getQuarterNumber(), new Date(), new Date());
		GuestDTO validGuestDTO = new GuestDTO("test", "test", "test",
				"test", new byte[]{},new Date(), presenceDTO);
		Guest guest = hostelBusinessLogic.createGuest(validGuestDTO);
		assertThat(guest.getPassport()).isEqualTo(validGuestDTO.getPassport());
		assertThat(guest.getPresences()).hasSize(1);
		assertThat(new ArrayList<>(guest.getPresences()).get(0).getQuarters().getQuarterNumber())
				.isEqualTo(validGuestDTO.getPresence().getQuartersNumber());
	}

	@Transactional
	@Test
	public void addPresence_Success() { // creates new presence and adds to a guest
		PresenceDTO emptyPassport = new PresenceDTO("111", 222,
				new Date(), new Date());
		Quarters quarters =  new Quarters();
		quarters.setQuarterNumber(222);
		quartersRepo.save(quarters);
		Guest guest = new Guest();
		guest.setPassport("111");
		guestRepo.save(guest);

		Guest result = hostelBusinessLogic.addPresence(emptyPassport);

		assertThat(result.getPassport()).isEqualTo("111");
		assertThat(result.getPresences()).hasSize(1);
		assertThat(new ArrayList<>(result.getPresences()).get(0).getQuarters().getQuarterNumber())
				.isEqualTo(emptyPassport.getQuartersNumber());
	}


	@Test
	public void checkIfQuartersAvailable_Success() {
		CategoryDTO categoryDTO = new CategoryDTO(CategoryEnum.valueOf("Apartment"), "TestTestTest");
		Category categoryDb = categoryService.createCategory(categoryDTO);
		QuartersDTO quartersDTO = new QuartersDTO(11, 11, new Date(), categoryDb.getCategoryName().toString());
		Quarters quartersDb = quartersService.createQuarters(quartersDTO);
		GuestDTO guestDTO = new GuestDTO("test", "test", "test",
				"test", new byte[]{}, new Date(), new PresenceDTO(
						null,
						quartersDTO.getQuarterNumber(),
				Date.from(LocalDate.of(2021, 06, 01)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()),
				Date.from(LocalDate.of(2021, 06, 15)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()))
		);
		Guest guestDb = guestService.createGuest(guestDTO);
		Presence presence = new Presence(
				guestDTO.getPresence().getCheckInDate(),
				guestDTO.getPresence().getCheckOutDate(),
				guestDb,
				quartersDb
		);
		presenceService.createPresence(presence);

		ValidateQuartersDTO validateQuartersDTO = new ValidateQuartersDTO(
				Date.from(LocalDate.of(2021, 05, 01)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()),
				Date.from(LocalDate.of(2021, 05, 15)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()),
				Set.of("Apartment"),
				1
		);
		Set<Quarters> result = hostelBusinessLogic.checkQuarters(validateQuartersDTO);

		assertThat(result).hasSize(1).contains(quartersRepo.findById(quartersDb.getId()).get());
	}

	@Test
	public void createUserWithRoles_Success() {
		roleService.createRole(new RoleDTO("TESTROLE"));
		UserDTO userDTO = new UserDTO(null, "test", "test", Set.of("TESTROLE"));
		UserDTO userOutputDTO = hostelBusinessLogic.createUser(userDTO);

		assertThat(userOutputDTO.getUsername()).isEqualTo(userDTO.getUsername());
		assertThat(userOutputDTO.getRoles()).isEqualTo(userDTO.getRoles());
	}

	@Test
	public void createUserWithRegistration_Success() {
		Role roleDb = new Role();
		roleDb.setRoleName("MANAGER");
		roleRepo.save(roleDb);
		UserDTO userDTO = new UserDTO(null,"test", "test", null);
		UserDTO userOutputDTO = hostelBusinessLogic.registrateUser(userDTO);

		assertThat(userOutputDTO.getUsername()).isEqualTo(userDTO.getUsername());
		assertThat(userOutputDTO.getRoles()).isEqualTo(Set.of("MANAGER"));
	}

	@Test
	public void addNewRolesToExisting_Success() {
		Role roleDb = roleService.createRole(new RoleDTO("manager"));
		roleService.createRole(new RoleDTO("admin"));
		UserDTO userDTO = new UserDTO(null,"test", "test", Set.of("manager"));
		Set<Role> roleSet = roleService.findAllByRoleNames(userDTO.getRoles());
		User user = userService.createUser(userDTO);
		user.setRoles(roleSet);

		User userWithRole = userService.saveUser(user);

		assertThat(userWithRole.getRoles()).contains(roleDb);

		UserDTO userRoleAddDTO = new UserDTO(null, userWithRole.getUserName(), null, Set.of("admin"));
		UserDTO userOutputDTO = hostelBusinessLogic.addRoles(userRoleAddDTO);

		assertThat(userOutputDTO.getId()).isEqualTo(userWithRole.getId());
		assertThat(userOutputDTO.getUsername()).isEqualTo(userWithRole.getUserName());
		assertThat(userOutputDTO.getRoles()).contains("MANAGER");
		assertThat(userOutputDTO.getRoles()).contains("ADMIN");
	}

	@Test
	public void overrideExistingRoles_Success() {
		Role oldRole = roleService.createRole(new RoleDTO("manager"));
		Role newRole = roleService.createRole(new RoleDTO("admin"));
		UserDTO userDTO = new UserDTO(null, "test", "test", Set.of("manager"));
		Set<Role> roleSet = roleService.findAllByRoleNames(userDTO.getRoles());
		User user = userService.createUser(userDTO);
		user.setRoles(roleSet);

		User userWithRole = userService.saveUser(user);
		assertThat(userWithRole.getRoles()).contains(oldRole);

		UserDTO userRoleAddDTO = new UserDTO(null, userWithRole.getUserName(), null,  Set.of("admin"));
		UserDTO userOutputDTO = hostelBusinessLogic.overrideRoles(userRoleAddDTO);

		assertThat(userOutputDTO.getId()).isEqualTo(userWithRole.getId());
		assertThat(userOutputDTO.getUsername()).isEqualTo(userWithRole.getUserName());
		assertThat(userOutputDTO.getRoles()).hasSize(1).contains(newRole.getRoleName());

	}
}
