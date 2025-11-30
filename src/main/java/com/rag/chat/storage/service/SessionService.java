package com.rag.chat.storage.service;

import com.rag.chat.storage.entity.SessionEntity;
import com.rag.chat.storage.repository.MessageRepository;
import com.rag.chat.storage.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;

    // Create a new session
    public SessionEntity createSession(UUID userId, String title) {
        SessionEntity session = new SessionEntity();
        session.setUserId(userId);
        session.setTitle(title);
        session.setFavorite(true);

        return sessionRepository.save(session);
    }

    // Get session by ID
    public SessionEntity getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
    }

    // Get all sessions for a user
    public List<SessionEntity> getSessionsByUser(UUID userId) {
        return sessionRepository.findByUserId(userId);
    }

    // Update favorite flag
    public SessionEntity updateFavorite(UUID sessionId, boolean isFavorite) {
        SessionEntity session = getSession(sessionId);
        session.setFavorite(isFavorite);
        session.setUpdatedAt(Instant.now());
        return sessionRepository.save(session);
    }

    // Update title
    public SessionEntity updateTitle(UUID sessionId, String title) {
        SessionEntity session = getSession(sessionId);
        session.setTitle(title);
        session.setUpdatedAt(Instant.now());
        return sessionRepository.save(session);
    }

    public String deleteSession(UUID sessionId) {
        sessionRepository.deleteById(sessionId);
        return "Session deleted successfully";
    }

    public List<SessionEntity> getAllSessions() {
        return sessionRepository.findAll();
    }

    public List<SessionEntity> getFavouriteSessions(UUID id) {
        return sessionRepository.findByUserIdAndIsFavoriteTrue(id);
    }
}
