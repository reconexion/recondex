package com.recondex.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.recondex.app.core.ProgressStore
import com.recondex.app.core.TOOLS

@Composable
fun ProfileScreen(progress: ProgressStore) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Tu perfil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text(
            "Herramientas exploradas: ${progress.completedTools.size}/${TOOLS.size}",
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
