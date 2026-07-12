param(
    [string]$MavenPath = "",
    [string]$MavenVersion = "3.9.16"
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot

function Resolve-Maven {
    param(
        [string]$PreferredPath,
        [string]$Version
    )

    if ($PreferredPath -and (Test-Path -LiteralPath $PreferredPath)) {
        return (Resolve-Path -LiteralPath $PreferredPath).Path
    }

    $fromPath = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if ($fromPath) {
        return $fromPath.Source
    }

    $fromPath = Get-Command mvn -ErrorAction SilentlyContinue
    if ($fromPath) {
        return $fromPath.Source
    }

    $ideaMaven = "E:\idea\IntelliJ IDEA 2024.3.1.1\plugins\maven\lib\maven3\bin\mvn.cmd"
    if (Test-Path -LiteralPath $ideaMaven) {
        return $ideaMaven
    }

    Write-Host "Maven was not found. Installing project-local Maven $Version..."
    $installer = Join-Path $PSScriptRoot "setup-maven.ps1"
    $installedMaven = & $installer -Version $Version | Select-Object -Last 1
    if ($installedMaven -and (Test-Path -LiteralPath $installedMaven)) {
        return (Resolve-Path -LiteralPath $installedMaven).Path
    }

    throw "Maven install failed. Install Maven manually or pass -MavenPath."
}

$maven = Resolve-Maven -PreferredPath $MavenPath -Version $MavenVersion
Push-Location $projectRoot
try {
    & $maven clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        throw "Maven build failed with exit code $LASTEXITCODE."
    }
}
finally {
    Pop-Location
}
