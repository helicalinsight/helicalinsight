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

Preferred job on the HI Jenkins controller: **Dev InstantBI Testcase** (same pattern as **Dev JUnit Testcase**).

Checked-in scripts:

- [`cicd/jenkins/InstantBITests/pipeline.groovy`](../../../cicd/jenkins/InstantBITests/pipeline.groovy)
- [`ci/Jenkinsfile.helicalbi-tests`](../ci/Jenkinsfile.helicalbi-tests) (same pipeline under InstantBI)
- Shell entrypoint: [`scripts/run_tests_with_report.sh`](../scripts/run_tests_with_report.sh)

Flow (matches HI7EndToEnd InstantBI obfuscation via `pyflask`):

1. `build job: 'HI7SourceCode'`
2. `cd .../HI7SourceCode/ib && docker compose up -d pyflask`
3. `docker compose exec -T pyflask bash scripts/run_tests_with_report.sh --no-venv --skip-llm`
4. Publish `ib/helicalbi/reports/junit.xml` + archive reports/htmlcov
5. `docker compose down`

Obfuscation reference (HI7EndToEnd):

```bash
cd /home/helical/.jenkins/workspace/HI7SourceCode/ib
docker compose up -d pyflask
docker compose exec -T pyflask sh -c '
  python /app/scripts/obfuscate_instantbi.py --src /app --out /dist/instantbi --install-deps
'
```

### Freestyle / host-shell fallback

```bash
bash /home/helical/.jenkins/workspace/HI7SourceCode/ib/helicalbi/scripts/run_tests_with_report.sh --skip-llm
```

### Credentials / host config (live LLM only)

Do **not** commit API keys, session cookies, or environment host IPs in InstantBI source.
Configure them on the Jenkins job (parameters / credentials) and pass into pyflask:

| Setting | Jenkins | Env var |
|---------|---------|---------|
| hi-ee base URL | Job param `HELICALBI_TEST_BASE_URL` | `HELICALBI_TEST_BASE_URL` |
| Model dir/file | Job params `HELICALBI_TEST_MODEL_*` | same names |
| Metadata dir/file | Job params `HELICALBI_TEST_METADATA_*` | same names |
| OpenAI API key | Secret text credential | `OPENAI_API_KEY` / `HELICALBI_TEST_OPENAI_API_KEY` |
| Session cookie | Secret text credential | `HELICALBI_TEST_SESSION_COOKIE` |

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
