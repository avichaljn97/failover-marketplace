package com.failover.router.config;

public class AppConfig {
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

}
