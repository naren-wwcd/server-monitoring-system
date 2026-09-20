# Architecture

This document describes how the system is designed, why key decisions were made, and what its known limitations are. It's intended both as project documentation and as a reference for discussing design trade-offs.

## System Overview

The system has four moving parts:

1. **Python agents** run on each monitored machine, collecting CPU/RAM/disk/network/uptime/process-count metrics every 10 seconds and POSTing them to the backend.
2. **Spring Boot backend** ingests metrics, persists them, caches hot data in Redis, tracks server liveness, enforces rate limits, and runs a scheduled job that evaluates alert conditions.
3. **PostgreSQL** is the system of record — every metric, server, and alert is durably stored here.
4. **React frontend** polls the backend every 10 seconds and renders live dashboards, historical charts, and active alerts.

## Data Flow

**Ingestion path:**

Agent -> POST /api/metrics -> Rate limit check (Redis)
-> Auto-register server if unknown (Postgres)
-> Save metric (Postgres)
-> Cache latest metric (Redis)
-> Refresh heartbeat TTL (Redis)


**Read path:**

Frontend -> GET /api/servers -> live status computed from Redis heartbeat
(not a stored DB column)
Frontend -> GET /api/servers/{id}/metrics -> Redis cache first,
Postgres fallback on miss
Frontend -> GET /api/alerts -> currently ACTIVE alerts from Postgres


**Alerting path (independent of ingestion):**

Scheduled job (every 15s) -> for each server:
- check Redis heartbeat -> SERVER_DOWN condition
- check latest cached metric -> CPU_HIGH / RAM_HIGH / DISK_HIGH conditions
- if breached and no active alert exists -> create one
- if not breached and an active alert exists -> resolve it


## Key Design Decisions

### Why Redis is used for three different things
Redis serves three distinct purposes in this system, each using a different pattern:
- **Caching** (`latest_metric:{id}`) — avoids a Postgres round-trip for the most frequently-read data (the current snapshot of each server).
- **Heartbeat / liveness** (`heartbeat:{id}`, with a 30s TTL) — a server is considered "up" purely by whether this key exists. This avoids needing a background job to mark servers as down; Redis's TTL expiry does that work for free. It also means liveness is never stale — a stored `status` column would require every mutation path to remember to update it, and would still be wrong the moment a server silently stops posting.
- **Rate limiting** (`rate_limit:{hostname}`, atomic `INCR` + `EXPIRE`) — protects the ingestion endpoint using the same "counter with a TTL" idea, but for throttling instead of liveness. `INCR` is atomic at the Redis level, avoiding race conditions that a naive read-then-write check would have under concurrent requests.

### Why alerting is a scheduled job, not event-driven
Threshold breaches (CPU/RAM/disk) could be checked at ingestion time (when a metric arrives). But server-down detection *cannot* — there is no event to react to when a server goes silent. A scheduled poll (every 15s) is the only approach that handles both cases uniformly, so the whole alerting engine was built as one polling job rather than splitting logic between an ingestion-time check and a separate down-detection mechanism.

### Why alerts deduplicate via an "active alert" lookup
Without deduplication, a server sitting at 95% CPU for ten consecutive scheduled checks would create ten identical alert rows. Instead, each check first asks "is there already an ACTIVE alert of this type for this server?" — if yes, do nothing; if the condition has cleared and one exists, resolve it. This keeps the `alerts` table meaningful (one row per incident, with a clear start and end) rather than a noisy log of every check.

### Why DTOs are always separate from entities
Controllers and the frontend never see JPA entities directly. This avoids leaking database structure (e.g. lazy-loaded relationships, internal IDs) into API responses, and lets the API shape evolve independently of the schema. The one known inconsistency — `GET /{id}/metrics` still returns a raw `Metric` entity instead of a DTO — is a deliberate leftover flagged for future cleanup, not an oversight.

### Why credentials are environment variables, not hardcoded
`application.properties` originally had a plaintext database password. Once the repo went public on GitHub, this was fixed by rotating the password and switching to `${DB_USERNAME}` / `${DB_PASSWORD}` placeholders resolved from environment variables at runtime — set via IntelliJ's run configuration locally, or via Compose's `environment:` block in containers. The old password remains visible in Git history but is no longer valid.

### Why Docker Compose uses service names instead of IP addresses
Early in development, running the backend as a standalone container required `host.docker.internal` to reach Postgres/Redis running outside Docker. Under Compose, all services share a network where they can address each other directly by service name (`postgres`, `redis`) — no manual IP/hostname configuration needed, and this configuration travels with the repo rather than being tied to one developer's machine.

## Known Limitations

- **No authentication** — all endpoints are open. A real deployment would need API keys for agent ingestion and user auth for the dashboard.
- **Single-instance scheduling** — the `@Scheduled` alert job assumes one backend instance. Running multiple backend replicas would cause duplicate alert evaluation; a production version would need a distributed lock or a dedicated scheduler service.
- **Fixed-window rate limiting** — the current rate limiter allows bursting at window boundaries (a known trade-off of fixed windows vs. sliding windows). Acceptable here since the traffic pattern is a single agent posting every 10 seconds, not adversarial traffic.
- **Alert thresholds are hardcoded constants** — CPU/RAM/disk thresholds (90%) are not configurable per-server or via the API. A natural next step would be a `threshold_configs` table.
- **No secrets manager** — environment variables are sufficient for a portfolio project, but a production system would use a proper secrets manager (AWS Secrets Manager, HashiCorp Vault, etc.) rather than plain environment variables in a Compose file.
- **Limited automated test coverage** — unit tests currently cover the alerting engine's core logic. Controller-level integration tests and repository tests would be the next layer to add.

## What's Next

Planned future work (see main README's project status):
- GitHub Actions CI extended to the frontend and to Docker image builds
- Additional test coverage (controllers, repositories)
- Configurable, per-server alert thresholds
::
