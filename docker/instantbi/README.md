# Instant BI (product Docker layout)

Compose does **not** build an Instant BI image. The `instantbi` service uses `python:3.13-slim` and bind-mounts the app, config, and logs:

```yaml
volumes:
  - ./instantbi/com/helicalinsight/instantbi:/app
  - ./hi/hi-repository/System/InstantBI:/app/helicalbi/config
  - ./hi/hi-repository/System/Logs:/app/logs
working_dir: /app
command: bash -c "pip install -r requirements.txt && if [ -f ./app ]; then chmod +x ./app && ./app; else python app.py; fi"
```

| Host (relative to `docker/`) | Container | Purpose |
|------------------------------|-----------|---------|
| `instantbi/com/helicalinsight/instantbi` | `/app` | Application |
| `hi/hi-repository/System/InstantBI` | `/app/helicalbi/config` | YAML overlay (`application_config.yaml`, `llm_config.yaml`, …) |
| `hi/hi-repository/System/Logs` | `/app/logs` | `instantbi_app.log` / `instantbi_error.log` (same folder as Helical Insight logs) |

On start, Compose installs Python deps, then runs the Nuitka binary `./app` if it exists, otherwise `python app.py`.

YAML source of truth stays in the Python tree:

```text
instantbi/src/com/helicalinsight/instantbi/helicalbi/config
```

Docker only changes the mount. From a git checkout, `setup-dev` links:

```text
instantbi/src/com/helicalinsight/instantbi  →  docker/instantbi/com/helicalinsight/instantbi
helicalbi/config                            →  server/hi-repository/System/InstantBI
server/hi-repository                        →  docker/hi/hi-repository
```

Packaging copies `helicalbi/config` into `hi/hi-repository/System/InstantBI` so the same compose mount works in the ZIP. Instant BI writes rotating logs next to `helical-insight.log` in `hi-repository/System/Logs`.

## Docker ZIP / release package

Packaging (`scripts/package-docker.sh`) copies source into:

```text
instantbi/com/helicalinsight/instantbi/
hi/hi-repository/System/InstantBI/   # copy of helicalbi/config
```

Unzip → `docker compose up -d`. First start runs `pip install` (can take several minutes).

## Git checkout

The app path is empty after clone. Link Instant BI once:

```bash
./scripts/setup-dev.sh          # Linux / macOS
.\scripts\setup-dev.ps1         # Windows
```

Then from `docker/`:

```bash
docker compose up -d
```

## What did not change

`postgres`, `hiee`, `nginx`, SSL, `.env`, and ports are the same as before.

Source / native run: [../../instantbi/README.md](../../instantbi/README.md)
