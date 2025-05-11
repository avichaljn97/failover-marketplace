package com.failover.router.manager;

import com.failover.router.config.SeverityConfig;
import com.failover.router.model.AppLog;
import com.failover.router.config.Constants;
import com.failover.router.util.LoggerUtil;

import java.util.Map;

public class RoutingManager {

    public static String resolveTopic(AppLog log) {
        try {
            String status = log.getStatus() != null ? log.getStatus().toUpperCase() : "UNKNOWN";
            String method = log.getMethod() != null ? log.getMethod().toUpperCase() : "UNKNOWN";
            Map<String, Object> payload = log.getPayload();

            boolean isFetch = false;

            if ("GET".equals(method)) {
                isFetch = true;
            } else if ("POST".equals(method)) {
                if (payload != null) {
                    Object operation = payload.get("operation");
                    if (operation != null && Constants.FETCH_OPERATION.equalsIgnoreCase(operation.toString())) {
                        isFetch = true;
                    }
                } else {
                    LoggerUtil.logInfo("No payload in POST request; defaulting to UPSERT");
                }
            }

            String prefix = isFetch ? Constants.FETCH_PREFIX : Constants.UPSERT_PREFIX;
            return prefix + status;

        } catch (Exception e) {
            LoggerUtil.logError("Exception while resolving Kafka topic", e);
            throw e; // rethrow so caller can handle it properly
        }
    }



    public static String normalizeStatus(String status) {
        if (status == null) return "error";

        String s = status.trim().toLowerCase();

        // Normalize common variants
        if (s.equals("success")) return "success";
        if (s.equals("error") || s.equals("failed") || s.equals("failure")) return "error";

        return "error"; // treat all unknowns as error-like
    }


    public static String resolveSeverity(AppLog log) {
        // Use client-provided severity if present
        if (log.getSeverity() != null && !log.getSeverity().isEmpty()) {
            LoggerUtil.logInfo("Using client-provided severity: " + log.getSeverity());
            return log.getSeverity().toUpperCase();
        }

        String normalizedStatus = normalizeStatus(log.getStatus());

        // Try to resolve from config
        LoggerUtil.logInfo("No client severity, checking config for: "
                + log.getService() + " " + log.getEndpoint() + " " + normalizedStatus);

        String fromConfig = SeverityConfig.getSeverity(
                log.getService(),
                log.getEndpoint(),
                normalizedStatus.toLowerCase()
        );

        // If config result found, use it
        if (fromConfig != null && !fromConfig.isEmpty()) {
            LoggerUtil.logInfo("Resolved severity from config: " + fromConfig);
            return fromConfig.toUpperCase();
        }

        // Else fallback
        String fallback = "success".equalsIgnoreCase(normalizedStatus)
                ? Constants.SEVERITY_LOW
                : Constants.SEVERITY_MODERATE;

        LoggerUtil.logInfo("No config match. Using fallback severity: " + fallback);
        return fallback;
    }
}
