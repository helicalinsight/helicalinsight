#!/usr/bin/env bash
# Sync client/build SPA assets into server/presentation/src/main/webapp.
# Preserves server-only paths (WEB-INF/, META-INF/, app.html, hdi.licence).
# Expects client/build/ to already exist (run npm run build in client/ first).
set -eu
(set -o pipefail) 2>/dev/null || true

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD="${ROOT}/client/build"
WEBAPP="${ROOT}/server/presentation/src/main/webapp"

if [[ ! -d "${BUILD}" ]]; then
  echo "error: ${BUILD} not found; run 'npm run build' in client/ first" >&2
  exit 1
fi

if [[ ! -d "${WEBAPP}" ]]; then
  echo "error: ${WEBAPP} not found" >&2
  exit 1
fi

sync_dir() {
  local name="$1"
  local src="${BUILD}/${name}"
  local dest="${WEBAPP}/${name}"
  if [[ ! -d "${src}" ]]; then
    echo "skip: ${name}/ (not in build)"
    return 0
  fi
  mkdir -p "${dest}"
  if command -v rsync >/dev/null 2>&1; then
    rsync -a --delete "${src}/" "${dest}/"
  else
    rm -rf "${dest}"
    mkdir -p "${dest}"
    cp -a "${src}/." "${dest}/"
  fi
  echo "synced: ${name}/"
}

copy_file() {
  local name="$1"
  local src="${BUILD}/${name}"
  if [[ ! -f "${src}" ]]; then
    echo "skip: ${name} (not in build)"
    return 0
  fi
  cp -f "${src}" "${WEBAPP}/${name}"
  echo "copied: ${name}"
}

# Chunk dirs: replace entirely so stale hashed chunks are removed
sync_dir js
sync_dir css

# Static assets from public/ (merged into build)
sync_dir fonts
sync_dir images
sync_dir jwt
sync_dir sso
sync_dir direct

copy_file index.html
copy_file asset-manifest.json
copy_file favicon.ico

echo "Frontend assets copied to ${WEBAPP}"
