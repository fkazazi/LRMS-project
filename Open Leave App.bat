@echo off
title Leave Requests Management System
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0open-app.ps1"
if errorlevel 1 pause
