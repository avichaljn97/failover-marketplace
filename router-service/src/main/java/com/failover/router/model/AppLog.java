package com.failover.router.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;

public class AppLog extends BaseSQLModel {
    private String userId;
    private String service;
    private String endpoint;
    private String status;
    private String method;
    private Map<String, Object> payload;
    private Map<String, Object> response;
    private String timestamp;
    private String severity;
    private String tableName;

    private static final ObjectMapper mapper = new ObjectMapper();

    // Required by BaseSQLModel
    @Override
    public String getTableName() {
        return "applogs"; // use your actual table name
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public Map<String, Object> getPrimaryKeyFieldMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("user_id", userId);
        map.put("service", service);
        map.put("endpoint", endpoint);
        map.put("timestamp", timestamp);
        return map;
    }

    @Override
    public Map<String, Object> getFieldMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("user_id", userId);
        map.put("service", service);
        map.put("endpoint", endpoint);
        map.put("status", status);
        map.put("method", method);
        map.put("severity", severity);

        try {
            map.put("payload", mapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            map.put("payload", "{\"error\": \"failed to serialize payload\"}");
        }

        try {
            map.put("response", mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            map.put("response", "{\"error\": \"failed to serialize response\"}");
        }

        map.put("timestamp", timestamp);
        return map;
    }

    // Existing Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public Map<String, Object> getResponse() { return response; }
    public void setResponse(Map<String, Object> response) { this.response = response; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String stringify() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Failed to serialize AppLog to JSON\"}";
        }
    }
}
