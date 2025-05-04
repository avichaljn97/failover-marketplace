package com.failover.router.consumer;

import com.failover.router.manager.LogPersistenceManager;
import com.failover.router.util.LoggerUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.failover.router.model.AppLog;
import static com.failover.router.config.Constants.*;


import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaLogConsumer {

    private static final String BOOTSTRAP_SERVERS = KAFKA_BOOTSTRAP_SERVERS;
    private static final String TOPIC = DEFAULT_KAFKA_TOPIC; // change if needed
    private static final String GROUP_ID = DEFAULT_KAFKA_GROUP;


    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC));
//             LoggerUtil.logInfo("Kafka consumer started. Listening on topic: " + TOPIC);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    LoggerUtil.logInfo("Received message: " + record.value());

                    try {
                        // Deserialize the JSON string to AppLog
                        AppLog appLog = objectMapper.readValue(record.value(), AppLog.class);

                        // Use LogPersistenceManager to persist with rollback logic
                        boolean success = LogPersistenceManager.persistLog(appLog);

                        if (!success) {
                            LoggerUtil.logError("Log persistence failed for key: " + appLog.getUserId());
                        }

                    } catch (Exception e) {
                        LoggerUtil.logError("Failed to parse or persist log", e);
                    }
                }
            }
        } catch (Exception e) {
            LoggerUtil.logError("Kafka consumer error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
