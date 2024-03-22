package com.waystech.realtimefrauddetectionsystem;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.waystech.realtimefrauddetectionsystem.fraud_detection_system.FraudDetectionSystem;
import com.waystech.realtimefrauddetectionsystem.fraud_detection_system.TransactionEvent;
import lombok.Data;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
@Data
public class RealTimeFraudDetectionSystemApplication {
    // Path Location of the data
    private static final String SAMPLE_FILE = "src/main/resources/data.txt";
    private static final Executor executor = Executors.newFixedThreadPool(10);
    private static final FraudDetectionSystem fraudDetectionSystem = new FraudDetectionSystem();

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        processTransactions(SAMPLE_FILE, objectMapper);
    }

    private static void processTransactions(String filename, ObjectMapper objectMapper) {
        try {
            System.out.println(filename);
            long x = System.currentTimeMillis();
            InputStream is = new FileInputStream(filename);
            String content = new Scanner(is).useDelimiter("\\Z").next();
            List<TransactionEvent> transactions = parseTransactions(content, objectMapper);
            long y = System.currentTimeMillis();
            System.out.println("SECS: " + (y - x));
            System.out.println(transactions.size());
            for (TransactionEvent transaction : transactions) {
                executor.execute(() -> {
                    try {
                        fraudDetectionSystem.processTransaction(transaction);
                    } catch (Exception ex) {
                        Logger.getLogger(RealTimeFraudDetectionSystemApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        } catch (IOException ex) {
            Logger.getLogger(RealTimeFraudDetectionSystemApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static List<TransactionEvent> parseTransactions(String content, ObjectMapper objectMapper) {

        try {
            return objectMapper.readValue(content, new TypeReference<List<TransactionEvent>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
