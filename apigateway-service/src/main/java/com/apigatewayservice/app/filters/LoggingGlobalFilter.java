package com.apigatewayservice.app.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;


@Component
@Slf4j
public class LoggingGlobalFilter implements GlobalFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
        Map<String, String> headers = request.getHeaders().toSingleValueMap();

        log.info("REQUEST ID: " + request.getId());
        log.info("METHOD: " + request.getMethod().name());
        log.info("PATH: " + request.getPath());
        log.info("URI: " + request.getURI());
        log.info("IP: " + request.getRemoteAddress());
        return chain.filter(exchange);
    }
}
