package com.recondex.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.recondex.app.core.ProgressStore
import com.recondex.app.core.SoundFx
import com.recondex.app.ui.theme.DuoGreen

/**
 * Botón/footer estándar que todas las herramientas usan para marcar
 * la herramienta como explorada: suena, muestra una confirmación y
 * guarda el estado una sola vez (ProgressStore controla duplicados).
 */
@Composable
fun CompleteToolFooter(progress: ProgressStore, toolId: String, label: String = "Marcar como explorada") {
    var showPopup by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
        DuoButton(
            text = label,
            color = DuoGreen,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                SoundFx.success()
                progress.markExplored(toolId)
                showPopup = true
            }
        )
        DoneConfirmPopup(visible = showPopup)
    }
}
