package com.apigatewayservice.app.security;

import com.apigatewayservice.app.dto.TokenDataDTO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthenticationManagerJwt implements ReactiveAuthenticationManager {

    @Value("${app.security.jwtSecret}")
    private String jwtSecret;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication.getCredentials().toString())
                // Process the token and return the claims of token
                .map(token -> {
                    Base64.Decoder decoder = Base64.getUrlDecoder();
                    String[] chunks = token.split("\\.");

                    String header = new String(decoder.decode(chunks[0]));
                    String payload = new String(decoder.decode(chunks[1]));
                    Gson gson = new Gson();
                    TokenDataDTO tokenDataDTO = gson.fromJson(payload, TokenDataDTO.class);
                    return tokenDataDTO;
                })
                .map(claims -> {
                    String username = claims.getUsername();
                    List<String> roles = claims.getAuthorities();
                    Collection<GrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority(role))
                            .collect(Collectors.toList());
                    return new UsernamePasswordAuthenticationToken("username", null, authorities);
                });
    }
}
