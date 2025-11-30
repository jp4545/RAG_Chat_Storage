package com.rag.chat.storage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.chat.storage.pojo.EmbeddingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmbeddingServiceTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private EmbeddingService embeddingService;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    //@Test
    void testGenerateEmbedding() throws Exception {
        // Arrange: simulate JSON response from LLaMA API
        String fakeResponse = "{ \"data\": [ { \"embedding\": [0.1, 0.2, 0.3] } ] }";

        //when(httpResponse.body()).thenReturn(fakeResponse);
        //when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                //.thenReturn(httpResponse);

        // Act
        float[] result = embeddingService.generateEmbedding("Hello world");

        // Assert
        assertNotNull(result);

        //verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    //@Test
    void testGenerateEmbedding1() throws Exception {
        // Arrange: simulate empty embedding list
        String fakeResponse = "{ \"data\": [ { \"embedding\": [] } ] }";

        //when(httpResponse.body()).thenReturn(fakeResponse);
        //when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                //.thenReturn(httpResponse);

        // Act
        float[] result = embeddingService.generateEmbedding("Empty test");

        // Assert
        assertNotNull(result);
        assertEquals(4096, result.length);
    }
}