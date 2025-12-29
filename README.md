# Real-Time Banking Fraud Detection System

This project implements a **real-time fraud detection pipeline** for banking transactions using **Kafka, Spring Boot, PostgreSQL, Server-Sent Events (SSE), and a React (Vite) frontend**.

Transactions are streamed, evaluated against fraud rules, scored, persisted, and visualized live on the UI.

---

## Features Implemented

- Kafka-based transaction ingestion
- Rule-based fraud detection engine
- Fraud scoring mechanism
- PostgreSQL persistence (transactions + alerts)
- Real-time fraud alert streaming via SSE
- React-based dashboard 
- Dockerized infrastructure

---

## Tech Stack

**Backend**
- Java 17
- Spring Boot
- Spring Kafka
- Spring Data JPA
- PostgreSQL
- Server-Sent Events (SSE)

**Frontend**
- React
- Vite
- TypeScript
- Tailwind / UI components

**Infrastructure**
- Docker
- Docker Compose
- Apache Kafka
- Zookeeper

---

## Project Setup & Run Instructions

### Clone Repository 


### Start Infrastructure & Backend

docker compose up --build

This starts:

Kafka

Zookeeper

PostgreSQL

Fraud Detection Service

 ### Transaction Simulation

Transactions are manually simulated using:

- Postman (REST API calls)
- Kafka console producer (optional)

### Start Frontend UI

npm run dev

Open UI:

http://localhost:3000

 
 ### Fraud Rules Implemented
 
Rule	Description

UNUSUAL_AMOUNT	Amount > 3× average or > 100,000
VELOCITY	≥ 3 transactions in short window
GEO_MISMATCH	Transaction country ≠ home country
NIGHT_TX	12 AM – 4 AM & amount > 50,000
RAPID_TRANSFER → ≥ 10 transactions in 5 min window

### Output

- Fraud alerts are persisted in PostgreSQL
- Live alerts are streamed to frontend using SSE
- Each alert includes:
  - Triggered rules
  - Fraud score
  - Transaction metadata

