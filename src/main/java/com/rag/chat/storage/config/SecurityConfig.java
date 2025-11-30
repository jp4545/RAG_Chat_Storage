package com.rag.chat.storage.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiKeyAuthFilter apiKeyAuthFilter;
    private final RateLimitFilter rateLimitFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll() // Allow public routes
                        .anyRequest().authenticated()              // Secure all other APIs
                )
                .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter, ApiKeyAuthFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}