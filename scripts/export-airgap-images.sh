#!/usr/bin/env bash
# Export already-local Docker images as tar files for offline clients.
# Does not build or pull — images must already exist (docker images).
#
# Usage (from any directory, typically repo root):
#   ./scripts/export-airgap-images.sh
#
# Output (default, under current working directory):
#   ./airgap/  (*.tar + import-airgap-images.sh + image-tags.env)
#   ./helicalinsight-airgap-images.zip
#
# hiee full image is exported as nitrogen-j25t11-full; import retags it to
# nitrogen-j25t11 so docker-compose.yml defaults keep working.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CWD="$(pwd)"
OUT_DIR="${AIRGAP_OUT_DIR:-$CWD/airgap}"
ZIP_PATH="${AIRGAP_ZIP_PATH:-$CWD/helicalinsight-airgap-images.zip}"

# Full/airgap image (Docker Hub / build tag). Import retags to HIEE_IMAGE.
HIEE_FULL_IMAGE="${HIEE_FULL_IMAGE:-hiee/helicalinsight:nitrogen-j25t11-full}"
# Compose default after import
HIEE_IMAGE="${HIEE_IMAGE:-hiee/helicalinsight:nitrogen-j25t11}"
POSTGRES_IMAGE="${POSTGRES_IMAGE:-postgres:15.8-alpine3.20}"
NGINX_IMAGE="${NGINX_IMAGE:-nginx:alpine}"
INSTANTBI_IMAGE="${INSTANTBI_IMAGE:-python:3.13-slim}"

IMAGES=("$HIEE_FULL_IMAGE" "$POSTGRES_IMAGE" "$NGINX_IMAGE" "$INSTANTBI_IMAGE")

safe_name() {
  echo "$1" | tr '/:' '__'
}

echo "Airgap export (save local images only)"
echo "  cwd: $CWD"
echo "  out: $OUT_DIR"
echo "  hiee (export): $HIEE_FULL_IMAGE"
echo "  hiee (compose after import): $HIEE_IMAGE"
echo

missing=0
for img in "${IMAGES[@]}"; do
  if ! docker image inspect "$img" >/dev/null 2>&1; then
    echo "error: image not found locally: $img" >&2
    missing=1
  else
    echo "  ok: $img"
  fi
done
if [ "$missing" -ne 0 ]; then
  echo >&2
  echo "Build or pull the missing images first, then re-run this script." >&2
  echo "  e.g. docker build -f docker/Dockerfile.airgap -t $HIEE_FULL_IMAGE docker" >&2
  exit 1
fi

mkdir -p "$OUT_DIR"

echo
echo "==> Saving images to $OUT_DIR"
for img in "${IMAGES[@]}"; do
  tar_path="$OUT_DIR/$(safe_name "$img").tar"
  echo "  docker save $img -> $tar_path"
  docker save -o "$tar_path" "$img"
done

# Ship import script inside the folder so the client can extract and run it
cp -a "$SCRIPT_DIR/import-airgap-images.sh" "$OUT_DIR/import-airgap-images.sh"
chmod +x "$OUT_DIR/import-airgap-images.sh"

MANIFEST="$OUT_DIR/image-tags.env"
cat > "$MANIFEST" <<EOF
# Exported image tags. Import retags HIEE_FULL_IMAGE -> HIEE_IMAGE for Compose.
HIEE_FULL_IMAGE=$HIEE_FULL_IMAGE
HIEE_IMAGE=$HIEE_IMAGE
POSTGRES_IMAGE=$POSTGRES_IMAGE
NGINX_IMAGE=$NGINX_IMAGE
INSTANTBI_IMAGE=$INSTANTBI_IMAGE
EOF

# Single archive for shipping (next to the airgap folder, in cwd)
rm -f "$ZIP_PATH"
(
  cd "$(dirname "$OUT_DIR")"
  folder="$(basename "$OUT_DIR")"
  if command -v zip >/dev/null 2>&1; then
    zip -qr "$ZIP_PATH" "$folder"
  else
    tar -a -cf "$ZIP_PATH" "$folder"
  fi
)

echo
echo "Done."
echo "  folder: $OUT_DIR  (tars + import-airgap-images.sh + image-tags.env)"
echo "  archive: $ZIP_PATH"
echo "Ship the archive to the client. They extract it and run: ./import-airgap-images.sh"
