# Helical Insight Docker Guide

Same steps for every Docker package. Copy `.env`, start Compose, open the browser — no Tomcat, Maven, or Node install required.

## What you need

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Start

1. Open the folder that contains `docker-compose.yml` (this `docker` folder, or your unzipped Docker package).
2. Copy settings (leave defaults if the browser runs on the same machine):

   ```bash
   cp .env.example .env
   ```

   On Windows PowerShell: `Copy-Item .env.example .env`

3. If browsers will reach this machine over the network, edit `.env` and set `HOST_IP` to this machine’s IP or hostname.
4. Start:

   ```bash
   docker compose up -d
   ```

   Instant BI bind-mounts the app (`instantbi/com/helicalinsight/instantbi` → `/app`), config (`hi/hi-repository/System/InstantBI` → `/app/helicalbi/config`), and logs (`hi/hi-repository/System/Logs` → `/app/logs`). It runs `pip install` on start (then Nuitka `./app` if present, else `python app.py`). First start can take several minutes.

5. Wait until services are up (first run can take several minutes — see below), then open **https://localhost**  
   Login: **hiadmin** / **hiadmin**

### From a git clone

Run the repo setup script once so Instant BI app is linked (`docker/instantbi/com/helicalinsight/instantbi`), `hi-repository/System/InstantBI` points at `helicalbi/config`, and `docker/.env` exists:

```bash
# from repository root
./scripts/setup-dev.sh          # Linux / macOS
# .\scripts\setup-dev.ps1       # Windows
cd docker && docker compose up -d
```

## First start: expect a few minutes

| Phase | What is happening |
|-------|-------------------|
| Pull | Images download (Postgres, Nginx, Helical Insight) |
| Instant BI | Bind-mount + `pip install` on container start |
| `hiee` | Tomcat boots; optional Chrome install for export |

Check progress:

```bash
docker compose ps
docker compose logs -f
```

Open the UI only after `hiee` / `nginx` look healthy (or after logs show Tomcat is ready). Instant BI reinstalls Python packages on each container start.

**Faster first boot without export/PDF:** in `.env` set `INSTALL_CHROME=false`, then restart.

## Stop

```bash
docker compose down
```

## Restart

```bash
docker compose down && docker compose up -d
```

## Logs

```bash
docker compose logs -f
```

## Update

```bash
docker compose down
docker compose pull
docker compose up -d
```

## Change the HTTPS port

See [portchange.md](portchange.md).

## More help

- Product overview & contributor setup: [../../README.md](../../README.md)
- [Helical Insight documentation](https://www.helicalinsight.com/)
