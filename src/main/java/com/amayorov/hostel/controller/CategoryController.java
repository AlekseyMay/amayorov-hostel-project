package com.amayorov.hostel.controller;

import com.amayorov.hostel.domain.dto.CategoryDTO;
import com.amayorov.hostel.domain.entity.Category;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@Slf4j
@RequestMapping("/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT token assignment")
public class CategoryController {

	private final CategoryService categoryService;


	@Operation(
			summary = "Getting all categories.",
			description = "Getting Set of categories fetched from DB.",
			tags = "Categories"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(array = @ArraySchema(
									schema = @Schema(implementation = Category.class)),
									mediaType = MediaType.ALL_VALUE)
							),
					@ApiResponse(
							responseCode = "400",
							description = "bad request",
							content = @Content(schema = @Schema(name = "string", example = "Error message info."),
									mediaType = MediaType.TEXT_PLAIN_VALUE)
					),
					@ApiResponse(
							responseCode = "401",
							description = "unauthorized",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
					),
					@ApiResponse(
							responseCode = "403",
							description = "access denied",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
					)
			}
	)
	@GetMapping
	public ResponseEntity<?> getAllCategory() {
		try {
			Set<Category> categorySet = categoryService.getAllCategory();
			return new ResponseEntity<>(categorySet, HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}


	@Operation(
			summary = "Adding new category. ADMIN only",
			description = "Adding new Category. Category name have to be from enum or fail. \n\n" +
					"Enum List: \"Apartment\", \"Business\", \"Deluxe\", \"Duplex\", \"Superior\", \"Standard\". Case sensitive.",
			tags = "Categories"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "201",
							description = "created",
							content = @Content(schema = @Schema(name = "string", example = "Category with name *** " +
									"created successfully"), mediaType = MediaType.ALL_VALUE)
					),
					@ApiResponse(
							responseCode = "400",
							description = "bad request",
							content = @Content(schema = @Schema(name = "string", example = "Error message info."),
									mediaType = MediaType.TEXT_PLAIN_VALUE)
					),
					@ApiResponse(
							responseCode = "401",
							description = "unauthorized",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
					),
					@ApiResponse(
							responseCode = "403",
							description = "access denied",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
					)}
	)
	@PostMapping
	public ResponseEntity<String> addNewCategory(@Validated @RequestBody CategoryDTO categoryDTO) {
		try {
			Category category = categoryService.createCategory(categoryDTO);
			return new ResponseEntity<>("Category with name \"" + category.getCategoryName() +
					"\" created successfully", HttpStatus.CREATED);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Delete category. ADMIN only",
			description = "Deleting category from DB by name. It is not possible to delete category if any quarters has it.",
			tags = "Categories"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(schema = @Schema(name = "string", example = "Category with name: *** " +
									"was successfully deleted"), mediaType = MediaType.ALL_VALUE)
					),
					@ApiResponse(
							responseCode = "400",
							description = "bad request",
							content = @Content(schema = @Schema(name = "string", example = "Error message info."),
									mediaType = MediaType.TEXT_PLAIN_VALUE)
					),
					@ApiResponse(
							responseCode = "401",
							description = "unauthorized",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
					),
					@ApiResponse(
							responseCode = "403",
							description = "access denied",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
					)
			}
	)
	@DeleteMapping("/{categoryName}")
	public ResponseEntity<String> deleteCategory(@Parameter(example = "ANYCREATEDCATEGORY", description = "Case sensitive. \n\n" +
			"Choose from options: \"Apartment\", \"Business\", \"Deluxe\", \"Duplex\", \"Superior\", \"Standard\"") @PathVariable String categoryName) {
		try {
			boolean isDeleted = categoryService.deleteCategory(categoryName);
			if (isDeleted) {
				return new ResponseEntity<>("Category with name: " + categoryName + " was successfully deleted", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Category with name: " + categoryName + " can not be deleted", HttpStatus.BAD_REQUEST);
			}
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (IllegalArgumentException e) {
			log.error("IllegalArgumentException in deleteCategory() method: {}", e.getMessage());
			return new ResponseEntity<>("This category \"" + categoryName + "\" doesn`t exist! \n" +
					"Please check the spelling and choose from: Apartment, Business, Deluxe, Duplex, Superior, Standard",
					HttpStatus.BAD_REQUEST);
		}
	}
}
