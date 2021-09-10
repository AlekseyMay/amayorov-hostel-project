package com.amayorov.hostel.service.NoMock;

import com.amayorov.hostel.AbstractHostelTest;
import com.amayorov.hostel.domain.entity.Presence;
import com.amayorov.hostel.domain.entity.Quarters;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.GuestRepo;
import com.amayorov.hostel.repository.PresenceRepo;
import com.amayorov.hostel.repository.QuartersRepo;
import com.amayorov.hostel.service.PresenceService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PresenceServiceTest extends AbstractHostelTest {

	@Autowired
	private PresenceService presenceService;

	@Autowired
	private PresenceRepo presenceRepo;

	@Autowired
	private QuartersRepo quartersRepo;

	@Autowired
	private GuestRepo guestRepo;

	@Before
	public void clearDb() {
		presenceRepo.deleteAll();
		quartersRepo.deleteAll();
		guestRepo.deleteAll();
	}

	@Test
	public void findByQuarter_EmptyList() {
		quartersRepo.save(new Quarters());
		long maxId = quartersRepo.findAll()
				.stream()
				.mapToLong(x -> Math.toIntExact(x.getId()))
				.max()
				.orElseThrow(NoSuchElementException::new);

		assertThatThrownBy(() -> presenceService.findByQuarter(maxId + 1))
				.isInstanceOf(CustomException.class)
				.hasMessage("The List of presences is empty, please add.");
	}

	@Test
	public void findByQuarters_Success() {
		Quarters quarters = new Quarters();
		quartersRepo.save(quarters);
		Presence presence = new Presence();
		presence.setQuarters(quarters);
		presenceRepo.save(presence);

		Set<Presence> check = presenceRepo.findPresencesByQuartersId(presence.getQuarters().getId());
		Set<Presence> result = presenceService.findByQuarter(presence.getQuarters().getId());

		assertThat(result).isEqualTo(check);
	}

	@Test
	public void createPresence_Success() {
		Quarters quarters = new Quarters(); //for cache to pass the test, as cache key is quarters id, so need to add it here
		quartersRepo.save(quarters);
		Presence presence = new Presence();
		presence.setQuarters(quarters);
		presenceService.createPresence(presence);
		Presence check = presenceRepo.save(presence);
		assertThat(check).isEqualTo(presence);
	}

	@Test
	public void deletePresence_NoPresenceExists() {
		assertThatThrownBy(() -> presenceService.deletePresence(Long.MAX_VALUE))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No presence with id: ");
	}

	@Test
	public void deletePresence_Success() {
		Presence presence = new Presence();
		presenceRepo.save(presence);
		Optional<Presence> check = presenceRepo.findById(presence.getId());

		assertThat(check).isPresent().contains(presence);

		presenceService.deletePresence(presence.getId());
		Optional<Presence> result = presenceRepo.findById(presence.getId());

		assertThat(result).isEmpty();
;	}
}
