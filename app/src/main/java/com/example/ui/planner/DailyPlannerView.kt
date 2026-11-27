package com.example.ui.planner

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.PlannerApprovalStatus
import com.example.model.PlannerBlock
import com.example.model.PlannerBlockCategory
import com.example.model.TutorPlannerUiState
import com.example.model.formatClockMinutes
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.AnimatedMasteryRing
import com.example.ui.components.AnodizedPill
import com.example.ui.components.KineticNumberText
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.FeedbackCorrect
import com.example.ui.theme.FeedbackCorrectContainer
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.InkOnAccentDark
import com.example.ui.theme.KineticDisplayLarge
import com.example.ui.theme.KineticDisplayXL
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoMetricMedium

@Composable
fun DailyPlannerView(
    state: TutorPlannerUiState,
    isWideLayout: Boolean,
    compactCoPlan: Boolean,
    onOpenScheduleSettings: () -> Unit,
    onToggleBlockCompleted: (String) -> Unit,
    onRequestAddBlock: () -> Unit,
    onRequestEditBlock: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val useTwoPane = isWideLayout && !compactCoPlan

    if (useTwoPane) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .testTag("daily_planner_view"),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PlannerSummaryAndAnchorsPane(
                state = state,
                onOpenScheduleSettings = onOpenScheduleSettings,
                onRequestAddBlock = onRequestAddBlock,
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight(),
            )
            PlannerTimelinePane(
                state = state,
                showCompactTopStrip = false,
                onOpenScheduleSettings = onOpenScheduleSettings,
                onToggleBlockCompleted = onToggleBlockCompleted,
                onRequestAddBlock = onRequestAddBlock,
                onRequestEditBlock = onRequestEditBlock,
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight(),
            )
        }
    } else {
        PlannerTimelinePane(
            state = state,
            showCompactTopStrip = true,
            onOpenScheduleSettings = onOpenScheduleSettings,
            onToggleBlockCompleted = onToggleBlockCompleted,
            onRequestAddBlock = onRequestAddBlock,
            onRequestEditBlock = onRequestEditBlock,
            modifier = modifier
                .fillMaxSize()
                .testTag("daily_planner_view"),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlannerSummaryAndAnchorsPane(
    state: TutorPlannerUiState,
    onOpenScheduleSettings: () -> Unit,
    onRequestAddBlock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Wake-to-Sleep Anchor & Adaptive Device Sync Card
        StaggeredEntrance(delayMillis = 20L) {
            ObsidianCard(
                accentEdgeColor = primary.copy(alpha = 0.36f),
                surfaceWashColor = primary.copy(alpha = 0.06f),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AnodizedPill(
                            text = if (state.isAdaptiveSyncEnabled) {
                                stringResource(R.string.planner_adaptive_badge)
                            } else {
                                stringResource(R.string.planner_manual_badge)
                            },
                            accentColor = if (state.isAdaptiveSyncEnabled) FeedbackCorrect else primary,
                            containerColor = if (state.isAdaptiveSyncEnabled) FeedbackCorrectContainer else tokens.carbonSurface,
                            textColor = if (state.isAdaptiveSyncEnabled) FeedbackCorrect else tokens.inkSecondary,
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = tokens.carbonSurface,
                            border = BorderStroke(1.dp, tokens.hairlineRest),
                            modifier = Modifier
                                .testTag("planner_settings_button")
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(
                                    role = Role.Button,
                                    onClick = onOpenScheduleSettings,
                                ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Tune,
                                    contentDescription = stringResource(R.string.planner_settings_cta),
                                    tint = primary,
                                    modifier = Modifier.size(14.dp),
                                )
                                Text(
                                    text = stringResource(R.string.planner_settings_cta),
                                    style = MicroTagMono,
                                    color = tokens.inkPrimary,
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        AnchorClockTile(
                            label = stringResource(R.string.planner_wake_anchor),
                            clockText = formatClockMinutes(state.effectiveWakeMinutes),
                            icon = Icons.Rounded.WbSunny,
                            accent = AccentMood.AMBER.accent,
                            modifier = Modifier.weight(1f),
                        )
                        AnchorClockTile(
                            label = stringResource(R.string.planner_sleep_anchor),
                            clockText = formatClockMinutes(state.effectiveSleepMinutes),
                            icon = Icons.Rounded.Bedtime,
                            accent = AccentMood.VIOLET.accent,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Text(
                        text = if (state.isAdaptiveSyncEnabled) {
                            "Shifted from observed First Unlock (${formatClockMinutes(state.observedUnlockMinutes)}) & Last Screen-Off (${formatClockMinutes(state.observedScreenOffMinutes)}) · ${state.awakeDurationLabel}"
                        } else {
                            "Using manual first-login baseline anchors · ${state.awakeDurationLabel}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                    )
                }
            }
        }

        // Progress & Remaining Syllabus Card
        StaggeredEntrance(delayMillis = 70L) {
            ObsidianCard(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.planner_progress_title).uppercase(),
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            KineticNumberText(
                                targetValue = state.remainingSyllabusChapters,
                                style = KineticDisplayXL,
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = "${stringResource(R.string.planner_remaining_label)} of ${state.totalSyllabusChapters}",
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.inkSecondary,
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            AnimatedMasteryRing(
                                percentage = state.plannerCompletionPercent,
                                accentColor = primary,
                                ringSize = 64.dp,
                                strokeWidth = 5.5.dp,
                            )
                            Text(
                                text = stringResource(R.string.planner_completion_label),
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                            )
                        }
                    }

                    AnimatedLinearProgressBar(
                        progressPercent = state.syllabusCompletedPercent,
                        accentColor = primary,
                        height = 6.dp,
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        PlannerBlockCategory.entries.forEach { cat ->
                            val count = state.blocks.count { it.category == cat }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = tokens.carbonSurface,
                                border = BorderStroke(1.dp, tokens.hairlineRest),
                            ) {
                                Text(
                                    text = "${cat.badgeCode} · $count",
                                    style = MicroTagMono,
                                    color = categoryColor(cat),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        // AI-Gated Request New Block CTA
        StaggeredEntrance(delayMillis = 120L) {
            Button(
                onClick = onRequestAddBlock,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = InkOnAccentDark,
                ),
                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("planner_request_add_button"),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.planner_request_add_cta),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun AnchorClockTile(
    label: String,
    clockText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = label,
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                    maxLines = 1,
                )
            }
            Text(
                text = clockText,
                style = KineticDisplayLarge,
                color = tokens.inkPrimary,
            )
        }
    }
}

@Composable
private fun PlannerTimelinePane(
    state: TutorPlannerUiState,
    showCompactTopStrip: Boolean,
    onOpenScheduleSettings: () -> Unit,
    onToggleBlockCompleted: (String) -> Unit,
    onRequestAddBlock: () -> Unit,
    onRequestEditBlock: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary

    LazyColumn(
        modifier = modifier.testTag("planner_timeline_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        if (showCompactTopStrip) {
            item(key = "compact_planner_strip") {
                ObsidianCard(
                    accentEdgeColor = primary.copy(alpha = 0.34f),
                    contentPadding = PaddingValues(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                AnodizedPill(
                                    text = "${formatClockMinutes(state.effectiveWakeMinutes)} → ${formatClockMinutes(state.effectiveSleepMinutes)}",
                                    accentColor = primary,
                                    containerColor = tokens.carbonSurface,
                                    textColor = tokens.inkPrimary,
                                )
                                Text(
                                    text = "${state.remainingSyllabusChapters} ch left",
                                    style = MicroTagMono,
                                    color = tokens.inkSecondary,
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = tokens.carbonSurface,
                                    border = BorderStroke(1.dp, tokens.hairlineRest),
                                    modifier = Modifier
                                        .testTag("planner_settings_button")
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable(role = Role.Button, onClick = onOpenScheduleSettings),
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Tune,
                                            contentDescription = stringResource(R.string.planner_settings_cta),
                                            tint = primary,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = primary,
                                    modifier = Modifier
                                        .testTag("planner_request_add_button")
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable(role = Role.Button, onClick = onRequestAddBlock),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = stringResource(R.string.planner_request_add_cta),
                                            tint = InkOnAccentDark,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Text(
                                            text = "Add",
                                            style = MicroTagMono,
                                            color = InkOnAccentDark,
                                        )
                                    }
                                }
                            }
                        }

                        AnimatedLinearProgressBar(
                            progressPercent = state.plannerCompletionPercent,
                            accentColor = primary,
                            height = 4.dp,
                        )
                    }
                }
            }
        }

        // Top Wake Anchor Boundary Ribbon
        item(key = "wake_anchor_ribbon") {
            TimelineBoundaryRibbon(
                icon = Icons.Rounded.WbSunny,
                title = stringResource(R.string.planner_wake_anchor),
                clockText = formatClockMinutes(state.effectiveWakeMinutes),
                subtitle = if (state.isAdaptiveSyncEnabled) "Adaptive unlock shift" else "Manual baseline",
                accent = AccentMood.AMBER.accent,
            )
        }

        // Scheduled Study / Revision / Quiz / Flashcards / Break Blocks
        itemsIndexed(state.blocks, key = { _, blk -> blk.id }) { index, block ->
            StaggeredEntrance(delayMillis = (index * 30L).coerceAtMost(180L)) {
                PlannerTimelineBlockCard(
                    block = block,
                    onToggleCompleted = { onToggleBlockCompleted(block.id) },
                    onRequestEdit = { onRequestEditBlock(block.id) },
                )
            }
        }

        // Bottom Sleep Boundary Lock Ribbon
        item(key = "sleep_anchor_ribbon") {
            TimelineBoundaryRibbon(
                icon = Icons.Rounded.Bedtime,
                title = stringResource(R.string.planner_sleep_anchor),
                clockText = formatClockMinutes(state.effectiveSleepMinutes),
                subtitle = "Hard cutoff enforced by AI Guardrail",
                accent = AccentMood.VIOLET.accent,
            )
        }
    }
}

@Composable
private fun TimelineBoundaryRibbon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    clockText: String,
    subtitle: String,
    accent: Color,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tokens.carbonSurface.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.30f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = title,
                    style = MicroTagMono,
                    color = accent,
                )
                Text(
                    text = "· $subtitle",
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = clockText,
                style = MonoMetricMedium,
                color = tokens.inkPrimary,
            )
        }
    }
}

@Composable
private fun PlannerTimelineBlockCard(
    block: PlannerBlock,
    onToggleCompleted: () -> Unit,
    onRequestEdit: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val catAccent = categoryColor(block.category)
    val checkInteraction = remember { MutableInteractionSource() }
    val editInteraction = remember { MutableInteractionSource() }

    val edgeColor by animateColorAsState(
        targetValue = when {
            block.isCompleted -> FeedbackCorrect.copy(alpha = 0.36f)
            block.approvalStatus == PlannerApprovalStatus.AI_APPROVED -> FeedbackCorrect.copy(alpha = 0.50f)
            else -> catAccent.copy(alpha = 0.32f)
        },
        animationSpec = spring(stiffness = 380f),
        label = "plannerBlockEdge",
    )

    ObsidianCard(
        accentEdgeColor = edgeColor,
        surfaceWashColor = if (block.isCompleted) FeedbackCorrect.copy(alpha = 0.04f) else catAccent.copy(alpha = 0.05f),
        contentPadding = PaddingValues(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("planner_block_${block.id}"),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Completion toggle button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .testTag("planner_block_check_${block.id}")
                    .minimumInteractiveComponentSize()
                    .tactileScale(checkInteraction, pressedScale = 0.90f)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = checkInteraction,
                        indication = ripple(color = catAccent.copy(alpha = 0.22f)),
                        role = Role.Checkbox,
                        onClick = onToggleCompleted,
                    ),
            ) {
                Icon(
                    imageVector = if (block.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = "Toggle ${block.title} completed",
                    tint = if (block.isCompleted) FeedbackCorrect else catAccent,
                    modifier = Modifier.size(22.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Top metadata: time range + category pill + AI approval status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = block.formattedTimeRange,
                            style = MonoMetricMedium,
                            color = tokens.inkPrimary,
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = catAccent.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, catAccent.copy(alpha = 0.32f)),
                        ) {
                            Text(
                                text = block.category.badgeCode,
                                style = MicroTagMono,
                                color = catAccent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }

                    // Approval status pill
                    val isAiApproved = block.approvalStatus == PlannerApprovalStatus.AI_APPROVED
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isAiApproved) FeedbackCorrectContainer else tokens.carbonSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isAiApproved) FeedbackCorrect.copy(alpha = 0.40f) else tokens.hairlineRest,
                        ),
                    ) {
                        Text(
                            text = block.approvalStatus.label.uppercase(),
                            style = MicroTagMono,
                            color = if (isAiApproved) FeedbackCorrect else tokens.inkMuted,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }

                // Block Title & Subtitle
                Text(
                    text = block.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (block.isCompleted) tokens.inkSecondary else tokens.inkPrimary,
                    textDecoration = if (block.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = block.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                // Footer row: critical protection tag + AI-Permission-Gated Request Edit action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (block.isCriticalPriority) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = AccentMood.AMBER.accent,
                                modifier = Modifier.size(12.dp),
                            )
                            Text(
                                text = "PROTECTED PRIORITY",
                                style = MicroTagMono,
                                color = AccentMood.AMBER.accent,
                            )
                        }
                    } else {
                        Text(
                            text = "${block.durationMinutes} min",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier
                            .testTag("planner_request_edit_${block.id}")
                            .tactileScale(editInteraction, pressedScale = 0.95f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = editInteraction,
                                indication = ripple(color = catAccent.copy(alpha = 0.18f)),
                                role = Role.Button,
                                onClick = onRequestEdit,
                            ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = catAccent,
                                modifier = Modifier.size(13.dp),
                            )
                            Text(
                                text = stringResource(R.string.planner_request_edit_cta),
                                style = MicroTagMono,
                                color = tokens.inkPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun categoryColor(category: PlannerBlockCategory): Color {
    return when (category) {
        PlannerBlockCategory.STUDY -> AccentMood.CYAN.accent
        PlannerBlockCategory.REVISION -> AccentMood.VIOLET.accent
        PlannerBlockCategory.QUIZ -> AccentMood.AMBER.accent
        PlannerBlockCategory.FLASHCARDS -> AccentMood.EMERALD.accent
        PlannerBlockCategory.BREAK_MEAL -> FeedbackCorrect
    }
}
