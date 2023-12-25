package com.post.service.app.security.config;

import com.post.service.app.clients.AuthClient;
import com.post.service.app.security.models.CommonUserDetailsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class WebFilterConfig implements WebFilter {

    @Autowired
    private AuthClient authClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Enter to filter()");

        return Mono.defer(() -> {
            String tokenHeader = this.getHeaderValue(exchange, "Authorization");
            if (tokenHeader == null && !tokenHeader.startsWith("Bearer")) {
                log.error("Token not valid!");
                throw new RuntimeException("Authorization token is invalid");
            }

            log.info("Authorization header with valid format");
            String token = tokenHeader.replace("Bearer ", "");

            return authClient.getCurrentUserDetails(token)
                    .flatMap(userDetails -> {
                        CommonUserDetailsDTO userDetailsDTO = CommonUserDetailsDTO.builder()
                                        .id(Long.parseLong(userDetails.get("id").toString()))
                                        .username(userDetails.get("username").toString())
                                        .token(token)
                                        .authorities(this.getAuthorities(userDetails)).build();

                        Authentication auth =
                                new UsernamePasswordAuthenticationToken(userDetailsDTO, null, userDetailsDTO.getAuthorities());
//                        ReactiveSecurityContextHolder.withAuthentication(auth);
                        return chain.filter(exchange)
                                .contextWrite(c -> ReactiveSecurityContextHolder.withAuthentication(auth));
                    })
//                    .onErrorResume(RuntimeException.class, err -> handleError(exchange, err))
                    .onErrorResume(throwable -> {
                        log.error("Error during authClient.getCurrentUserDetails: {}", throwable.getMessage());
                        return chain.filter(exchange);
                    });
        });
    }

    private String getHeaderValue(ServerWebExchange exchange, String key) {
        return exchange.getRequest().getHeaders().getFirst(key);
    }

    private List<GrantedAuthority> getAuthorities(Map<String, Object> userDetails) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<String> authoritiesList = (List<String>) userDetails.get("authorities");
        authoritiesList.stream().forEach(a -> {
            authorities.add(new SimpleGrantedAuthority(a));
        });
        return authorities;
    }
}