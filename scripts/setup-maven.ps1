param(
    [string]$Version = "3.9.16"
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$toolsDir = Join-Path $projectRoot "tools"
$mavenName = "apache-maven-$Version"
$mavenHome = Join-Path $toolsDir $mavenName
$mvnCmd = Join-Path $mavenHome "bin\mvn.cmd"
$zipPath = Join-Path $toolsDir "$mavenName-bin.zip"
$shaPath = "$zipPath.sha512"
$downloadBase = "https://dlcdn.apache.org/maven/maven-3/$Version/binaries"
$checksumBase = "https://downloads.apache.org/maven/maven-3/$Version/binaries"

if (Test-Path -LiteralPath $mvnCmd) {
    Write-Host "Maven already installed: $mavenHome"
    Write-Output $mvnCmd
    exit 0
}

New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null

if (-not (Test-Path -LiteralPath $zipPath)) {
    Invoke-WebRequest -Uri "$downloadBase/$mavenName-bin.zip" -OutFile $zipPath
}

Invoke-WebRequest -Uri "$checksumBase/$mavenName-bin.zip.sha512" -OutFile $shaPath

$expectedHash = ((Get-Content -Raw -LiteralPath $shaPath).Trim() -split "\s+")[0].ToUpperInvariant()
$actualHash = (Get-FileHash -LiteralPath $zipPath -Algorithm SHA512).Hash.ToUpperInvariant()

if ($actualHash -ne $expectedHash) {
    Remove-Item -LiteralPath $zipPath -Force
    throw "Maven download checksum mismatch. Deleted $zipPath."
}

Expand-Archive -LiteralPath $zipPath -DestinationPath $toolsDir -Force

if (-not (Test-Path -LiteralPath $mvnCmd)) {
    throw "Maven install completed, but mvn.cmd was not found: $mvnCmd"
}

Write-Host "Maven installed: $mavenHome"
Write-Output $mvnCmd
