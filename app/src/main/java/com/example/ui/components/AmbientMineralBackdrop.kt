package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
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
 * Whisper-soft drifting mineral light washes behind the pure #000000 AMOLED void.
 * Ultra-low opacity (4%–6%) preserves OLED battery efficiency and pitch-black contrast
 * with zero neon glow or cyberpunk visual clutter.
 */
@Composable
fun AmbientMineralBackdrop(
    accentMood: AccentMood,
    modifier: Modifier = Modifier,
) {
    val primaryColor by animateColorAsState(
        targetValue = accentMood.accent,
        animationSpec = spring(stiffness = 180f),
        label = "ambientPrimary",
    )
    val secondaryColor by animateColorAsState(
        targetValue = accentMood.next().accent,
        animationSpec = spring(stiffness = 180f),
        label = "ambientSecondary",
    )

    val transition = rememberInfiniteTransition(label = "mineralWashDrift")
    val driftPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = FastOutSlowInEasing),
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
        val primaryCenter = Offset(
            x = w * (0.22f + 0.14f * cos(rad).toFloat()),
            y = h * (0.18f + 0.10f * sin(rad).toFloat()),
        )
        val secondaryCenter = Offset(
            x = w * (0.82f - 0.12f * sin(rad).toFloat()),
            y = h * (0.78f - 0.10f * cos(rad).toFloat()),
        )

        val primaryRadius = maxOf(w, h) * 0.56f
        val secondaryRadius = maxOf(w, h) * 0.48f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.055f),
                    primaryColor.copy(alpha = 0.018f),
                    Color.Transparent,
                ),
                center = primaryCenter,
                radius = primaryRadius,
            ),
            radius = primaryRadius,
            center = primaryCenter,
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    secondaryColor.copy(alpha = 0.042f),
                    secondaryColor.copy(alpha = 0.012f),
                    Color.Transparent,
                ),
                center = secondaryCenter,
                radius = secondaryRadius,
            ),
            radius = secondaryRadius,
            center = secondaryCenter,
        )
    }
}
