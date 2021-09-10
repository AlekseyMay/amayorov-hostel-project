package com.amayorov.hostel.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


@Configuration
@EnableResourceServer
class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
				.headers()
	                .frameOptions()
					.disable()
				.and()
					.authorizeRequests()
					.mvcMatchers("/v3/api-docs/**", "/swagger-ui/**", "/h2-console/**", "/users/registration", "/configuration/**",
							"/configuration/ui/**", "/swagger-resources/**", "/configuration/security/**",
							"/swagger-ui.html", "/swagger-ui", "/swagger", "/").permitAll()
				    .mvcMatchers(HttpMethod.GET, "/categories").hasAnyRole("MANAGER", "ADMIN")
				    .mvcMatchers(HttpMethod.DELETE,"/quarters/{quartersNumber}").hasRole("ADMIN")
				    .mvcMatchers("/guests/**", "/presences/**", "/quarters/guests/{quartersNumber}",
						                    "/quarters/validation", "/quarters/{categories}", "/quarters/premises",
						    "/quarters/cleaning-date").hasAnyRole("MANAGER", "ADMIN")
					.mvcMatchers("/**").hasRole("ADMIN");
	}
}

