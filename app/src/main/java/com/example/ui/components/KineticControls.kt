package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GhickinTokens
import kotlinx.coroutines.delay

@Composable
fun KineticFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    leadingDotColor: Color? = null,
    leadingIcon: ImageVector? = null,
    trailingBadge: String? = null,
) {
    val tokens = GhickinTokens.colors
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(100.dp)

    val containerColor by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.16f) else tokens.carbonSurface,
        animationSpec = spring(dampingRatio = 0.84f, stiffness = 420f),
        label = "chipContainer",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.55f) else tokens.hairlineRest,
        animationSpec = spring(dampingRatio = 0.84f, stiffness = 420f),
        label = "chipBorder",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) tokens.inkPrimary else tokens.inkSecondary,
        animationSpec = spring(stiffness = 420f),
        label = "chipText",
    )

    Surface(
        shape = shape,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.955f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accentColor.copy(alpha = 0.18f)),
                role = Role.RadioButton,
                onClick = onClick,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (selected) accentColor else tokens.inkMuted,
                    modifier = Modifier.size(15.dp),
                )
            } else if (leadingDotColor != null) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(leadingDotColor),
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                maxLines = 1,
            )

            if (trailingBadge != null) {
                Text(
                    text = trailingBadge,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) accentColor else tokens.inkMuted,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun AnimatedLinearProgressBar(
    progressPercent: Int,
    accentColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 5.dp,
    delayMillis: Long = 0L,
) {
    val tokens = GhickinTokens.colors
    val animated = remember { Animatable(0f) }
    val clamped = progressPercent.coerceIn(0, 100) / 100f

    LaunchedEffect(clamped) {
        if (delayMillis > 0L) delay(delayMillis)
        animated.animateTo(
            targetValue = clamped,
            animationSpec = GhickinMotion.RingSpring,
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(100.dp))
            .background(tokens.trackSubtle),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated.value.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(100.dp))
                .background(accentColor),
        )
    }
}
