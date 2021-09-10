package com.amayorov.hostel.service;

import com.amayorov.hostel.domain.entity.Presence;
import org.springframework.lang.NonNull;

import java.util.Set;

public interface PresenceService {

	@NonNull
	Set<Presence> findByQuarter(@NonNull Long quarterId);

	void createPresence(@NonNull Presence presence);

	void deletePresence (@NonNull Long presenceId);

}
