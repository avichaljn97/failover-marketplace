package com.failover.router.config;

import com.failover.router.util.LoggerUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.failover.router.config.AppConfig.*;

public class SeverityConfig {

    private static final String CONFIG_FILE = SEVERITY_CONFIG;
    private static final Map<String, String> severityMap = new HashMap<>();

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, Map<String, String>>> rawConfig =
                    mapper.readValue(new File(CONFIG_FILE), new TypeReference<>() {
                    });

            for (Map.Entry<String, Map<String, Map<String, String>>> serviceEntry : rawConfig.entrySet()) {
                String service = serviceEntry.getKey();
                for (Map.Entry<String, Map<String, String>> endpointEntry : serviceEntry.getValue().entrySet()) {
                    String endpoint = endpointEntry.getKey();
                    for (Map.Entry<String, String> statusEntry : endpointEntry.getValue().entrySet()) {
                        String status = statusEntry.getKey();
                        String severity = statusEntry.getValue();
                        String key = buildKey(service, endpoint, status);
                        severityMap.put(key, severity);
                    }
                }
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to load severity-config.json: " + e.getMessage());
            throw new RuntimeException("Cannot start application without severity-config.json", e);
        }

    }

    public static String getSeverity(String service, String endpoint, String status) {
        return severityMap.getOrDefault(buildKey(service, endpoint, status), "MODERATE");
    }

    private static String buildKey(String service, String endpoint, String status) {
        return service + "|" + endpoint + "|" + status;
    }
}
