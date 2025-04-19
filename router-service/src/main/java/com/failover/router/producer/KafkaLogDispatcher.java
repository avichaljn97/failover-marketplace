package com.failover.router.producer;

import com.failover.router.model.AppLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaLogDispatcher {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final KafkaProducer<String, String> producer;

    static {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
    }

    public static String determineTopic(AppLog log) {
        String base = log.getMethod().equalsIgnoreCase("GET") || log.getMethod().equalsIgnoreCase("POST")
                ? "DATA_FETCH_LOG_"
                : "DATA_UPSERT_LOG_";

        return base + log.getStatus().toUpperCase();
    }

    public static void sendToKafka(String topic, AppLog log) throws Exception {
        String logJson = objectMapper.writeValueAsString(log);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, log.getUserId(), logJson);
        Future<RecordMetadata> future = producer.send(record);
        future.get(); // block until acknowledged
        System.out.println("✅ Sent log to topic: " + topic);
    }
}