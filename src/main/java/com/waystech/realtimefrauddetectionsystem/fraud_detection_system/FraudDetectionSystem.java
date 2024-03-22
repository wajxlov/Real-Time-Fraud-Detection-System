package com.waystech.realtimefrauddetectionsystem.fraud_detection_system;




import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FraudDetectionSystem {
    private final Map<String, UserTransactions> userTransactionsMap;

    public FraudDetectionSystem() {
        userTransactionsMap = new ConcurrentHashMap<>();
    }

    public void processTransaction(TransactionEvent event) {
        String userID = event.getUserID();

        // Retrieve or create a UserTransactions object atomically
        UserTransactions userTransactions = userTransactionsMap.computeIfAbsent(userID, k -> new UserTransactions());

        userTransactions.update(event);

        if (userTransactions.hasMultipleServicesWithinWindow()) {
            System.out.println("Flag user " + userID + ": conducting transactions in more than 3 distinct services within a 5-minute window.");
        }

        if (userTransactions.hasTransactionAboveThreshold()) {
            System.out.println("Flag user " + userID + ": transaction significantly higher than typical amount.");
        }
        if (userTransactions.hasPingPongActivityWithinWindow()) {
            System.out.println("Flag user " + userID + ": ping-pong activity detected within a 10-minute window.");
        }
    }
}