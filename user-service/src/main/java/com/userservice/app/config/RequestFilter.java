package com.userservice.app.config;

import com.userservice.app.clients.AuthFeignClient;
import lombok.extern.slf4j.Slf4j;
import nrt.common.microservice.security.dto.CommonUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RequestFilter extends OncePerRequestFilter {

    @Autowired
    private AuthFeignClient authFeignClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Enter to doFilterInternal()");
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            log.info("Authorization header with valid format");
            String token = tokenHeader.replace("Bearer ", "");
            List<GrantedAuthority> authorities = new ArrayList<>();
            Map<String, Object> userDetails = authFeignClient.getCurrentUserDetails(token);
            CommonUserDetails commonUserDetails = new CommonUserDetails();
            commonUserDetails.setId(Long.parseLong(userDetails.get("id").toString()));
            commonUserDetails.setUsername(userDetails.get("username").toString());
            List<String> authoritiesList = (List<String>) userDetails.get("authorities");
            authoritiesList.stream().forEach(a -> {
                authorities.add(new SimpleGrantedAuthority(a));
            });
            commonUserDetails.setAuthorities(authorities);
            commonUserDetails.setToken(token);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(commonUserDetails, null, authorities));

            log.info("User making the request -> " + commonUserDetails.getUsername());
        }
        filterChain.doFilter(request, response);
    }
}
