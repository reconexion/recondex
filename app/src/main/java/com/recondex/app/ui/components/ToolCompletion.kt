package com.recondex.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.recondex.app.core.ProgressStore
import com.recondex.app.core.SoundFx

/**
 * Botón/footer estándar que todas las herramientas usan para marcar
 * la "lección" como completada: suena, anima el popup de +XP y
 * acredita el XP una sola vez por herramienta (ProgressStore controla duplicados).
 */
@Composable
fun CompleteToolFooter(progress: ProgressStore, toolId: String, xp: Int, label: String = "Marcar herramienta como explorada") {
    var showPopup by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = {
            SoundFx.success()
            progress.awardXp(toolId, xp)
            showPopup = true
        }) {
            Text(label)
        }
        XpGainPopup(visible = showPopup, xp = xp)
    }
}
