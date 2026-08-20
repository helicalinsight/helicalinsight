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
# Link hi-repository into the shared Docker layout (same path the package uses)
$hiRepositoryLink = Join-Path $Root "docker\hi\hi-repository"
$hiRepositorySrc = Join-Path $Root "server\hi-repository"
$hiRepositoryParent = Join-Path $Root "docker\hi"
New-Item -ItemType Directory -Force -Path $hiRepositoryParent | Out-Null
if ((Test-Path $hiRepositorySrc) -and -not (Test-Path $hiRepositoryLink)) {
    try {
        New-Item -ItemType Junction -Path $hiRepositoryLink -Target $hiRepositorySrc | Out-Null
        Write-Host '[OK]   Linked docker/hi/hi-repository -> server/hi-repository' -ForegroundColor Green
    } catch {
        Copy-Item -Recurse $hiRepositorySrc $hiRepositoryLink
        Write-Host '[OK]   Copied server/hi-repository ->  docker/hi/hi-repository' -ForegroundColor Green
    }
} elseif (Test-Path $hiRepositoryLink) {
    Write-Host '[SKIP]  docker/hi/hi-repository already present' -ForegroundColor Yellow
}
# Link hi-ee.war into the shared Docker layout (same path the package uses)
$hiwarLink = Join-Path $Root "docker\hi\hi-ee.war"
$hiWarTarget = Join-Path $Root "server\presentation\target"
$hiWarSrc = Get-ChildItem $hiWarTarget -Filter "hi-ee-*.war" -File -ErrorAction SilentlyContinue | Select-Object -First 1

if ($HiWarSrc -and -not (Test-Path $HiWarLink)) {
    try {
		New-Item -ItemType SymbolicLink -Path $hiWarLink -Target $hiWarSrc.FullName | Out-Null
		Write-Host "[OK] Linked docker/hi/hi-ee.war -> $($HiWarSrc.Name)" -ForegroundColor Green
    } catch {
		Copy-Item $HiWarSrc.FullName $HiWarLink
       Write-Host "[OK] Copied $($HiWarSrc.Name) -> docker/hi/hi-ee.war" -ForegroundColor Green
    }
} elseif (Test-Path $hiWarLink) { 
	Write-Host '[SKIP] docker/hi/hi-ee.war already present' -ForegroundColor Yellow 
}

# Link Instant BI into the shared Docker layout (compose mounts ./instantbi/com/helicalinsight/instantbi)
$InstantBiLink = Join-Path $Root "docker\instantbi\com\helicalinsight\instantbi"
$InstantBiSrc = Join-Path $Root "instantbi\src\com\helicalinsight\instantbi"
$InstantBiParent = Join-Path $Root "docker\instantbi\com\helicalinsight"
New-Item -ItemType Directory -Force -Path $InstantBiParent | Out-Null
if ((Test-Path $InstantBiSrc) -and -not (Test-Path $InstantBiLink)) {
    try {
        New-Item -ItemType Junction -Path $InstantBiLink -Target $InstantBiSrc | Out-Null
        Write-Host '[OK]   Linked docker/instantbi/com/helicalinsight/instantbi -> instantbi/src/com/helicalinsight/instantbi' -ForegroundColor Green
    } catch {
        Copy-Item -Recurse $InstantBiSrc $InstantBiLink
        Write-Host '[OK]   Copied instantbi/src/com/helicalinsight/instantbi -> docker/instantbi/com/helicalinsight/instantbi' -ForegroundColor Green
    }
} elseif (Test-Path $InstantBiLink) {
    Write-Host '[SKIP] docker/instantbi/com/helicalinsight/instantbi already present' -ForegroundColor Yellow
}

# Docker mounts ./hi/hi-repository/System/InstantBI → /app/helicalbi/config.
# YAML source of truth stays helicalbi/config; this link is for Compose only.
$InstantBiConfigSrc = Join-Path $InstantBiSrc "helicalbi\config"
$InstantBiConfigLink = Join-Path $Root "server\hi-repository\System\InstantBI"
$linkItem = Get-Item $InstantBiConfigLink -Force -ErrorAction SilentlyContinue
if ($linkItem -and -not $linkItem.LinkType) {
    Remove-Item -Recurse -Force $InstantBiConfigLink
}
if ((Test-Path $InstantBiConfigSrc) -and -not (Test-Path $InstantBiConfigLink)) {
    try {
        New-Item -ItemType Junction -Path $InstantBiConfigLink -Target $InstantBiConfigSrc | Out-Null
        Write-Host '[OK]   Linked hi-repository/System/InstantBI -> helicalbi/config' -ForegroundColor Green
    } catch {
        cmd /c mklink /J "$InstantBiConfigLink" "$InstantBiConfigSrc" | Out-Null
        if (Test-Path $InstantBiConfigLink) {
            Write-Host '[OK]   Linked hi-repository/System/InstantBI -> helicalbi/config' -ForegroundColor Green
        } else {
            Copy-Item -Recurse $InstantBiConfigSrc $InstantBiConfigLink
            Write-Host '[OK]   Copied helicalbi/config -> hi-repository/System/InstantBI' -ForegroundColor Green
        }
    }
} elseif (Test-Path $InstantBiConfigLink) {
    Write-Host '[SKIP] hi-repository/System/InstantBI already present' -ForegroundColor Yellow
}

# Remove leftover docker/config/instantbi from the previous layout
$DockerInstantBiConfig = Join-Path $Root "docker\config\instantbi"
$dockerCfgItem = Get-Item $DockerInstantBiConfig -Force -ErrorAction SilentlyContinue
if ($dockerCfgItem -and $dockerCfgItem.LinkType) {
    Remove-Item $DockerInstantBiConfig -Force
    Write-Host '[OK]   Removed leftover docker/config/instantbi link' -ForegroundColor Green
} elseif ($dockerCfgItem -and $dockerCfgItem.PSIsContainer -and -not (Get-ChildItem $DockerInstantBiConfig -Force -ErrorAction SilentlyContinue)) {
    Remove-Item $DockerInstantBiConfig
    Write-Host '[OK]   Removed leftover empty docker/config/instantbi' -ForegroundColor Green
}

Write-Host ""
Write-Host "Setup complete. See README.md for full paths." -ForegroundColor Green
Write-Host ""
Write-Host "Recommended (full stack - first start can take a few minutes):"
Write-Host "  cd docker; docker compose up -d"
Write-Host "  # Open https://localhost  (login: hiadmin / hiadmin)"
Write-Host ""
Write-Host "Per component:"
Write-Host "  Backend:    cd server; mvn clean package -DskipTests"
Write-Host "              # Deploy presentation\target\hi-ee-7.0.0.war as %CATALINA_HOME%\webapps\hi-ee.war"
Write-Host "  Frontend:   cd client; npm ci --legacy-peer-deps; npm run start18"
Write-Host "  Instant BI: cd instantbi\src\com\helicalinsight\instantbi; pip install -r requirements.txt; python app.py"
Write-Host ""
Write-Host "Build backend from source in Docker:"
Write-Host "  docker compose -f docker-compose.dev.yml up --build"
