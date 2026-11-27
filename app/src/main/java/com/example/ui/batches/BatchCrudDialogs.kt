package com.example.ui.batches

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.BatchCrudDialog
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono

@Composable
fun BatchCrudDialogHost(
    activeDialog: BatchCrudDialog?,
    onDismiss: () -> Unit,
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
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = activeDialog != null, onBack = onDismiss)

    val tokens = GhickinTokens.colors
    val backdropInteraction = remember { MutableInteractionSource() }

    AnimatedVisibility(
        visible = activeDialog != null,
        enter = fadeIn(tween(160)),
        exit = fadeOut(tween(140)),
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.78f))
                .clickable(
                    interactionSource = backdropInteraction,
                    indication = null,
                    onClick = onDismiss,
                )
                .padding(horizontal = 20.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (activeDialog != null) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(180)) + scaleIn(
                        initialScale = 0.95f,
                        animationSpec = spring(dampingRatio = 0.86f, stiffness = 420f),
                    ),
                    exit = fadeOut(tween(130)) + scaleOut(targetScale = 0.96f),
                ) {
                    val cardInteraction = remember { MutableInteractionSource() }
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = tokens.obsidianSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 500.dp)
                            .testTag("batch_crud_dialog")
                            .clickable(
                                interactionSource = cardInteraction,
                                indication = null,
                                onClick = {},
                            ),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            when (activeDialog) {
                                is BatchCrudDialog.EditBatch -> EditBatchContent(
                                    dialog = activeDialog,
                                    onDismiss = onDismiss,
                                    onSave = { name, goal, target, mood ->
                                        onEditBatch(activeDialog.batchId, name, goal, target, mood)
                                    },
                                )

                                is BatchCrudDialog.ConfirmDeleteBatch -> ConfirmDeleteContent(
                                    title = stringResource(R.string.batches_delete_batch_title),
                                    itemName = activeDialog.batchName,
                                    detail = "All subjects, chapters, and lectures inside this batch will be removed from Batches and Vault.",
                                    onDismiss = onDismiss,
                                    onConfirm = { onDeleteBatch(activeDialog.batchId) },
                                )

                                is BatchCrudDialog.AddSubject -> SubjectFormContent(
                                    title = stringResource(R.string.batches_add_subject_title),
                                    initialName = "",
                                    initialMood = AccentMood.CYAN,
                                    confirmLabel = stringResource(R.string.batches_dialog_add),
                                    onDismiss = onDismiss,
                                    onSave = { name, mood -> onAddSubject(activeDialog.batchId, name, mood) },
                                )

                                is BatchCrudDialog.EditSubject -> SubjectFormContent(
                                    title = stringResource(R.string.batches_edit_subject_title),
                                    initialName = activeDialog.initialName,
                                    initialMood = activeDialog.initialMood,
                                    confirmLabel = stringResource(R.string.batches_dialog_save),
                                    onDismiss = onDismiss,
                                    onSave = { name, mood ->
                                        onEditSubject(activeDialog.batchId, activeDialog.subjectId, name, mood)
                                    },
                                )

                                is BatchCrudDialog.ConfirmDeleteSubject -> ConfirmDeleteContent(
                                    title = stringResource(R.string.batches_delete_subject_title),
                                    itemName = activeDialog.subjectName,
                                    detail = "All chapters and lectures under this subject will also be removed from Vault.",
                                    onDismiss = onDismiss,
                                    onConfirm = { onDeleteSubject(activeDialog.batchId, activeDialog.subjectId) },
                                )

                                is BatchCrudDialog.AddChapter -> ChapterFormContent(
                                    title = stringResource(R.string.batches_add_chapter_title),
                                    subtitle = "Subject: ${activeDialog.subjectName}",
                                    initialTitle = "",
                                    confirmLabel = stringResource(R.string.batches_dialog_add),
                                    onDismiss = onDismiss,
                                    onSave = { chTitle ->
                                        onAddChapter(activeDialog.batchId, activeDialog.subjectId, chTitle)
                                    },
                                )

                                is BatchCrudDialog.EditChapter -> ChapterFormContent(
                                    title = stringResource(R.string.batches_edit_chapter_title),
                                    subtitle = "Rename chapter across Batches & Vault",
                                    initialTitle = activeDialog.initialTitle,
                                    confirmLabel = stringResource(R.string.batches_dialog_save),
                                    onDismiss = onDismiss,
                                    onSave = { chTitle ->
                                        onEditChapter(
                                            activeDialog.batchId,
                                            activeDialog.subjectId,
                                            activeDialog.chapterId,
                                            chTitle,
                                        )
                                    },
                                )

                                is BatchCrudDialog.ConfirmDeleteChapter -> ConfirmDeleteContent(
                                    title = stringResource(R.string.batches_delete_chapter_title),
                                    itemName = activeDialog.chapterTitle,
                                    detail = "All lectures inside this chapter will be removed from Batches and Vault.",
                                    onDismiss = onDismiss,
                                    onConfirm = {
                                        onDeleteChapter(activeDialog.batchId, activeDialog.subjectId, activeDialog.chapterId)
                                    },
                                )

                                is BatchCrudDialog.AddLecture -> LectureFormContent(
                                    title = stringResource(R.string.batches_add_lecture_title),
                                    subtitle = "Chapter: ${activeDialog.chapterTitle}",
                                    initialTitle = "",
                                    initialDuration = "1h 10m",
                                    confirmLabel = stringResource(R.string.batches_dialog_add),
                                    onDismiss = onDismiss,
                                    onSave = { lecTitle, dur ->
                                        onAddLecture(
                                            activeDialog.batchId,
                                            activeDialog.subjectId,
                                            activeDialog.chapterId,
                                            lecTitle,
                                            dur,
                                        )
                                    },
                                )

                                is BatchCrudDialog.EditLecture -> LectureFormContent(
                                    title = stringResource(R.string.batches_edit_lecture_title),
                                    subtitle = "Update custom lecture name & duration",
                                    initialTitle = activeDialog.initialTitle,
                                    initialDuration = activeDialog.initialDuration,
                                    confirmLabel = stringResource(R.string.batches_dialog_save),
                                    onDismiss = onDismiss,
                                    onSave = { lecTitle, dur ->
                                        onEditLecture(
                                            activeDialog.batchId,
                                            activeDialog.subjectId,
                                            activeDialog.chapterId,
                                            activeDialog.lectureId,
                                            lecTitle,
                                            dur,
                                        )
                                    },
                                )

                                is BatchCrudDialog.ConfirmDeleteLecture -> ConfirmDeleteContent(
                                    title = stringResource(R.string.batches_delete_lecture_title),
                                    itemName = activeDialog.lectureTitle,
                                    detail = "This lecture and its linked notes, flashcards, and revision sheet will be removed.",
                                    onDismiss = onDismiss,
                                    onConfirm = {
                                        onDeleteLecture(
                                            activeDialog.batchId,
                                            activeDialog.subjectId,
                                            activeDialog.chapterId,
                                            activeDialog.lectureId,
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogHeaderRow(
    title: String,
    subtitle: String? = null,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = tokens.inkPrimary,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
            }
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("crud_dialog_close"),
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close dialog",
                tint = tokens.inkSecondary,
            )
        }
    }
}

@Composable
private fun EditBatchContent(
    dialog: BatchCrudDialog.EditBatch,
    onDismiss: () -> Unit,
    onSave: (String, String, String, AccentMood) -> Unit,
) {
    var name by remember(dialog) { mutableStateOf(dialog.initialName) }
    var goal by remember(dialog) { mutableStateOf(dialog.initialGoalTag) }
    var target by remember(dialog) { mutableStateOf(dialog.initialTargetLabel) }
    var mood by remember(dialog) { mutableStateOf(dialog.initialMood) }

    DialogHeaderRow(title = stringResource(R.string.batches_edit_batch_title), onDismiss = onDismiss)

    GrokMonoTextField(
        value = name,
        onValueChange = { name = it },
        label = "Batch Name",
        placeholder = "e.g., JEE Advanced 2026",
        testTag = "crud_batch_name_input",
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        GrokMonoTextField(
            value = goal,
            onValueChange = { goal = it },
            label = "Track Tag",
            placeholder = "e.g., Target 99.5%ile",
            testTag = "crud_batch_goal_input",
            modifier = Modifier.weight(1f),
        )
        GrokMonoTextField(
            value = target,
            onValueChange = { target = it },
            label = "Schedule Label",
            placeholder = "e.g., May 2026",
            testTag = "crud_batch_target_input",
            modifier = Modifier.weight(1f),
        )
    }

    SubtleDotPicker(selectedMood = mood, onSelect = { mood = it })

    DialogFooterActions(
        confirmLabel = stringResource(R.string.batches_dialog_save),
        confirmEnabled = name.trim().isNotEmpty(),
        isDestructive = false,
        onCancel = onDismiss,
        onConfirm = { onSave(name, goal, target, mood) },
    )
}

@Composable
private fun SubjectFormContent(
    title: String,
    initialName: String,
    initialMood: AccentMood,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onSave: (String, AccentMood) -> Unit,
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var mood by remember(initialMood) { mutableStateOf(initialMood) }

    DialogHeaderRow(title = title, subtitle = "Synced live across Home, Batches & Vault", onDismiss = onDismiss)

    GrokMonoTextField(
        value = name,
        onValueChange = { name = it },
        label = "Subject Name",
        placeholder = "e.g., Inorganic Chemistry",
        testTag = "crud_subject_name_input",
    )

    SubtleDotPicker(selectedMood = mood, onSelect = { mood = it })

    DialogFooterActions(
        confirmLabel = confirmLabel,
        confirmEnabled = name.trim().isNotEmpty(),
        isDestructive = false,
        onCancel = onDismiss,
        onConfirm = { onSave(name, mood) },
    )
}

@Composable
private fun ChapterFormContent(
    title: String,
    subtitle: String,
    initialTitle: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var chapterTitle by remember(initialTitle) { mutableStateOf(initialTitle) }

    DialogHeaderRow(title = title, subtitle = subtitle, onDismiss = onDismiss)

    GrokMonoTextField(
        value = chapterTitle,
        onValueChange = { chapterTitle = it },
        label = "Chapter Title",
        placeholder = "e.g., Rotational Mechanics",
        testTag = "crud_chapter_title_input",
    )

    DialogFooterActions(
        confirmLabel = confirmLabel,
        confirmEnabled = chapterTitle.trim().isNotEmpty(),
        isDestructive = false,
        onCancel = onDismiss,
        onConfirm = { onSave(chapterTitle) },
    )
}

@Composable
private fun LectureFormContent(
    title: String,
    subtitle: String,
    initialTitle: String,
    initialDuration: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var lecTitle by remember(initialTitle) { mutableStateOf(initialTitle) }
    var duration by remember(initialDuration) { mutableStateOf(initialDuration) }

    DialogHeaderRow(title = title, subtitle = subtitle, onDismiss = onDismiss)

    GrokMonoTextField(
        value = lecTitle,
        onValueChange = { lecTitle = it },
        label = "Custom Lecture Name",
        placeholder = "e.g., Gauss Law & Spherical Shells",
        testTag = "crud_lecture_title_input",
    )

    GrokMonoTextField(
        value = duration,
        onValueChange = { duration = it },
        label = "Duration",
        placeholder = "e.g., 1h 15m",
        testTag = "crud_lecture_duration_input",
    )

    DialogFooterActions(
        confirmLabel = confirmLabel,
        confirmEnabled = lecTitle.trim().isNotEmpty(),
        isDestructive = false,
        onCancel = onDismiss,
        onConfirm = { onSave(lecTitle, duration) },
    )
}

@Composable
private fun ConfirmDeleteContent(
    title: String,
    itemName: String,
    detail: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    DialogHeaderRow(title = title, onDismiss = onDismiss)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = itemName,
                style = MaterialTheme.typography.titleMedium,
                color = tokens.inkPrimary,
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
            )
        }
    }

    DialogFooterActions(
        confirmLabel = stringResource(R.string.batches_dialog_delete_confirm),
        confirmEnabled = true,
        isDestructive = true,
        onCancel = onDismiss,
        onConfirm = onConfirm,
    )
}

@Composable
private fun GrokMonoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    testTag: String,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label.uppercase(),
            style = MicroTagMono,
            color = tokens.inkMuted,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.inkMuted,
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = tokens.carbonSurface,
                unfocusedContainerColor = tokens.carbonSurface,
                focusedBorderColor = tokens.inkPrimary.copy(alpha = 0.55f),
                unfocusedBorderColor = tokens.hairlineRest,
                focusedTextColor = tokens.inkPrimary,
                unfocusedTextColor = tokens.inkPrimary,
                cursorColor = tokens.inkPrimary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
        )
    }
}

@Composable
private fun SubtleDotPicker(
    selectedMood: AccentMood,
    onSelect: (AccentMood) -> Unit,
) {
    val tokens = GhickinTokens.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "SUBTLE INDICATOR DOT",
            style = MicroTagMono,
            color = tokens.inkMuted,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AccentMood.entries.forEach { mood ->
                val selected = mood == selectedMood
                val interaction = remember { MutableInteractionSource() }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("crud_dot_${mood.id}")
                        .tactileScale(interaction, 0.9f)
                        .clip(CircleShape)
                        .background(if (selected) tokens.slateElevated else tokens.carbonSurface)
                        .border(
                            width = 1.dp,
                            color = if (selected) tokens.inkPrimary.copy(alpha = 0.65f) else tokens.hairlineSubtle,
                            shape = CircleShape,
                        )
                        .clickable(
                            interactionSource = interaction,
                            indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.14f)),
                            role = Role.RadioButton,
                            onClick = { onSelect(mood) },
                        ),
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(mood.accent),
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogFooterActions(
    confirmLabel: String,
    confirmEnabled: Boolean,
    isDestructive: Boolean,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val cancelInteraction = remember { MutableInteractionSource() }
    val confirmInteraction = remember { MutableInteractionSource() }

    val confirmBg = when {
        !confirmEnabled -> tokens.slateElevated
        isDestructive -> MaterialTheme.colorScheme.error
        else -> tokens.primaryCta
    }
    val confirmInk = when {
        !confirmEnabled -> tokens.inkMuted
        isDestructive -> Color.Black
        else -> tokens.onPrimaryCta
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .testTag("crud_dialog_cancel")
                .minimumInteractiveComponentSize()
                .tactileScale(cancelInteraction, 0.96f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = cancelInteraction,
                    indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.12f)),
                    role = Role.Button,
                    onClick = onCancel,
                ),
        ) {
            Text(
                text = stringResource(R.string.batches_dialog_cancel),
                style = MaterialTheme.typography.labelLarge,
                color = tokens.inkSecondary,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = confirmBg,
            modifier = Modifier
                .testTag("crud_dialog_confirm")
                .minimumInteractiveComponentSize()
                .tactileScale(confirmInteraction, 0.96f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    enabled = confirmEnabled,
                    interactionSource = confirmInteraction,
                    indication = ripple(color = Color.Black.copy(alpha = 0.18f)),
                    role = Role.Button,
                    onClick = onConfirm,
                ),
        ) {
            Text(
                text = confirmLabel,
                style = MaterialTheme.typography.labelLarge,
                color = confirmInk,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            )
        }
    }
}
