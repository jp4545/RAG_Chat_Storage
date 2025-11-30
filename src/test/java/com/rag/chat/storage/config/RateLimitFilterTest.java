package com.rag.chat.storage.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private Bucket bucket;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testShouldNotFilterSwaggerPaths() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/swagger-ui/index.html");
        assertTrue(rateLimitFilter.shouldNotFilter(request));

        request.setServletPath("/v3/api-docs");
        assertTrue(rateLimitFilter.shouldNotFilter(request));

        request.setServletPath("/chat");
        // no authentication set
        assertTrue(rateLimitFilter.shouldNotFilter(request));
    }

    @Test
    void testDoFilterInternalWithValidApiKeyAndTokens() throws Exception {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test-key", null)
        );
        when(rateLimiterService.resolveBucket("test-key")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1))
                .thenReturn(ConsumptionProbe.consumed(1, Long.valueOf(10)));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/data");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        rateLimitFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(response.getHeader("X-Rate-Limit-Remaining"));
    }

    @Test
    void testDoFilterInternalRateLimitExceeded() throws Exception {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test-key", null)
        );
        when(rateLimiterService.resolveBucket("test-key")).thenReturn(bucket);
        when(bucket.tryConsumeAndReturnRemaining(1))
                .thenReturn(ConsumptionProbe.rejected(0, Long.valueOf(30), Long.valueOf(30)));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/data");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        rateLimitFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        assertEquals(429, response.getStatus());
        assertTrue(response.getContentAsString().contains("Rate limit exceeded"));
    }
}