package com.rag.chat.storage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.chat.storage.pojo.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {
    @Value("${app.llm.base-url}")
    private String llamaBaseUrl;

    @Value("${app.llm.embedding-path}")
    private String llmEmbeddingPath;

    private String getLlamaChatEndpoint() {
        // Combine base URL + chat path
        return llamaBaseUrl + llmEmbeddingPath;
    }

    public float[] generateEmbedding(String text) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        Map<String, Object> payload = new HashMap<>();
        payload.put("model","mistral");
        payload.put("input",text);

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getLlamaChatEndpoint()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        EmbeddingResponse embeddingResponse = mapper.readValue(response.body(), EmbeddingResponse.class);

        List<Float> list = embeddingResponse.getData().get(0).getEmbedding();

        float[] floatArray = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            floatArray[i] = list.get(i).floatValue();
        }

        return floatArray;
    }
}
