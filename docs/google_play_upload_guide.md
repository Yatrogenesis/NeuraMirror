# Guía para Publicar NeuraMirror en Google Play Store

Este documento detalla los pasos necesarios para preparar y publicar la aplicación NeuraMirror en Google Play Store.

## Requisitos previos

1. **Cuenta de desarrollador de Google Play**:
   - Crear una cuenta de desarrollador en la [Google Play Console](https://play.google.com/console/about/)
   - Pagar la tarifa de registro única de $25 USD

2. **Preparación de firma de la aplicación**:
   - Generar una clave de firma para la aplicación
   - Configurar el archivo `keystore.properties` con la información de la clave

## Paso 1: Generar la clave de firma

```bash
keytool -genkey -v -keystore neuramirror-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias neuramirror
```

Guarda la clave generada en una ubicación segura y crea un archivo `keystore.properties` en la raíz del proyecto con el siguiente contenido:

```properties
storePassword=YOUR_STORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=neuramirror
storeFile=../path/to/neuramirror-keystore.jks
```

## Paso 2: Configurar el APK firmado

1. Editar el archivo `app/build.gradle` para incluir la configuración de firma:

```groovy
android {
    signingConfigs {
        release {
            def keystoreProperties = new Properties()
            def keystorePropertiesFile = rootProject.file('keystore.properties')
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
                storeFile file(keystoreProperties['storeFile'])
                storePassword keystoreProperties['storePassword']
                keyAlias keystoreProperties['keyAlias']
                keyPassword keystoreProperties['keyPassword']
            }
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            // otras configuraciones...
        }
    }
}
```

## Paso 3: Preparar los recursos gráficos

La aplicación requiere los siguientes recursos gráficos para Google Play Store:

1. **Icono de la aplicación**: Ya incluido en el proyecto
2. **Gráfico de funciones** (Feature Graphic): 1024 x 500 px
3. **Capturas de pantalla**: Al menos 2 capturas de pantalla por dispositivo
   - Teléfono: 16:9 (mínimo 320 px de ancho)
   - Tablet: 16:9 (mínimo 1080 px de ancho)
4. **Gráfico promocional** (opcional): 180 x 120 px

Todos estos recursos se deben guardar en la carpeta `D:\NeuraMirror\docs\play_store_assets\`.

## Paso 4: Compilar el APK/Bundle firmado

1. Desde Android Studio:
   - Menú: Build > Generate Signed Bundle / APK
   - Seleccionar "Android App Bundle" (recomendado) o "APK"
   - Seleccionar la clave de firma
   - Seleccionar "release" build variant
   - Finalizar

2. Desde línea de comandos:
   ```bash
   ./gradlew bundleRelease
   ```

El archivo resultante se guardará en `app/build/outputs/bundle/release/app-release.aab`

## Paso 5: Crear la ficha de Play Store

Inicia sesión en la [Google Play Console](https://play.google.com/console/) y sigue estos pasos:

1. Haz clic en "Crear app"
2. Completa la información básica:
   - Nombre de la aplicación: NeuraMirror
   - Idioma predeterminado: Español (México)
   - Tipo de aplicación: Aplicación
   - Gratis o de pago: Gratis
   - Categoría: Herramientas

3. Completa la ficha de Play Store:
   - Descripción breve (80 caracteres):
     ```
     Clona voces con alta fidelidad y controla las emociones de la voz generada.
     ```
   - Descripción completa (4000 caracteres):
     ```
     NeuraMirror es una revolucionaria aplicación de clonación de voz que utiliza el protocolo AION (Artificial Intelligence Orchestration Network) para crear réplicas de voz indistinguibles del original.

     ¡Crea tu propio modelo de voz con tan solo 5-10 segundos de audio y sintetiza textos con diferentes emociones!

     CARACTERÍSTICAS PRINCIPALES:

     ✓ Clonación de voz de alta fidelidad: Crea réplicas precisas de voces con sólo unos segundos de audio.
     ✓ Control de emociones: Ajusta la expresión emocional de la voz (neutral, feliz, triste, enojado, sorprendido).
     ✓ Procesamiento local o cloud: Elige entre máxima privacidad o rendimiento.
     ✓ Adaptación neuroplástica: El modelo mejora continuamente con el uso.
     ✓ Soporte multilingüe: Compatible con 17+ idiomas.
     ✓ Interfaz intuitiva: Diseño elegante y fácil de usar.
     ✓ Exportación flexible: Guarda el audio generado en diferentes formatos.

     USOS RECOMENDADOS:

     • Accesibilidad: Ayuda para personas con discapacidades del habla.
     • Localización: Traducciones manteniendo la identidad vocal.
     • Creación de contenido: Narración para videos, podcasts o audiolibros.
     • Educación: Asistentes personalizados de aprendizaje.
     • Personalización: Crea tu asistente virtual con tu propia voz.

     PROTOCOLO AION:

     NeuraMirror implementa el protocolo AION v2.0, un sistema neuroplástico avanzado que permite:
     - Extracción precisa de características vocales.
     - Generación de embeddings de voz multidimensionales.
     - Adaptación contextual y emocional.
     - Síntesis de voz natural con modulación prosódica.

     PRIVACIDAD:

     Tu privacidad es nuestra prioridad. NeuraMirror ofrece:
     - Procesamiento local: Todos los datos permanecen en tu dispositivo.
     - Control total: Tú decides qué voces guardar y por cuánto tiempo.
     - Sin grabación en la nube: Las muestras de voz no se almacenan permanentemente.

     REQUISITOS:
     - Android 7.0 o superior
     - 3GB de RAM (4GB recomendado)
     - 100MB de espacio libre

     Descubre una nueva dimensión en la síntesis de voz con NeuraMirror. ¡Descárgala ahora y libera el poder de tu voz!
     ```

4. Sube los recursos gráficos preparados en el Paso 3

5. Completa la sección de clasificación de contenido:
   - Cuestionario de clasificación de contenido
   - Política de privacidad: https://neuramirror.ai/privacy (o cargar el archivo `docs/privacy_policy.md`)
   - Audiencia objetivo: 13+ años

6. Completa información de contacto:
   - Email: contact@neuramirror.ai
   - Sitio web: https://neuramirror.ai
   - Teléfono (opcional)

## Paso 6: Subir el APK/Bundle

1. En la sección "Versiones de producción", haz clic en "Crear nueva versión"
2. Sube el archivo AAB/APK generado en el Paso 4
3. Añade notas de la versión:
   ```
   Primera versión de NeuraMirror.
   
   Características:
   - Clonación de voz con protocolo AION v2.0
   - Control de emociones
   - Procesamiento local y cloud
   - Soporte multilingüe
   ```
4. Revisa y envía para aprobación

## Paso 7: Publicación

Una vez que la aplicación haya sido revisada y aprobada por Google (generalmente toma entre 1 y 3 días):

1. Puedes elegir entre:
   - Publicación inmediata
   - Publicación programada
   - Publicación por etapas (lanzamiento gradual)

2. Para un lanzamiento gradual (recomendado para la primera versión):
   - Selecciona "Lanzamiento por etapas a producción"
   - Inicia con un 20% de los usuarios
   - Aumenta gradualmente según el rendimiento y comentarios

## Verificaciones finales

Antes de la publicación, asegúrate de haber cumplido con todos los requisitos de Google Play:

1. **Política de privacidad** válida y accesible
2. **Términos y condiciones** claros
3. **Permisos** justificados (RECORD_AUDIO, INTERNET, etc.)
4. **Metadatos** completos y precisos
5. **Contenido** apropiado para la clasificación de edad
6. **Rendimiento** verificado en diferentes dispositivos

## Mantenimiento post-lanzamiento

Después de publicar:

1. **Monitorear** estadísticas y comentarios
2. **Responder** a las reseñas de los usuarios
3. **Actualizar** regularmente la aplicación
4. **Solucionar** problemas reportados

## Recursos adicionales

- [Políticas para desarrolladores de Google Play](https://play.google.com/about/developer-content-policy/)
- [Directrices de calidad de las aplicaciones](https://developer.android.com/docs/quality-guidelines/)
- [Optimizar la ficha de Play Store](https://developer.android.com/distribute/best-practices/launch/store-listing)
