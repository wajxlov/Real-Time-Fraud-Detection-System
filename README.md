# Real-Time-Fraud-Detection-System

Welcome to the Real-Time Fraud Detection System! This system is designed to analyze transactions across services in real-time to identify potential fraud without relying on external libraries specifically designed for anomaly detection.

**Running the Prototype**

**Prerequisites:**

#Java 17 installed on your system

Instructions:
    Compile and run the FraudDetectionSystemApp class to execute the fraud detection system.

**Algorithm Overview**
The fraud detection system processes a continuous stream of transaction events, each comprising a timestamp, transaction amount, user ID, and service ID. Here's how it works:

    1.Data Processing: The system tracks user transactions, updating service counts, total amounts, and recent transactions.
    2.Fraud Detection Rules: It applies predefined rules to detect suspicious patterns, including:
        1.Multiple services within a time window
        2.Transactions significantly above the user's average
        3.Ping-pong activity between services

**Addressing Constraints**

Efficiency without External Libraries:

-The system prioritizes algorithm design over reliance on external libraries, ensuring efficient processing of large transaction volumes.

Handling Large Volumes of Data:

-Utilizing efficient data structures and processing techniques enables scalability to handle high transaction volumes without performance degradation. 

Simulating Real-Time Processing:

-While not using real-time frameworks, the system effectively simulates real-time processing by promptly handling arriving events, whether from input files or in-memory queues.

Conclusion

This Real-Time Fraud Detection System efficiently processes transactions in real-time, detects fraudulent patterns, and generates alerts, meeting the requirements of the task.
