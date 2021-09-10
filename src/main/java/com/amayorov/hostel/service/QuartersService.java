package com.amayorov.hostel.service;


import com.amayorov.hostel.domain.dto.CleaningDateDTO;
import com.amayorov.hostel.domain.dto.QuartersDTO;
import com.amayorov.hostel.domain.entity.Quarters;
import org.springframework.lang.NonNull;

import java.util.Set;

public interface QuartersService {


	@NonNull
	Quarters createQuarters(@NonNull QuartersDTO quartersDTO);

	@NonNull
	Quarters changeCategory(@NonNull Long quartersNumber, @NonNull String categoryName);

	@NonNull
	Quarters changeCleaningDate(@NonNull CleaningDateDTO cleaningDateDTO);

	@NonNull
	Integer getPremisesNumber(@NonNull Long quartersNumber) ;

	void deleteQuarters (@NonNull Long quartersNumber) ;

	@NonNull
	Quarters findByNumber(@NonNull Integer quarterNumber);

	@NonNull
	Set<Quarters> findAllByCategories(@NonNull Set<String> categories);
}
