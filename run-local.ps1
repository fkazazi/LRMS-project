# Run Annual Leave App without admin install (uses portable JDK + H2, not MySQL).
$jdkHome = "$env:LOCALAPPDATA\jdk-11\jdk-11.0.32+9"
if (-not (Test-Path "$jdkHome\bin\java.exe")) {
    Write-Host "JDK not found at $jdkHome"
    Write-Host "Download Temurin 11 JDK zip and extract to $env:LOCALAPPDATA\jdk-11\"
    exit 1
}
$env:JAVA_HOME = $jdkHome
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
Set-Location $PSScriptRoot
Write-Host "Starting app (H2 database). Login: admin@admin.com / adminadmin"
Write-Host "Open: http://localhost:8080/api/login"
.\mvnw.cmd spring-boot:run
