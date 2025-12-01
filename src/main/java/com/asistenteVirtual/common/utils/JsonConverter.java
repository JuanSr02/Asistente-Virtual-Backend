package com.asistenteVirtual.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JsonConverter {
    private final ObjectMapper objectMapper;

    public String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando a JSON", e);
        }
    }

    public <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializando JSON", e);
        }
    }
    
    public <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializando JSON", e);
        }
    }
}