package com.rag.chat.storage.controller;

import com.rag.chat.storage.dto.CreateSessionRequest;
import com.rag.chat.storage.dto.UpdateFavouriteSession;
import com.rag.chat.storage.dto.UpdateSessionTitle;
import com.rag.chat.storage.entity.SessionEntity;
import com.rag.chat.storage.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/session")
@Tag(name = "Sessions", description = "Manage chat sessions")
public class SessionController {
   @Autowired
   private SessionService sessionService;

    @Operation(
            summary = "Create a new session",
            description = "Creates a new session for a user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public SessionEntity createSession(@RequestBody CreateSessionRequest req) {

        SessionEntity sessionEntity = sessionService.createSession(UUID.fromString(req.userId()), req.title());
        return sessionEntity;
    }

    @Operation(
            summary = "Get a session by ID",
            description = "Fetches a session based on the provided session ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid session ID format"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @GetMapping("/{id}")
    public SessionEntity getSessionById(@PathVariable UUID id) {
        return sessionService.getSession(id);
    }

    @Operation(
            summary = "Get all sessions",
            description = "Fetches ass sessions"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully")
    })
    @GetMapping
    public List<SessionEntity> getAllSessions() {
        return sessionService.getAllSessions();
    }
    @Operation(
            summary = "Get list of session by User ID",
            description = "Fetches list of sessions based on the provided user ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format"),
            @ApiResponse(responseCode = "404", description = "User ID not found")
    })
    @GetMapping("user/{id}")
    public List<SessionEntity> getSessionByUserId(@PathVariable UUID id) {
        return sessionService.getSessionsByUser(id);
    }

    @Operation(
            summary = "Update session as favourite based on session ID",
            description = "Update session as favourite"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session marked as favourite successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/updateFavourite")
    public SessionEntity updateFavourite(@RequestBody UpdateFavouriteSession req) {
        String sessionId = req.sessionId();
        UUID id = UUID.fromString(sessionId);
        return sessionService.updateFavorite(id, "Y".equals(req.isFavourite()) ? true : false);
    }

    @Operation(
            summary = "Update session title based on session ID",
            description = "Update session title"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session title updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/updateTitle")
    public SessionEntity updateTitle(@RequestBody UpdateSessionTitle req) {
        String sessionId = req.sessionId();
        UUID id = UUID.fromString(sessionId);
        return sessionService.updateTitle(id, req.title());
    }

    @Operation(
            summary = "Delete session by session ID",
            description = "Delete session by SessionId"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessions deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid session ID format"),
            @ApiResponse(responseCode = "404", description = "session ID not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSession(UUID id) {
        String response = sessionService.deleteSession(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Fetch favourite session for a user",
            description = "Fetch favourite session for a user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessions fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format"),
            @ApiResponse(responseCode = "404", description = "user ID not found")
    })
    @GetMapping("/favourite")
    public List<SessionEntity> getFavouriteSessions(@PathVariable UUID id) {
        return sessionService.getFavouriteSessions(id);
    }
}
