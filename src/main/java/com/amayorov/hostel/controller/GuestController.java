package com.amayorov.hostel.controller;

import com.amayorov.hostel.HostelBusinessLogic;
import com.amayorov.hostel.domain.dto.GuestDTO;
import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.domain.transfer.ChangeGuest;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.service.GuestService;
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

import javax.validation.groups.Default;


@RestController
@Slf4j
@RequestMapping("/guests")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT token assignment")
public class GuestController {

	private final GuestService guestService;
	private final HostelBusinessLogic hostelBusinessLogic;

	@Operation(
			summary = "Adding new guest.",
			description = "Creating and adding new guest to DB. \n\n " +
					"You need to fill all the fields! Except 'guest-passport' in 'presence'!",
			tags = "Guests"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "201",
							description = "created",
							content = @Content(schema = @Schema(implementation = Guest.class), mediaType = MediaType.ALL_VALUE)
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
	public ResponseEntity<?> addNewGuest(@Validated(Default.class) @RequestBody GuestDTO guestDTO) {
		try {
			Guest guest = hostelBusinessLogic.createGuest(guestDTO);
			return new ResponseEntity<>(guest, HttpStatus.CREATED);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Changing existing guest.",
			description = "Changing personal information of the existing guest, finding guest by passport. \n\n" +
					"Therefore 'passport' field is obligatory to be filled! \n\n" +
					"Fill all the fields except 'presence', even if you need to change only one field you have to " +
					"fill all other fields, just copy the same info that should`t be changed.",
			tags = "Guests"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(schema = @Schema(implementation = Guest.class), mediaType = MediaType.ALL_VALUE)
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
	public ResponseEntity<?> changeGuest(@Validated(ChangeGuest.class) @RequestBody GuestDTO guestDTO) {
		try {
			Guest guest = guestService.changeGuest(guestDTO);
			return new ResponseEntity<>(guest, HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Getting all guests.",
			description = "Getting Set of guests fetched from DB.",
			tags = "Guests"
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
	@GetMapping
	public ResponseEntity<?> getAllGuest() {
		try {
			return new ResponseEntity<>(guestService.getAllGuest(), HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Get guest.",
			description = "Getting guest by passport from DB.",
			tags = "Guests"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(schema = @Schema(implementation = Guest.class), mediaType = MediaType.ALL_VALUE)
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
	@GetMapping("/passport")
	public ResponseEntity<?> getGuestByPassport(@Parameter(example = "1111556677", description = "Guest passport number") @RequestParam String passport) {
		try {
			return new ResponseEntity<>(guestService.getGuestByPassport(passport), HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Delete guest.",
			description = "Deleting guest by passport from DB.",
			tags = "Guests"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(schema = @Schema(name = "string", example = "Guest with passport: *** was successfully deleted"),
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
	@DeleteMapping("/{guestPassport}")
	public ResponseEntity<String> deleteGuest(@Parameter(example = "1111556677", description = "Guest passport number") @PathVariable String guestPassport) {
		try {
			guestService.deleteGuest(guestPassport);
			return new ResponseEntity<>("Guest with passport: " + guestPassport + " was successfully deleted", HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
