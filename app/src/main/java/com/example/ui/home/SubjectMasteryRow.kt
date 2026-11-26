package com.example.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.SubjectMastery
import com.example.ui.components.AnimatedMasteryRing
import com.example.ui.components.ObsidianCard
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle

@Composable
fun SubjectMasterySection(
    subjects: List<SubjectMastery>,
    isWideLayout: Boolean,
    onSubjectClick: (SubjectMastery) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.section_subject_mastery),
                style = MaterialTheme.typography.titleLarge,
                color = tokens.inkPrimary,
            )
            Text(
                text = stringResource(R.string.section_subject_mastery_meta),
                style = MonoBadgeStyle,
                color = tokens.inkMuted,
            )
        }

        if (isWideLayout) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                subjects.forEachIndexed { index, subject ->
                    SubjectMasteryCard(
                        subject = subject,
                        delayMillis = 90L + (index * 70L),
                        onClick = { onSubjectClick(subject) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            val rows = subjects.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rows.forEachIndexed { rowIndex, rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowItems.forEachIndexed { colIndex, subject ->
                            val idx = rowIndex * 2 + colIndex
                            SubjectMasteryCard(
                                subject = subject,
                                delayMillis = 90L + (idx * 70L),
                                onClick = { onSubjectClick(subject) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectMasteryCard(
    subject: SubjectMastery,
    delayMillis: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val subjectAccent = subject.accentMood.accent

    ObsidianCard(
        modifier = modifier.testTag("subject_mastery_card_${subject.id}"),
        containerColor = tokens.obsidianSurface,
        accentEdgeColor = subject.accentMood.hairlineTint,
        surfaceWashColor = subject.accentMood.surfaceWash.copy(alpha = 0.05f),
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(14.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${subject.chaptersCompleted}/${subject.totalChapters} ch",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkMuted,
                        maxLines = 1,
                    )
                }

                AnimatedMasteryRing(
                    percentage = subject.masteryPercent,
                    accentColor = subjectAccent,
                    ringSize = 64.dp,
                    strokeWidth = 5.5.dp,
                    delayMillis = delayMillis,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = subject.activeChapter,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = subject.accentMood.container,
                    border = BorderStroke(1.dp, subjectAccent.copy(alpha = 0.25f)),
                ) {
                    Text(
                        text = subject.weeklyDelta,
                        style = MonoBadgeStyle,
                        color = subject.accentMood.onContainer,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    )
                }
            }
        }
    }
}
