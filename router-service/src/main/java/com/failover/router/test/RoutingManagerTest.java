package com.failover.router.test;

import com.failover.router.model.AppLog;
import com.failover.router.manager.RoutingManager;

import java.util.HashMap;
import java.util.Map;

public class RoutingManagerTest {
    public static void main(String[] args) {
        testPostDefaultUpsert();
        testPostExplicitFetch();
        testGetDefaultFetch();
        testErrorStatusFallbackSeverity();
        testClientProvidedSeverity();
    }

    private static void testPostDefaultUpsert() {
        AppLog log = new AppLog();
        log.setMethod("POST");
        log.setStatus("SUCCESS");
        log.setPayload(new HashMap<>());

        assertTopicAndSeverity("Test 1: POST defaults to UPSERT",
                log, "DATA_UPSERT_LOG_SUCCESS", "LOW");
    }

    private static void testPostExplicitFetch() {
        AppLog log = new AppLog();
        log.setMethod("POST");
        log.setStatus("SUCCESS");

        Map<String, Object> payload = new HashMap<>();
        payload.put("operation", "fetch"); // explicit override
        log.setPayload(payload);

        assertTopicAndSeverity("Test 2: POST with fetch override",
                log, "DATA_FETCH_LOG_SUCCESS", "LOW");
    }

    private static void testGetDefaultFetch() {
        AppLog log = new AppLog();
        log.setMethod("GET");
        log.setStatus("SUCCESS");
        log.setPayload(new HashMap<>());

        assertTopicAndSeverity("Test 3: GET always fetch",
                log, "DATA_FETCH_LOG_SUCCESS", "LOW");
    }

    private static void testErrorStatusFallbackSeverity() {
        AppLog log = new AppLog();
        log.setMethod("GET");
        log.setStatus("ERROR");
        log.setPayload(new HashMap<>());

        assertTopicAndSeverity("Test 4: ERROR fallback severity",
                log, "DATA_FETCH_LOG_ERROR", "MODERATE");
    }

    private static void testClientProvidedSeverity() {
        AppLog log = new AppLog();
        log.setMethod("GET");
        log.setStatus("ERROR");
        log.setPayload(new HashMap<>());
        log.setSeverity("CRITICAL");

        assertTopicAndSeverity("Test 5: Client-provided severity",
                log, "DATA_FETCH_LOG_ERROR", "CRITICAL");
    }

    private static void assertTopicAndSeverity(String testName, AppLog log, String expectedTopic, String expectedSeverity) {
        String actualTopic = RoutingManager.resolveTopic(log);
        String actualSeverity = RoutingManager.resolveSeverity(log);

        System.out.println("👉 " + testName);
        System.out.println("   Resolved Topic:    " + actualTopic + " | Expected: " + expectedTopic);
        System.out.println("   Resolved Severity: " + actualSeverity + " | Expected: " + expectedSeverity);
        System.out.println(actualTopic.equals(expectedTopic) && actualSeverity.equals(expectedSeverity)
                ? "   ✅ Test Passed\n"
                : "   ❌ Test Failed\n");
    }
}
