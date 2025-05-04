package com.failover.router.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import com.failover.router.util.LoggerUtil;

import static com.failover.router.config.Constants.*;

public abstract class BaseSQLModel {

    public abstract String getTableName();
    public abstract Map<String, Object> getFieldMap();
    public abstract Map<String, Object> getPrimaryKeyFieldMap(); // For delete


    public boolean saveToSQLDb() {
        Map<String, Object> fields = getFieldMap();

        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(getTableName()).append(" (");

        for (String column : fields.keySet()) {
            sql.append(column).append(", ");
        }
        sql.setLength(sql.length() - 2); // Remove trailing comma
        sql.append(") VALUES (");
        sql.append("?,".repeat(fields.size()));
        sql.setLength(sql.length() - 1); // Remove trailing comma
        sql.append(")");

        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (Object value : fields.values()) {
                stmt.setObject(index++, value);
            }

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted == 0) {
                throw new SQLException("No rows inserted into table: " + getTableName());
            }

            LoggerUtil.logInfo("✅ Successfully stored in SQL DB: " + getClass().getSimpleName());
            return true;

        } catch (Exception e) {
            LoggerUtil.logError("❌ Failed to save " + getClass().getSimpleName() + " into SQL DB", e);
            throw new RuntimeException("Failed to save entity to SQL DB", e);
        }
    }

    public boolean deleteFromSQLDb() {

        Map<String, Object> primaryKeyFields = getPrimaryKeyFieldMap();

        if (primaryKeyFields.isEmpty()) {
            throw new IllegalStateException("Primary key fields not defined for " + getClass().getSimpleName());
        }

        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(getTableName()).append(" WHERE ");

        for (String column : primaryKeyFields.keySet()) {
            sql.append(column).append(" = ? AND ");
        }
        sql.setLength(sql.length() - 5); // Remove last 'AND'

        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (Object value : primaryKeyFields.values()) {
                stmt.setObject(index++, value);
            }

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (Exception e) {
            LoggerUtil.logError("Failed to delete entity from SQL DB", e);
            return false;
        }
    }
}
