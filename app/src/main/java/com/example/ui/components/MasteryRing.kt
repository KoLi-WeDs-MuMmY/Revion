package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoMetricMedium
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedMasteryRing(
    percentage: Int,
    accentColor: Color,
    modifier: Modifier = Modifier,
    ringSize: Dp = 74.dp,
    strokeWidth: Dp = 6.dp,
    delayMillis: Long = 0L,
) {
    val tokens = GhickinTokens.colors
    val progress = remember { Animatable(0f) }
    val clamped = percentage.coerceIn(0, 100)

    LaunchedEffect(clamped) {
        if (delayMillis > 0L) {
            delay(delayMillis)
        }
        progress.animateTo(
            targetValue = clamped / 100f,
            animationSpec = GhickinMotion.RingSpring,
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(ringSize),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokePx = strokeWidth.toPx()
            val inset = strokePx / 2f + 3.dp.toPx()
            val arcDiameter = size.minDimension - inset * 2f
            val topLeft = Offset(
                x = (size.width - arcDiameter) / 2f,
                y = (size.height - arcDiameter) / 2f,
            )
            val arcSize = Size(arcDiameter, arcDiameter)

            // Subtle precision watch-dial index ticks (4 cardinal markers)
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerTickRadius = size.minDimension / 2f
            val innerTickRadius = outerTickRadius - 2.dp.toPx()
            for (angleDeg in listOf(0.0, 90.0, 180.0, 270.0)) {
                val rad = angleDeg * PI / 180.0
                val start = Offset(
                    x = center.x + (innerTickRadius * cos(rad)).toFloat(),
                    y = center.y + (innerTickRadius * sin(rad)).toFloat(),
                )
                val end = Offset(
                    x = center.x + (outerTickRadius * cos(rad)).toFloat(),
                    y = center.y + (outerTickRadius * sin(rad)).toFloat(),
                )
                drawLine(
                    color = tokens.hairlineRest,
                    start = start,
                    end = end,
                    strokeWidth = 1.dp.toPx(),
                )
            }

            // Matte track ring
            drawArc(
                color = tokens.trackSubtle,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )

            // Crisp active progress arc (solid mineral tone, zero neon blur)
            val sweep = 360f * progress.value
            if (sweep > 0.5f) {
                drawArc(
                    color = accentColor,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                )
            }
        }

        KineticNumberText(
            targetValue = clamped,
            suffix = "%",
            style = MonoMetricMedium,
            color = tokens.inkPrimary,
            delayMillis = delayMillis,
        )
    }
}
