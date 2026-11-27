package com.example.ui.planner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.PlannerAiDecisionOutcome
import com.example.model.PlannerAiGateActionType
import com.example.model.PlannerAiGateRequest
import com.example.model.PlannerRag1State
import com.example.model.PlannerRag2State
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono

@Composable
fun PlannerAiGateModal(
    request: PlannerAiGateRequest,
    rag1: PlannerRag1State,
    rag2: PlannerRag2State,
    onUpdateTitle: (String) -> Unit,
    onUpdateTimeWindow: (String) -> Unit,
    onUpdateReason: (String) -> Unit,
    onSimulateOutcome: (PlannerAiDecisionOutcome) -> Unit,
    onApplyApproved: () -> Unit,
    onAcceptCounterProposal: () -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val emerald = tokens.semanticSuccess
    val amber = AccentMood.AMBER.accent
    val errorColor = MaterialTheme.colorScheme.error

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = tokens.obsidianSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 580.dp)
                .testTag("planner_ai_gate_modal"),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(tokens.carbonSurface),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.VerifiedUser,
                                contentDescription = "AI Approval Gate",
                                tint = tokens.themeAccent,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "AI PLANNER APPROVAL GATE",
                                style = MicroTagMono,
                                color = tokens.themeAccent,
                            )
                            Text(
                                text = request.actionType.label,
                                style = MaterialTheme.typography.titleMedium,
                                color = tokens.inkPrimary,
                            )
                        }
                    }

                    val closeInteraction = remember { MutableInteractionSource() }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                        modifier = Modifier
                            .testTag("planner_gate_close_button")
                            .minimumInteractiveComponentSize()
                            .tactileScale(closeInteraction, 0.94f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = closeInteraction,
                                indication = ripple(),
                                role = Role.Button,
                                onClick = onDismiss,
                            ),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(40.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close AI Gate",
                                tint = tokens.inkSecondary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                // Zero-Context Data-First Evidence Strip
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "ZERO-CONTEXT EVIDENCE · RAG(1) + RAG(2) ONLY",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                        Text(
                            text = "Peak Focus: ${rag2.peakFocusWindow} · Task Done: ${rag2.taskCompletedPercent}% · 20d Study Avg: %.1fh/d"
                                .format(rag1.oneMonthAvgStudyHours),
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }
                }

                // Editable proposal fields (when editing or adding)
                if (request.actionType == PlannerAiGateActionType.EDIT_TASK ||
                    request.actionType == PlannerAiGateActionType.ADD_TASK
                ) {
                    OutlinedTextField(
                        value = request.proposedTitle,
                        onValueChange = onUpdateTitle,
                        label = { Text("Task Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = tokens.themeAccent,
                            unfocusedBorderColor = tokens.hairlineRest,
                            focusedTextColor = tokens.inkPrimary,
                            unfocusedTextColor = tokens.inkPrimary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("planner_gate_title_input"),
                    )
                    OutlinedTextField(
                        value = request.proposedTimeWindow,
                        onValueChange = onUpdateTimeWindow,
                        label = { Text("Time Window (e.g. 18:30 – 19:45)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = tokens.themeAccent,
                            unfocusedBorderColor = tokens.hairlineRest,
                            focusedTextColor = tokens.inkPrimary,
                            unfocusedTextColor = tokens.inkPrimary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("planner_gate_time_input"),
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = request.proposedTitle,
                                style = MaterialTheme.typography.titleSmall,
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = "${request.proposedSubject} · ${request.proposedTimeWindow} · ${request.proposedDurationMinutes}m",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = request.userReason,
                    onValueChange = onUpdateReason,
                    label = { Text("Reason for AI Review (data-first rules still apply)") },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tokens.themeAccent,
                        unfocusedBorderColor = tokens.hairlineRest,
                        focusedTextColor = tokens.inkPrimary,
                        unfocusedTextColor = tokens.inkPrimary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("planner_gate_reason_input"),
                )

                // Interactive Test Path Selector (Approve / Counter-Proposal / Deny)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "INTERACTIVE AI DECISION TEST PATHS",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        GatePathChip(
                            label = "Approve",
                            selected = request.aiOutcome == PlannerAiDecisionOutcome.APPROVED,
                            accent = emerald,
                            testTag = "planner_gate_test_approve",
                            onClick = { onSimulateOutcome(PlannerAiDecisionOutcome.APPROVED) },
                            modifier = Modifier.weight(1f),
                        )
                        GatePathChip(
                            label = "Counter",
                            selected = request.aiOutcome == PlannerAiDecisionOutcome.COUNTER_PROPOSAL,
                            accent = amber,
                            testTag = "planner_gate_test_counter",
                            onClick = { onSimulateOutcome(PlannerAiDecisionOutcome.COUNTER_PROPOSAL) },
                            modifier = Modifier.weight(1f),
                        )
                        GatePathChip(
                            label = "Deny",
                            selected = request.aiOutcome == PlannerAiDecisionOutcome.DENIED,
                            accent = errorColor,
                            testTag = "planner_gate_test_deny",
                            onClick = { onSimulateOutcome(PlannerAiDecisionOutcome.DENIED) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                // AI Verdict Card
                if (request.aiOutcome != PlannerAiDecisionOutcome.PENDING) {
                    val (verdictColor, verdictIcon) = when (request.aiOutcome) {
                        PlannerAiDecisionOutcome.APPROVED -> emerald to Icons.Rounded.CheckCircle
                        PlannerAiDecisionOutcome.COUNTER_PROPOSAL -> amber to Icons.Rounded.Lightbulb
                        PlannerAiDecisionOutcome.DENIED -> errorColor to Icons.Rounded.Block
                        PlannerAiDecisionOutcome.PENDING -> tokens.themeAccent to Icons.Rounded.AutoAwesome
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, verdictColor.copy(alpha = 0.50f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("planner_gate_verdict_card"),
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
                                    imageVector = verdictIcon,
                                    contentDescription = request.aiOutcome.badge,
                                    tint = verdictColor,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = request.aiOutcome.badge,
                                    style = MicroTagMono,
                                    color = verdictColor,
                                )
                            }
                            Text(
                                text = request.aiReasoning,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkPrimary,
                            )

                            if (request.aiOutcome == PlannerAiDecisionOutcome.COUNTER_PROPOSAL &&
                                request.counterTitle != null
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = tokens.obsidianSurface,
                                    border = BorderStroke(1.dp, amber.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(3.dp),
                                    ) {
                                        Text(
                                            text = "AI COUNTER-PROPOSAL",
                                            style = MicroTagMono,
                                            color = amber,
                                        )
                                        Text(
                                            text = request.counterTitle,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = tokens.inkPrimary,
                                        )
                                        Text(
                                            text = "${request.counterTimeWindow ?: request.proposedTimeWindow} · ${request.counterDurationMinutes ?: request.proposedDurationMinutes}m",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = tokens.inkSecondary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val cancelInteraction = remember { MutableInteractionSource() }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier
                            .testTag("planner_gate_dismiss_cta")
                            .minimumInteractiveComponentSize()
                            .tactileScale(cancelInteraction, 0.96f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = cancelInteraction,
                                indication = ripple(),
                                role = Role.Button,
                                onClick = onDismiss,
                            ),
                    ) {
                        Text(
                            text = if (request.aiOutcome == PlannerAiDecisionOutcome.DENIED) "Keep Schedule" else "Cancel",
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.inkSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    when (request.aiOutcome) {
                        PlannerAiDecisionOutcome.PENDING -> {
                            GatePrimaryButton(
                                label = "Evaluate with AI",
                                containerColor = tokens.primaryCta,
                                contentColor = tokens.onPrimaryCta,
                                testTag = "planner_gate_evaluate_cta",
                                onClick = { onSimulateOutcome(PlannerAiDecisionOutcome.APPROVED) },
                            )
                        }

                        PlannerAiDecisionOutcome.APPROVED -> {
                            GatePrimaryButton(
                                label = "Apply Approved Change",
                                containerColor = emerald,
                                contentColor = Color.Black,
                                testTag = "planner_gate_apply_approved_cta",
                                onClick = onApplyApproved,
                            )
                        }

                        PlannerAiDecisionOutcome.COUNTER_PROPOSAL -> {
                            GatePrimaryButton(
                                label = "Accept Counter-Proposal",
                                containerColor = amber,
                                contentColor = Color.Black,
                                testTag = "planner_gate_accept_counter_cta",
                                onClick = onAcceptCounterProposal,
                            )
                        }

                        PlannerAiDecisionOutcome.DENIED -> {
                            GatePrimaryButton(
                                label = "Try Counter-Proposal",
                                containerColor = tokens.slateElevated,
                                contentColor = tokens.inkPrimary,
                                testTag = "planner_gate_try_counter_cta",
                                onClick = { onSimulateOutcome(PlannerAiDecisionOutcome.COUNTER_PROPOSAL) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GatePathChip(
    label: String,
    selected: Boolean,
    accent: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(11.dp)

    Surface(
        shape = shape,
        color = if (selected) accent.copy(alpha = 0.16f) else tokens.carbonSurface,
        border = BorderStroke(1.dp, if (selected) accent else tokens.hairlineRest),
        modifier = modifier
            .testTag(testTag)
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, 0.96f)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = accent.copy(alpha = 0.2f)),
                role = Role.RadioButton,
                onClick = onClick,
            ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) accent else tokens.inkSecondary,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun GatePrimaryButton(
    label: String,
    containerColor: Color,
    contentColor: Color,
    testTag: String,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(12.dp)

    Surface(
        shape = shape,
        color = containerColor,
        modifier = Modifier
            .testTag(testTag)
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, 0.96f)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
            )
        }
    }
}
