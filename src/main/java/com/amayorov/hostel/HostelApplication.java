package com.amayorov.hostel;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@SecurityScheme(name = "JWT token assignment", description = "Authorization with all the scopes, " +
		"privileges in this app depends on the user`s role.", type = SecuritySchemeType.OAUTH2,
		flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "oauth/token")))
public class HostelApplication {

	public static void main(String[] args) {
		SpringApplication.run(HostelApplication.class, args);
	}
}
