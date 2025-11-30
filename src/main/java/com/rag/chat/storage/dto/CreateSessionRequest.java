package com.rag.chat.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for creating a session")
public record CreateSessionRequest(

        @Schema(
                description = "User ID of the session owner",
                example = "d2c77c9a-5aa3-4d65-9fd5-b0f2a8b93110",
                required = true
        )
        String userId,

        @Schema(
                description = "Title for the chat session",
                example = "My First Chat Session",
                required = true
        )
        String title
) {}