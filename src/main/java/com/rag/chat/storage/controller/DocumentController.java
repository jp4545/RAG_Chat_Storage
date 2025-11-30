package com.rag.chat.storage.controller;

import com.rag.chat.storage.dto.FileUploadRequest;
import com.rag.chat.storage.service.DocumentReaderService;
import com.rag.chat.storage.service.DocumentService;
import com.rag.chat.storage.service.EmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DocumentController {

    @Autowired
    private final DocumentService documentService;
    @Autowired
    private final EmbeddingService embeddingService;
    @Autowired
    private final DocumentReaderService readerService;

    public DocumentController(DocumentService documentService,
                              EmbeddingService embeddingService,
                              DocumentReaderService readerService) {
        this.documentService = documentService;
        this.embeddingService = embeddingService;
        this.readerService = readerService;
    }

    @Operation(
            summary = "Upload Embedding to the model",
            description = "Upload your embeddings to the model"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document upload success"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/upload-file")
    public String uploadFile(@RequestBody FileUploadRequest req) throws Exception {
        // Convert file to plain text
        String content = readerService.extractText(req.title());

        // Generate embeddings
        float[] embedding = embeddingService.generateEmbedding(content);

        // Store in PostgreSQL
        documentService.saveDocument(req.title(), content, embedding);

        return "File uploaded and embeddings saved successfully!";
    }
}
