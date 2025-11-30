package com.rag.chat.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Create User")
public record CreateUserRequest(

        @Schema(
                description = "User name of the user",
                example = "Test User",
                required = true
        )
        String name
) {}