# Instant BI

Natural-language analytics service for Helical Insight. The product UI talks to this Python app (Flask + LangChain / LangGraph) for Instant BI chat and insights.

| Item | Value |
|------|-------|
| Source | [`helicalbi/`](helicalbi/) |
| Default port | `8000` |
| Docker (product stack) | Service `instantbi` in [`docker/docker-compose.yml`](../docker/docker-compose.yml) |

You do not need Instant BI running to use core reporting and dashboards. Start it when you work on Instant BI features or want that UI path available.

## Prerequisites

- Python **3.9+**
- For the full product in Docker: run repo [`scripts/setup-dev.sh`](../scripts/setup-dev.sh) / [`setup-dev.ps1`](../scripts/setup-dev.ps1) once so `ib/helicalbi` is linked to `docker/instantbi/helicalbi`

## Quick start (local)

```bash
cd ib/helicalbi
python -m venv .venv

# Linux / macOS
source .venv/bin/activate

# Windows PowerShell
# .\.venv\Scripts\Activate.ps1

pip install -r requirements.txt
cp .env.example .env          # PowerShell: Copy-Item .env.example .env
python app.py
```

Open http://localhost:8000

`.env.example` defaults to **stub** LLM mode (`HELICALBI_LLM_MODE=stub`) so you can develop without an API key. Set a real key and `HELICALBI_LLM_MODE=live` when you need a live model.

## Run with the product Docker stack

From the repository root (after setup-dev):

```bash
cd docker
docker compose up -d
```

Instant BI starts with the other services. **First** container start installs Python deps (`pip install`) and can take several minutes; later restarts are faster.

From a git checkout, `docker/instantbi/helicalbi` must exist (symlink/junction/copy created by setup-dev). See [docker/instantbi/README.md](../docker/instantbi/README.md).

## Tests

```bash
cd ib/helicalbi
pip install -r requirements.txt -r requirements-test.txt
pytest -m "not llm"
```

Details: [helicalbi/TESTING.md](helicalbi/TESTING.md) · Flow docs: [helicalbi/docs/README.md](helicalbi/docs/README.md)

## Related

- Backend Instant BI API notes: [server/instant/docs/InstantBIController-API.md](../server/instant/docs/InstantBIController-API.md)
- Root setup: [../README.md](../README.md)
