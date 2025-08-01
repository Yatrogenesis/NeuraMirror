@echo off
REM Script para instalar y configurar Android Studio para NeuraMirror
REM Autor: Francisco Molina

echo ========================================================
echo     Instalacion y configuracion de Android Studio
echo ========================================================
echo.

REM Crear directorio para descargas
if not exist "%USERPROFILE%\Downloads\NeuraMirror" (
    mkdir "%USERPROFILE%\Downloads\NeuraMirror"
)

REM Verificar si Java está instalado
java -version > nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ADVERTENCIA: Java JDK no detectado.
    echo Es recomendable tener instalado Java JDK 17 o superior.
    echo Puede descargarlo desde: https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
)

REM Descargar Android Studio
echo Descargando Android Studio...
echo Por favor, descargue Android Studio desde:
echo https://developer.android.com/studio
echo.
echo Guarde el instalador en: %USERPROFILE%\Downloads\NeuraMirror
echo.
echo Presione cualquier tecla cuando haya descargado el instalador...
pause > nul

REM Verificar si el instalador existe
if not exist "%USERPROFILE%\Downloads\NeuraMirror\android-studio-*.exe" (
    echo No se encontro el instalador de Android Studio.
    echo Por favor, descargue el instalador y coloquelo en:
    echo %USERPROFILE%\Downloads\NeuraMirror
    echo.
    echo Presione cualquier tecla para continuar...
    pause > nul
)

REM Instalar Android Studio
echo Instalando Android Studio...
echo Por favor, siga las instrucciones del instalador.
echo.
echo Configuracion recomendada:
echo 1. Seleccione instalacion "Standard"
echo 2. Seleccione tema claro u oscuro segun preferencia
echo 3. Acepte las licencias
echo 4. Deje que el instalador descargue los componentes necesarios
echo.
echo Presione cualquier tecla para iniciar la instalacion...
pause > nul

REM Buscar el instalador
for %%i in ("%USERPROFILE%\Downloads\NeuraMirror\android-studio-*.exe") do (
    echo Ejecutando: %%i
    start "" "%%i"
)

echo.
echo Espere a que finalice la instalacion de Android Studio...
echo.
echo Presione cualquier tecla cuando la instalacion haya terminado...
pause > nul

REM Crear carpeta para el proyecto
if not exist "D:\NeuraMirror-Project" (
    mkdir "D:\NeuraMirror-Project"
)

REM Clonar el repositorio
echo Clonando el repositorio de NeuraMirror...
cd /d D:\NeuraMirror-Project
git clone https://github.com/Yatrogenesis/NeuraMirror.git .

REM Instrucciones para configurar el proyecto
echo.
echo ========================================================
echo          Configuracion del proyecto NeuraMirror
echo ========================================================
echo.
echo Por favor, siga estos pasos para configurar el proyecto:
echo.
echo 1. Abra Android Studio
echo 2. Seleccione "Open an existing project"
echo 3. Navegue a D:\NeuraMirror-Project y abra el proyecto
echo 4. Espere a que Gradle sincronice el proyecto
echo 5. Android Studio descargara automaticamente las dependencias
echo.
echo Componentes necesarios (se instalaran automaticamente):
echo - Android SDK Platform 34
echo - Android SDK Build-Tools
echo - Android Emulator
echo - Android SDK Tools
echo.
echo Si se solicita instalar componentes adicionales, acepte la instalacion.
echo.
echo Para ejecutar la aplicacion:
echo 1. Conecte un dispositivo Android con depuracion USB habilitada, o
echo 2. Configure un emulador Android desde AVD Manager
echo 3. Seleccione Run > Run 'app'
echo.
echo ========================================================
echo                 Configuracion completa
echo ========================================================
echo.
echo Para mas informacion, consulte el archivo:
echo D:\NeuraMirror-Project\docs\final_implementation_steps.md
echo.

pause
