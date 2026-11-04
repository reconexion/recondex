package com.recondex.app.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

data class ToolInfo(
    val id: String,
    val route: String,
    val title: String,
    val subtitle: String,
    val xp: Int,
    val icon: ImageVector,
    val requiresRoot: Boolean = false,
)

val TOOLS = listOf(
    ToolInfo(
        id = "network",
        route = "network",
        title = "Escáner de Red",
        subtitle = "Descubre hosts y puertos abiertos en tu red",
        xp = 20,
        icon = Icons.Filled.Wifi,
    ),
    ToolInfo(
        id = "apk",
        route = "apk",
        title = "Analizador de APKs",
        subtitle = "Permisos, componentes y firma de apps instaladas",
        xp = 20,
        icon = Icons.Filled.BugReport,
    ),
    ToolInfo(
        id = "ble",
        route = "ble",
        title = "Escáner BLE",
        subtitle = "Dispositivos Bluetooth cercanos y sus servicios GATT",
        xp = 15,
        icon = Icons.Filled.Radar,
    ),
    ToolInfo(
        id = "nfc",
        route = "nfc",
        title = "Herramientas NFC",
        subtitle = "Lee y escribe tags NDEF",
        xp = 15,
        icon = Icons.Filled.Nfc,
    ),
    ToolInfo(
        id = "webrecon",
        route = "webrecon",
        title = "Web Recon",
        subtitle = "Headers de seguridad, rutas expuestas y SQLi básico",
        xp = 25,
        icon = Icons.Filled.Lock,
    ),
    ToolInfo(
        id = "hashlab",
        route = "hashlab",
        title = "Hash & Brute Force Lab",
        subtitle = "Identifica/crackea hashes y prueba Basic Auth",
        xp = 25,
        icon = Icons.Filled.Password,
    ),
)
