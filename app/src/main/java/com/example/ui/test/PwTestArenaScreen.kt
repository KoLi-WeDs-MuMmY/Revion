package com.example.ui.test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.mock.Phase162MockData
import com.example.model.PwQuizArenaUiState
import com.example.model.QuizAttemptSummary
import com.example.model.StudyBatch
import com.example.model.TestArenaMode
import com.example.model.VaultChapterNode
import com.example.model.VaultSubjectNode
import com.example.ui.components.AnimatedMasteryRing
import com.example.ui.components.ObsidianCard
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.rememberScreenLayoutMetrics

/**
 * Dedicated PW-style Quizzes Arena browser (`Batch -> Subject -> Chapter -> Quiz Sets`).
 * Visually distinct from Vault with readiness rings, `+4 / -1` Exam tags, Recent Attempts strip,
 * and mandatory Exam vs. Normal Mode selection gate.
 */
@Composable
fun PwTestArenaScreen(
    state: PwQuizArenaUiState,
    batches: List<StudyBatch>,
    subjectsByBatch: Map<String, List<VaultSubjectNode>>,
    isWideLayout: Boolean,
    onOpenSettings: () -> Unit,
    onCycleBatch: () -> Unit,
    onSelectSubject: (String?) -> Unit,
    onRequestStartChapterQuiz: (subjectId: String, chapterId: String) -> Unit,
    onDismissModeGate: () -> Unit,
    onConfirmStartMode: (TestArenaMode) -> Unit,
    onSelectOption: (Int) -> Unit,
    onClearOption: () -> Unit,
    onToggleMarkForReview: () -> Unit,
    onJumpToQuestion: (Int) -> Unit,
    onTogglePalette: () -> Unit,
    onSubmitTest: () -> Unit,
    onExitSession: () -> Unit,
    onRetryMissed: () -> Unit,
    onRetakeFull: () -> Unit,
    onStudyWeakChapterInTutor: (batchId: String, chapterId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // If a test session is currently running (or showing post-test analytics), render it full stage
    state.activeSession?.let { activeSession ->
        ActiveTestSessionScreen(
            session = activeSession,
            isWideLayout = isWideLayout,
            onSelectOption = onSelectOption,
            onClearOption = onClearOption,
            onToggleMarkForReview = onToggleMarkForReview,
            onJumpToQuestion = onJumpToQuestion,
            onTogglePalette = onTogglePalette,
            onSubmitTest = onSubmitTest,
            onExitSession = onExitSession,
            onRetryMissed = onRetryMissed,
            onRetakeFull = onRetakeFull,
            onStudyWeakChapterInTutor = onStudyWeakChapterInTutor,
        )
        return
    }

    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()

    val currentBatch = batches.firstOrNull { it.id == state.selectedBatchId } ?: batches.firstOrNull()
    val batchSubjects = currentBatch?.let { subjectsByBatch[it.id] }.orEmpty()
    val selectedSubject = batchSubjects.firstOrNull { it.id == state.selectedSubjectId }

    val pendingChapter = remember(state.pendingModeGateChapterId, batchSubjects) {
        val chId = state.pendingModeGateChapterId ?: return@remember null
        batchSubjects.flatMap { it.chapters }.firstOrNull { it.id == chId }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.amoledBackground)
            .testTag("pw_test_arena_screen"),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = metrics.maxContentWidth),
            contentPadding = PaddingValues(
                horizontal = metrics.horizontalPadding,
                vertical = metrics.verticalPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Arena Header
            item(key = "arena_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.quizzes_arena_title),
                                style = MaterialTheme.typography.headlineMedium,
                                color = tokens.inkPrimary,
                            )
                            Surface(
                                shape = RoundedCornerShape(7.dp),
                                color = tokens.carbonSurface,
                                border = BorderStroke(1.dp, tokens.hairlineSpecularTop),
                            ) {
                                Text(
                                    text = "EXAM +4/−1",
                                    style = MicroTagMono,
                                    color = tokens.themeAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.quizzes_arena_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        if (currentBatch != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = tokens.obsidianSurface,
                                border = BorderStroke(1.dp, tokens.hairlineRest),
                                modifier = Modifier
                                    .testTag("quizzes_switch_batch_button")
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable(role = Role.Button, onClick = onCycleBatch),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(currentBatch.accentMood.accent),
                                    )
                                    Text(
                                        text = currentBatch.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = tokens.inkPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 136.dp),
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.SwapHoriz,
                                        contentDescription = "Switch batch",
                                        tint = tokens.inkMuted,
                                        modifier = Modifier.size(15.dp),
                                    )
                                }
                            }
                        }

                        if (!isWideLayout) {
                            IconButton(
                                onClick = onOpenSettings,
                                modifier = Modifier.testTag("quizzes_settings_button"),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = stringResource(R.string.settings_open_desc),
                                    tint = tokens.inkSecondary,
                                )
                            }
                        }
                    }
                }
            }

            // Recent Quiz Attempts Strip
            if (state.recentAttempts.isNotEmpty() && selectedSubject == null) {
                item(key = "arena_recent_attempts") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = null,
                                tint = tokens.inkMuted,
                                modifier = Modifier.size(15.dp),
                            )
                            Text(
                                text = stringResource(R.string.quizzes_recent_attempts),
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            items(items = state.recentAttempts, key = { it.id }) { attempt ->
                                RecentAttemptCard(
                                    attempt = attempt,
                                    onReattempt = {
                                        onRequestStartChapterQuiz(attempt.subjectId, attempt.chapterId)
                                    },
                                )
                            }
                        }
                    }
                }
            }

            // Breadcrumb Trail (Batch > Subject)
            item(key = "arena_breadcrumbs") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = if (selectedSubject == null) tokens.slateElevated else tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .clickable(role = Role.Button) { onSelectSubject(null) },
                    ) {
                        Text(
                            text = currentBatch?.name ?: "All Subjects",
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.inkPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }

                    if (selectedSubject != null) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(16.dp),
                        )
                        Surface(
                            shape = RoundedCornerShape(9.dp),
                            color = tokens.slateElevated,
                            border = BorderStroke(1.dp, tokens.hairlineSpecularTop),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(selectedSubject.accentMood.accent),
                                )
                                Text(
                                    text = selectedSubject.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = tokens.inkPrimary,
                                )
                            }
                        }
                    }
                }
            }

            // Level 1: Subject Quiz Hubs OR Level 2: Chapter Quiz Drills
            if (selectedSubject == null) {
                if (isWideLayout) {
                    items(
                        items = batchSubjects.chunked(2),
                        key = { pair -> "subj_row_${pair.first().id}" },
                    ) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            pair.forEach { subject ->
                                SubjectQuizHubCard(
                                    subject = subject,
                                    recentAttempts = state.recentAttempts.filter { it.subjectId == subject.id },
                                    onOpenSubject = { onSelectSubject(subject.id) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    items(items = batchSubjects, key = { "subj_card_${it.id}" }) { subject ->
                        SubjectQuizHubCard(
                            subject = subject,
                            recentAttempts = state.recentAttempts.filter { it.subjectId == subject.id },
                            onOpenSubject = { onSelectSubject(subject.id) },
                        )
                    }
                }
            } else {
                items(items = selectedSubject.chapters, key = { "quiz_ch_${it.id}" }) { chapter ->
                    val qCount = remember(chapter.id) { Phase162MockData.questionsForChapter(chapter).size }
                    val lastAttempt = state.recentAttempts.firstOrNull { it.chapterId == chapter.id }
                    ChapterQuizDrillCard(
                        chapter = chapter,
                        questionCount = qCount,
                        lastAttempt = lastAttempt,
                        onAttemptQuiz = {
                            onRequestStartChapterQuiz(selectedSubject.id, chapter.id)
                        },
                    )
                }
            }
        }
    }

    // Mandatory Pre-Test Mode Gate Dialog
    if (pendingChapter != null) {
        val qCount = remember(pendingChapter.id) { Phase162MockData.questionsForChapter(pendingChapter).size }
        TestModeGateDialog(
            chapter = pendingChapter,
            questionCount = qCount,
            onStartMode = onConfirmStartMode,
            onDismiss = onDismissModeGate,
        )
    }
}

@Composable
private fun RecentAttemptCard(
    attempt: QuizAttemptSummary,
    onReattempt: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier
            .width(236.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onReattempt),
    ) {
        Column(
            modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = attempt.subjectName.uppercase(),
                    style = MicroTagMono,
                    color = tokens.themeAccent,
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = tokens.carbonSurface,
                ) {
                    Text(
                        text = "${attempt.netScore}/${attempt.maxScore} · ${attempt.accuracyPercent}%",
                        style = MicroTagMono,
                        color = tokens.semanticSuccess,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                    )
                }
            }
            Text(
                text = attempt.chapterTitle,
                style = MaterialTheme.typography.titleSmall,
                color = tokens.inkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${attempt.timestampLabel} · ${attempt.weakTopics.size} weak topic(s)",
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkMuted,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SubjectQuizHubCard(
    subject: VaultSubjectNode,
    recentAttempts: List<QuizAttemptSummary>,
    onOpenSubject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val totalQuestions = remember(subject) {
        subject.chapters.sumOf { Phase162MockData.questionsForChapter(it).size }
    }
    val avgAccuracy = remember(recentAttempts, subject) {
        if (recentAttempts.isNotEmpty()) {
            recentAttempts.map { it.accuracyPercent }.average().toInt()
        } else if (subject.totalLectures == 0) {
            0
        } else {
            (subject.completedLectures * 100) / subject.totalLectures
        }
    }

    ObsidianCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quiz_subject_card_${subject.id}")
            .clickable(role = Role.Button, onClick = onOpenSubject),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(subject.accentMood.accent),
                    )
                    Text(
                        text = "${subject.chapters.size} CHAPTER QUIZZES",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                }
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = "$totalQuestions curated questions · Exam (+4/−1) & Normal modes",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 2,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        text = "Browse Chapter Quizzes",
                        style = MaterialTheme.typography.labelLarge,
                        color = tokens.themeAccent,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = tokens.themeAccent,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }

            AnimatedMasteryRing(
                percentage = avgAccuracy,
                accentColor = subject.accentMood.accent,
                ringSize = 68.dp,
                strokeWidth = 5.dp,
            )
        }
    }
}

@Composable
private fun ChapterQuizDrillCard(
    chapter: VaultChapterNode,
    questionCount: Int,
    lastAttempt: QuizAttemptSummary?,
    onAttemptQuiz: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val examMins = (questionCount * 2).coerceAtLeast(4)
    val maxMarks = questionCount * 4

    ObsidianCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_chapter_card_${chapter.id}"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(7.dp),
                        color = tokens.carbonSurface,
                    ) {
                        Text(
                            text = chapter.chapterCode,
                            style = MicroTagMono,
                            color = chapter.subjectMood.accent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(7.dp),
                        color = tokens.carbonSurface,
                    ) {
                        Text(
                            text = "$questionCount Qs · $examMins min · $maxMarks Marks",
                            style = MicroTagMono,
                            color = tokens.inkSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }

                if (lastAttempt != null) {
                    Text(
                        text = "Last: ${lastAttempt.accuracyPercent}%",
                        style = MonoBadgeStyle,
                        color = tokens.semanticSuccess,
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = "Includes Exam Mode (+4/−1, Timer, Mark for Review) & Normal Mode (Instant Explanations).",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Timer,
                            contentDescription = null,
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "Exam +4/−1",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Quiz,
                            contentDescription = null,
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "Weak-Topic Report",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .testTag("quiz_attempt_cta_${chapter.id}")
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(11.dp))
                        .clickable(role = Role.Button, onClick = onAttemptQuiz),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = tokens.onPrimaryCta,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = stringResource(R.string.quizzes_start_cta),
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.onPrimaryCta,
                        )
                    }
                }
            }
        }
    }
}
