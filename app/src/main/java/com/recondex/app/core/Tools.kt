package com.recondex.app.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.recondex.app.ui.theme.DuoBlue
import com.recondex.app.ui.theme.DuoGreen
import com.recondex.app.ui.theme.DuoRed
import com.recondex.app.ui.theme.DuoYellow

data class ToolInfo(
    val id: String,
    val route: String,
    val title: String,
    val whatItDoes: String,
    val example: String,
    val icon: ImageVector,
    val accent: Color,
    val requiresRoot: Boolean = false,
)

val TOOLS = listOf(
    ToolInfo(
        id = "network",
        route = "network",
        title = "Escáner de Red",
        whatItDoes = "Busca qué dispositivos están conectados a tu red Wi-Fi y qué puertos tienen abiertos.",
        example = "Ejemplo: la usas en tu casa y descubres que tu smart TV tiene un puerto abierto que no sabías que existía.",
        icon = Icons.Filled.Wifi,
        accent = DuoGreen,
    ),
    ToolInfo(
        id = "apk",
        route = "apk",
        title = "Analizador de APKs",
        whatItDoes = "Revisa las apps instaladas en tu teléfono: qué permisos piden y qué partes quedan expuestas a otras apps.",
        example = "Ejemplo: te muestra que una app de linterna tiene permiso para leer tus contactos, algo que no debería necesitar.",
        icon = Icons.Filled.BugReport,
        accent = DuoBlue,
    ),
    ToolInfo(
        id = "ble",
        route = "ble",
        title = "Escáner BLE",
        whatItDoes = "Detecta dispositivos Bluetooth de bajo consumo cerca de ti y muestra sus servicios.",
        example = "Ejemplo: paseas por tu casa y ves tu banda de ejercicio, tus audífonos y una bombilla smart encendida.",
        icon = Icons.Filled.Radar,
        accent = DuoRed,
    ),
    ToolInfo(
        id = "nfc",
        route = "nfc",
        title = "Herramientas NFC",
        whatItDoes = "Lee lo que está guardado en una tarjeta o etiqueta NFC, o escribe texto en una regrabable.",
        example = "Ejemplo: acercas el teléfono a una pulsera NFC, lees el texto guardado y luego escribes tu nombre en una etiqueta nueva.",
        icon = Icons.Filled.Nfc,
        accent = DuoYellow,
    ),
    ToolInfo(
        id = "webrecon",
        route = "webrecon",
        title = "Web Recon",
        whatItDoes = "Revisa un sitio web propio: si usa conexión segura, qué protecciones tiene y si deja archivos sensibles a la vista.",
        example = "Ejemplo: revisas tu propia página y descubres un archivo con contraseñas que quedó accesible desde internet.",
        icon = Icons.Filled.Lock,
        accent = DuoGreen,
    ),
    ToolInfo(
        id = "hashlab",
        route = "hashlab",
        title = "Laboratorio de Hashes",
        whatItDoes = "Identifica qué tipo de hash es un texto cifrado y comprueba qué tan débil es una contraseña de prueba.",
        example = "Ejemplo: pegas un hash y la app te dice que es SHA-256; luego confirmas si tu contraseña de prueba sería fácil de adivinar.",
        icon = Icons.Filled.Password,
        accent = DuoBlue,
    ),
)
