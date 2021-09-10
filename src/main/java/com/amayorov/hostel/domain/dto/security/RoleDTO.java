package com.amayorov.hostel.domain.dto.security;

import com.amayorov.hostel.domain.entity.security.Role;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES))) // if you create model with 1 parameter and with @Value Lombok annotation it throws an exception
                                                                                           // Cannot construct instance of "..." , ...MismatchedInputException cannot deserialize from Object value
                                                                                           // see: https://stackoverflow.com/questions/48330613/objectmapper-cant-deserialize-without-default-constructor-after-upgrade-to-spri
                                                                                           // solution taken from stackoverflow.com
public class RoleDTO {


	@Schema(
			description = "The name of the role",
			example = "ADMIN or MANAGER or any if added to the code // in lower or upper case",
			required = true
	)
	@NotBlank()
	@JsonProperty("role-name")
	String roleName;

	public Role toEntity() {
		var roleEntity = new Role();
		roleEntity.setRoleName(this.getRoleName().replaceAll("\\s","").toUpperCase());
		return roleEntity;
	}


}
