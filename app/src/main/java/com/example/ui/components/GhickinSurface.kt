package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.SpaceGroteskFamily

/**
 * Matte obsidian architectural card with crisp 1dp anodized hairline border.
 * Achieves refined visual elevation without neon glow or cyberpunk bloom.
 */
@Composable
fun ObsidianCard(
    modifier: Modifier = Modifier,
    containerColor: Color = GhickinTokens.colors.obsidianSurface,
    accentEdgeColor: Color? = null,
    surfaceWashColor: Color = Color.Transparent,
    cornerRadius: Dp = 20.dp,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val tokens = GhickinTokens.colors
    val shape: Shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val baseEdge = accentEdgeColor ?: tokens.hairlineRest
    val activeEdge = when {
        isPressed -> (accentEdgeColor ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.42f)
        isHovered -> (accentEdgeColor ?: tokens.hairlineSpecularTop).copy(alpha = 0.28f)
        else -> baseEdge
    }
    val animatedEdge by animateColorAsState(
        targetValue = activeEdge,
        animationSpec = spring(stiffness = 450f),
        label = "cardEdgeColor",
    )

    val interactiveModifier = if (onClick != null) {
        Modifier
            .tactileScale(interactionSource)
            .clip(shape)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                role = Role.Button,
                onClick = onClick,
            )
    } else {
        Modifier.clip(shape)
    }

    Box(
        modifier = modifier
            .then(interactiveModifier)
            .background(containerColor, shape)
            .drawBehind {
                if (surfaceWashColor != Color.Transparent) {
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                surfaceWashColor,
                                Color.Transparent,
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(size.width * 0.72f, size.height * 0.85f),
                        ),
                        cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
                    )
                }
                // Crisp anodized top-lit 1dp hairline border
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            animatedEdge,
                            tokens.hairlineSubtle,
                        ),
                    ),
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
                    style = Stroke(width = 1.dp.toPx()),
                )
            }
            .padding(contentPadding),
        content = content,
    )
}

@Composable
fun AnodizedPill(
    text: String,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = GhickinTokens.colors.carbonSurface,
    textColor: Color = GhickinTokens.colors.inkPrimary,
    borderColor: Color = accentColor.copy(alpha = 0.28f),
    leadingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(100.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        shape = shape,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier
                        .minimumInteractiveComponentSize()
                        .tactileScale(interactionSource, pressedScale = 0.96f)
                        .clip(shape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(color = accentColor.copy(alpha = 0.18f)),
                            role = Role.Button,
                            onClick = onClick,
                        )
                } else {
                    Modifier
                },
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
        ) {
            if (leadingContent != null) {
                leadingContent()
            }
            Text(
                text = text,
                style = MonoBadgeStyle,
                color = textColor,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun GhickinMonogramMark(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    val tokens = GhickinTokens.colors
    val accent = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(12.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(tokens.carbonSurface)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.45f),
                        tokens.hairlineRest,
                    ),
                ),
                shape = shape,
            ),
    ) {
        Text(
            text = "G",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = tokens.inkPrimary,
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .size(5.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent),
        )
    }
}
