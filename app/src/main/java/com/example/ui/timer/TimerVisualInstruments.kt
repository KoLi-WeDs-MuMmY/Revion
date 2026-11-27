package com.example.ui.timer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PomodoroPhase
import com.example.model.PomodoroRingStyle
import com.example.model.formatDurationHms
import com.example.ui.components.tactileScale
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PomodoroCircleInstrument(
    style: PomodoroRingStyle,
    progress: Float,
    remainingSeconds: Int,
    phase: PomodoroPhase,
    completedCycles: Int,
    accentColor: Color,
    dialSize: Dp = 206.dp,
    compact: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 360),
        label = "pomodoroRingProgress",
    )

    val trackColor = tokens.trackSubtle
    val secondaryInk = tokens.inkMuted
    val surfaceColor = tokens.carbonSurface
    val hairline = tokens.hairlineRest

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(dialSize)
            .testTag("pomodoro_dial_${style.name.lowercase()}"),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minDim = size.minDimension
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = (minDim / 2f) - 10.dp.toPx()

            when (style) {
                PomodoroRingStyle.MINIMAL_HALO -> {
                    val strokeW = if (compact) 7.dp.toPx() else 10.dp.toPx()
                    drawCircle(
                        color = trackColor,
                        radius = outerRadius,
                        center = center,
                        style = Stroke(width = strokeW),
                    )
                    drawArc(
                        color = accentColor,
                        startAngle = -90f,
                        sweepAngle = (animatedProgress * 360f).coerceAtLeast(2f),
                        useCenter = false,
                        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                        size = Size(outerRadius * 2f, outerRadius * 2f),
                        style = Stroke(width = strokeW, cap = StrokeCap.Round),
                    )
                    // Subtle luminous tip marker
                    val tipRad = (-90f + animatedProgress * 360f) * (PI / 180.0)
                    val tipOffset = Offset(
                        x = center.x + (outerRadius * cos(tipRad)).toFloat(),
                        y = center.y + (outerRadius * sin(tipRad)).toFloat(),
                    )
                    drawCircle(
                        color = accentColor.copy(alpha = 0.28f),
                        radius = strokeW * 1.15f,
                        center = tipOffset,
                    )
                }

                PomodoroRingStyle.SEGMENTED_DIAL -> {
                    val tickCount = 60
                    val litTicks = (animatedProgress * tickCount).toInt().coerceIn(0, tickCount)
                    val innerR = outerRadius - (if (compact) 11.dp.toPx() else 15.dp.toPx())
                    for (i in 0 until tickCount) {
                        val angleDeg = -90f + (i * 360f / tickCount)
                        val rad = angleDeg * (PI / 180.0)
                        val isMajor = i % 5 == 0
                        val startR = if (isMajor) innerR - 4.dp.toPx() else innerR
                        val start = Offset(
                            center.x + (startR * cos(rad)).toFloat(),
                            center.y + (startR * sin(rad)).toFloat(),
                        )
                        val end = Offset(
                            center.x + (outerRadius * cos(rad)).toFloat(),
                            center.y + (outerRadius * sin(rad)).toFloat(),
                        )
                        drawLine(
                            color = if (i <= litTicks) accentColor else trackColor,
                            start = start,
                            end = end,
                            strokeWidth = if (isMajor) 2.8.dp.toPx() else 1.5.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                }

                PomodoroRingStyle.DUAL_ORBITAL -> {
                    val outerW = if (compact) 6.dp.toPx() else 8.dp.toPx()
                    val innerR = outerRadius - (if (compact) 14.dp.toPx() else 19.dp.toPx())
                    val innerW = if (compact) 3.5.dp.toPx() else 4.5.dp.toPx()
                    // Outer focus orbit
                    drawCircle(
                        color = trackColor,
                        radius = outerRadius,
                        center = center,
                        style = Stroke(width = outerW),
                    )
                    drawArc(
                        color = accentColor,
                        startAngle = -90f,
                        sweepAngle = (animatedProgress * 360f).coerceAtLeast(3f),
                        useCenter = false,
                        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                        size = Size(outerRadius * 2f, outerRadius * 2f),
                        style = Stroke(width = outerW, cap = StrokeCap.Round),
                    )
                    // Inner cycle progress orbit (4-cycle goal)
                    val cycleFrac = ((completedCycles % 4).coerceAtLeast(1) / 4f)
                    drawCircle(
                        color = hairline,
                        radius = innerR,
                        center = center,
                        style = Stroke(width = innerW),
                    )
                    drawArc(
                        color = secondaryInk.copy(alpha = 0.72f),
                        startAngle = -90f,
                        sweepAngle = cycleFrac * 360f,
                        useCenter = false,
                        topLeft = Offset(center.x - innerR, center.y - innerR),
                        size = Size(innerR * 2f, innerR * 2f),
                        style = Stroke(width = innerW, cap = StrokeCap.Round),
                    )
                }

                PomodoroRingStyle.ARCHITECTURAL_RING -> {
                    val strokeW = if (compact) 5.dp.toPx() else 7.dp.toPx()
                    // Soft filled inner plate
                    drawCircle(
                        color = surfaceColor,
                        radius = outerRadius - 6.dp.toPx(),
                        center = center,
                    )
                    drawCircle(
                        color = hairline,
                        radius = outerRadius,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx()),
                    )
                    drawArc(
                        color = accentColor,
                        startAngle = -90f,
                        sweepAngle = (animatedProgress * 360f).coerceAtLeast(2f),
                        useCenter = false,
                        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                        size = Size(outerRadius * 2f, outerRadius * 2f),
                        style = Stroke(width = strokeW, cap = StrokeCap.Butt),
                    )
                    // 4 bold cardinal architectural notches (12, 3, 6, 9)
                    listOf(0, 90, 180, 270).forEach { deg ->
                        val rad = (deg - 90) * (PI / 180.0)
                        val p1 = Offset(
                            center.x + ((outerRadius - 12.dp.toPx()) * cos(rad)).toFloat(),
                            center.y + ((outerRadius - 12.dp.toPx()) * sin(rad)).toFloat(),
                        )
                        val p2 = Offset(
                            center.x + ((outerRadius - 3.dp.toPx()) * cos(rad)).toFloat(),
                            center.y + ((outerRadius - 3.dp.toPx()) * sin(rad)).toFloat(),
                        )
                        drawLine(
                            color = accentColor.copy(alpha = 0.85f),
                            start = p1,
                            end = p2,
                            strokeWidth = 2.4.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                }
            }
        }

        // Center crisp tabular readout
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (compact) 2.dp else 4.dp),
        ) {
            Text(
                text = phase.label,
                style = MicroTagMono,
                color = accentColor,
            )
            Text(
                text = formatDurationHms(remainingSeconds),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 26.sp else 36.sp,
                    fontFeatureSettings = "tnum",
                ),
                color = tokens.inkPrimary,
                maxLines = 1,
            )
            if (!compact) {
                Text(
                    text = "CYCLE #$completedCycles",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
            }
        }
    }
}

/**
 * Play Store Calendar / Flip-Clock aesthetic stopwatch with tactile split-flap tiles
 * and tabular monospace digits (`tnum`) that never jitter across ticks.
 */
@Composable
fun CalendarFlipStopwatchView(
    elapsedSeconds: Int,
    accentColor: Color,
    compact: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val safe = elapsedSeconds.coerceAtLeast(0)
    val hours = (safe / 3600).toString().padStart(2, '0')
    val minutes = ((safe % 3600) / 60).toString().padStart(2, '0')
    val seconds = (safe % 60).toString().padStart(2, '0')

    Column(
        modifier = modifier.testTag("calendar_flip_stopwatch"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp),
    ) {
        // Calendar top strip
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                )
                Text(
                    text = "CALENDAR FLIP STOPWATCH · TODAY",
                    style = MicroTagMono,
                    color = tokens.inkSecondary,
                )
            }
        }

        // Flip-card HH : MM : SS row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp),
        ) {
            CalendarFlipDigitCard(
                digits = hours,
                unitLabel = "HRS",
                accentColor = accentColor,
                compact = compact,
            )
            Text(
                text = ":",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                ),
                color = tokens.inkMuted,
                modifier = Modifier.padding(bottom = if (compact) 14.dp else 18.dp),
            )
            CalendarFlipDigitCard(
                digits = minutes,
                unitLabel = "MIN",
                accentColor = accentColor,
                compact = compact,
            )
            Text(
                text = ":",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                ),
                color = tokens.inkMuted,
                modifier = Modifier.padding(bottom = if (compact) 14.dp else 18.dp),
            )
            CalendarFlipDigitCard(
                digits = seconds,
                unitLabel = "SEC",
                accentColor = accentColor,
                compact = compact,
                highlightBorder = true,
            )
        }
    }
}

@Composable
private fun CalendarFlipDigitCard(
    digits: String,
    unitLabel: String,
    accentColor: Color,
    compact: Boolean,
    highlightBorder: Boolean = false,
) {
    val tokens = GhickinTokens.colors
    val cardWidth = if (compact) 64.dp else 88.dp
    val cardHeight = if (compact) 74.dp else 102.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(if (compact) 14.dp else 18.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(
                1.dp,
                if (highlightBorder) accentColor.copy(alpha = 0.55f) else tokens.hairlineRest,
            ),
            modifier = Modifier.size(width = cardWidth, height = cardHeight),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top calendar binder bar accent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compact) 5.dp else 7.dp)
                        .align(Alignment.TopCenter)
                        .background(if (highlightBorder) accentColor.copy(alpha = 0.65f) else tokens.slateElevated),
                )
                // Digits
                Text(
                    text = digits,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (compact) 28.sp else 42.sp,
                        fontFeatureSettings = "tnum",
                    ),
                    color = if (highlightBorder) accentColor else tokens.inkPrimary,
                    modifier = Modifier.align(Alignment.Center),
                )
                // Split-flap horizontal hinge crease across center
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.5.dp)
                        .align(Alignment.Center)
                        .background(tokens.amoledBackground.copy(alpha = 0.75f)),
                )
            }
        }
        Text(
            text = unitLabel,
            style = MicroTagMono,
            color = tokens.inkMuted,
        )
    }
}

@Composable
fun PomodoroStyleSelectorStrip(
    selectedStyle: PomodoroRingStyle,
    accentColor: Color,
    onSelectStyle: (PomodoroRingStyle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        PomodoroRingStyle.entries.forEach { style ->
            val selected = style == selectedStyle
            val interaction = remember { MutableInteractionSource() }
            val shape = RoundedCornerShape(10.dp)
            Surface(
                shape = shape,
                color = if (selected) accentColor.copy(alpha = 0.16f) else tokens.carbonSurface,
                border = BorderStroke(1.dp, if (selected) accentColor else tokens.hairlineSubtle),
                modifier = Modifier
                    .weight(1f)
                    .testTag("pomodoro_style_chip_${style.name.lowercase()}")
                    .tactileScale(interaction, 0.95f)
                    .clip(shape)
                    .clickable(
                        interactionSource = interaction,
                        indication = ripple(color = accentColor.copy(alpha = 0.2f)),
                        role = Role.RadioButton,
                        onClick = { onSelectStyle(style) },
                    ),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 7.dp),
                ) {
                    Text(
                        text = style.label,
                        style = MicroTagMono,
                        color = if (selected) accentColor else tokens.inkSecondary,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
