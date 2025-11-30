package com.rag.chat.storage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.chat.storage.config.ApiKeyAuthFilter;
import com.rag.chat.storage.config.RateLimitFilter;
import com.rag.chat.storage.dto.FileUploadRequest;
import com.rag.chat.storage.service.*;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock the three service dependencies
    @Autowired
    private DocumentService documentService;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private DocumentReaderService readerService;

    @TestConfiguration
    static class MockConfig {

        @Bean
        DocumentService documentService() {return Mockito.mock(DocumentService.class);}
        @Bean
        EmbeddingService embeddingService() {return Mockito.mock(EmbeddingService.class);}

        @Bean
        DocumentReaderService readerService() {return Mockito.mock(DocumentReaderService.class);}
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

    @Test
    void testUploadFile() throws Exception {
        // Arrange: create request DTO
        FileUploadRequest req = new FileUploadRequest("sample.txt");

        // Stub the service calls
        Mockito.when(readerService.extractText(anyString()))
                .thenReturn("This is file content");
        Mockito.when(embeddingService.generateEmbedding(anyString()))
                .thenReturn(new float[]{0.1f, 0.2f, 0.3f});
        Mockito.doNothing().when(documentService)
                .saveDocument(anyString(), anyString(), any(float[].class));

        // Act & Assert
        mockMvc.perform(post("/api/upload-file")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("File uploaded and embeddings saved successfully!"));
    }
}