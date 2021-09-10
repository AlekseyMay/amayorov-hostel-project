package com.amayorov.hostel.service.Mock;

import com.amayorov.hostel.AbstractHostelTestMockito;
import com.amayorov.hostel.domain.dto.CategoryDTO;
import com.amayorov.hostel.domain.entity.Category;
import com.amayorov.hostel.domain.enums.CategoryEnum;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.CategoryRepo;
import com.amayorov.hostel.service.CategoryServiceImpl;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Array;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class CategoryServiceTestMockito extends AbstractHostelTestMockito {

	@InjectMocks
	private CategoryServiceImpl categoryService;

	@Mock
	private CategoryRepo categoryRepo;

	@Test
	public void createCategory_AlreadyExistException() {

		CategoryDTO categoryDTO = new CategoryDTO(
				CategoryEnum.Deluxe,
				"TestTestTest"
		);

        when(categoryRepo.findByCategoryName(any(CategoryEnum.class))).thenReturn(Optional.of(new Category()));

		assertThatThrownBy(() -> categoryService.createCategory(categoryDTO))
				.isInstanceOf(CustomException.class)
				.hasMessage("Category with this name >>> \"" + CategoryEnum.Deluxe + "\" already exists!");
	}


    @Test
	public void createCategory_Success() {
	    CategoryDTO categoryDTO = new CategoryDTO(
			    CategoryEnum.Deluxe,
			    "TestTestTest"
	    );

	    when(categoryRepo.save(any(Category.class))).thenReturn(categoryDTO.toEntity());
	    assertThat(categoryService.createCategory(categoryDTO)).isEqualTo(categoryDTO.toEntity());
	    verify(categoryRepo, times(1)).save(any(Category.class));

    }

    @Test
	public void deleteCategory_DoesntExistAtAll() {
		String doesntExist = "TestTest";
		assertThatThrownBy(() -> categoryService.deleteCategory(doesntExist))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("No enum constant ");
		verify(categoryRepo, times(0)).delete(any(Category.class));
    }

    @Test
	public void deleteCategory_DoesntExistInDateBase_ButExistsInEnum() {
		String existsInEnumButNotInDB = Array.get(CategoryEnum.values(), 0).toString();
		assertThatThrownBy(() -> categoryService.deleteCategory(existsInEnumButNotInDB))
				.isInstanceOf(CustomException.class)
				.hasMessage("Category not found with name: " + existsInEnumButNotInDB);
	    verify(categoryRepo, times(0)).delete(any(Category.class));
    }

    @Test
	public void deleteCategory_Success() {
		String category = Array.get(CategoryEnum.values(), 0).toString();
	    when(categoryRepo.findByCategoryName(any(CategoryEnum.class))).thenReturn(Optional.of(new Category()));
        categoryService.deleteCategory(category);
        verify(categoryRepo, times(1)).delete(any(Category.class));
    }

    @Test
	public void getAllCategories_Empty() {
		when(categoryRepo.findAll()).thenReturn(new ArrayList<>());
		assertThatThrownBy(() -> categoryService.getAllCategory())
				.isInstanceOf(CustomException.class)
				.hasMessage("The List of categories is empty, please add categories.");
    }

    @Test
	public void getAllCategories_Success() {
    when(categoryRepo.findAll()).thenReturn(new ArrayList<>(List.of(new Category())));
    Set<Category> result = categoryService.getAllCategory();
    assertThat(result).isEqualTo(new HashSet<>(new ArrayList<>(List.of(new Category()))));
    }
}