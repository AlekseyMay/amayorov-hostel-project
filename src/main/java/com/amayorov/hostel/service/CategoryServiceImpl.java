package com.amayorov.hostel.service;

import com.amayorov.hostel.domain.dto.CategoryDTO;
import com.amayorov.hostel.domain.entity.Category;
import com.amayorov.hostel.domain.enums.CategoryEnum;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepo categoryRepo;

	@CacheEvict(cacheNames = "getAllCategory", allEntries = true)
	@NonNull
	@Override
	public Category createCategory(@NonNull CategoryDTO categoryDTO) {
		log.debug("Entering method createCategory.");
		log.debug("Trying to create category with the following data: {}.", categoryDTO);
		if (categoryRepo.findByCategoryName(categoryDTO.getCategoryName()).isPresent()) {
			log.error("Error in createCategory() method. Cause: categoryName already present in DB.");
			throw new CustomException("Category with this name >>> \"" + categoryDTO.getCategoryName() + "\" already exists!");
		}
		log.info("Category with the following data: {} has been created successfully.", categoryDTO);
		return categoryRepo.save(categoryDTO.toEntity());

	}

	@CacheEvict(cacheNames = "getAllCategory", allEntries = true)
	@NonNull
	@Override
	public boolean deleteCategory(@NonNull String categoryName) {
		log.debug("Entering method deleteCategory.");
		log.debug("Trying to delete category with name: {}", categoryName);
		var categoryDb = categoryRepo.findByCategoryName(Enum.valueOf(CategoryEnum.class, categoryName)).orElseThrow(() -> {
			    log.error("Error in method deleteCategory(). Cause: categoryName is not found in DB.");
				return new CustomException("Category not found with name: " + categoryName);
		});
		try {
			categoryRepo.delete(categoryDb);
			log.info("Category with name {} has been deleted.", categoryName);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	@Cacheable(cacheNames = "getAllCategory")
	@NonNull
	@Override
	public Set<Category> getAllCategory() {
		log.debug("Entering method getAllCategory");
		log.debug("Trying to get the List of all the categories that exist");
		if (categoryRepo.findAll().isEmpty()) {
			log.error("Error in method getAllCategory(). Cause: no categories in DB.");
			throw new CustomException("The List of categories is empty, please add categories.");
		}
		log.info("The List of categories has been found: {}", categoryRepo.findAll());
		return new HashSet<>(categoryRepo.findAll());
	}
}
