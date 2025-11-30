package com.rag.chat.storage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.chat.storage.config.ApiKeyAuthFilter;
import com.rag.chat.storage.config.RateLimitFilter;
import com.rag.chat.storage.dto.ChatRequest;
import com.rag.chat.storage.entity.MessageEntity;
import com.rag.chat.storage.service.ChatService;
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

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatService chatService;

    private UUID sessionId;
    private MessageEntity sampleMessage;

    @TestConfiguration
    static class MockConfig {
        @Bean
        ChatService chatService() { return Mockito.mock(ChatService.class);};
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

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        sampleMessage = new MessageEntity();
        sampleMessage.setId(UUID.randomUUID());
        sampleMessage.setSessionId(sessionId);
        sampleMessage.setContent("Hello AI");
        sampleMessage.setCreatedAt(Instant.now());
    }

    @Test
    void testChatWithAssistant() throws Exception {
        ChatRequest req = new ChatRequest(sessionId.toString(), "Hello AI");

        Mockito.when(chatService.sendMessage(any(UUID.class), anyString()))
                .thenReturn("Hi User");

        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Hi User"));
    }

    @Test
    void testGetMessageHistory() throws Exception {
        List<MessageEntity> history = Arrays.asList(sampleMessage);
        Mockito.when(chatService.getChatHistory(sessionId)).thenReturn(history);

        mockMvc.perform(get("/chat/{id}", sessionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello AI"));
    }
}