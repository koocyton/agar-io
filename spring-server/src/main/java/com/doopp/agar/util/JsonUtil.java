package com.doopp.agar.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    private ObjectMapper objectMapper;

    public JsonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] toJsonBytes(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public <T> T toObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T toObject(String json, TypeReference typeReference) {
        try {
            return (T) objectMapper.readValue(json, typeReference);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public <T> T toList(String json, Class<T> clazz) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, Object> toMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String findValue(String resData, String resPro) {
        try {
            JsonNode node = objectMapper.readTree(resData);
            JsonNode resProNode = node.get(resPro);
            return this.toJsonString(resProNode);
        } catch (IOException e) {
            return null;
        }
    }
}
