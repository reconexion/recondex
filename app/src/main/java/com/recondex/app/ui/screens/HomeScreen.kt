package com.recondex.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.recondex.app.core.ProgressStore
import com.recondex.app.core.SoundFx
import com.recondex.app.core.ToolInfo
import com.recondex.app.ui.components.AnimatedCounter
import com.recondex.app.ui.components.PulsingIcon
import com.recondex.app.ui.components.pressBounce

@Composable
fun HomeScreen(progress: ProgressStore, tools: List<ToolInfo>, onToolClick: (ToolInfo) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { StreakXpHeader(progress) }
        item {
            Text(
                "Tu camino de habilidades",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        items(tools) { tool ->
            ToolCard(
                tool = tool,
                completed = progress.completedTools.contains(tool.id),
                onClick = {
                    SoundFx.tap()
                    onToolClick(tool)
                }
            )
        }
    }
}

@Composable
private fun StreakXpHeader(progress: ProgressStore) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PulsingIcon(
                    icon = Icons.Filled.LocalFireDepartment,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "racha"
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    AnimatedCounter(progress.streak, MaterialTheme.typography.titleLarge, MaterialTheme.colorScheme.onPrimary)
                    Text("racha", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = "XP", tint = MaterialTheme.colorScheme.tertiary)
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    AnimatedCounter(progress.xp, MaterialTheme.typography.titleLarge, MaterialTheme.colorScheme.onPrimary)
                    Text("XP", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ToolCard(tool: ToolInfo, completed: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pressBounce(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (completed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        if (completed) Icons.Filled.CheckCircle else tool.icon,
                        contentDescription = tool.title,
                        tint = if (completed) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(tool.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(tool.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)) {
                Text(
                    "+${tool.xp} XP",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}
