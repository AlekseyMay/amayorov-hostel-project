package com.amayorov.hostel.service.security;

import com.amayorov.hostel.domain.dto.security.UserDTO;
import com.amayorov.hostel.domain.entity.security.User;
import org.springframework.lang.NonNull;

import java.security.Principal;

public interface UserService {

	@NonNull
	User createUser(@NonNull UserDTO userDTO);

	@NonNull
	User saveUser(@NonNull User user);

	@NonNull
	User findUserByName(@NonNull String userName);

	void deleteUser(@NonNull String userName, Principal principal);


}
