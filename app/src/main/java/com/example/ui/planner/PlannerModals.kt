package com.example.ui.planner

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GppBad
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.AiPermissionRequest
import com.example.model.PermissionRequestAction
import com.example.model.PermissionVerdictPhase
import com.example.model.PlannerBlockCategory
import com.example.model.TutorPlannerUiState
import com.example.model.formatClockMinutes
import com.example.ui.components.KineticFilterChip
import com.example.ui.theme.AccentMood
import com.example.ui.theme.FeedbackCorrect
import com.example.ui.theme.FeedbackCorrectContainer
import com.example.ui.theme.FeedbackIncorrect
import com.example.ui.theme.FeedbackIncorrectContainer
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.InkOnAccentDark
import com.example.ui.theme.KineticDisplayLarge
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoMetricMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScheduleSettingsSheet(
    state: TutorPlannerUiState,
    onDismiss: () -> Unit,
    onToggleAdaptiveSync: (Boolean) -> Unit,
    onStepManualWake: (Int) -> Unit,
    onStepManualSleep: (Int) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = tokens.obsidianSurface,
        contentColor = tokens.inkPrimary,
        scrimColor = Color.Black.copy(alpha = 0.72f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 8.dp)
                .testTag("planner_schedule_settings_sheet"),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(R.string.planner_settings_title),
                style = MaterialTheme.typography.headlineSmall,
                color = tokens.inkPrimary,
            )
            Text(
                text = stringResource(R.string.planner_settings_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
            )

            // Adaptive Device Unlock / Screen-Off Mock Sync Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(
                    1.dp,
                    if (state.isAdaptiveSyncEnabled) FeedbackCorrect.copy(alpha = 0.42f) else tokens.hairlineRest,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.planner_adaptive_toggle_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = stringResource(R.string.planner_adaptive_toggle_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.isAdaptiveSyncEnabled) FeedbackCorrect else tokens.inkMuted,
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = state.isAdaptiveSyncEnabled,
                        onCheckedChange = onToggleAdaptiveSync,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = InkOnAccentDark,
                            checkedTrackColor = FeedbackCorrect,
                            uncheckedThumbColor = tokens.inkSecondary,
                            uncheckedTrackColor = tokens.slateElevated,
                        ),
                        modifier = Modifier.testTag("planner_adaptive_sync_switch"),
                    )
                }
            }

            // Manual Baseline Anchor Steppers (First-Login Onboarding Defaults)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TimeStepperCard(
                    title = "MANUAL WAKE BASELINE",
                    clockText = formatClockMinutes(state.manualWakeMinutes),
                    accent = AccentMood.AMBER.accent,
                    onDecrement = { onStepManualWake(-15) },
                    onIncrement = { onStepManualWake(15) },
                    testTagPrefix = "wake_stepper",
                    modifier = Modifier.weight(1f),
                )
                TimeStepperCard(
                    title = "MANUAL SLEEP BASELINE",
                    clockText = formatClockMinutes(state.manualSleepMinutes),
                    accent = AccentMood.VIOLET.accent,
                    onDecrement = { onStepManualSleep(-15) },
                    onIncrement = { onStepManualSleep(15) },
                    testTagPrefix = "sleep_stepper",
                    modifier = Modifier.weight(1f),
                )
            }

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    contentColor = InkOnAccentDark,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("planner_settings_done_button"),
            ) {
                Text("Done", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun TimeStepperCard(
    title: String,
    clockText: String,
    accent: Color,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    testTagPrefix: String,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MicroTagMono,
                color = accent,
            )
            Text(
                text = clockText,
                style = KineticDisplayLarge,
                color = tokens.inkPrimary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier.testTag("${testTagPrefix}_minus"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = "Subtract 15 minutes",
                        tint = tokens.inkPrimary,
                    )
                }
                Text(
                    text = "±15m",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier.testTag("${testTagPrefix}_plus"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add 15 minutes",
                        tint = tokens.inkPrimary,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AiPermissionGateSheet(
    request: AiPermissionRequest,
    onDismiss: () -> Unit,
    onUpdateDraft: (
        action: PermissionRequestAction?,
        title: String?,
        category: PlannerBlockCategory?,
        startMinutes: Int?,
        durationMinutes: Int?,
        reasonText: String?,
    ) -> Unit,
    onSubmitToAi: () -> Unit,
    onApplyApproved: () -> Unit,
    onAcceptCounterProposal: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = tokens.obsidianSurface,
        contentColor = tokens.inkPrimary,
        scrimColor = Color.Black.copy(alpha = 0.74f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 8.dp)
                .testTag("planner_permission_gate_sheet"),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    text = stringResource(R.string.planner_gate_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = tokens.inkPrimary,
                )
            }

            // Locked Guardrail Explanation Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(1.dp, AccentMood.AMBER.accent.copy(alpha = 0.30f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = AccentMood.AMBER.accent,
                        modifier = Modifier
                            .padding(top = 1.dp)
                            .size(15.dp),
                    )
                    Text(
                        text = stringResource(R.string.planner_gate_locked_notice),
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                    )
                }
            }

            // Action Switcher when editing an existing block (Edit vs Remove/Skip)
            if (request.targetBlockId != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        PermissionRequestAction.EDIT_BLOCK,
                        PermissionRequestAction.DELETE_BLOCK,
                    ).forEach { act ->
                        KineticFilterChip(
                            label = act.label,
                            selected = request.action == act,
                            onClick = { onUpdateDraft(act, null, null, null, null, null) },
                            accentColor = if (act == PermissionRequestAction.DELETE_BLOCK) FeedbackIncorrect else primary,
                            modifier = Modifier.testTag("gate_action_${act.name.lowercase()}"),
                        )
                    }
                }
            }

            // Editable fields (shown when not deleting)
            if (request.action != PermissionRequestAction.DELETE_BLOCK) {
                OutlinedTextField(
                    value = request.proposedTitle,
                    onValueChange = { onUpdateDraft(null, it, null, null, null, null) },
                    label = { Text("Block Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primary,
                        unfocusedBorderColor = tokens.hairlineRest,
                        focusedContainerColor = tokens.carbonSurface,
                        unfocusedContainerColor = tokens.carbonSurface,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gate_title_input"),
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PlannerBlockCategory.entries.forEach { cat ->
                        KineticFilterChip(
                            label = cat.label,
                            selected = request.proposedCategory == cat,
                            onClick = { onUpdateDraft(null, null, cat, null, null, null) },
                            accentColor = primary,
                            modifier = Modifier.testTag("gate_category_${cat.name.lowercase()}"),
                        )
                    }
                }

                // Start Time & Duration Steppers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CompactGateStepper(
                        label = "START TIME",
                        valueText = formatClockMinutes(request.proposedStartMinutes),
                        onMinus = { onUpdateDraft(null, null, null, request.proposedStartMinutes - 30, null, null) },
                        onPlus = { onUpdateDraft(null, null, null, request.proposedStartMinutes + 30, null, null) },
                        testTagPrefix = "gate_start",
                        modifier = Modifier.weight(1f),
                    )
                    CompactGateStepper(
                        label = "DURATION",
                        valueText = "${request.proposedDurationMinutes} min",
                        onMinus = { onUpdateDraft(null, null, null, null, request.proposedDurationMinutes - 15, null) },
                        onPlus = { onUpdateDraft(null, null, null, null, request.proposedDurationMinutes + 15, null) },
                        testTagPrefix = "gate_duration",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            OutlinedTextField(
                value = request.reasonText,
                onValueChange = { onUpdateDraft(null, null, null, null, null, it) },
                label = { Text("Reason for AI Review (Optional)") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primary,
                    unfocusedBorderColor = tokens.hairlineRest,
                    focusedContainerColor = tokens.carbonSurface,
                    unfocusedContainerColor = tokens.carbonSurface,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gate_reason_input"),
            )

            // Animated AI Permission Verdict / Action Section
            AnimatedContent(
                targetState = request.phase,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(140)) },
                label = "permissionVerdictTransition",
            ) { phase ->
                when (phase) {
                    PermissionVerdictPhase.DRAFTING -> {
                        Button(
                            onClick = onSubmitToAi,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primary,
                                contentColor = InkOnAccentDark,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("planner_gate_submit_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.planner_gate_submit),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }

                    PermissionVerdictPhase.EVALUATING -> {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = tokens.carbonSurface,
                            border = BorderStroke(1.dp, primary.copy(alpha = 0.42f)),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                CircularProgressIndicator(
                                    color = primary,
                                    strokeWidth = 2.5.dp,
                                    modifier = Modifier.size(20.dp),
                                )
                                Text(
                                    text = stringResource(R.string.planner_gate_evaluating),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.inkPrimary,
                                )
                            }
                        }
                    }

                    PermissionVerdictPhase.APPROVED -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = FeedbackCorrectContainer.copy(alpha = 0.85f),
                                border = BorderStroke(1.dp, FeedbackCorrect.copy(alpha = 0.50f)),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = FeedbackCorrect,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Text(
                                            text = stringResource(R.string.planner_gate_approved_badge),
                                            style = MicroTagMono,
                                            color = FeedbackCorrect,
                                        )
                                    }
                                    Text(
                                        text = request.verdictHeadline,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = tokens.inkPrimary,
                                    )
                                    Text(
                                        text = request.verdictExplanation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.inkSecondary,
                                    )
                                }
                            }

                            Button(
                                onClick = onApplyApproved,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FeedbackCorrect,
                                    contentColor = InkOnAccentDark,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .minimumInteractiveComponentSize()
                                    .testTag("planner_gate_apply_button"),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.planner_gate_apply_unlocked),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }

                    PermissionVerdictPhase.BLOCKED -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = FeedbackIncorrectContainer.copy(alpha = 0.85f),
                                border = BorderStroke(1.dp, FeedbackIncorrect.copy(alpha = 0.48f)),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.GppBad,
                                            contentDescription = null,
                                            tint = FeedbackIncorrect,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Text(
                                            text = stringResource(R.string.planner_gate_blocked_badge),
                                            style = MicroTagMono,
                                            color = FeedbackIncorrect,
                                        )
                                    }
                                    Text(
                                        text = request.verdictHeadline,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = tokens.inkPrimary,
                                    )
                                    Text(
                                        text = request.verdictExplanation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.inkSecondary,
                                    )
                                    val counter = request.counterProposal
                                    if (counter != null) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = tokens.obsidianSurface,
                                            border = BorderStroke(1.dp, primary.copy(alpha = 0.35f)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 4.dp),
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                verticalArrangement = Arrangement.spacedBy(3.dp),
                                            ) {
                                                Text(
                                                    text = "AI COUNTER-PROPOSAL",
                                                    style = MicroTagMono,
                                                    color = primary,
                                                )
                                                Text(
                                                    text = counter.summary,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = tokens.inkPrimary,
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                OutlinedButton(
                                    onClick = { onUpdateDraft(null, null, null, null, null, null) },
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, tokens.hairlineRest),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .testTag("planner_gate_revise_button"),
                                ) {
                                    Text(
                                        text = stringResource(R.string.planner_gate_revise),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = tokens.inkSecondary,
                                    )
                                }

                                if (request.counterProposal != null) {
                                    Button(
                                        onClick = onAcceptCounterProposal,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = primary,
                                            contentColor = InkOnAccentDark,
                                        ),
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .minimumInteractiveComponentSize()
                                            .testTag("planner_gate_counter_button"),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AutoFixHigh,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = stringResource(R.string.planner_gate_accept_counter),
                                            style = MaterialTheme.typography.labelLarge,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CompactGateStepper(
    label: String,
    valueText: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    testTagPrefix: String,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = onMinus,
                    modifier = Modifier.testTag("${testTagPrefix}_minus"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = "Decrease $label",
                        tint = tokens.inkSecondary,
                    )
                }
                Text(
                    text = valueText,
                    style = MonoMetricMedium,
                    color = tokens.inkPrimary,
                )
                IconButton(
                    onClick = onPlus,
                    modifier = Modifier.testTag("${testTagPrefix}_plus"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Increase $label",
                        tint = tokens.inkSecondary,
                    )
                }
            }
        }
    }
}
