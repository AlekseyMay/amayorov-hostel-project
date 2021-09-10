package com.amayorov.hostel.service.security;

import com.amayorov.hostel.domain.dto.security.RoleDTO;
import com.amayorov.hostel.domain.entity.security.Role;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.security.RoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

	private final RoleRepo roleRepo;

	@CacheEvict(cacheNames = "findAllByRoleNames", allEntries = true)
	@NonNull
	@Override
	public Role createRole(@NonNull RoleDTO roleDTO) {
		log.debug("Entering method createRole");
		log.debug("Rolename is not null >>> trying to create role with name: {}", roleDTO.getRoleName().replaceAll("\\s","").toUpperCase());
		if (roleRepo.findByRoleName(roleDTO.getRoleName().replaceAll("\\s","").toUpperCase()).isPresent()) {
			log.error("Error in createRole() method. Cause: rolename already exists.");
			throw new CustomException("Role with this name >>> \"" + roleDTO.getRoleName().replaceAll("\\s","").toUpperCase() + "\" already exists!");
		}
		log.info("Role with name {} created successfully", roleDTO.getRoleName().replaceAll("\\s",""));
		return roleRepo.save(roleDTO.toEntity());
	}

	@CacheEvict(cacheNames = "findAllByRoleNames", allEntries = true)
	@NonNull
	@Override
	public boolean deleteRole(@NonNull String roleName) {
		log.debug("Entering method deleteRole");
		log.debug("Trying to delete role with name: {}", roleName.toUpperCase());
		Role roleDb = roleRepo.findByRoleName(roleName.toUpperCase()).orElseThrow(() -> {
			log.error("Error in deleteRole() method. Cause: no such role exists in role table.");
			return new CustomException("Check the spelling, there is no such role >>> " + roleName + " in system");
		});
		if (!roleDb.getUsers().isEmpty()) {
			log.error("Error in deleteRole() method. Cause: impossible to delete role if any user has it");
			throw new CustomException("You can't delete the ROLE if any USER has that role, " +
					"pls first delete all the users with this role");
		}
		try {
			roleRepo.delete(roleDb);
			log.info("Role with name {} deleted successfully", roleName);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	@Cacheable(cacheNames = "findAllByRoleNames", key = "#roleNames")
	@NonNull
	@Override
	public Set<Role> findAllByRoleNames(@NonNull Set<String> roleNames) {
		log.debug("Entering method findAllByRoleNames");
		Set<String> roleNamesToUpper = roleNames.stream()
				.map(String::toUpperCase)
				.collect(Collectors.toSet());
		final String roleNamesLog = roleNamesToUpper
				.toString()
				.replace("[", "")
				.replace("]", "");
		log.debug("Trying to find all roles with the following names: {}", roleNamesLog);
		Set<Role> roleSet = roleRepo
				.findAll()
				.stream()
				.filter(roles -> roleNamesToUpper
						.contains(roles.getRoleName()))
				.collect(Collectors.toSet());
		if (roleSet.isEmpty()) {
			log.error("Error in findAllByRoleNames() method. Cause: no roles found.");
			throw new CustomException("No roles found with inserted values!");
		}
		Set<String> nonValidRoles = roleNamesToUpper
				.stream()
				.filter(roles -> roleRepo.findByRoleName(roles).isEmpty())
				.collect(Collectors.toSet());
		if (!nonValidRoles.isEmpty()) {
			log.error("Error in findAllByRoleNames() method. Cause: roles are not in system.");
			throw new CustomException("These roles: " + nonValidRoles + " are not in system, so you can`t add it to User.");
		}
		log.info("Roles with the following names: {} were found successfully", roleNamesLog);
		return roleSet;
	}
}





