package com.example.ui.home

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.InspectSelection
import com.example.model.WorkspaceTab
import com.example.ui.components.AnodizedPill
import com.example.ui.components.tactileScale
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoMetricMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectDetailSheet(
    selection: InspectSelection,
    onDismiss: () -> Unit,
    onJumpToTab: (WorkspaceTab) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val presentation = remember(selection) {
        when (selection) {
            is InspectSelection.HeroBrief -> InspectPresentation(
                badgeText = "DAILY REVISION BRIEF",
                title = selection.brief.headline,
                subtitle = selection.brief.summary,
                accentColor = selection.brief.sourceSubjects.firstOrNull()?.second?.accent,
                metrics = listOf(
                    "Duration" to "${selection.brief.estimatedMinutes} min",
                    "Checks" to "${selection.brief.checkCount}",
                    "Target" to "${selection.brief.targetRetentionPercent}%",
                ),
                bullets = selection.brief.focusPoints,
                targetTab = WorkspaceTab.VAULT,
            )

            is InspectSelection.Subject -> InspectPresentation(
                badgeText = "${selection.subject.name.uppercase()} TELEMETRY",
                title = selection.subject.activeChapter,
                subtitle = selection.subject.focusNote,
                accentColor = selection.subject.accentMood.accent,
                metrics = listOf(
                    "Mastery" to "${selection.subject.masteryPercent}%",
                    "Chapters" to "${selection.subject.chaptersCompleted}/${selection.subject.totalChapters}",
                    "Weekly" to selection.subject.weeklyDelta,
                ),
                bullets = selection.subject.keyConcepts,
                targetTab = WorkspaceTab.BATCHES,
            )

            is InspectSelection.Topic -> InspectPresentation(
                badgeText = "${selection.topic.subjectName.uppercase()} • LOGGED TOPIC",
                title = selection.topic.chapterTitle,
                subtitle = selection.topic.subtopicSummary,
                accentColor = selection.topic.accentMood.accent,
                metrics = listOf(
                    "Logged" to selection.topic.loggedTimeLabel,
                    "Session" to "${selection.topic.durationMinutes} min",
                    "Confidence" to selection.topic.confidenceLabel,
                ),
                bullets = selection.topic.keyTakeaways,
                targetTab = WorkspaceTab.VAULT,
            )

            is InspectSelection.Review -> InspectPresentation(
                badgeText = "SPACED QUEUE • ${selection.review.intervalBadge.uppercase()}",
                title = selection.review.topicTitle,
                subtitle = "${selection.review.subjectName} spaced retention checkpoint scheduled for ${selection.review.dueLabel}.",
                accentColor = selection.review.accentMood.accent,
                metrics = listOf(
                    "Interval" to selection.review.intervalBadge,
                    "Cards" to "${selection.review.cardCount}",
                    "Retention" to "${selection.review.retentionPercent}%",
                ),
                bullets = selection.review.reviewNotes,
                targetTab = WorkspaceTab.VAULT,
            )
        }
    }

    val resolvedAccent = presentation.accentColor ?: MaterialTheme.colorScheme.primary

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = tokens.obsidianSurface,
        contentColor = tokens.inkPrimary,
        scrimColor = Color.Black.copy(alpha = 0.72f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 6.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(tokens.hairlineRest),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 620.dp)
                .align(Alignment.CenterHorizontally)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .testTag("inspect_detail_sheet"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AnodizedPill(
                    text = presentation.badgeText,
                    accentColor = resolvedAccent,
                    containerColor = tokens.carbonSurface,
                    textColor = resolvedAccent,
                    borderColor = resolvedAccent.copy(alpha = 0.32f),
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("inspect_close_button"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.inspect_close_desc),
                        tint = tokens.inkSecondary,
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = presentation.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = presentation.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.inkSecondary,
                )
            }

            // Metrics strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                presentation.metrics.forEach { (label, value) ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                        modifier = Modifier.weight(1f),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = value,
                                style = MonoMetricMedium,
                                color = tokens.inkPrimary,
                                maxLines = 1,
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.inkMuted,
                            )
                        }
                    }
                }
            }

            // Key focus bullets
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.inspect_key_focus),
                    style = MaterialTheme.typography.titleSmall,
                    color = tokens.inkPrimary,
                )
                presentation.bullets.forEach { bullet ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(resolvedAccent),
                        )
                        Text(
                            text = bullet,
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.inkSecondary,
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.inspect_phase_preview_note),
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkMuted,
            )

            // Jump CTA button
            val jumpInteraction = remember { MutableInteractionSource() }
            val jumpShape = RoundedCornerShape(14.dp)
            Surface(
                shape = jumpShape,
                color = resolvedAccent,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inspect_jump_to_${presentation.targetTab.routeId}")
                    .minimumInteractiveComponentSize()
                    .tactileScale(jumpInteraction, pressedScale = 0.975f)
                    .clip(jumpShape)
                    .clickable(
                        interactionSource = jumpInteraction,
                        indication = ripple(color = Color.Black.copy(alpha = 0.2f)),
                        role = Role.Button,
                        onClick = { onJumpToTab(presentation.targetTab) },
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(
                            R.string.inspect_open_tab_format,
                            "${presentation.targetTab.label} (${presentation.targetTab.phaseTag})",
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = tokens.amoledBackground,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = tokens.amoledBackground,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

private data class InspectPresentation(
    val badgeText: String,
    val title: String,
    val subtitle: String,
    val accentColor: Color?,
    val metrics: List<Pair<String, String>>,
    val bullets: List<String>,
    val targetTab: WorkspaceTab,
)
