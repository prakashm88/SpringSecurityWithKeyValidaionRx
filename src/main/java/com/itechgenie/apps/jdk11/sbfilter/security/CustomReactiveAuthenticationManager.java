package com.itechgenie.apps.jdk11.sbfilter.security;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import com.itechgenie.apps.jdk11.sbfilter.utils.AppCommonUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

	private final CustomReactiveUserDetailsService userDetailsService;

	private final String jwkUrl;

	public CustomReactiveAuthenticationManager(CustomReactiveUserDetailsService userDetailsService, String jwkUrl) {
		this.userDetailsService = userDetailsService;
		this.jwkUrl = jwkUrl;
	}

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		log.debug("Inside CustomReactiveAuthenticationManager.authenticate: " + authentication);

		JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkUrl).build();

		Jwt token = jwtDecoder.decode(authentication.getName());

		log.debug("Inside CustomReactiveAuthenticationManager.authenticate: JWT Object: " + AppCommonUtil.toJson(token ));
		
		// Validate JWT token and fetch user details using userDetailsService
		return validateAndFetchUserDetails(token).map(userDetails -> {
			MultiValueMap<String, Object> requestHeaders = CustomReactiveAuthenticationManager.getHttpHeaderToMap(authentication) ; // CustomReactiveAuthenticationManager
			// .getHttpHeaderToMap(authentication); //
			// extractRequestHeaders(exchange.getRequest());

			// Create CustomUserDetails object with request headers
			UserDetails ud = new CustomUserDetails(userDetails.getUsername(), userDetails.getAuthorities(), "1", "1",
					requestHeaders);

			Authentication updatedAuthentication = new UsernamePasswordAuthenticationToken(ud,
					userDetails.getPassword(), ud.getAuthorities());

			log.debug("Inside CustomReactiveAuthenticationManager.updatedAuthentication: " + updatedAuthentication);

			return updatedAuthentication;
		}).switchIfEmpty(Mono.empty()); // Return empty Mono if user details not found

	}

	private Mono<UserDetails> validateAndFetchUserDetails(Jwt token) {
		return userDetailsService.findByUsername(token.getSubject());
	}

	public static MultiValueMap<String, Object> getHttpHeaderToMap(Authentication authentication) {
		MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
		// Extract and return request headers from the authentication details
		if (authentication.getDetails() instanceof ServerWebExchange) {
			ServerWebExchange exchange = (ServerWebExchange) authentication.getDetails();
			for (Map.Entry<String, List<String>> entry : exchange.getRequest().getHeaders().entrySet()) {
				String key = entry.getKey();
				List<Object> values = entry.getValue().stream().map(value -> (Object) value)
						.collect(Collectors.toList());

				multiValueMap.put(key, values);
			}
		}
		return multiValueMap;
	}

}
