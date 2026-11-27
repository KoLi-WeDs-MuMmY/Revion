package com.example.ui.planner

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.AppRagFileId
import com.example.model.PlannerAiDecisionOutcome
import com.example.model.PlannerRag1State
import com.example.model.PlannerRag2State
import com.example.model.StandalonePlannerTask
import com.example.model.StandalonePlannerUiState
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.ObsidianCard
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.rememberScreenLayoutMetrics

@Composable
fun StandaloneDailyPlannerScreen(
    state: StandalonePlannerUiState,
    isWideLayout: Boolean,
    onOpenSettings: () -> Unit,
    onOpenRagInSettings: (AppRagFileId) -> Unit,
    onToggleAiPlanner: (Boolean) -> Unit,
    onReopenZeroContextWindow: () -> Unit,
    onSimulateFirstTimeIngestion: () -> Unit,
    onRefreshRag2: () -> Unit,
    onRequestConfirmSchedule: () -> Unit,
    onToggleTaskCompleted: (String) -> Unit,
    onRequestAddTask: () -> Unit,
    onRequestEditTask: (StandalonePlannerTask) -> Unit,
    onRequestCancelTask: (StandalonePlannerTask) -> Unit,
    onUpdateGateTitle: (String) -> Unit,
    onUpdateGateTimeWindow: (String) -> Unit,
    onUpdateGateReason: (String) -> Unit,
    onSimulateGateOutcome: (PlannerAiDecisionOutcome) -> Unit,
    onApplyGateApproved: () -> Unit,
    onAcceptGateCounterProposal: () -> Unit,
    onDismissGate: () -> Unit,
    onDismissStatusNotice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("standalone_daily_planner_screen"),
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header + AI Toggle + Settings
            item(key = "planner_header") {
                PlannerTopHeader(
                    state = state,
                    isWideLayout = isWideLayout,
                    onToggleAiPlanner = onToggleAiPlanner,
                    onOpenSettings = onOpenSettings,
                )
            }

            // Status notice banner (if triggered)
            if (state.statusNotice != null) {
                item(key = "planner_notice") {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.themeAccent.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("planner_status_notice"),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = state.statusNotice,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkPrimary,
                                modifier = Modifier.weight(1f),
                            )
                            val closeInt = remember { MutableInteractionSource() }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        interactionSource = closeInt,
                                        indication = ripple(),
                                        role = Role.Button,
                                        onClick = onDismissStatusNotice,
                                    ),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Dismiss notice",
                                    tint = tokens.inkMuted,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                }
            }

            // Zero-Context Window Verification Bar + Simulate First-Time Ingestion
            item(key = "planner_zero_context_bar") {
                ZeroContextAndIngestionBar(
                    state = state,
                    isWideLayout = isWideLayout,
                    onReopenZeroContextWindow = onReopenZeroContextWindow,
                    onSimulateFirstTimeIngestion = onSimulateFirstTimeIngestion,
                )
            }

            // Dual Planner RAGs — RAG(1) & RAG(2)
            item(key = "planner_dual_rags") {
                if (isWideLayout) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        PlannerRag1Card(
                            rag1 = state.rag1,
                            onInspectInSettings = { onOpenRagInSettings(AppRagFileId.PLANNER_RAG_1) },
                            modifier = Modifier.weight(1f),
                        )
                        PlannerRag2Card(
                            rag2 = state.rag2,
                            liveTaskPct = state.liveTaskCompletionPercent,
                            onRefreshRag2 = onRefreshRag2,
                            onInspectInSettings = { onOpenRagInSettings(AppRagFileId.PLANNER_RAG_2) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        PlannerRag1Card(
                            rag1 = state.rag1,
                            onInspectInSettings = { onOpenRagInSettings(AppRagFileId.PLANNER_RAG_1) },
                        )
                        PlannerRag2Card(
                            rag2 = state.rag2,
                            liveTaskPct = state.liveTaskCompletionPercent,
                            onRefreshRag2 = onRefreshRag2,
                            onInspectInSettings = { onOpenRagInSettings(AppRagFileId.PLANNER_RAG_2) },
                        )
                    }
                }
            }

            // Schedule Confirmation Gate Header
            item(key = "planner_schedule_header") {
                ScheduleControlBar(
                    state = state,
                    isWideLayout = isWideLayout,
                    onRequestConfirmSchedule = onRequestConfirmSchedule,
                    onRequestAddTask = onRequestAddTask,
                )
            }

            // Daily Task Cards
            items(
                items = state.tasks,
                key = { it.id },
            ) { task ->
                PlannerTaskCard(
                    task = task,
                    isAiEnabled = state.isAiEnabled,
                    onToggleComplete = { onToggleTaskCompleted(task.id) },
                    onEdit = { onRequestEditTask(task) },
                    onCancel = { onRequestCancelTask(task) },
                )
            }
        }
    }

    // Interactive AI Approval Modal
    val gate = state.pendingGate
    if (gate != null) {
        PlannerAiGateModal(
            request = gate,
            rag1 = state.rag1,
            rag2 = state.rag2,
            onUpdateTitle = onUpdateGateTitle,
            onUpdateTimeWindow = onUpdateGateTimeWindow,
            onUpdateReason = onUpdateGateReason,
            onSimulateOutcome = onSimulateGateOutcome,
            onApplyApproved = onApplyGateApproved,
            onAcceptCounterProposal = onAcceptGateCounterProposal,
            onDismiss = onDismissGate,
        )
    }
}

@Composable
private fun PlannerTopHeader(
    state: StandalonePlannerUiState,
    isWideLayout: Boolean,
    onToggleAiPlanner: (Boolean) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val settingsInteraction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = stringResource(R.string.planner_standalone_title),
                style = MaterialTheme.typography.headlineMedium,
                color = tokens.inkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.planner_standalone_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // AI Planner Toggle Pill
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = tokens.obsidianSurface,
            border = BorderStroke(
                1.dp,
                if (state.isAiEnabled) tokens.themeAccent.copy(alpha = 0.45f) else tokens.hairlineRest,
            ),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = if (state.isAiEnabled) tokens.themeAccent else tokens.inkMuted,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = if (state.isAiEnabled) "AI ON" else "AI OFF",
                    style = MicroTagMono,
                    color = if (state.isAiEnabled) tokens.inkPrimary else tokens.inkMuted,
                )
                Switch(
                    checked = state.isAiEnabled,
                    onCheckedChange = onToggleAiPlanner,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = tokens.onPrimaryCta,
                        checkedTrackColor = tokens.themeAccent,
                    ),
                    modifier = Modifier.testTag("planner_ai_toggle_switch"),
                )
            }
        }

        if (!isWideLayout) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = tokens.obsidianSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .testTag("planner_settings_button")
                    .minimumInteractiveComponentSize()
                    .tactileScale(settingsInteraction, 0.94f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = settingsInteraction,
                        indication = ripple(),
                        role = Role.Button,
                        onClick = onOpenSettings,
                    ),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(44.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.settings_open_desc),
                        tint = tokens.inkSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ZeroContextAndIngestionBar(
    state: StandalonePlannerUiState,
    isWideLayout: Boolean,
    onReopenZeroContextWindow: () -> Unit,
    onSimulateFirstTimeIngestion: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val emerald = tokens.semanticSuccess
    val reopenInt = remember { MutableInteractionSource() }
    val ingestInt = remember { MutableInteractionSource() }

    ObsidianCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("planner_zero_context_card"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(14.dp),
    ) {
        if (isWideLayout) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(emerald),
                        )
                        Text(
                            text = "ZERO CONTEXT CARRYOVER · WINDOW #${state.windowSessionNumber} · ${state.zeroContextTurnCount} PRIOR TURNS",
                            style = MicroTagMono,
                            color = emerald,
                        )
                    }
                    Text(
                        text = "Every planner open starts with 0 conversational history and hydrates strictly from RAG(1) + RAG(2).",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                    )
                }
                PlannerMiniActionButton(
                    label = "Reopen (0 Context)",
                    icon = Icons.Rounded.Refresh,
                    accent = tokens.inkPrimary,
                    testTag = "planner_reopen_zero_context_btn",
                    interactionSource = reopenInt,
                    onClick = onReopenZeroContextWindow,
                )
                PlannerMiniActionButton(
                    label = "Simulate 1st Ingestion",
                    icon = Icons.Rounded.DataUsage,
                    accent = tokens.themeAccent,
                    testTag = "planner_simulate_ingestion_btn",
                    interactionSource = ingestInt,
                    onClick = onSimulateFirstTimeIngestion,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(emerald),
                    )
                    Text(
                        text = "ZERO CONTEXT · WINDOW #${state.windowSessionNumber} · 0 PRIOR TURNS",
                        style = MicroTagMono,
                        color = emerald,
                    )
                }
                Text(
                    text = "Fresh RAG(1) + RAG(2) load on every window open — completely isolated from AI Tutor.",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PlannerMiniActionButton(
                        label = "Reopen (0 Ctx)",
                        icon = Icons.Rounded.Refresh,
                        accent = tokens.inkPrimary,
                        testTag = "planner_reopen_zero_context_btn",
                        interactionSource = reopenInt,
                        onClick = onReopenZeroContextWindow,
                        modifier = Modifier.weight(1f),
                    )
                    PlannerMiniActionButton(
                        label = "1st Ingestion",
                        icon = Icons.Rounded.DataUsage,
                        accent = tokens.themeAccent,
                        testTag = "planner_simulate_ingestion_btn",
                        interactionSource = ingestInt,
                        onClick = onSimulateFirstTimeIngestion,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun PlannerRag1Card(
    rag1: PlannerRag1State,
    onInspectInSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val inspectInt = remember { MutableInteractionSource() }

    ObsidianCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("planner_rag1_card"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Storage,
                        contentDescription = null,
                        tint = tokens.themeAccent,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "PLANNER RAG (1) · TELEMETRY",
                        style = MicroTagMono,
                        color = tokens.themeAccent,
                    )
                }
                Text(
                    text = "${rag1.rawEstimatedTokens} → ${rag1.compactedEstimatedTokens} tok",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
            }

            // Multi-horizon horizon pills: 20d Individual | 1-2m Avg | 6m Avg
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HorizonMetricPill(
                    title = "20d INDIV",
                    value = "%.1fh".format(rag1.daily20Entries.map { it.studyHours }.average()),
                    sub = "Scr %.1fh".format(rag1.daily20Entries.map { it.screenTimeHours }.average()),
                    modifier = Modifier.weight(1f),
                )
                HorizonMetricPill(
                    title = "1–2m AVG",
                    value = "%.1fh".format(rag1.twoMonthAvgStudyHours),
                    sub = "Scr %.1fh".format(rag1.twoMonthAvgScreenHours),
                    modifier = Modifier.weight(1f),
                )
                HorizonMetricPill(
                    title = "6m AVG",
                    value = "%.1fh".format(rag1.sixMonthAvgStudyHours),
                    sub = "Scr %.1fh".format(rag1.sixMonthAvgScreenHours),
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                text = rag1.compactedSummaryText,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = rag1.lastOptimizedLabel,
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings RAG →",
                    style = MaterialTheme.typography.labelMedium,
                    color = tokens.themeAccent,
                    modifier = Modifier
                        .testTag("planner_rag1_inspect_settings")
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(
                            interactionSource = inspectInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = onInspectInSettings,
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun PlannerRag2Card(
    rag2: PlannerRag2State,
    liveTaskPct: Int,
    onRefreshRag2: () -> Unit,
    onInspectInSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val emerald = tokens.semanticSuccess
    val refreshInt = remember { MutableInteractionSource() }
    val inspectInt = remember { MutableInteractionSource() }

    ObsidianCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("planner_rag2_card"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = emerald,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "PLANNER RAG (2) · AI LIVE PROFILE",
                        style = MicroTagMono,
                        color = emerald,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                    modifier = Modifier
                        .testTag("planner_rag2_refresh_btn")
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = refreshInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = onRefreshRag2,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh RAG 2",
                            tint = tokens.inkSecondary,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = "Refresh",
                            style = MicroTagMono,
                            color = tokens.inkSecondary,
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Active Hours: ${rag2.activeHoursWindow}",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = "Peak Focus: ${rag2.peakFocusWindow}",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "TASK COMPLETION",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                    Text(
                        text = "$liveTaskPct%",
                        style = MicroTagMono,
                        color = emerald,
                    )
                }
                AnimatedLinearProgressBar(
                    progressPercent = liveTaskPct,
                    accentColor = emerald,
                    height = 5.dp,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = rag2.lastGeneratedLabel,
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings RAG →",
                    style = MaterialTheme.typography.labelMedium,
                    color = emerald,
                    modifier = Modifier
                        .testTag("planner_rag2_inspect_settings")
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(
                            interactionSource = inspectInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = onInspectInSettings,
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun HorizonMetricPill(
    title: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(tokens.carbonSurface)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(text = title, style = MicroTagMono, color = tokens.inkMuted, maxLines = 1)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = tokens.inkPrimary,
            maxLines = 1,
        )
        Text(text = sub, style = MicroTagMono, color = tokens.inkSecondary, maxLines = 1)
    }
}

@Composable
private fun ScheduleControlBar(
    state: StandalonePlannerUiState,
    isWideLayout: Boolean,
    onRequestConfirmSchedule: () -> Unit,
    onRequestAddTask: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val emerald = tokens.semanticSuccess
    val confirmInt = remember { MutableInteractionSource() }
    val addInt = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(
            1.dp,
            if (state.isScheduleConfirmed) emerald.copy(alpha = 0.45f) else tokens.hairlineRest,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = if (state.isScheduleConfirmed) Icons.Rounded.VerifiedUser else Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = if (state.isScheduleConfirmed) emerald else tokens.themeAccent,
                        modifier = Modifier.size(18.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            text = when {
                                state.isScheduleConfirmed -> "Today's Schedule Confirmed"
                                state.isAiEnabled -> "Draft Schedule · AI Approval Required"
                                else -> "Manual Override Mode (AI Planner Off)"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = if (state.isAiEnabled) {
                                "Confirming, editing, or cancelling any block routes through the interactive AI Gate."
                            } else {
                                "Direct manual edits enabled without AI review."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (state.isScheduleConfirmed) tokens.carbonSurface else tokens.primaryCta,
                    border = if (state.isScheduleConfirmed) BorderStroke(1.dp, emerald.copy(alpha = 0.45f)) else null,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("planner_confirm_schedule_cta")
                        .minimumInteractiveComponentSize()
                        .tactileScale(confirmInt, 0.97f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = confirmInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = onRequestConfirmSchedule,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = if (state.isScheduleConfirmed) emerald else tokens.onPrimaryCta,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.isScheduleConfirmed) {
                                if (isWideLayout) "Re-Verify Confirmed Day" else "Confirmed"
                            } else {
                                if (state.isAiEnabled) "Ask AI to Confirm Day" else "Confirm Day"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = if (state.isScheduleConfirmed) emerald else tokens.onPrimaryCta,
                            maxLines = 1,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .testTag("planner_add_task_cta")
                        .minimumInteractiveComponentSize()
                        .tactileScale(addInt, 0.96f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = addInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = onRequestAddTask,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add task",
                            tint = tokens.inkPrimary,
                            modifier = Modifier.size(17.dp),
                        )
                        Text(
                            text = "Add Task",
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.inkPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlannerTaskCard(
    task: StandalonePlannerTask,
    isAiEnabled: Boolean,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val emerald = tokens.semanticSuccess
    val accent = task.accentMood.accent
    val checkInt = remember { MutableInteractionSource() }
    val editInt = remember { MutableInteractionSource() }
    val delInt = remember { MutableInteractionSource() }

    ObsidianCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("planner_task_${task.id}"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Completion checkbox
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .testTag("planner_task_toggle_${task.id}")
                    .minimumInteractiveComponentSize()
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = checkInt,
                        indication = ripple(),
                        role = Role.Checkbox,
                        onClick = onToggleComplete,
                    ),
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = if (task.isCompleted) "Completed" else "Mark completed",
                    tint = if (task.isCompleted) emerald else tokens.inkMuted,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(accent),
                    )
                    Text(
                        text = "${task.subjectName.uppercase()} · ${task.timeWindow} · ${task.durationMinutes}m",
                        style = MicroTagMono,
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (task.isCompleted) tokens.inkMuted else tokens.inkPrimary,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                )

                Text(
                    text = task.aiStatusNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                        modifier = Modifier
                            .testTag("planner_task_edit_${task.id}")
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                interactionSource = editInt,
                                indication = ripple(),
                                role = Role.Button,
                                onClick = onEdit,
                            ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Edit task",
                                tint = tokens.inkSecondary,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = if (isAiEnabled) "AI Edit" else "Edit",
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.inkPrimary,
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                        modifier = Modifier
                            .testTag("planner_task_cancel_${task.id}")
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                interactionSource = delInt,
                                indication = ripple(),
                                role = Role.Button,
                                onClick = onCancel,
                            ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = "Cancel task",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = if (isAiEnabled) "AI Cancel" else "Cancel",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlannerMiniActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    testTag: String,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val shape = RoundedCornerShape(11.dp)

    Surface(
        shape = shape,
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier
            .testTag(testTag)
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, 0.96f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accent.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accent,
                modifier = Modifier.size(15.dp),
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = tokens.inkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
