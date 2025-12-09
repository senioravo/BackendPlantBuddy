@echo off
REM ====================================================
REM Ejecutar Backend Plant Buddy en nueva ventana
REM ====================================================

echo ======================================
echo   Backend Plant Buddy - Quick Start
echo ======================================
echo.
echo [INFO] Abriendo backend en nueva ventana...
echo [URL]  http://localhost:8080/api
echo.
echo Para detener: Cierra la nueva ventana
echo.

start "Backend Plant Buddy" cmd /k "cd /d %~dp0 && gradlew.bat bootRun"

echo [OK] Backend iniciado en nueva ventana
echo.
pause
