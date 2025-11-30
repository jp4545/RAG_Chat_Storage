package com.rag.chat.storage.controller;

import com.rag.chat.storage.dto.ChatRequest;
import com.rag.chat.storage.entity.MessageEntity;
import com.rag.chat.storage.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "Start texting with AI",
            description = "Ask your queries with AI"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hi User"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public String chatWithAssistant(@RequestBody ChatRequest req) throws Exception {
        String id = req.sessionId();
        UUID sessionId = UUID.fromString(id);
        return chatService.sendMessage(sessionId, req.message());
    }

    @Operation(
            summary = "Fetch chat history",
            description = "Fetch chat history"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chat history fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @GetMapping("/{id}")
    public List<MessageEntity> getMessageHistory(@PathVariable UUID id) {
        return chatService.getChatHistory(id);
    }
}
