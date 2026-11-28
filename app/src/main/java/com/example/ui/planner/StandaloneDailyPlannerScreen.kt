package com.example.ui.planner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppRagFileId
import com.example.model.MotionIntensity
import com.example.model.PlannerAiSideMessage
import com.example.model.PlannerAppealOutcome
import com.example.model.PlannerHeatmapDay
import com.example.model.PlannerUserTask
import com.example.model.StandalonePlannerTask
import com.example.model.StandalonePlannerUiState
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.rememberScreenLayoutMetrics

/**
 * Phase 1.6.4 Materialized Daily Planner:
 * - Clean 2-option view toggle (Planner Only | Planner + AI) with ZERO ratio numbers.
 * - Right-side AI Planner panel: 28% width on 16:9 landscape, slide-over drawer with scrim on 9:16 portrait.
 *   Resets context to 0 turns on every open & hydrates solely from simulated RAG(1) + RAG(2).
 * - Visual 28-day Study & Task Completion Heatmap + clean Simulated RAG preview modal.
 * - AI-Only Schedule Blocks with Appeal system (no free overwriting).
 * - Tasks list with 3-edit limit triggering automatic AI Intervention & lock until AI re-authorizes.
 */
@Composable
fun StandaloneDailyPlannerScreen(
    state: StandalonePlannerUiState,
    motionIntensity: MotionIntensity,
    isWideLayout: Boolean,
    onOpenSettings: () -> Unit,
    onOpenRagInSettings: (AppRagFileId) -> Unit,
    onSetAiSidePanelOpen: (Boolean) -> Unit,
    onReopenZeroContextWindow: () -> Unit,
    onOpenSimulatedRagSheet: () -> Unit,
    onCloseSimulatedRagSheet: () -> Unit,
    onSimulateFirstTimeIngestion: () -> Unit,
    onRefreshRag2: () -> Unit,
    onToggleScheduleBlockCompleted: (String) -> Unit,
    onOpenAppealForBlock: (StandalonePlannerTask) -> Unit,
    onSelectAppealPresetReason: (String) -> Unit,
    onUpdateAppealCustomReason: (String) -> Unit,
    onSubmitAppeal: (PlannerAppealOutcome?) -> Unit,
    onApplyApprovedAppeal: () -> Unit,
    onDismissAppeal: () -> Unit,
    onToggleUserTaskCompleted: (String) -> Unit,
    onAddUserTask: (title: String, subject: String, durationMin: Int) -> Unit,
    onManualEditUserTask: (taskId: String, title: String, timeWindow: String, durationMin: Int) -> Unit,
    onRequestAiReauthorizeManualEdits: () -> Unit,
    onUpdateAiSideComposer: (String) -> Unit,
    onSendAiSidePrompt: (String) -> Unit,
    onDismissStatusNotice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var editingUserTask by remember { mutableStateOf<PlannerUserTask?>(null) }

    val landscapeAiWeight by animateFloatAsState(
        targetValue = if (isWideLayout && state.isAiSidePanelOpen) 0.28f else 0f,
        animationSpec = motionIntensity.springSpec(),
        label = "plannerAiSidePanelWeight",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("standalone_daily_planner_screen"),
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Primary Materialized Planner Stage
            LazyColumn(
                modifier = Modifier
                    .weight((1f - landscapeAiWeight).coerceAtLeast(0.70f))
                    .fillMaxHeight(),
                contentPadding = PaddingValues(
                    horizontal = metrics.horizontalPadding,
                    vertical = metrics.verticalPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // 1. Header with 2-Option View Toggle & Simulated RAG Badge
                item(key = "planner_header") {
                    PlannerMaterialHeader(
                        state = state,
                        isWideLayout = isWideLayout,
                        onSetAiSidePanelOpen = onSetAiSidePanelOpen,
                        onOpenSimulatedRagSheet = onOpenSimulatedRagSheet,
                        onOpenSettings = onOpenSettings,
                    )
                }

                // Optional status toast / notice
                if (state.statusNotice != null) {
                    item(key = "planner_status_notice") {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = tokens.themeAccent.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, tokens.themeAccent.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = tokens.themeAccent, modifier = Modifier.size(16.dp))
                                Text(
                                    text = state.statusNotice,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.inkPrimary,
                                    modifier = Modifier.weight(1f),
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .clickable(onClick = onDismissStatusNotice),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Dismiss", tint = tokens.inkMuted, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // 2. Materialized Progress & 28-Day Visual Heatmap Card
                item(key = "planner_heatmap_card") {
                    PlannerProgressAndHeatmapCard(
                        state = state,
                        isWideLayout = isWideLayout,
                        onOpenSimulatedRagSheet = onOpenSimulatedRagSheet,
                    )
                }

                // 3. AI-Scheduled Timeline Header (Appeal-Only Governance)
                item(key = "planner_ai_schedule_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Today's AI Schedule",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = "AI-governed peak blocks · Tap Appeal to request a change",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(9.dp),
                            color = tokens.carbonSurface,
                            border = BorderStroke(1.dp, tokens.hairlineRest),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(Icons.Rounded.Shield, contentDescription = null, tint = AccentMood.EMERALD.accent, modifier = Modifier.size(13.dp))
                                Text(
                                    text = "AI-ONLY",
                                    style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                                    color = AccentMood.EMERALD.accent,
                                    maxLines = 1,
                                    softWrap = false,
                                )
                            }
                        }
                    }
                }

                // Schedule Block Cards (stable keys)
                items(items = state.tasks, key = { it.id }) { block ->
                    AiScheduleBlockCard(
                        block = block,
                        onToggleDone = { onToggleScheduleBlockCompleted(block.id) },
                        onAppeal = { onOpenAppealForBlock(block) },
                    )
                }

                // 4. Tasks Section with 3-Edit Cap & Automatic AI Intervention
                item(key = "planner_tasks_header") {
                    Spacer(Modifier.height(4.dp))
                    PlannerUserTasksHeader(
                        state = state,
                        onAddTask = { showAddTaskDialog = true },
                        onRequestAiReauthorize = onRequestAiReauthorizeManualEdits,
                    )
                }

                // AI Intervention Alert Banner when 0/3 edits remain
                if (state.aiInterventionBanner != null) {
                    item(key = "planner_ai_intervention_banner") {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = AccentMood.AMBER.accent.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, AccentMood.AMBER.accent.copy(alpha = 0.55f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("planner_ai_intervention_banner"),
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = AccentMood.AMBER.accent, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "AI Intervention Triggered (0/${state.manualEditCap} Edits Left)",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = AccentMood.AMBER.accent,
                                    )
                                }
                                Text(
                                    text = state.aiInterventionBanner,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.inkPrimary,
                                )
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = tokens.primaryCta,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable(role = Role.Button, onClick = onRequestAiReauthorizeManualEdits)
                                        .testTag("planner_reauthorize_edits_button"),
                                ) {
                                    Text(
                                        text = "Ask AI to Re-Authorize Manual Edits",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = tokens.onPrimaryCta,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                items(items = state.userTasks, key = { it.id }) { userTask ->
                    PlannerUserTaskCard(
                        task = userTask,
                        isLockedByAi = state.isManualTaskEditingLocked,
                        onToggleDone = { onToggleUserTaskCompleted(userTask.id) },
                        onManualEdit = { editingUserTask = userTask },
                    )
                }
            }

            // 16:9 Landscape 28% Right-Side Zero-Context AI Planner Panel
            if (isWideLayout && landscapeAiWeight > 0.02f) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(tokens.hairlineRest),
                )
                PlannerZeroContextAiPanel(
                    state = state,
                    onResetWindow = onReopenZeroContextWindow,
                    onClose = { onSetAiSidePanelOpen(false) },
                    onUpdateComposer = onUpdateAiSideComposer,
                    onSendPrompt = onSendAiSidePrompt,
                    onReauthorizeEdits = onRequestAiReauthorizeManualEdits,
                    modifier = Modifier
                        .weight(landscapeAiWeight)
                        .fillMaxHeight()
                        .testTag("planner_landscape_ai_side_panel"),
                )
            }
        }

        // 9:16 Portrait Slide-Over Right Drawer with Scrim Backdrop
        if (!isWideLayout) {
            AnimatedVisibility(
                visible = state.isAiSidePanelOpen,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.52f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSetAiSidePanelOpen(false) },
                        ),
                )
            }

            AnimatedVisibility(
                visible = state.isAiSidePanelOpen,
                enter = slideInHorizontally(animationSpec = motionIntensity.springSpec()) { it },
                exit = slideOutHorizontally(animationSpec = motionIntensity.springSpec()) { it },
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                PlannerZeroContextAiPanel(
                    state = state,
                    onResetWindow = onReopenZeroContextWindow,
                    onClose = { onSetAiSidePanelOpen(false) },
                    onUpdateComposer = onUpdateAiSideComposer,
                    onSendPrompt = onSendAiSidePrompt,
                    onReauthorizeEdits = onRequestAiReauthorizeManualEdits,
                    modifier = Modifier
                        .fillMaxWidth(0.84f)
                        .fillMaxHeight()
                        .testTag("planner_portrait_ai_drawer"),
                )
            }
        }
    }

    // Appeal Modal
    state.activeAppeal?.let { appeal ->
        PlannerAppealModal(
            appeal = appeal,
            onSelectPresetReason = onSelectAppealPresetReason,
            onUpdateCustomReason = onUpdateAppealCustomReason,
            onSubmitAppeal = onSubmitAppeal,
            onApplyApprovedUpdate = onApplyApprovedAppeal,
            onDismiss = onDismissAppeal,
        )
    }

    // Simulated RAG(1) & RAG(2) Clean Inspection Sheet
    if (state.showSimulatedRagSheet) {
        SimulatedPlannerRagSheetDialog(
            rag1 = state.rag1,
            rag2 = state.rag2,
            onSimulateFirstIngestion = onSimulateFirstTimeIngestion,
            onRefreshRag2 = onRefreshRag2,
            onOpenFullRagInSettings = onOpenRagInSettings,
            onDismiss = onCloseSimulatedRagSheet,
        )
    }

    // Add / Manual Edit User Task Dialog
    if (showAddTaskDialog || editingUserTask != null) {
        PlannerTaskEditorDialog(
            editingTask = editingUserTask,
            editsRemaining = state.manualEditsRemaining,
            editCap = state.manualEditCap,
            onSaveAdd = { title, subject, dur ->
                onAddUserTask(title, subject, dur)
                showAddTaskDialog = false
            },
            onSaveManualEdit = { id, title, window, dur ->
                onManualEditUserTask(id, title, window, dur)
                editingUserTask = null
            },
            onDismiss = {
                showAddTaskDialog = false
                editingUserTask = null
            },
        )
    }
}

@Composable
private fun PlannerMaterialHeader(
    state: StandalonePlannerUiState,
    isWideLayout: Boolean,
    onSetAiSidePanelOpen: (Boolean) -> Unit,
    onOpenSimulatedRagSheet: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val tokens = GhickinTokens.colors

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Planner",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Peak window ${state.rag2.peakFocusWindow}",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Subtle Simulated RAG Preview Pill -> Clean Inspect Sheet
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(1.dp, AccentMood.AMBER.accent.copy(alpha = 0.45f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .minimumInteractiveComponentSize()
                    .clickable(role = Role.Button, onClick = onOpenSimulatedRagSheet)
                    .testTag("planner_simulated_rag_badge"),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Science,
                        contentDescription = "Inspect Simulated RAG data",
                        tint = AccentMood.AMBER.accent,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = if (isWideLayout) "Simulated RAG" else "Sim RAG",
                        style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                        color = AccentMood.AMBER.accent,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }

            if (!isWideLayout) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onOpenSettings),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = tokens.inkSecondary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Clean 2-Option View Mode Toggle (Zero Ratio Numbers!)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("planner_view_mode_toggle"),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val plannerOnlySelected = !state.isAiSidePanelOpen
                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = if (plannerOnlySelected) tokens.primaryCta else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .clickable(role = Role.Tab) { onSetAiSidePanelOpen(false) }
                        .testTag("planner_view_only_button"),
                ) {
                    Text(
                        text = "Planner Only",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (plannerOnlySelected) tokens.onPrimaryCta else tokens.inkSecondary,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier
                            .padding(vertical = 9.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = if (!plannerOnlySelected) tokens.primaryCta else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .clickable(role = Role.Tab) { onSetAiSidePanelOpen(true) }
                        .testTag("planner_view_ai_panel_button"),
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = if (!plannerOnlySelected) tokens.onPrimaryCta else tokens.themeAccent,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Planner + AI Panel",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (!plannerOnlySelected) tokens.onPrimaryCta else tokens.inkSecondary,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlannerProgressAndHeatmapCard(
    state: StandalonePlannerUiState,
    isWideLayout: Boolean,
    onOpenSimulatedRagSheet: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    var selectedDay by remember(state.heatmapDays) {
        mutableStateOf(state.heatmapDays.lastOrNull())
    }
    val pct = state.liveTaskCompletionPercent

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("planner_heatmap_card"),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CONSISTENCY & DAILY COMPLETION",
                        style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkMuted,
                    )
                    Text(
                        text = "$pct% of today's plan & tasks completed",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkPrimary,
                    )
                }
                Text(
                    text = "${state.tasks.count { it.isCompleted } + state.userTasks.count { it.isCompleted }}/${state.tasks.size + state.userTasks.size}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = AccentMood.EMERALD.accent,
                )
            }

            AnimatedLinearProgressBar(
                progressPercent = pct,
                accentColor = AccentMood.EMERALD.accent,
                height = 7.dp,
            )

            // 28-Day Visual Heatmap Grid (4 rows x 7 columns)
            val rows = remember(state.heatmapDays) { state.heatmapDays.chunked(7) }
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                rows.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        week.forEach { day ->
                            val isSel = selectedDay?.dayIndex == day.dayIndex
                            val cellColor = when (day.intensityLevel) {
                                4 -> AccentMood.EMERALD.accent
                                3 -> AccentMood.EMERALD.accent.copy(alpha = 0.68f)
                                2 -> AccentMood.EMERALD.accent.copy(alpha = 0.40f)
                                1 -> AccentMood.EMERALD.accent.copy(alpha = 0.20f)
                                else -> tokens.trackSubtle
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(if (isWideLayout) 22.dp else 20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(cellColor)
                                    .then(
                                        if (isSel) {
                                            Modifier.background(Color.Transparent)
                                        } else Modifier,
                                    )
                                    .clickable { selectedDay = day },
                            ) {
                                if (isSel) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Transparent)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(tokens.inkPrimary.copy(alpha = 0.22f)),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Selected Day Readout + Simulated Telemetry Footnote
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                selectedDay?.let { day ->
                    Text(
                        text = "${day.shortDate} · ${day.studyHours}h study · ${day.taskCompletionPercent}% tasks",
                        style = MicroTagMono,
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
                Text(
                    text = "Inspect RAG →",
                    style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                    color = tokens.themeAccent,
                    modifier = Modifier.clickable(onClick = onOpenSimulatedRagSheet),
                )
            }
        }
    }
}

@Composable
private fun AiScheduleBlockCard(
    block: StandalonePlannerTask,
    onToggleDone: () -> Unit,
    onAppeal: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val accent = block.accentMood.accent

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(
            1.dp,
            if (block.isCompleted) AccentMood.EMERALD.accent.copy(alpha = 0.35f) else tokens.hairlineRest,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("planner_ai_block_${block.id}"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Checkbox, onClick = onToggleDone)
                    .testTag("planner_block_check_${block.id}"),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (block.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = "Mark block complete",
                    tint = if (block.isCompleted) AccentMood.EMERALD.accent else accent,
                    modifier = Modifier.size(22.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = block.timeWindow,
                        style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                        color = accent,
                        maxLines = 1,
                        softWrap = false,
                    )
                    Text(
                        text = "· ${block.durationMinutes}m · ${block.subjectName}",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = block.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (block.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    ),
                    color = if (block.isCompleted) tokens.inkMuted else tokens.inkPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = block.aiStatusNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Appeal CTA (No free overwriting of AI schedule)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = tokens.obsidianSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .minimumInteractiveComponentSize()
                    .tactileScale()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = tokens.themeAccent),
                        role = Role.Button,
                        onClick = onAppeal,
                    )
                    .testTag("planner_appeal_btn_${block.id}"),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Gavel,
                        contentDescription = "Appeal block",
                        tint = tokens.themeAccent,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = "Appeal",
                        style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlannerUserTasksHeader(
    state: StandalonePlannerUiState,
    onAddTask: () -> Unit,
    onRequestAiReauthorize: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val remaining = state.manualEditsRemaining
    val cap = state.manualEditCap
    val badgeColor = when {
        remaining == 0 -> AccentMood.AMBER.accent
        remaining == 1 -> AccentMood.AMBER.accent
        else -> AccentMood.EMERALD.accent
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Tasks & AI Slotting",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = tokens.inkPrimary,
            )
            Text(
                text = "Manual edits left: $remaining/$cap",
                style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                color = badgeColor,
                modifier = Modifier.testTag("planner_manual_edits_counter"),
            )
        }

        if (state.isManualTaskEditingLocked) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AccentMood.AMBER.accent.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, AccentMood.AMBER.accent.copy(alpha = 0.5f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onRequestAiReauthorize),
            ) {
                Text(
                    text = "Unlock via AI",
                    style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                    color = AccentMood.AMBER.accent,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = tokens.primaryCta,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .minimumInteractiveComponentSize()
                .tactileScale()
                .clickable(role = Role.Button, onClick = onAddTask)
                .testTag("planner_add_user_task_button"),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Task", tint = tokens.onPrimaryCta, modifier = Modifier.size(16.dp))
                Text(
                    text = "Add Task",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = tokens.onPrimaryCta,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}

@Composable
private fun PlannerUserTaskCard(
    task: PlannerUserTask,
    isLockedByAi: Boolean,
    onToggleDone: () -> Unit,
    onManualEdit: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val accent = task.accentMood.accent

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("planner_user_task_${task.id}"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Checkbox, onClick = onToggleDone),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = "Toggle task completion",
                    tint = if (task.isCompleted) AccentMood.EMERALD.accent else accent,
                    modifier = Modifier.size(20.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    ),
                    color = if (task.isCompleted) tokens.inkMuted else tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${task.slottedTimeWindow} · ${task.durationMinutes}m · ${task.subjectName}",
                    style = MicroTagMono,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Surface(
                shape = RoundedCornerShape(9.dp),
                color = if (isLockedByAi) tokens.slateElevated.copy(alpha = 0.5f) else tokens.carbonSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .clickable(enabled = !isLockedByAi, onClick = onManualEdit)
                    .testTag("planner_edit_user_task_${task.id}"),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = if (isLockedByAi) Icons.Rounded.Lock else Icons.Rounded.Edit,
                        contentDescription = if (isLockedByAi) "Locked by AI Intervention" else "Edit task manually",
                        tint = if (isLockedByAi) tokens.inkMuted else tokens.inkPrimary,
                        modifier = Modifier.size(13.dp),
                    )
                    Text(
                        text = if (isLockedByAi) "Locked" else "Edit",
                        style = MicroTagMono,
                        color = if (isLockedByAi) tokens.inkMuted else tokens.inkPrimary,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlannerZeroContextAiPanel(
    state: StandalonePlannerUiState,
    onResetWindow: () -> Unit,
    onClose: () -> Unit,
    onUpdateComposer: (String) -> Unit,
    onSendPrompt: (String) -> Unit,
    onReauthorizeEdits: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    Surface(
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Drawer Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = tokens.themeAccent, modifier = Modifier.size(18.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Planner",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Win #${state.windowSessionNumber} · ${state.zeroContextTurnCount} turns · Zero carryover",
                        style = MicroTagMono.copy(fontSize = 9.sp),
                        color = tokens.inkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onResetWindow)
                        .testTag("planner_ai_reset_zero_context"),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Reset Zero-Context Window", tint = tokens.inkSecondary, modifier = Modifier.size(16.dp))
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClose)
                        .testTag("planner_ai_panel_close"),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close AI Panel", tint = tokens.inkMuted, modifier = Modifier.size(16.dp))
                }
            }

            // Quick Action Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSendPrompt("Re-balance my remaining blocks around evening peak") },
                ) {
                    Text(
                        text = "Re-balance",
                        style = MicroTagMono,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    )
                }
                if (state.isManualTaskEditingLocked) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AccentMood.EMERALD.accent.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, AccentMood.EMERALD.accent.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onReauthorizeEdits),
                    ) {
                        Text(
                            text = "Unlock Edits",
                            style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                            color = AccentMood.EMERALD.accent,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            // Zero-Context Message List (stable keys)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = state.aiSideMessages, key = { it.id }) { msg ->
                    AiSideBubble(msg = msg)
                }
            }

            // Compact Composer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                OutlinedTextField(
                    value = state.aiSideComposerText,
                    onValueChange = onUpdateComposer,
                    placeholder = { Text("Ask AI Planner...", style = MaterialTheme.typography.bodySmall) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tokens.themeAccent,
                        unfocusedBorderColor = tokens.hairlineRest,
                        focusedTextColor = tokens.inkPrimary,
                        unfocusedTextColor = tokens.inkPrimary,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("planner_ai_composer_input"),
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val txt = state.aiSideComposerText.trim()
                            if (txt.isNotEmpty()) onSendPrompt(txt)
                        }
                        .testTag("planner_ai_send_button"),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = "Send to AI Planner",
                            tint = tokens.onPrimaryCta,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AiSideBubble(msg: PlannerAiSideMessage) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (msg.isUser) tokens.chatUserBubble else tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = msg.tagLabel,
                style = MicroTagMono.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                color = if (msg.isUser) tokens.inkSecondary else tokens.themeAccent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = msg.text,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkPrimary,
            )
        }
    }
}
