package com.recondex.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.recondex.app.core.AppAnalyzer
import com.recondex.app.core.AppDetail
import com.recondex.app.core.AppSummary
import com.recondex.app.core.ProgressStore
import com.recondex.app.core.SoundFx
import com.recondex.app.core.TOOLS
import com.recondex.app.ui.components.CompleteToolFooter
import com.recondex.app.ui.components.DuoButton
import com.recondex.app.ui.components.ExplainerCard
import com.recondex.app.ui.components.ToolScaffold

@Composable
fun ApkAnalyzerScreen(progress: ProgressStore) {
    val tool = remember { TOOLS.first { it.id == "apk" } }
    val context = LocalContext.current
    var apps by remember { mutableStateOf(listOf<AppSummary>()) }
    var selected by remember { mutableStateOf<AppDetail?>(null) }

    LaunchedEffect(Unit) {
        apps = AppAnalyzer.listApps(context)
    }

    ToolScaffold(title = "Analizador de APKs") {
        val detail = selected
        if (detail == null) {
            LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                item { ExplainerCard(tool.whatItDoes, tool.example, tool.accent) }
                item {
                    Text(
                        "Toca una app para ver permisos, componentes expuestos y firma.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                    )
                }
                items(apps) { app ->
                    Text(
                        "${if (app.isSystemApp) "⚙️" else "📱"} ${app.label}  (${app.packageName})",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                SoundFx.tap()
                                selected = AppAnalyzer.getAppDetail(context, app.packageName)
                            }
                            .padding(vertical = 8.dp)
                    )
                    Divider()
                }
                item { CompleteToolFooter(progress, toolId = "apk") }
            }
        } else {
            AppDetailView(detail, accent = tool.accent, onBack = { selected = null })
        }
    }
}

@Composable
private fun AppDetailView(detail: AppDetail, accent: androidx.compose.ui.graphics.Color, onBack: () -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item {
            DuoButton(text = "← Volver a la lista", color = accent, onClick = onBack)
        }
        item {
            Text(detail.label, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 10.dp))
            Text(detail.packageName, style = MaterialTheme.typography.bodyMedium)
            Text("Versión: ${detail.versionName ?: "?"}  ·  minSdk ${detail.minSdk}  ·  targetSdk ${detail.targetSdk}")
            if (detail.debuggable) {
                Text("⚠️ DEBUGGABLE=true — vulnerable a varios ataques de instrumentación", color = MaterialTheme.colorScheme.error)
            }
            Text("Firma SHA-256: ${detail.signatureSha256 ?: "no disponible"}", style = MaterialTheme.typography.bodyMedium)
        }
        item {
            Divider(modifier = Modifier.padding(vertical = 10.dp))
            Text("Permisos peligrosos (${detail.dangerousPermissions.size})", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
        }
        items(detail.dangerousPermissions) { perm -> Text("⚠️ $perm") }
        item {
            Text("Todos los permisos (${detail.permissions.size})", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 10.dp))
        }
        items(detail.permissions) { perm -> Text(perm, style = MaterialTheme.typography.bodyMedium) }

        item {
            Divider(modifier = Modifier.padding(vertical = 10.dp))
            Text("Activities exportadas (${detail.exportedActivities.size})", style = MaterialTheme.typography.titleMedium)
        }
        items(detail.exportedActivities) { Text(it) }
        item { Text("Services exportados (${detail.exportedServices.size})", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
        items(detail.exportedServices) { Text(it) }
        item { Text("Receivers exportados (${detail.exportedReceivers.size})", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp)) }
        items(detail.exportedReceivers) { Text(it) }
    }
}
