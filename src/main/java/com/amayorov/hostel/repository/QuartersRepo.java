package com.amayorov.hostel.repository;

import com.amayorov.hostel.domain.entity.Quarters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuartersRepo extends JpaRepository <Quarters, Long> {

	Optional<Quarters> findByQuarterNumber(Integer quarterNumber);

}
