# Used by Open Leave App.bat
$jdkHome = "$env:LOCALAPPDATA\jdk-11\jdk-11.0.32+9"
if (Test-Path "$jdkHome\bin\java.exe") {
    $env:JAVA_HOME = $jdkHome
    $env:Path = "$env:JAVA_HOME\bin;" + $env:Path
}
Set-Location $PSScriptRoot
Write-Host "Server running. Close this window to stop the app."
.\mvnw.cmd spring-boot:run
