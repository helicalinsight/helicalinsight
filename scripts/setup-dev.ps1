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
        $content = $content -replace '<BaseUrl>.*</BaseUrl>', '<BaseUrl>http://localhost:8080/hi-ee/</BaseUrl>'
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


$EnvExample = Join-Path $Root ".env.example"
$EnvFile = Join-Path $Root ".env"
if (-not (Test-Path $EnvFile) -and (Test-Path $EnvExample)) {
    Copy-Item $EnvExample $EnvFile
    Write-Host '[OK]   Created .env from .env.example' -ForegroundColor Green
}

$DockerEnvExample = Join-Path $Root "docker\.env.example"
$DockerEnv = Join-Path $Root "docker\.env"
if (-not (Test-Path $DockerEnv) -and (Test-Path $DockerEnvExample)) {
    Copy-Item $DockerEnvExample $DockerEnv
    Write-Host '[OK]   Created docker/.env from docker/.env.example' -ForegroundColor Green
}

# Link Instant BI into the shared Docker layout (same path the package uses)
$InstantBiLink = Join-Path $Root "docker\instantbi\helicalbi"
$InstantBiSrc = Join-Path $Root "ib\helicalbi"
$InstantBiParent = Join-Path $Root "docker\instantbi"
New-Item -ItemType Directory -Force -Path $InstantBiParent | Out-Null
if ((Test-Path $InstantBiSrc) -and -not (Test-Path $InstantBiLink)) {
    try {
        New-Item -ItemType Junction -Path $InstantBiLink -Target $InstantBiSrc | Out-Null
        Write-Host '[OK]   Linked docker/instantbi/helicalbi → ib/helicalbi' -ForegroundColor Green
    } catch {
        Copy-Item -Recurse $InstantBiSrc $InstantBiLink
        Write-Host '[OK]   Copied ib/helicalbi → docker/instantbi/helicalbi' -ForegroundColor Green
    }
} elseif (Test-Path $InstantBiLink) {
    Write-Host '[SKIP] docker/instantbi/helicalbi already present' -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Setup complete. See README.md for full paths." -ForegroundColor Green
Write-Host ""
Write-Host "Recommended (full stack — first start can take a few minutes):"
Write-Host "  cd docker; docker compose up -d"
Write-Host "  # Open https://localhost  (login: hiadmin / hiadmin)"
Write-Host ""
Write-Host "Per component:"
Write-Host "  Backend:    cd server; mvn clean package -DskipTests"
Write-Host "              # Deploy presentation\target\hi-ee-7.0.0.war as %CATALINA_HOME%\webapps\hi-ee.war"
Write-Host "  Frontend:   cd client; npm ci --legacy-peer-deps; npm run start18"
Write-Host "  Instant BI: cd ib\helicalbi; pip install -r requirements.txt; python app.py"
Write-Host ""
Write-Host "Build backend from source in Docker:"
Write-Host "  docker compose -f docker-compose.dev.yml up --build"
