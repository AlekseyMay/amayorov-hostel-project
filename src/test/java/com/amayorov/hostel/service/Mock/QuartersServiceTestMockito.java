package com.amayorov.hostel.service.Mock;


import com.amayorov.hostel.AbstractHostelTestMockito;
import com.amayorov.hostel.domain.dto.CleaningDateDTO;
import com.amayorov.hostel.domain.dto.QuartersDTO;
import com.amayorov.hostel.domain.entity.Category;
import com.amayorov.hostel.domain.entity.Quarters;
import com.amayorov.hostel.domain.enums.CategoryEnum;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.CategoryRepo;
import com.amayorov.hostel.repository.QuartersRepo;
import com.amayorov.hostel.service.QuartersServiceImpl;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class QuartersServiceTestMockito extends AbstractHostelTestMockito {

	@InjectMocks
	private QuartersServiceImpl quartersService;

	@Spy //just for learning purposes @Spy was used in this Test class
	private QuartersRepo quartersRepo;

	@Spy
	private CategoryRepo categoryRepo;


	@Test
	public void createQuarters_ExistsByNumber() {
		QuartersDTO quartersDTO = new QuartersDTO(99,99,new Date(), null);
		doReturn(Optional.of(new Quarters())).when(quartersRepo).findByQuarterNumber(anyInt());

		assertThatThrownBy(() -> quartersService.createQuarters(quartersDTO))
				.isInstanceOf(CustomException.class)
				.hasMessage("Quarters with this quarters number >>> " + quartersDTO.getQuarterNumber() + " already exists!");
	}

	@Test
	public void createQuarters_NoCategoryExistInTable() {
		QuartersDTO quartersDTO = new QuartersDTO(0, 0,
				new Date(), Array.get(CategoryEnum.values(), 0).toString());

		doReturn(Optional.empty()).when(categoryRepo)
				.findByCategoryName(CategoryEnum.valueOf(quartersDTO.getCategoryName()));

		assertThatThrownBy(() -> quartersService.createQuarters(quartersDTO))
				.isInstanceOf(CustomException.class)
				.hasMessage("No such category name exists right now in table >>> " + quartersDTO.getCategoryName() + ", " +
						"pls check categories and add a new one.");

	}

	@Test
	public void createQuarters_Success() {
		QuartersDTO noCategoryQuarters = new QuartersDTO(10, 10,
				Date.from(LocalDate.of(2021, 01, 01)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()), Array.get(CategoryEnum.values(), 0).toString());

		doReturn(Optional.empty()).when(quartersRepo).findByQuarterNumber(anyInt());
		doReturn(Optional.of(new Category())).when(categoryRepo)
				.findByCategoryName(CategoryEnum.valueOf(noCategoryQuarters.getCategoryName()));

		Quarters check = noCategoryQuarters.toEntity();
		check.setCategory(new Category());

		doReturn(check).when(quartersRepo).save(any(Quarters.class));
		Quarters quarters = quartersService.createQuarters(noCategoryQuarters);
		assertThat(quarters).isEqualTo(check);
	}

	@Test
	public void changeQuartersCategory_NoSuchCategoryExist() {
		assertThatThrownBy(() -> quartersService
				.changeCategory(anyLong(), Array.get(CategoryEnum.values(), 0).toString()))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No such category name exists right now in table >>> ");
	}

	@Test
	public void changeQuartersCategory_NoSuchQuartersNumber() {
		String testCategoryString = Array.get(CategoryEnum.values(), 0).toString();

		doReturn(Optional.empty()).when(quartersRepo).findByQuarterNumber(anyInt());
		doReturn(Optional.of(new Category())).when(categoryRepo)
				.findByCategoryName(any(CategoryEnum.class));

		assertThatThrownBy(() -> quartersService.changeCategory(anyLong(), testCategoryString))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No such quarters number exists: ");
	}

	@Test
	public void changeQuartersCategory_Success() {
		Long testRoomNumber = 0L;
		String testCategoryString = Array.get(CategoryEnum.values(), 0).toString();
		Quarters quarters = spy(Quarters.class);
		quarters.setQuarterNumber(testRoomNumber.intValue());
		Category category = spy(Category.class);
		category.setCategoryName(CategoryEnum.valueOf(testCategoryString));
		quarters.setCategory(category);

		doReturn(Optional.of(new Quarters())).when(quartersRepo).findByQuarterNumber(anyInt());
		doReturn(Optional.of(new Category())).when(categoryRepo)
				.findByCategoryName(any(CategoryEnum.class));
		doReturn(quarters).when(quartersRepo).save(any(Quarters.class));

		Quarters result = quartersService.changeCategory(testRoomNumber, testCategoryString);

		assertThat(result).isEqualTo(quarters);
	}

	@Test
	public void changeQuartersCleaningDate_NoQuartersExist() {
		CleaningDateDTO changeCleaningDateDTO = mock(CleaningDateDTO.class);
		when(changeCleaningDateDTO.getCleaningDate()).thenReturn(new Date());
		when(changeCleaningDateDTO.getQuarterNumber()).thenReturn(999);

		doReturn(Optional.empty()).when(quartersRepo).findByQuarterNumber(anyInt());

		assertThatThrownBy(() -> quartersService.changeCleaningDate(changeCleaningDateDTO))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No such quarters number exists: ");
	}

	@Test
	public void changeQuartersCleaningDate_Success() {

		CleaningDateDTO changeCleaningDateDTO = mock(CleaningDateDTO.class);
		when(changeCleaningDateDTO.getCleaningDate()).thenReturn(new Date());
		when(changeCleaningDateDTO.getQuarterNumber()).thenReturn(999);

		Quarters quartersFromDB = spy(Quarters.class);
		Quarters copyOfQuartersFromDbBeforeChange = spy(Quarters.class);
		copyOfQuartersFromDbBeforeChange.setCleaningDate(quartersFromDB.getCleaningDate());

		doReturn(Optional.of(quartersFromDB)).when(quartersRepo).findByQuarterNumber(anyInt());
		doReturn(quartersFromDB).when(quartersRepo).save(any(Quarters.class));

		Quarters quartersFromDbAfterChange = quartersService.changeCleaningDate(changeCleaningDateDTO);

		verify(quartersFromDB, times(1)).setCleaningDate(any(Date.class));
		assertThat(quartersFromDbAfterChange.getCleaningDate()).isNotEqualTo(copyOfQuartersFromDbBeforeChange.getCleaningDate());
	}

	@Test
	public void getPremisesNumber_NoSuchQuarterNumber() {
		doReturn(Optional.empty()).when(quartersRepo).findByQuarterNumber(anyInt());

		assertThatThrownBy(() -> quartersService.getPremisesNumber(anyLong()))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No such quarters number exists: ");
	}

	@Test
	public void getPremisesNumber_Success() {
		Quarters quarters = spy(Quarters.class);

		quarters.setPremisesNumber(0);

		doReturn(Optional.of(quarters)).when(quartersRepo).findByQuarterNumber(anyInt());

		int result = quartersService.getPremisesNumber(anyLong());

		assertThat(result).isEqualTo(quarters.getPremisesNumber());

	}

	@Test
	public void deleteQuarters_NoNumberExists() {
		doReturn(Optional.empty()).when(quartersRepo).findByQuarterNumber(anyInt());

		assertThatThrownBy(() -> quartersService.deleteQuarters(anyLong()))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No such quarters number exists: ");
		verify(quartersRepo, times(0)).delete(any(Quarters.class));
	}

	@Test
	public void deleteQuarters_Success() {
		doReturn(Optional.of(mock(Quarters.class))).when(quartersRepo).findByQuarterNumber(anyInt());

		quartersService.deleteQuarters(anyLong());
		verify(quartersRepo, times(1)).delete(any(Quarters.class));
	}

	@Test
	public void findByNumber_NoNumberExists() {
		doReturn(Optional.empty()).when(quartersRepo).findByQuarterNumber(anyInt());

		assertThatThrownBy(() -> quartersService.findByNumber(anyInt()))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining("No quarter found with number: ");
	}

	@Test
	public void findByNumber_Success() {
		Quarters quarters = mock(Quarters.class);

		doReturn(Optional.of(quarters)).when(quartersRepo).findByQuarterNumber(anyInt());

		Quarters result = quartersService.findByNumber(anyInt());
		assertThat(result).isEqualTo(quarters);

	}

	@Test
	public void findAllByCategories_IsEmpty() {
		doReturn(new ArrayList<>()).when(quartersRepo).findAll();
		assertThatThrownBy(() -> quartersService.findAllByCategories(new HashSet<>()))
				.isInstanceOf(CustomException.class)
				.hasMessage("No quarters found with chosen categories!");
	}


	@Test
	public void findAllByCategories_Successful() {
		Set<String> categories = Set.of(Array.get(CategoryEnum.values(), 0).toString());
		Quarters quarters = spy(Quarters.class);
		Category category = spy(Category.class);
		category.setCategoryName(CategoryEnum.valueOf(Array.get(CategoryEnum.values(), 0).toString()));

		quarters.setCategory(category);
		Set<Quarters> quartersSet = Set.of(quarters);
		doReturn(List.of(quarters)).when(quartersRepo).findAll();
		Set<Quarters> set = quartersService.findAllByCategories(categories);

		assertThat(set).isEqualTo(quartersSet);
	}

}
