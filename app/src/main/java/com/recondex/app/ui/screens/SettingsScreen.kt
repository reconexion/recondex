package com.recondex.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Acerca de RecoNDex", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Versión 1.0.0 · App educativa de seguridad ofensiva móvil.\n\n" +
                "Todas las herramientas funcionan sin root. Úsalas solo en dispositivos, " +
                "redes y sistemas de tu propiedad o con autorización explícita.\n\n" +
                "Roadmap (próximas fases, requieren root/hardware):\n" +
                "• Modo monitor WiFi vía adaptador USB externo + kernel NetHunter\n" +
                "• Frida server system-wide para instrumentación dinámica\n" +
                "• Captura de tráfico completa (VpnService avanzado)",
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
