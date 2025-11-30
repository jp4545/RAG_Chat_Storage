package com.rag.chat.storage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SessionEntity session;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(nullable = false)
    private String sender;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
