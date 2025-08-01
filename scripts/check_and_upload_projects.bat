@echo off
REM Script para revisar y subir proyectos del disco D: a GitHub
REM Autor: Francisco Molina

echo ========================================================
echo     Revision y subida de proyectos a GitHub
echo ========================================================
echo.

REM Directorio a explorar
set PROJECTS_DIR=D:\

REM Lista de proyectos conocidos a revisar
set PROJECTS=NeuraMirror NoiseCancellation

REM Verificar si Git está instalado
git --version > nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Git no esta instalado o no esta en el PATH.
    echo Por favor, instale Git desde https://git-scm.com/downloads
    exit /b 1
)

REM Configuración de Git para credenciales
echo Guardando credenciales de Git para evitar solicitudes multiples...
git config --global credential.helper store

REM Revisar cada proyecto conocido
for %%p in (%PROJECTS%) do (
    echo.
    echo Revisando proyecto: %%p
    
    REM Verificar si el directorio existe en D:
    if exist "%PROJECTS_DIR%%%p" (
        echo - El proyecto %%p existe en el disco D:
        
        REM Revisar si existe en GitHub
        cd /d "%PROJECTS_DIR%%%p"
        
        REM Verificar si es un repositorio Git
        if exist ".git" (
            echo - El directorio es un repositorio Git
            
            REM Verificar si tiene repositorio remoto
            git remote -v | findstr "origin" > nul
            if %ERRORLEVEL% equ 0 (
                echo - El repositorio tiene configurado un remote origin
                
                REM Verificar si el repositorio está sincronizado
                git fetch
                git status | findstr "Your branch is up to date" > nul
                if %ERRORLEVEL% equ 0 (
                    echo - El repositorio esta sincronizado con GitHub
                ) else (
                    echo - El repositorio NO esta sincronizado con GitHub
                    echo - Se recomienda sincronizar manualmente
                )
            ) else (
                echo - El repositorio NO tiene configurado un remote origin
                echo - Configurando remote origin para %%p...
                git remote add origin https://github.com/Yatrogenesis/%%p.git
            )
        ) else (
            echo - El directorio NO es un repositorio Git
            echo - Inicializando repositorio Git...
            git init
            
            REM Crear .gitignore básico
            echo # Archivos compilados y binarios > .gitignore
            echo *.class >> .gitignore
            echo *.o >> .gitignore
            echo *.so >> .gitignore
            echo *.dll >> .gitignore
            echo *.exe >> .gitignore
            echo *.apk >> .gitignore
            echo >> .gitignore
            echo # Archivos de configuración local >> .gitignore
            echo .idea/ >> .gitignore
            echo .gradle/ >> .gitignore
            echo build/ >> .gitignore
            echo local.properties >> .gitignore
            
            REM Configurar remote origin
            echo - Configurando remote origin...
            git remote add origin https://github.com/Yatrogenesis/%%p.git
            
            REM Verificar si el repositorio existe en GitHub
            git ls-remote --exit-code origin > nul 2>&1
            if %ERRORLEVEL% equ 0 (
                echo - El repositorio %%p existe en GitHub
            ) else (
                echo - El repositorio %%p NO existe en GitHub
                echo - Debe crear manualmente el repositorio en GitHub:
                echo   https://github.com/new
                echo   Nombre: %%p
                echo.
                echo Presione cualquier tecla cuando haya creado el repositorio...
                pause > nul
            )
            
            REM Añadir archivos y hacer commit
            echo - Añadiendo archivos al repositorio...
            git add .
            
            REM Hacer commit inicial
            echo - Realizando commit inicial...
            git commit -m "Version inicial de %%p"
            
            REM Subir a GitHub
            echo - Subiendo a GitHub...
            git push -u origin master
            if %ERRORLEVEL% neq 0 (
                echo   Intentando con rama main...
                git push -u origin main
            )
        )
    ) else (
        echo - El proyecto %%p NO existe en el disco D:
    )
)

REM Buscar otros posibles proyectos en D:
echo.
echo Buscando otros posibles proyectos en D:...
for /d %%d in ("%PROJECTS_DIR%*") do (
    set "DIRNAME=%%~nxd"
    
    REM Filtrar directorios que no son proyectos
    echo !DIRNAME! | findstr /v /i "Windows Program Files Users" > nul
    if %ERRORLEVEL% equ 0 (
        REM Verificar si no está en la lista de proyectos conocidos
        echo %PROJECTS% | findstr /i "!DIRNAME!" > nul
        if %ERRORLEVEL% neq 0 (
            echo.
            echo Posible proyecto encontrado: !DIRNAME!
            echo Ruta: %%d
            
            REM Preguntar si desea revisar este proyecto
            set /p CHECK_PROJECT=Desea revisar este proyecto? (s/n): 
            
            if /i "!CHECK_PROJECT!"=="s" (
                REM Verificar si es un repositorio Git
                if exist "%%d\.git" (
                    echo - El directorio es un repositorio Git
                    
                    REM Verificar si tiene repositorio remoto
                    cd /d "%%d"
                    git remote -v | findstr "origin" > nul
                    if %ERRORLEVEL% equ 0 (
                        echo - El repositorio tiene configurado un remote origin
                    ) else (
                        echo - El repositorio NO tiene configurado un remote origin
                        
                        REM Preguntar si desea configurar el repositorio remoto
                        set /p SETUP_REMOTE=Desea configurar un repositorio remoto para !DIRNAME!? (s/n): 
                        
                        if /i "!SETUP_REMOTE!"=="s" (
                            echo - Debe crear manualmente el repositorio en GitHub:
                            echo   https://github.com/new
                            echo   Nombre: !DIRNAME!
                            echo.
                            echo Presione cualquier tecla cuando haya creado el repositorio...
                            pause > nul
                            
                            REM Configurar remote origin
                            echo - Configurando remote origin...
                            git remote add origin https://github.com/Yatrogenesis/!DIRNAME!.git
                            
                            REM Subir a GitHub
                            echo - Subiendo a GitHub...
                            git push -u origin master
                            if %ERRORLEVEL% neq 0 (
                                echo   Intentando con rama main...
                                git push -u origin main
                            )
                        )
                    )
                ) else (
                    echo - El directorio NO es un repositorio Git
                    
                    REM Preguntar si desea inicializar un repositorio Git
                    set /p INIT_GIT=Desea inicializar un repositorio Git para !DIRNAME!? (s/n): 
                    
                    if /i "!INIT_GIT!"=="s" (
                        cd /d "%%d"
                        
                        REM Inicializar repositorio Git
                        echo - Inicializando repositorio Git...
                        git init
                        
                        REM Crear .gitignore básico
                        echo # Archivos compilados y binarios > .gitignore
                        echo *.class >> .gitignore
                        echo *.o >> .gitignore
                        echo *.so >> .gitignore
                        echo *.dll >> .gitignore
                        echo *.exe >> .gitignore
                        echo *.apk >> .gitignore
                        echo >> .gitignore
                        echo # Archivos de configuración local >> .gitignore
                        echo .idea/ >> .gitignore
                        echo .gradle/ >> .gitignore
                        echo build/ >> .gitignore
                        echo local.properties >> .gitignore
                        
                        REM Preguntar si desea crear un repositorio en GitHub
                        set /p CREATE_GITHUB=Desea crear un repositorio en GitHub para !DIRNAME!? (s/n): 
                        
                        if /i "!CREATE_GITHUB!"=="s" (
                            echo - Debe crear manualmente el repositorio en GitHub:
                            echo   https://github.com/new
                            echo   Nombre: !DIRNAME!
                            echo.
                            echo Presione cualquier tecla cuando haya creado el repositorio...
                            pause > nul
                            
                            REM Configurar remote origin
                            echo - Configurando remote origin...
                            git remote add origin https://github.com/Yatrogenesis/!DIRNAME!.git
                            
                            REM Añadir archivos y hacer commit
                            echo - Añadiendo archivos al repositorio...
                            git add .
                            
                            REM Hacer commit inicial
                            echo - Realizando commit inicial...
                            git commit -m "Version inicial de !DIRNAME!"
                            
                            REM Subir a GitHub
                            echo - Subiendo a GitHub...
                            git push -u origin master
                            if %ERRORLEVEL% neq 0 (
                                echo   Intentando con rama main...
                                git push -u origin main
                            )
                        )
                    )
                )
            )
        )
    )
)

echo.
echo ========================================================
echo        Revision y subida de proyectos completada
echo ========================================================
echo.
echo Se han revisado los proyectos conocidos y se han buscado
echo otros posibles proyectos en el disco D:.
echo.
echo Para proyectos especificos, use los siguientes comandos:
echo.
echo 1. Navegue al directorio del proyecto: cd D:\[Proyecto]
echo 2. Inicialice Git: git init
echo 3. Añada archivos: git add .
echo 4. Haga commit: git commit -m "Version inicial"
echo 5. Conecte con GitHub: git remote add origin https://github.com/Yatrogenesis/[Proyecto].git
echo 6. Suba a GitHub: git push -u origin master
echo.

pause
