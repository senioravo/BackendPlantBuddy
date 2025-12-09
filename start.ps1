# ==================================================
# Script de inicio para Backend Plant Buddy (Windows)
# ==================================================

param(
    [string]$Action = ""
)

$ErrorActionPreference = "Stop"

Write-Host "======================================"
Write-Host "  Backend Plant Buddy Startup"
Write-Host "======================================"
Write-Host ""

# Variables
$ProjectDir = $PSScriptRoot
$Gradlew = Join-Path $ProjectDir "gradlew.bat"

Write-Host "[DIR] Directorio del proyecto: $ProjectDir"
Write-Host ""

# Verificar que estamos en el directorio correcto
if (-not (Test-Path $Gradlew)) {
    Write-Host "[ERROR] No se encuentra gradlew.bat en el directorio actual" -ForegroundColor Red
    Write-Host "        Asegurate de ejecutar este script desde la raiz del proyecto"
    exit 1
}

# Funcion para verificar Java
function Check-Java {
    Write-Host "[CHECK] Verificando instalacion de Java..."
    try {
        $javaVersion = & java -version 2>&1 | Select-String "version" | Select-Object -First 1
        Write-Host "[OK] Java encontrado: $javaVersion" -ForegroundColor Green
        return $true
    }
    catch {
        Write-Host "[ERROR] Java no esta instalado" -ForegroundColor Red
        Write-Host "        Por favor instala Java 21 o superior"
        exit 1
    }
}

# Funcion para verificar PostgreSQL
function Check-Postgres {
    Write-Host ""
    Write-Host "[DB] Verificando configuracion de base de datos..."
    
    $propsFile = Join-Path $ProjectDir "src\main\resources\application.properties"
    $content = Get-Content $propsFile -Raw
    
    if ($content -match "jdbc:postgresql://your-neon-host") {
        Write-Host "[WARN] Base de datos no configurada" -ForegroundColor Yellow
        Write-Host "       Edita src\main\resources\application.properties"
        Write-Host "       y configura las credenciales de PostgreSQL/Neon"
        Write-Host ""
        $response = Read-Host "Deseas continuar de todas formas? (s/n)"
        if ($response -ne "s" -and $response -ne "S") {
            exit 1
        }
    }
    else {
        Write-Host "[OK] Configuracion de base de datos encontrada" -ForegroundColor Green
    }
}

# Funcion para compilar el proyecto
function Build-Project {
    Write-Host ""
    Write-Host "[BUILD] Compilando el proyecto..."
    Write-Host ""
    
    try {
        & $Gradlew clean build -x test
        Write-Host ""
        Write-Host "[OK] Compilacion exitosa" -ForegroundColor Green
        return $true
    }
    catch {
        Write-Host ""
        Write-Host "[ERROR] Error en la compilacion" -ForegroundColor Red
        exit 1
    }
}

# Funcion para iniciar el servidor
function Start-Server {
    Write-Host ""
    Write-Host "======================================"
    Write-Host "  Iniciando Backend Plant Buddy"
    Write-Host "======================================"
    Write-Host ""
    Write-Host "[SERVER] El servidor estara disponible en:"
    Write-Host "         http://localhost:8080/api"
    Write-Host ""
    Write-Host "[API] Endpoints disponibles:"
    Write-Host "      - POST /api/auth/register"
    Write-Host "      - POST /api/auth/login"
    Write-Host "      - GET  /api/productos"
    Write-Host "      - GET  /api/plantel/usuario/{userId}"
    Write-Host "      - POST /api/compras/crear"
    Write-Host ""
    Write-Host "[INFO] Presiona Ctrl+C para detener el servidor"
    Write-Host ""
    Write-Host "======================================"
    Write-Host ""
    
    & $Gradlew bootRun
}

# Funcion para mostrar el menu
function Show-Menu {
    Write-Host ""
    Write-Host "======================================"
    Write-Host "  Opciones de inicio"
    Write-Host "======================================"
    Write-Host "1) Iniciar servidor (compilar + ejecutar)"
    Write-Host "2) Ejecutar rapido (sin recompilar)"
    Write-Host "3) Solo compilar"
    Write-Host "4) Limpiar compilacion"
    Write-Host "5) Ejecutar tests"
    Write-Host "6) Salir"
    Write-Host ""
    
    $option = Read-Host "Selecciona una opcion (1-6)"
    
    switch ($option) {
        "1" {
            Check-Java
            Check-Postgres
            Build-Project
            Start-Server
        }
        "2" {
            Check-Java
            Write-Host ""
            Write-Host "[RUN] Ejecutando sin recompilar..."
            Start-Server
        }
        "3" {
            Check-Java
            Build-Project
            Write-Host ""
            Write-Host "[OK] Proyecto compilado exitosamente" -ForegroundColor Green
            Write-Host "     Ejecuta '.\start.ps1' y selecciona opcion 2 para iniciar"
        }
        "4" {
            Write-Host ""
            Write-Host "[CLEAN] Limpiando compilacion..."
            & $Gradlew clean
            Write-Host "[OK] Limpieza completada" -ForegroundColor Green
        }
        "5" {
            Check-Java
            Write-Host ""
            Write-Host "[TEST] Ejecutando tests..."
            & $Gradlew test
        }
        "6" {
            Write-Host ""
            Write-Host "[EXIT] Hasta luego!"
            exit 0
        }
        default {
            Write-Host "[ERROR] Opcion invalida" -ForegroundColor Red
            Show-Menu
        }
    }
}

# Procesar argumentos
switch ($Action.ToLower()) {
    "quick" {
        Check-Java
        Start-Server
    }
    "build" {
        Check-Java
        Build-Project
    }
    "help" {
        Write-Host "Uso: .\start.ps1 [opcion]"
        Write-Host ""
        Write-Host "Opciones:"
        Write-Host "  (sin argumentos)  Mostrar menu interactivo"
        Write-Host "  quick            Iniciar rapido sin recompilar"
        Write-Host "  build            Solo compilar"
        Write-Host "  help             Mostrar esta ayuda"
        Write-Host ""
        Write-Host "Ejemplos:"
        Write-Host "  .\start.ps1                # Menu interactivo"
        Write-Host "  .\start.ps1 quick          # Inicio rapido"
        Write-Host "  .\start.ps1 build          # Solo compilar"
    }
    default {
        # Mostrar menu si no hay argumentos
        Show-Menu
    }
}
