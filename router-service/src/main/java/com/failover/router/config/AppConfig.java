package com.failover.router.config;

public class AppConfig {
    // MySQL
    public static final String MYSQL_URL = "jdbc:mysql://localhost:3306/logdb";
    public static final String MYSQL_USER = "loguser";
    public static final String MYSQL_PASSWORD = "logpass";
    public static final String MYSQL_TABLE = "logs";

    // Redis
    public static final String REDIS_HOST = "localhost";
    public static final int REDIS_PORT = 6379;

    // Kafka
    public static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String DEFAULT_KAFKA_GROUP = "MODERATE_GROUP";
    public static final String DEFAULT_KAFKA_TOPIC = "DATA_FETCH_LOG_SUCCESS";
    public static final String KAFKA_TOPIC = "resources/kafka-topics.json";

    public static final String SEVERITY_CONFIG = "resources/severity-config.json";

}
