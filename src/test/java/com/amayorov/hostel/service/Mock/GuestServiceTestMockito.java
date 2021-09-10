package com.amayorov.hostel.service.Mock;

import com.amayorov.hostel.AbstractHostelTestMockito;
import com.amayorov.hostel.domain.dto.GuestDTO;
import com.amayorov.hostel.domain.dto.PresenceDTO;
import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.GuestRepo;
import com.amayorov.hostel.service.GuestServiceImpl;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class GuestServiceTestMockito extends AbstractHostelTestMockito {

	@InjectMocks
	private GuestServiceImpl guestService;

	@Mock
	private GuestRepo guestRepo;

	@Test
	public void createGuest_AlreadyExists() {
		GuestDTO guestDTO = mock(GuestDTO.class);
		when(guestDTO.getPassport()).thenReturn("");
		when(guestRepo.findByPassport(anyString())).thenReturn(Optional.of(new Guest()));

		assertThatThrownBy(() -> guestService.createGuest(guestDTO))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("Guest with this passport >>> " );
	}

	@Test
	public void createGuest_Success() {

		GuestDTO guestDTO = mock(GuestDTO.class);
		Guest afterToEntityGuest = new Guest();
		when(guestDTO.getPassport()).thenReturn("");
		when(guestRepo.findByPassport(anyString())).thenReturn(Optional.empty());

		when(guestDTO.toEntity()).thenReturn(afterToEntityGuest);

		when(guestRepo.save(any(Guest.class))).thenReturn(afterToEntityGuest);

		Guest result = guestService.createGuest(guestDTO);

        verify(guestDTO, times(1)).toEntity();
		assertThat(result).isEqualTo(afterToEntityGuest);
	}

	@Test
	public void changeGuest_NoGuestExists() {
		GuestDTO guestDTO = new GuestDTO("Test", "Test", "Test",
				"Test", new byte[]{}, new Date(), mock(PresenceDTO.class));

		when(guestRepo.findByPassport(anyString())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> guestService.changeGuest(guestDTO))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("Guest not found with passport: ");
	}

	@Test
	public void changeGuest_Success() {

		GuestDTO guestDTO = mock(GuestDTO.class);
		when(guestDTO.getPassport()).thenReturn("Test");
		when(guestDTO.getFirstName()).thenReturn("Test");
		when(guestDTO.getLastName()).thenReturn("Test");
		when(guestDTO.getPatronymic()).thenReturn("Test");
		when(guestDTO.getDateOfBirth()).thenReturn(new Date());
		when(guestDTO.getPhoto()).thenReturn(new byte[]{});

		Guest guestFromDb = mock(Guest.class);
		when(guestFromDb.getId()).thenReturn(999L);

		Guest finalGuest = spy(Guest.class);
		finalGuest.setFirstName(guestDTO.getFirstName());
		finalGuest.setLastName(guestDTO.getLastName());
		finalGuest.setPassport(guestDTO.getPassport());
		finalGuest.setPatronymic(guestDTO.getPatronymic());
		finalGuest.setDateOfBirth(guestDTO.getDateOfBirth());
		finalGuest.setPhoto(guestDTO.getPhoto());

		when(guestRepo.findByPassport(anyString())).thenReturn(Optional.of(guestFromDb));
		when(guestDTO.toEntity()).thenReturn(finalGuest);
		when(guestRepo.save(any(Guest.class))).thenReturn(finalGuest);

		Guest result = guestService.changeGuest(guestDTO);

		assertThat(result).isNotEqualTo(guestFromDb);
		verify(finalGuest, times(1)).setId(anyLong());
	}


	@Test
	public void changeGuestForPresenceAdd_Success() {
		Guest guest = mock(Guest.class);
		when(guestRepo.save(any(Guest.class))).thenReturn(guest);
		Guest result = guestService.addPresence(guest);

		assertThat(result).isEqualTo(guest);
	}

	@Test
	public void getAllGuest_NoGuestsFound() {
		when(guestRepo.findAll()).thenReturn(new ArrayList<>());

		assertThatThrownBy(() -> guestService.getAllGuest())
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("The List of guests is empty, please add guests");
	}

	@Test
	public void getAllGuest_Success() {
		Guest guest = mock(Guest.class);
		List<Guest> guestList = List.of(guest);
		when(guestRepo.findAll()).thenReturn(guestList);
		Set<Guest> result = guestService.getAllGuest();

		assertThat(result).isEqualTo(new HashSet<>(guestList));
	}

	@Test
	public void getGuestByPassport_NoGuestFound() {
		String test = "Test";
		when(guestRepo.findByPassport(anyString())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> guestService.getGuestByPassport(test))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No guest with this passport exists: ");
	}

	@Test
	public void getGuestsByPassport_Success() {
		String passport = "test";
        Guest guest = spy(Guest.class);
        guest.setPassport(passport);
		when(guestRepo.findByPassport(anyString())).thenReturn(Optional.of(guest));
		Guest result = guestService.getGuestByPassport(passport);

		assertThat(result.getPassport()).isEqualTo(passport);
	}

	@Test
	public void deleteGuest_NoGuestFound() {
		String test = "Test";
		when(guestRepo.findByPassport(anyString())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> guestService.deleteGuest(test))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No guest with this passport exists: ");
	}

	@Test
	public void deleteGuest_Success() {
		String passport = "test";
		when(guestRepo.findByPassport(anyString())).thenReturn(Optional.of(new Guest()));
		guestService.deleteGuest(passport);

		verify(guestRepo, times(1)).delete(any(Guest.class));
	}
}
