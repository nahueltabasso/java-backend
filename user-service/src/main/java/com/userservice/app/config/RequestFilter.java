package com.userservice.app.config;

import com.userservice.app.clients.AuthFeignClient;
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
public class RequestFilter extends OncePerRequestFilter {

    @Autowired
    private AuthFeignClient authFeignClient;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {

            token = token.replace("Bearer ", "");
            Map<String, Object> userDetails = authFeignClient.getCurrentUserDetails(token);
            CustomUserDetails customUserDetails = new CustomUserDetails();
            customUserDetails.setUsername(userDetails.get("username").toString());
            List<String> authoritiesList = (List<String>) userDetails.get("authorities");
            List<GrantedAuthority> authorities = new ArrayList<>();
            authoritiesList.stream().forEach(a -> {
                authorities.add(new SimpleGrantedAuthority(a));
            });
            customUserDetails.setAuthorities(authorities);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities()));
        }
        filterChain.doFilter(request, response);


    }

}
