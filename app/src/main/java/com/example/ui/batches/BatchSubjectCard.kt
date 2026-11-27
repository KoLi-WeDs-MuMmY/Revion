package com.example.ui.batches

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.BatchChapter
import com.example.model.BatchSubject
import com.example.model.VaultLectureNode
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.ObsidianCard
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle

@Composable
fun BatchSubjectCard(
    subject: BatchSubject,
    expanded: Boolean,
    expandedChapterId: String?,
    ringDelayMillis: Long,
    onToggleExpand: () -> Unit,
    onToggleChapterExpand: (String) -> Unit,
    onToggleChapterComplete: (String) -> Unit,
    onEditSubject: () -> Unit,
    onDeleteSubject: () -> Unit,
    onAddChapter: () -> Unit,
    onEditChapter: (BatchChapter) -> Unit,
    onDeleteChapter: (BatchChapter) -> Unit,
    onAddLecture: (BatchChapter) -> Unit,
    onToggleLectureCompleted: (chapterId: String, lectureId: String) -> Unit,
    onEditLecture: (BatchChapter, VaultLectureNode) -> Unit,
    onDeleteLecture: (BatchChapter, VaultLectureNode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val dotAccent = subject.accentMood.accent
    var subjectMenuOpen by remember { mutableStateOf(false) }

    val edgeColor by animateColorAsState(
        targetValue = if (expanded) tokens.inkPrimary.copy(alpha = 0.22f) else tokens.hairlineRest,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 360f),
        label = "subjectEdge_${subject.id}",
    )
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f),
        label = "subjectChevron_${subject.id}",
    )

    ObsidianCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("batch_subject_card_${subject.id}")
            .animateContentSize(spring(dampingRatio = 0.84f, stiffness = 360f)),
        containerColor = tokens.obsidianSurface,
        accentEdgeColor = edgeColor,
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(16.dp),
        onClick = onToggleExpand,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Top subject summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Quiet 7dp subject dot indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotAccent),
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = tokens.carbonSurface,
                            border = BorderStroke(1.dp, tokens.hairlineSubtle),
                        ) {
                            Text(
                                text = "${subject.chapters.size} CH · ${subject.lectureCount} LEC",
                                style = MicroTagMono,
                                color = tokens.inkSecondary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        AnimatedLinearProgressBar(
                            progressPercent = subject.masteryPercent,
                            accentColor = dotAccent,
                            height = 4.dp,
                            delayMillis = ringDelayMillis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "${subject.masteryPercent}%",
                            style = MonoBadgeStyle,
                            color = tokens.inkSecondary,
                        )
                    }
                }

                // Inline 3-dot menu for Subject CRUD
                Box {
                    IconButton(
                        onClick = { subjectMenuOpen = true },
                        modifier = Modifier.testTag("subject_menu_${subject.id}"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "Subject options for ${subject.name}",
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(19.dp),
                        )
                    }
                    DropdownMenu(
                        expanded = subjectMenuOpen,
                        onDismissRequest = { subjectMenuOpen = false },
                        containerColor = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.batches_add_chapter_cta), color = tokens.inkPrimary) },
                            leadingIcon = { Icon(Icons.Rounded.Add, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                subjectMenuOpen = false
                                onAddChapter()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.batches_action_edit), color = tokens.inkPrimary) },
                            leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                subjectMenuOpen = false
                                onEditSubject()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.batches_action_delete), color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                subjectMenuOpen = false
                                onDeleteSubject()
                            },
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = if (expanded) "Collapse chapters" else "Expand chapters",
                    tint = if (expanded) tokens.inkPrimary else tokens.inkMuted,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(chevronRotation),
                )
            }

            // Expanded chapters + expandable nested custom lectures
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(spring(stiffness = 380f)) +
                    expandVertically(spring(dampingRatio = 0.84f, stiffness = 360f)),
                exit = fadeOut(spring(stiffness = 420f)) +
                    shrinkVertically(spring(dampingRatio = 0.88f, stiffness = 420f)),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(9.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(tokens.hairlineSubtle),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "${stringResource(R.string.batches_chapters_label).uppercase()} (${subject.chapters.size})",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                        InlineAddActionChip(
                            label = stringResource(R.string.batches_add_chapter_cta),
                            testTag = "add_chapter_btn_${subject.id}",
                            onClick = onAddChapter,
                        )
                    }

                    if (subject.chapters.isEmpty()) {
                        Text(
                            text = "No chapters yet. Tap Add Chapter to structure lectures.",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkMuted,
                            modifier = Modifier.padding(vertical = 6.dp),
                        )
                    } else {
                        subject.chapters.forEachIndexed { chIdx, chapter ->
                            ChapterWithLecturesBlock(
                                index = chIdx + 1,
                                chapter = chapter,
                                dotAccent = dotAccent,
                                expanded = chapter.id == expandedChapterId,
                                onToggleExpand = { onToggleChapterExpand(chapter.id) },
                                onToggleComplete = { onToggleChapterComplete(chapter.id) },
                                onEditChapter = { onEditChapter(chapter) },
                                onDeleteChapter = { onDeleteChapter(chapter) },
                                onAddLecture = { onAddLecture(chapter) },
                                onToggleLectureCompleted = { lecId -> onToggleLectureCompleted(chapter.id, lecId) },
                                onEditLecture = { lec -> onEditLecture(chapter, lec) },
                                onDeleteLecture = { lec -> onDeleteLecture(chapter, lec) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterWithLecturesBlock(
    index: Int,
    chapter: BatchChapter,
    dotAccent: Color,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleComplete: () -> Unit,
    onEditChapter: () -> Unit,
    onDeleteChapter: () -> Unit,
    onAddLecture: () -> Unit,
    onToggleLectureCompleted: (String) -> Unit,
    onEditLecture: (VaultLectureNode) -> Unit,
    onDeleteLecture: (VaultLectureNode) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(14.dp)
    var chapterMenuOpen by remember { mutableStateOf(false) }

    val rowBorder by animateColorAsState(
        targetValue = if (expanded) tokens.inkPrimary.copy(alpha = 0.22f) else tokens.hairlineSubtle,
        animationSpec = spring(stiffness = 420f),
        label = "chapterBorder_${chapter.id}",
    )

    Surface(
        shape = shape,
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, rowBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chapter_row_${chapter.id}")
            .animateContentSize(spring(dampingRatio = 0.85f, stiffness = 380f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Chapter header row (tap to expand/collapse lectures)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.12f)),
                        role = Role.Button,
                        onClick = onToggleExpand,
                    )
                    .padding(start = 10.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(
                    onClick = onToggleComplete,
                    modifier = Modifier.testTag("chapter_check_${chapter.id}"),
                ) {
                    Icon(
                        imageVector = if (chapter.isCompleted) {
                            Icons.Rounded.CheckCircle
                        } else {
                            Icons.Rounded.RadioButtonUnchecked
                        },
                        contentDescription = if (chapter.isCompleted) "Chapter completed" else "Mark chapter completed",
                        tint = if (chapter.isCompleted) AccentMood.EMERALD.accent else tokens.inkMuted,
                        modifier = Modifier.size(19.dp),
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = "CH ${index.toString().padStart(2, '0')} · ${chapter.title}",
                        style = MaterialTheme.typography.titleSmall,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "${chapter.lectures.size} lectures · ${chapter.progressPercent}%",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                        AnimatedLinearProgressBar(
                            progressPercent = chapter.progressPercent,
                            accentColor = dotAccent,
                            height = 3.dp,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                // Inline 3-dot menu for Chapter CRUD
                Box {
                    IconButton(
                        onClick = { chapterMenuOpen = true },
                        modifier = Modifier.testTag("chapter_menu_${chapter.id}"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "Chapter options for ${chapter.title}",
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    DropdownMenu(
                        expanded = chapterMenuOpen,
                        onDismissRequest = { chapterMenuOpen = false },
                        containerColor = tokens.obsidianSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.batches_add_lecture_cta), color = tokens.inkPrimary) },
                            leadingIcon = { Icon(Icons.Rounded.Add, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                chapterMenuOpen = false
                                onAddLecture()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.batches_action_edit), color = tokens.inkPrimary) },
                            leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                chapterMenuOpen = false
                                onEditChapter()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.batches_action_delete), color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                chapterMenuOpen = false
                                onDeleteChapter()
                            },
                        )
                    }
                }
            }

            // Expanded nested Lectures list with custom Add/Rename/Delete
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 12.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(tokens.hairlineSubtle),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "LECTURES (${chapter.lectures.size})",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                        InlineAddActionChip(
                            label = stringResource(R.string.batches_add_lecture_cta),
                            testTag = "add_lecture_btn_${chapter.id}",
                            onClick = onAddLecture,
                        )
                    }

                    if (chapter.lectures.isEmpty()) {
                        Text(
                            text = "No lectures in this chapter yet. Tap Add Lecture.",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkMuted,
                        )
                    } else {
                        chapter.lectures.forEach { lecture ->
                            LectureManageRow(
                                lecture = lecture,
                                onToggleComplete = { onToggleLectureCompleted(lecture.id) },
                                onEdit = { onEditLecture(lecture) },
                                onDelete = { onDeleteLecture(lecture) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LectureManageRow(
    lecture: VaultLectureNode,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    var lectureMenuOpen by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(11.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("batch_lecture_row_${lecture.id}"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 4.dp, top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.testTag("batch_lecture_toggle_${lecture.id}"),
            ) {
                Icon(
                    imageVector = if (lecture.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = if (lecture.isCompleted) "Lecture completed" else "Mark lecture completed",
                    tint = if (lecture.isCompleted) AccentMood.EMERALD.accent else tokens.inkMuted,
                    modifier = Modifier.size(17.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${lecture.lectureCode} · ${lecture.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${lecture.durationLabel} · ${lecture.dateLabel}",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
            }

            Box {
                IconButton(
                    onClick = { lectureMenuOpen = true },
                    modifier = Modifier.testTag("lecture_menu_${lecture.id}"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Lecture options for ${lecture.title}",
                        tint = tokens.inkMuted,
                        modifier = Modifier.size(17.dp),
                    )
                }
                DropdownMenu(
                    expanded = lectureMenuOpen,
                    onDismissRequest = { lectureMenuOpen = false },
                    containerColor = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.batches_action_edit), color = tokens.inkPrimary) },
                        leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(17.dp)) },
                        onClick = {
                            lectureMenuOpen = false
                            onEdit()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.batches_action_delete), color = MaterialTheme.colorScheme.error) },
                        leadingIcon = { Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(17.dp)) },
                        onClick = {
                            lectureMenuOpen = false
                            onDelete()
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun InlineAddActionChip(
    label: String,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = tokens.slateElevated,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier
            .testTag(testTag)
            .tactileScale(interaction, 0.95f)
            .clip(RoundedCornerShape(9.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.14f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = tokens.inkPrimary,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = tokens.inkPrimary,
            )
        }
    }
}
