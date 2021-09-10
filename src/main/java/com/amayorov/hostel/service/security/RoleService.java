package com.amayorov.hostel.service.security;

import com.amayorov.hostel.domain.dto.security.RoleDTO;
import com.amayorov.hostel.domain.entity.security.Role;
import org.springframework.lang.NonNull;

import java.util.Set;

public interface RoleService {

	@NonNull
	Role createRole(@NonNull RoleDTO roleDTO);

	@NonNull
	boolean deleteRole(@NonNull String roleName);

	@NonNull
	Set<Role> findAllByRoleNames(@NonNull Set<String> roleNames);


}
