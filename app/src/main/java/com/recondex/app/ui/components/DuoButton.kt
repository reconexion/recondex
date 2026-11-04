package com.recondex.app.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.recondex.app.ui.theme.DuoGreen

/**
 * Botón grande, redondeado y de color sólido — la identidad visual de
 * RecoNDex (tipo Duolingo): claro, juguetón y fácil de tocar con el pulgar.
 */
@Composable
fun DuoButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = DuoGreen,
    textColor: Color = Color.White,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = textColor,
            disabledContainerColor = color.copy(alpha = 0.35f),
            disabledContentColor = textColor.copy(alpha = 0.7f),
        ),
        modifier = modifier.height(52.dp)
    ) {
        Text(text, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelLarge)
    }
}
