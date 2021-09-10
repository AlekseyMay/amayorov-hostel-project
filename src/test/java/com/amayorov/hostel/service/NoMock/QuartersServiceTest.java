package com.amayorov.hostel.service.NoMock;

import com.amayorov.hostel.AbstractHostelTest;
import com.amayorov.hostel.domain.dto.CleaningDateDTO;
import com.amayorov.hostel.domain.dto.QuartersDTO;
import com.amayorov.hostel.domain.entity.Category;
import com.amayorov.hostel.domain.entity.Quarters;
import com.amayorov.hostel.domain.enums.CategoryEnum;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.CategoryRepo;
import com.amayorov.hostel.repository.QuartersRepo;
import com.amayorov.hostel.service.QuartersService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QuartersServiceTest extends AbstractHostelTest {


	@Autowired
	private QuartersService quartersService;

	@Autowired
	private QuartersRepo quartersRepo;

	@Autowired
	private CategoryRepo categoryRepo;

	@Before
	public void clearDb() {
		quartersRepo.deleteAll();
		categoryRepo.deleteAll();
	}


	@Test
	public void createQuarters_ExistsByNumber() {
		quartersRepo.save(new Quarters(10, 10,
				Date.from(LocalDate.of(2021, 01, 01)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant())));
		QuartersDTO sameRoomNumberQuarters = new QuartersDTO(10, 10,
				Date.from(LocalDate.of(2021, 01, 01)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()), null);

		assertThatThrownBy(() -> quartersService.createQuarters(sameRoomNumberQuarters))
				.isInstanceOf(CustomException.class)
				.hasMessage("Quarters with this quarters number >>> " + sameRoomNumberQuarters.getQuarterNumber() + " already exists!");
	}


	@Test
	public void createQuarters_NoSuchCategoryInTable() {
		QuartersDTO noExistInTable = new QuartersDTO(10, 10,
				Date.from(LocalDate.of(2021, 01, 01)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()), Array.get(CategoryEnum.values(), 0).toString());

		assertThatThrownBy(() -> quartersService.createQuarters(noExistInTable))
				.isInstanceOf(CustomException.class)
				.hasMessage("No such category name exists right now in table >>> " + noExistInTable.getCategoryName() + ", " +
						"pls check categories and add a new one.");
	}

	@Test
	public void createQuarters_Success() {

		Category category = new Category(CategoryEnum.valueOf(Array.get(CategoryEnum.values(), 0).toString()),
				"TestTestTest");
		categoryRepo.save(category);
		Quarters check = new Quarters(10, 10,
				Date.from(LocalDate.of(2021, 01, 01)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()));
		check.setCategory(category);
		QuartersDTO quartersDTO = new QuartersDTO(10, 10,
				Date.from(LocalDate.of(2021, 01, 01)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()), Array.get(CategoryEnum.values(), 0).toString());
		Quarters result = quartersService.createQuarters(quartersDTO);
		check.setId(result.getId());
		assertThat(result).isEqualTo(check);
	}

	@Test
	public void changeCategory_NoSuchCategoryExists() {
		CategoryEnum categoryExistInTable = CategoryEnum.valueOf(Array.get(CategoryEnum.values(), 0).toString());
		CategoryEnum categoryNoExistInTable = CategoryEnum.valueOf(Array.get(CategoryEnum.values(), 1).toString());
		Category category = new Category(categoryExistInTable, "TestTestTest");
		categoryRepo.save(category);

		assertThatThrownBy(() -> quartersService.changeCategory(0L, categoryNoExistInTable.name()))
				.isInstanceOf(CustomException.class)
				.hasMessage("No such category name exists right now in table >>> " +
						"" + categoryNoExistInTable.name() + ", pls check categories and add a new one");
	}

	@Test
	public void changeCategory_NoSuchQuartersExists() {
		CategoryEnum categoryBefore = CategoryEnum.valueOf(Array.get(CategoryEnum.values(), 0).toString());
		CategoryEnum categoryAfter = CategoryEnum.valueOf(Array.get(CategoryEnum.values(), 1).toString());
		Category categoryBef = new Category(categoryBefore, "TestTestTest");
        Category categoryAft = new Category(categoryAfter, "TestTestTest");
		categoryRepo.save(categoryBef);
		categoryRepo.save(categoryAft);

		assertThatThrownBy(() -> quartersService.changeCategory(0L, categoryAfter.name()))
				.isInstanceOf(CustomException.class)
				.hasMessage("No such quarters number exists: " + 0L + ", pls check the quarters number");
	}

	@Test
	public void changeCategory_Success() {
		CategoryEnum categoryBefore = CategoryEnum.valueOf(Array.get(CategoryEnum.values(), 0).toString());
		Category categoryBeforeChange = new Category(categoryBefore, "TestBeforeChange");
		categoryRepo.save(categoryBeforeChange);

		CategoryEnum categoryAfter = CategoryEnum.valueOf(Array.get(CategoryEnum.values(), 1).toString());
		Category categoryAfterChange = new Category(categoryAfter, "TestAfterChange");
		categoryRepo.save(categoryAfterChange);

		Quarters dummy = new Quarters(10, 10,
				Date.from(LocalDate.of(2021, 01, 01)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()));
		dummy.setCategory(categoryBeforeChange);

		Quarters check = quartersRepo.save(dummy);

		Quarters result = quartersService.changeCategory(Long.valueOf(check.getQuarterNumber()),
				categoryAfterChange.getCategoryName().name());

		assertThat(result.getId()).isEqualTo(check.getId());
		assertThat(result.getCategory()).isNotEqualTo(check.getCategory());
		assertThat(result.getCategory()).isEqualTo(categoryAfterChange);
	}


	@Test
	public void changeCleaningDate_NoSuchQuartersExists() {
		Quarters dummy = new Quarters(10, 10,
				Date.from(LocalDate.of(1111, 11, 11)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()));
		quartersRepo.save(dummy);
		CleaningDateDTO changeCleaningDateDTO = new CleaningDateDTO(
				01,
				Date.from(LocalDate.of(2222, 10, 10)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()));
		assertThatThrownBy(() -> quartersService.changeCleaningDate(changeCleaningDateDTO))
				.isInstanceOf(CustomException.class)
				.hasMessage("No such quarters number exists: " + changeCleaningDateDTO.getQuarterNumber() +
						", pls check the quarters number");
	}

	@Test
	public void changeCleaningDate_Success() {
		Quarters dummy = new Quarters(10, 10,
				Date.from(LocalDate.of(1111, 11, 11)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()));
		Quarters check = quartersRepo.save(dummy);
		CleaningDateDTO changeCleaningDateDTO = new CleaningDateDTO(
				10,
				Date.from(LocalDate.of(2222, 10, 10)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()));
		Quarters result = quartersService.changeCleaningDate(changeCleaningDateDTO);
		assertThat(result.getCleaningDate()).isNotEqualTo(check.getCleaningDate());
	}

	@Test
	public void getPremisesNumber_NoSuchQuarterNumberExists() {

		int quarterNumberExists = 1;
		int quarterNumberNoExists = 2;
		Quarters quarters = new Quarters(quarterNumberExists, null, null);
		quartersRepo.save(quarters);

		assertThatThrownBy(() -> quartersService.getPremisesNumber((long) quarterNumberNoExists))
				.isInstanceOf(CustomException.class)
				.hasMessage("No such quarters number exists: " + quarterNumberNoExists + ", pls check the number");
	}

	@Test
	public void getPremisesNumber_Success() {

		int quarterNumberExists = 1;
		int premisesNumberForCheck = 2;
		Quarters quarters = new Quarters(quarterNumberExists, premisesNumberForCheck, null);
		quartersRepo.save(quarters);

		int premisesNumberResult = quartersService.getPremisesNumber((long) quarterNumberExists);

		assertThat(premisesNumberResult).isEqualTo(premisesNumberForCheck);
	}

	@Test
	public void deleteQuarters_NoSuchQuartersExists() {
		Long randomNumber = 99L;
		assertThatThrownBy(() -> quartersService.deleteQuarters(randomNumber))
				.isInstanceOf(CustomException.class)
				.hasMessage("No such quarters number exists: " + randomNumber + ", pls check the number");
	}

	@Transactional // otherwise LazyInitializationException https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex
	@Test
	public void deleteQuarters_Success() {
		Integer randomNUmber = 99;
		Quarters quarters = new Quarters();
		quarters.setQuarterNumber(randomNUmber);
		Quarters quartersDb = quartersRepo.save(quarters);
		Optional<Quarters> isSaved = quartersRepo.findById(quartersDb.getId());
        assertThat(isSaved).isPresent().contains(quartersDb);

		quartersService.deleteQuarters(Long.valueOf(quartersDb.getQuarterNumber()));

		Optional<Quarters> result = quartersRepo.findById(quartersDb.getId());

		assertThat(result).isEmpty();
	}


	@Test
	public void findByNumber_NoQuarterFoundWIthNumber() {
		int quarterNumberExists = 1;
		int quarterNumberNoExists = 2;
		Quarters quarters = new Quarters(quarterNumberExists, null, null);
		quartersRepo.save(quarters);

		assertThatThrownBy(() -> quartersService.findByNumber(quarterNumberNoExists))
				.isInstanceOf(CustomException.class)
				.hasMessage("No quarter found with number: " + quarterNumberNoExists);
	}

	@Test
	@Transactional
	//otherwise LazyInitializationException https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex
	public void findByNumber_Success() {
		int quarterNumberExists = 1;
		Quarters quarters = new Quarters(quarterNumberExists, null, null);
		quartersRepo.save(quarters);

		Quarters result = quartersService.findByNumber(quarterNumberExists);
		assertThat(result).isEqualTo(quarters);
	}


	@Test
	public void findAllByCategories_IsEmpty() {
		assertThatThrownBy(() -> quartersService.findAllByCategories(new HashSet<>()))
				.isInstanceOf(CustomException.class)
				.hasMessage("No quarters found with chosen categories!");
	}

	@Test
	@Transactional
	//otherwise LazyInitializationException https://stackoverflow.com/questions/11746499/how-to-solve-the-failed-to-lazily-initialize-a-collection-of-role-hibernate-ex
	public void findAllCategories_Success() {
		String categoryString = Array.get(CategoryEnum.values(), 0).toString();
		Category category = new Category(CategoryEnum.valueOf(categoryString), "Test");
		categoryRepo.save(category);

		Quarters quarters = new Quarters();
		quarters.setCategory(category);
		Quarters check = quartersRepo.save(quarters);
		Set<Quarters> checkSet = Set.of(check);

		Set<Quarters> result = quartersService.findAllByCategories(Set.of(categoryString));

		assertThat(result).isEqualTo(checkSet);
	}

}
