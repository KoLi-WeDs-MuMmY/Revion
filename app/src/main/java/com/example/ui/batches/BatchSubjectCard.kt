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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.AnimatedMasteryRing
import com.example.ui.components.ObsidianCard
import com.example.ui.components.tactileScale
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle

@Composable
fun BatchSubjectCard(
    subject: BatchSubject,
    expanded: Boolean,
    ringDelayMillis: Long,
    onToggleExpand: () -> Unit,
    onToggleChapter: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val accent = subject.accentMood.accent

    val edgeColor by animateColorAsState(
        targetValue = if (expanded) accent.copy(alpha = 0.52f) else subject.accentMood.hairlineTint,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 360f),
        label = "subjectEdge_${subject.id}",
    )
    val washColor by animateColorAsState(
        targetValue = if (expanded) subject.accentMood.surfaceWash else Color.Transparent,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 360f),
        label = "subjectWash_${subject.id}",
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
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = 0.84f,
                    stiffness = 360f,
                ),
            ),
        containerColor = tokens.obsidianSurface,
        accentEdgeColor = edgeColor,
        surfaceWashColor = washColor,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(18.dp),
        onClick = onToggleExpand,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Top subject summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(accent),
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = subject.accentMood.container,
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
                        ) {
                            Text(
                                text = "${subject.chapters.size} CH",
                                style = MonoBadgeStyle,
                                color = subject.accentMood.onContainer,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            )
                        }
                    }

                    Text(
                        text = "${subject.completedLectures}/${subject.lectureCount} lectures completed · ${subject.chapters.count { it.isCompleted }}/${subject.chapters.size} chapters mastered",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    AnimatedLinearProgressBar(
                        progressPercent = subject.masteryPercent,
                        accentColor = accent,
                        height = 5.dp,
                        delayMillis = ringDelayMillis,
                    )
                }

                AnimatedMasteryRing(
                    percentage = subject.masteryPercent,
                    accentColor = accent,
                    ringSize = 62.dp,
                    strokeWidth = 5.dp,
                    delayMillis = ringDelayMillis,
                )

                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = if (expanded) "Collapse chapters" else "Expand chapters",
                    tint = if (expanded) accent else tokens.inkMuted,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(chevronRotation),
                )
            }

            // Spring-expanded chapter syllabus rows
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = spring(stiffness = 380f)) +
                    expandVertically(
                        animationSpec = spring(
                            dampingRatio = 0.84f,
                            stiffness = 360f,
                        ),
                    ),
                exit = fadeOut(animationSpec = spring(stiffness = 420f)) +
                    shrinkVertically(
                        animationSpec = spring(
                            dampingRatio = 0.88f,
                            stiffness = 420f,
                        ),
                    ),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(tokens.hairlineSubtle),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.batches_chapters_label).uppercase(),
                        style = MonoBadgeStyle,
                        color = tokens.inkMuted,
                    )

                    subject.chapters.forEach { chapter ->
                        ChapterChecklistRow(
                            chapter = chapter,
                            accentColor = accent,
                            onToggle = { onToggleChapter(chapter.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterChecklistRow(
    chapter: BatchChapter,
    accentColor: Color,
    onToggle: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(13.dp)

    val rowBorder by animateColorAsState(
        targetValue = if (chapter.isCompleted) accentColor.copy(alpha = 0.34f) else tokens.hairlineSubtle,
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
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.985f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accentColor.copy(alpha = 0.16f)),
                role = Role.Checkbox,
                onClick = onToggle,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = if (chapter.isCompleted) {
                    Icons.Rounded.CheckCircle
                } else {
                    Icons.Rounded.RadioButtonUnchecked
                },
                contentDescription = if (chapter.isCompleted) "Completed" else "In progress",
                tint = if (chapter.isCompleted) accentColor else tokens.inkMuted,
                modifier = Modifier.size(20.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (chapter.isCompleted) tokens.inkPrimary else tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "${chapter.lectureCount} lectures · ${chapter.materialCount} files",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkMuted,
                    )
                    AnimatedLinearProgressBar(
                        progressPercent = chapter.progressPercent,
                        accentColor = accentColor,
                        height = 4.dp,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Text(
                text = "${chapter.progressPercent}%",
                style = MonoBadgeStyle,
                color = if (chapter.isCompleted) accentColor else tokens.inkSecondary,
            )
        }
    }
}
