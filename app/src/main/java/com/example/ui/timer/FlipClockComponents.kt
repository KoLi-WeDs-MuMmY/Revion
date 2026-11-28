package com.example.ui.timer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ClockThemeId
import com.example.model.LiveClockTick
import com.example.model.MotionIntensity
import com.example.model.StudyTimerMode
import com.example.model.TimerSessionSource
import com.example.model.formatDurationHms
import kotlinx.coroutines.flow.StateFlow

enum class FlipTileSize(
    val tileWidth: Dp,
    val tileHeight: Dp,
    val fontSize: TextUnit,
    val cornerRadius: Dp,
    val colonSize: Dp,
    val subLabelSize: TextUnit,
) {
    COMPACT_HOME(
        tileWidth = 62.dp,
        tileHeight = 68.dp,
        fontSize = 34.sp,
        cornerRadius = 12.dp,
        colonSize = 5.dp,
        subLabelSize = 9.sp,
    ),
    STANDARD_SPLIT(
        tileWidth = 74.dp,
        tileHeight = 84.dp,
        fontSize = 42.sp,
        cornerRadius = 14.dp,
        colonSize = 6.dp,
        subLabelSize = 10.sp,
    ),
    HERO_FULLSCREEN(
        tileWidth = 104.dp,
        tileHeight = 118.dp,
        fontSize = 62.sp,
        cornerRadius = 18.dp,
        colonSize = 7.dp,
        subLabelSize = 11.sp,
    ),
    NARROW_SPLIT(
        tileWidth = 54.dp,
        tileHeight = 60.dp,
        fontSize = 28.sp,
        cornerRadius = 10.dp,
        colonSize = 4.dp,
        subLabelSize = 8.sp,
    ),
}

/**
 * Isolated leaf composable collecting the 1-second StateFlow<LiveClockTick>.
 * Parent screens, NavRail, and sibling cards NEVER recompose on timer ticks.
 */
@Composable
fun FlipClockReadoutLeaf(
    clockTickFlow: StateFlow<LiveClockTick>,
    clockTheme: ClockThemeId,
    motionIntensity: MotionIntensity,
    tileSize: FlipTileSize,
    forceStopwatchFormat: Boolean = false,
    showSubLabels: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val tick by clockTickFlow.collectAsStateWithLifecycle()
    val palette = clockTheme.palette

    val totalSec = if (forceStopwatchFormat) {
        tick.elapsedSeconds.coerceAtLeast(0)
    } else {
        tick.displaySeconds
    }
    val showHours = forceStopwatchFormat ||
        tick.mode == StudyTimerMode.STOPWATCH ||
        totalSec >= 3600

    val hrs = (totalSec / 3600).coerceAtMost(99)
    val mins = (totalSec % 3600) / 60
    val secs = totalSec % 60

    val hh = hrs.toString().padStart(2, '0')
    val mm = mins.toString().padStart(2, '0')
    val ss = secs.toString().padStart(2, '0')

    val modeTag = when {
        forceStopwatchFormat || tick.mode == StudyTimerMode.STOPWATCH -> "STOPWATCH"
        else -> tick.pomodoroPhase.label
    }

    Surface(
        shape = RoundedCornerShape(tileSize.cornerRadius + 6.dp),
        color = palette.housingBackground,
        border = BorderStroke(1.dp, palette.housingBorder),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = if (tileSize == FlipTileSize.HERO_FULLSCREEN) 20.dp else 12.dp,
                vertical = if (tileSize == FlipTileSize.HERO_FULLSCREEN) 18.dp else 10.dp,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    if (tileSize == FlipTileSize.NARROW_SPLIT) 6.dp else 8.dp,
                ),
            ) {
                if (showHours) {
                    FlipDigitPairTile(
                        value = hh,
                        clockTheme = clockTheme,
                        size = tileSize,
                        motionIntensity = motionIntensity,
                        unitLabel = if (showSubLabels) "HRS" else null,
                        cornerTag = if (showSubLabels && tileSize != FlipTileSize.NARROW_SPLIT) modeTag.take(5) else null,
                    )
                    FlipColonDivider(
                        size = tileSize.colonSize,
                        color = if (tick.isRunning) palette.accentColor else palette.subLabelInk.copy(alpha = 0.55f),
                    )
                }

                FlipDigitPairTile(
                    value = mm,
                    clockTheme = clockTheme,
                    size = tileSize,
                    motionIntensity = motionIntensity,
                    unitLabel = if (showSubLabels) "MIN" else null,
                    cornerTag = if (!showHours && showSubLabels && tileSize != FlipTileSize.NARROW_SPLIT) modeTag else null,
                )

                FlipColonDivider(
                    size = tileSize.colonSize,
                    color = if (tick.isRunning) palette.accentColor else palette.subLabelInk.copy(alpha = 0.55f),
                )

                FlipDigitPairTile(
                    value = ss,
                    clockTheme = clockTheme,
                    size = tileSize,
                    motionIntensity = motionIntensity,
                    unitLabel = if (showSubLabels) "SEC" else null,
                    cornerTag = if (showSubLabels && tileSize == FlipTileSize.HERO_FULLSCREEN) {
                        if (tick.isRunning) "LIVE" else if (tick.isPaused) "PAUSED" else "READY"
                    } else null,
                )
            }
        }
    }
}

/**
 * Static / preview split-flap clock tile pair (used in Settings Clock Theme previews
 * and Home clock thumbnails without subscribing to 1s ticks).
 */
@Composable
fun FlipClockPreviewRow(
    minutesText: String,
    secondsText: String,
    clockTheme: ClockThemeId,
    tileSize: FlipTileSize = FlipTileSize.NARROW_SPLIT,
    modifier: Modifier = Modifier,
) {
    val palette = clockTheme.palette
    Surface(
        shape = RoundedCornerShape(tileSize.cornerRadius + 4.dp),
        color = palette.housingBackground,
        border = BorderStroke(1.dp, palette.housingBorder),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            FlipDigitPairTile(
                value = minutesText,
                clockTheme = clockTheme,
                size = tileSize,
                motionIntensity = MotionIntensity.REDUCED,
                unitLabel = null,
                cornerTag = null,
            )
            FlipColonDivider(
                size = tileSize.colonSize,
                color = palette.accentColor,
            )
            FlipDigitPairTile(
                value = secondsText,
                clockTheme = clockTheme,
                size = tileSize,
                motionIntensity = MotionIntensity.REDUCED,
                unitLabel = null,
                cornerTag = null,
            )
        }
    }
}

/**
 * True physical 3D half-card mechanical split-flap tile.
 * Only animates when [value] changes, using lightweight graphicsLayer rotationX + cameraDistance.
 */
@Composable
fun FlipDigitPairTile(
    value: String,
    clockTheme: ClockThemeId,
    size: FlipTileSize,
    motionIntensity: MotionIntensity,
    unitLabel: String? = null,
    cornerTag: String? = null,
    modifier: Modifier = Modifier,
) {
    val palette = clockTheme.palette
    val density = LocalDensity.current.density

    var previousValue by remember { mutableStateOf(value) }
    val flipAngle = remember { Animatable(180f) }

    LaunchedEffect(value) {
        if (value != previousValue) {
            if (motionIntensity == MotionIntensity.REDUCED) {
                previousValue = value
                flipAngle.snapTo(180f)
            } else {
                val durationMs = if (motionIntensity == MotionIntensity.CRISP) 240 else 320
                flipAngle.snapTo(0f)
                flipAngle.animateTo(
                    targetValue = 180f,
                    animationSpec = tween(durationMillis = durationMs, easing = FastOutSlowInEasing),
                )
                previousValue = value
            }
        }
    }

    val angle = flipAngle.value
    val isFlipping = value != previousValue && angle < 179.5f
    val digitStyle = remember(size.fontSize, palette.digitInk) {
        TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = size.fontSize,
            lineHeight = size.fontSize,
            color = palette.digitInk,
            textAlign = TextAlign.Center,
            fontFeatureSettings = "tnum",
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .width(size.tileWidth)
                .height(size.tileHeight)
                .clip(RoundedCornerShape(size.cornerRadius))
                .border(1.dp, palette.tileBorder, RoundedCornerShape(size.cornerRadius)),
        ) {
            // 1. Static TOP half — shows NEW value behind the folding top flap
            FlipHalfFace(
                text = value,
                isTopHalf = true,
                surfaceColor = palette.topHalfSurface,
                digitStyle = digitStyle,
                tileHeight = size.tileHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.TopCenter),
            )

            // 2. Static BOTTOM half — shows PREVIOUS value until new bottom flap unfolds over it
            FlipHalfFace(
                text = if (isFlipping) previousValue else value,
                isTopHalf = false,
                surfaceColor = palette.bottomHalfSurface,
                digitStyle = digitStyle,
                tileHeight = size.tileHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter),
            )

            // 3. Animated 3D TOP flap folding downward (0° -> 90°)
            if (isFlipping && angle <= 90f) {
                val shadeAlpha = (angle / 90f) * 0.34f
                FlipHalfFace(
                    text = previousValue,
                    isTopHalf = true,
                    surfaceColor = palette.topHalfSurface,
                    digitStyle = digitStyle,
                    tileHeight = size.tileHeight,
                    overlayShadowAlpha = shadeAlpha,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .align(Alignment.TopCenter)
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0.5f, 1.0f)
                            rotationX = -angle
                            cameraDistance = 14f * density
                        },
                )
            }

            // 4. Animated 3D BOTTOM flap unfolding into place (90° -> 180°)
            if (isFlipping && angle > 90f) {
                val remainingFraction = ((180f - angle) / 90f).coerceIn(0f, 1f)
                FlipHalfFace(
                    text = value,
                    isTopHalf = false,
                    surfaceColor = palette.bottomHalfSurface,
                    digitStyle = digitStyle,
                    tileHeight = size.tileHeight,
                    overlayShadowAlpha = remainingFraction * 0.28f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .align(Alignment.BottomCenter)
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0.5f, 0.0f)
                            rotationX = 180f - angle
                            cameraDistance = 14f * density
                        },
                )
            }

            // 5. Center mechanical split crease + subtle highlight seam
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.Center)
                    .background(palette.creaseLine),
            )

            // 6. Left & right metallic split-flap hinge pins
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(9.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                    .background(palette.hingeNotch),
            )
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(9.dp)
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(topStart = 2.dp, bottomStart = 2.dp))
                    .background(palette.hingeNotch),
            )

            // Optional crisp corner badge (e.g., FOCUS / LIVE)
            if (cornerTag != null) {
                Text(
                    text = cornerTag,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.subLabelSize.value - 1f).coerceAtLeast(7f).sp,
                        letterSpacing = 0.6.sp,
                    ),
                    color = palette.accentColor,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 6.dp, top = 4.dp),
                )
            }
        }

        if (unitLabel != null) {
            Text(
                text = unitLabel,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = size.subLabelSize,
                    letterSpacing = 1.1.sp,
                ),
                color = palette.subLabelInk,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

@Composable
private fun FlipHalfFace(
    text: String,
    isTopHalf: Boolean,
    surfaceColor: Color,
    digitStyle: TextStyle,
    tileHeight: Dp,
    overlayShadowAlpha: Float = 0f,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clipToBounds()
            .background(
                Brush.verticalGradient(
                    colors = if (isTopHalf) {
                        listOf(surfaceColor, surfaceColor.copy(alpha = 0.94f))
                    } else {
                        listOf(surfaceColor.copy(alpha = 0.96f), surfaceColor)
                    },
                ),
            ),
    ) {
        // Place full-height centered digit box shifted cleanly so each half shows its exact 50% slice
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(tileHeight)
                .align(if (isTopHalf) Alignment.TopCenter else Alignment.BottomCenter),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = digitStyle,
                maxLines = 1,
                softWrap = false,
            )
        }

        if (overlayShadowAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayShadowAlpha)),
            )
        }
    }
}

@Composable
private fun FlipColonDivider(
    size: Dp,
    color: Color,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(size * 1.8f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(color),
        )
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(color),
        )
    }
}

/**
 * Isolated leaf text widget for live ticking readouts (YPT members, Vault banner,
 * Home today total, Revision/Flashcards auto-timer pill).
 */
@Composable
fun LiveClockTickTextLeaf(
    clockTickFlow: StateFlow<LiveClockTick>,
    textProvider: (LiveClockTick) -> String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val tick by clockTickFlow.collectAsStateWithLifecycle()
    Text(
        text = textProvider(tick),
        style = style.copy(fontFeatureSettings = "tnum"),
        color = color,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

/**
 * Helper to format a YPT peer's live focus time inside an isolated leaf composable.
 */
@Composable
fun YptLiveMemberClockLeaf(
    clockTickFlow: StateFlow<LiveClockTick>,
    baseTodaySeconds: Int,
    isActivelyStudying: Boolean,
    isCurrentUser: Boolean,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    LiveClockTickTextLeaf(
        clockTickFlow = clockTickFlow,
        textProvider = { tick ->
            val liveSec = when {
                isCurrentUser && tick.isActiveSession -> baseTodaySeconds + tick.elapsedSeconds
                !isCurrentUser && isActivelyStudying -> baseTodaySeconds + tick.peerElapsedOffsetSeconds
                else -> baseTodaySeconds
            }
            val safe = liveSec.coerceAtLeast(0)
            val h = safe / 3600
            val m = (safe % 3600) / 60
            val s = safe % 60
            "%02d:%02d:%02d".format(h, m, s)
        },
        style = style,
        color = color,
        modifier = modifier,
    )
}

/**
 * Helper leaf readout for Auto Revision / Auto Flashcards header pills.
 */
@Composable
fun AutoTimerPillReadoutLeaf(
    clockTickFlow: StateFlow<LiveClockTick>,
    expectedSource: TimerSessionSource,
    prefix: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    LiveClockTickTextLeaf(
        clockTickFlow = clockTickFlow,
        textProvider = { tick ->
            val sec = if (tick.timerSource == expectedSource && tick.isActiveSession) {
                tick.elapsedSeconds
            } else {
                0
            }
            "$prefix · ${formatDurationHms(sec)}"
        },
        style = style,
        color = color,
        modifier = modifier,
    )
}
