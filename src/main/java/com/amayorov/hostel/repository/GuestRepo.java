package com.amayorov.hostel.repository;

import com.amayorov.hostel.domain.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestRepo extends JpaRepository <Guest, Long>{

	Optional<Guest> findByPassport(String passport);
}
