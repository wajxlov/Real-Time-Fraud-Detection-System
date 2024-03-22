package com.waystech.realtimefrauddetectionsystem.fraud_detection_system;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserTransactions {

    private static final Duration WINDOW_SIZE = Duration.ofMinutes(5);
    private static final double THRESHOLD_MULTIPLIER = 5;
    private static final Duration PING_PONG_WINDOW = Duration.ofMinutes(10);
    private static final int DISTINCT_SERVICE_COUNT = 3;

    private PriorityQueue<TransactionEvent> recentTransactions;


    private final ConcurrentHashMap<String, Integer> serviceCounts;
    private final ConcurrentHashMap<String, Double> totalAmounts;

    public UserTransactions() {
        recentTransactions = new PriorityQueue<>(Comparator.comparing(TransactionEvent::getTimestamp));
        serviceCounts = new ConcurrentHashMap<>();
        totalAmounts = new ConcurrentHashMap<>();
    }


//    Updates the user's transaction data with the information from a new transaction event.
//    Adds the transaction event to the recentTransactions queue, increments the service count,
//    and updates the total amount for the corresponding service.
//    Additionally, it evicts transactions from the window that are no longer within the specified time window.
//    The transaction event to be processed and added to the user's transaction data.

    public void update(TransactionEvent event) {

        String serviceID = event.getServiceID();
        double amount = event.getAmount();

        serviceCounts.put(serviceID, serviceCounts.getOrDefault(serviceID, 0) + 1);
        totalAmounts.put(serviceID, totalAmounts.getOrDefault(serviceID, 0.0) + amount);
        recentTransactions.offer(event);
        // Remove transactions outside the window
        evictTransactionsFromWindow();
    }



     // Evicts transactions from the recentTransactions queue that are outside the specified time window.
     // Transactions with timestamps before the calculated window start time are removed.
     // Adjusts the serviceCounts and totalAmounts accordingly.
    private void evictTransactionsFromWindow() {
        Instant windowStart = Instant.now().minus(WINDOW_SIZE);
        while (!recentTransactions.isEmpty() && recentTransactions.peek().getTimestamp().isBefore(windowStart)) {
            TransactionEvent removedEvent = recentTransactions.poll();
            String removedServiceID = removedEvent.getServiceID();
            serviceCounts.put(removedServiceID, serviceCounts.get(removedServiceID) - 1);
            totalAmounts.put(removedServiceID, totalAmounts.get(removedServiceID) - removedEvent.getAmount());
        }
    }

    /**
     * Checks if the user has conducted transactions in more than a specified number of distinct services
     * within a given time window.
     * @return true if the user has conducted transactions in more than the specified number of distinct services,
     *         false otherwise.
     */
    public boolean hasMultipleServicesWithinWindow() {

        // Calculate the start time of the window based on the WINDOW_SIZE
        Instant windowStart = Instant.now().minus(WINDOW_SIZE);
        List<TransactionEvent> eventsWithinWindow = recentTransactions.stream()
                .filter(event -> event.getTimestamp().isAfter(windowStart))
                .collect(Collectors.toList());

        Set<String> distinctServicesWithinWindow = eventsWithinWindow.stream()
                .map(TransactionEvent::getServiceID)
                .collect(Collectors.toSet());
        return distinctServicesWithinWindow.size() > DISTINCT_SERVICE_COUNT;
    }


    /**
     * Checks if the latest transaction amount is above a threshold compared to the average transaction amount
     * within the last 24 hours.
     * @return true if the latest transaction amount exceeds the threshold, false otherwise.
     */
    public boolean hasTransactionAboveThreshold() {
        if (recentTransactions.isEmpty()) {
            return false;
        }

        Instant twentyFourHoursAgo = Instant.now().minus(Duration.ofHours(24));
        List<TransactionEvent> relevantTransactions = recentTransactions.stream()
                .filter(event -> event.getTimestamp().isAfter(twentyFourHoursAgo))
                .collect(Collectors.toList());
        double sum = relevantTransactions.stream().mapToDouble(TransactionEvent::getAmount).sum();
        double average = sum / relevantTransactions.size();
        double latestAmount = recentTransactions.peek().getAmount();

        return latestAmount > average * THRESHOLD_MULTIPLIER;
    }

    /**
     * Checks if there is a "ping-pong" activity within the specified window.
     * "Ping-pong" activity refers to transactions bouncing back and forth between two services within a given time window.
     * @return true if ping-pong activity is detected within the window, false otherwise.
     */
    public boolean hasPingPongActivityWithinWindow() {
        Instant windowStart = Instant.now().minus(PING_PONG_WINDOW);
        String firstService = null;
        for (TransactionEvent transaction : recentTransactions) {
            if (transaction.getTimestamp().isAfter(windowStart)) {
                if (firstService == null) {
                    firstService = transaction.getServiceID();
                } else if (!transaction.getServiceID().equals(firstService)) {
                    return false;
                }
            } else {
                break;
            }
        }
        return true;
    }
}