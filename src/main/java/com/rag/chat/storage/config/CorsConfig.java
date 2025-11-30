package com.rag.chat.storage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:8080"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "DELETE"
        ));

        config.setAllowedHeaders(List.of(
                "X-API-KEY",
                "X-API-SECRET"
        ));

        config.setAllowCredentials(true);

        config.setExposedHeaders(List.of("X-Rate-Limit-Remaining"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}