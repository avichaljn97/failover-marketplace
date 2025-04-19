package com.failover.router;

//import com.failover.router.config.KafkaTopicManager;
//import org.json.JSONObject;
//
//public class Main {
//    public static void main(String[] args) {
//        System.out.println("Kafka and JSON setup example");
//
//        // Call the Kafka topic manager
//        KafkaTopicManager manager = new KafkaTopicManager();
//        try {
//            manager.createTopicsFromConfig();
//        } catch (Exception e) {
//            System.err.println("Failed to create topics: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        // Sample JSON usage
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("key", "value");
//        System.out.println("Sample JSON: " + jsonObject.toString());
//    }
//}

import com.failover.router.receiver.LogReceiver;

public class Main {
    public static void main(String[] args) {
        try {
            LogReceiver.start();
        } catch (Exception e) {
            System.err.println("Failed to start LogReceiver: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
