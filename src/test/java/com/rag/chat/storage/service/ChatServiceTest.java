package com.rag.chat.storage.service;

import com.rag.chat.storage.entity.MessageEntity;
import com.rag.chat.storage.repository.MessageRepository;
import com.rag.chat.storage.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private ChatService chatService;

    private UUID sessionId;
    private MessageEntity userMessage;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        userMessage = new MessageEntity();
        userMessage.setId(UUID.randomUUID());
        userMessage.setSessionId(sessionId);
        userMessage.setSender("user");
        userMessage.setContent("Hello AI");
        userMessage.setCreatedAt(Instant.now());
    }

    //@Test
    void testSendMessage() throws Exception {
        // Arrange: stub repository and httpClient
        when(messageRepository.save(any(MessageEntity.class))).thenReturn(userMessage);
        when(messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId))
                .thenReturn(Arrays.asList(userMessage));

        // Simulate LLaMA API response JSON
        String llamaResponse = "{ \"choices\": [ { \"text\": \"Hi User\" } ] }";
        //when(httpResponse.body()).thenReturn(llamaResponse);
        //when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        // Act
        String reply = chatService.sendMessage(sessionId, "Hello AI");

        // Assert
        assertNotNull(reply);
        //assertEquals("Hello there! How can I assist you today? Here to help with any questions or tasks you might have.", reply);
        verify(messageRepository, times(2)).save(any(MessageEntity.class)); // user + assistant
        verify(messageRepository).findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    @Test
    void testGetChatHistory() {
        when(messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId))
                .thenReturn(Arrays.asList(userMessage));

        List<MessageEntity> history = chatService.getChatHistory(sessionId);

        assertEquals(1, history.size());
        assertEquals("Hello AI", history.get(0).getContent());
        verify(messageRepository, times(1)).findBySessionIdOrderByCreatedAtAsc(sessionId);
    }
}