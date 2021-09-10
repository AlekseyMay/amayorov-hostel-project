package com.amayorov.hostel.service;

import com.amayorov.hostel.domain.dto.CategoryDTO;
import com.amayorov.hostel.domain.entity.Category;
import org.springframework.lang.NonNull;

import java.util.Set;

public interface CategoryService {

	@NonNull
	Category createCategory(@NonNull CategoryDTO categoryDTO) ;

	@NonNull
	boolean deleteCategory(@NonNull String categoryName) ;

	@NonNull
	Set<Category> getAllCategory();

}
