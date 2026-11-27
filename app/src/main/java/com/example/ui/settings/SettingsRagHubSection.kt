package com.example.ui.settings

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.Storage
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.AppRagFileId
import com.example.model.SettingsRagHubState
import com.example.model.WorkspaceUiState
import com.example.model.formatDurationCompactHm
import com.example.model.formatMinutesHm
import com.example.ui.components.ObsidianCard
import com.example.ui.components.tactileScale
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono

@Composable
fun SettingsRagHubSection(
    uiState: WorkspaceUiState,
    ragHubState: SettingsRagHubState,
    isWideLayout: Boolean,
    onSelectRagFile: (AppRagFileId) -> Unit,
    onUpdateUserNote: (String) -> Unit,
    onRunAiOptimization: (AppRagFileId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val emerald = tokens.semanticSuccess
    val selectedRag = ragHubState.selectedRagId
    val optimizationResult = ragHubState.optimizationResults[selectedRag]

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_rag_hub_section"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Section Header + Data-First Rule Banner
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Storage,
                    contentDescription = null,
                    tint = tokens.themeAccent,
                    modifier = Modifier.size(19.dp),
                )
                Text(
                    text = "Centralized RAG Vault & AI Optimizer",
                    style = MaterialTheme.typography.titleLarge,
                    color = tokens.inkPrimary,
                )
            }
            Text(
                text = "Inspect every RAG file across GHICKIN and ask AI to compact logs or generate recommendations. Recorded study & telemetry data always takes highest priority over subjective user wording.",
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
            )
        }

        // Data-First Priority Badge
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, emerald.copy(alpha = 0.42f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.FactCheck,
                    contentDescription = null,
                    tint = emerald,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "DATA-FIRST PRIORITY ENFORCED · Objective 20d/1m/6m logs, task completion % & timer totals always outweigh subjective text notes.",
                    style = MicroTagMono,
                    color = tokens.inkPrimary,
                )
            }
        }

        // 4 RAG File Selector Cards
        val ragFiles = AppRagFileId.entries
        if (isWideLayout) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ragFiles.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        pair.forEach { ragId ->
                            RagFileIndexCard(
                                ragId = ragId,
                                selected = ragId == selectedRag,
                                hasOptimizedOutput = ragHubState.optimizationResults.containsKey(ragId),
                                liveMetaLine = ragLiveMetricPreview(ragId, uiState),
                                onClick = { onSelectRagFile(ragId) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ragFiles.forEach { ragId ->
                    RagFileIndexCard(
                        ragId = ragId,
                        selected = ragId == selectedRag,
                        hasOptimizedOutput = ragHubState.optimizationResults.containsKey(ragId),
                        liveMetaLine = ragLiveMetricPreview(ragId, uiState),
                        onClick = { onSelectRagFile(ragId) },
                    )
                }
            }
        }

        // Active RAG Inspector & Ask AI to Optimize Panel
        ObsidianCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_rag_inspector_card"),
            containerColor = tokens.obsidianSurface,
            cornerRadius = 20.dp,
            contentPadding = PaddingValues(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = selectedRag.fileName,
                            style = MicroTagMono,
                            color = tokens.themeAccent,
                        )
                        Text(
                            text = selectedRag.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = tokens.inkPrimary,
                        )
                    }
                }

                // Current Raw + Compacted Payload Preview
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text = "CURRENT STORED RAG SNAPSHOT",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                        Text(
                            text = ragStoredSnapshotText(selectedRag, uiState),
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }
                }

                // Optional context note (to test Data-First Override)
                OutlinedTextField(
                    value = ragHubState.userNoteInput,
                    onValueChange = onUpdateUserNote,
                    label = { Text("Optional question or goal note for AI Optimizer") },
                    placeholder = { Text("e.g. I feel like studying 10h tonight even if tired…") },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tokens.themeAccent,
                        unfocusedBorderColor = tokens.hairlineRest,
                        focusedTextColor = tokens.inkPrimary,
                        unfocusedTextColor = tokens.inkPrimary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_rag_user_note_input"),
                )

                // Ask AI to Optimize & Recommend CTA
                val optInt = remember { MutableInteractionSource() }
                Surface(
                    shape = RoundedCornerShape(13.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_rag_optimize_cta")
                        .minimumInteractiveComponentSize()
                        .tactileScale(optInt, 0.97f)
                        .clip(RoundedCornerShape(13.dp))
                        .clickable(
                            interactionSource = optInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = { onRunAiOptimization(selectedRag) },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = tokens.onPrimaryCta,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ask AI to Optimize & Recommend (Data-First)",
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.onPrimaryCta,
                        )
                    }
                }

                // Optimization Output & Data-First Recommendations
                if (optimizationResult != null) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, emerald.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_rag_optimization_result"),
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = emerald,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Text(
                                        text = optimizationResult.optimizedAtLabel,
                                        style = MicroTagMono,
                                        color = emerald,
                                    )
                                }
                                Text(
                                    text = optimizationResult.tokenReductionLabel,
                                    style = MicroTagMono,
                                    color = tokens.inkMuted,
                                )
                            }

                            Text(
                                text = optimizationResult.dataFirstPriorityNote,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = tokens.themeAccent,
                            )

                            Text(
                                text = optimizationResult.compactedSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkPrimary,
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "DATA-DRIVEN RECOMMENDATIONS",
                                    style = MicroTagMono,
                                    color = tokens.inkMuted,
                                )
                                optimizationResult.recommendations.forEachIndexed { idx, rec ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 6.dp)
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(emerald),
                                        )
                                        Text(
                                            text = "${idx + 1}. $rec",
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
        }
    }
}

@Composable
private fun RagFileIndexCard(
    ragId: AppRagFileId,
    selected: Boolean,
    hasOptimizedOutput: Boolean,
    liveMetaLine: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(16.dp)

    Surface(
        shape = shape,
        color = if (selected) tokens.slateElevated else tokens.obsidianSurface,
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) tokens.themeAccent else tokens.hairlineRest,
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_rag_card_${ragId.name.lowercase()}")
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, 0.98f)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                role = Role.RadioButton,
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = ragId.categoryBadge,
                    style = MicroTagMono,
                    color = if (selected) tokens.themeAccent else tokens.inkMuted,
                )
                if (hasOptimizedOutput) {
                    Text(
                        text = "AI OPTIMIZED",
                        style = MicroTagMono,
                        color = tokens.semanticSuccess,
                    )
                }
            }
            Text(
                text = ragId.displayName,
                style = MaterialTheme.typography.titleSmall,
                color = tokens.inkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = liveMetaLine,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = ragId.cadenceNote,
                style = MicroTagMono,
                color = tokens.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun ragLiveMetricPreview(ragId: AppRagFileId, uiState: WorkspaceUiState): String {
    return when (ragId) {
        AppRagFileId.PLANNER_RAG_1 -> {
            val r1 = uiState.standalonePlannerState.rag1
            "20d indiv + 1–2m avg (%.1fh) + 6m avg (%.1fh) · ${r1.compactedEstimatedTokens} tok"
                .format(r1.twoMonthAvgStudyHours, r1.sixMonthAvgStudyHours)
        }
        AppRagFileId.PLANNER_RAG_2 -> {
            val p = uiState.standalonePlannerState
            "Peak ${p.rag2.peakFocusWindow} · Live tasks ${p.liveTaskCompletionPercent}%"
        }
        AppRagFileId.STUDY_LOG_RAG -> {
            val t = uiState.studyTimerGroupState
            val hi = t.highestStudyDay?.let { formatMinutesHm(it.totalMinutes) } ?: "—"
            val lo = t.lowestStudyDay?.let { formatMinutesHm(it.totalMinutes) } ?: "—"
            "Today ${formatDurationCompactHm(t.todayTotalStudySeconds)} · Raw High $hi / Low $lo"
        }
        AppRagFileId.CHAPTER_DIAGRAM_RAG -> {
            "${uiState.grokTutorState.diagramCatalog.size} offline HTML/SVG diagrams indexed in private filesDir"
        }
    }
}

private fun ragStoredSnapshotText(ragId: AppRagFileId, uiState: WorkspaceUiState): String {
    return when (ragId) {
        AppRagFileId.PLANNER_RAG_1 -> {
            val r1 = uiState.standalonePlannerState.rag1
            "${r1.compactedSummaryText}\n\nHorizon averages: 1m study %.1fh/d · 2m study %.1fh/d · 6m study %.1fh/d."
                .format(r1.oneMonthAvgStudyHours, r1.twoMonthAvgStudyHours, r1.sixMonthAvgStudyHours)
        }
        AppRagFileId.PLANNER_RAG_2 -> {
            val r2 = uiState.standalonePlannerState.rag2
            "Active hours: ${r2.activeHoursWindow}\nPeak focus: ${r2.peakFocusWindow}\n" +
                "Live task completion: ${uiState.standalonePlannerState.liveTaskCompletionPercent}%\nNote: ${r2.focusEfficiencyNote}"
        }
        AppRagFileId.STUDY_LOG_RAG -> {
            val t = uiState.studyTimerGroupState
            "${t.studyLogRag.compactedSummary}\n\nLogged sessions: ${t.studyLogRag.totalLoggedSessions} · " +
                "Raw unoptimized 20d records preserved for Daily Study Graph."
        }
        AppRagFileId.CHAPTER_DIAGRAM_RAG -> {
            val files = uiState.grokTutorState.diagramCatalog.joinToString(" · ") { it.fileName }
            "Indexed private diagram filenames ($files). Only filenames from the user's linked chapters are appended to AI Tutor prompts."
        }
    }
}
