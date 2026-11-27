package com.example.ui.timer

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.VaultLectureNode
import com.example.model.VaultSubjectNode
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono

/**
 * Popup shown when tapping Start without a pre-selected subject:
 * - Pick a Subject Tag (tags belong strictly to Subjects)
 * - Optionally Sync a Lecture to track exact time spent on that lecture
 * - Or tap Skip to start immediately Tagless
 */
@Composable
fun TimerStartTagDialog(
    subjects: List<VaultSubjectNode>,
    onStartWithSelection: (
        subjectId: String?,
        subjectName: String?,
        subjectMood: AccentMood,
        lectureId: String?,
        lectureTitle: String?,
    ) -> Unit,
    onSkipTagless: () -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    var selectedSubjectId by remember { mutableStateOf<String?>(subjects.firstOrNull()?.id) }
    var selectedLectureId by remember { mutableStateOf<String?>(null) }

    val selectedSubject = subjects.firstOrNull { it.id == selectedSubjectId }
    val lecturesForSubject: List<VaultLectureNode> = remember(selectedSubject) {
        selectedSubject?.chapters?.flatMap { it.lectures }.orEmpty()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = tokens.obsidianSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 540.dp)
                .testTag("timer_start_tag_dialog"),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
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
                            text = "TAG SUBJECT & SYNC LECTURE",
                            style = MicroTagMono,
                            color = tokens.themeAccent,
                        )
                        Text(
                            text = "Start Study Session",
                            style = MaterialTheme.typography.titleLarge,
                            color = tokens.inkPrimary,
                        )
                    }
                    val closeInt = remember { MutableInteractionSource() }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                        modifier = Modifier
                            .testTag("timer_tag_dialog_close")
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = closeInt,
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
                                contentDescription = "Close start popup",
                                tint = tokens.inkSecondary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                Text(
                    text = "Tags apply strictly to Subjects. Optionally sync a Lecture below to track exact time spent on it — or skip to start tagless.",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )

                // Step 1: Subject Tag Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1 · SUBJECT TAG (REQUIRED UNLESS SKIPPED)",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                    subjects.chunked(2).forEach { rowSubjects ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rowSubjects.forEach { subj ->
                                val isSelected = subj.id == selectedSubjectId
                                val subjInt = remember { MutableInteractionSource() }
                                val accent = subj.accentMood.accent
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) accent.copy(alpha = 0.16f) else tokens.carbonSurface,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) accent else tokens.hairlineRest,
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("timer_tag_subject_${subj.id}")
                                        .minimumInteractiveComponentSize()
                                        .tactileScale(subjInt, 0.96f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable(
                                            interactionSource = subjInt,
                                            indication = ripple(color = accent.copy(alpha = 0.2f)),
                                            role = Role.RadioButton,
                                            onClick = {
                                                selectedSubjectId = subj.id
                                                selectedLectureId = null
                                            },
                                        ),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(accent),
                                        )
                                        Text(
                                            text = subj.name,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = tokens.inkPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f),
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Rounded.CheckCircle,
                                                contentDescription = null,
                                                tint = accent,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                    }
                                }
                            }
                            if (rowSubjects.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Step 2: Optional Lecture Sync
                if (lecturesForSubject.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Link,
                                contentDescription = null,
                                tint = tokens.inkMuted,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = "2 · SYNC LECTURE TIME (OPTIONAL)",
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 172.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            lecturesForSubject.take(6).forEach { lec ->
                                val isLinked = lec.id == selectedLectureId
                                val lecInt = remember { MutableInteractionSource() }
                                Surface(
                                    shape = RoundedCornerShape(11.dp),
                                    color = if (isLinked) tokens.slateElevated else tokens.carbonSurface,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isLinked) tokens.themeAccent else tokens.hairlineSubtle,
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("timer_sync_lecture_${lec.id}")
                                        .clip(RoundedCornerShape(11.dp))
                                        .clickable(
                                            interactionSource = lecInt,
                                            indication = ripple(),
                                            role = Role.Checkbox,
                                            onClick = {
                                                selectedLectureId = if (isLinked) null else lec.id
                                            },
                                        ),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp),
                                        ) {
                                            Text(
                                                text = "${lec.lectureCode} · ${lec.title}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = tokens.inkPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            Text(
                                                text = "${lec.chapterTitle} · ${lec.durationLabel}",
                                                style = MicroTagMono,
                                                color = tokens.inkMuted,
                                                maxLines = 1,
                                            )
                                        }
                                        if (isLinked) {
                                            Text(
                                                text = "SYNCED",
                                                style = MicroTagMono,
                                                color = tokens.themeAccent,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer CTAs: Skip (Tagless) vs Start Tagged
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val skipInt = remember { MutableInteractionSource() }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier
                            .testTag("timer_start_skip_tagless")
                            .minimumInteractiveComponentSize()
                            .tactileScale(skipInt, 0.96f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = skipInt,
                                indication = ripple(),
                                role = Role.Button,
                                onClick = onSkipTagless,
                            ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SkipNext,
                                contentDescription = "Skip and start tagless",
                                tint = tokens.inkSecondary,
                                modifier = Modifier.size(17.dp),
                            )
                            Text(
                                text = "Skip (Tagless)",
                                style = MaterialTheme.typography.labelLarge,
                                color = tokens.inkSecondary,
                            )
                        }
                    }

                    val startInt = remember { MutableInteractionSource() }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.primaryCta,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("timer_start_confirm_tag")
                            .minimumInteractiveComponentSize()
                            .tactileScale(startInt, 0.96f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = startInt,
                                indication = ripple(),
                                role = Role.Button,
                                onClick = {
                                    val chosenLec = lecturesForSubject.firstOrNull { it.id == selectedLectureId }
                                    onStartWithSelection(
                                        selectedSubject?.id,
                                        selectedSubject?.name,
                                        selectedSubject?.accentMood ?: AccentMood.CYAN,
                                        chosenLec?.id,
                                        chosenLec?.let { "${it.lectureCode} · ${it.title}" },
                                    )
                                },
                            ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = tokens.onPrimaryCta,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedSubject != null) "Start · ${selectedSubject.name}" else "Start Timer",
                                style = MaterialTheme.typography.labelLarge,
                                color = tokens.onPrimaryCta,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}
