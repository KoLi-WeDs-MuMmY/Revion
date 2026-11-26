package com.example.ui.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.Phase13MockData
import com.example.model.QuizMode
import com.example.model.QuizQuestion
import com.example.model.RevisionQuizUiState
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.AnodizedPill
import com.example.ui.components.KineticFilterChip
import com.example.ui.components.ObsidianCard
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.FeedbackCorrect
import com.example.ui.theme.FeedbackCorrectContainer
import com.example.ui.theme.FeedbackIncorrect
import com.example.ui.theme.FeedbackIncorrectContainer
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.MonoMetricMedium
import kotlinx.coroutines.delay

@Composable
fun QuizArenaView(
    state: RevisionQuizUiState,
    isWideLayout: Boolean,
    onSelectQuizMode: (QuizMode) -> Unit,
    onSelectQuizSubject: (String) -> Unit,
    onSelectOption: (questionId: String, optionIndex: Int, elapsedSeconds: Int) -> Unit,
    onAdvanceOrSubmit: (elapsedSeconds: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val question = state.currentQuestion ?: return
    val totalQuestions = state.activeQuestions.size.coerceAtLeast(1)
    val selectedOption = state.selectedAnswers[question.id]
    val isNormalAnswered = state.quizMode == QuizMode.NORMAL && selectedOption != null
    val isLastQuestion = state.currentQuestionIndex >= totalQuestions - 1

    val subjectAccent by animateColorAsState(
        targetValue = question.accentMood.accent,
        animationSpec = spring(dampingRatio = 0.86f, stiffness = 340f),
        label = "quizSubjectAccent",
    )

    // Per-question stopwatch: resets to 00:00 for EVERY question in BOTH Normal and Exam modes!
    var questionElapsedSec by remember(state.sessionNonce, state.currentQuestionIndex) {
        mutableIntStateOf(0)
    }

    LaunchedEffect(state.sessionNonce, state.currentQuestionIndex, isNormalAnswered, state.isQuizSubmitted) {
        if (!state.isQuizSubmitted && !isNormalAnswered) {
            while (true) {
                delay(1000L)
                questionElapsedSec += 1
            }
        }
    }

    val progressPercent = (((state.currentQuestionIndex + if (selectedOption != null) 1 else 0).toFloat() /
        totalQuestions.toFloat()) * 100f).toInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("quiz_arena_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Top Quiz Control Strip: Subject selector + Normal/Exam Mode Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val subjectChoices = listOf(
                    Triple(Phase13MockData.SUBJECT_PHYSICS_ID, "Physics", AccentMood.CYAN),
                    Triple(Phase13MockData.SUBJECT_CHEMISTRY_ID, "Chemistry", AccentMood.VIOLET),
                    Triple(Phase13MockData.SUBJECT_BIOLOGY_ID, "Biology", AccentMood.EMERALD),
                    Triple(Phase13MockData.SUBJECT_COMPOUND_ID, "Compound Mix", AccentMood.AMBER),
                )
                subjectChoices.forEach { (id, label, mood) ->
                    KineticFilterChip(
                        label = label,
                        selected = state.selectedQuizSubjectId == id,
                        accentColor = mood.accent,
                        leadingDotColor = mood.accent,
                        onClick = { onSelectQuizSubject(id) },
                        modifier = Modifier.testTag("quiz_subject_chip_$id"),
                    )
                }
            }

            if (isWideLayout) {
                Spacer(modifier = Modifier.width(12.dp))
                QuizModeToggle(
                    activeMode = state.quizMode,
                    accentColor = subjectAccent,
                    onSelectMode = onSelectQuizMode,
                )
            }
        }

        if (!isWideLayout) {
            QuizModeToggle(
                activeMode = state.quizMode,
                accentColor = subjectAccent,
                onSelectMode = onSelectQuizMode,
            )
        }

        // Progress + Per-Question 00:00 Stopwatch Telemetry Strip
        ObsidianCard(
            containerColor = tokens.obsidianSurface,
            cornerRadius = 16.dp,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "Q ${state.currentQuestionIndex + 1} / $totalQuestions",
                            style = MonoMetricMedium,
                            color = tokens.inkPrimary,
                        )
                        if (state.isRetryMissedSession) {
                            AnodizedPill(
                                text = "RETRY MISSED DRILL",
                                accentColor = AccentMood.AMBER.accent,
                                containerColor = AccentMood.AMBER.container,
                                textColor = AccentMood.AMBER.onContainer,
                            )
                        } else {
                            Text(
                                text = if (state.quizMode == QuizMode.NORMAL) {
                                    stringResource(R.string.quiz_mode_normal_hint)
                                } else {
                                    stringResource(R.string.quiz_mode_exam_hint)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                            )
                        }
                    }

                    // Per-question 00:00 Stopwatch Pill
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, subjectAccent.copy(alpha = 0.34f)),
                        modifier = Modifier.testTag("quiz_question_timer_pill"),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Timer,
                                contentDescription = "Question timer",
                                tint = subjectAccent,
                                modifier = Modifier.size(15.dp),
                            )
                            Text(
                                text = formatStopwatch(questionElapsedSec),
                                style = MonoMetricMedium,
                                color = tokens.inkPrimary,
                            )
                        }
                    }
                }

                AnimatedLinearProgressBar(
                    progressPercent = progressPercent,
                    accentColor = subjectAccent,
                    height = 5.dp,
                )
            }
        }

        // Animated Question Arena Stage
        AnimatedContent(
            targetState = question,
            contentKey = { "${state.sessionNonce}_${it.id}" },
            transitionSpec = {
                val springSpec = spring<IntOffset>(dampingRatio = 0.86f, stiffness = 380f)
                (fadeIn(tween(220)) + slideInHorizontally(springSpec) { it / 14 }) togetherWith
                    (fadeOut(tween(140)) + slideOutHorizontally(springSpec) { -it / 18 })
            },
            label = "quizQuestionTransition",
        ) { activeQuestion ->
            val chosen = state.selectedAnswers[activeQuestion.id]
            val revealFeedback = state.quizMode == QuizMode.NORMAL && chosen != null

            if (isWideLayout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    // Left Prompt + Normal-Mode Explanation Column
                    Column(
                        modifier = Modifier.weight(0.95f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        QuestionPromptCard(
                            question = activeQuestion,
                            accentColor = subjectAccent,
                        )
                        NormalModeExplanationBox(
                            visible = revealFeedback,
                            isCorrect = chosen == activeQuestion.correctOptionIndex,
                            explanation = activeQuestion.explanation,
                        )
                    }

                    // Right 4 Tactile MCQ Options + Advance CTA Column
                    Column(
                        modifier = Modifier.weight(1.05f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        activeQuestion.options.forEachIndexed { optIdx, optText ->
                            QuizOptionCard(
                                index = optIdx,
                                text = optText,
                                isSelected = chosen == optIdx,
                                isCorrectOption = optIdx == activeQuestion.correctOptionIndex,
                                revealFeedback = revealFeedback,
                                accentColor = subjectAccent,
                                onClick = {
                                    onSelectOption(activeQuestion.id, optIdx, questionElapsedSec)
                                },
                            )
                        }

                        QuizAdvanceButton(
                            enabled = chosen != null,
                            isLastQuestion = isLastQuestion,
                            accentColor = subjectAccent,
                            onClick = { onAdvanceOrSubmit(questionElapsedSec) },
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    QuestionPromptCard(
                        question = activeQuestion,
                        accentColor = subjectAccent,
                    )
                    activeQuestion.options.forEachIndexed { optIdx, optText ->
                        QuizOptionCard(
                            index = optIdx,
                            text = optText,
                            isSelected = chosen == optIdx,
                            isCorrectOption = optIdx == activeQuestion.correctOptionIndex,
                            revealFeedback = revealFeedback,
                            accentColor = subjectAccent,
                            onClick = {
                                onSelectOption(activeQuestion.id, optIdx, questionElapsedSec)
                            },
                        )
                    }
                    NormalModeExplanationBox(
                        visible = revealFeedback,
                        isCorrect = chosen == activeQuestion.correctOptionIndex,
                        explanation = activeQuestion.explanation,
                    )
                    QuizAdvanceButton(
                        enabled = chosen != null,
                        isLastQuestion = isLastQuestion,
                        accentColor = subjectAccent,
                        onClick = { onAdvanceOrSubmit(questionElapsedSec) },
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizModeToggle(
    activeMode: QuizMode,
    accentColor: Color,
    onSelectMode: (QuizMode) -> Unit,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier.testTag("quiz_mode_toggle"),
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            QuizMode.entries.forEach { mode ->
                val selected = mode == activeMode
                val bg by animateColorAsState(
                    targetValue = if (selected) accentColor.copy(alpha = 0.20f) else Color.Transparent,
                    animationSpec = spring(dampingRatio = 0.84f, stiffness = 420f),
                    label = "quizModeBg_${mode.name}",
                )
                val interaction = remember { MutableInteractionSource() }
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = bg,
                    border = if (selected) BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)) else null,
                    modifier = Modifier
                        .testTag("quiz_mode_${mode.name.lowercase()}")
                        .tactileScale(interaction, pressedScale = 0.96f)
                        .clip(RoundedCornerShape(100.dp))
                        .clickable(
                            interactionSource = interaction,
                            indication = ripple(color = accentColor.copy(alpha = 0.18f)),
                            role = Role.RadioButton,
                            onClick = { onSelectMode(mode) },
                        ),
                ) {
                    Text(
                        text = if (mode == QuizMode.NORMAL) {
                            stringResource(R.string.quiz_mode_normal)
                        } else {
                            stringResource(R.string.quiz_mode_exam)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) tokens.inkPrimary else tokens.inkSecondary,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionPromptCard(
    question: QuizQuestion,
    accentColor: Color,
) {
    val tokens = GhickinTokens.colors
    ObsidianCard(
        containerColor = tokens.obsidianSurface,
        accentEdgeColor = accentColor.copy(alpha = 0.36f),
        surfaceWashColor = accentColor.copy(alpha = 0.06f),
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AnodizedPill(
                    text = question.subjectName.uppercase(),
                    accentColor = accentColor,
                    containerColor = question.accentMood.container,
                    textColor = question.accentMood.onContainer,
                )
                Text(
                    text = question.subtopicTag,
                    style = MonoBadgeStyle,
                    color = tokens.inkSecondary,
                )
            }

            Text(
                text = question.prompt,
                style = MaterialTheme.typography.titleLarge,
                color = tokens.inkPrimary,
            )
        }
    }
}

@Composable
private fun QuizOptionCard(
    index: Int,
    text: String,
    isSelected: Boolean,
    isCorrectOption: Boolean,
    revealFeedback: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val optionLetter = ('A' + index).toString()

    val targetContainer = when {
        revealFeedback && isCorrectOption -> FeedbackCorrectContainer
        revealFeedback && isSelected && !isCorrectOption -> FeedbackIncorrectContainer
        !revealFeedback && isSelected -> accentColor.copy(alpha = 0.16f)
        else -> tokens.obsidianSurface
    }
    val targetBorder = when {
        revealFeedback && isCorrectOption -> FeedbackCorrect.copy(alpha = 0.72f)
        revealFeedback && isSelected && !isCorrectOption -> FeedbackIncorrect.copy(alpha = 0.72f)
        !revealFeedback && isSelected -> accentColor.copy(alpha = 0.62f)
        else -> tokens.hairlineRest
    }

    val containerColor by animateColorAsState(
        targetValue = targetContainer,
        animationSpec = spring(dampingRatio = 0.84f, stiffness = 400f),
        label = "optBg_$index",
    )
    val borderColor by animateColorAsState(
        targetValue = targetBorder,
        animationSpec = spring(dampingRatio = 0.84f, stiffness = 400f),
        label = "optBorder_$index",
    )

    val shape = RoundedCornerShape(16.dp)
    Surface(
        shape = shape,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_option_$index")
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.98f)
            .clip(shape)
            .clickable(
                enabled = !revealFeedback,
                interactionSource = interaction,
                indication = ripple(color = accentColor.copy(alpha = 0.18f)),
                role = Role.RadioButton,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        when {
                            revealFeedback && isCorrectOption -> FeedbackCorrect.copy(alpha = 0.22f)
                            revealFeedback && isSelected && !isCorrectOption -> FeedbackIncorrect.copy(alpha = 0.22f)
                            !revealFeedback && isSelected -> accentColor.copy(alpha = 0.24f)
                            else -> tokens.carbonSurface
                        },
                    ),
            ) {
                Text(
                    text = optionLetter,
                    style = MonoBadgeStyle,
                    color = when {
                        revealFeedback && isCorrectOption -> FeedbackCorrect
                        revealFeedback && isSelected && !isCorrectOption -> FeedbackIncorrect
                        !revealFeedback && isSelected -> accentColor
                        else -> tokens.inkSecondary
                    },
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.inkPrimary,
                modifier = Modifier.weight(1f),
            )

            if (revealFeedback && (isCorrectOption || isSelected)) {
                Icon(
                    imageVector = if (isCorrectOption) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                    contentDescription = if (isCorrectOption) "Correct" else "Incorrect",
                    tint = if (isCorrectOption) FeedbackCorrect else FeedbackIncorrect,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun NormalModeExplanationBox(
    visible: Boolean,
    isCorrect: Boolean,
    explanation: String,
) {
    val tokens = GhickinTokens.colors
    val feedbackAccent = if (isCorrect) FeedbackCorrect else FeedbackIncorrect

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(spring(stiffness = 380f)) +
            expandVertically(spring(dampingRatio = 0.84f, stiffness = 360f)),
        exit = fadeOut(),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, feedbackAccent.copy(alpha = 0.36f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quiz_normal_explanation_box"),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = if (isCorrect) {
                        "CORRECT · ${stringResource(R.string.quiz_explanation_label)}"
                    } else {
                        "INCORRECT · ${stringResource(R.string.quiz_explanation_label)}"
                    },
                    style = MonoBadgeStyle,
                    color = feedbackAccent,
                )
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.inkPrimary,
                )
            }
        }
    }
}

@Composable
private fun QuizAdvanceButton(
    enabled: Boolean,
    isLastQuestion: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(14.dp)

    Surface(
        shape = shape,
        color = if (enabled) accentColor else tokens.slateElevated,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_advance_submit_button")
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.97f)
            .clip(shape)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = ripple(color = Color.Black.copy(alpha = 0.2f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (isLastQuestion) {
                    stringResource(R.string.quiz_submit_exam)
                } else {
                    stringResource(R.string.quiz_next_question)
                },
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) tokens.amoledBackground else tokens.inkMuted,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = if (enabled) tokens.amoledBackground else tokens.inkMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

internal fun formatStopwatch(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val mins = safe / 60
    val secs = safe % 60
    return "%02d:%02d".format(mins, secs)
}
