package com.apigatewayservice.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping (value = "/fallback/auth-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    @ResponseBody
    public Mono<ResponseEntity<Map<String, String>>>  authServiceFallback() {
        Map<String, String> map = new HashMap<>();
        map.put("httpStatus", HttpStatus.SERVICE_UNAVAILABLE.value() + "");
        map.put("type", "error");
        map.put("message", "Auth Service not available!");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(map));
    }

    @RequestMapping (value = "/fallback/user-service",
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    @ResponseBody
    public Mono<ResponseEntity<Map<String, String>>> userServiceFallback() {
        Map<String, String> map = new HashMap<>();
        map.put("httpStatus", HttpStatus.SERVICE_UNAVAILABLE.value() + "");
        map.put("type", "error");
        map.put("message", "User Service not available!");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(map));
    }

}
