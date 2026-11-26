package com.example.ui.vault

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Info
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.VaultMaterial
import com.example.ui.components.AnodizedPill
import com.example.ui.components.ObsidianCard
import com.example.ui.components.tactileScale
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.MonoMetricMedium

/**
 * Full-width distraction-free Mock PDF Reader Deck shown after the right-hand preview pane collapses.
 */
@Composable
fun VaultMockPdfReader(
    material: VaultMaterial,
    currentPage: Int,
    showInfoDrawer: Boolean,
    isWideLayout: Boolean,
    onCloseReader: () -> Unit,
    onToggleInfoDrawer: () -> Unit,
    onStepPage: (Int) -> Unit,
    onJumpToPage: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val accent = material.subjectMood.accent

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = if (isWideLayout) 24.dp else 14.dp,
                vertical = if (isWideLayout) 16.dp else 12.dp,
            )
            .testTag("vault_mock_pdf_reader"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Top reader command bar
        ObsidianCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = tokens.obsidianSurface,
            accentEdgeColor = accent.copy(alpha = 0.36f),
            cornerRadius = 18.dp,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    val backInteraction = remember { MutableInteractionSource() }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier
                            .testTag("vault_reader_close_button")
                            .minimumInteractiveComponentSize()
                            .tactileScale(backInteraction, pressedScale = 0.95f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = backInteraction,
                                indication = ripple(color = accent.copy(alpha = 0.18f)),
                                role = Role.Button,
                                onClick = onCloseReader,
                            ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.vault_reader_close),
                                tint = tokens.inkPrimary,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.vault_reader_close),
                                style = MaterialTheme.typography.labelMedium,
                                color = tokens.inkPrimary,
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = material.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${material.subjectName} · ${material.chapterTitle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AnodizedPill(
                        text = "PAGE $currentPage / ${material.pageCount}",
                        accentColor = accent,
                        containerColor = tokens.carbonSurface,
                        textColor = tokens.inkPrimary,
                        borderColor = accent.copy(alpha = 0.32f),
                    )

                    val infoInteraction = remember { MutableInteractionSource() }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (showInfoDrawer) material.subjectMood.container else tokens.carbonSurface,
                        border = BorderStroke(
                            1.dp,
                            if (showInfoDrawer) accent.copy(alpha = 0.45f) else tokens.hairlineRest,
                        ),
                        modifier = Modifier
                            .testTag("vault_reader_info_toggle")
                            .minimumInteractiveComponentSize()
                            .tactileScale(infoInteraction, pressedScale = 0.95f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = infoInteraction,
                                indication = ripple(color = accent.copy(alpha = 0.18f)),
                                role = Role.Button,
                                onClick = onToggleInfoDrawer,
                            ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = stringResource(R.string.vault_reader_info_toggle),
                                tint = if (showInfoDrawer) accent else tokens.inkSecondary,
                                modifier = Modifier.size(16.dp),
                            )
                            if (isWideLayout) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.vault_reader_info_toggle),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (showInfoDrawer) material.subjectMood.onContainer else tokens.inkSecondary,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Main Reader Stage (Preview panel stays collapsed by default; optional info drawer slides in on demand)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    val forward = targetState >= initialState
                    val springSpec = spring<IntOffset>(dampingRatio = 0.86f, stiffness = 400f)
                    (fadeIn(tween(180)) +
                        slideInHorizontally(springSpec) { if (forward) it / 16 else -it / 16 }) togetherWith
                        (fadeOut(tween(120)) +
                            slideOutHorizontally(springSpec) { if (forward) -it / 20 else it / 20 })
                },
                label = "mockPdfPageTurn",
            ) { pageNumber ->
                val sectionIndex = ((pageNumber - 1) % material.outlineSections.size.coerceAtLeast(1))
                val activeSection = material.outlineSections.getOrElse(sectionIndex) { material.chapterTitle }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    ObsidianCard(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .widthIn(max = 920.dp),
                        containerColor = tokens.obsidianSurface,
                        accentEdgeColor = tokens.hairlineRest,
                        cornerRadius = 22.dp,
                        contentPadding = PaddingValues(if (isWideLayout) 32.dp else 20.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = activeSection.uppercase(),
                                    style = MonoBadgeStyle,
                                    color = accent,
                                )
                                Text(
                                    text = "SHEET %02d".format(pageNumber),
                                    style = MonoBadgeStyle,
                                    color = tokens.inkMuted,
                                )
                            }

                            Text(
                                text = "${material.chapterTitle} — Focus Sheet $pageNumber",
                                style = MaterialTheme.typography.headlineSmall,
                                color = tokens.inkPrimary,
                            )

                            Text(
                                text = material.summary,
                                style = MaterialTheme.typography.bodyLarge,
                                color = tokens.inkSecondary,
                            )

                            // Clean conceptual callout block simulating study sheet layout
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = tokens.carbonSurface,
                                border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = "CORE PRINCIPLE & RECALL CHECK",
                                        style = MonoBadgeStyle,
                                        color = accent,
                                    )
                                    Text(
                                        text = "Section highlight: $activeSection. Key relationships, boundary assumptions, and high-frequency exam checkpoints are anchored here for zero-distraction landscape study.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = tokens.inkPrimary,
                                    )
                                }
                            }

                            // Simulated structured diagram / notes placeholder canvas
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = tokens.carbonSurface,
                                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(110.dp),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = "KEY EQUATION / STEP",
                                            style = MonoBadgeStyle,
                                            color = tokens.inkMuted,
                                        )
                                        Text(
                                            text = "Page $pageNumber of ${material.pageCount}",
                                            style = MonoMetricMedium,
                                            color = tokens.inkPrimary,
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = tokens.carbonSurface,
                                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(110.dp),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = "ANNOTATION MARGIN",
                                            style = MonoBadgeStyle,
                                            color = tokens.inkMuted,
                                        )
                                        Text(
                                            text = stringResource(R.string.vault_reader_mock_notice),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = tokens.inkSecondary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Optional collapsible quick-outline drawer (hidden by default!)
            androidx.compose.animation.AnimatedVisibility(
                visible = showInfoDrawer,
                enter = slideInHorizontally(spring(dampingRatio = 0.86f, stiffness = 400f)) { it } + fadeIn(),
                exit = slideOutHorizontally(spring(dampingRatio = 0.86f, stiffness = 400f)) { it } + fadeOut(),
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.38f)),
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = 260.dp, max = 320.dp)
                        .testTag("vault_reader_info_drawer"),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "QUICK OUTLINE",
                            style = MonoBadgeStyle,
                            color = accent,
                        )
                        material.outlineSections.forEachIndexed { idx, section ->
                            val targetPage = ((idx * (material.pageCount / material.outlineSections.size.coerceAtLeast(1))) + 1)
                                .coerceIn(1, material.pageCount)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = tokens.obsidianSurface,
                                border = BorderStroke(1.dp, tokens.hairlineSubtle),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onJumpToPage(targetPage) },
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = section,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.inkPrimary,
                                    )
                                    Text(
                                        text = "Jump to p.$targetPage",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = tokens.inkMuted,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Page Stepper Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            PageStepButton(
                label = "Previous",
                enabled = currentPage > 1,
                icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                iconLeading = true,
                onClick = { onStepPage(-1) },
                modifier = Modifier.testTag("vault_reader_prev_page"),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "$currentPage / ${material.pageCount}",
                style = MonoMetricMedium,
                color = tokens.inkPrimary,
            )
            Spacer(modifier = Modifier.width(16.dp))
            PageStepButton(
                label = "Next",
                enabled = currentPage < material.pageCount,
                icon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                iconLeading = false,
                onClick = { onStepPage(1) },
                modifier = Modifier.testTag("vault_reader_next_page"),
            )
        }
    }
}

@Composable
private fun PageStepButton(
    label: String,
    enabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconLeading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(12.dp)

    Surface(
        shape = shape,
        color = if (enabled) tokens.carbonSurface else tokens.obsidianSurface,
        border = BorderStroke(1.dp, if (enabled) tokens.hairlineRest else tokens.hairlineSubtle),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.95f)
            .clip(shape)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = ripple(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            if (iconLeading) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (enabled) tokens.inkPrimary else tokens.inkMuted,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (enabled) tokens.inkPrimary else tokens.inkMuted,
            )
            if (!iconLeading) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (enabled) tokens.inkPrimary else tokens.inkMuted,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
