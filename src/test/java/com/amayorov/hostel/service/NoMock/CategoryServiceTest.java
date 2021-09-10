package com.amayorov.hostel.service.NoMock;

import com.amayorov.hostel.AbstractHostelTest;
import com.amayorov.hostel.domain.dto.CategoryDTO;
import com.amayorov.hostel.domain.entity.Category;
import com.amayorov.hostel.domain.enums.CategoryEnum;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.CategoryRepo;
import com.amayorov.hostel.service.CategoryService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CategoryServiceTest extends AbstractHostelTest {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private CategoryRepo categoryRepo;

	@Before
	public void clearDb() {
		categoryRepo.deleteAll();
	}

	@Test
	public void createCategory_ExistByName() {
		categoryRepo.save(new Category(CategoryEnum.Deluxe, "Already exist"));
		CategoryDTO categoryDTO = new CategoryDTO(CategoryEnum.Deluxe, "Already exist");
		assertThatThrownBy(() -> categoryService.createCategory(categoryDTO))
				.isInstanceOf(CustomException.class)
				.hasMessage("Category with this name >>> \"" + CategoryEnum.Deluxe + "\" already exists!");
	}


	@Test
	public void createCategory_Success() {
		CategoryDTO categoryDTO = new CategoryDTO(CategoryEnum.Deluxe, "Test");

		Category result = categoryService.createCategory(categoryDTO);
		Category saved = categoryRepo.findById(result.getId()).orElseThrow(
				() -> new RuntimeException("Not found")
		);
		assertThat(result).isEqualTo(saved);
	}

	@Test
	public void deleteCategory_DoesntExistAtAll() {

		assertThatThrownBy(() ->
				categoryService.deleteCategory("Test_Non_Existing_Category"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("No enum constant com.amayorov.hostel.domain.enums.CategoryEnum.Test_Non_Existing_Category");

	}

	@Test
	public void deleteCategory_DoesntExistInDateBase_ButExistsInEnum() {
		assertThatThrownBy(() ->
				categoryService.deleteCategory("Deluxe"))
				.isInstanceOf(CustomException.class)
				.hasMessage("Category not found with name: Deluxe");
	}


	@Test
	public void deleteCategory_Success() {

		CategoryDTO categoryDTO = new CategoryDTO(CategoryEnum.Deluxe, "Test");
		Category category = categoryService.createCategory(categoryDTO);
		assertThat(categoryRepo.findById(category.getId())).isPresent();
		categoryService.deleteCategory(category.getCategoryName().toString());
		assertThat(categoryRepo.findById(category.getId())).isEmpty();
	}


	@Test
	public void getAllCategories_IsEmpty() {
		assertThatThrownBy(() ->
				categoryService.getAllCategory())
				.isInstanceOf(CustomException.class)
				.hasMessage("The List of categories is empty, please add categories.");
	}

	@Test
	public void getAllCategories_Success() {
		Category category = new Category(CategoryEnum.Deluxe, "Test");
		Set<Category> savedCategories = Set.of(categoryRepo.save(category));
		Set<Category> result = categoryService.getAllCategory();
		assertThat(result).isEqualTo(savedCategories);
	}
}
