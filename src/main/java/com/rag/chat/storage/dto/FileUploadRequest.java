package com.rag.chat.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Request payload for updating session as favourite")
public record FileUploadRequest (

        @Schema(
        description = "Title for the embedding model",
        example = "Spring Boot Course",
        required = true
    )
    String title
) {}
