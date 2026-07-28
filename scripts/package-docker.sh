#!/usr/bin/env bash
# Build a runnable Docker ZIP for end users (same layout as docker/ + Instant BI + WAR).
#
# Prerequisites:
#   - server/presentation/target/hi-ee-*.war  (mvn package)
#   - Optional: frontend already baked into the WAR via scripts/copy-frontend-to-webapp.sh
#
# Usage:
#   ./scripts/package-docker.sh [version]
#   VERSION=7.0.0 ./scripts/package-docker.sh
#
# Output:
#   dist/helicalinsight-<version>-docker.zip
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
VERSION="${1:-${VERSION:-}}"
if [ -z "$VERSION" ]; then
  if git -C "$ROOT" describe --tags --exact-match >/dev/null 2>&1; then
    VERSION="$(git -C "$ROOT" describe --tags --exact-match)"
  else
    VERSION="$(git -C "$ROOT" describe --tags --always 2>/dev/null || echo "dev")"
  fi
fi
# Strip leading v for folder names if present
VERSION_CLEAN="${VERSION#v}"

DIST="$ROOT/dist"
PKG_NAME="helicalinsight-${VERSION_CLEAN}-docker"
PKG_DIR="$DIST/$PKG_NAME"
ZIP_PATH="$DIST/${PKG_NAME}.zip"

WAR_SRC="$(ls -1 "$ROOT"/server/presentation/target/hi-ee-*.war 2>/dev/null | head -1 || true)"
if [ -z "$WAR_SRC" ] || [ ! -f "$WAR_SRC" ]; then
  echo "error: WAR not found at server/presentation/target/hi-ee-*.war" >&2
  echo "       Run: mvn -f server/pom.xml clean package -DskipTests" >&2
  exit 1
fi

REPO_SRC="$ROOT/server/hi-repository"
if [ ! -d "$REPO_SRC" ]; then
  echo "error: missing $REPO_SRC" >&2
  exit 1
fi

INSTANTBI_SRC="$ROOT/ib/helicalbi"
if [ ! -d "$INSTANTBI_SRC" ]; then
  echo "error: missing $INSTANTBI_SRC (Instant BI source)" >&2
  exit 1
fi

DOCKER_SRC="$ROOT/docker"
if [ ! -f "$DOCKER_SRC/docker-compose.yml" ]; then
  echo "error: missing $DOCKER_SRC/docker-compose.yml" >&2
  exit 1
fi

echo "Packaging Docker ZIP"
echo "  version: $VERSION_CLEAN"
echo "  war:     $WAR_SRC"
echo "  out:     $ZIP_PATH"

rm -rf "$PKG_DIR"
mkdir -p "$PKG_DIR/hi/tomcat/logs" "$PKG_DIR/hi/hi-ee" "$PKG_DIR/instantbi" "$DIST"

# Compose + env + docs + runtime config
cp -a "$DOCKER_SRC/docker-compose.yml" "$PKG_DIR/"
cp -a "$DOCKER_SRC/.env.example" "$PKG_DIR/"
cp -a "$DOCKER_SRC/.env.example" "$PKG_DIR/.env"
cp -a "$DOCKER_SRC/config" "$PKG_DIR/"
cp -a "$DOCKER_SRC/readme" "$PKG_DIR/"
if [ -f "$DOCKER_SRC/Dockerfile" ]; then
  cp -a "$DOCKER_SRC/Dockerfile" "$PKG_DIR/"
fi
if [ -f "$DOCKER_SRC/.dockerignore" ]; then
  cp -a "$DOCKER_SRC/.dockerignore" "$PKG_DIR/"
fi

# Sample DB layout (exclude runtime lock files)
if [ -d "$DOCKER_SRC/hi/db" ]; then
  mkdir -p "$PKG_DIR/hi/db"
  if command -v rsync >/dev/null 2>&1; then
    rsync -a \
      --exclude '*.lck' \
      --exclude '*.zip' \
      "$DOCKER_SRC/hi/db/" "$PKG_DIR/hi/db/"
  else
    cp -a "$DOCKER_SRC/hi/db/." "$PKG_DIR/hi/db/"
    find "$PKG_DIR/hi/db" -name '*.lck' -delete 2>/dev/null || true
  fi
else
  mkdir -p "$PKG_DIR/hi/db"
fi

# hi-repository (entrypoint rewrites INSTALL_PATH / BaseUrl at container start)
if command -v rsync >/dev/null 2>&1; then
  rsync -a \
    --exclude 'System/Logs/*' \
    --exclude '*.log' \
    "$REPO_SRC/" "$PKG_DIR/hi/hi-repository/"
else
  cp -a "$REPO_SRC" "$PKG_DIR/hi/hi-repository"
  find "$PKG_DIR/hi/hi-repository/System/Logs" -type f -delete 2>/dev/null || true
fi
mkdir -p "$PKG_DIR/hi/hi-repository/System/Logs"

# WAR must be named hi-ee.war for compose volume mount / context path
cp -a "$WAR_SRC" "$PKG_DIR/hi/hi-ee.war"

# Instant BI (compose mounts ./instantbi/helicalbi)
if command -v rsync >/dev/null 2>&1; then
  rsync -a \
    --exclude '.venv/' \
    --exclude '__pycache__/' \
    --exclude '.pytest_cache/' \
    --exclude '.deepeval/' \
    --exclude 'tests/' \
    --exclude '.env' \
    "$INSTANTBI_SRC/" "$PKG_DIR/instantbi/helicalbi/"
else
  cp -a "$INSTANTBI_SRC" "$PKG_DIR/instantbi/helicalbi"
  rm -rf "$PKG_DIR/instantbi/helicalbi/.venv" \
         "$PKG_DIR/instantbi/helicalbi/__pycache__" \
         "$PKG_DIR/instantbi/helicalbi/.pytest_cache" \
         "$PKG_DIR/instantbi/helicalbi/tests" 2>/dev/null || true
fi
if [ -f "$INSTANTBI_SRC/.env.example" ]; then
  cp -a "$INSTANTBI_SRC/.env.example" "$PKG_DIR/instantbi/helicalbi/.env.example"
fi

# Lightweight package README at zip root
cat > "$PKG_DIR/README.md" <<EOF
# Helical Insight Docker package (${VERSION_CLEAN})

## Start

\`\`\`bash
cp .env.example .env   # already done if you use the included .env
docker compose up -d
\`\`\`

Open **https://localhost** — login: \`hiadmin\` / \`hiadmin\`

Same \`docker/\` layout as the repo. First start builds Instant BI once if needed; later starts are fast.

Full guide: \`readme/readme.md\`
EOF

# Zip from dist/ so archive root is helicalinsight-<ver>-docker/
rm -f "$ZIP_PATH"
(
  cd "$DIST"
  if command -v zip >/dev/null 2>&1; then
    zip -qr "${PKG_NAME}.zip" "$PKG_NAME"
  else
    # Fallback when zip is unavailable (produces .zip via tar+gzip-compatible tools on GNU)
    tar -a -cf "${PKG_NAME}.zip" "$PKG_NAME"
  fi
)

echo "Created $ZIP_PATH ($(du -h "$ZIP_PATH" | awk '{print $1}'))"
echo "Contents preview:"
find "$PKG_DIR" -maxdepth 3 -type d | head -40
