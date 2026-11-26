package com.example.ui.batches

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.BatchesUiState
import com.example.model.PresetSubjectTemplate
import com.example.model.StudyBatch
import com.example.ui.components.AnodizedPill
import com.example.ui.components.KineticNumberText
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoMetricMedium

@Composable
fun BatchesScreen(
    batchesState: BatchesUiState,
    activeThemeMood: AccentMood,
    isWideLayout: Boolean,
    onSelectBatch: (String) -> Unit,
    onToggleExpandSubject: (String) -> Unit,
    onToggleChapterCompleted: (batchId: String, subjectId: String, chapterId: String) -> Unit,
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
    val selectedBatch = batchesState.selectedBatch
    val expandedSubject = selectedBatch?.subjects?.firstOrNull {
        it.id == batchesState.expandedSubjectId
    }

    // Smooth subject/batch accent color interpolation
    val activeFocusMood = expandedSubject?.accentMood ?: selectedBatch?.accentMood ?: activeThemeMood
    val animatedFocusAccent by animateColorAsState(
        targetValue = activeFocusMood.accent,
        animationSpec = spring(dampingRatio = 0.88f, stiffness = 300f),
        label = "batchesActiveAccent",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("batches_screen"),
    ) {
        if (isWideLayout) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 1320.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                // Left Batch Roster Column (~31%)
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
                        onSelectBatch = onSelectBatch,
                        onCreateBatchClick = onOpenBuilder,
                    )
                }

                // Right Subject & Chapter Workspace (~68%)
                Box(
                    modifier = Modifier
                        .weight(0.68f)
                        .fillMaxHeight(),
                ) {
                    if (selectedBatch != null) {
                        BatchDetailCanvas(
                            batch = selectedBatch,
                            expandedSubjectId = batchesState.expandedSubjectId,
                            focusAccent = animatedFocusAccent,
                            onToggleExpandSubject = onToggleExpandSubject,
                            onToggleChapterCompleted = { subjectId, chapterId ->
                                onToggleChapterCompleted(selectedBatch.id, subjectId, chapterId)
                            },
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item(key = "batches_mobile_header") {
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
                }

                item(key = "batches_mobile_roster") {
                    BatchRosterPane(
                        batches = batchesState.batches,
                        selectedBatchId = batchesState.selectedBatchId,
                        onSelectBatch = onSelectBatch,
                        onCreateBatchClick = onOpenBuilder,
                    )
                }

                if (selectedBatch != null) {
                    item(key = "batches_mobile_banner_${selectedBatch.id}") {
                        BatchOverviewBanner(
                            batch = selectedBatch,
                            focusAccent = animatedFocusAccent,
                        )
                    }

                    itemsIndexed(
                        items = selectedBatch.subjects,
                        key = { _, subject -> subject.id },
                    ) { index, subject ->
                        StaggeredEntrance(delayMillis = index * 55L) {
                            BatchSubjectCard(
                                subject = subject,
                                expanded = subject.id == batchesState.expandedSubjectId,
                                ringDelayMillis = 80L + (index * 60L),
                                onToggleExpand = { onToggleExpandSubject(subject.id) },
                                onToggleChapter = { chapterId ->
                                    onToggleChapterCompleted(selectedBatch.id, subject.id, chapterId)
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
    }
}

@Composable
private fun BatchDetailCanvas(
    batch: StudyBatch,
    expandedSubjectId: String?,
    focusAccent: Color,
    onToggleExpandSubject: (String) -> Unit,
    onToggleChapterCompleted: (subjectId: String, chapterId: String) -> Unit,
) {
    AnimatedContent(
        targetState = batch,
        contentKey = { it.id },
        transitionSpec = {
            (fadeIn(animationSpec = tween(220)) +
                slideInVertically(
                    initialOffsetY = { it / 20 },
                    animationSpec = spring(dampingRatio = 0.86f, stiffness = 380f),
                )) togetherWith fadeOut(animationSpec = tween(140))
        },
        label = "batchDetailSwitch",
    ) { activeBatch ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item(key = "overview_${activeBatch.id}") {
                BatchOverviewBanner(
                    batch = activeBatch,
                    focusAccent = focusAccent,
                )
            }

            itemsIndexed(
                items = activeBatch.subjects,
                key = { _, subject -> subject.id },
            ) { index, subject ->
                StaggeredEntrance(delayMillis = 45L * index) {
                    BatchSubjectCard(
                        subject = subject,
                        expanded = subject.id == expandedSubjectId,
                        ringDelayMillis = 70L + (index * 55L),
                        onToggleExpand = { onToggleExpandSubject(subject.id) },
                        onToggleChapter = { chapterId ->
                            onToggleChapterCompleted(subject.id, chapterId)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun BatchOverviewBanner(
    batch: StudyBatch,
    focusAccent: Color,
) {
    val tokens = GhickinTokens.colors

    ObsidianCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("batch_overview_banner_${batch.id}"),
        containerColor = tokens.obsidianSurface,
        accentEdgeColor = focusAccent.copy(alpha = 0.40f),
        surfaceWashColor = focusAccent.copy(alpha = 0.08f),
        cornerRadius = 22.dp,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
    ) {
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
                    AnodizedPill(
                        text = batch.goalTag.uppercase(),
                        accentColor = focusAccent,
                        containerColor = focusAccent.copy(alpha = 0.14f),
                        textColor = focusAccent,
                        borderColor = focusAccent.copy(alpha = 0.34f),
                    )
                    Text(
                        text = batch.targetDateLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkMuted,
                    )
                }
                Text(
                    text = batch.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = tokens.inkPrimary,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    KineticNumberText(
                        targetValue = batch.overallProgressPercent,
                        suffix = "%",
                        style = MonoMetricMedium,
                        color = focusAccent,
                    )
                    Text(
                        text = "Mastery",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkMuted,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    KineticNumberText(
                        targetValue = batch.totalHoursLogged,
                        suffix = "h",
                        style = MonoMetricMedium,
                        color = tokens.inkPrimary,
                    )
                    Text(
                        text = "Logged",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkMuted,
                    )
                }
            }
        }
    }
}
