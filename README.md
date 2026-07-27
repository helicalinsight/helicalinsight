# Helical Insight

[![GitHub release](https://img.shields.io/github/v/release/helicalinsight/helicalinsight)](https://github.com/helicalinsight/helicalinsight/releases)
[![GitHub Downloads](https://img.shields.io/github/downloads/helicalinsight/helicalinsight/latest/total)](https://github.com/helicalinsight/helicalinsight/releases/latest)
[![GitHub Stars](https://img.shields.io/github/stars/helicalinsight/helicalinsight)](https://github.com/helicalinsight/helicalinsight/stargazers)
![Coverage](https://raw.githubusercontent.com/helicalinsight/helicalinsight/master/.github/badges/jacoco.svg)

[![Docker Pulls](https://img.shields.io/docker/pulls/hiee/helicalinsight)](https://hub.docker.com/)

[Helical Insight](https://www.helicalinsight.com/) is an open-source business intelligence (BI) and analytics platform. It lets teams connect to data sources, explore metadata, build ad hoc reports and dashboards, schedule deliveries, and export results — all from a modern web interface.

## Video Overview
[![Watch the Helical Insight Introduction](https://img.youtube.com/vi/hz07TO1gL9c/0.jpg)](https://youtu.be/hz07TO1gL9c?si=Tg0d2oJipahBeElL)
<video src="https://raw.githubusercontent.com/helicalinsight/helicalinsight/master/docs/Helical-Insight-Open-Source-Business-Intelligence-Introduction-video.webm" autoplay loop muted playsinline width="100%">
</video>

## Features

- **Ad hoc reporting** — drag-and-drop report designer with tables, charts, crosstabs, and custom visualizations
- **Dashboard designer** — interactive dashboards with filters and drill-down
- **Metadata management** — connect to JDBC, NoSQL, and middleware data sources
- **Scheduling** — email and automate report delivery
- **Export** — PDF, Excel, and other formats (Chrome-based rendering on the server)
- **Administration** — users, roles, plugins, and system configuration
- **Instant BI** — Easily ask questions in natural language to get instant visual insights. It includes the flexibility to **Bring Your Own LLM (BYOL) model.
- **Canned Report** - Create professional reports ideal for invoices, financial statements, compliance documents, and operational needs.

## Demo
![Helical Insight Introduction](docs/All%20Resource%20Open%20mode.gif)

---

## Pick your path

| You want to… | Do this |
|--------------|---------|
| **Run the product** (no coding) | [Run Helical Insight](#run-helical-insight-zero-configuration) |
| **Contribute / develop** | [Developer setup](#developer-setup) |

Configuration is handled for you. End users copy one `.env` file and start Docker. Contributors run one setup script, then work on the component they care about.

---

## Run Helical Insight (zero configuration)

**Best path for downloads, demos, and anyone who just wants the app up.**

You need [Docker](https://docs.docker.com/get-docker/) and Docker Compose. Nothing else.

### From a Docker package (ZIP)

Download **`helicalinsight-<version>-docker.zip`** from [GitHub Releases](https://github.com/helicalinsight/helicalinsight/releases) (created automatically when a version tag is pushed).

1. Unzip the package and open the folder that contains `docker-compose.yml`.
2. Copy settings (defaults are fine on your own machine):

   ```bash
   cp .env.example .env
   # Windows PowerShell: Copy-Item .env.example .env
   ```

3. Start:

   ```bash
   docker compose up -d
   ```

4. Open **https://localhost** — login: `hiadmin` / `hiadmin`

### From this git repository

```bash
git clone https://github.com/helicalinsight/helicalinsight.git
cd helicalinsight

# One-time: link Instant BI + create docker/.env
./scripts/setup-dev.sh          # Linux / macOS
# .\scripts\setup-dev.ps1       # Windows PowerShell

cd docker
docker compose up -d
```

Open **https://localhost** — login: `hiadmin` / `hiadmin`

Full Docker guide (stop, logs, ports, updates): [docker/readme/readme.md](docker/readme/readme.md)

### First start takes a few minutes — that is normal

The **first** `docker compose up` is the slowest. Wait for containers to become healthy before judging the app.

| What happens | Why it takes time |
|--------------|-------------------|
| Image pull / Instant BI build | Downloads images; builds Instant BI once if the local image is missing (deps baked into image) |
| App bootstrap | Tomcat starts; Chrome may install for export (`INSTALL_CHROME=true` by default) |

**How to know it is ready**

```bash
cd docker
docker compose ps          # wait until services look healthy / running
docker compose logs -f     # watch progress; Ctrl+C to stop following
```

Then open **https://localhost**. Later starts are much faster (images and Instant BI deps are cached).

**Optional speed tip (no PDF/export needed):** in `docker/.env` set `INSTALL_CHROME=false`, then restart.

| Username | Password | Role |
|----------|----------|------|
| `hiadmin` | `hiadmin` | Administrator |
| `hiuser` | `hiuser` | Standard user |

Change these credentials outside local/demo use.

---

## Developer setup

Goal: spend time on product work, not path/XML/env hunting. Run the shared setup **once**, then start only the stack pieces you need.

### What’s in this repo

| Component | Directory | Stack | Details |
|-----------|-----------|-------|---------|
| **Backend** | [`server/`](server/) | Java 25, Spring, Hibernate, Tomcat WAR | [server/README.md](server/README.md) |
| **Frontend** | [`client/`](client/) | React 17, Redux, Ant Design | [client/README.md](client/README.md) |
| **Instant BI** | [`ib/`](ib/) | Python, Flask, LangChain / LangGraph | [ib/README.md](ib/README.md) |

```
┌────────────┐     /hi-ee/*      ┌────────────┐     Instant BI API     ┌────────────┐
│  Frontend  │ ───────────────► │  Backend   │ ─────────────────────► │ Instant BI │
│  :3000     │                  │  :8080     │                        │  :8000     │
└────────────┘                  └────────────┘                        └────────────┘
```

### Recommended IDE

| Area | Recommendation |
|------|----------------|
| **Backend** | **[Eclipse IDE](https://www.eclipse.org/downloads/)** (Enterprise Java / Web) — best fit: add a Tomcat 11 server in Eclipse and run/debug the `presentation` WAR without manual copy-deploy |
| **Frontend** | VS Code, Cursor, or any Node-friendly editor |
| **Instant BI** | VS Code, Cursor, or any Python-friendly editor |

Other Java IDEs (e.g. IntelliJ IDEA) work, but **Eclipse + Tomcat** is the supported day-to-day path for the backend. Steps: [server/README.md — Eclipse + Tomcat](server/README.md#eclipse--tomcat-recommended).

### 1. Prerequisites (once)

| Tool | Version | Needed for |
|------|---------|------------|
| JDK | 25+ | Backend |
| Maven | 3.8+ | Backend |
| Node.js | 18+ | Frontend |
| npm | 9+ | Frontend |
| Python | 3.9+ | Instant BI (native) |
| Apache Tomcat | 11.x+ | Backend (native only) |
| Docker + Compose | recent | Fastest full stack / product run |

```bash
./scripts/check-prerequisites.sh      # Linux / macOS
# .\scripts\check-prerequisites.ps1  # Windows PowerShell
```

### 2. One-time repo setup (required)

Patches `hi-repository` paths for your machine, creates `.env` files, and links Instant BI into `docker/instantbi/helicalbi` (needed for Docker from a git checkout).

```bash
./scripts/setup-dev.sh      # Linux / macOS
# .\scripts\setup-dev.ps1  # Windows PowerShell
```

After this, you should not hand-edit install paths for normal local work.

### 3. Choose how you work

#### A — Full stack in Docker (recommended for most contributors)

Runs backend + Postgres + Nginx + Instant BI with minimal local tooling:

```bash
cd docker
docker compose up -d
# https://localhost  →  hiadmin / hiadmin
```

Build the **backend from source** instead of the published image:

```bash
docker compose -f docker-compose.dev.yml up --build
# http://localhost:8080/hi-ee/  →  hiadmin / hiadmin
```

#### B — Native: work on one component

Use this when you need a fast edit–reload loop on a single app.

<details>
<summary><strong>Backend</strong> — Eclipse + Tomcat (recommended) or WAR deploy</summary>

**Recommended:** use **Eclipse** with a Tomcat 11 server and deploy the `presentation` module as context `/hi-ee`. See [server/README.md — Eclipse + Tomcat](server/README.md#eclipse--tomcat-recommended).

Or build and deploy by hand:

```bash
cd server
mvn clean package -DskipTests
# Deploy as hi-ee.war so the context path matches the frontend:
# presentation/target/hi-ee-7.0.0.war  →  $CATALINA_HOME/webapps/hi-ee.war
```

Default Maven profile uses embedded Derby under `server/db/` (no Postgres install).  
Verify: `http://localhost:8080/hi-ee/`

More: [server/README.md](server/README.md)

</details>

<details>
<summary><strong>Frontend</strong> — React dev server (needs a running backend)</summary>

```bash
cd client
npm ci --legacy-peer-deps
npm run start18
# http://localhost:3000
```

Dev proxy defaults to `http://localhost:8080`. Start the backend (native Tomcat or Docker) first.

More: [client/README.md](client/README.md)

</details>

<details>
<summary><strong>Instant BI</strong> — Python service on port 8000</summary>

```bash
cd ib/helicalbi
python -m venv .venv
# Linux / macOS: source .venv/bin/activate
# Windows:       .\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
cp .env.example .env    # defaults use stub LLM — no API key required
python app.py
# http://localhost:8000
```

Or let Docker run it as part of the compose stack (after `setup-dev`).

More: [ib/README.md](ib/README.md)

</details>

### Suggested day-to-day flows

| You’re changing… | Typical loop |
|------------------|--------------|
| UI only | Backend via Docker → `npm run start18` in `client/` |
| Java APIs / reports | Build & deploy WAR (or `docker-compose.dev.yml`) → hit API or UI |
| Instant BI / LLM | Run `ib/helicalbi` locally or Instant BI container → exercise from the product UI |
| Everything | `cd docker && docker compose up -d` |

### Tests

```bash
# Backend
cd server && mvn test

# Frontend
cd client && npm test

# Instant BI
cd ib/helicalbi && pytest -m "not llm"
```

CI graph: [`.github/workflows/README.md`](.github/workflows/README.md)

---

## Repository structure

```
├── client/                  # React frontend
├── server/                  # Java backend (Maven multi-module → hi-ee WAR)
│   ├── core/ adhoc/ export/ scheduling/ …
│   ├── presentation/        # WAR packaging → hi-ee-7.0.0.war
│   └── hi-repository/       # System config and templates
├── ib/helicalbi/            # Instant BI (Python)
├── docker/                  # One-command product run (users + contributors)
├── scripts/                 # setup-dev, prerequisite checks
├── docker-compose.dev.yml   # Build backend from source in Docker
└── README.md
```

> The WAR is built as `hi-ee-7.0.0.war` but must be deployed as `hi-ee.war` so the Tomcat context `/hi-ee` matches the frontend.

## Contributing

**Fork the repo → work on a branch → open a pull request.** See [CONTRIBUTING.md](CONTRIBUTING.md) for the full flow. Keep PRs focused; prefer product changes over one-off machine config commits (no secrets, no personal paths, no `.env`).

## License

See [LICENSE](LICENSE) and [LICENSE-HICL](LICENSE-HICL.MD). If licensing is unclear for your use case, contact [Helical Insight](https://www.helicalinsight.com/) before redistribution.

## Support

- Website: [helicalinsight.com](https://www.helicalinsight.com/)
- Issues: [GitHub Issues](https://github.com/helicalinsight/helicalinsight/issues)
