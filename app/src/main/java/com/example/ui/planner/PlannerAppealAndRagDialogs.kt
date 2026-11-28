package com.example.ui.planner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppRagFileId
import com.example.model.PlannerAppealOutcome
import com.example.model.PlannerAppealRequest
import com.example.model.PlannerRag1State
import com.example.model.PlannerRag2State
import com.example.model.PlannerUserTask
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono

/**
 * Appeal Modal for AI-Only Daily Planner blocks:
 * Users cannot directly overwrite AI-scheduled blocks; they submit an Appeal
 * and receive an interactive Approved & Updated or Declined (with reason) verdict.
 */
@Composable
fun PlannerAppealModal(
    appeal: PlannerAppealRequest,
    onSelectPresetReason: (String) -> Unit,
    onUpdateCustomReason: (String) -> Unit,
    onSubmitAppeal: (forceOutcome: PlannerAppealOutcome?) -> Unit,
    onApplyApprovedUpdate: () -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val presets = remember {
        listOf(
            "Need lighter load in this slot",
            "Shift after coaching / school class",
            "Swap with evening peak focus window",
            "Feeling tired — request skip",
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 480.dp)
                .testTag("planner_appeal_modal"),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(tokens.themeAccent.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Gavel,
                            contentDescription = null,
                            tint = tokens.themeAccent,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Appeal AI-Scheduled Block",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = "${appeal.blockTitle} · ${appeal.blockTimeWindow}",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.inkMuted)
                    }
                }

                if (appeal.outcome == PlannerAppealOutcome.PENDING) {
                    Text(
                        text = "SELECT APPEAL REASON",
                        style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkMuted,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        presets.forEach { reason ->
                            val selected = appeal.selectedPresetReason == reason
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) tokens.themeAccent.copy(alpha = 0.14f) else tokens.obsidianSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) tokens.themeAccent else tokens.hairlineSubtle,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSelectPresetReason(reason) },
                            ) {
                                Text(
                                    text = reason,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    ),
                                    color = if (selected) tokens.inkPrimary else tokens.inkSecondary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = appeal.customReason,
                        onValueChange = onUpdateCustomReason,
                        label = { Text("Optional context for AI Planner") },
                        placeholder = { Text("e.g., Lab practical runs until 17:00 today") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = tokens.themeAccent,
                            unfocusedBorderColor = tokens.hairlineRest,
                            focusedTextColor = tokens.inkPrimary,
                            unfocusedTextColor = tokens.inkPrimary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("planner_appeal_custom_input"),
                    )

                    // Primary submit + quick test buttons for both verdict paths
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.primaryCta,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .minimumInteractiveComponentSize()
                            .clickable(role = Role.Button) { onSubmitAppeal(null) }
                            .testTag("planner_appeal_submit_button"),
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = tokens.onPrimaryCta,
                                modifier = Modifier.size(17.dp),
                            )
                            Spacer(Modifier.width(7.dp))
                            Text(
                                text = "Submit Appeal to AI Planner",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = tokens.onPrimaryCta,
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AccentMood.EMERALD.accent.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, AccentMood.EMERALD.accent.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSubmitAppeal(PlannerAppealOutcome.APPROVED_UPDATED) }
                                .testTag("planner_appeal_test_approve"),
                        ) {
                            Text(
                                text = "Preview: Approve",
                                style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                                color = AccentMood.EMERALD.accent,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AccentMood.AMBER.accent.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, AccentMood.AMBER.accent.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSubmitAppeal(PlannerAppealOutcome.DECLINED) }
                                .testTag("planner_appeal_test_decline"),
                        ) {
                            Text(
                                text = "Preview: Decline",
                                style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                                color = AccentMood.AMBER.accent,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                    }
                } else {
                    val approved = appeal.outcome == PlannerAppealOutcome.APPROVED_UPDATED
                    val verdictColor = if (approved) AccentMood.EMERALD.accent else AccentMood.AMBER.accent

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = verdictColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, verdictColor.copy(alpha = 0.55f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = if (approved) Icons.Rounded.CheckCircle else Icons.Rounded.Block,
                                    contentDescription = null,
                                    tint = verdictColor,
                                    modifier = Modifier.size(20.dp),
                                )
                                Text(
                                    text = appeal.aiVerdictTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = verdictColor,
                                )
                            }
                            Text(
                                text = appeal.aiVerdictExplanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkPrimary,
                            )
                            if (approved && appeal.updatedTimeWindow != null) {
                                Surface(
                                    shape = RoundedCornerShape(9.dp),
                                    color = tokens.obsidianSurface,
                                    border = BorderStroke(1.dp, verdictColor.copy(alpha = 0.4f)),
                                ) {
                                    Text(
                                        text = "New AI Slot: ${appeal.updatedTimeWindow} (${appeal.updatedDurationMinutes ?: appeal.blockDurationMinutes}m)",
                                        style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                                        color = verdictColor,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        if (approved) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = tokens.primaryCta,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .minimumInteractiveComponentSize()
                                    .clickable(role = Role.Button, onClick = onApplyApprovedUpdate)
                                    .testTag("planner_appeal_apply_button"),
                            ) {
                                Text(
                                    text = "Apply AI-Updated Schedule",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = tokens.onPrimaryCta,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 12.dp),
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = tokens.obsidianSurface,
                                border = BorderStroke(1.dp, tokens.hairlineRest),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .minimumInteractiveComponentSize()
                                    .clickable(role = Role.Button, onClick = onDismiss)
                                    .testTag("planner_appeal_keep_plan_button"),
                            ) {
                                Text(
                                    text = "Keep AI-Scheduled Block",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = tokens.inkPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 12.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Clean modal for inspecting Simulated RAG(1) (20d/2m/6m) & RAG(2) (active/peak hours)
 * without cluttering the main Materialized Daily Planner stage.
 */
@Composable
fun SimulatedPlannerRagSheetDialog(
    rag1: PlannerRag1State,
    rag2: PlannerRag2State,
    onSimulateFirstIngestion: () -> Unit,
    onRefreshRag2: () -> Unit,
    onOpenFullRagInSettings: (AppRagFileId) -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 520.dp)
                .testTag("planner_simulated_rag_sheet"),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Science,
                        contentDescription = null,
                        tint = AccentMood.AMBER.accent,
                        modifier = Modifier.size(20.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Simulated RAG(1) & RAG(2) Preview",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = "Real Digital Wellbeing (UsageStatsManager) & live RAG arrive next phase",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.inkMuted)
                    }
                }

                // RAG(1) Summary Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("RAG(1) · MULTI-HORIZON (SIMULATED)", style = MicroTagMono.copy(fontWeight = FontWeight.Bold), color = AccentMood.CYAN.accent)
                            Text("${rag1.rawEstimatedTokens}→${rag1.compactedEstimatedTokens} tok", style = MicroTagMono, color = tokens.inkMuted)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            RagHorizonMiniStat("20-Day", "${rag1.oneMonthAvgStudyHours}h", "${rag1.oneMonthAvgScreenHours}h scr", Modifier.weight(1f))
                            RagHorizonMiniStat("1–2 Mo", "${rag1.twoMonthAvgStudyHours}h", "${rag1.twoMonthAvgScreenHours}h scr", Modifier.weight(1f))
                            RagHorizonMiniStat("6 Mo", "${rag1.sixMonthAvgStudyHours}h", "${rag1.sixMonthAvgScreenHours}h scr", Modifier.weight(1f))
                        }
                        Text(
                            text = rag1.compactedSummaryText,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(9.dp),
                                color = tokens.slateElevated,
                                border = BorderStroke(1.dp, tokens.hairlineRest),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable(onClick = onSimulateFirstIngestion),
                            ) {
                                Text(
                                    text = "Simulate 1st Ingestion",
                                    style = MicroTagMono.copy(fontWeight = FontWeight.SemiBold),
                                    color = tokens.inkPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(9.dp),
                                color = tokens.slateElevated,
                                border = BorderStroke(1.dp, tokens.hairlineRest),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable {
                                        onDismiss()
                                        onOpenFullRagInSettings(AppRagFileId.PLANNER_RAG_1)
                                    },
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(Icons.Rounded.Storage, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(12.dp))
                                    Text("Open in Settings", style = MicroTagMono, color = tokens.inkSecondary)
                                }
                            }
                        }
                    }
                }

                // RAG(2) Summary Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("RAG(2) · PEAK & COMPLETION (SIMULATED)", style = MicroTagMono.copy(fontWeight = FontWeight.Bold), color = AccentMood.EMERALD.accent)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = tokens.slateElevated,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = onRefreshRag2),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Icon(Icons.Rounded.Refresh, contentDescription = "Refresh RAG 2", tint = tokens.inkPrimary, modifier = Modifier.size(12.dp))
                                    Text("Refresh", style = MicroTagMono, color = tokens.inkPrimary)
                                }
                            }
                        }
                        Text("Active Hours: ${rag2.activeHoursWindow}", style = MaterialTheme.typography.labelMedium, color = tokens.inkPrimary)
                        Text("Peak Windows: ${rag2.peakFocusWindow}", style = MaterialTheme.typography.labelMedium, color = AccentMood.EMERALD.accent)
                        Text("Live Tasks Completed: ${rag2.taskCompletedPercent}%", style = MicroTagMono, color = tokens.inkSecondary)
                        Text(rag2.focusEfficiencyNote, style = MaterialTheme.typography.bodySmall, color = tokens.inkSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun RagHorizonMiniStat(
    label: String,
    study: String,
    screen: String,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(label, style = MicroTagMono, color = tokens.inkMuted)
            Text(
                text = study,
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = tokens.inkPrimary,
            )
            Text(screen, style = MicroTagMono, color = tokens.inkSecondary)
        }
    }
}

/**
 * Dialog for Adding a new User Task (AI auto-slots it) or Manually Editing an existing User Task
 * (counts toward the 3-edit cap before automatic AI Intervention).
 */
@Composable
fun PlannerTaskEditorDialog(
    editingTask: PlannerUserTask?,
    editsRemaining: Int,
    editCap: Int,
    onSaveAdd: (title: String, subject: String, durationMin: Int) -> Unit,
    onSaveManualEdit: (taskId: String, title: String, timeWindow: String, durationMin: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    var title by remember(editingTask) { mutableStateOf(editingTask?.title ?: "") }
    var subject by remember(editingTask) { mutableStateOf(editingTask?.subjectName ?: "Physics") }
    var timeWindow by remember(editingTask) { mutableStateOf(editingTask?.slottedTimeWindow ?: "17:00 – 17:45") }
    var duration by remember(editingTask) { mutableIntStateOf(editingTask?.durationMinutes ?: 30) }
    val subjects = remember { listOf("Physics", "Chemistry", "Maths", "Biology") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
                .testTag("planner_task_editor_dialog"),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (editingTask == null) "Add Task (AI Will Auto-Slot)" else "Manual Task Edit",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkPrimary,
                        )
                        if (editingTask != null) {
                            Text(
                                text = "Uses 1 manual edit · Remaining: $editsRemaining/$editCap (0 triggers AI Intervention)",
                                style = MicroTagMono,
                                color = if (editsRemaining <= 1) AccentMood.AMBER.accent else tokens.inkSecondary,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.inkMuted)
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    placeholder = { Text("e.g., Solve 12 Optics ray-diagram PYQs") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tokens.themeAccent,
                        unfocusedBorderColor = tokens.hairlineRest,
                        focusedTextColor = tokens.inkPrimary,
                        unfocusedTextColor = tokens.inkPrimary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("planner_task_title_input"),
                )

                if (editingTask == null) {
                    Text("SUBJECT", style = MicroTagMono, color = tokens.inkMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        subjects.forEach { subj ->
                            val sel = subj == subject
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (sel) tokens.primaryCta else tokens.obsidianSurface,
                                border = BorderStroke(1.dp, if (sel) tokens.primaryCta else tokens.hairlineSubtle),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { subject = subj },
                            ) {
                                Text(
                                    text = subj,
                                    style = MicroTagMono,
                                    color = if (sel) tokens.onPrimaryCta else tokens.inkSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = timeWindow,
                        onValueChange = { timeWindow = it },
                        label = { Text("Preferred Time Slot") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = tokens.themeAccent,
                            unfocusedBorderColor = tokens.hairlineRest,
                            focusedTextColor = tokens.inkPrimary,
                            unfocusedTextColor = tokens.inkPrimary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("planner_task_window_input"),
                    )
                }

                Text("DURATION: ${duration}m", style = MicroTagMono, color = tokens.inkMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(20, 30, 45, 60).forEach { mins ->
                        val sel = mins == duration
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (sel) tokens.primaryCta else tokens.obsidianSurface,
                            border = BorderStroke(1.dp, if (sel) tokens.primaryCta else tokens.hairlineSubtle),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { duration = mins },
                        ) {
                            Text(
                                text = "${mins}m",
                                style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                                color = if (sel) tokens.onPrimaryCta else tokens.inkPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .minimumInteractiveComponentSize()
                        .clickable(role = Role.Button) {
                            val cleanTitle = title.trim().ifBlank { "Focused Practice Task" }
                            if (editingTask == null) {
                                onSaveAdd(cleanTitle, subject, duration)
                            } else {
                                onSaveManualEdit(editingTask.id, cleanTitle, timeWindow.trim().ifBlank { editingTask.slottedTimeWindow }, duration)
                            }
                        }
                        .testTag("planner_task_save_button"),
                ) {
                    Text(
                        text = if (editingTask == null) "Add & Let AI Slot Task" else "Save Manual Edit",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = tokens.onPrimaryCta,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                    )
                }
            }
        }
    }
}
