package com.amayorov.hostel.controller;

import com.amayorov.hostel.HostelBusinessLogic;
import com.amayorov.hostel.domain.dto.PresenceDTO;
import com.amayorov.hostel.domain.entity.Guest;
import com.amayorov.hostel.domain.transfer.CreatePresence;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.service.PresenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RestController
@Slf4j
@RequestMapping("/presences")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT token assignment")
public class PresenceController {

	private final HostelBusinessLogic hostelBusinessLogic;
	private final PresenceService presenceService;

	@Operation(
			summary = "Creating new presence and adding it to existing guest.",
			description = "Creating and adding new presence to existing guest in case when one guest needs more than one quarters. \n\n " +
					"You need to fill all the fields!",
			tags = "Presences"
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
	@PostMapping//adding new presence to existing guest
	public ResponseEntity<?> addPresence(@Validated(CreatePresence.class) @RequestBody PresenceDTO presenceDTO) {
		try {
			Guest guest = hostelBusinessLogic.addPresence(presenceDTO);
			return new ResponseEntity<>(guest, HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Delete presence.",
			description = "Deleting presence by presence ID.",
			tags = "Presences"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(schema = @Schema(name = "string", example = "Presence with id: *** was successfully deleted"),
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
	@DeleteMapping("/{presenceId}")
	public ResponseEntity<String> deletePresence(@Parameter(example = "111",
			description = "Presence Id, can obtain it while getting needed guest by passport") @PathVariable Long presenceId) {
		try {
			presenceService.deletePresence(presenceId);
			return new ResponseEntity<>("Presence with id: " + presenceId + " was successfully deleted", HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
