package com.itechgenie.apps.jdk11.sbfilter.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/secure")
public class SBAppController {

	@GetMapping("/api/user")
	public ResponseEntity<Mono<String>> usersProxy() {
		log.debug("Inside secure controller");

		Mono<String> name = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
				.map(Authentication::getName);

		log.debug("Logged in user info: " + name);
		HttpStatus status = HttpStatus.OK;

		return ResponseEntity.status(status).body(name);
	}

	@GetMapping("/api/user/details")
	public ResponseEntity<Mono<Authentication>> userDetails() {
		log.debug("Inside secure controller.userDetails");

		Mono<Authentication> auth = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);

		HttpStatus status = HttpStatus.OK;

		return ResponseEntity.status(status).body(auth);
	}
}
