package com.rag.chat.storage.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentReaderServiceTest {
    private final DocumentReaderService documentReaderService = new DocumentReaderService();

    @Test
    void testExtractTextFromFile() throws Exception {
        // Arrange: put a file named "sample.txt" in src/test/resources with some text content
        String fileName = "sample.txt";

        // Act
        String content = documentReaderService.extractText(fileName);

        // Assert
        assertNotNull(content);
        assertTrue(content.contains("Hello World!")); // adjust to match content of sample.txt
    }

    @Test
    void testExtractTextFileNotFound() {
        Exception ex = assertThrows(Exception.class,
                () -> documentReaderService.extractText("nonexistent.txt"));

        assertTrue(ex.getMessage().contains("nonexistent.txt"));
    }
}
