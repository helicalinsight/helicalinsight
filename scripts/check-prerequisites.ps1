#Requires -Version 5.1
$ErrorActionPreference = "Continue"

. (Join-Path $PSScriptRoot "Print-Banner.ps1")
Write-Host ""
Write-Host "Prerequisite check" -ForegroundColor Cyan
Write-Host "====================================="

function Test-Command {
    param([string]$Name, [string]$Command, [string]$Hint)
    $cmd = Get-Command $Command -ErrorAction SilentlyContinue
    if ($cmd) {
        $version = & $Command --version 2>&1 | Select-Object -First 1
        Write-Host ('[OK]   ' + $Name + ': ' + $version) -ForegroundColor Green
        return $true
    }
    Write-Host ('[FAIL] ' + $Name + ' not found. ' + $Hint) -ForegroundColor Red
    return $false
}

$ok = $true
$ok = (Test-Command "Java" "java" "Install JDK 25+ and set JAVA_HOME.") -and $ok
$ok = (Test-Command "Maven" "mvn" "Install Maven 3.8+ (https://maven.apache.org/).") -and $ok
$ok = (Test-Command "Node.js" "node" "Install Node.js 18 LTS (https://nodejs.org/).") -and $ok
$ok = (Test-Command "npm" "npm" "npm ships with Node.js; reinstall Node if missing.") -and $ok

$python = Get-Command python -ErrorAction SilentlyContinue
if (-not $python) { $python = Get-Command python3 -ErrorAction SilentlyContinue }
if ($python) {
    $pyVer = & $python.Name --version 2>&1 | Select-Object -First 1
    Write-Host ('[OK]   Python: ' + $pyVer) -ForegroundColor Green
} else {
    Write-Host '[WARN] Python not found (needed only for native Instant BI; Docker covers it).' -ForegroundColor Yellow
}

if (Get-Command docker -ErrorAction SilentlyContinue) {
    Write-Host ('[OK]   Docker: ' + (docker --version)) -ForegroundColor Green
} else {
    Write-Host '[WARN] Docker not found (optional — fastest full-stack / product run).' -ForegroundColor Yellow
}

Write-Host ""
if (-not $ok) {
    Write-Host "Fix the failed checks above before continuing." -ForegroundColor Red
    exit 1
}

Write-Host "All required tools found. Next steps:" -ForegroundColor Green
Write-Host "  .\scripts\setup-dev.ps1"
Write-Host "  # Full stack:  cd docker; docker compose up -d"
Write-Host "  # Or per component — see README.md (Backend / Frontend / Instant BI)"
