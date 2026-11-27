package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AmoledBlack
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Ultra-minimal studio monochrome sheen over a true #000000 OLED canvas.
 * Removes neon fog completely while preserving whisper-quiet architectural depth.
 */
@Composable
fun AmbientMineralBackdrop(
    @Suppress("UNUSED_PARAMETER") accentMood: AccentMood,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "monochromeAmbientDrift")
    val driftPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "driftPhase",
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(AmoledBlack),
    ) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val rad = driftPhase * PI
        val subtleCenter = Offset(
            x = w * (0.30f + 0.10f * cos(rad).toFloat()),
            y = h * (0.14f + 0.08f * sin(rad).toFloat()),
        )
        val subtleRadius = maxOf(w, h) * 0.52f

        // Whisper-quiet 2.5% neutral studio light catch on true #000000 black
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.025f),
                    Color.White.copy(alpha = 0.008f),
                    Color.Transparent,
                ),
                center = subtleCenter,
                radius = subtleRadius,
            ),
            radius = subtleRadius,
            center = subtleCenter,
        )
    }
}
