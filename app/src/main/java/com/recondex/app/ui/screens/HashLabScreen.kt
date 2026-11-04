package com.recondex.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import com.recondex.app.core.HashLab
import com.recondex.app.core.ProgressStore
import com.recondex.app.core.SoundFx
import com.recondex.app.core.TOOLS
import com.recondex.app.ui.components.CompleteToolFooter
import com.recondex.app.ui.components.DuoButton
import com.recondex.app.ui.components.ExplainerCard
import com.recondex.app.ui.components.ToolScaffold
import kotlinx.coroutines.launch

@Composable
fun HashLabScreen(progress: ProgressStore) {
    val tool = remember { TOOLS.first { it.id == "hashlab" } }
    val scope = rememberCoroutineScope()

    var hashInput by remember { mutableStateOf("") }
    var hashType by remember { mutableStateOf<String?>(null) }
    var cracking by remember { mutableStateOf(false) }
    var crackResult by remember { mutableStateOf<String?>(null) }

    var authUrl by remember { mutableStateOf("") }
    var bruteRunning by remember { mutableStateOf(false) }
    val attempts = remember { mutableStateOf(listOf<HashLab.AuthAttemptResult>()) }

    ToolScaffold(title = "Laboratorio de Hashes") {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { ExplainerCard(tool.whatItDoes, tool.example, tool.accent) }
            item {
                Text(
                    "1. Identificar y poner a prueba un hash",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
                OutlinedTextField(
                    value = hashInput,
                    onValueChange = {
                        hashInput = it
                        hashType = if (it.isNotBlank()) HashLab.identifyHashType(it) else null
                        crackResult = null
                    },
                    label = { Text("Hash (MD5/SHA1/SHA256/SHA512)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
                hashType?.let { Text("Tipo detectado: $it", style = MaterialTheme.typography.bodyMedium) }
                DuoButton(
                    text = if (cracking) "Probando diccionario..." else "Probar hash",
                    color = tool.accent,
                    enabled = !cracking && hashInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    onClick = {
                        cracking = true
                        crackResult = null
                        scope.launch {
                            val result = HashLab.crackHash(hashInput)
                            crackResult = if (result != null) {
                                SoundFx.success()
                                "✅ Encontrado: \"${result.first}\" (algoritmo ${result.second})"
                            } else {
                                SoundFx.error()
                                "❌ No está en el diccionario educativo de ${HashLab.COMMON_PASSWORDS.size} contraseñas comunes."
                            }
                            cracking = false
                        }
                    }
                )
            }
            if (cracking) item { CircularProgressIndicator(modifier = Modifier.padding(8.dp)) }
            crackResult?.let { item { Text(it, modifier = Modifier.padding(top = 4.dp)) } }

            item { Divider(modifier = Modifier.padding(vertical = 14.dp)) }

            item {
                Text("2. Probar contraseñas en un login Basic Auth", style = MaterialTheme.typography.titleMedium)
                Text("Solo contra servidores propios de prueba. Usa un diccionario educativo pequeño.", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = authUrl,
                    onValueChange = { authUrl = it },
                    label = { Text("URL con Basic Auth (ej. https://miapp.test/admin)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
                DuoButton(
                    text = if (bruteRunning) "Probando combinaciones..." else "Probar contraseñas",
                    color = tool.accent,
                    enabled = !bruteRunning && authUrl.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    onClick = {
                        bruteRunning = true
                        attempts.value = emptyList()
                        scope.launch {
                            HashLab.bruteForceBasicAuth(
                                authUrl,
                                usernames = listOf("admin", "root", "user"),
                                passwords = HashLab.COMMON_PASSWORDS.take(25)
                            ) { attempt ->
                                if (attempt.success) {
                                    attempts.value = attempts.value + attempt
                                    SoundFx.success()
                                }
                            }
                            bruteRunning = false
                        }
                    }
                )
            }
            if (bruteRunning) item { CircularProgressIndicator(modifier = Modifier.padding(8.dp)) }
            items(attempts.value) { a -> Text("🔓 ${a.username}:${a.password} funcionó") }
            if (!bruteRunning && attempts.value.isEmpty() && authUrl.isNotBlank()) {
                item { Text("Sin resultados (todavía) con este diccionario corto.") }
            }

            item { CompleteToolFooter(progress, toolId = "hashlab") }
        }
    }
}
