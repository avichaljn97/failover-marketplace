package com.failover.router.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaLogConsumer {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "log-consumer-group";
    private static final String TOPIC_NAME = "logs-topic";

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = getStringStringKafkaConsumer();
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));

        System.out.println("🔁 Listening for messages on topic: " + TOPIC_NAME);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("📩 Received -> key: " + record.key() +
                        ", value: " + record.value() +
                        ", partition: " + record.partition() +
                        ", offset: " + record.offset());
            }
        }
    }

    private static KafkaConsumer<String, String> getStringStringKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new KafkaConsumer<>(props);
    }
}
