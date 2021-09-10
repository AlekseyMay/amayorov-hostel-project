package com.amayorov.hostel;

import com.amayorov.hostel.domain.dto.ValidateQuartersDTO;
import com.amayorov.hostel.domain.dto.GuestDTO;
import com.amayorov.hostel.domain.dto.PresenceDTO;
import com.amayorov.hostel.domain.dto.security.UserDTO;
import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.domain.entity.Quarters;
import org.springframework.lang.NonNull;

import java.util.Set;

public interface HostelBusinessLogic {
	@NonNull
	Set<Guest> findGuestsOfQuarter(@NonNull Integer quarterNumber);

	@NonNull
	Set<Quarters> checkQuarters(@NonNull ValidateQuartersDTO validateQuartersDTO);

	@NonNull
	Guest createGuest(@NonNull GuestDTO guestDTO);

	@NonNull
	Guest addPresence(@NonNull PresenceDTO presenceDTO);

	@NonNull
	UserDTO createUser(@NonNull UserDTO userDTO);

	@NonNull
	UserDTO registrateUser(@NonNull UserDTO userDTO);

	@NonNull
	UserDTO addRoles(@NonNull UserDTO userDTO);

	@NonNull
	UserDTO overrideRoles(@NonNull UserDTO userDTO);
}

