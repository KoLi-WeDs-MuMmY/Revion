package com.example.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FocusMode
import com.example.ui.components.CalmStatusPulseDot
import com.example.ui.components.KineticNumberText
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AppThemeId
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoMetricMedium

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeHeaderBar(
    streakDays: Int,
    focusMode: FocusMode,
    activeTheme: AppThemeId,
    activeMood: AccentMood,
    isWideLayout: Boolean,
    onToggleFocusMode: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isWideLayout) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            GreetingBlock(
                activeTheme = activeTheme,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StreakAndFocusPill(
                    streakDays = streakDays,
                    focusMode = focusMode,
                    activeMood = activeMood,
                    onToggleFocusMode = onToggleFocusMode,
                )
                ActiveThemePill(
                    activeTheme = activeTheme,
                    onClick = onOpenSettings,
                )
                SettingsTopBarGearButton(
                    testTag = "home_header_settings_button",
                    onClick = onOpenSettings,
                )
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                GreetingBlock(
                    activeTheme = activeTheme,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(10.dp))
                SettingsTopBarGearButton(
                    testTag = "home_header_settings_button",
                    onClick = onOpenSettings,
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StreakAndFocusPill(
                    streakDays = streakDays,
                    focusMode = focusMode,
                    activeMood = activeMood,
                    onToggleFocusMode = onToggleFocusMode,
                )
                ActiveThemePill(
                    activeTheme = activeTheme,
                    onClick = onOpenSettings,
                )
            }
        }
    }
}

@Composable
private fun GreetingBlock(
    activeTheme: AppThemeId,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = stringResource(R.string.header_greeting),
            style = MaterialTheme.typography.headlineMedium,
            color = tokens.inkPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "${stringResource(R.string.header_subtitle)} · ${activeTheme.label}",
            style = MaterialTheme.typography.bodySmall,
            color = tokens.inkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StreakAndFocusPill(
    streakDays: Int,
    focusMode: FocusMode,
    activeMood: AccentMood,
    onToggleFocusMode: () -> Unit,
) {
    val tokens = GhickinTokens.colors
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
                indication = ripple(color = tokens.themeAccent.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onToggleFocusMode,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
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
                color = activeMood.accent,
                dotSize = 10.dp,
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
private fun ActiveThemePill(
    activeTheme: AppThemeId,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(100.dp)

    Surface(
        shape = shape,
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier
            .testTag("home_theme_switcher_pill")
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.96f)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = tokens.themeAccent.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Palette,
                contentDescription = null,
                tint = tokens.themeAccent,
                modifier = Modifier.size(15.dp),
            )
            Text(
                text = activeTheme.label.uppercase(),
                style = MicroTagMono,
                color = tokens.inkPrimary,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun SettingsTopBarGearButton(
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }

    Surface(
        shape = CircleShape,
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier
            .size(48.dp)
            .testTag(testTag)
            .tactileScale(interaction, pressedScale = 0.92f)
            .clip(CircleShape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = tokens.themeAccent.copy(alpha = 0.18f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = stringResource(R.string.settings_open_desc),
                tint = tokens.inkPrimary,
                modifier = Modifier.size(21.dp),
            )
        }
    }
}
