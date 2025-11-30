package com.rag.chat.storage.service;

import org.apache.tika.Tika;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
@Service
public class DocumentReaderService {
    private final Tika tika = new Tika();

    // Read from MultipartFile
    public String extractText(String fileName) throws Exception {
        ClassPathResource resource = new ClassPathResource(fileName);
        InputStream inputStream = resource.getInputStream();
        return tika.parseToString(inputStream);
    }
}
