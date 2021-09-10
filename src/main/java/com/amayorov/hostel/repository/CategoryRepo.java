package com.amayorov.hostel.repository;

import com.amayorov.hostel.domain.entity.Category;
import com.amayorov.hostel.domain.enums.CategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository <Category, Long> {

	Optional<Category> findByCategoryName(CategoryEnum categoryName);
}
