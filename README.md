# Task Management System – Spring Boot Project

## Project Overview

This is a multi-step Spring Boot application built to demonstrate how to incrementally develop backend system with:

- RESTful API
- In-memory storage and in-memory database (H2)
- PostgreSQL with Flyway migrations
- Docker and Docker Compose
- Redis caching
- RabbitMQ messaging
- Scheduled and asynchronous background processing
- Unit and integration testing

Each step is implemented in a **separate Git branch**. The project simulates a **Task Manager** with Users, Tasks, and Notifications.

---

## Tech Stack

- Java 17+
- Spring Boot 3.x
- Spring Web, Spring Data JPA, Spring Cache
- H2, PostgreSQL
- Redis, RabbitMQ
- JUnit 5, Mockito
- Docker, Docker Compose
- Flyway (for migrations)

---

## How to Run with Docker

Starting from **Step 4**, this project includes full Docker support using `docker-compose`.

### Requirements

- Docker
- Docker Compose

---

### Running the Project

```bash
docker-compose up --build
```

## Ports Overview

| Component      | Description                  | Port (Host:Container)        | URL / Access                         |
|----------------|------------------------------|------------------------------|--------------------------------------|
| Redis        | Caching server               | 6379:6379                   | `redis://localhost:6379`             |
| PostgreSQL   | Relational database          | 5432:5432                   | `jdbc:postgresql://localhost:5432`   |
| RabbitMQ UI  | RabbitMQ Management Console  | 15672:15672                 | [http://localhost:15672](http://localhost:15672) |
| Spring Boot  | Main REST API                | 8080:8080                   | [http://localhost:8080](http://localhost:8080) |



