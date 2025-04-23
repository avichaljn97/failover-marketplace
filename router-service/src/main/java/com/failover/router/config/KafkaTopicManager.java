package com.failover.router.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.errors.TopicExistsException;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import static com.failover.router.config.AppConfig.*;


public class KafkaTopicManager {

    private static final String BOOTSTRAP_SERVERS = KAFKA_BOOTSTRAP_SERVERS;
    private static final String TOPIC_CONFIG_FILE = KAFKA_TOPIC;

    public void createTopicsFromConfig() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        KafkaTopicsWrapper wrapper = mapper.readValue(new File(TOPIC_CONFIG_FILE), KafkaTopicsWrapper.class);
        KafkaTopicConfig[] topics = wrapper.getTopics();

        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        try (AdminClient adminClient = AdminClient.create(config)) {
            for (KafkaTopicConfig topic : topics) {
                NewTopic newTopic = new NewTopic(
                        topic.getName(),
                        topic.getPartitions(),
                        topic.getReplicationFactor()
                );
                CreateTopicsResult result = adminClient.createTopics(Collections.singleton(newTopic));
                try {
                    result.all().get();
                    System.out.println("Created topic: " + topic.getName());
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof TopicExistsException) {
                        System.out.println("Topic already exists: " + topic.getName());
                    } else {
                        throw e; // Rethrow other unexpected errors
                    }
                }
            }
        } catch (ExecutionException e) {
            System.out.println("Some error occured init: " + e.getMessage());
        }
    }
}
