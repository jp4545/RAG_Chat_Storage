package com.rag.chat.storage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.chat.storage.entity.MessageEntity;
import com.rag.chat.storage.entity.SessionEntity;
import com.rag.chat.storage.entity.UserEntity;
import com.rag.chat.storage.repository.MessageRepository;
import com.rag.chat.storage.repository.SessionRepository;
import com.rag.chat.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.llm.base-url}")
    private String llamaBaseUrl;

    @Value("${app.llm.chat-path}")
    private String llamaChatPath;

    private String getLlamaChatEndpoint() {
        // Combine base URL + chat path
        return llamaBaseUrl + llamaChatPath;
    }

    @Transactional
    public String sendMessage(UUID sessionId, String userMessage) throws Exception {

        UUID userId = null;
        Optional<SessionEntity> sessionEntity = sessionRepository.findById(sessionId);
        if(sessionEntity != null && sessionEntity.get().getUserId() != null) {
            userId = sessionEntity.get().getUserId();;
        }

        Optional<UserEntity> userEntity = userRepository.findById(userId);
        // 1️⃣ Save user message
        MessageEntity userMsg = new MessageEntity();
        userMsg.setSessionId(sessionId);
        userMsg.setSender(userEntity!= null && userEntity.get().getName() != null ? userEntity.get().getName() : "user");
        userMsg.setContent(userMessage);
        messageRepository.save(userMsg);

        // 2️⃣ Get chat history
        List<MessageEntity> history = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);

        // 3️⃣ Build LLaMA prompt
        StringBuilder promptBuilder = new StringBuilder();
        for (MessageEntity m : history) {
            promptBuilder.append(m.getSender()).append(": ").append(m.getContent()).append("\n");
        }
        promptBuilder.append("assistant: "); // model should reply after this

        String prompt = promptBuilder.toString().replace("\"", "\\\"");

        Map<String, Object> payload = new HashMap<>();
        payload.put("model","mistral");
        payload.put("prompt",prompt);
        payload.put("max_tokens", 300);

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(payload);

        // 4️⃣ Call LLaMA API
        //String requestBody = "{ \"model\": \"mistral\", \"prompt\": \"" + prompt + "\", \"max_tokens\": 300 }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getLlamaChatEndpoint()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // 5️⃣ Parse LLaMA response
        JsonNode jsonNode = objectMapper.readTree(response.body());
        JsonNode jsonNchoices = jsonNode.path("choices"); // adjust if your server returns different field
        String assistantReply = "";
        if(jsonNchoices.isArray() && jsonNchoices.size() > 0) {
            assistantReply = jsonNchoices.get(0).path("text").asText();
        }
        // 6️⃣ Save assistant message
        MessageEntity assistantMsg = new MessageEntity();
        assistantMsg.setSessionId(sessionId);
        assistantMsg.setSender("assistant");
        assistantMsg.setContent(assistantReply);
        messageRepository.save(assistantMsg);

        return assistantReply;
    }

    public List<MessageEntity> getChatHistory(UUID sessionId) {
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

}
