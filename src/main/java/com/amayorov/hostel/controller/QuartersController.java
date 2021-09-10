package com.amayorov.hostel.controller;


import com.amayorov.hostel.HostelBusinessLogic;
import com.amayorov.hostel.domain.dto.CleaningDateDTO;
import com.amayorov.hostel.domain.dto.QuartersDTO;
import com.amayorov.hostel.domain.dto.ValidateQuartersDTO;
import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.domain.entity.Quarters;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.service.QuartersService;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/quarters")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT token assignment")
public class QuartersController {

	private final QuartersService quartersService;
	private final HostelBusinessLogic hostelBusinessLogic;

	@Operation(
			summary = "Adding quarters. ADMIN only",
			description = "Creating new Quarters and adding it to DB.",
			tags = "Quarters"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "201",
							description = "created",
							content = @Content(schema = @Schema(implementation = Quarters.class),
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
	@PostMapping
	public ResponseEntity<?> addQuarters(@Validated @RequestBody QuartersDTO quartersDTO) {
		try {
			Quarters quarters = quartersService.createQuarters(quartersDTO);
			return new ResponseEntity<>(quarters, HttpStatus.CREATED);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (IllegalArgumentException e) {
			log.error("IllegalArgumentException: {}", e.getMessage());
			return new ResponseEntity<>("This category \"" + quartersDTO.getCategoryName() + "\" doesn`t exist! \n" +
					"Please check the spelling and choose from: Apartment, Business, Deluxe, Duplex, Superior, Standard",
					HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Delete quarters. ADMIN only",
			description = "Deleting quarters by number.",
			tags = "Quarters"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(schema = @Schema(name = "string", example = "Quarters with numbers: *** " +
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
	@DeleteMapping("/{quartersNumber}")
	public ResponseEntity<String> deleteQuarters(@Parameter(example = "999", description = "Insert quarters number")
	                                                 @PathVariable Long quartersNumber) {
		try {
			quartersService.deleteQuarters(quartersNumber);
			return new ResponseEntity<>("Quarters with numbers: " + quartersNumber + " was successfully deleted",
			HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (DataIntegrityViolationException e) {
			return new ResponseEntity<>("You can't delete quarter if any guest has it!", HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Change category. ADMIN only",
			description = "Changing quarters' category by inserting category name and quarters' number.",
			tags = "Quarters"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(schema = @Schema(name = "string", example = "Quarters with number *** " +
									"now have new category with Name *** and id of this category is >>> ***"),
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
	@PutMapping
	public ResponseEntity<String> changeCategory(@Parameter(example = "999", description = "Insert quarters` number") @RequestParam Long quartersNumber,
	                                             @Parameter(example = "Apartment", description = "Insert name of new Category (must be from enum List) \n\n" +
			                                             "e.g. \"Apartment\", \"Business\", \"Deluxe\", \"Duplex\", \"Superior\", \"Standard\"") @RequestParam String categoryName) {
		try {
			Quarters quarters = quartersService.changeCategory(quartersNumber, categoryName);
			return new ResponseEntity<>("Quarters with number " + quartersNumber + " now have new category with Name " +
					quarters.getCategory().getCategoryName() + " and id of this category is >>> " + quarters.getCategory().getId(),
					HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (IllegalArgumentException e) {
			log.error("IllegalArgumentException: {}", e.getMessage());
			return new ResponseEntity<>("This category \"" + categoryName + "\" doesn`t exist! \n" +
					"Please check the spelling and choose from: Apartment, Business, Deluxe, Duplex, Superior, Standard",
			HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Changing quarters` cleaning date.",
			description = "Updates quarters` cleaning date.",
			tags = "Quarters"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(schema = @Schema(name = "string", example = "Quarters with number *** " +
									"now have new cleaningDate: ***"), mediaType = MediaType.ALL_VALUE)
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
	@PutMapping("/cleaning-date")
	public ResponseEntity<String> changeCleaningDate(@Validated @RequestBody CleaningDateDTO cleaningDateDTO) {
		try {
			Quarters quarters = quartersService.changeCleaningDate(cleaningDateDTO);
			return new ResponseEntity<>("Quarters with number " + quarters.getQuarterNumber() +
					" now have new cleaningDate: " + quarters.getCleaningDate(), HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Getting number of premises.",
			description = "Getting quantity of premises by quarters` number.",
			tags = "Quarters"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(schema = @Schema(name = "string", example = "Quarters with number >>> *** " +
									"have *** premises"),mediaType = MediaType.ALL_VALUE)
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
	@GetMapping("/premises")
	public ResponseEntity<String> getPremisesNumber(@Parameter(example = "999", description = "Insert quarters` number") @RequestParam Long quartersNumber) {
		try {
			return new ResponseEntity<>("Quarters with number >>> " + quartersNumber + " have " +
					quartersService.getPremisesNumber(quartersNumber) + " premises", HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Getting guests of quarters.",
			description = "Getting Set of guests from quarters found by number.",
			tags = "Quarters"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(array = @ArraySchema(
									schema = @Schema(implementation = Guest.class)),
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
	@GetMapping("/guests/{quartersNumber}")
	public ResponseEntity<?> getGuestsOfQuarter(@Parameter(example = "999", description = "Insert quarters` number") @PathVariable Long quartersNumber) {
		try {
			Set<Guest> guestSet = hostelBusinessLogic.findGuestsOfQuarter(quartersNumber.intValue());
			return new ResponseEntity<>(guestSet, HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Getting all quarters by categories.",
			description = "Getting Set of quarters filtered by inserted categories.",
			tags = "Quarters"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(array = @ArraySchema(
									schema = @Schema(implementation = Quarters.class)),
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
	@GetMapping("/{categories}")
	public ResponseEntity<?> getAllByCategories(@Parameter(example = "Apartment, Business, etc",
			description = "Insert categories divided with comma or comma+space") @PathVariable Set<String> categories) {
		try {
			Set<Quarters> quartersSet = quartersService.findAllByCategories(categories);
			return new ResponseEntity<>(quartersSet, HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}


	@Operation(
			summary = "Checking quarters` availability.",
			description = "Checking if quarters is/are free on the demanded dates, taking into account wanted categories " +
					"and amount of quarters. On success shows the Set of free quarters.",
			tags = "Quarters"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(array = @ArraySchema(
									schema = @Schema(implementation = Quarters.class)),
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
	@PostMapping(value = "/validation")
	public ResponseEntity<?> checkIfQuartersFree(@Validated @RequestBody ValidateQuartersDTO validateQuartersDTO) {
		try {
			Set<Quarters> freeQuarters = hostelBusinessLogic.checkQuarters(validateQuartersDTO);
			if (freeQuarters.isEmpty()) {
				return new ResponseEntity<>("No free Quarters available with inserted values.", HttpStatus.BAD_REQUEST);
			} else {
				return new ResponseEntity<>(freeQuarters, HttpStatus.OK);
			}
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
