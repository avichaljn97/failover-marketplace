package com.failover.router.receiver;

import com.failover.router.model.AppLog;
import com.failover.router.producer.KafkaLogDispatcher;
import com.failover.router.config.SeverityConfig;
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
        System.out.println("LogReceiver HTTP server started on port " + PORT);
    }

    static class LogHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (!"POST".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                InputStream requestBody = exchange.getRequestBody();
                AppLog appLog = objectMapper.readValue(requestBody, AppLog.class);

                // Validate required fields (minimal)
                if (appLog.getService() == null || appLog.getEndpoint() == null || appLog.getStatus() == null) {
                    String response = "Missing required fields";
                    exchange.sendResponseHeaders(400, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                    return;
                }

                // Determine severity based on config
                String severity = SeverityConfig.getSeverity(appLog.getService(), appLog.getEndpoint(), appLog.getStatus());
                appLog.setSeverity(severity);

                // Determine Kafka topic
                String topic = KafkaLogDispatcher.determineTopic(appLog);

                // Dispatch to Kafka
                KafkaLogDispatcher.sendToKafka(topic, appLog);

                // Return success
                String response = "Log received and dispatched to Kafka.";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    String response = "Internal Server Error";
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                } catch (Exception ignored) {}
            }
        }
    }
}
