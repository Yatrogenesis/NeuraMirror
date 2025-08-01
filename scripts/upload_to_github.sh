#!/bin/bash

# Script para subir el proyecto NeuraMirror a GitHub
# Autor: Francisco Molina

echo "Inicializando repositorio git para NeuraMirror..."

# Navegar al directorio del proyecto
cd /d/NeuraMirror

# Inicializar repositorio Git si no existe
if [ ! -d ".git" ]; then
    git init
    echo "Repositorio Git inicializado."
else
    echo "Repositorio Git ya existente."
fi

# Crear archivo .gitignore
cat > .gitignore << EOL
# Archivos compilados y binarios
*.apk
*.aar
*.ap_
*.aab
*.class
*.dex

# Archivos de registro
*.log

# Archivos de configuración local
local.properties

# Gradle
.gradle/
build/
captures/
.externalNativeBuild/
.cxx/

# IntelliJ/Android Studio
*.iml
.idea/
misc.xml
deploymentTargetDropDown.xml
render.experimental.xml

# Carpetas generadas
bin/
gen/
out/

# Archivos macOS
.DS_Store

# Carpetas específicas de la aplicación
/app/release
/app/debug
/app/src/androidTest
/app/src/test

# Archivos de claves (para seguridad)
*.jks
*.keystore
*.pepk
EOL

echo ".gitignore creado."

# Crear README.md para GitHub
# (Ya creado anteriormente)

# Agregar todos los archivos
git add .

# Commit inicial
git commit -m "Versión inicial de NeuraMirror - Clonador de voz basado en el protocolo AION"

# Configurar remote
echo "Configurando repositorio remoto..."
git remote add origin https://github.com/Yatrogenesis/NeuraMirror.git

# Subir a la rama principal
echo "Subiendo el proyecto a GitHub..."
git push -u origin master

echo "¡Proyecto subido a GitHub con éxito!"
echo "URL del repositorio: https://github.com/Yatrogenesis/NeuraMirror"
