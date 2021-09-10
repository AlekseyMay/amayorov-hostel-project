package com.amayorov.hostel.repository.security;

import com.amayorov.hostel.domain.entity.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository <Role, Long> {

	Optional<Role> findByRoleName(String roleName);

}
