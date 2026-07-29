#Requires -Version 5.1
<#
.SYNOPSIS
  Run HelicalBI pytest suite and write JUnit + coverage reports (Windows).

.EXAMPLE
  .\scripts\run_tests_with_report.ps1
  .\scripts\run_tests_with_report.ps1 -SkipLlm
  .\scripts\run_tests_with_report.ps1 -LlmMode live
#>
[CmdletBinding()]
param(
    [string]$HelicalbiRoot = "",
    [switch]$SkipLlm,
    [switch]$ContinueOnCollectionErrors,
    [switch]$NoVenv,
    [ValidateSet("stub", "live")]
    [string]$LlmMode = "stub",
    [string[]]$ExtraPytestArgs = @()
)

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$DefaultRoot = Resolve-Path (Join-Path $ScriptDir "..")
if (-not $HelicalbiRoot) {
    $HelicalbiRoot = $DefaultRoot.Path
}

$PytestIni = Join-Path $HelicalbiRoot "pytest.ini"
if (-not (Test-Path $PytestIni)) {
    Write-Error "pytest.ini not found under HELICALBI_ROOT=$HelicalbiRoot"
}

Set-Location $HelicalbiRoot
Write-Host "HelicalBI root: $HelicalbiRoot"
$env:HELICALBI_LLM_MODE = $LlmMode
$env:PYTHONUNBUFFERED = "1"

if (-not $NoVenv) {
    if (-not (Test-Path ".venv")) {
        python -m venv .venv
    }
    $Activate = Join-Path $HelicalbiRoot ".venv\Scripts\Activate.ps1"
    . $Activate
    python -m pip install -U pip
    python -m pip install -r requirements.txt -r requirements-test.txt
}

New-Item -ItemType Directory -Force -Path "reports" | Out-Null
Remove-Item -Force -ErrorAction SilentlyContinue reports\junit.xml, reports\coverage.xml, reports\pytest.log, reports\summary.txt
if (Test-Path "htmlcov") {
    Remove-Item -Recurse -Force htmlcov
}

$pytestArgs = @(
    "--cov=helicalbi",
    "--cov=app",
    "--cov-config=.coveragerc",
    "--cov-report=term-missing",
    "--cov-report=html:htmlcov",
    "--cov-report=xml:reports/coverage.xml",
    "--junitxml=reports/junit.xml",
    "-o", "junit_family=xunit2"
)

if ($SkipLlm) {
    $pytestArgs += @("-m", "not llm")
}
if ($ContinueOnCollectionErrors) {
    $pytestArgs += "--continue-on-collection-errors"
}
if ($ExtraPytestArgs) {
    $pytestArgs += $ExtraPytestArgs
}

$logPath = Join-Path $HelicalbiRoot "reports\pytest.log"
$exitCode = 0
try {
    python -m pytest @pytestArgs 2>&1 | Tee-Object -FilePath $logPath
    if ($LASTEXITCODE -ne $null) {
        $exitCode = $LASTEXITCODE
    }
}
catch {
    $exitCode = 1
    $_ | Tee-Object -FilePath $logPath -Append
}

$summary = @(
    "helicalbi_root=$HelicalbiRoot"
    "llm_mode=$LlmMode"
    "skip_llm=$SkipLlm"
    "exit_code=$exitCode"
    "junit=reports/junit.xml"
    "coverage_xml=reports/coverage.xml"
    "coverage_html=htmlcov/index.html"
    (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")
)
$summary | Set-Content -Path (Join-Path $HelicalbiRoot "reports\summary.txt") -Encoding UTF8

Write-Host ""
Write-Host "Reports written under $HelicalbiRoot\reports and $HelicalbiRoot\htmlcov"
Get-Content (Join-Path $HelicalbiRoot "reports\summary.txt")

exit $exitCode
