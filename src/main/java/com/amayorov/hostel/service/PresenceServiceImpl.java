package com.amayorov.hostel.service;

import com.amayorov.hostel.domain.entity.Presence;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.PresenceRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {
	private final PresenceRepo presenceRepo;

	@Cacheable(cacheNames = "findByQuarter", key = "#quarterId")
	@NonNull
	@Override
	public Set<Presence> findByQuarter(@NonNull Long quarterId) {
		log.debug("Entering method findByQuarter.");
		log.debug("Trying to find Set of Presence by Quarter ID: {}.", quarterId);
		if (presenceRepo.findPresencesByQuartersId(quarterId).isEmpty()) {
			log.warn("Error in findByQuarter() method. Cause: empty presence list.");
			throw new CustomException("The List of presences is empty, please add.");
		}
		log.info("The Set of presence has been found for quarters ID: {}.", quarterId);
		return presenceRepo.findPresencesByQuartersId(quarterId);
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "findByQuarter", key = "#presence.quarters.id"),
			@CacheEvict(cacheNames = "allGuests", allEntries = true),
			@CacheEvict(cacheNames = "findGuestsOfQuarter", allEntries = true)
	}
	)
	@NonNull
	@Override
	public void createPresence(@NonNull Presence presence) {
		log.debug("Entering method createPresence.");
		log.debug("Trying to create Presence.");
		presenceRepo.save(presence);
		log.info("The Presence has been created.");
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "findByQuarter", allEntries = true),
			@CacheEvict(cacheNames = "guestByPassport", allEntries = true),
			@CacheEvict(cacheNames = "allGuests", allEntries = true)
	}
	)
	@Override
	public void deletePresence(@NonNull Long presenceId) { // not really needed because when you delete guest Presence auto deletes
		log.debug("Entering method deletePresence");
		log.debug("Trying to delete Presence with ID: {}", presenceId);
		Presence presenceDb = presenceRepo.findById(presenceId).orElseThrow(() -> {
			log.error("Error in deletePresence() method. Cause: no presence with this id in DB.");
			return new CustomException("No presence with id: " + presenceId);
		});
		log.info("The Presence with ID: {} has been deleted.", presenceId);
		presenceRepo.delete(presenceDb);
	}
}
