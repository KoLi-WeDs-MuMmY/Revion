package com.example.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Palette
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FocusMode
import com.example.ui.components.CalmStatusPulseDot
import com.example.ui.components.KineticNumberText
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.MonoMetricMedium

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeHeaderBar(
    streakDays: Int,
    focusMode: FocusMode,
    activeMood: AccentMood,
    isWideLayout: Boolean,
    onToggleFocusMode: () -> Unit,
    onSelectMood: (AccentMood) -> Unit,
    onCycleMood: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    if (isWideLayout) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            GreetingBlock(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StreakAndFocusPill(
                    streakDays = streakDays,
                    focusMode = focusMode,
                    onToggleFocusMode = onToggleFocusMode,
                )
                DynamicAccentSwitcher(
                    activeMood = activeMood,
                    onSelectMood = onSelectMood,
                    onCycleMood = onCycleMood,
                )
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GreetingBlock()
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StreakAndFocusPill(
                    streakDays = streakDays,
                    focusMode = focusMode,
                    onToggleFocusMode = onToggleFocusMode,
                )
                DynamicAccentSwitcher(
                    activeMood = activeMood,
                    onSelectMood = onSelectMood,
                    onCycleMood = onCycleMood,
                )
            }
        }
    }
}

@Composable
private fun GreetingBlock(modifier: Modifier = Modifier) {
    val tokens = GhickinTokens.colors
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.header_greeting),
            style = MaterialTheme.typography.headlineMedium,
            color = tokens.inkPrimary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.header_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = tokens.inkSecondary,
        )
    }
}

@Composable
private fun StreakAndFocusPill(
    streakDays: Int,
    focusMode: FocusMode,
    onToggleFocusMode: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(100.dp)

    val focusLabel = when (focusMode) {
        FocusMode.AI_READY -> stringResource(R.string.header_focus_ready)
        FocusMode.DEEP_REVIEW -> stringResource(R.string.header_focus_calm)
    }
    val pulseDuration = when (focusMode) {
        FocusMode.AI_READY -> 2200
        FocusMode.DEEP_REVIEW -> 1400
    }

    Surface(
        shape = shape,
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier
            .testTag("focus_pulse_pill")
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.965f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = primary.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onToggleFocusMode,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = null,
                tint = AccentMood.AMBER.accent,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(5.dp))
            KineticNumberText(
                targetValue = streakDays,
                suffix = "d",
                style = MonoMetricMedium,
                color = tokens.inkPrimary,
                delayMillis = 120L,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(14.dp)
                    .background(tokens.hairlineRest),
            )
            Spacer(modifier = Modifier.width(10.dp))
            CalmStatusPulseDot(
                color = primary,
                dotSize = 11.dp,
                durationMillis = pulseDuration,
            )
            Spacer(modifier = Modifier.width(7.dp))
            AnimatedContent(
                targetState = focusLabel,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "focusModeText",
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    color = tokens.inkPrimary,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun DynamicAccentSwitcher(
    activeMood: AccentMood,
    onSelectMood: (AccentMood) -> Unit,
    onCycleMood: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary
    val cycleInteraction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(100.dp)

    Surface(
        shape = shape,
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier.testTag("dynamic_accent_switcher"),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(start = 10.dp, end = 8.dp, top = 5.dp, bottom = 5.dp),
        ) {
            // Quick-shift cycle button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .testTag("accent_cycle_button")
                    .size(34.dp)
                    .tactileScale(cycleInteraction, pressedScale = 0.92f)
                    .clip(CircleShape)
                    .background(tokens.carbonSurface)
                    .clickable(
                        interactionSource = cycleInteraction,
                        indication = ripple(color = primary.copy(alpha = 0.2f)),
                        role = Role.Button,
                        onClick = onCycleMood,
                    )
                    .semantics {
                        contentDescription = "Cycle dynamic accent mood"
                    },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Palette,
                    contentDescription = stringResource(R.string.header_accent_cycle_desc),
                    tint = primary,
                    modifier = Modifier.size(16.dp),
                )
            }

            Text(
                text = activeMood.label.uppercase(),
                style = MonoBadgeStyle,
                color = tokens.inkSecondary,
                modifier = Modifier.padding(end = 2.dp),
            )

            AccentMood.entries.forEach { mood ->
                val isSelected = mood == activeMood
                val dotSize by animateDpAsState(
                    targetValue = if (isSelected) 20.dp else 15.dp,
                    animationSpec = spring(dampingRatio = 0.72f, stiffness = 460f),
                    label = "swatchSize_${mood.id}",
                )
                val ringColor by animateColorAsState(
                    targetValue = if (isSelected) tokens.inkPrimary else tokens.hairlineRest,
                    animationSpec = spring(stiffness = 400f),
                    label = "swatchRing_${mood.id}",
                )
                val swatchInteraction = remember { MutableInteractionSource() }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .testTag("accent_swatch_${mood.id}")
                        .size(32.dp)
                        .tactileScale(swatchInteraction, pressedScale = 0.88f)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = swatchInteraction,
                            indication = ripple(color = mood.accent.copy(alpha = 0.25f)),
                            role = Role.RadioButton,
                            onClick = { onSelectMood(mood) },
                        )
                        .semantics {
                            contentDescription = "Select ${mood.label} accent theme"
                        },
                ) {
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(mood.accent)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = ringColor,
                                shape = CircleShape,
                            ),
                    )
                }
            }
        }
    }
}
