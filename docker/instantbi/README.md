# Instant BI (product Docker layout)

Compose builds Instant BI from:

```text
docker/instantbi/helicalbi/Dockerfile
```

Python dependencies are baked into the image `helicalinsight/instantbi:local`, so **later starts do not re-run `pip install`**.

## Docker ZIP / release package

Packaging (`scripts/package-docker.sh`) copies `ib/helicalbi` here (including `Dockerfile`).  
Unzip → `docker compose up -d` (Compose builds the image on first run if needed).

## Git checkout

This path is empty after clone. Link Instant BI once:

```bash
./scripts/setup-dev.sh          # Linux / macOS
.\scripts\setup-dev.ps1         # Windows
```

```text
ib/helicalbi  →  docker/instantbi/helicalbi
```

Then from `docker/`:

```bash
docker compose up -d
```

Rebuild Instant BI only when its code or `requirements.txt` change:

```bash
docker compose up -d --build instantbi
```

## What did not change

`postgres`, `hiee`, `nginx`, volumes, SSL, `.env`, and ports are the same as before. Only Instant BI switched from “pip on every start” to “build once, run fast”.

Source / native run: [../../ib/README.md](../../ib/README.md)
