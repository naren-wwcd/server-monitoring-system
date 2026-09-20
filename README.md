# Server Monitoring & Alerting System

A full-stack real-time server monitoring platform. Lightweight Python agents collect live CPU, RAM, disk, and network metrics from monitored machines and report them to a Spring Boot backend, which stores the data, caches hot reads in Redis, tracks server liveness, and automatically detects and resolves alerts for threshold breaches and server outages. A React dashboard displays everything live.

## Features

- **Metrics ingestion** — REST API receives metrics from Python agents, with automatic registration of unknown servers
- **Redis caching** — latest metric per server cached in Redis, with automatic fallback to PostgreSQL on a cache miss
- **Heartbeat-based liveness detection** — Redis TTL keys determine whether a server is currently reporting, with no stale database flags to maintain
- **Rate limiting** — per-hostname request throttling using atomic Redis counters
- **Automated alerting** — a scheduled job evaluates every server every 15 seconds for CPU/RAM/disk threshold breaches and server-down conditions, automatically creating and resolving alerts as conditions change
- **Historical metrics** — time-range history (24h / 7d / 30d) per server for charting
- **Dockerized** — multi-stage Docker builds for both services; the full stack (Postgres, Redis, backend, frontend) runs with a single Docker Compose command
- **CI pipeline** — GitHub Actions builds and tests both the backend and frontend on every push

## Tech Stack

| Layer | Technology |
|---|---|
| Monitoring Agent | Python (psutil) |
| Backend | Java 25, Spring Boot 4.1.1, Spring Data JPA, Spring Data Redis |
| Database | PostgreSQL |
| Cache / Ephemeral State | Redis |
| Frontend | React (Vite), recharts |
| Testing | JUnit 5, Mockito |
| Containerization | Docker, Docker Compose (multi-stage builds) |
| CI/CD | GitHub Actions |

## Project Structure

server-monitoring-system/
├── backend/ # Spring Boot API, alerting engine, Redis/Postgres integration
├── frontend/ # React dashboard
├── docker-compose.yml # Full-stack orchestration
└── README.md


See `backend/ARCHITECTURE.md` for a detailed explanation of the system's design decisions, data flow, and known limitations.

## Running the Project

### Option 1 — Docker Compose (recommended, one command)

Create a `.env` file in this folder:

POSTGRES_PASSWORD=choose-any-password


Then run:

```bash
docker compose up --build
```

This starts PostgreSQL, Redis, the backend (port 8080), and the frontend (port 8082), fully networked together.

- Dashboard: `http://localhost:8082`
- API: `http://localhost:8080/api/servers`

### Option 2 — Manual / local development

Requires Java 25, Maven, PostgreSQL, and Redis running locally.

**Backend:**

```bash
cd backend
# set environment variables DB_USERNAME and DB_PASSWORD first
./mvnw spring-boot:run
```

**Frontend:**

```bash
cd frontend
npm install
npm run dev
```

## Running Tests

```bash
cd backend
./mvnw test
```

Unit tests cover the alerting engine's core logic (threshold breach detection, deduplication of active alerts, and automatic resolution) using Mockito to isolate business logic from the database and Redis.

## API Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/servers` | List all registered servers with live status |
| GET | `/api/servers/{id}` | Get a single server |
| GET | `/api/servers/{id}/metrics` | Latest metric for a server (Redis-cached) |
| GET | `/api/servers/{id}/metrics/history?range=24h,7d,30d` | Historical metrics |
| POST | `/api/metrics` | Ingest a metric from an agent (rate-limited) |
| GET | `/api/alerts` | List currently active alerts |

## Project Status

This is an actively developed portfolio project, built incrementally with a focus on real infrastructure patterns: caching, liveness detection, rate limiting, scheduled alerting, containerization, and CI. See `backend/ARCHITECTURE.md` for design decisions and known limitations.
