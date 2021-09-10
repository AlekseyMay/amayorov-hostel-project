package com.amayorov.hostel.service;

import com.amayorov.hostel.domain.dto.CleaningDateDTO;
import com.amayorov.hostel.domain.dto.QuartersDTO;
import com.amayorov.hostel.domain.entity.Category;
import com.amayorov.hostel.domain.entity.Quarters;
import com.amayorov.hostel.domain.enums.CategoryEnum;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.CategoryRepo;
import com.amayorov.hostel.repository.QuartersRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class QuartersServiceImpl implements QuartersService {

	private final QuartersRepo quartersRepo;
	private final CategoryRepo categoryRepo;

	@Caching(
			put = @CachePut(cacheNames = "findByNumber", key = "#quartersDTO.quarterNumber"),
			evict = {
					@CacheEvict(cacheNames = "findAllByCategories", allEntries = true),
					@CacheEvict(cacheNames = "checkQuarters", allEntries = true)
			}
	)
	@NonNull
	@Override
	public Quarters createQuarters(@NonNull QuartersDTO quartersDTO) {
		log.debug("Entering method createQuarters.");
		log.debug("Trying to create Quarters.");
		if (quartersRepo.findByQuarterNumber(quartersDTO.getQuarterNumber()).isPresent()) {
			log.error("Error in createQuarters() method. Cause: already existing quarters' number.");
			throw new CustomException("Quarters with this quarters number >>> " + quartersDTO.getQuarterNumber() + " already exists!");
		}
			Category categoryDb = categoryRepo.findByCategoryName(Enum.valueOf(CategoryEnum.class, quartersDTO.getCategoryName())).orElseThrow(() -> {
				log.error("Error in createQuarters() method. Cause: no category in table.");
				return new CustomException("No such category name exists right now in table >>> " + quartersDTO.getCategoryName() + ", " +
						"pls check categories and add a new one.");
			});
			Quarters quarters = quartersDTO.toEntity();
			quarters.setCategory(categoryDb);
			log.info("The Quarters have been created successfully.");
			return quartersRepo.save(quarters);
	}

	@Caching(
			put = @CachePut(cacheNames = "findByNumber", key = "#quartersNumber"),
			evict = {
					@CacheEvict(cacheNames = "findAllByCategories", allEntries = true),
					@CacheEvict(cacheNames = "allGuests", allEntries = true),
					@CacheEvict(cacheNames = "guestByPassport", allEntries = true),
					@CacheEvict(cacheNames = "checkQuarters", allEntries = true)
			}
	)
	@NonNull
	@Override
	public Quarters changeCategory(@NonNull Long quartersNumber, @NonNull String categoryName) {
		log.debug("Entering method changeCategory()");
		log.debug("Trying to change the Category on: {}, for existing Quarter with number: {}", categoryName, quartersNumber);
		Category categoryDb = categoryRepo.findByCategoryName(CategoryEnum.valueOf(categoryName)).orElseThrow(() -> {
			log.error("Error in changeCategory() method. Cause: no category in table.");
			return new CustomException("No such category name exists right now in table >>> " + categoryName + ", " +
					"pls check categories and add a new one");
		});
		var quartersDb = quartersRepo.findByQuarterNumber((quartersNumber.intValue())).orElseThrow(() -> {
			log.error("Error in changeCategory() method. Cause: no quarters number exists.");
			return new CustomException("No such quarters number exists: " + quartersNumber + ", pls check the quarters number");
		});
		quartersDb.setCategory(categoryDb);
		log.info("The category of Quarters with number: {} has been changed successfully for {}", quartersNumber, categoryName);
		return quartersRepo.save(quartersDb);
	}

	@Caching(
			put = @CachePut(cacheNames = "findByNumber", key = "#cleaningDateDTO.quarterNumber"),
			evict = {
					@CacheEvict(cacheNames = "findAllByCategories", allEntries = true),
					@CacheEvict(cacheNames = "allGuests", allEntries = true),
					@CacheEvict(cacheNames = "guestByPassport", allEntries = true),
					@CacheEvict(cacheNames = "checkQuarters", allEntries = true)
			}
	)
	@NonNull
	@Override
	public Quarters changeCleaningDate(@NonNull CleaningDateDTO cleaningDateDTO) {
		log.debug("Entering method changeCleaningDate()");
		log.debug("Trying to change the cleaningDate on {} for quarters with number: {}.",
				cleaningDateDTO.getCleaningDate(), cleaningDateDTO.getQuarterNumber());
		Quarters quartersDb = quartersRepo.findByQuarterNumber(cleaningDateDTO.getQuarterNumber()).orElseThrow(() ->
		{
			log.error("Error in changeCleaningDate() method. Cause: no quarters exists with this number");
			return new CustomException("No such quarters number exists: " + cleaningDateDTO.getQuarterNumber() +
					", pls check the quarters number");
		});
		quartersDb.setCleaningDate(cleaningDateDTO.getCleaningDate());
		log.info("The cleaningDate of Quarters with number: {} has been changed successfully for {}",
				cleaningDateDTO.getQuarterNumber(), cleaningDateDTO.getCleaningDate());
		return quartersRepo.save(quartersDb);
	}

	@Cacheable(cacheNames = "getPremisesNumber", key = "#quartersNumber")
	@NonNull
	@Override
	public Integer getPremisesNumber(@NonNull Long quartersNumber) {
		log.debug("Entering method getPremisesNumber");
		log.debug("Trying to get the number of premises in Quarter with number: {}", quartersNumber);
		Quarters quartersDb = quartersRepo.findByQuarterNumber(quartersNumber.intValue()).orElseThrow(() -> {
			log.error("Error in getPremisesNumber() method. Cause: no quarters number exists.");
			return new CustomException("No such quarters number exists: " + quartersNumber + ", pls check the number");
		});
		log.info("The number of premises has been obtained successfully from Quarters with number: {}", quartersNumber);
		return quartersDb.getPremisesNumber();
	}

	@Caching(
			evict = {
					@CacheEvict(cacheNames = "findByNumber", key = "#quartersNumber"),
					@CacheEvict(cacheNames = "getPremisesNumber", key = "#quartersNumber"),
					@CacheEvict(cacheNames = "findAllByCategories", allEntries = true),
					@CacheEvict(cacheNames = "checkQuarters", allEntries = true)
			}
	)
	@Override
	public void deleteQuarters(@NonNull Long quartersNumber) {
		log.debug("Entering method deleteQuarters");
		log.debug("Trying to delete Quarters with number: {}", quartersNumber);
		Quarters quartersDb = quartersRepo.findByQuarterNumber(quartersNumber.intValue()).orElseThrow(() -> {
			log.error("Error in deleteQuarters() method. Cause: no quarters number exists.");
			return new CustomException("No such quarters number exists: " + quartersNumber + ", pls check the number");
		});
		quartersRepo.delete(quartersDb);
		log.info("The Quarters with number: {} has been deleted successfully", quartersNumber);

	}

	@Cacheable(cacheNames = "findByNumber", key = "#quarterNumber")
	@NonNull
	@Override
	public Quarters findByNumber(@NonNull Integer quarterNumber) {
		log.debug("Entering method findByNumber");
		log.debug("Trying to find Quarter by the following number: {}", quarterNumber);
		Quarters quarters = quartersRepo.findByQuarterNumber(quarterNumber).orElseThrow(() -> {
			log.error("Error in findNumber() method. Cause: no quarters' number exists.");
			return new CustomException("No quarter found with number: " + quarterNumber);
		});
		log.info("The Quarters with the following number: {} have been found successfully", quarterNumber);
		return quarters;
	}

	@Cacheable(cacheNames = "findAllByCategories", key = "#categories")
	@NonNull
	@Override
	public Set<Quarters> findAllByCategories(@NonNull Set<String> categories) {
		log.debug("Entering method findAllByCategories");
		final String categoriesForLog = categories.toString()
				.replace("[", "")
				.replace("]", "");
		log.debug("Trying to find all Quarters by the following categories: {}", categoriesForLog);
		Set<Quarters> quartersSet = quartersRepo
				.findAll()
				.stream()
				.filter(quarters -> categories
						.contains(quarters.getCategory().getCategoryName().toString()))
				.collect(Collectors.toSet());

		if (quartersSet.isEmpty()) {
			log.warn("Error in findAllByCategories() method. Cause: there no categories with inserted values.");
			throw new CustomException("No quarters found with chosen categories!");
		}
		log.info("The Set of Quarters with the following categories {} " +
				"has been obtained successfully", categoriesForLog);
		return quartersSet;
	}
}
