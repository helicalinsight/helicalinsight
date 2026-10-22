#!/usr/bin/env bash
# Load airgap image tars from this script's directory (or an optional path).
# Retags the full hiee image to the Compose default so docker-compose.yml works
# unchanged.
#
# Usage (on the offline client, after extracting the airgap archive):
#   ./import-airgap-images.sh
#
# Or from the repo (dev):
#   ./scripts/import-airgap-images.sh /path/to/tar-dir
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# Default: same folder as this script (export ships the script next to the .tar files)
TAR_DIR="${1:-${AIRGAP_TAR_DIR:-$SCRIPT_DIR}}"

if [ ! -d "$TAR_DIR" ]; then
  echo "error: tar directory not found: $TAR_DIR" >&2
  echo "usage: $0 [path-to-tar-dir]" >&2
  exit 1
fi

HIEE_FULL_IMAGE="${HIEE_FULL_IMAGE:-hiee/helicalinsight:nitrogen-j25t11-full}"
HIEE_IMAGE="${HIEE_IMAGE:-hiee/helicalinsight:nitrogen-j25t11}"

# Prefer tags recorded at export time
if [ -f "$TAR_DIR/image-tags.env" ]; then
  # shellcheck disable=SC1091
  set -a
  # shellcheck source=/dev/null
  . "$TAR_DIR/image-tags.env"
  set +a
fi

shopt -s nullglob
tars=("$TAR_DIR"/*.tar)
if [ ${#tars[@]} -eq 0 ]; then
  echo "error: no .tar files in $TAR_DIR" >&2
  exit 1
fi

echo "Airgap import from $TAR_DIR"
echo

for tar in "${tars[@]}"; do
  echo "==> docker load -i $tar"
  docker load -i "$tar"
done

echo
echo "==> Retagging $HIEE_FULL_IMAGE -> $HIEE_IMAGE (Compose default)"
if ! docker image inspect "$HIEE_FULL_IMAGE" >/dev/null 2>&1; then
  echo "error: expected full image not found after load: $HIEE_FULL_IMAGE" >&2
  exit 1
fi
docker tag "$HIEE_FULL_IMAGE" "$HIEE_IMAGE"

if [ -f "$TAR_DIR/image-tags.env" ]; then
  echo
  echo "Tags after import:"
  cat "$TAR_DIR/image-tags.env"
fi

echo
echo "Images loaded. $HIEE_IMAGE is ready for docker compose."
echo "Next: in your Helical Insight docker package folder,"
echo "  ensure .env exists (from .env.example), then: docker compose up -d"
