package com.todolist.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        security.csrf((csrf) -> csrf.disable());
        security.cors((cors) -> cors.disable());
        security.authorizeHttpRequests((auth) -> auth.anyRequest().permitAll());
        return security.build();
    }
}
