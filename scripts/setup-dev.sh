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
    sed -i.bak "s|<BaseUrl>.*</BaseUrl>|<BaseUrl>http://localhost:8080/hi-ee/hi.html</BaseUrl>|" "$SETTING"
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

PERSISTENCE_PATCHED=0
while IFS= read -r file; do
  if patch_persistence_placeholders "$file"; then
    PERSISTENCE_PATCHED=$((PERSISTENCE_PATCHED + 1))
  fi
done < <(collect_persistence_xml_files "$SERVER")

if [ "$PERSISTENCE_PATCHED" -gt 0 ]; then
  echo "[OK]   Normalized persistence.xml placeholders in ${PERSISTENCE_PATCHED} file(s)"
else
  echo "[SKIP] persistence.xml already uses Maven placeholders"
fi


if [ ! -f "$ROOT/.env" ] && [ -f "$ROOT/.env.example" ]; then
  cp "$ROOT/.env.example" "$ROOT/.env"
  echo "[OK]   Created .env from .env.example"
fi

echo ""
echo "Setup complete. Build and run:"
echo "  cd server && mvn clean package -DskipTests"
echo "  # Deploy presentation/target/hi-ce-7.0.0.war as \$CATALINA_HOME/webapps/hi-ee.war"
echo "  cd client && npm ci --legacy-peer-deps && npm run start18"
echo ""
echo "Or use Docker:"
echo "  docker compose -f docker-compose.dev.yml up --build"
