package com.apigatewayservice.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity httpSecurity) {
        return httpSecurity.authorizeExchange()
                .pathMatchers("/api/security/auth/**").permitAll()
                .pathMatchers("/api/security/password/**").permitAll()
                .pathMatchers("/api/users/**").hasRole("USER")
                .anyExchange().authenticated()
                .and().csrf().disable()
                .build();
    }
}
