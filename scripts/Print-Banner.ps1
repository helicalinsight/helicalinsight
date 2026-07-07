#Requires -Version 5.1
# Print the Helical Insight ANSI banner to the host console.

$BannerPath = Join-Path $PSScriptRoot 'banner.ansi'

function Enable-AnsiConsole {
    if ($PSVersionTable.PSVersion.Major -ge 6) { return $true }
    try {
        $null = [Console]::OutputEncoding
        $handle = [System.Console]::OpenStandardOutput()
        return $true
    } catch {
        return $false
    }
}

if (-not (Test-Path $BannerPath)) {
    Write-Host 'Helical Insight' -ForegroundColor Cyan
    Write-Host 'Business Intelligence Platform' -ForegroundColor DarkGray
    return
}

$raw = Get-Content -Path $BannerPath -Raw -Encoding UTF8
$ansiEnabled = Enable-AnsiConsole

if ($ansiEnabled -or $env:WT_SESSION -or $env:TERM) {
    [Console]::Write($raw)
} else {
    Write-Host ($raw -replace '\x1b\[[0-9;]*m', '')
}
