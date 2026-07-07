#!/usr/bin/env bash
# Check prerequisites for Helical Insight local development.
set -euo pipefail

ERRORS=0

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
"${SCRIPT_DIR}/print-banner.sh"
echo ""

check_cmd() {
  local name="$1"
  local cmd="$2"
  local hint="$3"
  if command -v "$cmd" >/dev/null 2>&1; then
    echo "[OK]   $name: $($cmd --version 2>&1 | head -1)"
  else
    echo "[FAIL] $name not found. $hint"
    ERRORS=$((ERRORS + 1))
  fi
}

echo "Prerequisite check"
echo "====================================="

check_cmd "Java" "java" "Install JDK 21+ and set JAVA_HOME."
check_cmd "Maven" "mvn" "Install Maven 3.8+ (https://maven.apache.org/)."
check_cmd "Node.js" "node" "Install Node.js 18 LTS (https://nodejs.org/)."
check_cmd "npm" "npm" "npm ships with Node.js; reinstall Node if missing."

if command -v docker >/dev/null 2>&1; then
  echo "[OK]   Docker: $(docker --version)"
else
  echo "[WARN] Docker not found (optional for docker-compose.dev.yml)."
fi

NODE_MAJOR=""
if command -v node >/dev/null 2>&1; then
  NODE_MAJOR=$(node -p "process.versions.node.split('.')[0]")
  if [ "$NODE_MAJOR" -ge 18 ]; then
    echo "[INFO] Node $NODE_MAJOR detected — use 'npm run start18' if OpenSSL errors occur."
  fi
fi

echo ""
if [ "$ERRORS" -gt 0 ]; then
  echo "$ERRORS required tool(s) missing. Fix the items above before continuing."
  exit 1
fi

echo "All required tools found. Next steps:"
echo "  ./scripts/setup-dev.sh"
echo "  cd server && mvn clean package -DskipTests"
echo "  cd client && npm ci --legacy-peer-deps && npm run start18"
