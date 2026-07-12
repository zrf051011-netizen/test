param(
    [string]$Version = "9.0.120"
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$toolsDir = Join-Path $projectRoot "tools"
$tomcatName = "apache-tomcat-$Version"
$tomcatHome = Join-Path $toolsDir $tomcatName
$zipPath = Join-Path $toolsDir "$tomcatName.zip"
$shaPath = "$zipPath.sha512"
$downloadBase = "https://dlcdn.apache.org/tomcat/tomcat-9/v$Version/bin"

if (Test-Path -LiteralPath (Join-Path $tomcatHome "bin\catalina.bat")) {
    Write-Host "Tomcat already installed: $tomcatHome"
    exit 0
}

New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null

if (-not (Test-Path -LiteralPath $zipPath)) {
    Invoke-WebRequest -Uri "$downloadBase/$tomcatName.zip" -OutFile $zipPath
}

Invoke-WebRequest -Uri "$downloadBase/$tomcatName.zip.sha512" -OutFile $shaPath

$expectedHash = ((Get-Content -Raw -LiteralPath $shaPath).Trim() -split "\s+")[0].ToUpperInvariant()
$actualHash = (Get-FileHash -LiteralPath $zipPath -Algorithm SHA512).Hash.ToUpperInvariant()

if ($actualHash -ne $expectedHash) {
    Remove-Item -LiteralPath $zipPath -Force
    throw "Tomcat download checksum mismatch. Deleted $zipPath."
}

Expand-Archive -LiteralPath $zipPath -DestinationPath $toolsDir -Force

Write-Host "Tomcat installed: $tomcatHome"

