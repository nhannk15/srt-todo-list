package com.todolist.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final OAuth2LoginSuccessHandler auth2LoginSuccessHandler;

    public SecurityConfiguration(OAuth2LoginSuccessHandler auth2LoginSuccessHandler) {
        this.auth2LoginSuccessHandler = auth2LoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable());
        http.cors((cors) -> cors.disable());
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/oauth2/**", "/login/**").permitAll()
                .requestMatchers("/error", "/actuator/**").permitAll()
                .anyRequest().authenticated());
        http.oauth2Login((oauth2) -> oauth2.successHandler(auth2LoginSuccessHandler));
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
