package com.amayorov.hostel.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {

	@Bean
	public GroupedOpenApi apiConfig() {
		return GroupedOpenApi.builder()
				.group("HostelAPI")
				.pathsToMatch("/categories/**", "/quarters/**", "/guests/**",
						"/presences/**", "/users/**", "/roles/**")
				.build();
	}

	@Bean
	public OpenAPI openApiConfig() {
		return new OpenAPI().info(new Info().title("Hostel RESTful Web Services")
				.version("1.0.0")
				.description("<h2><b>Documentation</b></h2> \n\n" +
						"----------------------------- \n\n" +
						"First of all be attentive while inserting information into requests' fields. As i was told not to use lots of similar DTOs and SWAGGER doesn't support dynamically \n\n" +
						"changing documentation of DTO fields for different request (the only way was to use different DTOs for each request). The only option was to use hints which fields to fill and which have to be left unfilled. \n\n" +
						"----------------------------- \n\n" +
						"At the start of the application the <b>default User with 'username': admin and 'password': admin</b> with 'roles': `ADMIN`,`MANAGER` has been created. \n\n" +
						"----------------------------- \n\n" +
						"<b>Client details: \n\n" +
						"'client_id': my-trusted-client \n\n" +
						"'client_secret': secret</b> \n\n" +
						"---------------------------- \n\n" +
						"my JMS default endpoint (may differ): <http://localhost:8161/> \n\n" +
						"login: admin \n\n" +
						"password: admin \n\n" +
						"<b>JMS implementation:</b> when a new User with role `ADMIN` is created it sends queue and listener sends informational email to email list, after which a topic is going to be created and all the subscribers \n\n" +
						"receive console informational message about the email sent to them. To check whether it is working please log in gmail.com: \n\n" +
						"login: amayorovhostel@gmail.com \n\n" +
						"password: Hostel111 \n\n" +
						"----------------------------- \n\n" +
						"<b>H2 database info:</b> \n\n" +
						"endpoint: <http://localhost:8080/h2-console> \n\n" +
						"JDBC URL: jdbc:h2:file:./src/main/resources/data/hosteldb;AUTO_SERVER=true \n\n" +
						"user name:  sa \n\n" +
						"password:   \n\n" +
						"----------------------------- \n\n" +
						"<b>Caffeine cache implementation:</b> \n\n" +
						"All the requests are using cache system, setting/cleaning/updating. Cache cleans automatically after 60 min of inactivity. \n\n" +
						"----------------------------- \n\n" +
						"<b>Akka actors classic simple implementation:</b> \n\n" +
						"Every 120 seconds actor shows in console information about all registered users and about quarters available for the next 7 days. \n\n" +
						"----------------------------- \n\n" +
						"<b>Docker compose commands:</b> \n\n" +
						"1. mvn clean        || just once when changes to the code are made\n\n" +
						"2. mvn install      || just once when changes to the code are made\n\n" +
						"3. docker-compose build \n\n" +
						"4. docker-compose up OR docker-compose up -d (to run in hidden mode, only in Desktop Docker log will be shown) \n\n" +
						"5. docker-compose stop (to stop the working containers)\n\n" +
						"6. docker-compose down (to stop and delete all the containers) \n\n" +
						"7. docker-compose start (to start exixsting containers) OR docker-compose up \n\n" +
						"It is also possible to use Docker Desktop software to start or stop containers. \n\n" +
						"----------------------------- \n\n" +
						"<b>Way of proceeding (to test the app):</b> \n\n" +
						"1. Check Registration. It should work for everybody. \n\n" +
						"2. At start of the application it creates two roles: ADMIN, MANAGER. And these roles are already set up for privileges, \n\n" +
						"just for test purposes you can create additional roles, but in order to achieve functionality, yo have to change the code. \n\n" +
						"3. Log in with default admin User and create a new Admin user. Log out and log in with newly created account. \n\n" +
						"4. Create Categories, at least several from the enum List. \n\n" +
						"5. Create several Quarters. \n\n" +
						"6. Imagine that any guests need particular quarters for particular time, so check the quarters for availability with /quarters/validation, \n\n" +
						"of course at the beginning, when no guests with presence added, it will response the list of all the quarters, meaning that all are free. \n\n" +
						"7. Create several Guests. \n\n" +
						"8. You can try adding additional presence to guests. \n\n" +
						"9. You can check the quarters again on those dates on which you created presence for guests, it will response that there are no free quarters with \n\n" +
						"demanded categories and amount. \n\n" +
						"10. You can try now any other remaining requests. As well as log in as `MANAGER` to check if the privileges works in accordance with technical assignment.\n\n" +
						"-------------------------------"))
				.tags(List.of(
						new Tag().name("Registration || NO AUTHENTICATION NEEDED").description("Accessed without authentication."),
						new Tag().name("Roles").description("Role creation/deletion. Roles: ADMIN"),
						new Tag().name("Users").description("Users management. Roles: ADMIN"),
						new Tag().name("Categories").description("Categories management. Roles: ADMIN, partly MANAGER"),
						new Tag().name("Quarters").description("Quarters management. Roles: ADMIN, partly MANAGER"),
						new Tag().name("Guests").description("Guests management. Roles: ADMIN, MANAGER"),
						new Tag().name("Presences").description("Presence management. Roles: ADMIN, MANAGER")));
	}
}
