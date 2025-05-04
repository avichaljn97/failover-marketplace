package com.failover.router.mysql;

import com.failover.router.model.AppLog;
import com.failover.router.util.LoggerUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import static com.failover.router.config.Constants.*;

public class MySQLLogWriter {

    private static final String URL = MYSQL_URL;
    private static final String USER = MYSQL_USER;
    private static final String PASSWORD = MYSQL_PASSWORD;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void writeToMySQL(AppLog log) {
        String sql = "INSERT INTO "
                +MYSQL_TABLE+
                " (user_id, service, endpoint, status, method, severity, payload, response, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, log.getUserId());
            stmt.setString(2, log.getService());
            stmt.setString(3, log.getEndpoint());
            stmt.setString(4, log.getStatus());
            stmt.setString(5, log.getMethod());
            stmt.setString(6, log.getSeverity());
            stmt.setString(7, mapper.writeValueAsString(log.getPayload()));
            stmt.setString(8, mapper.writeValueAsString(log.getResponse()));
            stmt.setString(9, log.getTimestamp());

            stmt.executeUpdate();
            LoggerUtil.logInfo("✅ Log stored in MySQL");

        } catch (Exception e) {
            LoggerUtil.logError("❌ Failed to write log to MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
