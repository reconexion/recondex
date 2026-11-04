# RecoNDex 🤖

App Android educativa de seguridad ofensiva móvil, con UI gamificada (rachas, XP, "lecciones")
inspirada en el estilo de apps tipo Duolingo — con identidad visual **propia** (paleta índigo/menta,
mascota original de ojitos de robot kawaii), sin usar la marca ni la mascota de Duolingo.

## ⚠️ Uso responsable

Todas las herramientas de esta app funcionan **sin root** y están pensadas para usarse
ÚNICAMENTE sobre dispositivos, redes y sistemas **propios** o con **autorización explícita**.
Usarlas contra terceros sin permiso es ilegal en la mayoría de países. La app muestra este
aviso en pantalla antes de dejarte usar cualquier herramienta.

## Herramientas incluidas (v1, sin root)

| Módulo | Qué hace |
|---|---|
| **Escáner de Red** | Descubre hosts vivos en tu subred /24 y escanea puertos TCP (connect scan) en un host objetivo |
| **Analizador de APKs** | Lista apps instaladas; muestra permisos peligrosos, componentes exportados (activities/services/receivers) y SHA-256 de la firma |
| **Escáner BLE** | Escanea dispositivos Bluetooth Low Energy cercanos, conecta y enumera servicios/características GATT |
| **Herramientas NFC** | Lee tags NDEF (texto/URI/MIME) y escribe texto en tags NFC regrabables |
| **Web Recon** | Revisa headers de seguridad (HSTS, CSP, X-Frame-Options...), rutas comunes expuestas (.env, .git/HEAD, /admin...) y una prueba heurística básica de SQLi |
| **Hash & Brute Force Lab** | Identifica tipo de hash (MD5/SHA1/SHA256/SHA512) y lo crackea contra un diccionario educativo; prueba fuerza bruta de HTTP Basic Auth |

## Roadmap (fases futuras — requieren root y/o hardware externo)

Estas NO están en la v1 porque técnicamente son imposibles sin root en un teléfono normal
(ver investigación: el chip WiFi interno de la mayoría de gamas medias no soporta modo
monitor/inyección):

- Modo monitor WiFi + deauth + captura de handshakes WPA → requiere adaptador USB externo
  (RTL8812AU/AR9271) + kernel NetHunter compilado
- Frida server system-wide para instrumentación dinámica de cualquier app
- Captura de tráfico raw completa (VPN-based MITM estilo PCAPdroid) — es un proyecto aparte
  de ingeniería (stack TCP/IP de usuario) que no se improvisa en una v1
- Bypass de SSL pinning vía repackaging con frida-gadget

## 🛠️ Cómo generar el APK

Este proyecto **no se puede compilar en el sandbox donde fue generado** (no tiene acceso a
Google Maven / Maven Central). Tienes dos caminos igual de válidos:

### Opción A — GitHub Actions (automático, recomendado)

1. Crea un repositorio nuevo y vacío en tu cuenta de GitHub.
2. Sube este proyecto completo:
   ```bash
   cd recondex
   git init
   git add .
   git commit -m "RecoNDex v1"
   git branch -M main
   git remote add origin https://github.com/TU_USUARIO/TU_REPO.git
   git push -u origin main
   ```
3. En GitHub, ve a la pestaña **Actions** de tu repo — el workflow `Build RecoNDex APK`
   se dispara solo al hacer push a `main`.
4. Cuando termine (2-4 minutos), descarga el APK desde:
   - El artifact `recondex-debug-apk` del run, o
   - La sección **Releases** del repo (el workflow crea una release automática con el APK adjunto).
5. Transfiere el `.apk` a tu teléfono e instálalo (activa "Orígenes desconocidos" si lo pide).

### Opción B — Android Studio en tu PC

1. Abre Android Studio → "Open" → selecciona la carpeta `recondex/`.
2. Deja que Gradle sincronice (descargará el SDK/dependencias automáticamente).
3. Conecta tu teléfono por USB (con depuración USB activada) o usa un emulador.
4. Run ▶ — o `Build > Build Bundle(s)/APK(s) > Build APK(s)` para solo generar el archivo.

## Nombre, ícono y marca

- Nombre: **RecoNDex** (Recon + tu apodo Dex)
- Ícono: carita de robot kawaii con antena (adaptive icon, `res/drawable/ic_launcher_foreground.xml`)
- Paleta: índigo `#5B6EF5` / menta `#2BD9A0` / ámbar `#FFC14D` — distinta a la paleta de marca
  de Duolingo a propósito (su verde y su mascota búho son marca registrada)
