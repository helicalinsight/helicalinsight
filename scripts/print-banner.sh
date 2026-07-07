#!/usr/bin/env bash
# Print the Helical Insight ANSI banner (no trailing newline required in banner file).
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BANNER="${SCRIPT_DIR}/banner.ansi"

if [ -f "$BANNER" ]; then
  cat "$BANNER"
else
  printf '\033[38;5;39mHelical Insight\033[0m — Business Intelligence Platform\n'
fi
