#!/usr/bin/env bash
# Prepare filesystem + SampleTravelData Derby DB for presentation integration tests.
#
# Usage:
#   Local (default layout under server/db):
#     ./scripts/setup-test-env.sh
#     cd server && mvn test
#
#   CI (legacy /home/helical/Performance layout used by many tests):
#     HI_ROOT=/home/helical/Performance/hi ./scripts/setup-test-env.sh
#     cd server && mvn test -Denv=ci
#
# Presentation test resources (application-context.xml, persistence.xml, quartz.properties)
# are Maven-filtered from presentation/pom.xml profiles � no manual edits needed there.
set -eu
(set -o pipefail) 2>/dev/null || true

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SERVER="$ROOT/server"
REPO_SRC="$SERVER/hi-repository"
SQL_SOURCE="$ROOT/db-dump/SampleTravelData.sql"

# Default: local layout that matches the Maven "dev" profile.
# CI sets HI_ROOT=/home/helical/Performance/hi (matches the "ci" profile).
HI_ROOT="${HI_ROOT:-$SERVER}"
DB_ROOT="${DB_ROOT:-$HI_ROOT/db}"
SAMPLE_DB_PATH="$DB_ROOT/SampleTravelData"
REPO_LINK="$HI_ROOT/hi-repository"

DERBY_VERSION="${DERBY_VERSION:-10.17.1.0}"
DERBY_LIB="${DERBY_LIB:-/tmp/derby-lib}"
DERBY_SQL="${DERBY_SQL:-/tmp/SampleTravelData-derby.sql}"

echo "Preparing test environment"
echo "  HI_ROOT=$HI_ROOT"
echo "  DB_ROOT=$DB_ROOT"

if [ ! -f "$SQL_SOURCE" ]; then
  echo "ERROR: SampleTravelData SQL dump not found at ${SQL_SOURCE}" >&2
  exit 1
fi

is_under_home_helical=false
case "$HI_ROOT" in
  /home/helical/*) is_under_home_helical=true ;;
esac

RUNNER_USER="$(id -un 2>/dev/null || echo "")"
RUNNER_GROUP="$(id -gn 2>/dev/null || echo "")"

ensure_dir() {
  local dir="$1"
  if mkdir -p "$dir" 2>/dev/null; then
    return 0
  fi
  if [ "$is_under_home_helical" = true ] && command -v sudo >/dev/null 2>&1; then
    echo "[INFO] Creating ${dir} with sudo"
    sudo mkdir -p "$dir"
    sudo chown -R "${RUNNER_USER}:${RUNNER_GROUP}" /home/helical
    return 0
  fi
  echo "ERROR: cannot create directory ${dir}" >&2
  exit 1
}

ensure_dir "$DB_ROOT"
ensure_dir "$DERBY_LIB"
ensure_dir "$HI_ROOT"

# Point HI_ROOT/hi-repository at the repo copy (CI layout) unless already the same path.
if [ "$(cd "$HI_ROOT" && pwd)" != "$(cd "$SERVER" && pwd)" ]; then
  if [ -e "$REPO_LINK" ] && [ ! -L "$REPO_LINK" ]; then
    echo "ERROR: ${REPO_LINK} exists and is not a symlink" >&2
    exit 1
  fi
  ln -sfn "$REPO_SRC" "$REPO_LINK"
  echo "[OK]   Symlinked ${REPO_LINK} -> ${REPO_SRC}"
else
  REPO_LINK="$REPO_SRC"
fi

# Fixture paths used by some integration tests
mkdir -p \
  "$REPO_SRC/System/Logs" \
  "$REPO_SRC/System/Temp" \
  "$REPO_SRC/System/Reports/ExportTemplates" \
  "$DB_ROOT"
SETTING="$REPO_SRC/System/Admin/setting.xml"
GLOBAL_CONN="$REPO_SRC/System/Admin/globalConnections.xml"
REPO_ABS="$(cd "$REPO_LINK" && pwd)"

if [ -f "$SETTING" ]; then
  sed -i.bak "s|<efwSolution>.*</efwSolution>|<efwSolution>${REPO_ABS}</efwSolution>|" "$SETTING"
  rm -f "${SETTING}.bak"
  echo "[OK]   Patched setting.xml (efwSolution -> ${REPO_ABS})"
fi

if [ -f "$GLOBAL_CONN" ]; then
  # Derby URLs use forward slashes even on Windows.
  SAMPLE_JDBC_PATH="$(printf '%s' "$SAMPLE_DB_PATH" | sed 's|\\|/|g')"
  sed -i.bak "s|<url>.*SampleTravelData</url>|<url>jdbc:derby:${SAMPLE_JDBC_PATH}</url>|" "$GLOBAL_CONN"
  rm -f "${GLOBAL_CONN}.bak"
  echo "[OK]   Patched globalConnections.xml (SampleTravelData)"
fi

echo "[INFO] Converting MySQL dump to Derby-compatible SQL"
sed -E \
  -e 's/\r$//' \
  -e '/^CREATE DATABASE /d' \
  -e '/^USE /d' \
  -e 's/CREATE TABLE IF NOT EXISTS/CREATE TABLE/g' \
  -e 's/\)ENGINE=InnoDB( DEFAULT CHARSET=latin1)?;/);/g' \
  -e '/^ENGINE=InnoDB/d' \
  -e 's/^\)[[:space:]]*$/);/' \
  -e 's/[[:space:]]+DEFAULT CHARSET=latin1//g' \
  -e 's/\bdatetime\b/TIMESTAMP/g' \
  "$SQL_SOURCE" > "$DERBY_SQL"

echo "[INFO] Resolving Derby ${DERBY_VERSION} jars"
for artifact in derby derbyshared derbytools; do
  mvn -q org.apache.maven.plugins:maven-dependency-plugin:3.6.1:copy \
    -Dartifact="org.apache.derby:${artifact}:${DERBY_VERSION}" \
    -DoutputDirectory="$DERBY_LIB" \
    -f "$SERVER/pom.xml"
done

DERBY_CP="$(find "$DERBY_LIB" -maxdepth 1 -name '*.jar' -print | sort | paste -sd: -)"
if [ -z "$DERBY_CP" ]; then
  echo "ERROR: No Derby jars found in ${DERBY_LIB}" >&2
  exit 1
fi

if [ -d "$SAMPLE_DB_PATH" ]; then
  echo "[INFO] Removing existing Derby database at ${SAMPLE_DB_PATH}"
  rm -rf "$SAMPLE_DB_PATH"
fi

IJ_SCRIPT="$(mktemp)"
# Normalize path separators for the JDBC URL
SAMPLE_JDBC_PATH="$(printf '%s' "$SAMPLE_DB_PATH" | sed 's|\\|/|g')"
cat > "$IJ_SCRIPT" <<EOF
CONNECT 'jdbc:derby:${SAMPLE_JDBC_PATH};create=true';
RUN '${DERBY_SQL}';
DISCONNECT;
EXIT;
EOF

echo "[INFO] Creating Derby database at ${SAMPLE_DB_PATH}"
java -cp "$DERBY_CP" org.apache.derby.tools.ij "$IJ_SCRIPT"
rm -f "$IJ_SCRIPT"

VERIFY_OUTPUT="$(
  java -cp "$DERBY_CP" org.apache.derby.tools.ij <<EOF 2>&1
CONNECT 'jdbc:derby:${SAMPLE_JDBC_PATH}';
SELECT TABLENAME FROM SYS.SYSTABLES
  WHERE TABLETYPE = 'T'
    AND TABLENAME IN ('EMPLOYEE_DETAILS', 'GEO_CORDINATES', 'MEETING_DETAILS', 'TRAVEL_DETAILS', 'DIMDATE');
DISCONNECT;
EXIT;
EOF
)"

for table in EMPLOYEE_DETAILS GEO_CORDINATES MEETING_DETAILS TRAVEL_DETAILS DIMDATE; do
  if ! echo "$VERIFY_OUTPUT" | grep -qi "$table"; then
    echo "ERROR: SampleTravelData is missing table ${table}" >&2
    echo "$VERIFY_OUTPUT" >&2
    exit 1
  fi
done

echo "[OK]   SampleTravelData Derby database ready (5 tables loaded)"
echo ""
echo "Next:"
if [ "$is_under_home_helical" = true ]; then
  echo "  cd server && mvn test -Denv=ci"
else
  echo "  cd server && mvn test"
fi