package com.example.ui.quiz

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Verified
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.QuizMode
import com.example.model.QuizQuestion
import com.example.model.RevisionQuizUiState
import com.example.model.WeakTopicBreakdownItem
import com.example.ui.components.AnimatedMasteryRing
import com.example.ui.components.AnodizedPill
import com.example.ui.components.KineticNumberText
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.FeedbackCorrect
import com.example.ui.theme.FeedbackCorrectContainer
import com.example.ui.theme.FeedbackIncorrect
import com.example.ui.theme.FeedbackIncorrectContainer
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.MonoMetricLarge
import com.example.ui.theme.MonoMetricMedium

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizResultBreakdownView(
    state: RevisionQuizUiState,
    isWideLayout: Boolean,
    onRetryMissed: () -> Unit,
    onJumpToRevisionNotes: (preferredSubjectId: String?) -> Unit,
    onRetakeFullQuiz: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val questions = state.activeQuestions
    val total = questions.size.coerceAtLeast(1)
    val correct = state.correctCount
    val accuracyPercent = ((correct.toFloat() / total.toFloat()) * 100f).toInt()
    val weakTopics = state.weakTopics
    val totalTimeSec = questions.sumOf { state.questionElapsedSeconds[it.id] ?: 0 }
    val avgTimeSec = (totalTimeSec / total).coerceAtLeast(0)

    val scoreAccent = when {
        accuracyPercent >= 80 -> FeedbackCorrect
        accuracyPercent >= 50 -> AccentMood.AMBER.accent
        else -> FeedbackIncorrect
    }

    val primaryWeakSubjectId = weakTopics.firstOrNull()?.subjectId ?: state.selectedQuizSubjectId

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp)
            .testTag("quiz_result_breakdown_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Top Kinetic Score & Action Summary Card
        StaggeredEntrance(delayMillis = 0L) {
            ObsidianCard(
                containerColor = tokens.obsidianSurface,
                accentEdgeColor = scoreAccent.copy(alpha = 0.42f),
                surfaceWashColor = scoreAccent.copy(alpha = 0.06f),
                cornerRadius = 22.dp,
                contentPadding = PaddingValues(20.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        AnimatedMasteryRing(
                            percentage = accuracyPercent,
                            accentColor = scoreAccent,
                            ringSize = 82.dp,
                            strokeWidth = 6.5.dp,
                            delayMillis = 60L,
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                AnodizedPill(
                                    text = if (state.quizMode == QuizMode.EXAM) {
                                        "EXAM MODE GRADED"
                                    } else {
                                        "NORMAL MODE COMPLETE"
                                    },
                                    accentColor = scoreAccent,
                                    containerColor = scoreAccent.copy(alpha = 0.14f),
                                    textColor = scoreAccent,
                                )
                                Text(
                                    text = "Total ${formatStopwatch(totalTimeSec)} · Avg ${avgTimeSec}s/q",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tokens.inkMuted,
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                KineticNumberText(
                                    targetValue = correct,
                                    style = MonoMetricLarge,
                                    color = tokens.inkPrimary,
                                    delayMillis = 80L,
                                )
                                Text(
                                    text = "/ $total correct",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = tokens.inkSecondary,
                                    modifier = Modifier.padding(bottom = 2.dp),
                                )
                            }
                        }
                    }

                    // Action Row: Retry Missed, Jump to Revision Notes, Retake Full Quiz
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        if (weakTopics.isNotEmpty()) {
                            ResultActionButton(
                                label = "${stringResource(R.string.quiz_action_retry_missed)} (${state.missedQuestions.size})",
                                icon = Icons.Rounded.Replay,
                                primaryFill = true,
                                accentColor = AccentMood.AMBER.accent,
                                onClick = onRetryMissed,
                                modifier = Modifier.testTag("quiz_retry_missed_button"),
                            )
                        }

                        ResultActionButton(
                            label = stringResource(R.string.quiz_action_jump_revision),
                            icon = Icons.Rounded.AutoStories,
                            primaryFill = weakTopics.isEmpty(),
                            accentColor = MaterialTheme.colorScheme.primary,
                            onClick = { onJumpToRevisionNotes(primaryWeakSubjectId) },
                            modifier = Modifier.testTag("quiz_jump_revision_button"),
                        )

                        ResultActionButton(
                            label = stringResource(R.string.quiz_action_retake_full),
                            icon = Icons.Rounded.Replay,
                            primaryFill = false,
                            accentColor = tokens.inkPrimary,
                            onClick = onRetakeFullQuiz,
                            modifier = Modifier.testTag("quiz_retake_full_button"),
                        )
                    }
                }
            }
        }

        // Split landscape or stacked portrait: Weak-Topic Breakdown + Graded Answer Review
        if (isWideLayout) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top,
            ) {
                WeakTopicsColumn(
                    weakTopics = weakTopics,
                    modifier = Modifier.weight(0.45f),
                )
                GradedReviewColumn(
                    questions = questions,
                    selectedAnswers = state.selectedAnswers,
                    elapsedTimes = state.questionElapsedSeconds,
                    modifier = Modifier.weight(0.55f),
                )
            }
        } else {
            WeakTopicsColumn(weakTopics = weakTopics)
            GradedReviewColumn(
                questions = questions,
                selectedAnswers = state.selectedAnswers,
                elapsedTimes = state.questionElapsedSeconds,
            )
        }
    }
}

@Composable
private fun WeakTopicsColumn(
    weakTopics: List<WeakTopicBreakdownItem>,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weak_topic_breakdown_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.quiz_weak_topics_header),
            style = MaterialTheme.typography.titleMedium,
            color = tokens.inkPrimary,
        )

        if (weakTopics.isEmpty()) {
            ObsidianCard(
                containerColor = tokens.obsidianSurface,
                accentEdgeColor = FeedbackCorrect.copy(alpha = 0.42f),
                cornerRadius = 18.dp,
                contentPadding = PaddingValues(18.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Verified,
                        contentDescription = null,
                        tint = FeedbackCorrect,
                        modifier = Modifier.size(28.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = stringResource(R.string.quiz_all_clear_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = stringResource(R.string.quiz_all_clear_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }
                }
            }
        } else {
            weakTopics.forEachIndexed { idx, item ->
                StaggeredEntrance(delayMillis = 70L + idx * 50L) {
                    ObsidianCard(
                        containerColor = tokens.obsidianSurface,
                        accentEdgeColor = FeedbackIncorrect.copy(alpha = 0.36f),
                        cornerRadius = 18.dp,
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.testTag("weak_topic_card_$idx"),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = item.subjectName.uppercase(),
                                    style = MonoBadgeStyle,
                                    color = item.accentMood.accent,
                                )
                                Text(
                                    text = "${item.missedCount} MISSED",
                                    style = MonoBadgeStyle,
                                    color = FeedbackIncorrect,
                                )
                            }

                            Text(
                                text = item.subtopicTag,
                                style = MaterialTheme.typography.titleMedium,
                                color = tokens.inkPrimary,
                            )

                            Text(
                                text = item.focusHint,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                            )

                            // Phase 3 Bite-Sized Fix Video Preview Badge
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = tokens.carbonSurface,
                                border = BorderStroke(1.dp, item.accentMood.accent.copy(alpha = 0.30f)),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PlayCircleOutline,
                                        contentDescription = null,
                                        tint = item.accentMood.accent,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Text(
                                        text = stringResource(R.string.quiz_fix_video_badge),
                                        style = MonoBadgeStyle,
                                        color = tokens.inkPrimary,
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

@Composable
private fun GradedReviewColumn(
    questions: List<QuizQuestion>,
    selectedAnswers: Map<String, Int>,
    elapsedTimes: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quiz_graded_review_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.quiz_answer_review_header),
            style = MaterialTheme.typography.titleMedium,
            color = tokens.inkPrimary,
        )

        questions.forEachIndexed { idx, q ->
            val chosenIdx = selectedAnswers[q.id]
            val isCorrect = chosenIdx == q.correctOptionIndex
            val timeSec = elapsedTimes[q.id] ?: 0
            val statusColor = if (isCorrect) FeedbackCorrect else FeedbackIncorrect
            val statusBg = if (isCorrect) FeedbackCorrectContainer else FeedbackIncorrectContainer

            ObsidianCard(
                containerColor = tokens.obsidianSurface,
                accentEdgeColor = statusColor.copy(alpha = 0.30f),
                cornerRadius = 16.dp,
                contentPadding = PaddingValues(14.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                                contentDescription = if (isCorrect) "Correct" else "Incorrect",
                                tint = statusColor,
                                modifier = Modifier.size(17.dp),
                            )
                            Text(
                                text = "Q${idx + 1} · ${q.subtopicTag}",
                                style = MonoBadgeStyle,
                                color = statusColor,
                            )
                        }
                        Text(
                            text = formatStopwatch(timeSec),
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.inkMuted,
                        )
                    }

                    Text(
                        text = q.prompt,
                        style = MaterialTheme.typography.titleSmall,
                        color = tokens.inkPrimary,
                    )

                    if (!isCorrect && chosenIdx != null) {
                        Text(
                            text = "Your answer: ${('A' + chosenIdx)}. ${q.options.getOrElse(chosenIdx) { "" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = FeedbackIncorrect,
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = statusBg.copy(alpha = 0.72f),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "Correct: ${('A' + q.correctOptionIndex)}. ${q.options[q.correctOptionIndex]}",
                                style = MaterialTheme.typography.labelMedium,
                                color = FeedbackCorrect,
                            )
                            Text(
                                text = q.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultActionButton(
    label: String,
    icon: ImageVector,
    primaryFill: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(13.dp)

    Surface(
        shape = shape,
        color = if (primaryFill) accentColor else tokens.carbonSurface,
        border = if (primaryFill) null else BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.965f)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = accentColor.copy(alpha = 0.2f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 11.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (primaryFill) tokens.amoledBackground else accentColor,
                modifier = Modifier.size(17.dp),
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (primaryFill) tokens.amoledBackground else tokens.inkPrimary,
            )
        }
    }
}
