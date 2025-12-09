# ====================================================
# Script para ejecutar Backend Plant Buddy en segundo plano
# ====================================================

param(
    [string]$Action = "start"
)

$ProjectDir = $PSScriptRoot
$Gradlew = Join-Path $ProjectDir "gradlew.bat"
$PidFile = Join-Path $ProjectDir "backend.pid"
$LogFile = Join-Path $ProjectDir "backend.log"

function Start-Backend {
    Write-Host "======================================"
    Write-Host "  Iniciando Backend Plant Buddy"
    Write-Host "======================================"
    Write-Host ""
    
    # Verificar si ya está corriendo
    if (Test-Path $PidFile) {
        $pid = Get-Content $PidFile
        if (Get-Process -Id $pid -ErrorAction SilentlyContinue) {
            Write-Host "[ERROR] El backend ya está corriendo (PID: $pid)" -ForegroundColor Red
            Write-Host "        Usa './run-backend.ps1 stop' para detenerlo primero"
            exit 1
        }
    }
    
    Write-Host "[BUILD] Compilando el proyecto..." -ForegroundColor Cyan
    & $Gradlew clean build -x test | Out-Null
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Fallo la compilacion" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "[OK] Compilacion exitosa" -ForegroundColor Green
    Write-Host ""
    Write-Host "[START] Iniciando servidor en segundo plano..." -ForegroundColor Cyan
    
    # Iniciar el proceso en segundo plano
    $process = Start-Process -FilePath $Gradlew -ArgumentList "bootRun" -NoNewWindow -PassThru -RedirectStandardOutput $LogFile -RedirectStandardError $LogFile
    
    # Guardar el PID
    $process.Id | Out-File -FilePath $PidFile
    
    # Esperar a que el servidor inicie
    Write-Host "[WAIT] Esperando que el servidor inicie..." -ForegroundColor Yellow
    Start-Sleep -Seconds 8
    
    # Verificar si el proceso sigue corriendo
    if (Get-Process -Id $process.Id -ErrorAction SilentlyContinue) {
        Write-Host ""
        Write-Host "======================================"
        Write-Host "  Backend iniciado exitosamente!"
        Write-Host "======================================"
        Write-Host ""
        Write-Host "[SERVER] URL: http://localhost:8080/api" -ForegroundColor Green
        Write-Host "[PID]    Process ID: $($process.Id)" -ForegroundColor Cyan
        Write-Host "[LOG]    Logs en: backend.log" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Comandos disponibles:"
        Write-Host "  ./run-backend.ps1 stop     - Detener el servidor"
        Write-Host "  ./run-backend.ps1 status   - Ver estado"
        Write-Host "  ./run-backend.ps1 logs     - Ver logs en tiempo real"
        Write-Host "  ./run-backend.ps1 restart  - Reiniciar servidor"
        Write-Host ""
    }
    else {
        Write-Host "[ERROR] El servidor no pudo iniciarse" -ForegroundColor Red
        Write-Host "        Revisa los logs: Get-Content backend.log -Tail 50"
        Remove-Item $PidFile -ErrorAction SilentlyContinue
        exit 1
    }
}

function Stop-Backend {
    Write-Host "[STOP] Deteniendo backend..." -ForegroundColor Yellow
    
    if (-not (Test-Path $PidFile)) {
        Write-Host "[WARN] No se encontro archivo PID. El backend no parece estar corriendo" -ForegroundColor Yellow
        return
    }
    
    $pid = Get-Content $PidFile
    
    if (Get-Process -Id $pid -ErrorAction SilentlyContinue) {
        Stop-Process -Id $pid -Force
        Write-Host "[OK] Backend detenido (PID: $pid)" -ForegroundColor Green
    }
    else {
        Write-Host "[WARN] Proceso no encontrado (PID: $pid)" -ForegroundColor Yellow
    }
    
    Remove-Item $PidFile -ErrorAction SilentlyContinue
}

function Get-BackendStatus {
    Write-Host "======================================"
    Write-Host "  Estado del Backend"
    Write-Host "======================================"
    Write-Host ""
    
    if (-not (Test-Path $PidFile)) {
        Write-Host "[STATUS] Backend: DETENIDO" -ForegroundColor Red
        return
    }
    
    $pid = Get-Content $PidFile
    $process = Get-Process -Id $pid -ErrorAction SilentlyContinue
    
    if ($process) {
        Write-Host "[STATUS] Backend: CORRIENDO" -ForegroundColor Green
        Write-Host "[PID]    Process ID: $pid" -ForegroundColor Cyan
        Write-Host "[CPU]    Uso CPU: $($process.CPU)" -ForegroundColor Cyan
        Write-Host "[MEM]    Memoria: $([math]::Round($process.WorkingSet64/1MB, 2)) MB" -ForegroundColor Cyan
        Write-Host "[URL]    http://localhost:8080/api" -ForegroundColor Cyan
        Write-Host ""
        
        # Verificar si el puerto está escuchando
        $connection = Test-NetConnection -ComputerName localhost -Port 8080 -WarningAction SilentlyContinue
        if ($connection.TcpTestSucceeded) {
            Write-Host "[PORT]   Puerto 8080: ACTIVO" -ForegroundColor Green
        }
        else {
            Write-Host "[PORT]   Puerto 8080: INACTIVO (aun iniciando?)" -ForegroundColor Yellow
        }
    }
    else {
        Write-Host "[STATUS] Backend: DETENIDO (proceso no encontrado)" -ForegroundColor Red
        Remove-Item $PidFile -ErrorAction SilentlyContinue
    }
}

function Show-Logs {
    if (-not (Test-Path $LogFile)) {
        Write-Host "[ERROR] No se encontro archivo de logs" -ForegroundColor Red
        return
    }
    
    Write-Host "[LOGS] Mostrando logs en tiempo real. Presiona Ctrl+C para salir" -ForegroundColor Cyan
    Write-Host ""
    Get-Content $LogFile -Tail 50 -Wait
}

function Restart-Backend {
    Write-Host "[RESTART] Reiniciando backend..." -ForegroundColor Cyan
    Stop-Backend
    Start-Sleep -Seconds 2
    Start-Backend
}

# Menu principal
switch ($Action.ToLower()) {
    "start" {
        Start-Backend
    }
    "stop" {
        Stop-Backend
    }
    "status" {
        Get-BackendStatus
    }
    "logs" {
        Show-Logs
    }
    "restart" {
        Restart-Backend
    }
    "help" {
        Write-Host "Uso: ./run-backend.ps1 [comando]"
        Write-Host ""
        Write-Host "Comandos:"
        Write-Host "  start    - Iniciar el backend en segundo plano (default)"
        Write-Host "  stop     - Detener el backend"
        Write-Host "  status   - Ver estado del backend"
        Write-Host "  logs     - Ver logs en tiempo real"
        Write-Host "  restart  - Reiniciar el backend"
        Write-Host "  help     - Mostrar esta ayuda"
        Write-Host ""
        Write-Host "Ejemplos:"
        Write-Host "  ./run-backend.ps1              # Iniciar backend"
        Write-Host "  ./run-backend.ps1 stop         # Detener backend"
        Write-Host "  ./run-backend.ps1 status       # Ver estado"
        Write-Host "  ./run-backend.ps1 logs         # Ver logs"
    }
    default {
        Write-Host "[ERROR] Comando desconocido: $Action" -ForegroundColor Red
        Write-Host "        Usa './run-backend.ps1 help' para ver comandos disponibles"
        exit 1
    }
}
