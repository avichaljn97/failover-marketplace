package com.failover.router;

//import com.failover.router.config.KafkaTopicManager;
//import org.json.JSONObject;
//
//public class Main {
//    public static void main(String[] args) {
//        LoggerUtil.logInfo("Kafka and JSON setup example");
//
//        // Call the Kafka topic manager
//        KafkaTopicManager manager = new KafkaTopicManager();
//        try {
//            manager.createTopicsFromConfig();
//        } catch (Exception e) {
//            LoggerUtil.logError("Failed to create topics: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        // Sample JSON usage
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("key", "value");
//        LoggerUtil.logInfo("Sample JSON: " + jsonObject.toString());
//    }
//}

import com.failover.router.receiver.LogReceiver;
import com.failover.router.util.LoggerUtil;

public class Main {
    public static void main(String[] args) {
        try {
            LogReceiver.start();
        } catch (Exception e) {
            LoggerUtil.logError("Failed to start LogReceiver: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
