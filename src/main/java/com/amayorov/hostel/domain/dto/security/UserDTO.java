package com.amayorov.hostel.domain.dto.security;

import com.amayorov.hostel.domain.entity.security.Role;
import com.amayorov.hostel.domain.entity.security.User;
import com.amayorov.hostel.domain.transfer.CreateUser;
import com.amayorov.hostel.domain.transfer.RegistrateUser;
import com.amayorov.hostel.domain.transfer.UserRoleChange;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;


@Value
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO implements Serializable {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	Long id;

	@Schema(
			description = "Name of the user, need it in any request with this DTO.",
			example = "Username",
			required = true
	)
	@NotBlank(groups = {CreateUser.class, RegistrateUser.class, UserRoleChange.class})
	String username;

	@Schema(
			description = "Password of the user, need this field only for User creation or User registration.",
			example = "anyPassword | in case of '/new-roles', '/additional-roles' requests >>> pass null OR leave example(this) string with no change"
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(groups = {CreateUser.class, RegistrateUser.class})
	String password;

	@Schema(
			description = "Roles of the User, need this field on User creation, User adding Roles, User Overriding Roles requests.",
			example = "[\"Role1\",\"Role2\",\"etc\",\" | in case of '/registration' >>> pass null or leave the example(this) array values with no change\"]"
	)
	@NotEmpty(groups = {CreateUser.class, UserRoleChange.class})
	Set<String> roles;


	public User toEntity() {
		var userEntity = new User();
		userEntity.setUserName(this.getUsername().replaceAll("\\s", ""));
		userEntity.setPassword(this.getPassword().replaceAll("\\s", ""));
		return userEntity;
	}

	public static UserDTO fromEntity(User user) {
		return new UserDTO(
				user.getId(),
				user.getUserName().replaceAll("\\s", ""),
				null,
				user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()));
	}

}
