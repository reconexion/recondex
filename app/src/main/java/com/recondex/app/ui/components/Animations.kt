package com.recondex.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Icono que "respira" (pulso infinito sutil) — usado en estados activos/escaneando. */
@Composable
fun PulsingIcon(icon: ImageVector, tint: Color, contentDescription: String? = null) {
    val infinite = rememberInfiniteTransition(label = "pulse")
    val scale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.scale(scale))
}

/** Contador que anima el valor hacia arriba (usado para conteos simples, ej. herramientas exploradas). */
@Composable
fun AnimatedCounter(targetValue: Int, style: TextStyle, color: Color) {
    val animated by animateIntAsState(targetValue = targetValue, animationSpec = tween(500), label = "counter")
    Text("$animated", style = style, color = color, fontWeight = FontWeight.ExtraBold)
}

/** Modifier con "bounce" al presionar: da sensación táctil/fluida en tarjetas y botones. */
@Composable
fun Modifier.pressBounce(interactionSource: MutableInteractionSource): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = tween(120),
        label = "pressBounce"
    )
    return this.scale(scale)
}

/** Popup flotante de confirmación (scale+fade) que aparece y desaparece solo. */
@Composable
fun DoneConfirmPopup(visible: Boolean, text: String = "¡Listo!") {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.secondary
        ) {
            Text(
                text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
