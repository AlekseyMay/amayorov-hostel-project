package com.amayorov.hostel.controller.security;


import com.amayorov.hostel.domain.dto.security.RoleDTO;
import com.amayorov.hostel.domain.entity.security.Role;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.service.security.RoleService;
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
@RequestMapping("/roles")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT token assignment")
public class RoleController {

	private final RoleService roleService;

	@Operation(
			summary = "Adding role.",
			description = "Creates and then adding a new role to the DB. \n\n" +
					"By default, at the first start of the application, two roles: `ADMIN` and `MANAGER` are added to system. \n\n" +
					"And all the settings are based on these roles, so this endpoint is just to show that it is possible to add roles, \n\n" +
					"but if you add additional roles and want them having some privileges, you have to change the code of the app. ",
			tags = "Roles"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "201",
							description = "new role created",
							content = @Content(schema = @Schema(name = "string", example = "Role with name *** created successfully"),
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
	public ResponseEntity<String> addNewRole(@Validated @RequestBody RoleDTO roleDTO) {
		try {
			Role role = roleService.createRole(roleDTO);
			return new ResponseEntity<>("Role with name \"" + role.getRoleName() + "\" created successfully", HttpStatus.CREATED);
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(
			summary = "Deleting role.",
			description = "Deleting role found by name from DB. \n\n" +
					"It is not possible to delete role if any User has it.",
			tags = "Roles"
	)
	@ApiResponses(
			value = {
					@ApiResponse(
							responseCode = "200",
							description = "successful operation",
							content = @Content(schema = @Schema(name = "string", example = "Role with name: *** " +
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
	@DeleteMapping("/{roleName}")
	public ResponseEntity<String> deleteRole(@Parameter(example = "ANYCREATEDROLE",
			description = "In upper or lower case") @PathVariable String roleName) {
		try {
			boolean isDeleted = roleService.deleteRole(roleName);
			if (isDeleted) {
				return new ResponseEntity<>("Role with name: " + roleName + " was successfully deleted", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Role with id: " + roleName + " can not be deleted", HttpStatus.BAD_REQUEST);
			}
		} catch (CustomException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
