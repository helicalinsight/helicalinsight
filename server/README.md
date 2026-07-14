# Helical Insight — Backend (Server)

The Helical Insight backend is a Java enterprise application packaged as a WAR (`hi-ee`) and deployed on Apache Tomcat. It provides REST/Servlet APIs, metadata services, report generation, scheduling, export, and administration for the React frontend.

## Table of contents

- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Project setup](#project-setup)
- [Configuration](#configuration)
- [Running the backend](#running-the-backend)
- [Building the WAR](#building-the-war)
- [Tomcat deployment](#tomcat-deployment)
- [Docker](#docker)
- [Troubleshooting](#troubleshooting)

## Architecture

The server is a Maven multi-module project (version `7.0.0`):

| Module | Purpose |
|--------|---------|
| `core` | Framework, security, admin, data source management |
| `adhoc` | Ad hoc report engine and metadata |
| `instant` | Instant BI integration |
| `cache` | Caching layer |
| `export` | Report export (Chrome headless, PDF, Excel) |
| `externalauth` | SSO and external authentication |
| `hwf` | Helical Workflow |
| `scheduling` | Quartz-based job scheduling |
| `validation` | Input validation |
| `presentation` | WAR assembly — **this is the deployable artifact** |

Runtime dependencies:

- **JDK 25**
- **Apache Tomcat 11.x** (Servlet 6 / Jakarta EE)
- **Database** — PostgreSQL (recommended for production) or Apache Derby (local development)
- **Google Chrome** — required on the server for export and screenshot features

The `hi-repository/` directory holds system configuration, report templates, plugins, and administrative XML files. It is referenced at runtime via `setting.xml` and `project.properties`.

## Prerequisites

| Requirement | Notes |
|-------------|-------|
| JDK 25 | Set `JAVA_HOME` and ensure `java -version` reports 25+ |
| Apache Maven 3.8+ | No Maven Wrapper is bundled; install Maven globally |
| Apache Tomcat 11 | Required to run the built WAR |
| Google Chrome | Latest stable; chromedriver is managed under `hi-repository/System/Reports/` |

## Project setup

### Quick setup (recommended)

From the repository root:

```bash
# Linux / macOS
./scripts/setup-dev.sh

# Windows PowerShell
.\scripts\setup-dev.ps1
```

This creates `server/db/`, patches `hi-repository` paths for your machine, and copies `.env.example` → `.env`.

### 1. Enter the server directory

```bash
cd server
```

### 2. Prepare the database

**Embedded Derby (default `local` profile)**

No database install required. Derby files are created under `server/db/` on first startup.

```bash
mvn clean package -DskipTests
```


### 3. Configure installation paths

With the `dev` profile (default), `project.properties`, `persistence.xml`, `application-context.xml`, and `quartz.properties` are **Maven-filtered** from `presentation/pom.xml` profile properties (`${dbUrl}`, `${dbDriver}`, etc.). Run `./scripts/setup-dev.sh` to patch `setting.xml` and `globalConnections.xml`, and to normalize any stale hardcoded JDBC values in `persistence.xml` back to Maven placeholders.

For custom installations, edit:

#### `hi-repository/System/Admin/setting.xml`

```xml
<efwSolution>/absolute/path/to/server/hi-repository</efwSolution>
<BaseUrl>http://localhost:8080/hi-ee/hi.html</BaseUrl>
```

#### `hi-repository/System/Admin/globalConnections.xml`

```xml
<url>jdbc:derby:/absolute/path/to/server/db/SampleTravelData</url>
```

> **Tip:** After the first Maven build, filtered copies of `project.properties`, `application-context.xml`, and `quartz.properties` are written into `presentation/target/hi-ee-7.0.0/WEB-INF/classes/`. Verify paths there before deploying.

## Configuration

### Maven profiles

Profiles are defined in `presentation/pom.xml`:

| Profile | Activation | Database |
|---------|------------|----------|
| `dev` | **Default** | Embedded Derby (`server/db/`) |
| `production` | `-Denv=production` | Derby |
| `docker` | `-Denv=docker` | PostgreSQL (container hostname `postgres`) |

### Default application users

On first startup against an empty database, the application creates:

| Username | Password | Role |
|----------|----------|------|
| `hiadmin` | `hiadmin` | Administrator |
| `hiuser` | `hiuser` | Standard user |

Change these immediately outside of local development.

## Running the backend

### Development workflow (Tomcat)

1. **Build the WAR** (see below).
2. **Copy** `presentation/target/hi-ee-7.0.0.war` to `$CATALINA_HOME/webapps/hi-ee.war`.
3. **Ensure** `hi-repository/` is on disk at the path set in `setting.xml`.
4. **Start Tomcat:**

   ```bash
   # Linux / macOS
   $CATALINA_HOME/bin/startup.sh

   # Windows
   %CATALINA_HOME%\bin\startup.bat
   ```

5. **Verify** the application is running:

   ```
   http://localhost:8080/hi-ee/hi.html
   http://localhost:8080/hi-ee/applicationSettings
   ```

### Run tests

Presentation Spring / JPA / Quartz test configs under
`presentation/src/test/resources` (`application-context.xml`, `META-INF/persistence.xml`,
`quartz.properties`) are **Maven-filtered** from the active profile in `presentation/pom.xml`.
You do not edit JDBC URLs there by hand.

**1. Prepare filesystem + SampleTravelData (once per machine / CI job)**

# Local: uses server/db and the default "dev" Maven profile
./scripts/setup-test-env.sh

# CI / matching hardcoded /home/helical/Performance paths in older tests
./scripts/setup-ci-test-env.sh

The setup script:

- Creates the DB directory layout
- Symlinks `hi-repository` when using the CI root
- Patches `setting.xml` and `globalConnections.xml`
- Converts `db-dump/SampleTravelData.sql` and loads it into an embedded Derby DB

**2. Run tests**

```bash
cd server

# Local (dev profile — filters test resources to server/db)
mvn test

```



# Single module

```bash
mvn test -pl presentation

# CI layout (filters test resources to /home/helical/Performance/hi/db)
mvn test -Denv=ci
```
| Profile | Activation | App DB | Quartz DB | SampleTravelData |
|---------|------------|--------|-----------|------------------|
| `dev` (default) | none | `server/db/hiee` | `server/db/hischeduledata` | `server/db/SampleTravelData` (via setup) |
| `ci` | `-Denv=ci` | `/home/helical/Performance/hi/db/hiee` | `.../hischeduledata` | `.../SampleTravelData` (via setup-ci) |
| `docker` | `-Denv=docker` | PostgreSQL `postgres:5432/hiee` | `hischeduledata` | N/A (image build) |

> GitHub Actions runs `scripts/setup-ci-test-env.sh` then `mvn test -Denv=ci`.


## Building the WAR

From the `server/` directory:

```bash
# Default profile (Derby, local development)
mvn clean package -DskipTests

# PostgreSQL (CI / integration)
mvn clean package -DskipTests -Ptest

# Docker profile (PostgreSQL service hostname)
mvn clean package -DskipTests -Denv=docker
```

The deployable artifact is produced at:

```
presentation/target/hi-ee-7.0.0.war
```

Deploy it as `hi-ee.war` on Tomcat so the context path `/hi-ee` matches the frontend.

## Tomcat deployment

Deploy to Apache Tomcat when running the backend directly on a host (without Docker).

### Deployment checklist

1. **Install prerequisites** — JDK 21, Tomcat 10/11, database, Google Chrome.
2. **Build the WAR** — `mvn clean package -DskipTests` from `server/`.
3. **Configure paths** — update `setting.xml`, `project.properties`, `globalConnections.xml`, and `quartz.properties` (see [Project setup](#project-setup)).
4. **Deploy the WAR** — copy `presentation/target/hi-ee-*.war` to `$CATALINA_HOME/webapps/hi-ee.war`.
5. **Place `hi-repository/`** — must exist at the absolute path referenced in `setting.xml`.
6. **Start Tomcat** and wait for the WAR to expand into `$CATALINA_HOME/webapps/hi-ee/`.
7. **Verify** — open `http://<host>:<port>/hi-ee/hi.html`.

### Post-deploy configuration

After Tomcat expands the WAR, you may need to adjust filtered config inside the exploded webapp:

| File | Path under Tomcat |
|------|-------------------|
| `application-context.xml` | `webapps/hi-ee/WEB-INF/classes/application-context.xml` |
| `persistence.xml` | `webapps/hi-ee/WEB-INF/classes/META-INF/persistence.xml` |
| `project.properties` | `webapps/hi-ee/WEB-INF/classes/project.properties` |
| `quartz.properties` | `webapps/hi-ee/WEB-INF/classes/quartz.properties` |
| `log4j2.properties` | `webapps/hi-ee/WEB-INF/classes/log4j2.properties` |

Confirm JDBC URLs, Hibernate dialect, log file paths, and repository paths match your environment. Restart Tomcat after changes.

### Chrome driver (export features)

For report export and PDF generation, ensure Google Chrome is installed on the server. ChromeDriver binaries are managed under:

```
hi-repository/System/Reports/
```

On Windows, place `windows_chromedriver.exe` in that directory. On Linux, use `linux_chromedriver`.

### Production deployment with the frontend

In production you typically run:

- **Backend** — Tomcat serving `hi-ee` (host install or [Docker image](#docker))
- **Frontend** — static build from `client/` served by Nginx or the [client Docker image](../client/README.md#docker)

Configure the frontend reverse proxy (`client/nginx.conf`) to forward `/hi-ee/` requests to your Tomcat instance.

## Docker

The backend includes a multi-stage `Dockerfile` that builds the WAR, bundles Apache Tomcat 11, and runs it with a trimmed JRE. For the full local stack (Postgres + backend + frontend), use the root [`docker-compose.dev.yml`](../docker-compose.dev.yml).

### Build the backend image

**Step 1 — Build the WAR**

```bash
cd server
mvn clean package -DskipTests -Denv=docker
```

Use `-Denv=docker` when the backend will connect to PostgreSQL on a Docker network (hostname `postgres`). For a standalone container with Derby or a host-mounted database, use the `dev` or `test` profile instead.

**Step 2 — Build the image**

```bash
docker build -t helicalinsight/backend:latest .
```

The Dockerfile expects the WAR at `presentation/target/hi-ee-*.war` (built inside the image).

### Run the backend container

```bash
docker run -d \
  --name hi-backend \
  -p 8080:8080 \
  -e HOST_IP=localhost \
  -e INSTALL_CHROME=false \
  -v "$(pwd)/hi-repository:/usr/local/Helical Insight/hi/hi-repository" \
  -v "$(pwd)/db:/usr/local/Helical Insight/hi/db" \
  helicalinsight/backend:latest
```

Access: `http://localhost:8080/hi-ee/hi.html`

### Image environment variables

| Variable | Default | Description |
|----------|---------|-------------|
| `HOST_IP` | `localhost` | Host IP used to set `<BaseUrl>` in `setting.xml` at startup |
| `INSTALL_CHROME` | `true` | Download and configure Chrome for export features |
| `CATALINA_HOME` | `/usr/local/tomcat` | Tomcat installation path |
| `INSTALLATION_LOCATION` | `/usr/local/Helical Insight` | Application data root |

### Run backend and frontend together

Create a shared network so the frontend Nginx container can reach the backend by service name:

```bash
docker network create hi-net

# Backend
cd server
mvn clean package -DskipTests -Denv=docker
docker build -t helicalinsight/backend:latest .
docker run -d \
  --name hi-backend \
  --network hi-net \
  -p 8080:8080 \
  -e HOST_IP=localhost \
  -v "$(pwd)/hi-repository:/usr/local/Helical Insight/hi/hi-repository" \
  helicalinsight/backend:latest

# Frontend — set proxy_pass to http://hi-backend:8080/hi-ee/ in nginx.conf first
cd ../client
npm ci --legacy-peer-deps
docker build -t helicalinsight/frontend:latest .
docker run -d \
  --name hi-frontend \
  --network hi-net \
  -p 3000:80 \
  helicalinsight/frontend:latest
```

Open [http://localhost:3000](http://localhost:3000) for the UI, or [http://localhost:8080/hi-ee](http://localhost:8080/hi-ee) for the backend directly.

### Database in Docker

When running with PostgreSQL in a container, start Postgres on the same network and use the `docker` Maven profile so JDBC URLs point to hostname `postgres`:

```bash
docker run -d \
  --name postgres \
  --network hi-net \
  -e POSTGRES_USER=hiuser \
  -e POSTGRES_PASSWORD=hiuser \
  -e POSTGRES_DB=hiee \
  postgres:15-alpine
```

Create a second database (`hischeduledata`) for Quartz if using the JDBC job store.

## Troubleshooting

| Symptom | Likely cause | Fix |
|---------|--------------|-----|
| 404 on `/hi-ee` | WAR not deployed or Tomcat not started | Check `$CATALINA_HOME/webapps/` and Tomcat logs |
| Database connection errors | Wrong JDBC URL or DB not running | Verify `application-context.xml` / Maven profile DB settings |
| Export/PDF fails | Chrome not installed | Install Chrome and the matching chromedriver; in Docker set `INSTALL_CHROME=true` |
| Wrong redirect URL | `BaseUrl` mismatch | Update `hi-repository/System/Admin/setting.xml` or set `HOST_IP` when using Docker |
| Container cannot reach database | Wrong JDBC hostname | Use `-Denv=docker` and hostname `postgres` on a shared Docker network |
| Out of memory during Maven build | Insufficient heap | `export MAVEN_OPTS="-Xmx2g"` |

### Log locations

- Tomcat logs: `$CATALINA_HOME/logs/catalina.out`
- Application logs: `hi-repository/System/Logs/` (path configured in `log4j2.properties`)

## Related documentation

- [Frontend README](../client/README.md)
- [Root README](../README.md)
