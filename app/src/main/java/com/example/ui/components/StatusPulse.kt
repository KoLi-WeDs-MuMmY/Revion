package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Calm rhythmic studio status dot with organic breathing halo.
 * Intentionally restrained to avoid neon or cyberpunk flicker.
 */
@Composable
fun CalmStatusPulseDot(
    color: Color,
    modifier: Modifier = Modifier,
    dotSize: Dp = 12.dp,
    durationMillis: Int = 2200,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "calmPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulsePhase",
    )

    Canvas(modifier = modifier.size(dotSize)) {
        val maxRadius = size.minDimension / 2f
        val haloRadius = maxRadius * (0.62f + 0.36f * pulse)
        val haloAlpha = 0.14f + 0.16f * (1f - pulse)
        val coreRadius = maxRadius * (0.44f + 0.06f * pulse)

        drawCircle(
            color = color.copy(alpha = haloAlpha),
            radius = haloRadius,
        )
        drawCircle(
            color = color,
            radius = coreRadius,
        )
    }
}
