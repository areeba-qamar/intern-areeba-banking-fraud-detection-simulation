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
- React-based dashboard (investigation pane)
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

### Clone Repository of front end


### Start Infrastructure & Backend

docker compose up --build


This starts:

Kafka

Zookeeper

PostgreSQL

Fraud Detection Service

 Run Transaction Simulator / Producer

Send transactions using:

Postman

Kafka console producer


### Start Frontend UI

npm run dev


Open UI:

http://localhost:3000

 
 ### Fraud Rules Implemented
 
Rule	Description

UNUSUAL_AMOUNT	Amount > 3× average or > 100,000
VELOCITY	≥ 5 transactions in short window
GEO_MISMATCH	Transaction country ≠ home country
NIGHT_TX	12 AM – 4 AM & amount > 50,000

 ### Output

Fraud alerts stored in database

Live alerts pushed to frontend via SSE

Alerts include score, rules, transaction details
