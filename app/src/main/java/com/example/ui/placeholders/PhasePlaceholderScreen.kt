package com.example.ui.placeholders

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Palette
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
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.WorkspaceTab
import com.example.ui.components.AnodizedPill
import com.example.ui.components.CalmStatusPulseDot
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle

@Composable
fun PhasePlaceholderScreen(
    tab: WorkspaceTab,
    activeMood: AccentMood,
    isWideLayout: Boolean,
    onReturnHome: () -> Unit,
    onCycleAccentMood: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = if (isWideLayout) 28.dp else 16.dp,
                vertical = if (isWideLayout) 24.dp else 16.dp,
            )
            .testTag("placeholder_screen_${tab.routeId}"),
        contentAlignment = Alignment.Center,
    ) {
        StaggeredEntrance(delayMillis = 0L) {
            ObsidianCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 860.dp),
                containerColor = tokens.obsidianSurface,
                accentEdgeColor = tokens.accentHairline,
                surfaceWashColor = tokens.accentSurfaceWash,
                cornerRadius = 24.dp,
                contentPadding = PaddingValues(if (isWideLayout) 28.dp else 20.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    // Header row: phase badge + live accent indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            AnodizedPill(
                                text = tab.phaseTag,
                                accentColor = primary,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                borderColor = primary.copy(alpha = 0.36f),
                                leadingContent = {
                                    CalmStatusPulseDot(
                                        color = primary,
                                        dotSize = 9.dp,
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                },
                            )
                            Text(
                                text = stringResource(R.string.placeholder_status_blueprint),
                                style = MonoBadgeStyle,
                                color = tokens.inkMuted,
                            )
                        }
                    }

                    // Module emblem & title block
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(tokens.carbonSurface),
                        ) {
                            Icon(
                                imageVector = tab.selectedIcon,
                                contentDescription = tab.label,
                                tint = primary,
                                modifier = Modifier.size(28.dp),
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = tab.previewTitle,
                                style = MaterialTheme.typography.headlineSmall,
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = tab.previewSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.inkSecondary,
                            )
                        }
                    }

                    // Planned capabilities list
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = stringResource(R.string.placeholder_planned_modules),
                            style = MonoBadgeStyle,
                            color = tokens.inkMuted,
                        )
                        tab.plannedCapabilities.forEach { capability ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = tokens.carbonSurface,
                                border = BorderStroke(1.dp, tokens.hairlineSubtle),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircleOutline,
                                        contentDescription = null,
                                        tint = primary,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Text(
                                        text = capability,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = tokens.inkPrimary,
                                    )
                                }
                            }
                        }
                    }

                    // Bottom action bar: Return to Home + Live Dynamic Accent test button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val homeInteraction = remember { MutableInteractionSource() }
                        val homeShape = RoundedCornerShape(14.dp)
                        Surface(
                            shape = homeShape,
                            color = primary,
                            modifier = Modifier
                                .testTag("placeholder_return_home_button")
                                .minimumInteractiveComponentSize()
                                .tactileScale(homeInteraction, pressedScale = 0.97f)
                                .clip(homeShape)
                                .clickable(
                                    interactionSource = homeInteraction,
                                    indication = ripple(color = onPrimary.copy(alpha = 0.22f)),
                                    role = Role.Button,
                                    onClick = onReturnHome,
                                ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = null,
                                    tint = onPrimary,
                                    modifier = Modifier.size(17.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.placeholder_return_home),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = onPrimary,
                                )
                            }
                        }

                        val accentInteraction = remember { MutableInteractionSource() }
                        val accentShape = RoundedCornerShape(14.dp)
                        Surface(
                            shape = accentShape,
                            color = tokens.carbonSurface,
                            border = BorderStroke(1.dp, tokens.hairlineRest),
                            modifier = Modifier
                                .testTag("placeholder_accent_cycle_button")
                                .minimumInteractiveComponentSize()
                                .tactileScale(accentInteraction, pressedScale = 0.97f)
                                .clip(accentShape)
                                .clickable(
                                    interactionSource = accentInteraction,
                                    indication = ripple(color = primary.copy(alpha = 0.18f)),
                                    role = Role.Button,
                                    onClick = onCycleAccentMood,
                                ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Palette,
                                    contentDescription = null,
                                    tint = primary,
                                    modifier = Modifier.size(17.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${stringResource(R.string.placeholder_test_accent)} (${activeMood.label})",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = tokens.inkPrimary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
