package com.failover.router.receiver;

import com.failover.router.manager.RoutingManager;
import com.failover.router.model.AppLog;
import com.failover.router.producer.KafkaLogDispatcher;
import com.failover.router.config.SeverityConfig;
import com.failover.router.util.LoggerUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class LogReceiver {

    private static final int PORT = 8080;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void start() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/log", new LogHandler());
        server.setExecutor(null);
        server.start();
        LoggerUtil.logInfo("LogReceiver HTTP server started on port " + PORT);
    }

    static class LogHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (!"POST".equals(exchange.getRequestMethod())) {
                    sendJsonResponse(exchange, 405, false, "Only POST method is supported.");
                    return;
                }

                InputStream requestBody = exchange.getRequestBody();
                AppLog appLog = objectMapper.readValue(requestBody, AppLog.class);
                LoggerUtil.logInfo("Received AppLog: " + appLog.stringify());
                // Validate required fields (minimal)
                if (appLog.getService() == null || appLog.getEndpoint() == null || appLog.getStatus() == null) {
                    sendJsonResponse(exchange, 400, false, "Missing required fields in request.");
                    return;
                }

                // Determine severity based on config
                String resolvedSeverity = RoutingManager.resolveSeverity(appLog);
                appLog.setSeverity(resolvedSeverity);

                // Determine Kafka topic
                String topic = KafkaLogDispatcher.determineTopic(appLog);

                // Dispatch to Kafka
                KafkaLogDispatcher.sendToKafka(topic, appLog);

                // Return success
                sendJsonResponse(exchange, 200, true, "Log received and dispatched to Kafka.");

            } catch (Throwable t) {
                t.printStackTrace();
                try {
                    sendJsonResponse(exchange, 500, false, "Internal Server Error. Please try again later.");
                } catch (Exception ignored) {}
            }
        }

        private void sendJsonResponse(HttpExchange exchange, int statusCode, boolean success, String message) throws Exception {
            String responseJson = String.format(
                    "{\"success\": %s, \"message\": \"%s\"}",
                    success,
                    message.replace("\"", "'")
            );
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            byte[] responseBytes = responseJson.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}
