package com.failover.router.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaLogProducer {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC_NAME = "logs-topic";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<>(props);

        String key = "log-key";
        String value = "{\"level\":\"INFO\", \"message\":\"User login success\", \"timestamp\":\"2025-04-17T10:30:00Z\"}";

        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, key, value);

        try {
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("✅ Sent message to topic " + metadata.topic() +
                            " partition " + metadata.partition() +
                            " offset " + metadata.offset());
                } else {
                    exception.printStackTrace();
                }
            });
        } finally {
            producer.close();
        }
    }
}
