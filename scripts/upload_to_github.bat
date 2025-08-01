@echo off
REM Script para subir NeuraMirror a un repositorio GitHub existente
REM Autor: Francisco Molina

echo ========================================================
echo         Subida de NeuraMirror a GitHub
echo ========================================================
echo.

REM Navegar al directorio del proyecto
cd /d D:\NeuraMirror

REM Verificar si Git está instalado
git --version > nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Git no está instalado o no está en el PATH.
    echo Por favor, instale Git desde https://git-scm.com/downloads
    exit /b 1
)

REM Inicializar repositorio Git si no existe
if not exist .git (
    echo Inicializando repositorio Git...
    git init
) else (
    echo Repositorio Git ya inicializado.
)

REM Configurar usuario Git si es necesario
git config --get user.name > nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Configurando usuario Git...
    git config --global user.name "Francisco Molina"
    git config --global user.email "pako.molina@gmail.com"
)

REM Verificar si existe el repositorio remoto
git remote get-url origin > nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Añadiendo repositorio remoto...
    git remote add origin https://github.com/Yatrogenesis/NeuraMirror.git
) else (
    echo Actualizando URL del repositorio remoto...
    git remote set-url origin https://github.com/Yatrogenesis/NeuraMirror.git
)

REM Crear archivo .gitignore si no existe
if not exist .gitignore (
    echo Creando archivo .gitignore...
    (
        echo # Archivos compilados y binarios
        echo *.apk
        echo *.aab
        echo *.ap_
        echo *.dex
        echo *.class
        echo.
        echo # Archivos de registro
        echo *.log
        echo.
        echo # Archivos de configuración local
        echo local.properties
        echo keystore.properties
        echo *.keystore
        echo *.jks
        echo.
        echo # Gradle
        echo .gradle/
        echo build/
        echo captures/
        echo .externalNativeBuild/
        echo .cxx/
        echo.
        echo # IntelliJ/Android Studio
        echo *.iml
        echo .idea/
        echo misc.xml
        echo deploymentTargetDropDown.xml
        echo render.experimental.xml
        echo.
        echo # Carpetas generadas
        echo bin/
        echo gen/
        echo out/
        echo.
        echo # Archivos macOS
        echo .DS_Store
        echo.
        echo # Archivos específicos de la aplicación
        echo /app/release
        echo /app/debug
    ) > .gitignore
)

REM Añadir todos los archivos al repositorio
echo Añadiendo archivos al repositorio...
git add .

REM Realizar el commit inicial
echo Realizando el commit inicial...
git commit -m "Versión inicial de NeuraMirror - Clonador de voz basado en el protocolo AION"

REM Subir el código al repositorio remoto (rama principal)
echo Subiendo código al repositorio remoto (rama master)...
git push -u origin master
if %ERRORLEVEL% neq 0 (
    echo Intentando subir a la rama main...
    git push -u origin main
    if %ERRORLEVEL% neq 0 (
        echo ERROR: No se pudo subir el código a GitHub.
        echo Asegúrese de que el repositorio remoto esté configurado correctamente
        echo y que tenga permisos para subir código.
        exit /b 1
    )
)

echo.
echo ========================================================
echo       Código subido exitosamente a GitHub
echo ========================================================

pause
