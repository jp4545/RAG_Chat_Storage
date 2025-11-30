package com.rag.chat.storage.repository;

import com.rag.chat.storage.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {

    Optional<SessionEntity> findById(UUID id);

    // Get all sessions for a user
    List<SessionEntity> findByUserId(UUID userId);

    // Optional: Get favorite sessions
    List<SessionEntity> findByUserIdAndIsFavoriteTrue(UUID userId);
}
