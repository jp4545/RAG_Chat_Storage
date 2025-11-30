package com.rag.chat.storage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DocumentService documentService;

    private String title;
    private String content;
    private float[] embedding;

    @BeforeEach
    void setUp() {
        title = "Test Document";
        content = "This is some test content";
        embedding = new float[]{0.1f, 0.2f, 0.3f};
    }

    @Test
    void testSaveDocument() {
        documentService.saveDocument(title, content, embedding);

        verify(jdbcTemplate, times(1)).update(
                eq("INSERT INTO documents (id, title, content, embedding) VALUES (?, ?, ?, ?)"),
                any(UUID.class),
                eq(title),
                eq(content),
                eq(embedding)
        );
    }
}