# Airgap / offline Docker images

Use this when the client machine has **no internet** (cannot pull from Docker Hub or download Chrome). Build images on a connected host, export them as tars, ship to the client, load offline, and start with the same `docker-compose.yml`.

The fat `hiee` image is built/pushed as **`hiee/helicalinsight:nitrogen-j25t11-full`**. Import retags it to **`hiee/helicalinsight:nitrogen-j25t11`** so Compose defaults work unchanged.

## Image tags

| Service | Export / Docker Hub | After import (Compose) |
|---------|---------------------|-------------------------|
| hiee | `hiee/helicalinsight:nitrogen-j25t11-full` | `hiee/helicalinsight:nitrogen-j25t11` |
| postgres | `postgres:15.8-alpine3.20` | same |
| nginx | `nginx:alpine` | same |
| instantbi | `python:3.13-slim` | same |

## 1. Prepare images (connected host)

Build / push the full airgap `hiee` image:

```bash
cd docker
docker build -f Dockerfile.airgap -t hiee/helicalinsight:nitrogen-j25t11-full .
docker push hiee/helicalinsight:nitrogen-j25t11-full
```

Pull or build Postgres, Nginx, and Instant BI under the Compose tags above.

## 2. Export (connected host)

Saves **already-local** images only — no build or pull. Output goes under the **current working directory**:

```bash
# from repository root
chmod +x scripts/export-airgap-images.sh scripts/import-airgap-images.sh
./scripts/export-airgap-images.sh
```

Writes:

- `./airgap/*.tar` (includes the `-full` hiee image)
- `./airgap/import-airgap-images.sh` (copied into the folder for the client)
- `./airgap/image-tags.env`
- `./helicalinsight-airgap-images.zip` (single archive of that folder)

Fails if any expected image is missing.

Ship to the client:

- `helicalinsight-airgap-images.zip` (or the `airgap/` folder)
- The usual Docker package (compose, `.env.example`, `config/`, `hi/`, Instant BI app files)

## 3. Import + start (airgapped client)

1. Extract the archive and load images (retags `-full` → Compose default):

   ```bash
   unzip helicalinsight-airgap-images.zip
   cd airgap
   chmod +x import-airgap-images.sh
   ./import-airgap-images.sh
   ```

2. In the Helical Insight Docker package folder, copy `.env.example` to `.env` if needed. **Leave `HIEE_IMAGE` at the default** (`hiee/helicalinsight:nitrogen-j25t11`).

3. Start as usual:

   ```bash
   docker compose up -d
   ```

## What the airgap `hiee` image includes

- Base: `tomcat:11.0.25-jdk25-temurin-noble` (Ubuntu 24.04, full Temurin JDK 25, Tomcat 11)
- Chrome-for-Testing + chromedriver from `storage.googleapis.com/chrome-for-testing-public` for the image arch (`linux64` / `linux-arm64`), stored as fixed `/tmp/chrome.zip` and `/tmp/chromedriver.zip`. On unsupported arches (e.g. `ppc64le`) Chrome bake is skipped with a warning.
- Chrome runtime libraries
- Baked `/entrypoint.airgap.sh` (slim Compose mount of `entrypoint.sh` is unused)

On first start, entrypoint extracts Chrome into the bind-mounted `hi-repository` (no network).

See also [build.md](build.md) for the slim image (`hiee/helicalinsight:nitrogen-j25t11`).
