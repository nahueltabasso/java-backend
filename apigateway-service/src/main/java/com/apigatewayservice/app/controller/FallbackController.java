package com.apigatewayservice.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class FallbackController {

    @GetMapping("/fallback/auth-service")
    public Mono<String> authServiceFallback() {
        return Mono.just("Auth Service not available!");
    }

    @GetMapping("/fallback/user-service")
    public Mono<String> userServiceFallback() {
        return Mono.just("User Service not available!");
    }

}
