package com.failover.router.test;

import com.failover.router.model.AppLog;
import com.failover.router.producer.KafkaLogDispatcher;

import java.util.HashMap;
import java.util.Map;

public class KafkaLogDispatcherTest {
    public static void main(String[] args) {
        runTest("Test 1: POST (default UPSERT)", buildLog("POST", "SUCCESS", null),
                "DATA_UPSERT_LOG_SUCCESS");

        runTest("Test 2: POST + operation=fetch (explicit FETCH)", buildLog("POST", "SUCCESS", "fetch"),
                "DATA_FETCH_LOG_SUCCESS");

        runTest("Test 3: GET (default FETCH)", buildLog("GET", "SUCCESS", null),
                "DATA_FETCH_LOG_SUCCESS");

        runTest("Test 4: PUT (default UPSERT)", buildLog("PUT", "SUCCESS", null),
                "DATA_UPSERT_LOG_SUCCESS");

        runTest("Test 5: POST + operation=fetch + status=ERROR", buildLog("POST", "ERROR", "fetch"),
                "DATA_FETCH_LOG_ERROR");
    }

    private static AppLog buildLog(String method, String status, String operation) {
        AppLog log = new AppLog();
        log.setMethod(method);
        log.setStatus(status);
        Map<String, Object> payload = new HashMap<>();
        if (operation != null) {
            payload.put("operation", operation);
        }
        log.setPayload(payload);
        return log;
    }

    private static void runTest(String label, AppLog log, String expectedTopic) {
        String actualTopic = KafkaLogDispatcher.determineTopic(log);

        System.out.println("👉 " + label);
        System.out.println("   Method: " + log.getMethod() + ", Status: " + log.getStatus() +
                ", Payload: " + log.getPayload());
        System.out.println("   Expected Topic: " + expectedTopic);
        System.out.println("   Actual Topic:   " + actualTopic);
        System.out.println(expectedTopic.equals(actualTopic)
                ? "   ✅ Test Passed\n"
                : "   ❌ Test Failed\n");
    }
}
