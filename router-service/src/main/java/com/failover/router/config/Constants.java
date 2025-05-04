package com.failover.router.config;

public class Constants {
    // MySQL
    public static final String MYSQL_URL = "jdbc:mysql://localhost:3306/logs";
    public static final String MYSQL_USER = "router_app";
    public static final String MYSQL_PASSWORD = "routerpass";
    public static final String MYSQL_TABLE = "applogs";

    // Redis
    public static final String REDIS_HOST = "localhost";
    public static final int REDIS_PORT = 6379;

    // Kafka
    public static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String DEFAULT_KAFKA_GROUP = "MODERATE_GROUP";
    public static final String DEFAULT_KAFKA_TOPIC = "DATA_FETCH_LOG_SUCCESS";
//    public static final String KAFKA_TOPIC = "resources/kafka-topics.json";
    public static final String KAFKA_TOPIC = "/home/avichal.jain/failover-marketplace/router-service/resources/kafka-topics.json";

//    public static final String SEVERITY_CONFIG = "resources/severity-config.json";
    public static final String SEVERITY_CONFIG = "/home/avichal.jain/failover-marketplace/router-service/resources/severity-config.json";

    public static final String FETCH_PREFIX = "DATA_FETCH_LOG_";
    public static final String UPSERT_PREFIX = "DATA_UPSERT_LOG_";

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";

    public static final int REDIS_EXPIRY_SECONDS = 3 * 24 * 3600;
    public static final int KAFKA_EXPIRY_HOURS = 24;

    public static final String FETCH_OPERATION = "fetch";

    public static final String SEVERITY_LOW = "LOW";
    public static final String SEVERITY_MODERATE = "MODERATE";
    public static final String SEVERITY_HIGH = "HIGH";
    public static final String SEVERITY_CRITICAL = "CRITICAL";

    public static final String DEFAULT_SUCCESS_SEVERITY = SEVERITY_LOW;
    public static final String DEFAULT_FAILURE_SEVERITY = SEVERITY_MODERATE;}
