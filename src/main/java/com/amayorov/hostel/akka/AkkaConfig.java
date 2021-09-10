package com.amayorov.hostel.akka;

import akka.actor.ActorSystem;
import com.amayorov.hostel.akka.extension.SpringExtension;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AkkaConfig {

	private final ApplicationContext applicationContext;
	private final SpringExtension springExtension;

	@Bean
	public ActorSystem actorSystem() {
		ActorSystem system = ActorSystem.create("AKKA");
		// initializing the application context in the Akka Spring Extension
		springExtension.initialize(applicationContext);
		return system;
	}
}