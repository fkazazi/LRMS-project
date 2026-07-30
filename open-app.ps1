$loginUrl = "http://localhost:8080/api/login"
Set-Location $PSScriptRoot

$jdkHome = "$env:LOCALAPPDATA\jdk-11\jdk-11.0.32+9"
if (-not (Test-Path "$jdkHome\bin\java.exe")) {
    if (Get-Command java -ErrorAction SilentlyContinue) {
        Write-Host "Using Java from PATH."
    } else {
        Write-Host ""
        Write-Host "Java was not found."
        Write-Host "Install JDK 11, or extract Temurin to:"
        Write-Host "  $env:LOCALAPPDATA\jdk-11\"
        Write-Host ""
        Read-Host "Press Enter to close"
        exit 1
    }
}

function Test-ServerReady {
    try {
        Invoke-WebRequest -Uri $loginUrl -UseBasicParsing -TimeoutSec 2 | Out-Null
        return $true
    } catch {
        return $false
    }
}

if (Test-ServerReady) {
    Start-Process $loginUrl
    Write-Host "Opened browser (server was already running)."
    Read-Host "Press Enter to close"
    exit 0
}

Start-Process powershell -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-File", (Join-Path $PSScriptRoot "run-server.ps1")

Write-Host "Starting server..."
Write-Host "Opening browser when ready (first start can take 1-2 minutes)."
for ($i = 0; $i -lt 90; $i++) {
    if (Test-ServerReady) {
        Start-Process $loginUrl
        Write-Host "Done."
        Read-Host "Press Enter to close this window"
        exit 0
    }
    Start-Sleep -Seconds 2
}

Start-Process $loginUrl
Write-Host "Browser opened. If the page fails, wait and refresh."
Read-Host "Press Enter to close"
