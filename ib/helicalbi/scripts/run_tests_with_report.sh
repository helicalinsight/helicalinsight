#!/usr/bin/env bash
# Run HelicalBI pytest suite and write JUnit + coverage reports.
#
# Usage (Linux / Jenkins):
#   ./scripts/run_tests_with_report.sh
#   ./scripts/run_tests_with_report.sh --skip-llm
#   HELICALBI_ROOT=/path/to/ib/helicalbi ./scripts/run_tests_with_report.sh
#
# Jenkins workspace default:
#   WORKSPACE=/home/helical/.jenkins/workspace/HI7SourceCode
#   HELICALBI_ROOT=$WORKSPACE/ib/helicalbi

set -euo pipefail

SKIP_LLM=0
CONTINUE_COLLECTION=0
CREATE_VENV=1
EXTRA_PYTEST_ARGS=()

while [[ $# -gt 0 ]]; do
  case "$1" in
    --skip-llm)
      SKIP_LLM=1
      shift
      ;;
    --continue-on-collection-errors)
      CONTINUE_COLLECTION=1
      shift
      ;;
    --no-venv)
      CREATE_VENV=0
      shift
      ;;
    --llm-mode)
      export HELICALBI_LLM_MODE="${2:-stub}"
      shift 2
      ;;
    -h|--help)
      sed -n '1,20p' "$0"
      exit 0
      ;;
    *)
      EXTRA_PYTEST_ARGS+=("$1")
      shift
      ;;
  esac
done

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
WORKSPACE_ROOT="${WORKSPACE:-/home/helical/.jenkins/workspace/HI7SourceCode}"
HELICALBI_ROOT="${HELICALBI_ROOT:-$DEFAULT_ROOT}"

# Prefer Jenkins layout when pytest.ini is not next to this script's parent.
if [[ ! -f "${HELICALBI_ROOT}/pytest.ini" && -f "${WORKSPACE_ROOT}/ib/helicalbi/pytest.ini" ]]; then
  HELICALBI_ROOT="${WORKSPACE_ROOT}/ib/helicalbi"
fi

if [[ ! -f "${HELICALBI_ROOT}/pytest.ini" ]]; then
  echo "ERROR: pytest.ini not found under HELICALBI_ROOT=${HELICALBI_ROOT}" >&2
  echo "Set HELICALBI_ROOT to the InstantBI package root (contains pytest.ini)." >&2
  exit 2
fi

cd "${HELICALBI_ROOT}"
echo "HelicalBI root: ${HELICALBI_ROOT}"
echo "Workspace:      ${WORKSPACE_ROOT}"

export HELICALBI_LLM_MODE="${HELICALBI_LLM_MODE:-stub}"
export PYTHONUNBUFFERED=1

if [[ "${CREATE_VENV}" -eq 1 ]]; then
  if [[ ! -d .venv ]]; then
    python3 -m venv .venv
  fi
  # shellcheck disable=SC1091
  source .venv/bin/activate
  python -m pip install -U pip
  python -m pip install -r requirements.txt -r requirements-test.txt
fi

mkdir -p reports
rm -f reports/junit.xml reports/coverage.xml reports/pytest.log reports/summary.txt
rm -rf htmlcov

PYTEST_ARGS=(
  --cov=helicalbi
  --cov=app
  --cov-config=.coveragerc
  --cov-report=term-missing
  --cov-report=html:htmlcov
  --cov-report=xml:reports/coverage.xml
  --junitxml=reports/junit.xml
  -o junit_family=xunit2
)

if [[ "${SKIP_LLM}" -eq 1 ]]; then
  PYTEST_ARGS+=(-m "not llm")
fi
if [[ "${CONTINUE_COLLECTION}" -eq 1 ]]; then
  PYTEST_ARGS+=(--continue-on-collection-errors)
fi
if [[ ${#EXTRA_PYTEST_ARGS[@]} -gt 0 ]]; then
  PYTEST_ARGS+=("${EXTRA_PYTEST_ARGS[@]}")
fi

set +e
python -m pytest "${PYTEST_ARGS[@]}" 2>&1 | tee reports/pytest.log
EXIT_CODE=${PIPESTATUS[0]}
set -e

{
  echo "helicalbi_root=${HELICALBI_ROOT}"
  echo "llm_mode=${HELICALBI_LLM_MODE}"
  echo "skip_llm=${SKIP_LLM}"
  echo "exit_code=${EXIT_CODE}"
  echo "junit=reports/junit.xml"
  echo "coverage_xml=reports/coverage.xml"
  echo "coverage_html=htmlcov/index.html"
  date -u +"%Y-%m-%dT%H:%M:%SZ"
} > reports/summary.txt

echo ""
echo "Reports written under ${HELICALBI_ROOT}/reports and ${HELICALBI_ROOT}/htmlcov"
echo "Summary:"
cat reports/summary.txt

exit "${EXIT_CODE}"
