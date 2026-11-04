package com.recondex.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DisclaimerScreen(onAccept: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.GppGood,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(72.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Antes de empezar",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "RecoNDex es una app EDUCATIVA de seguridad ofensiva. Todas sus herramientas " +
                    "(escaneo de red, análisis de APKs, BLE, NFC, web recon, hashes) están pensadas " +
                    "para usarse ÚNICAMENTE sobre:\n\n" +
                    "• Dispositivos y redes de tu propiedad\n" +
                    "• Sistemas donde tengas autorización explícita por escrito\n\n" +
                    "Usar estas herramientas contra redes, apps o servidores de terceros sin permiso " +
                    "es ilegal en la mayoría de países y puede tener consecuencias legales serias. " +
                    "Tú eres responsable de cómo usas esta app.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))
            Button(onClick = onAccept) {
                Text("Entiendo y acepto usarla de forma responsable")
            }
        }
    }
}
