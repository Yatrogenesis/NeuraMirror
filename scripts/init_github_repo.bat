@echo off
REM Script para inicializar y subir el repositorio de NeuraMirror a GitHub
REM Autor: Francisco Molina

echo ========================================================
echo        Inicialización del repositorio NeuraMirror
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

REM Realizar el primer commit
echo Realizando el primer commit...
git commit -m "Versión inicial de NeuraMirror - Clonador de voz basado en el protocolo AION"

REM Preguntar si se desea crear el repositorio remoto en GitHub ahora
set /p CREATE_REPO=¿Desea crear el repositorio remoto en GitHub ahora? (s/n): 

if /i "%CREATE_REPO%"=="s" (
    REM Verificar si Chrome está instalado
    if exist "C:\Program Files\Google\Chrome\Application\chrome.exe" (
        echo Abriendo GitHub en Chrome para crear el repositorio...
        start "Chrome" "C:\Program Files\Google\Chrome\Application\chrome.exe" "https://github.com/new"
        
        echo.
        echo Por favor, complete la creación del repositorio en el navegador:
        echo 1. Inicie sesión en GitHub (si aún no lo ha hecho)
        echo 2. Establezca el nombre del repositorio como "NeuraMirror"
        echo 3. Añada la descripción: "Clonador de voz para Android basado en el protocolo AION"
        echo 4. Seleccione "Público"
        echo 5. No inicialice el repositorio con README, .gitignore o licencia
        echo 6. Haga clic en "Crear repositorio"
        echo.
        
        pause
        
        REM Añadir el repositorio remoto
        echo Añadiendo repositorio remoto...
        git remote add origin https://github.com/Yatrogenesis/NeuraMirror.git
        
        REM Subir el código al repositorio
        echo Subiendo código al repositorio remoto...
        git push -u origin master
        
        echo.
        echo ¡Repositorio creado y código subido exitosamente!
    ) else (
        echo Chrome no está instalado en la ruta estándar.
        echo Por favor, cree el repositorio manualmente en https://github.com/new
        echo.
        echo Una vez creado, ejecute los siguientes comandos:
        echo git remote add origin https://github.com/Yatrogenesis/NeuraMirror.git
        echo git push -u origin master
    )
) else (
    echo.
    echo Omitiendo la creación del repositorio remoto.
    echo Para crear manualmente más tarde, vaya a https://github.com/new
    echo.
    echo Luego ejecute estos comandos:
    echo git remote add origin https://github.com/Yatrogenesis/NeuraMirror.git
    echo git push -u origin master
)

echo.
echo ========================================================
echo       Inicialización del repositorio completada
echo ========================================================

pause
