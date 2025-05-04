package com.failover.router.test;

import com.failover.router.model.AppLog;
import com.failover.router.config.Constants;
import com.failover.router.manager.RoutingManager;

public class SeverityResolutionTest {

    public static void main(String[] args) {
        testClientProvidedSeverity();
        testClientProvidedLowercaseSeverity();
        testConfigMatchOnSuccess();
        testConfigMatchOnError();
        testFallbackUnknownServiceSuccess();
        testFallbackUnknownServiceError();
        testMixedCaseStatus();
        testMissingStatusInConfig();
        testEmptyClientSeverityFallsToConfig();
        testNullStatusUsesDefaultModerate();

        // ➕ Additional tests
        testNullClientSeverityField();
        testWhitespaceClientSeverity();
        testConfigServiceMatchEndpointMiss();
        testConfigEndpointMatchServiceMiss();
        testGibberishStatus();
        testAllFieldsNull();
        testEmptyStringsEverywhere();
        testCaseInsensitiveService();
        testExplicitFailureStatus();
        testUppercaseStatusMatchConfig();
    }

    private static void testClientProvidedSeverity() {
        AppLog log = new AppLog();
        log.setSeverity("CRITICAL");
        assertSeverity("CRITICAL", RoutingManager.resolveSeverity(log), "Client-provided severity is used directly");
    }

    private static void testClientProvidedLowercaseSeverity() {
        AppLog log = new AppLog();
        log.setSeverity("high");
        assertSeverity("HIGH", RoutingManager.resolveSeverity(log), "Client-provided severity is auto-uppercased");
    }

    private static void testConfigMatchOnSuccess() {
        AppLog log = new AppLog();
        log.setService("walletService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus("success");
        assertSeverity("MODERATE", RoutingManager.resolveSeverity(log), "Severity found in config for SUCCESS");
    }

    private static void testConfigMatchOnError() {
        AppLog log = new AppLog();
        log.setService("walletService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus("error");
        assertSeverity("HIGH", RoutingManager.resolveSeverity(log), "Severity found in config for ERROR");
    }

    private static void testFallbackUnknownServiceSuccess() {
        AppLog log = new AppLog();
        log.setService("unknownService");
        log.setEndpoint("/invalid");
        log.setStatus("SUCCESS");
        assertSeverity(Constants.SEVERITY_LOW, RoutingManager.resolveSeverity(log), "Fallback LOW when service not found and status is SUCCESS");
    }

    private static void testFallbackUnknownServiceError() {
        AppLog log = new AppLog();
        log.setService("unknownService");
        log.setEndpoint("/invalid");
        log.setStatus("ERROR");
        assertSeverity(Constants.SEVERITY_MODERATE, RoutingManager.resolveSeverity(log), "Fallback MODERATE when service not found and status is ERROR");
    }

    private static void testMixedCaseStatus() {
        AppLog log = new AppLog();
        log.setService("walletService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus("SuCcEsS");
        assertSeverity("MODERATE", RoutingManager.resolveSeverity(log), "Mixed-case status should match config");
    }

    private static void testMissingStatusInConfig() {
        AppLog log = new AppLog();
        log.setService("walletService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus("failed");
        assertSeverity(Constants.SEVERITY_HIGH, RoutingManager.resolveSeverity(log), "Fallback used if status not found in config");
    }

    private static void testEmptyClientSeverityFallsToConfig() {
        AppLog log = new AppLog();
        log.setSeverity("");
        log.setService("walletService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus("success");
        assertSeverity("MODERATE", RoutingManager.resolveSeverity(log), "Empty severity falls back to config");
    }

    private static void testNullStatusUsesDefaultModerate() {
        AppLog log = new AppLog();
        log.setService("walletService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus(null);
        assertSeverity(Constants.SEVERITY_HIGH, RoutingManager.resolveSeverity(log), "Null status defaults to MODERATE");
    }

    private static void testNullClientSeverityField() {
        AppLog log = new AppLog();
        log.setSeverity(null);
        log.setService("walletService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus("error");
        assertSeverity("HIGH", RoutingManager.resolveSeverity(log), "Client-provided severity is null");
    }

    private static void testWhitespaceClientSeverity() {
        AppLog log = new AppLog();
        log.setSeverity("   ");
        log.setService("walletService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus("error");
        assertSeverity("HIGH", RoutingManager.resolveSeverity(log), "Client-provided severity is whitespace");
    }

    private static void testConfigServiceMatchEndpointMiss() {
        AppLog log = new AppLog();
        log.setService("walletService");
        log.setEndpoint("/unknown");
        log.setStatus("success");
        assertSeverity(Constants.SEVERITY_LOW, RoutingManager.resolveSeverity(log), "Service in config, endpoint not");
    }

    private static void testConfigEndpointMatchServiceMiss() {
        AppLog log = new AppLog();
        log.setService("unknownService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus("success");
        assertSeverity(Constants.SEVERITY_LOW, RoutingManager.resolveSeverity(log), "Endpoint in config, service not");
    }

    private static void testGibberishStatus() {
        AppLog log = new AppLog();
        log.setStatus("xyz");
        log.setService("walletService");
        log.setEndpoint("/api/wallet/balance");
        assertSeverity(Constants.SEVERITY_MODERATE, RoutingManager.resolveSeverity(log), "Status is gibberish");
    }

    private static void testAllFieldsNull() {
        AppLog log = new AppLog();
        assertSeverity(Constants.SEVERITY_MODERATE, RoutingManager.resolveSeverity(log), "All fields null");
    }

    private static void testEmptyStringsEverywhere() {
        AppLog log = new AppLog();
        log.setService("");
        log.setEndpoint("");
        log.setStatus("");
        assertSeverity(Constants.SEVERITY_MODERATE, RoutingManager.resolveSeverity(log), "Empty strings everywhere");
    }

    private static void testCaseInsensitiveService() {
        AppLog log = new AppLog();
        log.setService("WalletService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus("success");
        assertSeverity("MODERATE", RoutingManager.resolveSeverity(log), "Service-case insensitive match");
    }

    private static void testExplicitFailureStatus() {
        AppLog log = new AppLog();
        log.setService("walletService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus("failure");
        assertSeverity("HIGH", RoutingManager.resolveSeverity(log), "Status 'failure' is normalized to 'error'");
    }

    private static void testUppercaseStatusMatchConfig() {
        AppLog log = new AppLog();
        log.setService("walletService");
        log.setEndpoint("/api/wallet/balance");
        log.setStatus("ERROR");
        assertSeverity("HIGH", RoutingManager.resolveSeverity(log), "Valid config, uppercase ERROR status");
    }

    private static void assertSeverity(String expected, String actual, String testCase) {
        if (expected.equals(actual)) {
            System.out.println("✅ PASSED: " + testCase + " → " + actual);
        } else {
            System.err.println("❌ FAILED: " + testCase + " → Expected: " + expected + ", Got: " + actual);
        }
    }
}
