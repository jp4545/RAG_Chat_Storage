package com.rag.chat.storage.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddingResponse {

    private String object;
    private List<DataItem> data;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<DataItem> getData() {
        return data;
    }

    public void setData(List<DataItem> data) {
        this.data = data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataItem {
        private String object;
        private List<Float> embedding;

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public List<Float> getEmbedding() {
            return embedding;
        }

        public void setEmbedding(List<Float> embedding) {
            this.embedding = embedding;
        }
    }
}