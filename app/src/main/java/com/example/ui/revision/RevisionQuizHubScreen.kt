package com.example.ui.revision

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.model.QuizMode
import com.example.model.RevisionQuizTabMode
import com.example.model.RevisionQuizUiState
import com.example.ui.components.tactileScale
import com.example.ui.quiz.QuizArenaView
import com.example.ui.quiz.QuizResultBreakdownView
import com.example.ui.theme.GhickinTokens
import kotlinx.coroutines.launch

@Composable
fun RevisionQuizHubScreen(
    state: RevisionQuizUiState,
    isWideLayout: Boolean,
    onSelectMode: (RevisionQuizTabMode) -> Unit,
    onSelectRevisionSubject: (String) -> Unit,
    onToggleCompoundMix: () -> Unit,
    onToggleDiagramLabels: () -> Unit,
    onSelectQuizMode: (QuizMode) -> Unit,
    onSelectQuizSubject: (String) -> Unit,
    onSelectQuizOption: (questionId: String, optionIndex: Int, elapsedSeconds: Int) -> Unit,
    onAdvanceOrSubmitQuiz: (elapsedSeconds: Int) -> Unit,
    onRetryMissedQuestions: () -> Unit,
    onRetakeFullQuiz: () -> Unit,
    onJumpFromQuizToRevision: (preferredSubjectId: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val exportMessage = stringResource(R.string.revision_export_snackbar)

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("revision_quiz_hub_screen"),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1320.dp)
                .padding(
                    horizontal = if (isWideLayout) 24.dp else 16.dp,
                    vertical = if (isWideLayout) 18.dp else 14.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Top Hub Header + Spring-Animated Mode Switcher
            if (isWideLayout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = stringResource(R.string.revision_hub_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = stringResource(R.string.revision_hub_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }

                    HubModePillSwitcher(
                        activeMode = state.activeMode,
                        onSelectMode = onSelectMode,
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.revision_hub_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = tokens.inkPrimary,
                    )
                    HubModePillSwitcher(
                        activeMode = state.activeMode,
                        onSelectMode = onSelectMode,
                    )
                }
            }

            // Animated Switch between Revision Notes Reader and Subject Quiz Arena/Results
            AnimatedContent(
                targetState = state.activeMode,
                transitionSpec = {
                    val forward = targetState == RevisionQuizTabMode.SUBJECT_QUIZ
                    val dir = if (forward) 1 else -1
                    val springSpec = spring<IntOffset>(dampingRatio = 0.86f, stiffness = 380f)
                    (fadeIn(tween(220)) + slideInHorizontally(springSpec) { dir * (it / 16) }) togetherWith
                        (fadeOut(tween(140)) + slideOutHorizontally(springSpec) { -dir * (it / 18) })
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "revisionQuizModeSwitch",
            ) { mode ->
                when (mode) {
                    RevisionQuizTabMode.REVISION_NOTES -> RevisionNotesView(
                        state = state,
                        isWideLayout = isWideLayout,
                        onSelectSubject = onSelectRevisionSubject,
                        onToggleCompoundMix = onToggleCompoundMix,
                        onToggleLabels = onToggleDiagramLabels,
                        onExportRevisionPdf = {
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(exportMessage)
                            }
                        },
                    )

                    RevisionQuizTabMode.SUBJECT_QUIZ -> AnimatedContent(
                        targetState = state.isQuizSubmitted,
                        transitionSpec = {
                            fadeIn(tween(240)) togetherWith fadeOut(tween(160))
                        },
                        label = "quizArenaToResults",
                    ) { submitted ->
                        if (submitted) {
                            QuizResultBreakdownView(
                                state = state,
                                isWideLayout = isWideLayout,
                                onRetryMissed = onRetryMissedQuestions,
                                onJumpToRevisionNotes = onJumpFromQuizToRevision,
                                onRetakeFullQuiz = onRetakeFullQuiz,
                            )
                        } else {
                            QuizArenaView(
                                state = state,
                                isWideLayout = isWideLayout,
                                onSelectQuizMode = onSelectQuizMode,
                                onSelectQuizSubject = onSelectQuizSubject,
                                onSelectOption = onSelectQuizOption,
                                onAdvanceOrSubmit = onAdvanceOrSubmitQuiz,
                            )
                        }
                    }
                }
            }
        }

        // Sleek AMOLED SnackbarHost for mock PDF Export feedback
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = tokens.carbonSurface,
                contentColor = tokens.inkPrimary,
                shape = RoundedCornerShape(14.dp),
            )
        }
    }
}

@Composable
private fun HubModePillSwitcher(
    activeMode: RevisionQuizTabMode,
    onSelectMode: (RevisionQuizTabMode) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary

    Surface(
        shape = RoundedCornerShape(100.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier.testTag("revision_quiz_mode_switcher"),
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOf(
                Triple(
                    RevisionQuizTabMode.REVISION_NOTES,
                    stringResource(R.string.revision_mode_notes),
                    Icons.Rounded.AutoStories,
                ),
                Triple(
                    RevisionQuizTabMode.SUBJECT_QUIZ,
                    stringResource(R.string.revision_mode_quiz),
                    Icons.Rounded.Quiz,
                ),
            ).forEach { (mode, label, icon) ->
                val selected = mode == activeMode
                val pillBg by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    animationSpec = spring(dampingRatio = 0.82f, stiffness = 400f),
                    label = "hubModeBg_${mode.name}",
                )
                val pillBorder by animateColorAsState(
                    targetValue = if (selected) primary.copy(alpha = 0.45f) else Color.Transparent,
                    animationSpec = spring(stiffness = 400f),
                    label = "hubModeBorder_${mode.name}",
                )
                val interaction = remember { MutableInteractionSource() }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = pillBg,
                    border = BorderStroke(1.dp, pillBorder),
                    modifier = Modifier
                        .testTag("hub_mode_${mode.name.lowercase()}")
                        .minimumInteractiveComponentSize()
                        .tactileScale(interaction, pressedScale = 0.96f)
                        .clip(RoundedCornerShape(100.dp))
                        .clickable(
                            interactionSource = interaction,
                            indication = ripple(color = primary.copy(alpha = 0.18f)),
                            role = Role.Tab,
                            onClick = { onSelectMode(mode) },
                        ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (selected) primary else tokens.inkSecondary,
                            modifier = Modifier.size(17.dp),
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) tokens.inkPrimary else tokens.inkSecondary,
                        )
                    }
                }
            }
        }
    }
}
