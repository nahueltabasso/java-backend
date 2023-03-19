package com.auth.service.app.security.jwt;

import com.auth.service.app.security.services.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Enter to doFilterInternal()");
        log.info("Uri to process = " + request.getRequestURI());

        try {
            log.info(request.getRequestURI());
            log.info(request.getHeader("Authorization"));
            String jwtToken = parseJwt(request);

            if (jwtToken != null && jwtProvider.validateJwtToken(jwtToken)) {
                String username = jwtProvider.getUsernameFromToken(jwtToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Invalid token");
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        log.info("Enter to parseJwt()");
        String headerAuthorization = request.getHeader("Authorization");
        if (!headerAuthorization.isEmpty() && headerAuthorization.startsWith("Bearer ")) {
            return headerAuthorization.substring(7, headerAuthorization.length());
        }
        return null;
    }
}
