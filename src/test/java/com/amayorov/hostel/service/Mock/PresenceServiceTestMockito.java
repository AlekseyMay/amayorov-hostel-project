package com.amayorov.hostel.service.Mock;

import com.amayorov.hostel.AbstractHostelTestMockito;
import com.amayorov.hostel.domain.entity.Presence;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.PresenceRepo;
import com.amayorov.hostel.service.PresenceServiceImpl;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PresenceServiceTestMockito extends AbstractHostelTestMockito {

	@InjectMocks
	private PresenceServiceImpl presenceService;

	@Mock
	private PresenceRepo presenceRepo;

	@Test
	public void findByQuarter_EmptyList() {
        Long customId = 99L;
		when(presenceRepo.findPresencesByQuartersId(anyLong())).thenReturn(new HashSet<>());

		assertThatThrownBy(() -> presenceService.findByQuarter(customId))
				.isInstanceOf(CustomException.class)
				.hasMessage("The List of presences is empty, please add.");
	}

	@Test
	public void findByQuarter_Success() {
        Set<Presence> presenceSet = Set.of(new Presence());
		when(presenceRepo.findPresencesByQuartersId(anyLong())).thenReturn(presenceSet);

		Set<Presence> result = presenceService.findByQuarter(99L);

		assertThat(result).isEqualTo(presenceSet);
	}

	@Test
	public void createPresence_Success() {

		Presence presence = mock(Presence.class);
		when(presenceRepo.save(any(Presence.class))).thenReturn(presence);
		presenceService.createPresence(presence);

		verify(presenceRepo, times(1)).save(any(Presence.class));
	}

	@Test
	public void deletePresence_NoPresenceExists() {
		Long customId = 99L;
		when(presenceRepo.findById(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> presenceService.deletePresence(customId))
				.isInstanceOf(CustomException.class)
				.hasMessage("No presence with id: " + customId);
	}

	@Test
	public void deletePresence_Success() {

		Long customId = 99L;
		when(presenceRepo.findById(anyLong())).thenReturn(Optional.of(new Presence()));

		presenceService.deletePresence(customId);

		verify(presenceRepo, times(1)).delete(any(Presence.class));
	}
}
