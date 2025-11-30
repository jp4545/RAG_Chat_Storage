package com.rag.chat.storage.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiKeyAuthFilterTest {

    private ApiKeyAuthFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new ApiKeyAuthFilter();

        // Inject expected values manually (since @Value won't work in plain unit test)
        filter.expectedApiKey = "test-key";
        filter.expectedApiSecret = "test-secret";

        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidApiKeyAndSecret() throws ServletException, IOException {
        when(request.getHeader("X-API-KEY")).thenReturn("test-key");
        when(request.getHeader("X-API-SECRET")).thenReturn("test-secret");

        filter.doFilterInternal(request, response, filterChain);

        // Verify filter chain proceeded
        verify(filterChain, times(1)).doFilter(request, response);

        // Verify authentication was set
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test-key", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void testInvalidApiKeyOrSecret() throws ServletException, IOException {
        when(request.getHeader("X-API-KEY")).thenReturn("wrong-key");
        when(request.getHeader("X-API-SECRET")).thenReturn("wrong-secret");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilterInternal(request, response, filterChain);

        // Verify filter chain was NOT called
        verify(filterChain, never()).doFilter(request, response);

        // Verify response status set to 401
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testMissingHeaders() throws ServletException, IOException {
        when(request.getHeader("X-API-KEY")).thenReturn(null);
        when(request.getHeader("X-API-SECRET")).thenReturn(null);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testShouldNotFilterSwaggerPaths() {
        when(request.getServletPath()).thenReturn("/swagger-ui/index.html");
        assertTrue(filter.shouldNotFilter(request));

        when(request.getServletPath()).thenReturn("/v3/api-docs");
        assertTrue(filter.shouldNotFilter(request));

        when(request.getServletPath()).thenReturn("/api-docs");
        assertTrue(filter.shouldNotFilter(request));

        when(request.getServletPath()).thenReturn("/swagger-ui.html");
        assertTrue(filter.shouldNotFilter(request));

        when(request.getServletPath()).thenReturn("/chat");
        assertFalse(filter.shouldNotFilter(request));
    }
}