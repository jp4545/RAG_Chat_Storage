package com.rag.chat.storage.repository;

import com.rag.chat.storage.entity.SessionEntity;
import com.rag.chat.storage.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
