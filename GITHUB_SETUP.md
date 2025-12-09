# Pasos para Subir BackendPlantBuddy a GitHub

## 1. Crear el Repositorio en GitHub

1. Ve a [GitHub](https://github.com/new)
2. Configura el repositorio:
   - **Repository name**: `BackendPlantBuddy`
   - **Description**: `Backend REST API para PlantBuddy - Spring Boot con PostgreSQL`
   - **Visibility**: Public o Private (según tu preferencia)
   - **NO marques**: "Add a README file", "Add .gitignore", "Choose a license"
   - Haz clic en "Create repository"

## 2. Conectar y Subir el Código

Una vez creado el repositorio en GitHub, ejecuta estos comandos en PowerShell:

```powershell
# Ya hemos inicializado el repositorio local y hecho el commit inicial ✓

# Conectar con el repositorio remoto de GitHub
# IMPORTANTE: Reemplaza 'senioravo' con tu nombre de usuario de GitHub
git remote add origin https://github.com/senioravo/BackendPlantBuddy.git

# Renombrar la rama principal a 'main' (si es necesario)
git branch -M main

# Subir los cambios a GitHub
git push -u origin main
```

## 3. Verificar

Después de hacer push, ve a:
```
https://github.com/senioravo/BackendPlantBuddy
```

Deberías ver todos tus archivos subidos.

## Estado Actual

✅ Repositorio git inicializado
✅ Commit inicial creado con 66 archivos
✅ Archivos incluidos:
   - Código fuente completo (controllers, services, entities, etc.)
   - Configuración (application.properties, build.gradle)
   - Documentación (README.md, ARQUITECTURA.md, etc.)
   - Scripts de inicio (start.ps1, run-backend.ps1)
   - Schema de base de datos (database-schema.sql)

## Comandos Útiles Adicionales

### Ver el estado del repositorio
```powershell
git status
```

### Ver el historial de commits
```powershell
git log --oneline
```

### Ver archivos que se van a subir
```powershell
git ls-files
```

### Si necesitas hacer cambios antes de subir
```powershell
# Agregar cambios
git add .

# Hacer commit
git commit -m "Descripción de los cambios"

# Subir
git push
```

## Nota sobre .gitignore

El proyecto ya tiene un archivo `.gitignore` que excluye:
- Archivos de compilación (`build/`, `bin/`)
- Dependencias de Gradle (`.gradle/`)
- Archivos del IDE
- Archivos temporales

Esto significa que solo se subirá el código fuente y configuraciones necesarias, no los archivos generados.
