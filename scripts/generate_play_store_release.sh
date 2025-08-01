#!/bin/bash

# Script para generar APK firmado y preparado para Google Play Store
# Autor: Francisco Molina

echo "==========================================================="
echo "     Generación de APK para Google Play Store"
echo "==========================================================="
echo

# Navegar al directorio del proyecto
cd /d/NeuraMirror

# Verificar si existe el directorio para archivos de publicación
if [ ! -d "app/release" ]; then
  mkdir -p app/release
fi

# Verificar si existe la configuración de firma
if [ ! -f "keystore.properties" ]; then
  echo "ERROR: No se encontró el archivo keystore.properties"
  echo "Creando un archivo de ejemplo..."
  
  cat > keystore.properties << EOL
# Configuración para la firma de la aplicación
# Reemplace estos valores con su información real
storePassword=REPLACE_WITH_STORE_PASSWORD
keyPassword=REPLACE_WITH_KEY_PASSWORD
keyAlias=neuramirror
storeFile=../keystore/neuramirror-keystore.jks
EOL

  echo "Por favor, actualice el archivo keystore.properties con su información real."
  echo "Si no tiene una clave de firma, puede crear una con el siguiente comando:"
  echo "keytool -genkey -v -keystore keystore/neuramirror-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias neuramirror"
  
  # Crear directorio para keystore
  mkdir -p keystore
  
  exit 1
fi

# Verificar si está instalado Gradle
if ! command -v ./gradlew &> /dev/null; then
  echo "ERROR: No se encontró el archivo gradlew."
  echo "Asegúrese de estar en el directorio raíz del proyecto y que el archivo gradlew exista."
  exit 1
fi

# Limpiar el proyecto
echo "Limpiando el proyecto..."
./gradlew clean
if [ $? -ne 0 ]; then
  echo "ERROR: No se pudo limpiar el proyecto."
  exit 1
fi

# Generar APK de versión
echo "Generando APK firmado..."
./gradlew assembleRelease
if [ $? -ne 0 ]; then
  echo "ERROR: No se pudo generar el APK de release."
  exit 1
fi

# Generar Bundle de Android (AAB)
echo "Generando Android App Bundle (AAB)..."
./gradlew bundleRelease
if [ $? -ne 0 ]; then
  echo "ERROR: No se pudo generar el Android App Bundle."
  exit 1
fi

# Copiar los archivos generados a la carpeta de release
echo "Copiando archivos a la carpeta de release..."
cp app/build/outputs/apk/release/app-release.apk app/release/neuramirror-1.0.0.apk
cp app/build/outputs/bundle/release/app-release.aab app/release/neuramirror-1.0.0.aab

# Generar información de la versión
echo "Generando información de la versión..."
APP_VERSION=$(grep "versionName" app/build.gradle | grep -o '"[^"]*"' | sed 's/"//g')
APP_VERSION_CODE=$(grep "versionCode" app/build.gradle | grep -o '[0-9]*')

cat > app/release/release_info.txt << EOL
NeuraMirror
Versión: $APP_VERSION
Código de versión: $APP_VERSION_CODE
Fecha de compilación: $(date +"%d/%m/%Y %H:%M:%S")

APK SHA-256: $(sha256sum app/release/neuramirror-1.0.0.apk | cut -d' ' -f1)
AAB SHA-256: $(sha256sum app/release/neuramirror-1.0.0.aab | cut -d' ' -f1)

Cambios en esta versión:
- Implementación inicial del protocolo AION v2.0
- Clonación de voz de alta fidelidad
- Control de emociones en la voz generada
- Soporte para procesamiento local y cloud
- Interfaz de usuario intuitiva
- Soporte multilingüe
EOL

# Generar metadatos para Google Play Store
echo "Generando metadatos para Google Play Store..."
mkdir -p app/release/play_store_assets
cat > app/release/play_store_assets/release_notes.txt << EOL
Primera versión de NeuraMirror.

Características principales:
- Clonación de voz con protocolo AION v2.0
- Control de emociones en la voz generada
- Procesamiento local y cloud
- Soporte multilingüe
- Interfaz intuitiva y fácil de usar
EOL

echo "==========================================================="
echo "             Generación completada"
echo "==========================================================="
echo
echo "APK: app/release/neuramirror-1.0.0.apk"
echo "AAB: app/release/neuramirror-1.0.0.aab"
echo "Información de la versión: app/release/release_info.txt"
echo "Notas de la versión: app/release/play_store_assets/release_notes.txt"
echo
echo "Recuerde que el formato AAB es el requerido para Google Play Store."
echo "Para más información sobre cómo subir la aplicación a Google Play Store,"
echo "consulte el archivo docs/google_play_upload_guide.md"
