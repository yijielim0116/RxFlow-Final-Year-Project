# RxFlow backend

Quarkus REST API for RxFlow: protocol engine, consultations, access control, audit and document services.

**Stack:** Java 21 · Quarkus · Hibernate ORM with Panache · PostgreSQL · Flyway · SmallRye Health · OpenAPI

## Prerequisites

- Java 21 (`JAVA_HOME` set, e.g. `export JAVA_HOME=$(/usr/libexec/java_home -v 21)`)
- Podman, with the machine running: `podman machine start`
- podman-compose (`brew install podman-compose`)

Maven does not need to be installed; use the included wrapper (`./mvnw`).

## Run in development mode

Start PostgreSQL from the repository root, then start Quarkus with live reload:

```bash
podman compose up -d
cd backend
./mvnw quarkus:dev
```

| URL | Purpose |
|---|---|
| http://localhost:8080/api/info | Service name and version |
| http://localhost:8080/q/health | Liveness and readiness, including the database connection |
| http://localhost:8080/q/swagger-ui | Interactive API documentation |
| http://localhost:8080/q/dev-ui | Quarkus Dev UI |

Stop the database with `podman compose down` (data is kept in the `rxflow-pgdata` volume).

## Run the tests

Tests use Quarkus Dev Services, which starts a temporary PostgreSQL container automatically. With Podman on macOS, point Testcontainers at the Podman socket and disable its Ryuk helper container (which cannot mount the macOS socket path inside the Podman VM):

```bash
export DOCKER_HOST="unix://$(podman machine inspect --format '{{.ConnectionInfo.PodmanSocket.Path}}')"
export TESTCONTAINERS_RYUK_DISABLED=true
./mvnw test
```

## Configuration

| Profile | Database |
|---|---|
| `dev` | `localhost:5432/rxflow` from `compose.yaml` (local development credentials only) |
| `test` | Temporary container started by Dev Services |
| `prod` | Set with the environment variables `DB_JDBC_URL`, `DB_USERNAME` and `DB_PASSWORD` |

The database schema is managed only by Flyway migrations in `src/main/resources/db/migration`; Hibernate never creates or alters tables.
