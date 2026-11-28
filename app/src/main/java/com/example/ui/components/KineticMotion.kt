package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

object GhickinMotion {
    val SpatialSpring = spring<Float>(
        dampingRatio = 0.86f,
        stiffness = 380f,
    )

    val PressSpring = spring<Float>(
        dampingRatio = 0.78f,
        stiffness = 520f,
    )

    val RingSpring = spring<Float>(
        dampingRatio = 0.84f,
        stiffness = 190f,
    )
}

@Composable
fun Modifier.tactileScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.978f,
    hoveredScale: Float = 1.008f,
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> pressedScale
            isHovered -> hoveredScale
            else -> 1f
        },
        animationSpec = GhickinMotion.PressSpring,
        label = "tactileScale",
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

@Composable
fun Modifier.tactileScale(
    pressedScale: Float = 0.975f,
): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = GhickinMotion.PressSpring,
        label = "tactileScaleSimple",
    )
    return this
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    isPressed = event.changes.any { it.pressed }
                }
            }
        }
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
}

@Composable
fun KineticNumberText(
    targetValue: Int,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    delayMillis: Long = 0L,
) {
    val animatedValue = remember { Animatable(0f) }

    LaunchedEffect(targetValue) {
        if (delayMillis > 0L) {
            delay(delayMillis)
        }
        animatedValue.animateTo(
            targetValue = targetValue.toFloat(),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = 140f,
            ),
        )
    }

    Text(
        text = "$prefix${animatedValue.value.roundToInt()}$suffix",
        style = style,
        color = color,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
fun StaggeredEntrance(
    delayMillis: Long = 0L,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (delayMillis > 0L) {
            delay(delayMillis)
        }
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(durationMillis = 260)) +
            slideInVertically(
                initialOffsetY = { it / 14 },
                animationSpec = spring(
                    dampingRatio = 0.88f,
                    stiffness = 380f,
                ),
            ),
    ) {
        content()
    }
}
