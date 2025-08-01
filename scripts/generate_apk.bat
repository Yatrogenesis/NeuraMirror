@echo off
REM Script para generar el APK de NeuraMirror
REM Autor: Francisco Molina

echo =======================================================
echo           Generación de APK para NeuraMirror
echo =======================================================
echo.

REM Verificar si existe la clave de firma
if not exist ..\keystore.properties (
    echo ERROR: No se encontró el archivo keystore.properties
    echo Cree el archivo keystore.properties con la información de su clave de firma.
    echo Ejemplo:
    echo storePassword=YOUR_STORE_PASSWORD
    echo keyPassword=YOUR_KEY_PASSWORD
    echo keyAlias=neuramirror
    echo storeFile=../path/to/neuramirror-keystore.jks
    exit /b 1
)

echo Limpiando proyecto...
cd ..
call gradlew clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: No se pudo limpiar el proyecto.
    exit /b 1
)

echo.
echo Compilando APK de depuración...
call gradlew assembleDebug
if %ERRORLEVEL% neq 0 (
    echo ERROR: No se pudo compilar el APK de depuración.
    exit /b 1
)

echo.
echo Compilando APK de release...
call gradlew assembleRelease
if %ERRORLEVEL% neq 0 (
    echo ERROR: No se pudo compilar el APK de release.
    exit /b 1
)

echo.
echo Generando bundle de Android (AAB)...
call gradlew bundleRelease
if %ERRORLEVEL% neq 0 (
    echo ERROR: No se pudo generar el bundle de Android.
    exit /b 1
)

echo.
echo =======================================================
echo                Generación completada
echo =======================================================
echo.
echo APK de depuración: app\build\outputs\apk\debug\app-debug.apk
echo APK de release: app\build\outputs\apk\release\app-release.apk
echo Bundle de Android: app\build\outputs\bundle\release\app-release.aab
echo.
echo Recuerde que el bundle (AAB) es el formato recomendado para Google Play Store.
echo.
echo Para instalar el APK de depuración en un dispositivo conectado, ejecute:
echo adb install app\build\outputs\apk\debug\app-debug.apk
echo.

pause
