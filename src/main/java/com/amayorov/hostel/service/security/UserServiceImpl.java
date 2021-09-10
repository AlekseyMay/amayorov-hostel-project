package com.amayorov.hostel.service.security;

import com.amayorov.hostel.domain.dto.security.UserDTO;
import com.amayorov.hostel.domain.entity.security.User;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.security.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;


	@NonNull
	@Override
	public User createUser(@NonNull UserDTO userDTO) {
		log.debug("Entering method createUser");
		log.debug("Trying to create user with following data: {}", userDTO);
		if (userRepo.findByUserName(userDTO.getUsername()).isPresent()) {
			log.error("Error in createUser() method. Cause: username already exists in DB.");
			throw new CustomException("User with this name >>> \"" + userDTO.getUsername() + "\" already exists!");
		}
		User user = userDTO.toEntity();
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		log.info("Transitional User has been created created successfully");
		return userRepo.save(user);

	}

	@CachePut(cacheNames = "findUserByName", key = "#user.userName", condition = "#user.userName != null") //condition just for test case to pass
	@NonNull
	@Override
	public User saveUser(@NonNull User user) {    //need this just for saving USER after adding ROLES in HostelBusinessLogic
		log.debug("Entering method saveUser");
		log.debug("Trying to save User after adding it a new Role, the following roles are going to be saved: {}",
				user.getRoles().toString().replace("[", "").replace("]", ""));
		log.info("User with set of roles: {} has been saved",
				user.getRoles().toString().replace("[", "").replace("]", ""));
		return userRepo.save(user);
	}

	@Cacheable(cacheNames = "findUserByName", key = "#userName")
	@NonNull
	@Override
	public User findUserByName(@NonNull String userName) {
		log.debug("Entering method findByUserName");
		log.debug("Trying to find the User with name {}", userName);
		User user = userRepo.findByUserName(userName).orElseThrow(() -> {
			log.error("Error in findUserByName() method. Cause: username is not found in DB.");
			return new CustomException("No user with this name >>> \"" + userName + "\"");
		});
		log.info("User with the name: {} was found", userName);
		return user;
	}

	@CacheEvict(cacheNames = "findUserByName", key = "#userName")
	@NonNull
	@Override
	public void deleteUser(@NonNull String userName, Principal principal) {
		log.debug("Entering method deleteUser");
		log.debug("Trying to delete User with name: {}", userName);
		if (principal.getName().equals(userName)) {
			log.error("Error in deleteUser() method. Cause: trying to delete the account on which you logged in.");
			throw new CustomException("You can't delete the account on which you logged in, " +
					"ask another ADMIN to delete your account"); //that will help me to avoid deleting the last and only ADMIN account
		}
		User userDb = userRepo.findByUserName(userName).orElseThrow(() -> {
			log.error("Error in deleteUser() method. Cause: username not fount in DB.");
			return new CustomException("No user with this username exists: " + userName + ", pls check the username");
		});
		log.info("The User with name: {} was deleted successfully", userName);
		userRepo.delete(userDb);
	}
}




