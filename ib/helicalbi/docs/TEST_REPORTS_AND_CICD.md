# HelicalBI — Run All Tests & Generate Reports

How to run the full pytest suite with coverage and JUnit reports on **Windows (local)** and **Linux / Jenkins** (`/home/helical/.jenkins/workspace/HI7SourceCode`).

HelicalBI package root (where `pytest.ini` lives):

| Environment | Typical path |
|-------------|--------------|
| Windows local | `D:\hi7\ib\helicalbi` |
| Jenkins workspace | `/home/helical/.jenkins/workspace/HI7SourceCode/ib/helicalbi` |

If your checkout layout differs, set `HELICALBI_ROOT` to the directory that contains `pytest.ini`.

---

## What the report run produces

Artifacts under `reports/` (and `htmlcov/`):

| Artifact | Path | Use |
|----------|------|-----|
| JUnit XML | `reports/junit.xml` | Jenkins “Publish JUnit test result report” |
| Coverage XML | `reports/coverage.xml` | Jenkins Cobertura / Coverage plugin |
| Coverage HTML | `htmlcov/index.html` | Open in browser |
| Pytest log | `reports/pytest.log` | CI console archive |
| Summary | `reports/summary.txt` | Pass/fail + coverage line |

Default CI mode skips live LLM calls (`HELICALBI_LLM_MODE=stub`) and can optionally skip LLM tests entirely with `-m "not llm"` for faster pipelines.

---

## Prerequisites

- Python **3.10+** (3.11/3.13 fine if deps install)
- Network once to `pip install`
- From HelicalBI root:

```text
pip install -r requirements.txt
pip install -r requirements-test.txt
```

---

## Windows (local)

### Option A — helper script (recommended)

PowerShell (from anywhere):

```powershell
cd D:\hi7\ib\helicalbi
.\scripts\run_tests_with_report.ps1
```

Common flags:

```powershell
# All tests (functional + integration + llm stub)
.\scripts\run_tests_with_report.ps1

# Fast CI-like run (skip LLM marker)
.\scripts\run_tests_with_report.ps1 -SkipLlm

# Fail on collection errors still continue reporting
.\scripts\run_tests_with_report.ps1 -ContinueOnCollectionErrors

# Live LLM (needs OPENAI_API_KEY / local yaml)
.\scripts\run_tests_with_report.ps1 -LlmMode live
```

Open the HTML coverage report:

```powershell
Start-Process .\htmlcov\index.html
```

### Option B — manual commands

```powershell
cd D:\hi7\ib\helicalbi
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
pip install -r requirements-test.txt

$env:HELICALBI_LLM_MODE = "stub"
New-Item -ItemType Directory -Force -Path reports | Out-Null

python -m pytest `
  --cov=helicalbi --cov=app --cov-config=.coveragerc `
  --cov-report=term-missing `
  --cov-report=html:htmlcov `
  --cov-report=xml:reports/coverage.xml `
  --junitxml=reports/junit.xml `
  -o junit_family=xunit2 `
  2>&1 | Tee-Object -FilePath reports\pytest.log
```

---

## Linux / Jenkins

Workspace on the Jenkins agent:

```text
/home/helical/.jenkins/workspace/HI7SourceCode
```

HelicalBI module (default):

```text
/home/helical/.jenkins/workspace/HI7SourceCode/ib/helicalbi
```

### Option A — helper script

```bash
cd /home/helical/.jenkins/workspace/HI7SourceCode/ib/helicalbi
chmod +x scripts/run_tests_with_report.sh
./scripts/run_tests_with_report.sh
```

Useful env vars / flags:

```bash
# Skip LLM tests (typical CI)
./scripts/run_tests_with_report.sh --skip-llm

# Explicit root (if layout differs)
export HELICALBI_ROOT=/home/helical/.jenkins/workspace/HI7SourceCode/ib/helicalbi
export WORKSPACE=/home/helical/.jenkins/workspace/HI7SourceCode
./scripts/run_tests_with_report.sh --skip-llm

# Live LLM (secrets via Jenkins credentials, not committed files)
export HELICALBI_LLM_MODE=live
export OPENAI_API_KEY=...   # from Jenkins credential binding
./scripts/run_tests_with_report.sh
```

### Option B — one-liner for a freestyle / pipeline shell step

```bash
set -euo pipefail
export WORKSPACE="${WORKSPACE:-/home/helical/.jenkins/workspace/HI7SourceCode}"
export HELICALBI_ROOT="${HELICALBI_ROOT:-$WORKSPACE/ib/helicalbi}"
cd "$HELICALBI_ROOT"
python3 -m venv .venv
# shellcheck disable=SC1091
source .venv/bin/activate
pip install -U pip
pip install -r requirements.txt -r requirements-test.txt
export HELICALBI_LLM_MODE="${HELICALBI_LLM_MODE:-stub}"
mkdir -p reports
python -m pytest \
  -m "not llm" \
  --cov=helicalbi --cov=app --cov-config=.coveragerc \
  --cov-report=term-missing \
  --cov-report=html:htmlcov \
  --cov-report=xml:reports/coverage.xml \
  --junitxml=reports/junit.xml \
  -o junit_family=xunit2 \
  | tee reports/pytest.log
```

---

## Jenkins CI/CD

Use the checked-in pipeline script:

- Declarative pipeline: [`ci/Jenkinsfile.helicalbi-tests`](../ci/Jenkinsfile.helicalbi-tests)
- Shell entrypoint used by the pipeline: [`scripts/run_tests_with_report.sh`](../scripts/run_tests_with_report.sh)

### Freestyle job (quick setup)

1. **Source**: checkout into `HI7SourceCode` (or your SCM root).
2. **Build → Execute shell**:

   ```bash
   bash "$WORKSPACE/ib/helicalbi/scripts/run_tests_with_report.sh" --skip-llm
   ```

3. **Post-build**:
   - **Publish JUnit**: `ib/helicalbi/reports/junit.xml`
   - **Cobertura / Coverage**: `ib/helicalbi/reports/coverage.xml`
   - **Archive**: `ib/helicalbi/reports/**`, `ib/helicalbi/htmlcov/**`

### Pipeline job

Point the job at `ci/Jenkinsfile.helicalbi-tests` (or copy its `pipeline { ... }` into your existing HI7 Jenkinsfile as a stage named `HelicalBI Tests`).

Default agent path assumptions:

```groovy
environment {
  WORKSPACE_ROOT = '/home/helical/.jenkins/workspace/HI7SourceCode'
  HELICALBI_ROOT = "${WORKSPACE_ROOT}/ib/helicalbi"
}
```

Override `HELICALBI_ROOT` if InstantBI lives elsewhere under the same workspace.

### Credentials (live LLM only)

Do **not** commit API keys or session cookies. For live runs:

| Secret | Jenkins binding | Env var |
|--------|-----------------|---------|
| OpenAI API key | Secret text | `OPENAI_API_KEY` |
| Optional session cookie | Secret text | `HELICALBI_TEST_SESSION_COOKIE` |

Keep CI default as `HELICALBI_LLM_MODE=stub` and `-m "not llm"` unless a dedicated night job needs live evaluation.

---

## Marker cheat sheet

| Goal | Command fragment |
|------|------------------|
| All layers | `pytest` (no `-m`) |
| Skip LLM | `pytest -m "not llm"` |
| Functional only | `pytest -m functional` |
| Integration only | `pytest -m integration` |
| LLM only (stub) | `HELICALBI_LLM_MODE=stub pytest -m llm` |
| LLM only (live) | `HELICALBI_LLM_MODE=live pytest -m llm` |

Travle_Agent SQL/viz suite:

```bash
pytest tests/llm/test_travle_agent_sql_viz.py -v
```

---

## Exit codes

| Code | Meaning |
|------|---------|
| `0` | All selected tests passed |
| non-zero | Failures, errors, or script setup failure |

Jenkins should mark the build **UNSTABLE** or **FAILED** when the script exits non-zero (default for most jobs).

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| `pytest.ini` not found | `cd` into HelicalBI root or set `HELICALBI_ROOT` |
| `ModuleNotFoundError: helicalbi` | Install `requirements.txt` inside the venv; run from package root |
| LLM tests skipped | `pip install -r requirements-test.txt` (needs `deepeval`) |
| Collection errors on a few modules | Re-run with `--continue-on-collection-errors` (script flag supported) |
| Coverage HTML empty | Ensure pytest finished; open `htmlcov/index.html` from HelicalBI root |
| Jenkins can’t find junit.xml | Publish path relative to `$WORKSPACE`: `ib/helicalbi/reports/junit.xml` |

See also: [TESTING.md](../TESTING.md) for suite layout and DeepEval details.
