package com.auth.service.app.security.jwt;

import com.auth.service.app.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    @Value("${app.security.jwtSecret}")
    private String jwtSecret;
    @Value("${app.security.jwtExpirationTimeMs}")
    private int jwtExpirationTime;

    @PostConstruct
    protected void init() {
        this.jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }

    public String generateJwtToken(Authentication authentication) {
        log.info("Enter to generateJwtToken()");
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationTime))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateTokenFromUsername(String username) {
        log.info("Enter to generateTokenFromUsername()");
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationTime))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        log.info("Enter to getUsernameFromToken()");
        try {
            return Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody().getSubject();
        } catch (Exception e) {
            return "invalid token";
        }
    }

    public boolean validateJwtToken(String authToken) {
        log.info("Enter to validateJwtToken()");
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT Signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT Token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT Token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT Claims string is empty: {}", e.getMessage());
        }
        return false;
    }

}
