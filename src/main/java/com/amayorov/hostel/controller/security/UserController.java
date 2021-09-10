package com.amayorov.hostel.controller.security;

import com.amayorov.hostel.HostelBusinessLogic;
import com.amayorov.hostel.domain.dto.security.UserDTO;
import com.amayorov.hostel.domain.transfer.CreateUser;
import com.amayorov.hostel.domain.transfer.RegistrateUser;
import com.amayorov.hostel.domain.transfer.UserRoleChange;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.service.security.UserService;
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

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
@SecurityRequirement(name = "JWT token assignment")
public class UserController {

	private final HostelBusinessLogic hostelBusinessLogic;
	private final UserService userService;


	@Operation(
			summary = "Adding user.",
			description = "Creates and then adding a new user to the DB. \n\n" +
					"By default, at the first start of the application, it creates the default admin user with: \n\n" +
					"username: admin \n\n" +
					"password: admin \n\n" +
					"roles: ADMIN, MANAGER \n\n" +
					"IMPORTANT: in this request you need to fill all the fields: \"username\", \"password\", \"roles\"",
			tags = "Users"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "201",
							description = "created",
							content = @Content(schema = @Schema(implementation = UserDTO.class),
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
	public ResponseEntity<?> addNewUser(@Validated(CreateUser.class) @RequestBody UserDTO userDTO) {
		try {
			UserDTO userNoPass = hostelBusinessLogic.createUser(userDTO);
			return new ResponseEntity<>(userNoPass, HttpStatus.CREATED);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Adding new roles.",
			description = "Adding new roles to existing without overriding. \n\n" +
					"Fill \"username\", \"roles\" fields only!",
			tags = "Users"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "operation successful",
							content = @Content(schema = @Schema(implementation = UserDTO.class),
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
	@PutMapping("/additional-roles") //adding roles to existing roles
	public ResponseEntity<?> addRoles(@Validated(UserRoleChange.class) @RequestBody UserDTO userDTO) {
		try {
			UserDTO userNoPass = hostelBusinessLogic.addRoles(userDTO);
			return new ResponseEntity<>(userNoPass, HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Overriding roles.",
			description = "Overriding the existing user`s role List. \n\n" +
					"Fill \"username\", \"roles\" fields only!",
			tags = "Users"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "operation successful",
							content = @Content(schema = @Schema(implementation = UserDTO.class),
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
	@PutMapping("/new-roles")
	public ResponseEntity<?> overrideRoles(@Validated(UserRoleChange.class) @RequestBody UserDTO userDTO) {
		try {
			UserDTO userNoPass = hostelBusinessLogic.overrideRoles(userDTO);
			return new ResponseEntity<>(userNoPass, HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Delete user.",
			description = "Deleting User by Name.",
			tags = "Users"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "operation successful",
							content = @Content(schema = @Schema(name = "string",
									example = "User with name: *** was successfully deleted"),
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
	@DeleteMapping("/{userName}")
	public ResponseEntity<String> deleteUser(@Parameter(example = "ANYCREATEDUSER",
			description = "Case sensitive, be attentive") @PathVariable String userName, Principal principal) {
		try {
			userService.deleteUser(userName, principal);
			return new ResponseEntity<>("User with name: " + userName + " was successfully deleted", HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}


	@Operation(
			summary = "User registration.",
			description = "User registration with basic `MANAGER` role. \n\n" +
					"Fill \"username\", \"password\" fields only!",
			tags = "Registration || NO AUTHENTICATION NEEDED"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "operation successful",
							content = @Content(schema = @Schema(name = "string", example = "User with name: *** has been registered. \n" +
									"Now this user has role: MANAGER."), mediaType = MediaType.ALL_VALUE)
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
	@PostMapping("/registration")
	public ResponseEntity<?> register(@Validated(RegistrateUser.class) @RequestBody UserDTO userDTO) {
		try {
			UserDTO outputUser = hostelBusinessLogic.registrateUser(userDTO);
			return new ResponseEntity<>("User with name: " + outputUser.getUsername() + " has been registered.  \n" +
					"Now this user has role: MANAGER.", HttpStatus.CREATED);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}


