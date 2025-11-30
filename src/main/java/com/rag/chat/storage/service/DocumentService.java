package com.rag.chat.storage.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DocumentService {

    private final JdbcTemplate jdbcTemplate;

    public DocumentService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveDocument(String title, String content, float[] embedding) {
        String sql = "INSERT INTO documents (id, title, content, embedding) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                UUID.randomUUID(),
                title,
                content,
                embedding
        );
    }
}
