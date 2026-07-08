# Helical Insight

[Helical Insight](https://www.helicalinsight.com/) is an open-source business intelligence (BI) and analytics platform. It lets teams connect to data sources, explore metadata, build ad hoc reports and dashboards, schedule deliveries, and export results — all from a modern web interface.

## Watch the Helical Insight Introduction video
[![Watch the Helical Insight Introduction](https://img.youtube.com/vi/hz07TO1gL9c/0.jpg)](https://youtu.be/hz07TO1gL9c?si=Tg0d2oJipahBeElL)

![Helical Insight Introduction](docs/All%20Resource%20Open%20mode.gif)

This repository contains two components:

| Component | Directory | Stack |
|-----------|-----------|-------|
| **Backend** | [`server/`](server/) | Java 21, Spring, Hibernate, Apache Tomcat (WAR) |
| **Frontend** | [`client/`](client/) | React 17, Redux, Ant Design |

## Features

- **Ad hoc reporting** — drag-and-drop report designer with tables, charts, crosstabs, and custom visualizations
- **Dashboard designer** — interactive dashboards with filters and drill-down
- **Metadata management** — connect to JDBC, NoSQL, and middleware data sources
- **Scheduling** — email and automate report delivery
- **Export** — PDF, Excel, and other formats (Chrome-based rendering on the server)
- **Administration** — users, roles, plugins, and system configuration
- **Instant BI** — Easily ask questions in natural language to get instant visual insights. It includes the flexibility to **Bring Your Own LLM (BYOL) model.
- **Canned Report** - Create professional reports ideal for invoices, financial statements, compliance documents, and operational needs.

## Quick start

### Prerequisites

| Tool | Version |
|------|---------|
| JDK | 21+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| npm | 9+ |
| Apache Tomcat | 11.x+ (native backend only) |
| Docker + Compose | Optional — fastest way to run everything |

Verify tools:

```bash
# Linux / macOS
./scripts/check-prerequisites.sh

# Windows PowerShell
.\scripts\check-prerequisites.ps1
```

### 1. Clone the repository

```bash
git clone https://github.com/helicalinsight/helicalinsight.git
cd helicalinsight
git checkout helicalinsight-ce-7.0
```

### 2. Fastest path — Docker (recommended)

```bash
cp .env.example .env   # optional — defaults work for local dev
# Windows PowerShell: Copy-Item .env.example .env
docker compose -f docker-compose.dev.yml up --build
```

| URL | Purpose |
|-----|---------|
| http://localhost:3000 | React frontend |
| http://localhost:8080/hi-ce/hi.html | Backend (Tomcat) |

### 3. Native development 

**Prepare paths and directories:**

```bash
# Linux / macOS
./scripts/setup-dev.sh

# Windows PowerShell
.\scripts\setup-dev.ps1
```

**Backend** — see [server/README.md](server/README.md):

```bash
cd server
mvn clean package -DskipTests
# Deploy as hi-ce.war so the frontend context path matches:
# copy presentation/target/hi-ce-7.0.0.war → $CATALINA_HOME/webapps/hi-ce.war
```

Run `./scripts/setup-dev.sh` (or `.\scripts\setup-dev.ps1`) before the first build to patch `hi-repository` paths. Database settings in `persistence.xml` come from Maven profiles (`dev`, `docker`, `production`) — use `mvn clean package -DskipTests` or `-Denv=docker` for PostgreSQL.

**Frontend** — see [client/README.md](client/README.md):

```bash
cd client
npm ci --legacy-peer-deps
npm run start18
# Open http://localhost:3000
```

The dev proxy points at `http://localhost:8080` by default.

### 4. Log in

On first startup, the backend creates two default users (password matches username):

| Username | Role |
|----------|------|
| `hiadmin` | Administrator |
| `hiuser` | Standard user |

Change these credentials immediately in any non-development environment.

## Docker

| Compose file | Purpose |
|--------------|---------|
| [`docker-compose.dev.yml`](docker-compose.dev.yml) | **OSS local dev** — Postgres + backend + frontend |


Component Dockerfiles:

| Component | Dockerfile | Documentation |
|-----------|------------|---------------|
| Backend | `server/Dockerfile` | [server/README.md#docker](server/README.md#docker) |
| Frontend | `client/Dockerfile` | [client/README.md#docker](client/README.md#docker) |

## Repository structure

```
├── client/                  # React frontend application
├── server/                  # Java backend (Maven multi-module WAR)
│   ├── core/                # Core framework and services
│   ├── adhoc/               # Ad hoc reporting engine
│   ├── export/              # Report export (Chrome/PDF)
│   ├── scheduling/          # Job scheduling (Quartz)
│   ├── presentation/        # WAR packaging → hi-ce-7.0.0.war
│   └── hi-repository/       # System configuration and templates
├── scripts/                 # Dev setup and prerequisite checks
├── docker-compose.dev.yml   # One-command local dev stack
├── .env.example             # Environment variable template
└── README.md
```

> **Note:** The WAR artifact is named `hi-ce-7.0.0.war` but is deployed as `hi-ce.war` so the Tomcat context path `/hi-ce` matches the React frontend.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

See the [LICENSE](LICENSE) and [LICENSE-HICL](LICENSE-HICL.MD) files in the repository root. If no license file is present yet, contact [Helical Insight](https://www.helicalinsight.com/) for licensing terms before redistribution.

## Support

- Website: [helicalinsight.com](https://www.helicalinsight.com/)
- Issues: [GitHub Issues](https://github.com/helicalinsight/helicalinsight/issues)
