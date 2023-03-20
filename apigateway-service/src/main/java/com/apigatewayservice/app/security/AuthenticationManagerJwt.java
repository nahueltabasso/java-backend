//package com.apigatewayservice.app.security;
//
//import io.jsonwebtoken.Jwts;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import javax.annotation.PostConstruct;
//import java.util.Base64;
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class AuthenticationManagerJwt implements ReactiveAuthenticationManager {
//
//    @Value("${app.security.jwtSecret}")
//    private String jwtSecret;
//
//    @PostConstruct
//    protected void init() {
//        this.jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
//    }
//
//    @Override
//    public Mono<Authentication> authenticate(Authentication authentication) {
//        return Mono.just(authentication.getCredentials().toString())
//                // Process the token and return the claims of token
//                .map(token -> {
//                    return Jwts.parser()
//                            .setSigningKey(jwtSecret)
//                            .parseClaimsJws(token)
//                            .getBody();
//                })
//                .map(claims -> {
//                    String username = claims.get("user_name", String.class);
//                    List<String> roles = claims.get("authorities", List.class);
//                    Collection<GrantedAuthority> authorities = roles.stream()
//                            .map(role -> new SimpleGrantedAuthority(role))
//                            .collect(Collectors.toList());
//                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
//                });
//    }
//}
