package com.rag.chat.storage.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${security.api.key}")
    public String expectedApiKey;

    @Value("${security.api.secret}")
    public String expectedApiSecret;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger")
                || path.startsWith("/api-docs")
                || path.equals("/swagger-ui.html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-KEY");
        String apiSecret = request.getHeader("X-API-SECRET");

        if (apiKey == null || apiSecret == null ||
                !expectedApiKey.equals(apiKey) ||
                !expectedApiSecret.equals(apiSecret)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid API key or secret");
            return;
        }

        // Authentication is valid → allow request to proceed
        Authentication auth = new UsernamePasswordAuthenticationToken(
                apiKey,
                null,
                Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}