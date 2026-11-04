package com.recondex.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import com.recondex.app.core.ProgressStore
import com.recondex.app.core.SoundFx
import com.recondex.app.core.WebRecon
import com.recondex.app.ui.components.CompleteToolFooter
import com.recondex.app.ui.components.ToolScaffold
import kotlinx.coroutines.launch

@Composable
fun WebReconScreen(progress: ProgressStore) {
    val scope = rememberCoroutineScope()
    var url by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val headerResults = remember { mutableStateOf(listOf<WebRecon.HeaderCheck>()) }
    val pathResults = remember { mutableStateOf(listOf<WebRecon.PathCheck>()) }

    var sqliUrl by remember { mutableStateOf("") }
    var sqliResult by remember { mutableStateOf<String?>(null) }
    var sqliLoading by remember { mutableStateOf(false) }

    ToolScaffold(title = "Web Recon") {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Text("1. Headers y rutas comunes", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL objetivo (ej. miapp.test)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
                Button(
                    enabled = !loading && url.isNotBlank(),
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        headerResults.value = emptyList()
                        pathResults.value = emptyList()
                        loading = true
                        scope.launch {
                            headerResults.value = WebRecon.checkSecurityHeaders(url)
                            WebRecon.checkCommonPaths(url) { result ->
                                pathResults.value = (pathResults.value + result).sortedBy { it.path }
                            }
                            loading = false
                            SoundFx.success()
                        }
                    }
                ) { Text(if (loading) "Analizando..." else "Analizar sitio") }
            }
            if (loading) item { CircularProgressIndicator(modifier = Modifier.padding(8.dp)) }
            items(headerResults.value) { h ->
                Text(if (h.present) "✅ ${h.header}: ${h.value}" else "⚠️ ${h.header}: ausente")
            }
            items(pathResults.value) { p ->
                Text(if (p.exposed) "🚨 /${p.path} → HTTP ${p.statusCode} (¡expuesto!)" else "🔒 /${p.path} → HTTP ${p.statusCode}")
            }

            item { Divider(modifier = Modifier.padding(vertical = 14.dp)) }

            item {
                Text("2. Prueba básica de SQLi (heurística)", style = MaterialTheme.typography.titleMedium)
                Text("Usa solo en endpoints propios con parámetros, ej: https://miapp.test/item?id=1", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = sqliUrl,
                    onValueChange = { sqliUrl = it },
                    label = { Text("URL con parámetro") },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
                Button(
                    enabled = !sqliLoading && sqliUrl.isNotBlank(),
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        sqliLoading = true
                        sqliResult = null
                        scope.launch {
                            sqliResult = WebRecon.probeBasicSqli(sqliUrl)
                            sqliLoading = false
                            SoundFx.success()
                        }
                    }
                ) { Text(if (sqliLoading) "Probando..." else "Probar SQLi básico") }
            }
            if (sqliLoading) item { CircularProgressIndicator(modifier = Modifier.padding(8.dp)) }
            sqliResult?.let { r -> item { Text(r, modifier = Modifier.padding(top = 6.dp)) } }

            item { CompleteToolFooter(progress, toolId = "webrecon", xp = 25) }
        }
    }
}
