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
import com.example.ui.theme.AppThemeId
import com.example.ui.theme.GhickinTokens
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Theme-aware ambient studio sheen over the active modular theme canvas.
 * Preserves pure #000000 OLED black in Grok Black while giving Claude Editorial,
 * Paper Light, and Slate their own whisper-quiet architectural warmth.
 */
@Composable
fun AmbientMineralBackdrop(
    @Suppress("UNUSED_PARAMETER") accentMood: AccentMood,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val transition = rememberInfiniteTransition(label = "themeAmbientDrift")
    val driftPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "driftPhase",
    )

    val sheenColor = when (tokens.activeTheme) {
        AppThemeId.GROK_BLACK -> Color.White
        AppThemeId.CLAUDE_EDITORIAL -> tokens.themeAccent
        AppThemeId.PAPER_LIGHT -> tokens.themeAccent
        AppThemeId.SLATE -> tokens.themeAccent
    }
    val peakAlpha = when (tokens.activeTheme) {
        AppThemeId.GROK_BLACK -> 0.025f
        AppThemeId.CLAUDE_EDITORIAL -> 0.045f
        AppThemeId.PAPER_LIGHT -> 0.055f
        AppThemeId.SLATE -> 0.04f
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.amoledBackground),
    ) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val rad = driftPhase * PI
        val subtleCenter = Offset(
            x = w * (0.30f + 0.10f * cos(rad).toFloat()),
            y = h * (0.14f + 0.08f * sin(rad).toFloat()),
        )
        val subtleRadius = maxOf(w, h) * 0.54f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    sheenColor.copy(alpha = peakAlpha),
                    sheenColor.copy(alpha = peakAlpha * 0.35f),
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
