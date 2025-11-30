//package com.rag.chat.storage.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.Instant;
//import java.util.UUID;
//
//@Entity
//@Table(name = "documents")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Document {
//    @Id
//    private UUID id;
//    private String title;
//
//    @Column(columnDefinition = "text")
//    private String content;
//
////    @Column(columnDefinition = "jsonb")
////    private String metadata;
//
////    // embedding column with pgvector support
////    @Type(type = "vector")
////    @Column(columnDefinition = "vector(1536)", nullable = false)
////    private float[] embedding;
//
//    private Instant createdAt;
//}
