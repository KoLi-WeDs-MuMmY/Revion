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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Schedule
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ClockThemeId
import com.example.model.HomeClockTagPickerState
import com.example.model.LiveClockTick
import com.example.model.MotionIntensity
import com.example.model.StandalonePlannerUiState
import com.example.model.StudyBatch
import com.example.model.StudyTimerGroupUiState
import com.example.model.VaultSubjectNode
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.timer.FlipClockReadoutLeaf
import com.example.ui.timer.FlipTileSize
import kotlinx.coroutines.flow.StateFlow

/**
 * Modern Rectangular Pomodoro Desk-Clock Card on Home:
 * - Styled in the user's active Flip-Clock Theme from Settings.
 * - Uses isolated FlipClockReadoutLeaf so Home never recomposes on 1s ticks.
 * - Clicking Play opens the cascading Batch -> Subject -> Chapter -> Lecture picker
 *   and launches Full-Screen Clock Mode (collapsing navigation chrome until Stop).
 */
@Composable
fun HomeRectangularFlipClockCard(
    timerState: StudyTimerGroupUiState,
    clockTickFlow: StateFlow<LiveClockTick>,
    clockTheme: ClockThemeId,
    motionIntensity: MotionIntensity,
    onClickTagSelector: () -> Unit,
    onClickPlayClock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val palette = clockTheme.palette
    val tagLabel = buildString {
        if (timerState.activeSubjectName != null) {
            append(timerState.activeSubjectName)
            timerState.syncedLectureTitle?.let { append(" · $it") }
        } else {
            append("Untagged · Tap Play to choose")
        }
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_rectangular_flip_clock_card"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header Row
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
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        tint = palette.accentColor,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "FOCUS DESK CLOCK",
                        style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                ) {
                    Text(
                        text = clockTheme.label,
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            // Rectangular Split-Flap Flip Clock Readout (Isolated Leaf!)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                FlipClockReadoutLeaf(
                    clockTickFlow = clockTickFlow,
                    clockTheme = clockTheme,
                    motionIntensity = motionIntensity,
                    tileSize = FlipTileSize.COMPACT_HOME,
                    showSubLabels = true,
                )
            }

            // Active Subject / Lecture Tag Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(
                            if (timerState.activeSubjectName != null) timerState.activeSubjectMood.accent else tokens.inkMuted,
                        ),
                )
                Text(
                    text = tagLabel,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }

            // Actions Row: Tag Subject/Lecture + Play (Full-Screen Launch)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .minimumInteractiveComponentSize()
                        .tactileScale()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = tokens.inkPrimary),
                            role = Role.Button,
                            onClick = onClickTagSelector,
                        )
                        .testTag("home_clock_tag_button"),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Label,
                            contentDescription = "Tag Subject or Lecture",
                            tint = tokens.inkSecondary,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Tag Lecture",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .weight(1.15f)
                        .clip(RoundedCornerShape(12.dp))
                        .minimumInteractiveComponentSize()
                        .tactileScale()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = tokens.onPrimaryCta),
                            role = Role.Button,
                            onClick = onClickPlayClock,
                        )
                        .testTag("home_clock_play_button"),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = if (timerState.isTimerActiveSession) Icons.Rounded.Fullscreen else Icons.Rounded.PlayArrow,
                            contentDescription = "Start Focus Clock",
                            tint = tokens.onPrimaryCta,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = if (timerState.isTimerActiveSession) "Full Screen" else "Play Focus",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = tokens.onPrimaryCta,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Clean, Airy "Today's Plan & Tasks" Card on Home:
 * - Next 2–3 scheduled AI blocks (time + title + subject tag)
 * - Quick Task checklist (up to 4 tasks with working checkboxes + completion bar)
 * - One-tap "Open Planner" action
 */
@Composable
fun HomeTodaysPlanTasksCard(
    plannerState: StandalonePlannerUiState,
    onToggleUserTask: (String) -> Unit,
    onOpenPlanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val nextBlocks = remember(plannerState.tasks) {
        val upcoming = plannerState.tasks.filterNot { it.isCompleted }
        (if (upcoming.isNotEmpty()) upcoming else plannerState.tasks).take(3)
    }
    val visibleTasks = remember(plannerState.userTasks) {
        plannerState.userTasks.take(4)
    }
    val doneCount = plannerState.userTasks.count { it.isCompleted }
    val totalCount = plannerState.userTasks.size.coerceAtLeast(1)

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_todays_plan_tasks_card"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Card Header
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
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = AccentMood.EMERALD.accent,
                        modifier = Modifier.size(17.dp),
                    )
                    Text(
                        text = "TODAY'S PLAN & TASKS",
                        style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .clickable(role = Role.Button, onClick = onOpenPlanner)
                        .testTag("home_open_planner_button"),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "Open Planner",
                            style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                            color = tokens.themeAccent,
                            maxLines = 1,
                            softWrap = false,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = tokens.themeAccent,
                            modifier = Modifier.size(13.dp),
                        )
                    }
                }
            }

            // Next Scheduled Blocks
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "NEXT SCHEDULED",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
                nextBlocks.forEach { block ->
                    Surface(
                        shape = RoundedCornerShape(11.dp),
                        color = tokens.obsidianSurface,
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(block.accentMood.accent),
                            )
                            Text(
                                text = block.timeWindow,
                                style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                                color = block.accentMood.accent,
                                maxLines = 1,
                                softWrap = false,
                            )
                            Text(
                                text = block.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = tokens.inkPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            // Quick Task Checklist + Progress
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "QUICK TASKS",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                    Text(
                        text = "$doneCount/${plannerState.userTasks.size} done",
                        style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                        color = AccentMood.EMERALD.accent,
                    )
                }

                AnimatedLinearProgressBar(
                    progressPercent = ((doneCount * 100) / totalCount).coerceIn(0, 100),
                    accentColor = AccentMood.EMERALD.accent,
                    height = 5.dp,
                )

                visibleTasks.forEach { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(role = Role.Checkbox) { onToggleUserTask(task.id) }
                            .padding(horizontal = 4.dp, vertical = 5.dp)
                            .testTag("home_quick_task_${task.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = if (task.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                            contentDescription = "Toggle task",
                            tint = if (task.isCompleted) AccentMood.EMERALD.accent else task.accentMood.accent,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            ),
                            color = if (task.isCompleted) tokens.inkMuted else tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "${task.durationMinutes}m",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Clean Cascading Selection Window triggered from Home's Flip Clock (Play or Tag):
 * Pick Batch -> Subject -> Chapter -> Lecture (or start Untagged) before launching Full-Screen Focus.
 */
@Composable
fun HomeCascadingClockTagModal(
    pickerState: HomeClockTagPickerState,
    batches: List<StudyBatch>,
    subjectsByBatch: Map<String, List<VaultSubjectNode>>,
    onSelectBatch: (String) -> Unit,
    onSelectSubject: (String?) -> Unit,
    onSelectChapter: (String?) -> Unit,
    onSelectLecture: (String?) -> Unit,
    onConfirmSelection: () -> Unit,
    onStartUntagged: () -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val subjects = subjectsByBatch[pickerState.selectedBatchId].orEmpty()
    val selectedSubject = subjects.firstOrNull { it.id == pickerState.selectedSubjectId }
    val chapters = selectedSubject?.chapters.orEmpty()
    val selectedChapter = chapters.firstOrNull { it.id == pickerState.selectedChapterId }
    val lectures = selectedChapter?.lectures.orEmpty()

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
                .testTag("home_cascading_clock_tag_modal"),
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (pickerState.launchFullScreenOnConfirm) {
                                "Choose What You're Studying"
                            } else {
                                "Tag Focus Clock Session"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = "Batch → Subject → Chapter → Lecture",
                            style = MicroTagMono,
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

                // 1. Batch Step
                CascadeStepLabel("1. BATCH")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items = batches, key = { it.id }) { batch ->
                        val sel = batch.id == pickerState.selectedBatchId
                        CascadeChip(
                            label = batch.name,
                            selected = sel,
                            accent = batch.accentMood.accent,
                            onClick = { onSelectBatch(batch.id) },
                        )
                    }
                }

                // 2. Subject Step
                CascadeStepLabel("2. SUBJECT")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items = subjects, key = { it.id }) { subj ->
                        val sel = subj.id == pickerState.selectedSubjectId
                        CascadeChip(
                            label = subj.name,
                            selected = sel,
                            accent = subj.accentMood.accent,
                            onClick = { onSelectSubject(subj.id) },
                        )
                    }
                }

                // 3. Chapter Step
                if (chapters.isNotEmpty()) {
                    CascadeStepLabel("3. CHAPTER (OPTIONAL)")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(items = chapters, key = { it.id }) { ch ->
                            val sel = ch.id == pickerState.selectedChapterId
                            CascadeChip(
                                label = ch.title,
                                selected = sel,
                                accent = selectedSubject?.accentMood?.accent ?: tokens.themeAccent,
                                onClick = { onSelectChapter(ch.id) },
                            )
                        }
                    }
                }

                // 4. Lecture Step
                if (lectures.isNotEmpty()) {
                    CascadeStepLabel("4. LECTURE (OPTIONAL)")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        lectures.take(5).forEach { lec ->
                            val sel = lec.id == pickerState.selectedLectureId
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (sel) tokens.themeAccent.copy(alpha = 0.16f) else tokens.obsidianSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (sel) tokens.themeAccent else tokens.hairlineSubtle,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSelectLecture(lec.id) },
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = lec.title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                        ),
                                        color = tokens.inkPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Text(
                                        text = lec.durationLabel,
                                        style = MicroTagMono,
                                        color = tokens.inkMuted,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(2.dp))

                // Footer Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.obsidianSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .minimumInteractiveComponentSize()
                            .clickable(role = Role.Button, onClick = onStartUntagged)
                            .testTag("home_clock_modal_untagged_button"),
                    ) {
                        Text(
                            text = if (pickerState.launchFullScreenOnConfirm) "Start Untagged" else "Clear Tag",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = tokens.inkSecondary,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier
                                .padding(vertical = 12.dp, horizontal = 10.dp)
                                .align(Alignment.CenterVertically),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.primaryCta,
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(12.dp))
                            .minimumInteractiveComponentSize()
                            .clickable(role = Role.Button, onClick = onConfirmSelection)
                            .testTag("home_clock_modal_confirm_button"),
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = if (pickerState.launchFullScreenOnConfirm) Icons.Rounded.PlayArrow else Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = tokens.onPrimaryCta,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (pickerState.launchFullScreenOnConfirm) "Start Full Screen" else "Save Tag",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = tokens.onPrimaryCta,
                                maxLines = 1,
                                softWrap = false,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CascadeStepLabel(text: String) {
    Text(
        text = text,
        style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
        color = GhickinTokens.colors.inkMuted,
    )
}

@Composable
private fun CascadeChip(
    label: String,
    selected: Boolean,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) accent.copy(alpha = 0.18f) else tokens.obsidianSurface,
        border = BorderStroke(1.dp, if (selected) accent else tokens.hairlineRest),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(role = Role.RadioButton, onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(accent),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                ),
                color = if (selected) tokens.inkPrimary else tokens.inkSecondary,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
