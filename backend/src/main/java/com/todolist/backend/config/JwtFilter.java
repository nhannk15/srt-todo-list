package com.todolist.backend.config;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.todolist.backend.model.entity.MyUser;
import com.todolist.backend.repository.MyUserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserRepository myUserRepository;

    

    public JwtFilter(JwtService jwtService, MyUserRepository myUserRepository) {
        this.jwtService = jwtService;
        this.myUserRepository = myUserRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = null;
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie: request.getCookies()) {
                if (cookie.getName().equals("access_token")) {
                    accessToken = cookie.getValue();
                }

                if (cookie.getName().equals("refresh_token")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (accessToken == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            }
        }

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        

        try {
            if (jwtService.verifyToken(accessToken)) {
                authenticateUser(accessToken);
            } else if (refreshToken != null && jwtService.verifyToken(refreshToken)) {
                String email = jwtService.extractEmail(refreshToken);
                MyUser user = myUserRepository.findByEmail(email).orElse(null);

                if (user.getRefreshToken().equals(refreshToken)) {
                    String newAccessToken = jwtService.generateAccessToken(user);
                    Cookie newAccessCookie = new Cookie("access_token", newAccessToken);
                    newAccessCookie.setHttpOnly(true);
                    newAccessCookie.setSecure(false);
                    newAccessCookie.setPath("/");
                    newAccessCookie.setMaxAge(60 * 15);
                    response.addCookie(newAccessCookie);

                    authenticateUser(newAccessToken);
                }
            }
        } catch (Exception e) {
            log.info("jwtFilter() - Exception occurs");
        }

        filterChain.doFilter(request, response);
    }

    public void authenticateUser(String accessToken) throws ParseException {
        String email = jwtService.extractEmail(accessToken);

        MyUser user = myUserRepository.findByEmail(email).orElse(null);
        if (user != null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    email, // principal → getPrincipal() trả về String email
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER" )));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
    
}
