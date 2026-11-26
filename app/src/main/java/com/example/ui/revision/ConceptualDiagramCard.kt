package com.example.ui.revision

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.DiagramKind
import com.example.ui.components.AnodizedPill
import com.example.ui.components.ObsidianCard
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated Jetpack Compose Canvas conceptual diagram card with Show / Hide Labels toggle.
 * Crisp architectural linework and calm travelling pulse highlights (zero neon bloom).
 */
@Composable
fun ConceptualDiagramCard(
    diagramKind: DiagramKind,
    title: String,
    caption: String,
    accentColor: Color,
    showLabels: Boolean,
    onToggleLabels: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    val infiniteTransition = rememberInfiniteTransition(label = "diagramPathPulse")
    val flowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "flowPhase",
    )

    ObsidianCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("conceptual_diagram_card"),
        containerColor = tokens.obsidianSurface,
        accentEdgeColor = accentColor.copy(alpha = 0.36f),
        surfaceWashColor = accentColor.copy(alpha = 0.05f),
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Card header with Show/Hide Labels spring toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    AnodizedPill(
                        text = stringResource(R.string.revision_diagram_badge),
                        accentColor = accentColor,
                        containerColor = accentColor.copy(alpha = 0.14f),
                        textColor = accentColor,
                        borderColor = accentColor.copy(alpha = 0.32f),
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.inkPrimary,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                val toggleInteraction = remember { MutableInteractionSource() }
                val toggleShape = RoundedCornerShape(100.dp)
                Surface(
                    shape = toggleShape,
                    color = if (showLabels) accentColor.copy(alpha = 0.16f) else tokens.carbonSurface,
                    border = BorderStroke(
                        1.dp,
                        if (showLabels) accentColor.copy(alpha = 0.48f) else tokens.hairlineRest,
                    ),
                    modifier = Modifier
                        .testTag("diagram_labels_toggle")
                        .minimumInteractiveComponentSize()
                        .tactileScale(toggleInteraction, pressedScale = 0.95f)
                        .clip(toggleShape)
                        .clickable(
                            interactionSource = toggleInteraction,
                            indication = ripple(color = accentColor.copy(alpha = 0.18f)),
                            role = Role.Switch,
                            onClick = onToggleLabels,
                        ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                    ) {
                        Icon(
                            imageVector = if (showLabels) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                            contentDescription = null,
                            tint = if (showLabels) accentColor else tokens.inkSecondary,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showLabels) {
                                stringResource(R.string.revision_labels_hide)
                            } else {
                                stringResource(R.string.revision_labels_show)
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (showLabels) tokens.inkPrimary else tokens.inkSecondary,
                        )
                    }
                }
            }

            // Matte Diagram Canvas Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(tokens.amoledBackground)
                    .padding(10.dp),
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("diagram_canvas_${diagramKind.name.lowercase()}"),
                ) {
                    when (diagramKind) {
                        DiagramKind.GAUSS_SPHERE -> drawGaussSphereDiagram(
                            accent = accentColor,
                            hairline = tokens.hairlineRest,
                            track = tokens.trackSubtle,
                            phase = flowPhase,
                        )

                        DiagramKind.ARRHENIUS_CURVE -> drawArrheniusDiagram(
                            accent = accentColor,
                            secondary = AccentMood.EMERALD.accent,
                            hairline = tokens.hairlineRest,
                            phase = flowPhase,
                        )

                        DiagramKind.DNA_REPLICATION -> drawDnaReplicationDiagram(
                            accent = accentColor,
                            secondary = AccentMood.CYAN.accent,
                            hairline = tokens.hairlineRest,
                            phase = flowPhase,
                        )

                        DiagramKind.COMPOUND_MAP -> drawCompoundSynthesisDiagram(
                            hairline = tokens.hairlineRest,
                            phase = flowPhase,
                        )
                    }
                }

                // Toggleable Diagram Labels Overlay
                androidx.compose.animation.AnimatedVisibility(
                    visible = showLabels,
                    enter = fadeIn(tween(220)),
                    exit = fadeOut(tween(180)),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    DiagramLabelsOverlay(
                        diagramKind = diagramKind,
                        accentColor = accentColor,
                    )
                }
            }

            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
            )
        }
    }
}

@Composable
private fun DiagramLabelsOverlay(
    diagramKind: DiagramKind,
    accentColor: Color,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (diagramKind) {
            DiagramKind.GAUSS_SPHERE -> {
                DiagramTagChip("E = 0 inside (r < R)", accentColor, Modifier.align(Alignment.Center))
                DiagramTagChip("Shell R (+Q)", accentColor, Modifier.align(Alignment.TopStart))
                DiagramTagChip("Gaussian r > R", AccentMood.AMBER.accent, Modifier.align(Alignment.TopEnd))
                DiagramTagChip("Φ = Q_enc / ε₀", accentColor, Modifier.align(Alignment.BottomEnd))
            }

            DiagramKind.ARRHENIUS_CURVE -> {
                DiagramTagChip("Ea (Uncatalyzed)", accentColor, Modifier.align(Alignment.TopCenter))
                DiagramTagChip("Ea (Catalyzed)", AccentMood.EMERALD.accent, Modifier.align(Alignment.Center))
                DiagramTagChip("Reactants", GhickinTokens.colors.inkSecondary, Modifier.align(Alignment.BottomStart))
                DiagramTagChip("Products (ΔH invariant)", GhickinTokens.colors.inkSecondary, Modifier.align(Alignment.BottomEnd))
            }

            DiagramKind.DNA_REPLICATION -> {
                DiagramTagChip("Helicase Fork", accentColor, Modifier.align(Alignment.CenterStart))
                DiagramTagChip("Leading 5' → 3' (Continuous)", AccentMood.CYAN.accent, Modifier.align(Alignment.TopEnd))
                DiagramTagChip("Lagging (Okazaki + Ligase)", accentColor, Modifier.align(Alignment.BottomEnd))
            }

            DiagramKind.COMPOUND_MAP -> {
                DiagramTagChip("Flux Symmetry (E=0)", AccentMood.CYAN.accent, Modifier.align(Alignment.TopStart))
                DiagramTagChip("Arrhenius Slope (−Ea/R)", AccentMood.VIOLET.accent, Modifier.align(Alignment.TopEnd))
                DiagramTagChip("DNA Pol III (5'→3')", AccentMood.EMERALD.accent, Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}

@Composable
private fun DiagramTagChip(
    text: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = tokens.carbonSurface.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.34f)),
        modifier = modifier.padding(4.dp),
    ) {
        Text(
            text = text,
            style = MonoBadgeStyle,
            color = tokens.inkPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

private fun DrawScope.drawGaussSphereDiagram(
    accent: Color,
    hairline: Color,
    track: Color,
    phase: Float,
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val minDim = size.minDimension
    val innerRadius = minDim * 0.23f
    val outerGaussianRadius = minDim * 0.41f
    val rayOuterRadius = minDim * 0.48f

    // Subtle matte core indicating E = 0 interior
    drawCircle(
        color = track.copy(alpha = 0.45f),
        radius = innerRadius,
        center = center,
    )

    // Conducting spherical shell ring
    drawCircle(
        color = accent,
        radius = innerRadius,
        center = center,
        style = Stroke(width = 2.5.dp.toPx()),
    )

    // Dashed Gaussian envelope (r > R)
    drawCircle(
        color = AccentMood.AMBER.accent.copy(alpha = 0.72f),
        radius = outerGaussianRadius,
        center = center,
        style = Stroke(
            width = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), phase * 44f),
        ),
    )

    // 8 radial electric field lines + animated outward flux pulse dots
    for (i in 0 until 8) {
        val angleRad = (i * 45.0) * PI / 180.0
        val cosA = cos(angleRad).toFloat()
        val sinA = sin(angleRad).toFloat()
        val start = Offset(center.x + innerRadius * cosA, center.y + innerRadius * sinA)
        val end = Offset(center.x + rayOuterRadius * cosA, center.y + rayOuterRadius * sinA)

        drawLine(
            color = hairline.copy(alpha = 0.45f),
            start = start,
            end = end,
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )

        val pulseR = innerRadius + (rayOuterRadius - innerRadius) * phase
        val pulsePos = Offset(center.x + pulseR * cosA, center.y + pulseR * sinA)
        drawCircle(
            color = accent.copy(alpha = 0.9f - 0.55f * phase),
            radius = 3.2.dp.toPx(),
            center = pulsePos,
        )
    }
}

private fun DrawScope.drawArrheniusDiagram(
    accent: Color,
    secondary: Color,
    hairline: Color,
    phase: Float,
) {
    val left = size.width * 0.08f
    val right = size.width * 0.92f
    val top = size.height * 0.14f
    val bottom = size.height * 0.84f

    // Clean coordinate axes
    drawLine(hairline, Offset(left, top), Offset(left, bottom), strokeWidth = 1.5.dp.toPx())
    drawLine(hairline, Offset(left, bottom), Offset(right, bottom), strokeWidth = 1.5.dp.toPx())

    val startPt = Offset(left + 16f, size.height * 0.62f)
    val endPt = Offset(right - 16f, size.height * 0.74f)
    val highCtrl = Offset(size.width * 0.46f, size.height * 0.04f)
    val lowCtrl = Offset(size.width * 0.48f, size.height * 0.34f)

    // Uncatalyzed high Ea curve
    val uncatalyzedPath = Path().apply {
        moveTo(startPt.x, startPt.y)
        quadraticTo(highCtrl.x, highCtrl.y, endPt.x, endPt.y)
    }
    drawPath(
        path = uncatalyzedPath,
        color = accent,
        style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round),
    )

    // Catalyzed lower Ea dashed path
    val catalyzedPath = Path().apply {
        moveTo(startPt.x, startPt.y)
        quadraticTo(lowCtrl.x, lowCtrl.y, endPt.x, endPt.y)
    }
    drawPath(
        path = catalyzedPath,
        color = secondary,
        style = Stroke(
            width = 2.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
        ),
    )

    // Travelling progress highlight bead along catalyzed curve (quadratic Bézier)
    val t = phase
    val oneMinusT = 1f - t
    val px = oneMinusT * oneMinusT * startPt.x + 2 * oneMinusT * t * lowCtrl.x + t * t * endPt.x
    val py = oneMinusT * oneMinusT * startPt.y + 2 * oneMinusT * t * lowCtrl.y + t * t * endPt.y
    drawCircle(
        color = secondary,
        radius = 4.5.dp.toPx(),
        center = Offset(px, py),
    )
}

private fun DrawScope.drawDnaReplicationDiagram(
    accent: Color,
    secondary: Color,
    hairline: Color,
    phase: Float,
) {
    val midY = size.height * 0.5f
    val startX = size.width * 0.08f
    val forkX = size.width * 0.36f
    val endX = size.width * 0.90f
    val topY = size.height * 0.22f
    val botY = size.height * 0.78f

    // Unwound double helix stem on left
    drawLine(hairline, Offset(startX, midY - 7.dp.toPx()), Offset(forkX, midY - 4.dp.toPx()), strokeWidth = 2.dp.toPx())
    drawLine(hairline, Offset(startX, midY + 7.dp.toPx()), Offset(forkX, midY + 4.dp.toPx()), strokeWidth = 2.dp.toPx())

    // Upper template + continuous leading strand
    drawLine(hairline.copy(alpha = 0.65f), Offset(forkX, midY - 4.dp.toPx()), Offset(endX, topY), strokeWidth = 2.dp.toPx())
    val leadEndX = forkX + (endX - forkX) * (0.25f + 0.72f * phase)
    val leadEndY = (midY - 4.dp.toPx()) + (topY - (midY - 4.dp.toPx())) * (0.25f + 0.72f * phase)
    drawLine(
        color = secondary,
        start = Offset(forkX + 8.dp.toPx(), midY - 12.dp.toPx()),
        end = Offset(leadEndX, leadEndY - 8.dp.toPx()),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round,
    )

    // Lower template + Okazaki discontinuous lagging fragments
    drawLine(hairline.copy(alpha = 0.65f), Offset(forkX, midY + 4.dp.toPx()), Offset(endX, botY), strokeWidth = 2.dp.toPx())
    for (seg in 0 until 3) {
        val t0 = 0.12f + seg * 0.28f
        val t1 = t0 + 0.20f
        val sx = forkX + (endX - forkX) * t0
        val sy = (midY + 12.dp.toPx()) + (botY - midY) * t0
        val ex = forkX + (endX - forkX) * t1
        val ey = (midY + 12.dp.toPx()) + (botY - midY) * t1
        val segAlpha = if (((phase * 3f).toInt() % 3) == seg) 1f else 0.52f
        drawLine(
            color = accent.copy(alpha = segAlpha),
            start = Offset(sx, sy),
            end = Offset(ex, ey),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }

    // Helicase node at fork junction
    drawCircle(
        color = accent.copy(alpha = 0.22f),
        radius = (11f + 3f * sin(phase * 2 * PI).toFloat()).dp.toPx(),
        center = Offset(forkX, midY),
    )
    drawCircle(
        color = accent,
        radius = 5.5.dp.toPx(),
        center = Offset(forkX, midY),
    )
}

private fun DrawScope.drawCompoundSynthesisDiagram(
    hairline: Color,
    phase: Float,
) {
    val nodePhys = Offset(size.width * 0.24f, size.height * 0.30f)
    val nodeChem = Offset(size.width * 0.76f, size.height * 0.30f)
    val nodeBio = Offset(size.width * 0.50f, size.height * 0.76f)

    val edges = listOf(
        Triple(nodePhys, nodeChem, AccentMood.CYAN.accent),
        Triple(nodeChem, nodeBio, AccentMood.VIOLET.accent),
        Triple(nodeBio, nodePhys, AccentMood.EMERALD.accent),
    )

    edges.forEach { (start, end, color) ->
        drawLine(
            color = hairline.copy(alpha = 0.6f),
            start = start,
            end = end,
            strokeWidth = 1.6.dp.toPx(),
        )
        val px = start.x + (end.x - start.x) * phase
        val py = start.y + (end.y - start.y) * phase
        drawCircle(
            color = color,
            radius = 3.8.dp.toPx(),
            center = Offset(px, py),
        )
    }

    listOf(
        nodePhys to AccentMood.CYAN.accent,
        nodeChem to AccentMood.VIOLET.accent,
        nodeBio to AccentMood.EMERALD.accent,
    ).forEach { (pos, color) ->
        drawCircle(color = color.copy(alpha = 0.20f), radius = 14.dp.toPx(), center = pos)
        drawCircle(color = color, radius = 6.dp.toPx(), center = pos)
    }
}
