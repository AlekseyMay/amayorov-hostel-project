package com.amayorov.hostel.service.NoMock;

import com.amayorov.hostel.AbstractHostelTest;
import com.amayorov.hostel.domain.dto.GuestDTO;
import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.GuestRepo;
import com.amayorov.hostel.repository.QuartersRepo;
import com.amayorov.hostel.service.GuestService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GuestServiceTest extends AbstractHostelTest {

	@Autowired
	private GuestService guestService;

	@Autowired
	private GuestRepo guestRepo;

	@Autowired
	private QuartersRepo quartersRepo;

	@Before
	public void clearDb() {
		guestRepo.deleteAll();
		quartersRepo.deleteAll();
	}

	@Test
	public void createGuest_AlreadyExists() {
		String passport = "test";
		Guest guest = new Guest();
		guest.setPassport(passport);
		guestRepo.save(guest);

		GuestDTO guestDTO = new GuestDTO(null, null, null, passport,
				null, null, null);

		assertThatThrownBy(() -> guestService.createGuest(guestDTO))
				.isInstanceOf(CustomException.class)
				.hasMessage("Guest with this passport >>> " + guestDTO.getPassport() + " already exists!");
	}

	@Test
	public void createGuest_Success() {
		GuestDTO guestDTO = new GuestDTO("test", "test", "test", "test",
				new byte[]{}, new Date(), null);

		Guest result = guestService.createGuest(guestDTO);

		assertThat(result.getPassport()).isEqualTo(guestDTO.getPassport());
		assertThat(result.getFirstName()).isEqualTo(guestDTO.getFirstName());
		assertThat(result.getLastName()).isEqualTo(guestDTO.getLastName());
		assertThat(result.getPatronymic()).isEqualTo(guestDTO.getPatronymic());
		assertThat(result.getPhoto()).isEqualTo(guestDTO.getPhoto());
		assertThat(result.getDateOfBirth()).isEqualTo(guestDTO.getDateOfBirth());
	}

	@Test
	public void changeGuest_NoGuestFound() {
		GuestDTO guestDTO = new GuestDTO("Test", "Test", "Test",
				"Test", new byte[]{}, new Date(), null);

		assertThatThrownBy(() -> guestService.changeGuest(guestDTO))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("Guest not found with passport: " + guestDTO.getPassport());
	}

	@Test
	public void changeGuest_Success() {
		Guest guest = new Guest("Test", "Test", "Test",
				"Test", new byte[]{}, new Date());
		Guest guestSavedInDb = guestRepo.save(guest);
		GuestDTO guestDTO = new GuestDTO("TestTest", "TestTest", "TestTest",
				"Test", new byte[]{}, new Date(), null);
		Guest result = guestService.changeGuest(guestDTO);

		assertThat(result.getPassport()).isEqualTo(guestSavedInDb.getPassport());
		assertThat(result).isNotEqualTo(guestSavedInDb);
	}

	@Test
	public void addPresence_Success() {
		Guest savedInDb = new Guest("Test", "Test", "Test",
				"Test", new byte[]{}, new Date());
		guestRepo.save(savedInDb);
		Guest guestWithNewPresence = new Guest("Test", "Test", "Test",
				"Test", new byte[]{}, new Date());
		guestWithNewPresence.setId(savedInDb.getId());
		Guest result = guestService.addPresence(guestWithNewPresence);

		assertThat(result).isEqualTo(guestWithNewPresence);
	}

	@Test
	public void getAllGuest_EmptyList() {
		assertThatThrownBy(() -> guestService.getAllGuest())
				.isInstanceOf(CustomException.class)
				.hasMessage("The List of guests is empty, please add guests");
	}

	@Transactional // otherwise LazyInitializationException https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex
	@Test
	public void getAllGuest_Success() {
		Guest guest = new Guest();
		guestRepo.save(guest);
		Set<Guest> result = guestService.getAllGuest();

		assertThat(result).isEqualTo(Set.of(guest));
	}

	@Test
	public void getGuestByPassport_NoGuestFound() {
		String passport = "TestTest";
		assertThatThrownBy(() -> guestService.getGuestByPassport(passport))
				.isInstanceOf(CustomException.class)
				.hasMessage("No guest with this passport exists: " + passport + ", pls check the passport");
	}

	@Transactional // otherwise LazyInitializationException https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex
	@Test
	public void getGuestByPassport_Success() {
		String passport = "Test";
		Guest guest = new Guest();
		guest.setPassport(passport);
		Guest guestFromDb = guestRepo.save(guest);
		Guest result = guestService.getGuestByPassport(passport);

		assertThat(result).isEqualTo(guestFromDb);

	}

	@Test
	public void deleteGuest_NoGuestFound() {
		String passport = "Test";
		assertThatThrownBy(() -> guestService.deleteGuest(passport))
				.isInstanceOf(CustomException.class)
				.hasMessage("No guest with this passport exists: " + passport + ", pls check the passport");
	}

	@Test
	public void deleteGuest_Success() {
		String passport = "Test";
		Guest savedInDb = new Guest("Test", "Test", "Test",
				passport, new byte[]{}, new Date());
		guestRepo.save(savedInDb);
		assertThat(guestRepo.findByPassport(passport)).isPresent();
		guestService.deleteGuest(passport);
		assertThat(guestRepo.findByPassport(passport)).isEmpty();
	}
}
