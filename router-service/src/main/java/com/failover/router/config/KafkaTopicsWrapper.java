package com.failover.router.config;

public class KafkaTopicsWrapper {
    private KafkaTopicConfig[] topics;

    public KafkaTopicConfig[] getTopics() {
        return topics;
    }

    public void setTopics(KafkaTopicConfig[] topics) {
        this.topics = topics;
    }
}
