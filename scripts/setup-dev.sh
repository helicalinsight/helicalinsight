#!/usr/bin/env bash
# Prepare local development directories and patch hi-repository paths.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SERVER="$ROOT/server"
REPO="$SERVER/hi-repository"
DB="$SERVER/db"
SETTING="$REPO/System/Admin/setting.xml"
GLOBAL_CONN="$REPO/System/Admin/globalConnections.xml"

# shellcheck source=setup-dev.helpers.sh
source "${SCRIPT_DIR}/setup-dev.helpers.sh"

"${SCRIPT_DIR}/print-banner.sh"
echo ""
echo "Development setup"
echo "Repository root: $ROOT"

mkdir -p "$DB" "$REPO/System/Logs"

REPO_ABS="$(cd "$REPO" && pwd)"
DB_ABS="$(cd "$DB" && pwd)"

if [ -f "$SETTING" ]; then
  if grep -q '\${INSTALL_PATH}' "$SETTING"; then
    sed -i.bak "s|<efwSolution>.*</efwSolution>|<efwSolution>${REPO_ABS}</efwSolution>|" "$SETTING"
    sed -i.bak "s|<BaseUrl>.*</BaseUrl>|<BaseUrl>http://localhost:8080/hi-ee/</BaseUrl>|" "$SETTING"
    rm -f "${SETTING}.bak"
    echo "[OK]   Patched setting.xml (efwSolution, BaseUrl)"
  else
    echo "[SKIP] setting.xml already has absolute paths"
  fi
fi

if [ -f "$GLOBAL_CONN" ]; then
  if grep -q 'SampleTravelData' "$GLOBAL_CONN"; then
    sed -i.bak "s|<url>.*SampleTravelData</url>|<url>jdbc:derby:${DB_ABS}/SampleTravelData</url>|" "$GLOBAL_CONN"
    rm -f "${GLOBAL_CONN}.bak"
    echo "[OK]   Patched globalConnections.xml (SampleTravelData)"
  fi
fi


if [ ! -f "$ROOT/.env" ] && [ -f "$ROOT/.env.example" ]; then
  cp "$ROOT/.env.example" "$ROOT/.env"
  echo "[OK]   Created .env from .env.example"
fi

DOCKER_ENV_EXAMPLE="$ROOT/docker/.env.example"
DOCKER_ENV="$ROOT/docker/.env"
if [ ! -f "$DOCKER_ENV" ] && [ -f "$DOCKER_ENV_EXAMPLE" ]; then
  cp "$DOCKER_ENV_EXAMPLE" "$DOCKER_ENV"
  echo "[OK]   Created docker/.env from docker/.env.example"
fi

# Link Instant BI into the shared Docker layout (same path the package uses)
INSTANTBI_LINK="$ROOT/docker/instantbi/helicalbi"
INSTANTBI_SRC="$ROOT/ib/helicalbi"
mkdir -p "$ROOT/docker/instantbi"
if [ -d "$INSTANTBI_SRC" ] && [ ! -e "$INSTANTBI_LINK" ]; then
  ln -sfn "$INSTANTBI_SRC" "$INSTANTBI_LINK"
  echo "[OK]   Linked docker/instantbi/helicalbi → ib/helicalbi"
elif [ -e "$INSTANTBI_LINK" ]; then
  echo "[SKIP] docker/instantbi/helicalbi already present"
fi

echo ""
echo "Setup complete. See README.md for full paths."
echo ""
echo "Recommended (full stack — first start can take a few minutes):"
echo "  cd docker && docker compose up -d"
echo "  # Open https://localhost  (login: hiadmin / hiadmin)"
echo ""
echo "Per component:"
echo "  Backend:    cd server && mvn clean package -DskipTests"
echo "              # Deploy presentation/target/hi-ee-7.0.0.war as \$CATALINA_HOME/webapps/hi-ee.war"
echo "  Frontend:   cd client && npm ci --legacy-peer-deps && npm run start18"
echo "  Instant BI: cd ib/helicalbi && pip install -r requirements.txt && python app.py"
echo ""
echo "Build backend from source in Docker:"
echo "  docker compose -f docker-compose.dev.yml up --build"
