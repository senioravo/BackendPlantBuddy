# ====================================================
# Script simple para ejecutar Backend Plant Buddy
# ====================================================

param(
    [string]$Action = "start"
)

$ProjectDir = $PSScriptRoot
$Gradlew = Join-Path $ProjectDir "gradlew.bat"

function Start-Backend {
    Write-Host "======================================"
    Write-Host "  Backend Plant Buddy"
    Write-Host "======================================"
    Write-Host ""
    Write-Host "[INFO] El servidor se iniciara en esta ventana"
    Write-Host "[INFO] Presiona Ctrl+C para detenerlo"
    Write-Host ""
    Write-Host "[URL]  http://localhost:8080/api"
    Write-Host ""
    Write-Host "======================================"
    Write-Host ""
    
    & $Gradlew bootRun
}

function Quick-Start {
    Write-Host "[BUILD] Compilando..." -ForegroundColor Cyan
    & $Gradlew clean build -x test
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Compilacion exitosa" -ForegroundColor Green
        Write-Host ""
        Start-Backend
    }
    else {
        Write-Host "[ERROR] Fallo la compilacion" -ForegroundColor Red
        exit 1
    }
}

function Test-Backend {
    Write-Host "[TEST] Probando conexion al backend..." -ForegroundColor Cyan
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/productos" -Method GET -TimeoutSec 5 -UseBasicParsing
        Write-Host "[OK] Backend respondiendo correctamente" -ForegroundColor Green
        Write-Host "[STATUS] Codigo HTTP: $($response.StatusCode)" -ForegroundColor Cyan
    }
    catch {
        Write-Host "[ERROR] Backend no responde o no esta iniciado" -ForegroundColor Red
        Write-Host "        Error: $($_.Exception.Message)"
    }
}

switch ($Action.ToLower()) {
    "start" {
        Start-Backend
    }
    "build" {
        Quick-Start
    }
    "test" {
        Test-Backend
    }
    "help" {
        Write-Host "Uso: ./run.ps1 [comando]"
        Write-Host ""
        Write-Host "Comandos:"
        Write-Host "  start  - Iniciar backend (sin recompilar)"
        Write-Host "  build  - Compilar e iniciar backend"
        Write-Host "  test   - Probar si el backend responde"
        Write-Host "  help   - Mostrar esta ayuda"
        Write-Host ""
        Write-Host "Para ejecutar en segundo plano:"
        Write-Host "  Start-Process powershell -ArgumentList '-File ./run.ps1 start'"
    }
    default {
        Write-Host "[ERROR] Comando desconocido: $Action" -ForegroundColor Red
        Write-Host "        Usa './run.ps1 help' para ver comandos disponibles"
        exit 1
    }
}
