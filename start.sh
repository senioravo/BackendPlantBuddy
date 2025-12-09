#!/bin/bash

# ==================================================
# Script de inicio para Backend Plant Buddy
# ==================================================

echo "======================================"
echo "  🌿 Backend Plant Buddy Startup"
echo "======================================"
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Variables
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
GRADLEW="./gradlew"

# Detectar sistema operativo
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    GRADLEW="./gradlew.bat"
fi

echo "📁 Directorio del proyecto: $PROJECT_DIR"
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "$GRADLEW" ]; then
    echo -e "${RED}❌ Error: No se encuentra gradlew en el directorio actual${NC}"
    echo "   Asegúrate de ejecutar este script desde la raíz del proyecto"
    exit 1
fi

# Función para verificar Java
check_java() {
    echo "🔍 Verificando instalación de Java..."
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
        echo -e "${GREEN}✅ Java encontrado: $JAVA_VERSION${NC}"
        return 0
    else
        echo -e "${RED}❌ Java no está instalado${NC}"
        echo "   Por favor instala Java 21 o superior"
        exit 1
    fi
}

# Función para verificar PostgreSQL
check_postgres() {
    echo ""
    echo "🗄️  Verificando configuración de base de datos..."
    
    if grep -q "jdbc:postgresql://your-neon-host" src/main/resources/application.properties; then
        echo -e "${YELLOW}⚠️  ADVERTENCIA: Base de datos no configurada${NC}"
        echo "   Edita src/main/resources/application.properties"
        echo "   y configura las credenciales de PostgreSQL/Neon"
        echo ""
        read -p "¿Deseas continuar de todas formas? (s/n): " -n 1 -r
        echo ""
        if [[ ! $REPLY =~ ^[Ss]$ ]]; then
            exit 1
        fi
    else
        echo -e "${GREEN}✅ Configuración de base de datos encontrada${NC}"
    fi
}

# Función para limpiar y compilar
build_project() {
    echo ""
    echo "🔨 Compilando el proyecto..."
    echo ""
    
    chmod +x $GRADLEW
    
    if $GRADLEW clean build -x test; then
        echo ""
        echo -e "${GREEN}✅ Compilación exitosa${NC}"
        return 0
    else
        echo ""
        echo -e "${RED}❌ Error en la compilación${NC}"
        exit 1
    fi
}

# Función para iniciar el servidor
start_server() {
    echo ""
    echo "======================================"
    echo "  🚀 Iniciando Backend Plant Buddy"
    echo "======================================"
    echo ""
    echo "📡 El servidor estará disponible en:"
    echo "   http://localhost:8080/api"
    echo ""
    echo "📝 Endpoints disponibles:"
    echo "   - POST /api/auth/register"
    echo "   - POST /api/auth/login"
    echo "   - GET  /api/productos"
    echo "   - GET  /api/plantel/usuario/{userId}"
    echo "   - POST /api/compras/crear"
    echo ""
    echo "⏹️  Presiona Ctrl+C para detener el servidor"
    echo ""
    echo "======================================"
    echo ""
    
    $GRADLEW bootRun
}

# Menú principal
show_menu() {
    echo ""
    echo "======================================"
    echo "  Opciones de inicio"
    echo "======================================"
    echo "1) 🚀 Iniciar servidor (compilar + ejecutar)"
    echo "2) ⚡ Ejecutar rápido (sin recompilar)"
    echo "3) 🔨 Solo compilar"
    echo "4) 🧹 Limpiar compilación"
    echo "5) 🧪 Ejecutar tests"
    echo "6) ❌ Salir"
    echo ""
    read -p "Selecciona una opción (1-6): " option
    
    case $option in
        1)
            check_java
            check_postgres
            build_project
            start_server
            ;;
        2)
            check_java
            echo ""
            echo "⚡ Ejecutando sin recompilar..."
            start_server
            ;;
        3)
            check_java
            build_project
            echo ""
            echo -e "${GREEN}✅ Proyecto compilado exitosamente${NC}"
            echo "   Ejecuta './start.sh' y selecciona opción 2 para iniciar"
            ;;
        4)
            echo ""
            echo "🧹 Limpiando compilación..."
            $GRADLEW clean
            echo -e "${GREEN}✅ Limpieza completada${NC}"
            ;;
        5)
            check_java
            echo ""
            echo "🧪 Ejecutando tests..."
            $GRADLEW test
            ;;
        6)
            echo ""
            echo "👋 ¡Hasta luego!"
            exit 0
            ;;
        *)
            echo -e "${RED}❌ Opción inválida${NC}"
            show_menu
            ;;
    esac
}

# Verificar si se pasó un argumento
if [ "$1" == "quick" ]; then
    check_java
    start_server
elif [ "$1" == "build" ]; then
    check_java
    build_project
elif [ "$1" == "help" ]; then
    echo "Uso: ./start.sh [opción]"
    echo ""
    echo "Opciones:"
    echo "  (sin argumentos)  Mostrar menú interactivo"
    echo "  quick            Iniciar rápido sin recompilar"
    echo "  build            Solo compilar"
    echo "  help             Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  ./start.sh                # Menú interactivo"
    echo "  ./start.sh quick          # Inicio rápido"
    echo "  ./start.sh build          # Solo compilar"
else
    # Mostrar menú si no hay argumentos
    show_menu
fi
