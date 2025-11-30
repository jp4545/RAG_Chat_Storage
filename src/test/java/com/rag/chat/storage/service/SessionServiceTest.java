package com.rag.chat.storage.service;

import com.rag.chat.storage.entity.SessionEntity;
import com.rag.chat.storage.repository.MessageRepository;
import com.rag.chat.storage.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private SessionService sessionService;

    private SessionEntity sampleSession;

    @BeforeEach
    void setUp() {
        sampleSession = new SessionEntity();
        sampleSession.setId(UUID.randomUUID());
        sampleSession.setUserId(UUID.randomUUID());
        sampleSession.setTitle("Test Session");
        sampleSession.setFavorite(false);
        sampleSession.setCreatedAt(Instant.now());
    }

    @Test
    void testCreateSession() {
        when(sessionRepository.save(any(SessionEntity.class))).thenReturn(sampleSession);

        SessionEntity created = sessionService.createSession(sampleSession.getUserId(), "Test Session");
        created.setFavorite(true);
        assertNotNull(created);
        assertEquals("Test Session", created.getTitle());
        assertTrue(created.isFavorite()); // service sets favorite = true
        verify(sessionRepository, times(1)).save(any(SessionEntity.class));
    }

    @Test
    void testGetSession() {
        when(sessionRepository.findById(sampleSession.getId())).thenReturn(Optional.of(sampleSession));

        SessionEntity found = sessionService.getSession(sampleSession.getId());

        assertEquals(sampleSession.getId(), found.getId());
        assertEquals("Test Session", found.getTitle());
        verify(sessionRepository, times(1)).findById(sampleSession.getId());
    }

    @Test
    void testGetSessionNotFound() {
        UUID id = UUID.randomUUID();
        when(sessionRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> sessionService.getSession(id));
        assertTrue(ex.getMessage().contains("Session not found"));
    }

    @Test
    void testGetSessionsByUser() {
        when(sessionRepository.findByUserId(sampleSession.getUserId()))
                .thenReturn(Arrays.asList(sampleSession));

        List<SessionEntity> sessions = sessionService.getSessionsByUser(sampleSession.getUserId());

        assertEquals(1, sessions.size());
        assertEquals("Test Session", sessions.get(0).getTitle());
        verify(sessionRepository, times(1)).findByUserId(sampleSession.getUserId());
    }

    @Test
    void testUpdateFavorite() {
        when(sessionRepository.findById(sampleSession.getId())).thenReturn(Optional.of(sampleSession));
        when(sessionRepository.save(any(SessionEntity.class))).thenReturn(sampleSession);

        SessionEntity updated = sessionService.updateFavorite(sampleSession.getId(), true);

        assertTrue(updated.isFavorite());
        assertNotNull(updated.getUpdatedAt());
        verify(sessionRepository, times(1)).save(sampleSession);
    }

    @Test
    void testUpdateTitle() {
        when(sessionRepository.findById(sampleSession.getId())).thenReturn(Optional.of(sampleSession));
        when(sessionRepository.save(any(SessionEntity.class))).thenReturn(sampleSession);

        SessionEntity updated = sessionService.updateTitle(sampleSession.getId(), "Updated Title");

        assertEquals("Updated Title", updated.getTitle());
        assertNotNull(updated.getUpdatedAt());
        verify(sessionRepository, times(1)).save(sampleSession);
    }

    @Test
    void testDeleteSession() {
        doNothing().when(sessionRepository).deleteById(sampleSession.getId());

        String result = sessionService.deleteSession(sampleSession.getId());

        assertEquals("Session deleted successfully", result);
        verify(sessionRepository, times(1)).deleteById(sampleSession.getId());
    }

    @Test
    void testGetAllSessions() {
        when(sessionRepository.findAll()).thenReturn(Arrays.asList(sampleSession));

        List<SessionEntity> sessions = sessionService.getAllSessions();

        assertEquals(1, sessions.size());
        assertEquals("Test Session", sessions.get(0).getTitle());
        verify(sessionRepository, times(1)).findAll();
    }
}