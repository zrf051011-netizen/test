param(
    [string]$HostName = "localhost",
    [int]$Port = 3306,
    [string]$User = "root",
    [string]$Password = "123456",
    [switch]$Force
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$sqlFile = Join-Path $projectRoot "sql\dormitory_system.sql"

if (-not $Force) {
    Write-Host "This script recreates tables in database dormitory_system."
    Write-Host "Run again with -Force after confirming existing data can be overwritten."
    exit 1
}

if (-not (Test-Path -LiteralPath $sqlFile)) {
    throw "SQL file not found: $sqlFile"
}

$mysql = Get-Command mysql.exe -ErrorAction SilentlyContinue
if (-not $mysql) {
    throw "mysql.exe was not found in PATH. Install MySQL client or add it to PATH."
}

$tmpCandidates = @()
if ($env:PUBLIC) {
    $tmpCandidates += (Join-Path $env:PUBLIC "dormitory-system")
}
$tmpCandidates += "C:\tmp\dormitory-system"
$tmpCandidates += (Join-Path $projectRoot "target\mysql-import")

$tmpDir = $null
foreach ($candidate in $tmpCandidates) {
    try {
        New-Item -ItemType Directory -Force -Path $candidate | Out-Null
        $tmpDir = $candidate
        break
    }
    catch {
        continue
    }
}

if (-not $tmpDir) {
    throw "No writable temporary directory was found for MySQL import."
}

$tmpSqlFile = Join-Path $tmpDir "dormitory_system.sql"
Copy-Item -LiteralPath $sqlFile -Destination $tmpSqlFile -Force

$sqlForMysql = $tmpSqlFile.Replace("\", "/")
& $mysql.Source "-h$HostName" "-P$Port" "-u$User" "-p$Password" "--default-character-set=utf8mb4" "-e" "source $sqlForMysql"
