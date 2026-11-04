package com.recondex.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.recondex.app.core.NetworkUtils
import com.recondex.app.core.ProgressStore
import com.recondex.app.core.SoundFx
import com.recondex.app.core.TOOLS
import com.recondex.app.ui.components.CompleteToolFooter
import com.recondex.app.ui.components.DuoButton
import com.recondex.app.ui.components.ExplainerCard
import com.recondex.app.ui.components.ToolScaffold
import kotlinx.coroutines.launch

@Composable
fun NetworkScannerScreen(progress: ProgressStore) {
    val tool = remember { TOOLS.first { it.id == "network" } }
    val scope = rememberCoroutineScope()

    var scanningHosts by remember { mutableStateOf(false) }
    val hosts = remember { mutableStateOf(listOf<String>()) }

    var targetIp by remember { mutableStateOf("") }
    var portRange by remember { mutableStateOf("1-1024") }
    var scanningPorts by remember { mutableStateOf(false) }
    val openPorts = remember { mutableStateOf(listOf<Int>()) }

    ToolScaffold(title = "Escáner de Red") {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { ExplainerCard(tool.whatItDoes, tool.example, tool.accent) }
            item {
                Text(
                    "1. Descubrir hosts en tu red local",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    "Prueba conexión a puertos comunes en cada IP de tu subred /24 para ver qué dispositivos responden.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            item {
                DuoButton(
                    text = if (scanningHosts) "Escaneando..." else "Descubrir hosts",
                    color = tool.accent,
                    enabled = !scanningHosts,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    onClick = {
                        val localIp = NetworkUtils.getLocalIPv4()
                        if (localIp == null) {
                            SoundFx.error()
                            return@DuoButton
                        }
                        hosts.value = emptyList()
                        scanningHosts = true
                        scope.launch {
                            val base = NetworkUtils.subnetBaseOf(localIp)
                            NetworkUtils.discoverHosts(base) { found ->
                                hosts.value = hosts.value + found
                            }
                            scanningHosts = false
                            SoundFx.success()
                        }
                    }
                )
            }
            if (scanningHosts) item { CircularProgressIndicator(modifier = Modifier.padding(8.dp)) }
            items(hosts.value) { host ->
                Text("🟢 $host", modifier = Modifier.padding(vertical = 2.dp))
            }

            item { Divider(modifier = Modifier.padding(vertical = 14.dp)) }

            item {
                Text("2. Escanear puertos de un host", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = targetIp,
                    onValueChange = { targetIp = it },
                    label = { Text("IP objetivo (ej. 192.168.1.1)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
                OutlinedTextField(
                    value = portRange,
                    onValueChange = { portRange = it },
                    label = { Text("Rango de puertos (ej. 1-1024 o 80,443,22)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
            }
            item {
                DuoButton(
                    text = if (scanningPorts) "Escaneando..." else "Escanear puertos",
                    color = tool.accent,
                    enabled = !scanningPorts && targetIp.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    onClick = {
                        val ports = parsePortRange(portRange)
                        openPorts.value = emptyList()
                        scanningPorts = true
                        scope.launch {
                            NetworkUtils.scanPorts(targetIp, ports) { port ->
                                openPorts.value = (openPorts.value + port).sorted()
                            }
                            scanningPorts = false
                            SoundFx.success()
                        }
                    }
                )
            }
            if (scanningPorts) item { CircularProgressIndicator(modifier = Modifier.padding(8.dp)) }
            items(openPorts.value) { port ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text("🔓 Puerto $port — ${NetworkUtils.serviceNameFor(port)}")
                }
            }

            item { CompleteToolFooter(progress, toolId = "network") }
        }
    }
}

private fun parsePortRange(input: String): List<Int> {
    val trimmed = input.trim()
    return when {
        trimmed.contains(',') -> trimmed.split(',').mapNotNull { it.trim().toIntOrNull() }
        trimmed.contains('-') -> {
            val (start, end) = trimmed.split('-').map { it.trim().toIntOrNull() ?: 0 }
            val safeEnd = end.coerceAtMost(start + 2000) // evita escaneos descontrolados
            (start..safeEnd).toList()
        }
        else -> trimmed.toIntOrNull()?.let { listOf(it) } ?: NetworkUtils.COMMON_PORTS
    }
}
