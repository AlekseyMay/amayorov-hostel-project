package com.amayorov.hostel;


import com.amayorov.hostel.domain.dto.GuestDTO;
import com.amayorov.hostel.domain.dto.PresenceDTO;
import com.amayorov.hostel.domain.dto.ValidateQuartersDTO;
import com.amayorov.hostel.domain.dto.security.UserDTO;
import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.domain.entity.Presence;
import com.amayorov.hostel.domain.entity.Quarters;
import com.amayorov.hostel.domain.entity.security.Role;
import com.amayorov.hostel.domain.entity.security.User;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.service.GuestService;
import com.amayorov.hostel.service.PresenceService;
import com.amayorov.hostel.service.QuartersService;
import com.amayorov.hostel.service.security.RoleService;
import com.amayorov.hostel.service.security.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HostelBusinessLogicImpl implements HostelBusinessLogic {

	private final GuestService guestService;
	private final PresenceService presenceService;
	private final QuartersService quartersService;
	private final UserService userService;
	private final RoleService roleService;
	@Qualifier("JmsQueueTemplate")
	private final JmsTemplate jmsQueueTemplate;


	@Cacheable(cacheNames = "findGuestsOfQuarter", key = "#quarterNumber")
	@NonNull
	@Override
	public Set<Guest> findGuestsOfQuarter(@NonNull Integer quarterNumber) {
		log.debug("Entering method findGuestsOfQuarter");
		log.debug("Trying to find all the Guests from Quarters with number: {}", quarterNumber);
		Quarters quarter = quartersService.findByNumber(quarterNumber);
		Set<Presence> presences = presenceService.findByQuarter(quarter.getId());
		log.info("The Set of Guests from Quarters with number: {} has been obtained successfully", quarterNumber);
		return presences
				.stream()
				.map(Presence::getGuest)
				.collect(Collectors.toSet());
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "findGuestsOfQuarter", key = "#guestDTO.presence.quartersNumber"),
			@CacheEvict(cacheNames = "checkQuarters", allEntries = true)
	},
			put = @CachePut(cacheNames = "guestByPassport", key = "#guestDTO.passport"))
	@NonNull
	@Override
	public Guest createGuest(@NonNull GuestDTO guestDTO) { // creates guest with presence
		log.debug("Entering method createGuest");
		log.debug("Trying to create the Guest with one Presence, taking into account Guest, Quarters and one Presence");
			PresenceDTO presenceDTO = guestDTO.getPresence();
			Quarters quartersDb = quartersService.findByNumber(presenceDTO.getQuartersNumber());
			Guest guest = guestService.createGuest(guestDTO);
			Presence presenceDb = new Presence(
					presenceDTO.getCheckInDate(),
					presenceDTO.getCheckOutDate(),
					guest,
					quartersDb);
			presenceService.createPresence(presenceDb);
			guest.getPresences().add(presenceDb);
			log.info("The Guest with one Presence has been created successfully.");
			return guest;
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "findGuestsOfQuarter", key = "#presenceDTO.quartersNumber"),
			@CacheEvict(cacheNames = "checkQuarters", allEntries = true)
	},
			put = @CachePut(cacheNames = "guestByPassport", key = "#presenceDTO.guestPassport"))
	@NonNull
	@Override
	public Guest addPresence(@NonNull PresenceDTO presenceDTO) { // creates new presence and adds it to existing guest
		log.debug("Entering method addPresence");
		log.debug("Trying to find the existing Guest by passport: {} and then add a new Presence, " +
				"(only needed when guest have more than one Presence)", presenceDTO.getGuestPassport());
		Quarters quartersDb = quartersService.findByNumber(presenceDTO.getQuartersNumber());
		Guest guest = guestService.getGuestByPassport(presenceDTO.getGuestPassport());
		Presence presenceDb = new Presence(presenceDTO.getCheckInDate(), presenceDTO.getCheckOutDate(), guest, quartersDb);
		presenceService.createPresence(presenceDb);
		guest.getPresences().add(presenceDb);
		log.info("The new Presence has been added successfully to a Guest with passport: {}", guest.getPassport());
		return guestService.addPresence(guest);
	}

	@Cacheable(cacheNames = "checkQuarters")
    @NonNull
    @Override
	public Set<Quarters> checkQuarters(@NonNull ValidateQuartersDTO validateQuartersDTO) { // checks if Quarters are available on inserted dates and with needed amount of rooms
		log.debug("Entering method checkQuarters");
		log.debug("Trying to check if the Quarters with the following categories: {} and the following" +
						" amount of Quarters: {} are available from: {}, till: {}",
				validateQuartersDTO.getCategories(), validateQuartersDTO.getAmountOfQuartersNeeded(),
				validateQuartersDTO.getFrom(), validateQuartersDTO.getTill());
		Set<Quarters> freeQuarters = quartersService.findAllByCategories(validateQuartersDTO.getCategories())
				.stream()
				.filter(quarters -> checkDate(validateQuartersDTO.getFrom(), validateQuartersDTO.getTill(), quarters))
				.collect(Collectors.toSet());

		int amountOfFreeQuarters = freeQuarters.size();
		boolean isAvailable = amountOfFreeQuarters >= validateQuartersDTO.getAmountOfQuartersNeeded();

		if (isAvailable) {
			log.info("Quarters have been successfully checked for availability.");
			return freeQuarters;
		}
		return new HashSet<>();
	}

    @NonNull
	private boolean checkDate(@NonNull Date from, @NonNull Date till, @NonNull Quarters quarters) { // support method for checkIfQuartersAvailable, check if Quarters is free on certain dates
		log.debug("Entering method checkIfQuartersFreeOnDate");
		log.debug("Trying to check if the Quarters are free from {}, till {}", from, till);
		boolean isFree;
		try {
			isFree = presenceService.findByQuarter(quarters.getId())
					.stream()
					.noneMatch(presence -> from.before(presence.getCheckOutDate())
							&& presence.getCheckInDate().before(till));
		} catch (CustomException e) {
			isFree = true;
		}
		log.info("Support checkDate method finished work correctly.");
		return isFree;
	}

	@NonNull
	@Override
	public UserDTO createUser(@NonNull UserDTO userDTO) { // creates User with roles
		log.debug("Entering method createUserWithRoles");
		log.debug("Trying to create User with Roles: {}", userDTO.getRoles());
		Set<Role> roleSet = roleService.findAllByRoleNames(userDTO.getRoles());
		User user = userService.createUser(userDTO);
		user.setRoles(roleSet);
		User userWithRole = userService.saveUser(user);
		log.info("User with following data: {} has been created successfully, password is hidden.", UserDTO.fromEntity(userWithRole));
		if (userDTO.getRoles().stream().anyMatch(x -> x.equalsIgnoreCase("ADMIN"))) { // if User With Admin role is created the queue  will be crated and then the email will be sent
			jmsQueueTemplate.convertAndSend("queue", userDTO);
		}
		return UserDTO.fromEntity(userWithRole);
	}

	@NonNull
	@Override
	public UserDTO registrateUser(@NonNull UserDTO userDTO) { // create User with role: MANAGER after registration
		log.debug("Entering method registrateUser");
		log.debug("Trying to create User with Role MANAGER through registration, password is hidden.");
		Set<Role> roleSet = roleService.findAllByRoleNames(Set.of("MANAGER"));
		User user = userService.createUser(userDTO);
		user.setRoles(roleSet);

		User userWithRole = userService.saveUser(user);
		log.info("User with following data: {} has been created successfully.", UserDTO.fromEntity(userWithRole));
		return UserDTO.fromEntity(userWithRole);
	}

	@NonNull
	@Override
	public UserDTO addRoles(@NonNull UserDTO userDTO) {   // add new roles to existing User`s roles
		log.debug("Entering method addRoles");
		log.debug("Trying to add a new Role to existing, without overriding");
		User user = userService.findUserByName(userDTO.getUsername());
		Set<Role> roleSet = roleService.findAllByRoleNames(userDTO.getRoles());
		user.getRoles().addAll(roleSet);

		User userWithAddedRoles = userService.saveUser(user);
		log.info("The new Roles have been successfully added to existing ones");
		return UserDTO.fromEntity(userWithAddedRoles);
	}

	@NonNull
	@Override
	public UserDTO overrideRoles(@NonNull UserDTO userDTO) { // override existing User`s roles
		log.debug("Entering method overrideRoles");
		log.debug("Trying to override the existing Roles");
		User user = userService.findUserByName(userDTO.getUsername());
		Set<Role> roleSet = roleService.findAllByRoleNames(userDTO.getRoles());
		user.setRoles(roleSet);

		User userWithOverriddenRoles = userService.saveUser(user);
		log.info("The Roles have been successfully overridden");
		return UserDTO.fromEntity(userWithOverriddenRoles);
	}
}
