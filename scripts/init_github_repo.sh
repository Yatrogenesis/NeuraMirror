#!/bin/bash

# Script para inicializar repositorio GitHub de NeuraMirror
# Autor: Francisco Molina

echo "Inicializando repositorio Git para NeuraMirror..."

# Navegar al directorio del proyecto
cd /d/NeuraMirror

# Inicializar repositorio Git
git init

# Verificar si el usuario ya está configurado
if [ -z "$(git config --get user.name)" ]; then
  echo "Configurando usuario Git..."
  git config --global user.name "Francisco Molina"
  git config --global user.email "pako.molina@gmail.com"
fi

# Crear archivo .gitignore si no existe
if [ ! -f ".gitignore" ]; then
  echo "Creando archivo .gitignore..."
  cat > .gitignore << EOL
# Archivos compilados y binarios
*.apk
*.aab
*.ap_
*.dex
*.class

# Archivos de registro
*.log

# Archivos de configuración local
local.properties
keystore.properties
*.keystore
*.jks

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

# Archivos específicos de la aplicación
/app/release
/app/debug
EOL
fi

# Agregar todos los archivos (excepto los ignorados)
git add .

# Realizar el primer commit
git commit -m "Versión inicial de NeuraMirror - Clonador de voz basado en el protocolo AION"

# Crear y configurar el repositorio remoto en GitHub usando el API de GitHub
# Nota: Esto requiere tener la CLI de GitHub instalada y configurada
# o un token personal de acceso configurado

echo "¿Desea crear el repositorio remoto en GitHub ahora? (s/n)"
read create_repo

if [ "$create_repo" = "s" ] || [ "$create_repo" = "S" ]; then
  # Verificar si gh CLI está instalado
  if command -v gh &> /dev/null; then
    echo "Creando repositorio remoto en GitHub usando GitHub CLI..."
    gh repo create Yatrogenesis/NeuraMirror --public --description "Clonador de voz para Android basado en el protocolo AION"
    
    # Añadir el repositorio remoto
    git remote add origin https://github.com/Yatrogenesis/NeuraMirror.git
    
    # Subir el código al repositorio
    git push -u origin master
    
    echo "¡Repositorio creado y código subido exitosamente!"
  else
    echo "GitHub CLI (gh) no está instalado. Por favor, instálelo o cree el repositorio manualmente."
    echo "Para crear manualmente, vaya a https://github.com/new e ingrese:"
    echo "Nombre del repositorio: NeuraMirror"
    echo "Descripción: Clonador de voz para Android basado en el protocolo AION"
    echo ""
    echo "Luego ejecute estos comandos:"
    echo "git remote add origin https://github.com/Yatrogenesis/NeuraMirror.git"
    echo "git push -u origin master"
  fi
else
  echo "Omitiendo la creación del repositorio remoto."
  echo "Para crear manualmente más tarde, vaya a https://github.com/new"
  echo "Luego ejecute estos comandos:"
  echo "git remote add origin https://github.com/Yatrogenesis/NeuraMirror.git"
  echo "git push -u origin master"
fi

echo "Inicialización del repositorio Git completada."
