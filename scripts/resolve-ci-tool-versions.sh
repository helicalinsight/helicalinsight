#!/usr/bin/env bash
# Load CI tooling versions from .github/ci-tool-versions.env
# Outputs for GitHub Actions:
#   java_versions / node_versions           — JSON arrays for strategy.matrix
#   java_release_version / node_release_version — packaging versions
#   java_distribution
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
VERSIONS_FILE="$ROOT/.github/ci-tool-versions.env"

if [[ ! -f "$VERSIONS_FILE" ]]; then
  echo "error: missing $VERSIONS_FILE" >&2
  exit 1
fi

# Strip CR so Windows-checked-out env files still parse on Linux runners
# shellcheck disable=SC1090
source <(tr -d '\r' < "$VERSIONS_FILE")

to_json_array() {
  local json='['
  local first=1
  local v
  for v in $1; do
    if [[ $first -eq 1 ]]; then
      first=0
    else
      json+=','
    fi
    json+="\"${v}\""
  done
  json+=']'
  printf '%s' "$json"
}

JAVA_VERSIONS="${JAVA_VERSIONS//$'\r'/}"
JAVA_RELEASE_VERSION="${JAVA_RELEASE_VERSION//$'\r'/}"
JAVA_DISTRIBUTION="${JAVA_DISTRIBUTION//$'\r'/}"
NODE_VERSIONS="${NODE_VERSIONS:-${NODE_VERSION:-}}"
NODE_VERSIONS="${NODE_VERSIONS//$'\r'/}"
NODE_RELEASE_VERSION="${NODE_RELEASE_VERSION:-${NODE_VERSION:-}}"
NODE_RELEASE_VERSION="${NODE_RELEASE_VERSION//$'\r'/}"

if [[ -z "${JAVA_VERSIONS}" || -z "${JAVA_RELEASE_VERSION}" || -z "${JAVA_DISTRIBUTION}" ]]; then
  echo "error: JAVA_VERSIONS, JAVA_RELEASE_VERSION, and JAVA_DISTRIBUTION must be set in $VERSIONS_FILE" >&2
  exit 1
fi
if [[ -z "${NODE_VERSIONS}" || -z "${NODE_RELEASE_VERSION}" ]]; then
  echo "error: NODE_VERSIONS and NODE_RELEASE_VERSION must be set in $VERSIONS_FILE" >&2
  exit 1
fi

JAVA_VERSIONS_JSON="$(to_json_array "${JAVA_VERSIONS}")"
NODE_VERSIONS_JSON="$(to_json_array "${NODE_VERSIONS}")"

echo "Resolved tooling versions: Java matrix=${JAVA_VERSIONS_JSON} release=${JAVA_RELEASE_VERSION} (${JAVA_DISTRIBUTION}) Node matrix=${NODE_VERSIONS_JSON} release=${NODE_RELEASE_VERSION}"

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  {
    echo "java_versions=${JAVA_VERSIONS_JSON}"
    echo "java_release_version=${JAVA_RELEASE_VERSION}"
    echo "java_distribution=${JAVA_DISTRIBUTION}"
    echo "node_versions=${NODE_VERSIONS_JSON}"
    echo "node_release_version=${NODE_RELEASE_VERSION}"
  } >> "$GITHUB_OUTPUT"
fi
