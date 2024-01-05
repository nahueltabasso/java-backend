package com.post.service.app.clients;

import com.post.service.app.models.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserClient {

    private final WebClient webClient;
    private final Environment environment;

    @Autowired
    public UserClient(WebClient.Builder webClientBuilder, Environment environment) {
        this.environment = environment;
        System.out.println("USER_SERVICE_URL -> " + environment.getProperty("user.service.uri"));
        this.webClient = webClientBuilder.baseUrl(environment.getProperty("user.service.uri")).build();
    }

    public Mono<UserDTO> getUserById(String token, Long userProfileId) {
        return this.webClient
                .get()
                .uri("/api/users/user-profile/" + userProfileId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .doOnError(e -> {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                });

    }
}
