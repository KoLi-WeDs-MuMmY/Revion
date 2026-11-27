package com.example.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.TextFields
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.FocusMode
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AppThemeId
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.displayFontForTheme
import com.example.ui.theme.rememberScreenLayoutMetrics

@Composable
fun SettingsScreen(
    activeTheme: AppThemeId,
    activeMood: AccentMood,
    focusMode: FocusMode,
    isWideLayout: Boolean,
    onSelectTheme: (AppThemeId) -> Unit,
    onSelectAccentMood: (AccentMood) -> Unit,
    onToggleFocusMode: () -> Unit,
    onCloseSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onCloseSettings)

    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = metrics.maxContentWidth),
            contentPadding = PaddingValues(
                horizontal = metrics.horizontalPadding,
                vertical = metrics.verticalPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item(key = "settings_header") {
                StaggeredEntrance(delayMillis = 0L) {
                    SettingsTopHeader(
                        isWideLayout = isWideLayout,
                        onBack = onCloseSettings,
                    )
                }
            }

            item(key = "settings_themes_section") {
                StaggeredEntrance(delayMillis = 45L) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Palette,
                                    contentDescription = null,
                                    tint = tokens.themeAccent,
                                    modifier = Modifier.size(19.dp),
                                )
                                Text(
                                    text = stringResource(R.string.settings_themes_section_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = tokens.inkPrimary,
                                )
                            }
                            Text(
                                text = stringResource(R.string.settings_themes_section_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                            )
                        }

                        val themes = AppThemeId.entries
                        if (isWideLayout) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                themes.chunked(2).forEach { pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    ) {
                                        pair.forEach { theme ->
                                            ThemePreviewCard(
                                                theme = theme,
                                                selected = theme == activeTheme,
                                                onClick = { onSelectTheme(theme) },
                                                modifier = Modifier.weight(1f),
                                            )
                                        }
                                        if (pair.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                themes.forEach { theme ->
                                    ThemePreviewCard(
                                        theme = theme,
                                        selected = theme == activeTheme,
                                        onClick = { onSelectTheme(theme) },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item(key = "settings_accents_and_posture") {
                StaggeredEntrance(delayMillis = 95L) {
                    if (isWideLayout) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            SubjectIndicatorAccentCard(
                                activeMood = activeMood,
                                onSelectMood = onSelectAccentMood,
                                modifier = Modifier.weight(1f),
                            )
                            DisplayPostureAndFocusCard(
                                activeTheme = activeTheme,
                                focusMode = focusMode,
                                isWideLayout = isWideLayout,
                                onToggleFocusMode = onToggleFocusMode,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            SubjectIndicatorAccentCard(
                                activeMood = activeMood,
                                onSelectMood = onSelectAccentMood,
                            )
                            DisplayPostureAndFocusCard(
                                activeTheme = activeTheme,
                                focusMode = focusMode,
                                isWideLayout = isWideLayout,
                                onToggleFocusMode = onToggleFocusMode,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsTopHeader(
    isWideLayout: Boolean,
    onBack: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val backInteraction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = tokens.obsidianSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .testTag("settings_back_button")
                .minimumInteractiveComponentSize()
                .tactileScale(backInteraction, pressedScale = 0.94f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = backInteraction,
                    indication = ripple(color = tokens.themeAccent.copy(alpha = 0.16f)),
                    role = Role.Button,
                    onClick = onBack,
                ),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(44.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Close Settings",
                    tint = tokens.inkPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                color = tokens.inkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.settings_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Surface(
            shape = RoundedCornerShape(100.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
        ) {
            Text(
                text = if (isWideLayout) "16:9 LANDSCAPE" else "9:16 PORTRAIT",
                style = MicroTagMono,
                color = tokens.inkSecondary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun ThemePreviewCard(
    theme: AppThemeId,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(20.dp)

    // Read each theme's real colors directly from its dedicated XML file
    val previewBg = colorResource(theme.bgRes)
    val previewSurface = colorResource(theme.surfaceRes)
    val previewCard = colorResource(theme.surfaceCardRes)
    val previewInk = colorResource(theme.inkPrimaryRes)
    val previewMuted = colorResource(theme.inkMutedRes)
    val previewCta = colorResource(theme.ctaRes)
    val previewOnCta = colorResource(theme.onCtaRes)
    val previewAccent = colorResource(theme.accentRes)
    val previewHairline = colorResource(theme.hairlineRes)

    val borderColor by animateColorAsState(
        targetValue = if (selected) tokens.themeAccent else tokens.hairlineRest,
        animationSpec = spring(stiffness = 380f),
        label = "themeCardBorder_${theme.id}",
    )

    Surface(
        shape = shape,
        color = tokens.obsidianSurface,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_theme_card_${theme.id}")
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.98f)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = previewAccent.copy(alpha = 0.2f)),
                role = Role.RadioButton,
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Live Miniature Theme Canvas Mockup
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = previewBg,
                border = BorderStroke(1.dp, previewHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Miniature typography specimen ("Aa") in theme's actual display font
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(previewSurface)
                            .border(1.dp, previewHairline, RoundedCornerShape(12.dp)),
                    ) {
                        Text(
                            text = "Aa",
                            fontFamily = displayFontForTheme(theme),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = previewInk,
                        )
                    }

                    // Miniature UI card + CTA preview
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(previewAccent),
                            )
                            Box(
                                modifier = Modifier
                                    .weight(0.65f)
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(previewInk.copy(alpha = 0.85f)),
                            )
                            Spacer(modifier = Modifier.weight(0.35f))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(previewMuted.copy(alpha = 0.5f)),
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(previewCta)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "Study",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = previewOnCta,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .height(20.dp)
                                    .weight(1f)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(previewCard)
                                    .border(1.dp, previewHairline, RoundedCornerShape(7.dp)),
                            )
                        }
                    }
                }
            }

            // Theme Title, Status & Font/XML Metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = theme.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.inkPrimary,
                    )
                    Text(
                        text = theme.tagline,
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (selected) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = tokens.themeAccent.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, tokens.themeAccent.copy(alpha = 0.45f)),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Active theme",
                                tint = tokens.themeAccent,
                                modifier = Modifier.size(13.dp),
                            )
                            Text(
                                text = "ACTIVE",
                                style = MicroTagMono,
                                color = tokens.themeAccent,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.TextFields,
                            contentDescription = null,
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = theme.fontPairingLabel,
                            style = MicroTagMono,
                            color = tokens.inkSecondary,
                            maxLines = 1,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Code,
                            contentDescription = null,
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = theme.xmlFileLabel,
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectIndicatorAccentCard(
    activeMood: AccentMood,
    onSelectMood: (AccentMood) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    ObsidianCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.settings_dot_accent_section_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = stringResource(R.string.settings_dot_accent_section_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AccentMood.entries.forEach { mood ->
                    val isSelected = mood == activeMood
                    val interaction = remember { MutableInteractionSource() }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) tokens.slateElevated else tokens.carbonSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) mood.accent.copy(alpha = 0.7f) else tokens.hairlineSubtle,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("settings_mood_${mood.id}")
                            .minimumInteractiveComponentSize()
                            .tactileScale(interaction, pressedScale = 0.95f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = interaction,
                                indication = ripple(color = mood.accent.copy(alpha = 0.2f)),
                                role = Role.RadioButton,
                                onClick = { onSelectMood(mood) },
                            ),
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(mood.accent),
                            )
                            Text(
                                text = mood.label,
                                style = MonoBadgeStyle,
                                color = if (isSelected) tokens.inkPrimary else tokens.inkSecondary,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DisplayPostureAndFocusCard(
    activeTheme: AppThemeId,
    focusMode: FocusMode,
    isWideLayout: Boolean,
    onToggleFocusMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val focusInteraction = remember { MutableInteractionSource() }

    ObsidianCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Devices,
                    contentDescription = null,
                    tint = tokens.themeAccent,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = stringResource(R.string.settings_posture_section_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = tokens.inkPrimary,
                )
            }

            Text(
                text = if (isWideLayout) {
                    "Active XML: values-land/dimens_screen.xml (16:9 Landscape Rail + Multi-Column)"
                } else {
                    "Active XML: values-port/dimens_screen.xml (9:16 Portrait Bottom Bar + Single Column)"
                },
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "ACTIVE THEME XML",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                    Text(
                        text = activeTheme.xmlFileLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = tokens.inkPrimary,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .testTag("settings_focus_mode_toggle")
                        .minimumInteractiveComponentSize()
                        .tactileScale(focusInteraction, pressedScale = 0.96f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = focusInteraction,
                            indication = ripple(color = tokens.themeAccent.copy(alpha = 0.16f)),
                            role = Role.Button,
                            onClick = onToggleFocusMode,
                        ),
                ) {
                    Text(
                        text = if (focusMode == FocusMode.AI_READY) "AI Focus Ready" else "Deep Review",
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.inkPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    )
                }
            }
        }
    }
}
