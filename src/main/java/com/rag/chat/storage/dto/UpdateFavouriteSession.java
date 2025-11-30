package com.rag.chat.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Request payload for updating session as favourite")
public record UpdateFavouriteSession (

        @Schema(
                description = "Session If of that particular session",
                example = "26736097-85bf-4814-9a3b-b33d82c061d3",
                required = true
        )
        String sessionId,

        @Schema(
                description = "Is this session your favourite",
                example = "Y /N",
                required = true
        )
        String isFavourite
) {}
