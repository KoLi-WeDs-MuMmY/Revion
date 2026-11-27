package com.example.ui.flashcards

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesomeMotion
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FlashcardHubMode
import com.example.model.FlashcardRating
import com.example.model.FlashcardsMistakesUiState
import com.example.model.MistakeCategory
import com.example.model.SpacedIntervalBucket
import com.example.ui.components.tactileScale
import com.example.ui.mistakes.MistakeBankView
import com.example.ui.theme.FeedbackCorrect
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import kotlinx.coroutines.launch

@Composable
fun FlashcardsHubScreen(
    state: FlashcardsMistakesUiState,
    isWideLayout: Boolean,
    onSelectMode: (FlashcardHubMode) -> Unit,
    onSelectBucket: (SpacedIntervalBucket) -> Unit,
    onSelectCardSubjectFilter: (String?) -> Unit,
    onToggleCardFlip: () -> Unit,
    onRateCard: (cardId: String, rating: FlashcardRating) -> Unit,
    onResetCardSession: () -> Unit,
    onSelectMistakeCategory: (MistakeCategory?) -> Unit,
    onSelectMistakeSubject: (String?) -> Unit,
    onSelectMistakeRecord: (String?) -> Unit,
    onSendMistakeToWeekendDeck: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sentSnackbarText = stringResource(R.string.mistakes_sent_snackbar)

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("flashcards_hub_screen"),
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Header + Spring Mode Switcher
            if (isWideLayout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HubTitleBlock(modifier = Modifier.weight(1f))
                    FlashcardsModeSwitcher(
                        activeMode = state.activeMode,
                        remainingCardsCount = state.remainingDeckQueue.size,
                        mistakesCount = state.mistakes.size,
                        onSelectMode = onSelectMode,
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    HubTitleBlock()
                    FlashcardsModeSwitcher(
                        activeMode = state.activeMode,
                        remainingCardsCount = state.remainingDeckQueue.size,
                        mistakesCount = state.mistakes.size,
                        onSelectMode = onSelectMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Animated Workspace Mode Switch (Flashcard Deck <-> Mistake Bank)
            AnimatedContent(
                targetState = state.activeMode,
                transitionSpec = {
                    val forward = targetState.ordinal >= initialState.ordinal
                    val dir = if (forward) 1 else -1
                    val slideSpring = spring<IntOffset>(dampingRatio = 0.86f, stiffness = 390f)
                    (fadeIn(tween(220)) +
                        slideInHorizontally(animationSpec = slideSpring) { dir * (it / 12) }) togetherWith
                        (fadeOut(tween(150)) +
                            slideOutHorizontally(animationSpec = slideSpring) { -dir * (it / 14) })
                },
                label = "flashcardsHubModeTransition",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { mode ->
                when (mode) {
                    FlashcardHubMode.FLASHCARD_DECK -> FlashcardDeckView(
                        state = state,
                        isWideLayout = isWideLayout,
                        onSelectBucket = onSelectBucket,
                        onSelectSubjectFilter = onSelectCardSubjectFilter,
                        onToggleFlip = onToggleCardFlip,
                        onRateCard = onRateCard,
                        onResetSession = onResetCardSession,
                    )

                    FlashcardHubMode.MISTAKE_BANK -> MistakeBankView(
                        state = state,
                        isWideLayout = isWideLayout,
                        onSelectCategory = onSelectMistakeCategory,
                        onSelectSubject = onSelectMistakeSubject,
                        onSelectMistake = onSelectMistakeRecord,
                        onSendToWeekendDeck = { mistakeId ->
                            onSendMistakeToWeekendDeck(mistakeId)
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(
                                    message = sentSnackbarText,
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        },
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                shape = RoundedCornerShape(14.dp),
                containerColor = tokens.carbonSurface,
                contentColor = tokens.inkPrimary,
                actionColor = FeedbackCorrect,
            )
        }
    }
}

@Composable
private fun HubTitleBlock(modifier: Modifier = Modifier) {
    val tokens = GhickinTokens.colors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = stringResource(R.string.flashcards_hub_title),
            style = MaterialTheme.typography.headlineMedium,
            color = tokens.inkPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(R.string.flashcards_hub_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = tokens.inkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FlashcardsModeSwitcher(
    activeMode: FlashcardHubMode,
    remainingCardsCount: Int,
    mistakesCount: Int,
    onSelectMode: (FlashcardHubMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val primaryAccent = MaterialTheme.colorScheme.primary

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier.testTag("flashcards_hub_mode_switcher"),
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ModeSwitcherSegment(
                label = stringResource(R.string.flashcards_mode_deck),
                badge = remainingCardsCount.toString(),
                icon = Icons.Rounded.AutoAwesomeMotion,
                selected = activeMode == FlashcardHubMode.FLASHCARD_DECK,
                accentColor = primaryAccent,
                onClick = { onSelectMode(FlashcardHubMode.FLASHCARD_DECK) },
                modifier = Modifier.testTag("hub_mode_deck"),
            )
            ModeSwitcherSegment(
                label = stringResource(R.string.flashcards_mode_mistakes),
                badge = mistakesCount.toString(),
                icon = Icons.Rounded.ReportProblem,
                selected = activeMode == FlashcardHubMode.MISTAKE_BANK,
                accentColor = primaryAccent,
                onClick = { onSelectMode(FlashcardHubMode.MISTAKE_BANK) },
                modifier = Modifier.testTag("hub_mode_mistakes"),
            )
        }
    }
}

@Composable
private fun ModeSwitcherSegment(
    label: String,
    badge: String,
    icon: ImageVector,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(12.dp)

    val containerColor by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f),
        label = "modeSegmentBg",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.45f) else Color.Transparent,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f),
        label = "modeSegmentBorder",
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) tokens.inkPrimary else tokens.inkSecondary,
        animationSpec = spring(stiffness = 420f),
        label = "modeSegmentText",
    )

    Surface(
        shape = shape,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.96f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accentColor.copy(alpha = 0.16f)),
                role = Role.Tab,
                onClick = onClick,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) accentColor else tokens.inkMuted,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                maxLines = 1,
            )
            Text(
                text = badge,
                style = MicroTagMono,
                color = if (selected) accentColor else tokens.inkMuted,
                maxLines = 1,
            )
        }
    }
}
