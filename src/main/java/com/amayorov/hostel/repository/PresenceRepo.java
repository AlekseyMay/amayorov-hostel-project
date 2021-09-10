package com.amayorov.hostel.repository;

import com.amayorov.hostel.domain.entity.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PresenceRepo extends JpaRepository <Presence, Long> {
	//using left join fetch to avoid LazyInitializationException correctly, without using 'fetch = FetchType.EAGER' https://thorben-janssen.com/lazyinitializationexception/
	@Query("select p from Presence p left join fetch p.guest g left join fetch g.presences where p.quarters.id = :quartersId")
	Set<Presence> findPresencesByQuartersId(@Param("quartersId") Long quartersId);
}
