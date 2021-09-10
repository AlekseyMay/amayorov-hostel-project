package com.amayorov.hostel.akka;

import com.amayorov.hostel.domain.entity.security.User;
import lombok.Value;

import java.util.List;

@Value
public class UserList {
	List<User> users;

	public UserList(List<User> users) {
		this.users = users;
	}
}
