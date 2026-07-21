#Requires -Version 5.1
# Prepare filesystem + SampleTravelData Derby DB for presentation integration tests (Windows).
#
# Usage:
#   .\scripts\setup-test-env.ps1
#   cd server; mvn test
#
# Presentation test resources (application-context.xml, persistence.xml, quartz.properties)
# are Maven-filtered from presentation/pom.xml profiles - no manual edits needed there.
$ErrorActionPreference = "Stop"

$Root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$Server = Join-Path $Root "server"
$RepoSrc = Join-Path $Server "hi-repository"
$SqlSource = Join-Path $Root "db-dump\SampleTravelData.sql"
$quartSource = Join-Path $Root "db-dump\quartz.sql"

# Default: local layout that matches the Maven "dev"/"ci" profile (paths under server/).
$HiRoot = if ($env:HI_ROOT) { $env:HI_ROOT } else { $Server }
$DbRoot = if ($env:DB_ROOT) { $env:DB_ROOT } else { Join-Path $HiRoot "db" }
$SampleDbPath = Join-Path $DbRoot "SampleTravelData"
$RepoLink = Join-Path $HiRoot "hi-repository"
$ScheduleDbPath = Join-Path $DbRoot "test/hischeduledata"
$DerbyVersion = if ($env:DERBY_VERSION) { $env:DERBY_VERSION } else { "10.17.1.0" }
$DerbyLib = if ($env:DERBY_LIB) { $env:DERBY_LIB } else { Join-Path $env:TEMP "derby-lib" }
$DerbySql = if ($env:DERBY_SQL) { $env:DERBY_SQL } else { Join-Path $env:TEMP "SampleTravelData-derby.sql" }



Write-Host "Preparing test environment"
Write-Host "  HI_ROOT=$HiRoot"
Write-Host "  DB_ROOT=$DbRoot"

if (-not (Test-Path $SqlSource)) {
    Write-Error "SampleTravelData SQL dump not found at $SqlSource"
    exit 1
}

New-Item -ItemType Directory -Force -Path $DbRoot, $DerbyLib, $HiRoot | Out-Null

# Point HI_ROOT/hi-repository at the repo copy unless it already is that path.
if ((Resolve-Path $HiRoot).Path -ne (Resolve-Path $Server).Path) {
    if ((Test-Path $RepoLink) -and -not ((Get-Item $RepoLink).LinkType)) {
        Write-Error "$RepoLink exists and is not a symlink"
        exit 1
    }
    New-Item -ItemType SymbolicLink -Path $RepoLink -Target $RepoSrc -Force | Out-Null
    Write-Host "[OK]   Symlinked $RepoLink -> $RepoSrc" -ForegroundColor Green
} else {
    $RepoLink = $RepoSrc
}

# Fixture paths used by some integration tests
New-Item -ItemType Directory -Force -Path `
    (Join-Path $RepoSrc "System\Logs"), `
    (Join-Path $RepoSrc "System\Temp"), `
    (Join-Path $RepoSrc "System\Reports\ExportTemplates"), `
    $DbRoot | Out-Null

$Setting = Join-Path $RepoSrc "System\Admin\setting.xml"
$GlobalConn = Join-Path $RepoSrc "System\Admin\globalConnections.xml"
# Java/Derby accept forward slashes on Windows; use them in config values.
$RepoAbs = ((Resolve-Path $RepoLink).Path) -replace '\\', '/'
$SampleJdbcPath = $SampleDbPath -replace '\\', '/'

if (Test-Path $Setting) {
    $content = Get-Content $Setting -Raw
    $content = $content -replace '<efwSolution>.*</efwSolution>', "<efwSolution>$RepoAbs</efwSolution>"
    Set-Content -Path $Setting -Value $content -NoNewline
    Write-Host "[OK]   Patched setting.xml (efwSolution -> $RepoAbs)" -ForegroundColor Green
}

if (Test-Path $GlobalConn) {
    $content = Get-Content $GlobalConn -Raw
    $content = $content -replace '<url>.*SampleTravelData</url>', "<url>jdbc:derby:$SampleJdbcPath</url>"
    Set-Content -Path $GlobalConn -Value $content -NoNewline
    Write-Host "[OK]   Patched globalConnections.xml (SampleTravelData)" -ForegroundColor Green
}

Write-Host "[INFO] Converting MySQL dump to Derby-compatible SQL"
$converted = New-Object System.Collections.Generic.List[string]
foreach ($line in (Get-Content $SqlSource)) {
    $l = $line -replace "`r$", ""
    if ($l -match '^CREATE DATABASE ') { continue }
    if ($l -match '^USE ') { continue }
    if ($l -match '^ENGINE=InnoDB') { continue }
    $l = $l -replace 'CREATE TABLE IF NOT EXISTS', 'CREATE TABLE'
    $l = $l -replace '\)ENGINE=InnoDB( DEFAULT CHARSET=latin1)?;', ');'
    $l = $l -replace '^\)\s*$', ');'
    $l = $l -replace '\s+DEFAULT CHARSET=latin1', ''
    $l = $l -replace '\bdatetime\b', 'TIMESTAMP'
    $converted.Add($l)
}
Set-Content -Path $DerbySql -Value $converted

Write-Host "[INFO] Resolving Derby $DerbyVersion jars"
foreach ($artifact in @("derby", "derbyshared", "derbytools")) {
    & mvn -q org.apache.maven.plugins:maven-dependency-plugin:3.6.1:copy `
        "-Dartifact=org.apache.derby:${artifact}:${DerbyVersion}" `
        "-DoutputDirectory=$DerbyLib" `
        -f (Join-Path $Server "pom.xml")
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to resolve Derby artifact: $artifact"
        exit 1
    }
}

$jars = Get-ChildItem -Path (Join-Path $DerbyLib "*.jar") -ErrorAction SilentlyContinue | Sort-Object Name
if (-not $jars) {
    Write-Error "No Derby jars found in $DerbyLib"
    exit 1
}
$DerbyCp = ($jars.FullName) -join ';'

if (Test-Path $SampleDbPath) {
    Write-Host "[INFO] Removing existing Derby database at $SampleDbPath"
    Remove-Item -Recurse -Force $SampleDbPath
}

$DerbySqlFwd = $DerbySql -replace '\\', '/'
$QuartzSqlFwd = $quartSource -replace '\\', '/'
$IjScript = [System.IO.Path]::GetTempFileName()
@"
CONNECT 'jdbc:derby:$SampleJdbcPath;create=true';
CREATE SCHEMA HIUSER;
SET SCHEMA HIUSER;
RUN '$DerbySqlFwd';
DISCONNECT;

CONNECT 'jdbc:derby:$ScheduleDbPath;create=true';
CREATE SCHEMA HIUSER;
SET SCHEMA HIUSER;
RUN '$QuartzSqlFwd';
DISCONNECT;
EXIT;
"@ | Set-Content -Path $IjScript -Encoding ASCII

Write-Host "[INFO] Creating Derby database at $SampleDbPath"
& java -cp $DerbyCp org.apache.derby.tools.ij $IjScript
Remove-Item -Force $IjScript



$VerifyScript = [System.IO.Path]::GetTempFileName()
@"
CONNECT 'jdbc:derby:$SampleJdbcPath';
SET SCHEMA HIUSER;
SELECT TABLENAME FROM SYS.SYSTABLES
  WHERE TABLETYPE = 'T'
    AND TABLENAME IN ('employee_details', 'geo_cordinates', 'meeting_details', 'travel_details', 'dimdate');
DISCONNECT;
EXIT;
"@ | Set-Content -Path $VerifyScript -Encoding ASCII

$VerifyOutput = (& java -cp $DerbyCp org.apache.derby.tools.ij $VerifyScript 2>&1 | Out-String)
Remove-Item -Force $VerifyScript

foreach ($table in @("employee_details", "geo_cordinates", "meeting_details", "travel_details", "dimdate")) {
    if ($VerifyOutput -notmatch $table) {
        Write-Error "SampleTravelData is missing table $table`n$VerifyOutput"
        exit 1
    }
}

Write-Host "[OK]   SampleTravelData Derby database ready (5 tables loaded)" -ForegroundColor Green
Write-Host ""
Write-Host "Next:"
Write-Host "  cd server; mvn test"
