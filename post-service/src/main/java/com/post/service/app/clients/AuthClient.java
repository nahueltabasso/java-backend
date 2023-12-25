package com.post.service.app.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class AuthClient {

    private final WebClient webClient;
    private final Environment environment;

    @Autowired
    public AuthClient(WebClient.Builder webClientBuilder, Environment environment) {
        this.environment = environment;
        System.out.println("AUTH_SERVICE_URL -> " + environment.getProperty("auth.service.uri"));
        this.webClient = webClientBuilder.baseUrl(environment.getProperty("auth.service.uri")).build();
    }

    public Mono<Map<String, Object>> getCurrentUserDetails(String token) {
        return this.webClient
                .get()
                .uri("/api/security/auth/get-user-details?token="+token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnError(e -> {
//                    System.out.println(this.baseAuthUrl);
                    e.printStackTrace();
                    throw new RuntimeException(e);
                });
    }


}