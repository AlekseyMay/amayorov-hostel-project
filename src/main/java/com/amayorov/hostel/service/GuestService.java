package com.amayorov.hostel.service;

import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.domain.dto.GuestDTO;
import org.springframework.lang.NonNull;

import java.util.Set;

public interface GuestService {

	@NonNull
	Guest createGuest(@NonNull GuestDTO guestDTO);

	@NonNull
	Guest changeGuest(@NonNull GuestDTO guestDTO);

	@NonNull
	Guest addPresence(@NonNull Guest guest);

	@NonNull
	Set<Guest> getAllGuest();

	@NonNull
	Guest getGuestByPassport(@NonNull String passport);

	void deleteGuest (@NonNull String passport);

}
