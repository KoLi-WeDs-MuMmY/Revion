package com.example.ui.test

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.ActiveQuizSession
import com.example.model.ArenaQuestion
import com.example.model.QuizAttemptSummary
import com.example.model.TestArenaMode
import com.example.model.VaultChapterNode
import com.example.ui.components.AnimatedMasteryRing
import com.example.ui.components.ObsidianCard
import com.example.ui.theme.AccentMood
import com.example.ui.theme.FeedbackIncorrect
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.rememberScreenLayoutMetrics

private fun formatTimer(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val mins = safe / 60
    val secs = safe % 60
    return String.format("%02d:%02d", mins, secs)
}

/**
 * Mandatory pre-test mode selector requiring the student to choose
 * Exam Mode (+4 / -1, Timer, Mark for Review, Palette) or Normal Practice Mode
 * before question 1 starts.
 */
@Composable
fun TestModeGateDialog(
    chapter: VaultChapterNode,
    questionCount: Int,
    onStartMode: (TestArenaMode) -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    var pickedMode by remember { mutableStateOf(TestArenaMode.EXAM) }
    val examMinutes = (questionCount * 2).coerceAtLeast(4)

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
                .widthIn(max = 560.dp)
                .testTag("test_mode_gate_dialog"),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${chapter.subjectName.uppercase()} · ${chapter.chapterCode}",
                            style = MicroTagMono,
                            color = tokens.themeAccent,
                        )
                        Text(
                            text = chapter.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = tokens.inkPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Cancel test start",
                            tint = tokens.inkSecondary,
                        )
                    }
                }

                Text(
                    text = "${stringResource(R.string.quizzes_mode_gate_subtitle)} ($questionCount questions · $examMinutes min in Exam Mode)",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )

                // Exam Mode Option
                ModeChoiceCard(
                    title = stringResource(R.string.quizzes_mode_exam_title),
                    description = stringResource(R.string.quizzes_mode_exam_desc),
                    badge = "$examMinutes MIN · +4 / −1",
                    icon = Icons.Rounded.Timer,
                    selected = pickedMode == TestArenaMode.EXAM,
                    testTag = "mode_gate_exam_option",
                    onClick = { pickedMode = TestArenaMode.EXAM },
                )

                // Normal Mode Option
                ModeChoiceCard(
                    title = stringResource(R.string.quizzes_mode_normal_title),
                    description = stringResource(R.string.quizzes_mode_normal_desc),
                    badge = "UNTIMED · INSTANT",
                    icon = Icons.Rounded.Lightbulb,
                    selected = pickedMode == TestArenaMode.NORMAL,
                    testTag = "mode_gate_normal_option",
                    onClick = { pickedMode = TestArenaMode.NORMAL },
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mode_gate_launch_button")
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(role = Role.Button) { onStartMode(pickedMode) },
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = if (pickedMode == TestArenaMode.EXAM) {
                                stringResource(R.string.quizzes_launch_exam_cta)
                            } else {
                                stringResource(R.string.quizzes_launch_normal_cta)
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.onPrimaryCta,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = tokens.onPrimaryCta,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeChoiceCard(
    title: String,
    description: String,
    badge: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) tokens.slateElevated else tokens.carbonSurface,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) tokens.themeAccent else tokens.hairlineRest,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clip(RoundedCornerShape(16.dp))
            .clickable(role = Role.RadioButton, onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (selected) tokens.primaryCta else tokens.obsidianSurface),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) tokens.onPrimaryCta else tokens.inkSecondary,
                    modifier = Modifier.size(19.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = tokens.inkPrimary,
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = tokens.obsidianSurface,
                    ) {
                        Text(
                            text = badge,
                            style = MicroTagMono,
                            color = if (selected) tokens.themeAccent else tokens.inkMuted,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        )
                    }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
            }
        }
    }
}

/**
 * Active Test Session supporting both Exam Mode (+4/-1, live countdown timer, Mark for Review,
 * Question Palette, locked results until submit) and Normal Mode (untimed, instant explanation).
 */
@Composable
fun ActiveTestSessionScreen(
    session: ActiveQuizSession,
    isWideLayout: Boolean,
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
) {
    if (session.isSubmitted && session.summary != null) {
        TestAnalyticsSummaryScreen(
            session = session,
            summary = session.summary,
            isWideLayout = isWideLayout,
            onRetryMissed = onRetryMissed,
            onRetakeFull = onRetakeFull,
            onBackToArena = onExitSession,
            onStudyWeakChapterInTutor = onStudyWeakChapterInTutor,
        )
        return
    }

    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()
    val question = session.currentQuestion
    val selectedOpt = session.selectedAnswers[session.currentIndex]
    val isMarked = session.currentIndex in session.markedForReview
    val isNormalRevealed = session.mode == TestArenaMode.NORMAL && session.currentIndex in session.revealedNormalIndices

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.amoledBackground)
            .testTag("active_test_session_screen"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = metrics.horizontalPadding, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Top Runner Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(
                    onClick = onExitSession,
                    modifier = Modifier.testTag("test_exit_button"),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Exit quiz",
                        tint = tokens.inkPrimary,
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.chapterTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Q ${session.currentIndex + 1} / ${session.questions.size} · ${session.mode.label}",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                }

                // Timer Pill in Exam Mode, Instant badge in Normal Mode
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (session.mode == TestArenaMode.EXAM && session.remainingSeconds <= 60) {
                        FeedbackIncorrect.copy(alpha = 0.18f)
                    } else {
                        tokens.carbonSurface
                    },
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            imageVector = if (session.mode == TestArenaMode.EXAM) Icons.Rounded.Timer else Icons.Rounded.Lightbulb,
                            contentDescription = null,
                            tint = if (session.mode == TestArenaMode.EXAM && session.remainingSeconds <= 60) {
                                FeedbackIncorrect
                            } else {
                                tokens.themeAccent
                            },
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = if (session.mode == TestArenaMode.EXAM) {
                                "${formatTimer(session.remainingSeconds)} · +4/−1"
                            } else {
                                "INSTANT"
                            },
                            style = MonoBadgeStyle,
                            color = tokens.inkPrimary,
                        )
                    }
                }

                if (!isWideLayout) {
                    IconButton(
                        onClick = onTogglePalette,
                        modifier = Modifier.testTag("test_palette_toggle_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GridView,
                            contentDescription = stringResource(R.string.quizzes_palette_desc),
                            tint = tokens.inkPrimary,
                        )
                    }
                }
            }

            // Main Body: 16:9 Side-by-side Question + Palette; 9:16 Single Column
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                ) {
                    // Question Card
                    item(key = "q_prompt_${question.id}") {
                        ObsidianCard(
                            containerColor = tokens.obsidianSurface,
                            cornerRadius = 20.dp,
                            contentPadding = PaddingValues(18.dp),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(7.dp),
                                        color = tokens.carbonSurface,
                                    ) {
                                        Text(
                                            text = "${question.topicName.uppercase()} · ${question.difficulty.uppercase()}",
                                            style = MicroTagMono,
                                            color = tokens.themeAccent,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        )
                                    }

                                    // Mark for Review toggle
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isMarked) AccentMood.AMBER.accent.copy(alpha = 0.2f) else tokens.carbonSurface,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isMarked) AccentMood.AMBER.accent else tokens.hairlineSubtle,
                                        ),
                                        modifier = Modifier
                                            .testTag("test_mark_review_button")
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable(role = Role.Button, onClick = onToggleMarkForReview),
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        ) {
                                            Icon(
                                                imageVector = if (isMarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                                contentDescription = null,
                                                tint = if (isMarked) AccentMood.AMBER.accent else tokens.inkSecondary,
                                                modifier = Modifier.size(14.dp),
                                            )
                                            Text(
                                                text = if (isMarked) {
                                                    stringResource(R.string.quizzes_marked_review)
                                                } else {
                                                    stringResource(R.string.quizzes_mark_review)
                                                },
                                                style = MicroTagMono,
                                                color = if (isMarked) AccentMood.AMBER.accent else tokens.inkSecondary,
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = question.prompt,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = tokens.inkPrimary,
                                )
                            }
                        }
                    }

                    // 4 Options
                    itemsIndexed(
                        items = question.options,
                        key = { idx, _ -> "${question.id}_opt_$idx" },
                    ) { idx, optionText ->
                        val letter = listOf("A", "B", "C", "D").getOrElse(idx) { "${idx + 1}" }
                        val isPicked = selectedOpt == idx
                        val isCorrect = idx == question.correctIndex

                        val borderColor = when {
                            isNormalRevealed && isCorrect -> tokens.semanticSuccess
                            isNormalRevealed && isPicked && !isCorrect -> FeedbackIncorrect
                            isPicked -> tokens.themeAccent
                            else -> tokens.hairlineRest
                        }
                        val bgColor = when {
                            isNormalRevealed && isCorrect -> tokens.semanticSuccess.copy(alpha = 0.14f)
                            isNormalRevealed && isPicked && !isCorrect -> FeedbackIncorrect.copy(alpha = 0.14f)
                            isPicked -> tokens.slateElevated
                            else -> tokens.obsidianSurface
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = bgColor,
                            border = BorderStroke(if (isPicked || (isNormalRevealed && isCorrect)) 1.5.dp else 1.dp, borderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_option_$idx")
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(
                                    enabled = !isNormalRevealed,
                                    role = Role.RadioButton,
                                    onClick = { onSelectOption(idx) },
                                ),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isPicked) tokens.primaryCta else tokens.carbonSurface),
                                ) {
                                    Text(
                                        text = letter,
                                        style = MonoBadgeStyle,
                                        color = if (isPicked) tokens.onPrimaryCta else tokens.inkSecondary,
                                    )
                                }
                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.inkPrimary,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }

                    // Clear choice button in Exam Mode (to avoid -1 penalty)
                    if (session.mode == TestArenaMode.EXAM && selectedOpt != null) {
                        item(key = "clear_exam_choice") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Text(
                                    text = "Clear response (avoid −1 negative mark)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = tokens.inkMuted,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(role = Role.Button, onClick = onClearOption)
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                )
                            }
                        }
                    }

                    // Normal Mode Instant Step-by-Step Explanation
                    if (isNormalRevealed) {
                        item(key = "normal_instant_explanation_${question.id}") {
                            ExplanationPanel(question = question, userPick = selectedOpt)
                        }
                    }
                }

                // 16:9 Landscape Inline Question Palette Rail
                if (isWideLayout) {
                    QuestionPalettePanel(
                        session = session,
                        onJumpToQuestion = onJumpToQuestion,
                        onSubmitTest = onSubmitTest,
                        modifier = Modifier
                            .width(248.dp)
                            .fillMaxHeight(),
                    )
                }
            }

            // Bottom Prev / Next / Submit Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            enabled = session.currentIndex > 0,
                            role = Role.Button,
                            onClick = { onJumpToQuestion(session.currentIndex - 1) },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Previous question",
                            tint = if (session.currentIndex > 0) tokens.inkPrimary else tokens.inkMuted,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "Prev",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (session.currentIndex > 0) tokens.inkPrimary else tokens.inkMuted,
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (session.currentIndex < session.questions.lastIndex) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier
                            .testTag("test_next_question_button")
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(role = Role.Button) {
                                onJumpToQuestion(session.currentIndex + 1)
                            },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Text(
                                text = "Next",
                                style = MaterialTheme.typography.labelLarge,
                                color = tokens.inkPrimary,
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = "Next question",
                                tint = tokens.inkPrimary,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .testTag("test_submit_final_button")
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(role = Role.Button, onClick = onSubmitTest),
                ) {
                    Text(
                        text = stringResource(R.string.quizzes_submit_final),
                        style = MaterialTheme.typography.labelLarge,
                        color = tokens.onPrimaryCta,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
                    )
                }
            }
        }

        // Portrait Question Palette Modal
        if (!isWideLayout && session.isPaletteOpen) {
            Dialog(
                onDismissRequest = onTogglePalette,
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .heightIn(max = 460.dp),
                ) {
                    QuestionPalettePanel(
                        session = session,
                        onJumpToQuestion = { idx ->
                            onJumpToQuestion(idx)
                            onTogglePalette()
                        },
                        onSubmitTest = {
                            onTogglePalette()
                            onSubmitTest()
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuestionPalettePanel(
    session: ActiveQuizSession,
    onJumpToQuestion: (Int) -> Unit,
    onSubmitTest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier.testTag("question_palette_panel"),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "QUESTION PALETTE",
                style = MicroTagMono,
                color = tokens.inkMuted,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Answered: ${session.answeredCount}/${session.questions.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.semanticSuccess,
                )
                Text(
                    text = "Marked: ${session.markedCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentMood.AMBER.accent,
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                session.questions.indices.forEach { idx ->
                    val isCurrent = idx == session.currentIndex
                    val isAnswered = idx in session.selectedAnswers
                    val isMarked = idx in session.markedForReview

                    val bg = when {
                        isCurrent -> tokens.primaryCta
                        isMarked -> AccentMood.AMBER.accent.copy(alpha = 0.24f)
                        isAnswered -> tokens.semanticSuccess.copy(alpha = 0.22f)
                        else -> tokens.carbonSurface
                    }
                    val fg = when {
                        isCurrent -> tokens.onPrimaryCta
                        isMarked -> AccentMood.AMBER.accent
                        isAnswered -> tokens.semanticSuccess
                        else -> tokens.inkSecondary
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = bg,
                        border = BorderStroke(1.dp, if (isCurrent) tokens.primaryCta else tokens.hairlineSubtle),
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(role = Role.Button) { onJumpToQuestion(idx) },
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${idx + 1}",
                                style = MonoBadgeStyle,
                                color = fg,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))

            if (session.mode == TestArenaMode.EXAM) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = tokens.carbonSurface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Marking Scheme: +4 Correct · −1 Wrong · 0 Unattempted",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ExplanationPanel(
    question: ArenaQuestion,
    userPick: Int?,
) {
    val tokens = GhickinTokens.colors
    val isCorrect = userPick == question.correctIndex
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(
            1.dp,
            if (isCorrect) tokens.semanticSuccess.copy(alpha = 0.5f) else tokens.hairlineSpecularTop,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = if (isCorrect) "CORRECT · STEP-BY-STEP EXPLANATION" else "STEP-BY-STEP EXPLANATION",
                style = MicroTagMono,
                color = if (isCorrect) tokens.semanticSuccess else tokens.themeAccent,
            )
            question.stepByStepExplanation.forEach { step ->
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkPrimary,
                )
            }
            Surface(
                shape = RoundedCornerShape(9.dp),
                color = tokens.obsidianSurface,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = question.formulaAnchor,
                    style = MonoBadgeStyle,
                    color = tokens.themeAccent,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                )
            }
        }
    }
}

/**
 * Post-Test Analytics Summary with +4 / -1 Exam Mode breakdown, Accuracy %,
 * Weak Topics grouped by Chapter/Subtopic with AI Tutor deep-link, and
 * expandable Step-by-Step Explanations for every question.
 */
@Composable
fun TestAnalyticsSummaryScreen(
    session: ActiveQuizSession,
    summary: QuizAttemptSummary,
    isWideLayout: Boolean,
    onRetryMissed: () -> Unit,
    onRetakeFull: () -> Unit,
    onBackToArena: () -> Unit,
    onStudyWeakChapterInTutor: (batchId: String, chapterId: String) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()
    var expandedQuestionIds by remember { mutableStateOf(setOf(session.questions.firstOrNull()?.id.orEmpty())) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.amoledBackground)
            .testTag("test_analytics_summary_screen"),
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Header
            item(key = "analytics_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    IconButton(onClick = onBackToArena) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back to Quiz Arena",
                            tint = tokens.inkPrimary,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${summary.subjectName.uppercase()} · ${summary.mode.label.uppercase()}",
                            style = MicroTagMono,
                            color = tokens.themeAccent,
                        )
                        Text(
                            text = "${summary.chapterTitle} — Results",
                            style = MaterialTheme.typography.headlineSmall,
                            color = tokens.inkPrimary,
                            maxLines = 2,
                        )
                    }
                }
            }

            // Net Score + Accuracy Hero Card
            item(key = "analytics_hero") {
                ObsidianCard(
                    containerColor = tokens.obsidianSurface,
                    cornerRadius = 22.dp,
                    contentPadding = PaddingValues(20.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (summary.mode == TestArenaMode.EXAM) "NET EXAM SCORE (+4 / −1)" else "PRACTICE SCORE",
                                    style = MicroTagMono,
                                    color = tokens.inkMuted,
                                )
                                Text(
                                    text = "${summary.netScore} / ${summary.maxScore}",
                                    style = MaterialTheme.typography.displayMedium,
                                    color = tokens.inkPrimary,
                                )
                                Text(
                                    text = "${summary.accuracyPercent}% Accuracy on Attempted Questions",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.inkSecondary,
                                )
                            }

                            AnimatedMasteryRing(
                                percentage = summary.accuracyPercent,
                                accentColor = if (summary.accuracyPercent >= 70) tokens.semanticSuccess else tokens.themeAccent,
                                ringSize = if (isWideLayout) 82.dp else 72.dp,
                                strokeWidth = 6.dp,
                            )
                        }

                        // Breakdown KPI chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            ScorePillStat(
                                label = if (summary.mode == TestArenaMode.EXAM) "CORRECT (+4)" else "CORRECT",
                                value = "${summary.correctCount}",
                                accent = tokens.semanticSuccess,
                                modifier = Modifier.weight(1f),
                            )
                            ScorePillStat(
                                label = if (summary.mode == TestArenaMode.EXAM) "WRONG (−1)" else "WRONG",
                                value = "${summary.wrongCount}",
                                accent = FeedbackIncorrect,
                                modifier = Modifier.weight(1f),
                            )
                            ScorePillStat(
                                label = "SKIPPED (0)",
                                value = "${summary.unattemptedCount}",
                                accent = tokens.inkSecondary,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        // Quick Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            if (summary.wrongCount + summary.unattemptedCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = tokens.primaryCta,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("analytics_retry_missed_button")
                                        .minimumInteractiveComponentSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable(role = Role.Button, onClick = onRetryMissed),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 11.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Refresh,
                                            contentDescription = null,
                                            tint = tokens.onPrimaryCta,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Retry Missed",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = tokens.onPrimaryCta,
                                        )
                                    }
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = tokens.carbonSurface,
                                border = BorderStroke(1.dp, tokens.hairlineRest),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("analytics_retake_button")
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable(role = Role.Button, onClick = onRetakeFull),
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 11.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "Retake Full Quiz",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = tokens.inkPrimary,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Weak Topics Detected Section
            item(key = "analytics_weak_topics") {
                ObsidianCard(
                    containerColor = tokens.obsidianSurface,
                    cornerRadius = 20.dp,
                    contentPadding = PaddingValues(18.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                        ) {
                            Icon(
                                imageVector = if (summary.weakTopics.isEmpty()) Icons.Rounded.CheckCircle else Icons.Rounded.WarningAmber,
                                contentDescription = null,
                                tint = if (summary.weakTopics.isEmpty()) tokens.semanticSuccess else AccentMood.AMBER.accent,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = "WEAK TOPICS DETECTED",
                                style = MicroTagMono,
                                color = tokens.inkPrimary,
                            )
                        }

                        if (summary.weakTopics.isEmpty()) {
                            Text(
                                text = "Zero weak subtopics detected! Every tested concept in ${summary.chapterTitle} was cleared cleanly.",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                            )
                        } else {
                            summary.weakTopics.forEach { weak ->
                                Surface(
                                    shape = RoundedCornerShape(13.dp),
                                    color = tokens.carbonSurface,
                                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = weak.topicName,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = tokens.inkPrimary,
                                            )
                                            Text(
                                                text = "${weak.chapterTitle} · ${weak.missedCount}/${weak.totalCount} missed",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = tokens.inkSecondary,
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = tokens.primaryCta,
                                            modifier = Modifier
                                                .testTag("weak_topic_tutor_cta_${weak.topicName}")
                                                .minimumInteractiveComponentSize()
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable(role = Role.Button) {
                                                    onStudyWeakChapterInTutor(summary.batchId, summary.chapterId)
                                                },
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = tokens.onPrimaryCta,
                                                    modifier = Modifier.size(14.dp),
                                                )
                                                Text(
                                                    text = stringResource(R.string.quizzes_ask_tutor_weak),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = tokens.onPrimaryCta,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Expandable Step-by-Step Explanations for EVERY Question
            item(key = "analytics_solutions_header") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.School,
                        contentDescription = null,
                        tint = tokens.themeAccent,
                        modifier = Modifier.size(17.dp),
                    )
                    Text(
                        text = "STEP-BY-STEP SOLUTIONS (${session.questions.size})",
                        style = MicroTagMono,
                        color = tokens.inkPrimary,
                    )
                }
            }

            itemsIndexed(items = session.questions, key = { _, q -> "sol_${q.id}" }) { idx, q ->
                val userPick = session.selectedAnswers[idx]
                val isCorrect = userPick == q.correctIndex
                val isExpanded = q.id in expandedQuestionIds
                val statusLabel = when {
                    userPick == null -> "UNATTEMPTED (0)"
                    isCorrect -> if (summary.mode == TestArenaMode.EXAM) "CORRECT (+4)" else "CORRECT"
                    else -> if (summary.mode == TestArenaMode.EXAM) "WRONG (−1)" else "WRONG"
                }
                val statusColor = when {
                    userPick == null -> tokens.inkMuted
                    isCorrect -> tokens.semanticSuccess
                    else -> FeedbackIncorrect
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(role = Role.Button) {
                            expandedQuestionIds = if (isExpanded) {
                                expandedQuestionIds - q.id
                            } else {
                                expandedQuestionIds + q.id
                            }
                        },
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "Q${idx + 1} · ${q.topicName}",
                                style = MaterialTheme.typography.labelLarge,
                                color = tokens.inkPrimary,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = statusLabel,
                                style = MicroTagMono,
                                color = statusColor,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                contentDescription = null,
                                tint = tokens.inkMuted,
                                modifier = Modifier.size(18.dp),
                            )
                        }

                        Text(
                            text = q.prompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                            maxLines = if (isExpanded) 10 else 2,
                            overflow = TextOverflow.Ellipsis,
                        )

                        AnimatedVisibility(visible = isExpanded) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Correct Answer: ${q.options[q.correctIndex]}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = tokens.semanticSuccess,
                                )
                                if (userPick != null && userPick != q.correctIndex) {
                                    Text(
                                        text = "Your Pick: ${q.options[userPick]}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = FeedbackIncorrect,
                                    )
                                }
                                ExplanationPanel(question = q, userPick = userPick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScorePillStat(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(tokens.carbonSurface)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(
            text = label,
            style = MicroTagMono,
            color = tokens.inkMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = accent,
        )
    }
}
