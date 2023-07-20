package com.apigatewayservice.app.filters;

import com.apigatewayservice.app.dto.TokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class BeforeRedirectionFilter extends AbstractGatewayFilterFactory<BeforeRedirectionFilter.Config> {

    public static class Config {}
    @Autowired
    private Environment environment;
    private WebClient.Builder webClient;

    public BeforeRedirectionFilter(WebClient.Builder webClient) {
        super(BeforeRedirectionFilter.Config.class);
        this.webClient = webClient;
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("Enter to apply()");
        log.info("Enter to Before Redirection Filter service");
        return (((exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.error("ERROR: the request does not contain the AUTHORIZATION header");
                return onError(exchange, HttpStatus.BAD_REQUEST);
            }

            String tokenHeader =
                    exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            String [] chunks = tokenHeader.split(" ");
            if (chunks.length != 2 || !chunks[0].equals("Bearer")) {
                log.error("ERROR: invalid token");
                return onError(exchange, HttpStatus.BAD_REQUEST);
            }

            log.info("Request URI = " + exchange.getRequest().getURI());
            return webClient.build()
                    .post()
                    .uri(environment.getProperty("app.security.validate-token-uri") + chunks[1])
                    .header("Authorization", "Bearer " + chunks[1])
                    .retrieve().bodyToMono(TokenDTO.class)
                    .map(t -> {
                        log.info("Token valid");
                        log.info("Response from auth-service = " + t.getAccessToken());
                        return exchange;
                    }).flatMap(chain::filter);
        }));    }

    public Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        log.info("Enter to onError()");
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

}
