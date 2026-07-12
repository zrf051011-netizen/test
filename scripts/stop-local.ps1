$ErrorActionPreference = "Stop"
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[Console]::OutputEncoding = $utf8NoBom
$OutputEncoding = $utf8NoBom
$projectRoot = Split-Path -Parent $PSScriptRoot
$tomcatHome = Join-Path $projectRoot "tools\apache-tomcat-9.0.120"
$catalina = Join-Path $tomcatHome "bin\catalina.bat"
$javaHome = $env:JAVA_HOME

if (-not $javaHome) {
    $javaCommand = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($javaCommand) {
        $javaHome = Split-Path -Parent (Split-Path -Parent $javaCommand.Source)
    }
}

if (-not (Test-Path -LiteralPath $catalina)) {
    Write-Host "Tomcat is not installed in tools."
    exit 0
}

$cmd = "chcp 65001 > NUL && set `"JAVA_HOME=$javaHome`" && set `"JAVA_OPTS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8`" && set `"CATALINA_HOME=$tomcatHome`" && set `"CATALINA_BASE=$tomcatHome`" && `"$catalina`" stop"
Start-Process -WindowStyle Hidden -FilePath "cmd.exe" -ArgumentList @("/c", $cmd) -WorkingDirectory (Join-Path $tomcatHome "bin") -Wait
Write-Host "Tomcat shutdown command sent."
