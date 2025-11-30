package com.rag.chat.storage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.chat.storage.config.ApiKeyAuthFilter;
import com.rag.chat.storage.config.RateLimitFilter;
import com.rag.chat.storage.dto.CreateSessionRequest;
import com.rag.chat.storage.dto.UpdateFavouriteSession;
import com.rag.chat.storage.dto.UpdateSessionTitle;
import com.rag.chat.storage.entity.SessionEntity;
import com.rag.chat.storage.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SessionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class MockConfig {
        @Bean
        SessionService sessionService() {
            return Mockito.mock(SessionService.class);
        }

        @Bean
        ApiKeyAuthFilter apiKeyAuthFilter() {
            return Mockito.mock(ApiKeyAuthFilter.class);
        }

        @Bean
        RateLimitFilter rateLimitFilter() {
            return Mockito.mock(RateLimitFilter.class);
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            return Mockito.mock(CorsConfigurationSource.class);
        }

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired
    private SessionService sessionService;


    @Autowired
    private ObjectMapper objectMapper;

    private SessionEntity sampleSession;

    @BeforeEach
    void setUp() {
        sampleSession = new SessionEntity();
        sampleSession.setId(UUID.randomUUID());
        sampleSession.setUserId(UUID.randomUUID());
        sampleSession.setTitle("Test Session");
        sampleSession.setFavorite(false);
    }

    @Test
    void testCreateSession() throws Exception {
        CreateSessionRequest req = new CreateSessionRequest(sampleSession.getUserId().toString(), "Test Session");

        Mockito.when(sessionService.createSession(any(UUID.class), anyString()))
                .thenReturn(sampleSession);
        mockMvc.perform(post("/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Session"));
    }

    @Test
    void testGetSessionById() throws Exception {
        UUID id = sampleSession.getId();
        Mockito.when(sessionService.getSession(any(UUID.class))).thenReturn(sampleSession);
        SessionEntity result = sessionService.getSession(UUID.randomUUID());
        String json = objectMapper.writeValueAsString(result);
        System.out.println("Returned session: " + json);
        mockMvc.perform(get("/session/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Session"));
    }

    @Test
    void testGetAllSessions() throws Exception {
        List<SessionEntity> sessions = Arrays.asList(sampleSession);
        Mockito.when(sessionService.getAllSessions()).thenReturn(sessions);

        mockMvc.perform(get("/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Session"));
    }

    @Test
    void testGetSessionsByUserId() throws Exception {
        UUID userId = UUID.randomUUID();
        List<SessionEntity> sessions = Arrays.asList(sampleSession);
        Mockito.when(sessionService.getSessionsByUser(any(UUID.class))).thenReturn(sessions);

        mockMvc.perform(get("/session/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Session"));
    }

    @Test
    void testUpdateFavourite() throws Exception {
        UpdateFavouriteSession req = new UpdateFavouriteSession(sampleSession.getId().toString(), "Y");

        sampleSession.setFavorite(true);
        Mockito.when(sessionService.updateFavorite(any(UUID.class), anyBoolean()))
                .thenReturn(sampleSession);

        mockMvc.perform(post("/session/updateFavourite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(true));
    }

    @Test
    void testUpdateTitle() throws Exception {
        UpdateSessionTitle req = new UpdateSessionTitle(sampleSession.getId().toString(), "Updated Title");

        sampleSession.setTitle("Updated Title");
        Mockito.when(sessionService.updateTitle(sampleSession.getId(), "Updated Title"))
                .thenReturn(sampleSession);

        mockMvc.perform(post("/session/updateTitle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    //@Test
    void testDeleteSession() throws Exception {
        UUID id = sampleSession.getId();
        Mockito.when(sessionService.deleteSession(id)).thenReturn("Deleted Successfully");
        String response = sessionService.deleteSession(id);
        System.out.println(response);
        mockMvc.perform(delete("/session/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted Successfully"));
    }
}