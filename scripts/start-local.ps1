param(
    [int]$Port = 8080
)

$ErrorActionPreference = "Stop"
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[Console]::OutputEncoding = $utf8NoBom
$OutputEncoding = $utf8NoBom
$projectRoot = Split-Path -Parent $PSScriptRoot
$tomcatHome = Join-Path $projectRoot "tools\apache-tomcat-9.0.120"
$catalina = Join-Path $tomcatHome "bin\catalina.bat"
$serverXml = Join-Path $tomcatHome "conf\server.xml"
$warSource = Join-Path $projectRoot "target\dormitory-system.war"
$warTarget = Join-Path $tomcatHome "webapps\dormitory-system.war"
$expandedApp = Join-Path $tomcatHome "webapps\dormitory-system"
$compiledJspCache = Join-Path $tomcatHome "work\Catalina\localhost\dormitory-system"
$javaHome = $env:JAVA_HOME

if (-not $javaHome) {
    $javaCommand = Get-Command java.exe -ErrorAction SilentlyContinue
    if (-not $javaCommand) {
        throw "java.exe was not found. Install JDK or set JAVA_HOME."
    }
    $javaHome = Split-Path -Parent (Split-Path -Parent $javaCommand.Source)
}

if (-not (Test-Path -LiteralPath $catalina)) {
    & (Join-Path $PSScriptRoot "setup-tomcat9.ps1")
}

$portInUse = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
if ($portInUse) {
    throw "Port $Port is already in use. Stop that process or run with another -Port value."
}

if ($Port -ne 8080) {
    $content = Get-Content -Raw -LiteralPath $serverXml
    $content = $content -replace 'Connector port="8080"', "Connector port=`"$Port`""
    Set-Content -LiteralPath $serverXml -Value $content -Encoding UTF8
}

$serverContent = Get-Content -Raw -LiteralPath $serverXml
if ($serverContent -notmatch "Connector port=`"$Port`"(?s:.)*?URIEncoding=`"UTF-8`"") {
    $connectorPattern = "(?s)<Connector port=`"$Port`" protocol=`"HTTP/1\.1`".*?/>"
    $serverContent = [regex]::Replace($serverContent, $connectorPattern, {
        param($match)
        $match.Value -replace 'maxParameterCount="1000"', "maxParameterCount=`"1000`"`r`n               URIEncoding=`"UTF-8`"`r`n               useBodyEncodingForURI=`"true`""
    }, 1)
    Set-Content -LiteralPath $serverXml -Value $serverContent -Encoding UTF8
}

& (Join-Path $PSScriptRoot "build-war.ps1")

$tomcatRoot = [IO.Path]::GetFullPath($tomcatHome)
foreach ($generatedPath in @($expandedApp, $compiledJspCache)) {
    $fullGeneratedPath = [IO.Path]::GetFullPath($generatedPath)
    if (-not $fullGeneratedPath.StartsWith($tomcatRoot, [StringComparison]::OrdinalIgnoreCase)) {
        throw "Refusing to delete path outside Tomcat home: $fullGeneratedPath"
    }
    if (Test-Path -LiteralPath $fullGeneratedPath) {
        Remove-Item -LiteralPath $fullGeneratedPath -Recurse -Force
    }
}

Copy-Item -LiteralPath $warSource -Destination $warTarget -Force

$cmd = "chcp 65001 > NUL && set `"JAVA_HOME=$javaHome`" && set `"JAVA_OPTS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8`" && set `"CATALINA_HOME=$tomcatHome`" && set `"CATALINA_BASE=$tomcatHome`" && `"$catalina`" run"
Start-Process -WindowStyle Hidden -FilePath "cmd.exe" -ArgumentList @("/c", $cmd) -WorkingDirectory (Join-Path $tomcatHome "bin")
Start-Sleep -Seconds 5

Write-Host "Started: http://localhost:$Port/dormitory-system/"
