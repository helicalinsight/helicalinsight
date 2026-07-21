#Requires -Version 5.1
# CI-style entrypoint for Windows: load SampleTravelData using the default (dynamic) layout.
#
# The Maven "ci" profile now resolves hiRepositoryPath / db paths from the build
# root (${maven.multiModuleProjectDirectory}), so no legacy /home/helical/Performance
# layout is required. This simply runs the standard setup and leaves the profile
# selection to Maven (mvn test -Denv=ci).
$ErrorActionPreference = "Stop"

& (Join-Path $PSScriptRoot "setup-test-env.ps1")

Write-Host ""
Write-Host "Next (CI profile):"
Write-Host "  cd server; mvn test -Denv=ci"
