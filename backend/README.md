# Linux Server Monitoring & Alerting System — Backend

A full-stack server monitoring platform that collects real-time system metrics (CPU, RAM, disk, network) from Linux servers, caches and serves them efficiently via Redis, and automatically detects and alerts on threshold breaches and server outages.

**Frontend repo:** [monitoring-frontend](https://github.com/naren-wwcd/monitoring-frontend)

## Features

- **Metrics ingestion** — REST API receives metrics from lightweight Python agents running on monitored servers, with automatic server registration for unknown hosts
- **Redis caching** — latest metric per server is cached in Redis, with automatic fallback to PostgreSQL on cache miss
- **Heartbeat-based liveness detection** — Redis TTL keys track whether a server is actively reporting, without relying on stale database flags
- **Rate limiting** — per-hostname request throttling using atomic Redis counters, protecting the ingestion endpoint from abuse or misbehaving agents
- **Automated alerting** — a scheduled job evaluates every server every 15 seconds for CPU/RAM/disk threshold breaches and server-down conditions, automatically creating and resolving alerts as conditions change (no duplicate alert spam)
- **Historical metrics** — queryable time-range history (24h / 7d / 30d) per server for charting
- **Dockerized** — multi-stage Docker builds for both backend and frontend; full stack (Postgres, Redis, backend, frontend) orchestrated with Docker Compose
- **CI pipeline** — GitHub Actions automatically builds and runs the test suite on every push

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 25, Spring Boot 4.1.1, Spring Data JPA, Spring Data Redis |
| Database | PostgreSQL |
| Cache / Ephemeral state | Redis |
| Agent | Python (psutil) |
| Frontend | React (Vite), recharts |
| Testing | JUnit 5, Mockito |
| Containerization | Docker, Docker Compose (multi-stage builds) |
| CI/CD | GitHub Actions |

## Architecture

Python Agent --POST /api/metrics--> Spring Boot API --> Rate Limit Check
                    |
                    v
Services <----> Redis (cache, heartbeat, rate limit)
                    |
                    v
                PostgreSQL

React Dashboard <--GET /api/servers-- Spring Boot API
React Dashboard <--GET /api/alerts--- Spring Boot API

Scheduled AlertService (runs every 15s) evaluates all servers
against Redis heartbeats and latest cached metrics, then
creates/resolves rows in the alerts table.


## Running the Project

### Option 1 — Docker Compose (recommended, one command)

```bash
docker compose up --build
```

This starts PostgreSQL, Redis, the backend (port 8080), and the frontend (port 8082) together, fully networked.

### Option 2 — Manual / Local Development

Requires: Java 25, Maven, PostgreSQL running locally, Redis running locally (e.g. via `docker run -d -p 6379:6379 redis`).

Set the following environment variables before running:

DB_USERNAME=postgres
DB_PASSWORD=<your-postgres-password>


Then:
```bash
./mvnw spring-boot:run
```

## Running Tests

```bash
./mvnw test
```

Unit tests cover the alerting engine's core logic (threshold breach detection, deduplication of active alerts, and automatic resolution) using Mockito to isolate business logic from the database and Redis.

## API Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/servers` | List all registered servers with live status |
| GET | `/api/servers/{id}` | Get a single server |
| GET | `/api/servers/{id}/metrics` | Latest metric for a server (Redis-cached) |
| GET | `/api/servers/{id}/metrics/history?range=24h\|7d\|30d` | Historical metrics |
| POST | `/api/metrics` | Ingest a metric from an agent (rate-limited) |
| GET | `/api/alerts` | List currently active alerts |

## Project Status

This is an actively developed portfolio project. See commit history for progression through metrics ingestion, caching, alerting, containerization, and CI.
