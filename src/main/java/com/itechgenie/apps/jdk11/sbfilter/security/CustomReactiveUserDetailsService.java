package com.itechgenie.apps.jdk11.sbfilter.security;

import java.util.UUID;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		// TODO Auto-generated method stub

		log.debug("Inside CustomReactiveUserDetailsService.findByUsername: user id: " + username);
		
		CustomUserDetails ud = new CustomUserDetails();
		ud.setUsername(username);
		ud.setSessionId("ITG".concat(UUID.randomUUID().toString()));
		ud.setClientId("ITG-WEB");
		
		return Mono.just(ud);
	}

}
