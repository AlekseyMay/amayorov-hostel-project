package com.amayorov.hostel.service;

import com.amayorov.hostel.domain.dto.GuestDTO;
import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.GuestRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {

	private final GuestRepo guestRepo;


	@NonNull
	@Override
	public Guest createGuest(@NonNull GuestDTO guestDTO) {
		log.debug("Entering method createGuest");
		log.debug("Trying to create Guest with the following data: {}", guestDTO);
		if (guestRepo.findByPassport(guestDTO.getPassport()).isPresent()) {
			log.error("Error in createGuest() method. Cause: guest passport already exists in DB");
			throw new CustomException("Guest with this passport >>> " + guestDTO.getPassport() + " already exists!");
		}
		Guest guest = guestDTO.toEntity();
		guestRepo.save(guest);
		log.info("Guest with the following data: {} has been created", guest);
		return guest;
	}

	@Caching(evict = @CacheEvict(cacheNames = "allGuests", allEntries = true),
	         put = @CachePut(cacheNames = "guestByPassport", key = "#guestDTO.passport")
	)
	@NonNull
	@Override
	public Guest changeGuest(@NonNull GuestDTO guestDTO) { // changes guest personal info only
		log.debug("Entering method changeGuest");
		log.debug("Trying to find the guest by ID and then change guest's personal information");
		Guest guest = guestRepo.findByPassport(guestDTO.getPassport()).orElseThrow(() -> {
			log.error("Error in changeGuest() method. Cause: no guest in DB with this passport");
			return new CustomException("Guest not found with passport: " + guestDTO.getPassport());
		});
		Guest finalGuest = guestDTO.toEntity();
		finalGuest.setId(guest.getId());
		Guest guestOutput = guestRepo.save(finalGuest);
		log.info("New personal information has been set successfully");
		return guestOutput;
	}

	@Caching(evict = @CacheEvict(cacheNames = "allGuests", allEntries = true),
			put = @CachePut(cacheNames = "guestByPassport", key = "#guest.passport")
	)
	@NonNull
	@Override
	public Guest addPresence(@NonNull Guest guest) { // just support method for adding additional Presence to Guest
		log.debug("Entering method addPresence");
		log.debug("After new Presence has been added for existing Guest, " +
				"trying to save the existing guest");
		log.info("New Presence has been added successfully to the Guest");
		return guestRepo.save(guest);
	}

	@Cacheable(cacheNames = "allGuests")
	@NonNull
	@Override
	public Set<Guest> getAllGuest() {
		log.debug("Entering method getAllGuest");
		log.debug("Trying to get the List of all existing guests");
		if (guestRepo.findAll().isEmpty()) {
			log.error("Error in getAllGuest() method. Cause: no guests found.");
			throw new CustomException("The List of guests is empty, please add guests");
		}
		log.info("The List has been obtained successfully");
		return new HashSet<>(guestRepo.findAll());
	}

	@Cacheable(cacheNames = "guestByPassport", key = "#passport")
	@NonNull
	@Override
	public Guest getGuestByPassport(@NonNull String passport) {
		log.debug("Entering method getGuestByPassport");
		log.debug("Trying to find the Guest by the following passport: {}", passport.replaceAll("\\s",""));
		Guest guest = guestRepo.findByPassport(passport.replaceAll("\\s","")).orElseThrow(() -> {
			log.error("Error in getGuestByPassport() method. Cause: no guest found in DB with passport");
			return new CustomException("No guest with this passport exists: " + passport + ", pls check the passport");
		});
		log.info("The guest with passport: {} has been found successfully", passport.replaceAll("\\s",""));
		return guest;

	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "allGuests", allEntries = true),
			@CacheEvict(cacheNames = "guestByPassport", key = "#passport"),
			@CacheEvict(cacheNames = "findByQuarter", allEntries = true)
	})
	@Override
	public void deleteGuest(@NonNull String passport) {
		log.debug("Entering method deleteGuest");
		log.debug("Trying to delete guest with passport: {}", passport.replaceAll("\\s",""));
		Guest guestDb = guestRepo.findByPassport(passport.replaceAll("\\s","")).orElseThrow(() -> {
			log.error("Error in deleteGuest() method. Cause: Cause: no guest found in DB with passport");
			return new CustomException("No guest with this passport exists: " + passport + ", pls check the passport");
		});
		log.info("Guest with passport: {} has been deleted successfully", passport.replaceAll("\\s",""));
		guestRepo.delete(guestDb);
	}
}
