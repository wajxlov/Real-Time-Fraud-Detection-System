package com.waystech.realtimefrauddetectionsystem.fraud_detection_system;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/*TransactionEvent class encapsulates the necessary
 information for each transaction according to the task requirement*/
public class TransactionEvent {
    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("userId")
    private String userID;

    @JsonProperty("serviceId")
    private String serviceID;

    // Default constructor
    public TransactionEvent() {
    }

    // Constructor with parameters
    public TransactionEvent(Instant timestamp, double amount, String userID, String serviceID) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.userID = userID;
        this.serviceID = serviceID;
    }

    // Getters and setters
    public Instant getTimestamp() {
        return timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public String getUserID() {
        return userID;
    }

    public String getServiceID() {
        return serviceID;
    }

}