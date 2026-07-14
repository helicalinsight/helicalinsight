#!/usr/bin/env bash
# CI entrypoint: create the legacy /home/helical/Performance layout, then load SampleTravelData.
# Presentation test configs are filtered by the Maven "ci" profile (-Denv=ci).
set -eu
(set -o pipefail) 2>/dev/null || true

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
export HI_ROOT="${HI_ROOT:-/home/helical/Performance/hi}"
export DB_ROOT="${DB_ROOT:-$HI_ROOT/db}"

exec bash "${ROOT}/scripts/setup-test-env.sh"