package com.failover.router.redis;

import com.failover.router.model.AppLog;
import redis.clients.jedis.Jedis;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.failover.router.config.AppConfig.*;


public class RedisLogWriter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void writeToRedis(AppLog log) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            // Build Redis key from log components
            String key = String.format("logs:%s:%s:%s:%s",
                    log.getUserId(),
                    log.getService(),
                    log.getEndpoint(),
                    log.getSeverity());

            // Serialize the log object as a JSON string
            String logJson = objectMapper.writeValueAsString(log);

            // Push log to Redis list
            jedis.rpush(key, logJson);

            System.out.println("✅ Log stored in Redis at key: " + key);
        } catch (Exception e) {
            System.err.println("❌ Failed to write log to Redis: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
