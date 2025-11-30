package com.rag.chat.storage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String api_key_hash;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
