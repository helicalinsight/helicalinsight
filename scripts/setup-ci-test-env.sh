#!/usr/bin/env bash
# Prepare GitHub Actions / Linux CI paths expected by integration tests.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SERVER="$ROOT/server"
REPO_SRC="$SERVER/hi-repository"
SQL_SOURCE="$ROOT/db-dump/SampleTravelData.sql"

HI_ROOT="${HI_ROOT:-/home/helical/Performance/hi}"
DB_ROOT="$HI_ROOT/db"
SAMPLE_DB_PATH="$DB_ROOT/SampleTravelData"
DERBY_VERSION="${DERBY_VERSION:-10.17.1.0}"
DERBY_LIB="${DERBY_LIB:-/tmp/derby-lib}"
DERBY_SQL="${DERBY_SQL:-/tmp/SampleTravelData-derby.sql}"

echo "Preparing CI test environment under ${HI_ROOT}"

if [ ! -f "$SQL_SOURCE" ]; then
  echo "ERROR: SampleTravelData SQL dump not found at ${SQL_SOURCE}" >&2
  exit 1
fi

RUNNER_USER="$(id -un)"
RUNNER_GROUP="$(id -gn)"

if ! mkdir -p "$HI_ROOT" "$DB_ROOT" 2>/dev/null; then
  echo "[INFO] Creating ${HI_ROOT} with sudo (CI runner cannot write under /home/helical)"
  sudo mkdir -p "$HI_ROOT" "$DB_ROOT"
  sudo chown -R "${RUNNER_USER}:${RUNNER_GROUP}" /home/helical
fi
mkdir -p "$DERBY_LIB"

if [ -e "$HI_ROOT/hi-repository" ] && [ ! -L "$HI_ROOT/hi-repository" ]; then
  echo "ERROR: ${HI_ROOT}/hi-repository exists and is not a symlink" >&2
  exit 1
fi
ln -sfn "$REPO_SRC" "$HI_ROOT/hi-repository"

SETTING="$REPO_SRC/System/Admin/setting.xml"
GLOBAL_CONN="$REPO_SRC/System/Admin/globalConnections.xml"

if [ -f "$SETTING" ]; then
  sed -i.bak "s|<efwSolution>.*</efwSolution>|<efwSolution>${HI_ROOT}/hi-repository</efwSolution>|" "$SETTING"
  rm -f "${SETTING}.bak"
  echo "[OK]   Patched setting.xml (efwSolution -> ${HI_ROOT}/hi-repository)"
fi

if [ -f "$GLOBAL_CONN" ]; then
  sed -i.bak "s|<url>.*SampleTravelData</url>|<url>jdbc:derby:${SAMPLE_DB_PATH}</url>|" "$GLOBAL_CONN"
  rm -f "${GLOBAL_CONN}.bak"
  echo "[OK]   Patched globalConnections.xml (SampleTravelData Derby URL)"
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
cat > "$IJ_SCRIPT" <<EOF
CONNECT 'jdbc:derby:${SAMPLE_DB_PATH};create=true';
RUN '${DERBY_SQL}';
DISCONNECT;
EXIT;
EOF

echo "[INFO] Creating Derby database at ${SAMPLE_DB_PATH}"
java -cp "$DERBY_CP" org.apache.derby.tools.ij "$IJ_SCRIPT"
rm -f "$IJ_SCRIPT"

VERIFY_OUTPUT="$(
  java -cp "$DERBY_CP" org.apache.derby.tools.ij <<EOF 2>&1
CONNECT 'jdbc:derby:${SAMPLE_DB_PATH}';
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
