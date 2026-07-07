#Requires -Version 5.1
$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "Print-Banner.ps1")
. (Join-Path $PSScriptRoot "setup-dev.helpers.ps1")

$Root = Resolve-Path (Join-Path $PSScriptRoot "..")
$Server = Join-Path $Root "server"
$Repo = Join-Path $Server "hi-repository"
$Db = Join-Path $Server "db"
$Setting = Join-Path $Repo "System\Admin\setting.xml"
$GlobalConn = Join-Path $Repo "System\Admin\globalConnections.xml"

Write-Host ""
Write-Host "Development setup" -ForegroundColor Cyan
Write-Host "Repository root: $Root"

New-Item -ItemType Directory -Force -Path $Db, (Join-Path $Repo "System\Logs") | Out-Null

$RepoAbs = (Resolve-Path $Repo).Path
$DbAbs = (Resolve-Path $Db).Path

if (Test-Path $Setting) {
    $content = Get-Content $Setting -Raw
    if ($content -match '\$\{INSTALL_PATH\}') {
        $content = $content -replace '<efwSolution>.*</efwSolution>', "<efwSolution>$RepoAbs</efwSolution>"
        $content = $content -replace '<BaseUrl>.*</BaseUrl>', '<BaseUrl>http://localhost:8080/hi-ee/hi.html</BaseUrl>'
        Set-Content -Path $Setting -Value $content -NoNewline
        Write-Host '[OK]   Patched setting.xml (efwSolution, BaseUrl)' -ForegroundColor Green
    } else {
        Write-Host '[SKIP] setting.xml already has absolute paths' -ForegroundColor Yellow
    }
}

if (Test-Path $GlobalConn) {
    $content = Get-Content $GlobalConn -Raw
    if ($content -match 'SampleTravelData') {
        $derbyUrl = "jdbc:derby:$($DbAbs -replace '\\','/')/SampleTravelData"
        $content = $content -replace '<url>.*SampleTravelData</url>', "<url>$derbyUrl</url>"
        Set-Content -Path $GlobalConn -Value $content -NoNewline
        Write-Host '[OK]   Patched globalConnections.xml (SampleTravelData)' -ForegroundColor Green
    }
}

$persistencePatched = 0
foreach ($file in Get-PersistenceXmlFiles -ServerRoot $Server) {
    if (Restore-PersistencePlaceholders -FilePath $file) {
        $persistencePatched++
    }
}

if ($persistencePatched -gt 0) {
    Write-Host "[OK]   Normalized persistence.xml placeholders in $persistencePatched file(s)" -ForegroundColor Green
} else {
    Write-Host '[SKIP] persistence.xml already uses Maven placeholders' -ForegroundColor Yellow
}


$EnvExample = Join-Path $Root ".env.example"
$EnvFile = Join-Path $Root ".env"
if (-not (Test-Path $EnvFile) -and (Test-Path $EnvExample)) {
    Copy-Item $EnvExample $EnvFile
    Write-Host '[OK]   Created .env from .env.example' -ForegroundColor Green
}

Write-Host ""
Write-Host "Setup complete. Build and run:" -ForegroundColor Green
Write-Host "  cd server; mvn clean package -DskipTests"
Write-Host "  # Deploy presentation\target\hi-ce-7.0.0.war as %CATALINA_HOME%\webapps\hi-ee.war"
Write-Host "  cd client; npm ci --legacy-peer-deps; npm run start18"
Write-Host ""
Write-Host "Or use Docker:"
Write-Host "  docker compose -f docker-compose.dev.yml up --build"
