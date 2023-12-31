package com.itechgenie.framework.filters;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppRequestsFilter implements WebFilter {
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		ServerHttpRequest req = exchange.getRequest();

		log.info("Starting a transaction for req : {}", req.getPath());

		log.info("Committing a transaction for req : {}", req.getPath());
		return chain.filter(exchange) .contextWrite(Context.of("headers", req.getHeaders()));
	}

}
