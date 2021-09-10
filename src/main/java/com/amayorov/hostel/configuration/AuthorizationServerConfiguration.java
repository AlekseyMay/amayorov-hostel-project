package com.amayorov.hostel.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;


	@Bean
	public TokenStore tokenStore() {                             // adding custom token instead of standard ->> JWT token
		return new JwtTokenStore(converter());
	}

	@Bean                                                        //configuration of JWT token
	public JwtAccessTokenConverter converter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey("secret");
		return converter;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints
//				.pathMapping("/oauth/token", "/oa/token")
				.authenticationManager(authenticationManager)
				.tokenStore(tokenStore())// adding JWT system
				.accessTokenConverter(converter());             // adding JWT token system
	}


	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
				.inMemory()
				.withClient("my-trusted-client")
				.authorizedGrantTypes("client_credentials", "password") // password is only acceptable when the authserver and the client
				// are developed by same organization otherwise it is not
				// recommended to use pass as granttype
				.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
				.scopes("read", "write", "trust")
				//					.resourceIds("oauth2-resource")                     // default value of this setting if not override it ll be "oauth2-resource"
				.accessTokenValiditySeconds(10000)
//				.redirectUris("http://localhost:8080/swagger-ui/oauth2-redirect.html")
				.secret(passwordEncoder.encode("secret"));
	}


	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {
		security
				.checkTokenAccess("permitAll()"); //can be changed to "permitAll()" for all to have access to token info on /oauth/check_token/?token={TOKENVALUE}
												  // or can be isAuthenticated() for only authenticated people
	}


}

