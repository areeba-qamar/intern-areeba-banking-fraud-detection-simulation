# Real-Time Banking Fraud Detection System

This backend service implements a **real-time fraud detection pipeline** for banking transactions. 
It processes transactions from Kafka, evaluates them against fraud rules, stores them in PostgreSQL, 
and streams live alerts to the frontend via SSE.

---

## Features Implemented

- Kafka-based transaction ingestion
- Rule-based fraud detection engine
- Fraud scoring mechanism
- PostgreSQL persistence (transactions + alerts)
- Real-time fraud alert streaming via SSE
- Dockerized infrastructure

---

## Tech Stack

- Java 17
- Spring Boot (REST APIs, Kafka integration, SSE)
- Spring Kafka (message ingestion)
- Spring Data JPA (database persistence)
- PostgreSQL (transaction & alerts storage)
- Server-Sent Events (SSE) for live updates
- Maven (build & dependency management)
- Docker & Docker Compose (containerized services: Kafka, Zookeeper, PostgreSQL, Backend)
---

## Project Setup & Run Instructions

### Clone Repository

git clone https://github.com/areeba-qamar/intern-areeba-banking-fraud-detection-simulation.git

cd intern-areeba-banking-fraud-detection-simulation


### Start Backend and Infrastructure

**Terminal 1**

docker compose up --build

This starts:

- Kafka & Zookeeper
- PostgreSQL
- Fraud Detection Backend Service

**Terminal 2**

Open another terminal to check the application logs after each transaction:

docker logs -f fraud-service

**Terminal 3**

Verify the list of kafka topics:

- docker exec -it fraud-kafka kafka-topics --list --bootstrap-server kafka:9092

It should list down two kafka topics :

 - transactions
 - fraud-alerts

In case any of the above topic is missing:

To create transaction topic

- docker exec -it fraud-kafka kafka-topics --create --topic transactions --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

To create fraud-alerts topic

- docker exec -it fraud-kafka kafka-topics --create --topic fraud-alerts --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

To verify the data  stored in database tables, Enter into the DB container:

- docker exec -it fraud-postgres psql -U fraud_user banking_fraud_detection_db

Now you can perform your queries

- SELECT * FROM account_profiles;
- SELECT * FROM transactions;
- SELECT * FROM fraud_alerts;

**Terminal 4** (optional)

curl commands to see the message push via SSE

- curl.exe -N http://localhost:8080/stream/transactions
- curl.exe -N http://localhost:8080/stream/fraud-alerts


### Transaction Simulation

You can simulate transactions using Postman or any REST client.

#### 1. Create an Account Profile
- **Create Profile:**: `POST http://localhost:8080/account-profiles`
- **Payload Example**:
```json
{
  "accountId": "acc-101",
  "avgDailySpend": 50000,
  "avgTxnAmount": 20000,
  "homeCountry": "PK",
  "riskTier": "LOW"
}
```
This endpoint creates the profile for a specific account.

#### 2. Send Transactions

- **Send Transaction:**: `POST http://localhost:8080/transactions`
- **Payload Example**:
```json
{
  "transactionId": "tx-101",
  "accountId": "acc-101",
  "txnType": "DEBIT",
  "amount": 35000,
  "currency": "PKR",
  "location": "PK",
  "merchant": "ATM-123",
  "timestamp": "2025-12-29T10:30:00"
}
```
You can send multiple transactions for the same accountId with different transactionId.
Fraud detection rules are applied in real-time, and alerts are generated for any suspicious transactions.

### Start Frontend UI (in parallel while sending transactions)

Frontend runs separately (React + Vite) on
http://localhost:3000/
Visit Frontend README for setup.
https://github.com/areeba-qamar/banking_fraud_detection_simulation.git

*Make sure to run the frontend and open the dashboard in parallel while sending transactions using 
Postman or any REST client.*

### Fraud Rules Implemented

| Rule             | Description                                         |
|------------------|-----------------------------------------------------|
| UNUSUAL_AMOUNT   | Amount > 3× average or > 100,000                    |
| VELOCITY         | ≥ 3 transactions in a short window                  |
| GEO_MISMATCH     | Transaction country ≠ home country, other than "PK" |
| NIGHT_TX         | Between 12 AM – 4 AM & amount > 50,000              |
| RAPID_TRANSFER   | ≥ 10 transactions in 5-minute window                |


### Output

- Transactions are persisted in PostgreSQL
- Live transactions are streamed to frontend using SSE.
- Fraud alerts are persisted in PostgreSQL
- Live alerts are streamed to frontend using SSE
- Each alert includes:
  - Triggered rules
  - Fraud score
  - Transaction metadata
- On clicking each Alert the, You can visualize profile associated with that account.

