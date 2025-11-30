package com.rag.chat.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for updating session title")
public record UpdateSessionTitle(

        @Schema(
                description = "Session If of that particular session",
                example = "26736097-85bf-4814-9a3b-b33d82c061d3",
                required = true
        )
        String sessionId,

        @Schema(
                description = "Updated session title",
                example = "New Updated session",
                required = true
        )
        String title
) {}
