package com.example.ui.batches

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.BatchCrudDialog
import com.example.model.BatchesUiState
import com.example.model.PresetSubjectTemplate
import com.example.model.StudyBatch
import com.example.ui.components.KineticNumberText
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.home.SettingsTopBarGearButton
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.MonoMetricMedium
import com.example.ui.theme.rememberScreenLayoutMetrics

@Composable
fun BatchesScreen(
    batchesState: BatchesUiState,
    primaryBatchId: String,
    activeThemeMood: AccentMood,
    isWideLayout: Boolean,
    onOpenSettings: () -> Unit,
    onSelectBatch: (String) -> Unit,
    onSetPrimaryBatch: (String) -> Unit,
    onToggleExpandSubject: (String) -> Unit,
    onToggleExpandChapter: (String) -> Unit,
    onToggleChapterCompleted: (batchId: String, subjectId: String, chapterId: String) -> Unit,
    onToggleLectureCompleted: (batchId: String, subjectId: String, chapterId: String, lectureId: String) -> Unit,
    onOpenCrudDialog: (BatchCrudDialog) -> Unit,
    onCloseCrudDialog: () -> Unit,
    onEditBatch: (batchId: String, name: String, goalTag: String, targetLabel: String, mood: AccentMood) -> Unit,
    onDeleteBatch: (batchId: String) -> Unit,
    onAddSubject: (batchId: String, name: String, mood: AccentMood) -> Unit,
    onEditSubject: (batchId: String, subjectId: String, name: String, mood: AccentMood) -> Unit,
    onDeleteSubject: (batchId: String, subjectId: String) -> Unit,
    onAddChapter: (batchId: String, subjectId: String, title: String) -> Unit,
    onEditChapter: (batchId: String, subjectId: String, chapterId: String, title: String) -> Unit,
    onDeleteChapter: (batchId: String, subjectId: String, chapterId: String) -> Unit,
    onAddLecture: (batchId: String, subjectId: String, chapterId: String, title: String, duration: String) -> Unit,
    onEditLecture: (batchId: String, subjectId: String, chapterId: String, lectureId: String, title: String, duration: String) -> Unit,
    onDeleteLecture: (batchId: String, subjectId: String, chapterId: String, lectureId: String) -> Unit,
    onOpenBuilder: () -> Unit,
    onCloseBuilder: () -> Unit,
    onCreateBatch: (
        name: String,
        goalTag: String,
        accentMood: AccentMood,
        selectedSubjects: List<PresetSubjectTemplate>,
        customSubjectName: String,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()
    val selectedBatch = batchesState.selectedBatch
    val canDeleteBatch = batchesState.batches.size > 1

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("batches_screen"),
    ) {
        if (isWideLayout) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = metrics.maxContentWidth)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = metrics.horizontalPadding, vertical = metrics.verticalPadding),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                // Left Batch Roster Column (~32%)
                Column(
                    modifier = Modifier
                        .weight(0.32f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = stringResource(R.string.batches_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = stringResource(R.string.batches_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }

                    BatchRosterPane(
                        batches = batchesState.batches,
                        selectedBatchId = batchesState.selectedBatchId,
                        primaryBatchId = primaryBatchId,
                        onSelectBatch = onSelectBatch,
                        onCreateBatchClick = onOpenBuilder,
                        onSetPrimaryBatch = onSetPrimaryBatch,
                        onEditBatchClick = { batch ->
                            onOpenCrudDialog(
                                BatchCrudDialog.EditBatch(
                                    batchId = batch.id,
                                    initialName = batch.name,
                                    initialGoalTag = batch.goalTag,
                                    initialTargetLabel = batch.targetDateLabel,
                                    initialMood = batch.accentMood,
                                ),
                            )
                        },
                        onAddSubjectToBatchClick = { batch ->
                            onOpenCrudDialog(BatchCrudDialog.AddSubject(batchId = batch.id))
                        },
                        onDeleteBatchClick = { batch ->
                            onOpenCrudDialog(
                                BatchCrudDialog.ConfirmDeleteBatch(batchId = batch.id, batchName = batch.name),
                            )
                        },
                    )
                }

                // Right Subject, Chapter & Lecture Workspace (~68%)
                Box(
                    modifier = Modifier
                        .weight(0.68f)
                        .fillMaxHeight(),
                ) {
                    if (selectedBatch != null) {
                        BatchDetailCanvas(
                            batch = selectedBatch,
                            isPrimary = selectedBatch.id == primaryBatchId,
                            canDeleteBatch = canDeleteBatch,
                            expandedSubjectId = batchesState.expandedSubjectId,
                            expandedChapterId = batchesState.expandedChapterId,
                            onSetPrimaryBatch = { onSetPrimaryBatch(selectedBatch.id) },
                            onOpenCrudDialog = onOpenCrudDialog,
                            onToggleExpandSubject = onToggleExpandSubject,
                            onToggleExpandChapter = onToggleExpandChapter,
                            onToggleChapterCompleted = { subjectId, chapterId ->
                                onToggleChapterCompleted(selectedBatch.id, subjectId, chapterId)
                            },
                            onToggleLectureCompleted = { subjectId, chapterId, lectureId ->
                                onToggleLectureCompleted(selectedBatch.id, subjectId, chapterId, lectureId)
                            },
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = metrics.horizontalPadding,
                    vertical = metrics.verticalPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item(key = "batches_mobile_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.batches_title),
                                style = MaterialTheme.typography.headlineMedium,
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = stringResource(R.string.batches_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        SettingsTopBarGearButton(
                            testTag = "batches_header_settings_button",
                            onClick = onOpenSettings,
                        )
                    }
                }

                item(key = "batches_mobile_roster") {
                    PortraitBatchSelectorStrip(
                        batches = batchesState.batches,
                        selectedBatchId = batchesState.selectedBatchId,
                        primaryBatchId = primaryBatchId,
                        onSelectBatch = onSelectBatch,
                        onCreateBatchClick = onOpenBuilder,
                    )
                }

                if (selectedBatch != null) {
                    item(key = "batches_mobile_banner_${selectedBatch.id}") {
                        BatchOverviewBanner(
                            batch = selectedBatch,
                            isPrimary = selectedBatch.id == primaryBatchId,
                            canDeleteBatch = canDeleteBatch,
                            onSetPrimaryBatch = { onSetPrimaryBatch(selectedBatch.id) },
                            onAddSubject = { onOpenCrudDialog(BatchCrudDialog.AddSubject(selectedBatch.id)) },
                            onEditBatch = {
                                onOpenCrudDialog(
                                    BatchCrudDialog.EditBatch(
                                        batchId = selectedBatch.id,
                                        initialName = selectedBatch.name,
                                        initialGoalTag = selectedBatch.goalTag,
                                        initialTargetLabel = selectedBatch.targetDateLabel,
                                        initialMood = selectedBatch.accentMood,
                                    ),
                                )
                            },
                            onDeleteBatch = {
                                onOpenCrudDialog(
                                    BatchCrudDialog.ConfirmDeleteBatch(selectedBatch.id, selectedBatch.name),
                                )
                            },
                        )
                    }

                    itemsIndexed(
                        items = selectedBatch.subjects,
                        key = { _, subject -> subject.id },
                    ) { index, subject ->
                        StaggeredEntrance(delayMillis = index * 50L) {
                            BatchSubjectCard(
                                subject = subject,
                                expanded = subject.id == batchesState.expandedSubjectId,
                                expandedChapterId = batchesState.expandedChapterId,
                                ringDelayMillis = 70L + (index * 55L),
                                onToggleExpand = { onToggleExpandSubject(subject.id) },
                                onToggleChapterExpand = onToggleExpandChapter,
                                onToggleChapterComplete = { chapterId ->
                                    onToggleChapterCompleted(selectedBatch.id, subject.id, chapterId)
                                },
                                onEditSubject = {
                                    onOpenCrudDialog(
                                        BatchCrudDialog.EditSubject(
                                            batchId = selectedBatch.id,
                                            subjectId = subject.id,
                                            initialName = subject.name,
                                            initialMood = subject.accentMood,
                                        ),
                                    )
                                },
                                onDeleteSubject = {
                                    onOpenCrudDialog(
                                        BatchCrudDialog.ConfirmDeleteSubject(
                                            batchId = selectedBatch.id,
                                            subjectId = subject.id,
                                            subjectName = subject.name,
                                        ),
                                    )
                                },
                                onAddChapter = {
                                    onOpenCrudDialog(
                                        BatchCrudDialog.AddChapter(
                                            batchId = selectedBatch.id,
                                            subjectId = subject.id,
                                            subjectName = subject.name,
                                        ),
                                    )
                                },
                                onEditChapter = { ch ->
                                    onOpenCrudDialog(
                                        BatchCrudDialog.EditChapter(
                                            batchId = selectedBatch.id,
                                            subjectId = subject.id,
                                            chapterId = ch.id,
                                            initialTitle = ch.title,
                                        ),
                                    )
                                },
                                onDeleteChapter = { ch ->
                                    onOpenCrudDialog(
                                        BatchCrudDialog.ConfirmDeleteChapter(
                                            batchId = selectedBatch.id,
                                            subjectId = subject.id,
                                            chapterId = ch.id,
                                            chapterTitle = ch.title,
                                        ),
                                    )
                                },
                                onAddLecture = { ch ->
                                    onOpenCrudDialog(
                                        BatchCrudDialog.AddLecture(
                                            batchId = selectedBatch.id,
                                            subjectId = subject.id,
                                            chapterId = ch.id,
                                            chapterTitle = ch.title,
                                        ),
                                    )
                                },
                                onToggleLectureCompleted = { chapterId, lectureId ->
                                    onToggleLectureCompleted(selectedBatch.id, subject.id, chapterId, lectureId)
                                },
                                onEditLecture = { ch, lec ->
                                    onOpenCrudDialog(
                                        BatchCrudDialog.EditLecture(
                                            batchId = selectedBatch.id,
                                            subjectId = subject.id,
                                            chapterId = ch.id,
                                            lectureId = lec.id,
                                            initialTitle = lec.title,
                                            initialDuration = lec.durationLabel,
                                        ),
                                    )
                                },
                                onDeleteLecture = { ch, lec ->
                                    onOpenCrudDialog(
                                        BatchCrudDialog.ConfirmDeleteLecture(
                                            batchId = selectedBatch.id,
                                            subjectId = subject.id,
                                            chapterId = ch.id,
                                            lectureId = lec.id,
                                            lectureTitle = lec.title,
                                        ),
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }

        // Slide-in Custom Batch Builder Sheet
        CreateBatchBuilderSheet(
            visible = batchesState.isBuilderOpen,
            isWideLayout = isWideLayout,
            defaultAccent = activeThemeMood,
            onDismiss = onCloseBuilder,
            onCreateBatch = onCreateBatch,
        )

        // Full Batches / Subjects / Chapters / Lectures CRUD Dialogs
        BatchCrudDialogHost(
            activeDialog = batchesState.activeCrudDialog,
            onDismiss = onCloseCrudDialog,
            onEditBatch = onEditBatch,
            onDeleteBatch = onDeleteBatch,
            onAddSubject = onAddSubject,
            onEditSubject = onEditSubject,
            onDeleteSubject = onDeleteSubject,
            onAddChapter = onAddChapter,
            onEditChapter = onEditChapter,
            onDeleteChapter = onDeleteChapter,
            onAddLecture = onAddLecture,
            onEditLecture = onEditLecture,
            onDeleteLecture = onDeleteLecture,
        )
    }
}

@Composable
private fun BatchDetailCanvas(
    batch: StudyBatch,
    isPrimary: Boolean,
    canDeleteBatch: Boolean,
    expandedSubjectId: String?,
    expandedChapterId: String?,
    onSetPrimaryBatch: () -> Unit,
    onOpenCrudDialog: (BatchCrudDialog) -> Unit,
    onToggleExpandSubject: (String) -> Unit,
    onToggleExpandChapter: (String) -> Unit,
    onToggleChapterCompleted: (subjectId: String, chapterId: String) -> Unit,
    onToggleLectureCompleted: (subjectId: String, chapterId: String, lectureId: String) -> Unit,
) {
    AnimatedContent(
        targetState = batch,
        contentKey = { it.id },
        transitionSpec = {
            (fadeIn(animationSpec = tween(200)) +
                slideInVertically(
                    initialOffsetY = { it / 22 },
                    animationSpec = spring(dampingRatio = 0.86f, stiffness = 380f),
                )) togetherWith fadeOut(animationSpec = tween(130))
        },
        label = "batchDetailSwitch",
    ) { activeBatch ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 20.dp),
        ) {
            item(key = "overview_${activeBatch.id}") {
                BatchOverviewBanner(
                    batch = activeBatch,
                    isPrimary = isPrimary,
                    canDeleteBatch = canDeleteBatch,
                    onSetPrimaryBatch = onSetPrimaryBatch,
                    onAddSubject = { onOpenCrudDialog(BatchCrudDialog.AddSubject(activeBatch.id)) },
                    onEditBatch = {
                        onOpenCrudDialog(
                            BatchCrudDialog.EditBatch(
                                batchId = activeBatch.id,
                                initialName = activeBatch.name,
                                initialGoalTag = activeBatch.goalTag,
                                initialTargetLabel = activeBatch.targetDateLabel,
                                initialMood = activeBatch.accentMood,
                            ),
                        )
                    },
                    onDeleteBatch = {
                        onOpenCrudDialog(
                            BatchCrudDialog.ConfirmDeleteBatch(activeBatch.id, activeBatch.name),
                        )
                    },
                )
            }

            itemsIndexed(
                items = activeBatch.subjects,
                key = { _, subject -> subject.id },
            ) { index, subject ->
                StaggeredEntrance(delayMillis = 40L * index) {
                    BatchSubjectCard(
                        subject = subject,
                        expanded = subject.id == expandedSubjectId,
                        expandedChapterId = expandedChapterId,
                        ringDelayMillis = 60L + (index * 50L),
                        onToggleExpand = { onToggleExpandSubject(subject.id) },
                        onToggleChapterExpand = onToggleExpandChapter,
                        onToggleChapterComplete = { chapterId ->
                            onToggleChapterCompleted(subject.id, chapterId)
                        },
                        onEditSubject = {
                            onOpenCrudDialog(
                                BatchCrudDialog.EditSubject(
                                    batchId = activeBatch.id,
                                    subjectId = subject.id,
                                    initialName = subject.name,
                                    initialMood = subject.accentMood,
                                ),
                            )
                        },
                        onDeleteSubject = {
                            onOpenCrudDialog(
                                BatchCrudDialog.ConfirmDeleteSubject(
                                    batchId = activeBatch.id,
                                    subjectId = subject.id,
                                    subjectName = subject.name,
                                ),
                            )
                        },
                        onAddChapter = {
                            onOpenCrudDialog(
                                BatchCrudDialog.AddChapter(
                                    batchId = activeBatch.id,
                                    subjectId = subject.id,
                                    subjectName = subject.name,
                                ),
                            )
                        },
                        onEditChapter = { ch ->
                            onOpenCrudDialog(
                                BatchCrudDialog.EditChapter(
                                    batchId = activeBatch.id,
                                    subjectId = subject.id,
                                    chapterId = ch.id,
                                    initialTitle = ch.title,
                                ),
                            )
                        },
                        onDeleteChapter = { ch ->
                            onOpenCrudDialog(
                                BatchCrudDialog.ConfirmDeleteChapter(
                                    batchId = activeBatch.id,
                                    subjectId = subject.id,
                                    chapterId = ch.id,
                                    chapterTitle = ch.title,
                                ),
                            )
                        },
                        onAddLecture = { ch ->
                            onOpenCrudDialog(
                                BatchCrudDialog.AddLecture(
                                    batchId = activeBatch.id,
                                    subjectId = subject.id,
                                    chapterId = ch.id,
                                    chapterTitle = ch.title,
                                ),
                            )
                        },
                        onToggleLectureCompleted = { chapterId, lectureId ->
                            onToggleLectureCompleted(subject.id, chapterId, lectureId)
                        },
                        onEditLecture = { ch, lec ->
                            onOpenCrudDialog(
                                BatchCrudDialog.EditLecture(
                                    batchId = activeBatch.id,
                                    subjectId = subject.id,
                                    chapterId = ch.id,
                                    lectureId = lec.id,
                                    initialTitle = lec.title,
                                    initialDuration = lec.durationLabel,
                                ),
                            )
                        },
                        onDeleteLecture = { ch, lec ->
                            onOpenCrudDialog(
                                BatchCrudDialog.ConfirmDeleteLecture(
                                    batchId = activeBatch.id,
                                    subjectId = subject.id,
                                    chapterId = ch.id,
                                    lectureId = lec.id,
                                    lectureTitle = lec.title,
                                ),
                            )
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BatchOverviewBanner(
    batch: StudyBatch,
    isPrimary: Boolean,
    canDeleteBatch: Boolean,
    onSetPrimaryBatch: () -> Unit,
    onAddSubject: () -> Unit,
    onEditBatch: () -> Unit,
    onDeleteBatch: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val primaryInteraction = remember { MutableInteractionSource() }
    var bannerMenuOpen by remember { mutableStateOf(false) }

    ObsidianCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("batch_overview_banner_${batch.id}"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(batch.accentMood.accent),
                        )
                        Text(
                            text = batch.goalTag.uppercase(),
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (isPrimary) tokens.slateElevated else tokens.carbonSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isPrimary) tokens.inkPrimary.copy(alpha = 0.28f) else tokens.hairlineRest,
                            ),
                            modifier = Modifier
                                .testTag("batch_set_primary_button_${batch.id}")
                                .minimumInteractiveComponentSize()
                                .tactileScale(primaryInteraction, pressedScale = 0.95f)
                                .clip(RoundedCornerShape(100.dp))
                                .clickable(
                                    enabled = !isPrimary,
                                    interactionSource = primaryInteraction,
                                    indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.14f)),
                                    role = Role.Button,
                                    onClick = onSetPrimaryBatch,
                                ),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    imageVector = if (isPrimary) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                                    contentDescription = null,
                                    tint = if (isPrimary) tokens.inkPrimary else tokens.inkSecondary,
                                    modifier = Modifier.size(13.dp),
                                )
                                Text(
                                    text = if (isPrimary) {
                                        stringResource(R.string.batches_primary_badge)
                                    } else {
                                        stringResource(R.string.batches_set_primary_cta)
                                    },
                                    style = MonoBadgeStyle,
                                    color = if (isPrimary) tokens.inkPrimary else tokens.inkSecondary,
                                )
                            }
                        }
                    }
                    Text(
                        text = batch.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = tokens.inkPrimary,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        KineticNumberText(
                            targetValue = batch.overallProgressPercent,
                            suffix = "%",
                            style = MonoMetricMedium,
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = "Coverage",
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.inkMuted,
                        )
                    }
                    Box {
                        IconButton(
                            onClick = { bannerMenuOpen = true },
                            modifier = Modifier.testTag("batch_banner_menu_${batch.id}"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = "Batch actions",
                                tint = tokens.inkSecondary,
                            )
                        }
                        DropdownMenu(
                            expanded = bannerMenuOpen,
                            onDismissRequest = { bannerMenuOpen = false },
                            containerColor = tokens.carbonSurface,
                            border = BorderStroke(1.dp, tokens.hairlineRest),
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.batches_add_subject_cta), color = tokens.inkPrimary) },
                                leadingIcon = { Icon(Icons.Rounded.Add, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp)) },
                                onClick = {
                                    bannerMenuOpen = false
                                    onAddSubject()
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.batches_action_edit), color = tokens.inkPrimary) },
                                leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp)) },
                                onClick = {
                                    bannerMenuOpen = false
                                    onEditBatch()
                                },
                            )
                            if (canDeleteBatch) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.batches_action_delete), color = MaterialTheme.colorScheme.error) },
                                    leadingIcon = { Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) },
                                    onClick = {
                                        bannerMenuOpen = false
                                        onDeleteBatch()
                                    },
                                )
                            }
                        }
                    }
                }
            }

            // Bottom quick action strip: Add Subject + synced info (wraps cleanly on portrait)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "${batch.subjects.size} subjects · ${batch.totalChapters} chapters · Synced with Vault",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                InlineAddActionChip(
                    label = stringResource(R.string.batches_add_subject_cta),
                    testTag = "batch_banner_add_subject_${batch.id}",
                    onClick = onAddSubject,
                )
            }
        }
    }
}
