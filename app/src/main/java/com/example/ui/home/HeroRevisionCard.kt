package com.example.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.DailyRevisionBrief
import com.example.ui.components.AnodizedPill
import com.example.ui.components.CalmStatusPulseDot
import com.example.ui.components.KineticNumberText
import com.example.ui.components.ObsidianCard
import com.example.ui.components.tactileScale
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoMetricMedium

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeroRevisionCard(
    brief: DailyRevisionBrief,
    isWideLayout: Boolean,
    onInspectHero: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary

    ObsidianCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_revision_card"),
        containerColor = tokens.obsidianSurface,
        accentEdgeColor = tokens.accentHairline,
        surfaceWashColor = tokens.accentSurfaceWash,
        cornerRadius = 22.dp,
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 20.dp),
        onClick = onInspectHero,
    ) {
        if (isWideLayout) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                HeroEditorialContent(
                    brief = brief,
                    modifier = Modifier.weight(1.45f),
                )
                HeroActionColumn(
                    brief = brief,
                    onStartRevision = onInspectHero,
                    modifier = Modifier.weight(0.85f),
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                HeroEditorialContent(brief = brief)
                HeroMetricsRow(brief = brief)
                HeroPrimaryCtaButton(
                    onClick = onInspectHero,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HeroEditorialContent(
    brief: DailyRevisionBrief,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnodizedPill(
                text = stringResource(R.string.hero_badge_ready),
                accentColor = primary,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                borderColor = primary.copy(alpha = 0.34f),
                leadingContent = {
                    CalmStatusPulseDot(
                        color = primary,
                        dotSize = 10.dp,
                        durationMillis = 1900,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                },
            )
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = tokens.inkMuted,
                modifier = Modifier.size(15.dp),
            )
        }

        Text(
            text = brief.headline,
            style = MaterialTheme.typography.headlineSmall,
            color = tokens.inkPrimary,
        )

        Text(
            text = brief.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = tokens.inkSecondary,
            maxLines = 2,
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            brief.sourceSubjects.forEach { (label, mood) ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, mood.accent.copy(alpha = 0.28f)),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(mood.accent),
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.inkPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroActionColumn(
    brief: DailyRevisionBrief,
    onStartRevision: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            HeroMetricsRow(brief = brief)
            HeroPrimaryCtaButton(
                onClick = onStartRevision,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HeroMetricsRow(brief: DailyRevisionBrief) {
    val tokens = GhickinTokens.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        HeroMetricCell(
            value = brief.estimatedMinutes,
            suffix = "m",
            label = "Duration",
            delayMillis = 140L,
        )
        HeroMetricCell(
            value = brief.checkCount,
            suffix = "",
            label = "Checks",
            delayMillis = 210L,
        )
        HeroMetricCell(
            value = brief.targetRetentionPercent,
            suffix = "%",
            label = "Target",
            delayMillis = 280L,
            accentHighlight = true,
        )
    }
}

@Composable
private fun HeroMetricCell(
    value: Int,
    suffix: String,
    label: String,
    delayMillis: Long,
    accentHighlight: Boolean = false,
) {
    val tokens = GhickinTokens.colors
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        KineticNumberText(
            targetValue = value,
            suffix = suffix,
            style = MonoMetricMedium,
            color = if (accentHighlight) MaterialTheme.colorScheme.primary else tokens.inkPrimary,
            delayMillis = delayMillis,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = tokens.inkMuted,
        )
    }
}

@Composable
private fun HeroPrimaryCtaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(14.dp)

    // Subtle kinetic nudge on the forward arrow
    val infiniteTransition = rememberInfiniteTransition(label = "ctaArrowNudge")
    val arrowShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "arrowOffsetX",
    )

    Surface(
        shape = shape,
        color = primary,
        modifier = modifier
            .testTag("hero_start_revision_cta")
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.965f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = onPrimary.copy(alpha = 0.22f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.hero_cta_start),
                style = MaterialTheme.typography.labelLarge,
                color = onPrimary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = onPrimary,
                modifier = Modifier
                    .size(17.dp)
                    .offset(x = arrowShift.dp),
            )
        }
    }
}
