package com.failover.router.redis;

import com.failover.router.model.AppLog;
import com.failover.router.util.LoggerUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import static com.failover.router.config.Constants.*;

public class RedisLogWriter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean writeToRedis(AppLog log) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            long timestamp = System.currentTimeMillis();

            String key = String.format("logs|%s|%s|%s|%s|%d",
                    log.getUserId(),
                    log.getService(),
                    log.getEndpoint(),
                    log.getSeverity(),
                    timestamp);

            String logJson = objectMapper.writeValueAsString(log);

            // ✅ Store with 3-day TTL
            jedis.setex(key, REDIS_EXPIRY_SECONDS, logJson);

            LoggerUtil.logInfo("✅ Log stored in Redis with key: " + key);
            return true;
        } catch (Exception e) {
            LoggerUtil.logError("❌ Failed to write log to Redis: " + e.getMessage(), e);
            return false;
        }
    }
}
