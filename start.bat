@echo off
REM ==================================================
REM Script de inicio para Backend Plant Buddy (Windows CMD)
REM ==================================================

echo ======================================
echo   🌿 Backend Plant Buddy Startup
echo ======================================
echo.

REM Verificar Java
echo 🔍 Verificando instalación de Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java no está instalado
    echo    Por favor instala Java 21 o superior
    pause
    exit /b 1
)
echo ✅ Java encontrado
echo.

REM Verificar configuración de base de datos
echo 🗄️  Verificando configuración de base de datos...
findstr /C:"jdbc:postgresql://your-neon-host" src\main\resources\application.properties >nul 2>&1
if %errorlevel% == 0 (
    echo ⚠️  ADVERTENCIA: Base de datos no configurada
    echo    Edita src\main\resources\application.properties
    echo    y configura las credenciales de PostgreSQL/Neon
    echo.
    set /p continue="¿Deseas continuar de todas formas? (s/n): "
    if /i not "%continue%"=="s" exit /b 1
) else (
    echo ✅ Configuración de base de datos encontrada
)

echo.
echo ======================================
echo   Opciones de inicio
echo ======================================
echo 1) 🚀 Iniciar servidor (compilar + ejecutar)
echo 2) ⚡ Ejecutar rápido (sin recompilar)
echo 3) 🔨 Solo compilar
echo 4) 🧹 Limpiar compilación
echo 5) 🧪 Ejecutar tests
echo 6) ❌ Salir
echo.

set /p option="Selecciona una opción (1-6): "

if "%option%"=="1" goto build-and-run
if "%option%"=="2" goto quick-run
if "%option%"=="3" goto build-only
if "%option%"=="4" goto clean
if "%option%"=="5" goto test
if "%option%"=="6" goto end
goto invalid

:build-and-run
echo.
echo 🔨 Compilando el proyecto...
echo.
call gradlew.bat clean build -x test
if errorlevel 1 (
    echo ❌ Error en la compilación
    pause
    exit /b 1
)
echo ✅ Compilación exitosa
goto start-server

:quick-run
echo.
echo ⚡ Ejecutando sin recompilar...
goto start-server

:build-only
echo.
echo 🔨 Compilando el proyecto...
call gradlew.bat clean build -x test
if errorlevel 1 (
    echo ❌ Error en la compilación
    pause
    exit /b 1
)
echo.
echo ✅ Proyecto compilado exitosamente
echo    Ejecuta 'start.bat' y selecciona opción 2 para iniciar
pause
exit /b 0

:clean
echo.
echo 🧹 Limpiando compilación...
call gradlew.bat clean
echo ✅ Limpieza completada
pause
exit /b 0

:test
echo.
echo 🧪 Ejecutando tests...
call gradlew.bat test
pause
exit /b 0

:start-server
echo.
echo ======================================
echo   🚀 Iniciando Backend Plant Buddy
echo ======================================
echo.
echo 📡 El servidor estará disponible en:
echo    http://localhost:8080/api
echo.
echo 📝 Endpoints disponibles:
echo    - POST /api/auth/register
echo    - POST /api/auth/login
echo    - GET  /api/productos
echo    - GET  /api/plantel/usuario/{userId}
echo    - POST /api/compras/crear
echo.
echo ⏹️  Presiona Ctrl+C para detener el servidor
echo.
echo ======================================
echo.
call gradlew.bat bootRun
goto end

:invalid
echo ❌ Opción inválida
pause
exit /b 1

:end
echo.
echo 👋 ¡Hasta luego!
pause
