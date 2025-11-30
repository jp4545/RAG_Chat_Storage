package com.rag.chat.storage.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // Skip Swagger + open endpoints
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger")
                || path.equals("/swagger-ui.html")
                || SecurityContextHolder.getContext().getAuthentication() == null;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (apiKey != null) {
            var bucket = rateLimiterService.resolveBucket(apiKey);

            var probe = bucket.tryConsumeAndReturnRemaining(1);

            if (!probe.isConsumed()) {
                response.setStatus(429);
                response.getWriter().write(
                        "Rate limit exceeded. Try again in a while."
                );
                return;
            }

            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
        }

        filterChain.doFilter(request, response);
    }
}