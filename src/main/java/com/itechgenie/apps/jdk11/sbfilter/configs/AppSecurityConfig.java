package com.itechgenie.apps.jdk11.sbfilter.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import com.itechgenie.apps.jdk11.sbfilter.security.CustomReactiveAuthenticationManager;
import com.itechgenie.apps.jdk11.sbfilter.security.CustomReactiveUserDetailsService;
import com.itechgenie.apps.jdk11.sbfilter.utils.AppCommonUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class AppSecurityConfig {

	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
	String jwkUri;

	@Bean
	SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {

		http.authorizeExchange(
				(exchange) -> exchange.pathMatchers("/", "/public/**", "/**login**", "/error", "/webjars/**",
						"/actuator/**", "/v3/api-docs/**", "swagger-ui.html").permitAll().anyExchange().authenticated())
				// .authenticationManager(this.manager)
				// .authenticationManager(customReactiveAuthenticationManager()).authorizeExchange(withDefaults())
				.oauth2ResourceServer((oauth2ResourceServer) -> {
					// oauth2ResourceServer.jwt((jwt) -> jwtDecoder());
					oauth2ResourceServer.authenticationManagerResolver(resolver());
				})
		// .oauth2ResourceServer(withDefaults())
		// .authenticationManagerResolver(resolver())
		// .authenticationManager(customReactiveAuthenticationManager())
		// .oauth2ResourceServer(withDefaults()).authenticationManager(customReactiveAuthenticationManager())
		; // .httpBasic(Customizer.withDefaults());
			// .httpBasic(Customizer.withDefaults());

		return http.build();
	}

	public ReactiveAuthenticationManagerResolver<ServerWebExchange> resolver() {
		return exchange -> {
			if (exchange.getRequest().getPath().subPath(0).value().startsWith("/employee")) {
				log.debug("Resolving employee path");
				return Mono.just(customReactiveAuthenticationManager());
			}
			log.debug("Resolving normal path");
			return Mono.just(customReactiveAuthenticationManager());
		};
	}

	@Bean
	CustomReactiveUserDetailsService customReactiveUserDetailsService() {
		return new CustomReactiveUserDetailsService();
	}

	@Bean
	CustomReactiveAuthenticationManager customReactiveAuthenticationManager() {
		return new CustomReactiveAuthenticationManager(customReactiveUserDetailsService(), jwkUri );
	}

	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withJwkSetUri(jwkUri)
				// .withIssuerLocation(issuerUri)
				.build();
	}

	public static MultiValueMap<String, Object> getHttpHeaderToMap(ServerWebExchange exchange) {
		MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();

		// Extract and return request headers from the authentication details
		for (Map.Entry<String, List<String>> entry : exchange.getRequest().getHeaders().entrySet()) {
			String key = entry.getKey();
			List<Object> values = entry.getValue().stream().map(value -> (Object) value).collect(Collectors.toList());

			multiValueMap.put(key, values);
		}
		log.debug("$$$ Final headers: " + AppCommonUtil.toJson(multiValueMap));
		return multiValueMap;
	}
}
