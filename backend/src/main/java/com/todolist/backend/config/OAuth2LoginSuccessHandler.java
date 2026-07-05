package com.todolist.backend.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.todolist.backend.model.entity.MyUser;
import com.todolist.backend.repository.MyUserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${frontend.base-url}")
    private String frontendBasedUrl;

    private final MyUserRepository myUserRepository;
    private final JwtService jwtService;

    @Autowired
    public OAuth2LoginSuccessHandler(MyUserRepository myUserRepository, JwtService jwtService) {
        this.myUserRepository = myUserRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        MyUser user = myUserRepository.findByEmail(email)
                .orElseGet(() -> {
                    String googleId = oAuth2User.getAttribute("sub");
                    String fullName = oAuth2User.getAttribute("name");

                    MyUser newUser = MyUser
                            .builder()
                            .email(email)
                            .fullname(fullName)
                            .googleId(googleId)
                            .build();
                    return myUserRepository.save(newUser);
                });
        String accessToken = null;
        String refreshToken = null;
        try {
            accessToken = jwtService.generateAccessToken(user);
            refreshToken = jwtService.generateRefreshToken(user);
            user.setRefreshToken(refreshToken);
            myUserRepository.save(user);
        } catch (JOSEException ex) {
            ex.printStackTrace();
        }

        Cookie cookie = new Cookie("access_token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 15);
        response.addCookie(cookie);

        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(refreshTokenCookie);

        response.sendRedirect(frontendBasedUrl);
    }

}
