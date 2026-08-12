#!/usr/bin/env bash
# Load CI tooling versions from .github/ci-tool-versions.env
# Outputs for GitHub Actions:
#   java_versions / node_versions           — JSON arrays for strategy.matrix
#   java_release_version / node_release_version — packaging versions
#   java_distribution
#
# Version lists may be comma- or space-separated, quoted or unquoted:
#   JAVA_VERSIONS=25,26
#   NODE_VERSIONS="20 22"
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
VERSIONS_FILE="$ROOT/.github/ci-tool-versions.env"

if [[ ! -f "$VERSIONS_FILE" ]]; then
  echo "error: missing $VERSIONS_FILE" >&2
  exit 1
fi

# Parse KEY=VALUE without `source` — unquoted spaces must not become extra commands.
read_kv() {
  local wanted="$1"
  local line key value
  while IFS= read -r line || [[ -n "$line" ]]; do
    line="${line//$'\r'/}"
    [[ "$line" =~ ^[[:space:]]*# ]] && continue
    [[ "$line" =~ ^[[:space:]]*$ ]] && continue
    key="${line%%=*}"
    key="${key%"${key##*[![:space:]]}"}"
    key="${key#"${key%%[![:space:]]*}"}"
    [[ "$key" == "$wanted" ]] || continue
    value="${line#*=}"
    value="${value#"${value%%[![:space:]]*}"}"
    value="${value%"${value##*[![:space:]]}"}"
    if [[ "$value" == \"*\" ]]; then
      value="${value:1:${#value}-2}"
    elif [[ "$value" == \'*\' ]]; then
      value="${value:1:${#value}-2}"
    fi
    printf '%s' "$value"
    return 0
  done < "$VERSIONS_FILE"
  return 1
}

to_json_array() {
  local raw="${1//,/ }"
  local json='['
  local first=1
  local v
  # shellcheck disable=SC2086
  for v in $raw; do
    [[ -z "$v" ]] && continue
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

write_output() {
  local name="$1"
  local value="$2"
  {
    echo "${name}<<EOF"
    echo "${value}"
    echo "EOF"
  } >> "$GITHUB_OUTPUT"
}

JAVA_VERSIONS="$(read_kv JAVA_VERSIONS || true)"
JAVA_RELEASE_VERSION="$(read_kv JAVA_RELEASE_VERSION || true)"
JAVA_DISTRIBUTION="$(read_kv JAVA_DISTRIBUTION || true)"
NODE_VERSIONS="$(read_kv NODE_VERSIONS || true)"
NODE_RELEASE_VERSION="$(read_kv NODE_RELEASE_VERSION || true)"
# Back-compat with a single NODE_VERSION=20
if [[ -z "$NODE_VERSIONS" ]]; then
  NODE_VERSIONS="$(read_kv NODE_VERSION || true)"
fi
if [[ -z "$NODE_RELEASE_VERSION" ]]; then
  NODE_RELEASE_VERSION="$(read_kv NODE_VERSION || true)"
fi

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

if [[ "$JAVA_VERSIONS_JSON" == "[]" || "$NODE_VERSIONS_JSON" == "[]" ]]; then
  echo "error: version lists resolved to empty arrays" >&2
  exit 1
fi

echo "Resolved tooling versions: Java matrix=${JAVA_VERSIONS_JSON} release=${JAVA_RELEASE_VERSION} (${JAVA_DISTRIBUTION}) Node matrix=${NODE_VERSIONS_JSON} release=${NODE_RELEASE_VERSION}"

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  write_output java_versions "${JAVA_VERSIONS_JSON}"
  write_output java_release_version "${JAVA_RELEASE_VERSION}"
  write_output java_distribution "${JAVA_DISTRIBUTION}"
  write_output node_versions "${NODE_VERSIONS_JSON}"
  write_output node_release_version "${NODE_RELEASE_VERSION}"
fi
