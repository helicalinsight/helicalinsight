#Requires -Version 5.1
# Shared helpers for Helical Insight local development setup.

function Restore-PersistencePlaceholders {
    param([string]$FilePath)

    if (-not (Test-Path $FilePath)) {
        return $false
    }

    $content = Get-Content $FilePath -Raw
    $updated = $content

    if ($updated -notmatch 'hibernate\.dialect" value="\$\{dbDialect\}"') {
        $updated = $updated -replace '<property name="hibernate\.dialect" value="[^"]*"', '<property name="hibernate.dialect" value="${dbDialect}"'
    }
    if ($updated -notmatch 'jakarta\.persistence\.jdbc\.driver" value="\$\{dbDriver\}"') {
        $updated = $updated -replace '<property name="jakarta\.persistence\.jdbc\.driver" value="[^"]*"', '<property name="jakarta.persistence.jdbc.driver" value="${dbDriver}"'
    }
    if ($updated -notmatch 'jakarta\.persistence\.jdbc\.url" value="\$\{dbUrl\}"') {
        $updated = $updated -replace '<property name="jakarta\.persistence\.jdbc\.url" value="[^"]*"', '<property name="jakarta.persistence.jdbc.url" value="${dbUrl}"'
    }
    if ($updated -notmatch 'jakarta\.persistence\.jdbc\.user" value="\$\{dbUser\}"') {
        $updated = $updated -replace '<property name="jakarta\.persistence\.jdbc\.user" value="[^"]*"', '<property name="jakarta.persistence.jdbc.user" value="${dbUser}"'
    }
    if ($updated -notmatch 'jakarta\.persistence\.jdbc\.password" value="\$\{dbPassword\}"') {
        $updated = $updated -replace '<property name="jakarta\.persistence\.jdbc\.password" value="[^"]*"', '<property name="jakarta.persistence.jdbc.password" value="${dbPassword}"'
    }
    $updated = $updated -replace '__HI_DB_PATH__', '${dbBasePath}'

    if ($updated -ne $content) {
        Set-Content -Path $FilePath -Value $updated -NoNewline
        return $true
    }

    return $false
}

function Get-PersistenceXmlFiles {
    param([string]$ServerRoot)

    $files = @(
        (Join-Path $ServerRoot "presentation\src\main\resources\META-INF\persistence.xml"),
        (Join-Path $ServerRoot "presentation\src\test\resources\META-INF\persistence.xml")
    )

    $targetRoot = Join-Path $ServerRoot "presentation\target"
    if (Test-Path $targetRoot) {
        Get-ChildItem -Path $targetRoot -Recurse -Filter "persistence.xml" -ErrorAction SilentlyContinue |
            ForEach-Object { $files += $_.FullName }
    }

    return $files | Select-Object -Unique
}