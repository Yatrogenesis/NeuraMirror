@echo off
REM Script para corregir el commit y evitar caracteres especiales
REM Autor: Francisco Molina

echo ========================================================
echo   Correccion de commit sin caracteres especiales
echo ========================================================
echo.

REM Navegar al directorio del proyecto
cd /d D:\NeuraMirror

REM Verificar si Git está instalado
git --version > nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Git no esta instalado o no esta en el PATH.
    echo Por favor, instale Git desde https://git-scm.com/downloads
    exit /b 1
)

REM Realizar un nuevo commit con mensaje sin caracteres especiales
echo Realizando commit con mensaje sin caracteres especiales...
git commit --amend -m "Version inicial de NeuraMirror - Clonador de voz basado en el protocolo AION"

REM Forzar la actualización en el repositorio remoto
echo Actualizando el repositorio remoto...
git push -f origin master
if %ERRORLEVEL% neq 0 (
    echo Intentando actualizar la rama main...
    git push -f origin main
    if %ERRORLEVEL% neq 0 (
        echo ERROR: No se pudo actualizar el repositorio remoto.
        exit /b 1
    )
)

echo.
echo ========================================================
echo       Commit corregido y actualizado en GitHub
echo ========================================================

pause
