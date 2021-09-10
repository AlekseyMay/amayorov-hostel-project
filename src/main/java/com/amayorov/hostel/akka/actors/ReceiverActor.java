package com.amayorov.hostel.akka.actors;


import akka.actor.AbstractActor;
import com.amayorov.hostel.akka.UserList;
import com.amayorov.hostel.domain.entity.security.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReceiverActor extends AbstractActor {

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(UserList.class, this::usersInfo)
				.match(Integer.class, this::quarterAmount)
				.build();
	}

	private void usersInfo(UserList userList) {
		log.info("The amount of all registered users in system is: " + userList.getUsers().size() + ", updates every 120 sec, their usernames: " +
				userList.getUsers().stream().map(User::getUserName).collect(Collectors.toList()).toString()
						.replaceAll("[\\[\\]]", "") + ".");
	}

	private void quarterAmount(Integer integer) {
		log.info("Amount of free Quarters of all categories for the next 7 days: " + integer + ". Next update in 120 sec.");
	}
}

