@echo off
REM Script para generar APK firmado y preparado para Google Play Store
REM Autor: Francisco Molina

echo ==========================================================
echo      Generación de APK para Google Play Store
echo ==========================================================
echo.

REM Navegar al directorio del proyecto
cd /d D:\NeuraMirror

REM Verificar si existe el directorio para archivos de publicación
if not exist app\release (
    mkdir app\release
)

REM Verificar si existe la configuración de firma
if not exist keystore.properties (
    echo ERROR: No se encontró el archivo keystore.properties
    echo Creando un archivo de ejemplo...
    
    (
        echo # Configuración para la firma de la aplicación
        echo # Reemplace estos valores con su información real
        echo storePassword=REPLACE_WITH_STORE_PASSWORD
        echo keyPassword=REPLACE_WITH_KEY_PASSWORD
        echo keyAlias=neuramirror
        echo storeFile=../keystore/neuramirror-keystore.jks
    ) > keystore.properties
    
    echo Por favor, actualice el archivo keystore.properties con su información real.
    echo Si no tiene una clave de firma, puede crear una con el siguiente comando:
    echo keytool -genkey -v -keystore keystore\neuramirror-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias neuramirror
    
    REM Crear directorio para keystore
    if not exist keystore mkdir keystore
    
    exit /b 1
)

REM Verificar si está instalado Gradle
if not exist gradlew.bat (
    echo ERROR: No se encontró el archivo gradlew.bat.
    echo Asegúrese de estar en el directorio raíz del proyecto y que el archivo gradlew.bat exista.
    exit /b 1
)

REM Limpiar el proyecto
echo Limpiando el proyecto...
call gradlew clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: No se pudo limpiar el proyecto.
    exit /b 1
)

REM Generar APK de versión
echo Generando APK firmado...
call gradlew assembleRelease
if %ERRORLEVEL% neq 0 (
    echo ERROR: No se pudo generar el APK de release.
    exit /b 1
)

REM Generar Bundle de Android (AAB)
echo Generando Android App Bundle (AAB)...
call gradlew bundleRelease
if %ERRORLEVEL% neq 0 (
    echo ERROR: No se pudo generar el Android App Bundle.
    exit /b 1
)

REM Copiar los archivos generados a la carpeta de release
echo Copiando archivos a la carpeta de release...
copy app\build\outputs\apk\release\app-release.apk app\release\neuramirror-1.0.0.apk
copy app\build\outputs\bundle\release\app-release.aab app\release\neuramirror-1.0.0.aab

REM Generar información de la versión
echo Generando información de la versión...
set APP_VERSION=1.0.0
set APP_VERSION_CODE=1

(
    echo NeuraMirror
    echo Versión: %APP_VERSION%
    echo Código de versión: %APP_VERSION_CODE%
    echo Fecha de compilación: %date% %time%
    echo.
    echo APK SHA-256: [Ejecute 'certutil -hashfile app\release\neuramirror-1.0.0.apk SHA256' para obtener el hash]
    echo AAB SHA-256: [Ejecute 'certutil -hashfile app\release\neuramirror-1.0.0.aab SHA256' para obtener el hash]
    echo.
    echo Cambios en esta versión:
    echo - Implementación inicial del protocolo AION v2.0
    echo - Clonación de voz de alta fidelidad
    echo - Control de emociones en la voz generada
    echo - Soporte para procesamiento local y cloud
    echo - Interfaz de usuario intuitiva
    echo - Soporte multilingüe
) > app\release\release_info.txt

REM Generar metadatos para Google Play Store
echo Generando metadatos para Google Play Store...
if not exist app\release\play_store_assets mkdir app\release\play_store_assets
(
    echo Primera versión de NeuraMirror.
    echo.
    echo Características principales:
    echo - Clonación de voz con protocolo AION v2.0
    echo - Control de emociones en la voz generada
    echo - Procesamiento local y cloud
    echo - Soporte multilingüe
    echo - Interfaz intuitiva y fácil de usar
) > app\release\play_store_assets\release_notes.txt

echo ===========================================================
echo              Generación completada
echo ===========================================================
echo.
echo APK: app\release\neuramirror-1.0.0.apk
echo AAB: app\release\neuramirror-1.0.0.aab
echo Información de la versión: app\release\release_info.txt
echo Notas de la versión: app\release\play_store_assets\release_notes.txt
echo.
echo Recuerde que el formato AAB es el requerido para Google Play Store.
echo Para más información sobre cómo subir la aplicación a Google Play Store,
echo consulte el archivo docs\google_play_upload_guide.md

pause
