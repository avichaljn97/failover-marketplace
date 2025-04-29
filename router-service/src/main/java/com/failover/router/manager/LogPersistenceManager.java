package com.failover.router.manager;

import com.failover.router.model.AppLog;
import com.failover.router.redis.RedisLogWriter;
import com.failover.router.util.LoggerUtil;

public class LogPersistenceManager {

    /**
     * Persist the log first into MySQL, then into Redis.
     * Rollback MySQL insert if Redis write fails.
     *
     * @param log the AppLog object to persist
     * @return true if both MySQL and Redis succeed, false otherwise
     */
    public static boolean persistLog(AppLog log) {
        boolean mysqlSuccess = false;
        boolean redisSuccess = false;

        try {
            // Step 1: Insert into MySQL
            mysqlSuccess = log.saveToSQLDb();

            if (!mysqlSuccess) {
                throw new RuntimeException("MySQL insert failed — aborting persistence.");
            }

            // Step 2: Insert into Redis
            redisSuccess = RedisLogWriter.writeToRedis(log);

            if (!redisSuccess) {
                // Step 3: Rollback MySQL insert if Redis fails
                boolean rollbackSuccess = log.deleteFromSQLDb();
                if (!rollbackSuccess) {
                    LoggerUtil.logError("Rollback of MySQL insert failed after Redis failure for AppLog: " + log.getUserId());
                }
                throw new RuntimeException("Redis insert failed after MySQL success — rollback attempted.");
            }

            // Step 4: Both succeeded
            return true;

        } catch (Exception e) {
            LoggerUtil.logError("Log persistence failed", e);
            return false;
        }
    }
}
