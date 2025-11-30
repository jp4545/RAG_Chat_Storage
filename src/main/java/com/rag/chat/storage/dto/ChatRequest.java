package com.rag.chat.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for chat with AI")
public record ChatRequest(

        @Schema(
                description = "Sessin ID of this session",
                example = "d2c77c9a-5aa3-4d65-9fd5-b0f2a8b93110",
                required = true
        )
        String sessionId,

        @Schema(
                description = "Your message for AI",
                example = "Helli AI, How are you",
                required = true
        )
        String message
) {}