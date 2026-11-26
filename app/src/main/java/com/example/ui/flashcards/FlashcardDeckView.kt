package com.example.ui.flashcards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FlashcardRating
import com.example.model.FlashcardsMistakesUiState
import com.example.model.Phase14MockData
import com.example.model.SpacedIntervalBucket
import com.example.ui.components.AnimatedMasteryRing
import com.example.ui.components.AnodizedPill
import com.example.ui.components.KineticFilterChip
import com.example.ui.components.KineticNumberText
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.FeedbackCorrect
import com.example.ui.theme.FeedbackIncorrect
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.InkOnAccentDark
import com.example.ui.theme.KineticDisplayLarge
import com.example.ui.theme.KineticDisplayXL
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoMetricMedium
import com.example.ui.theme.SpaceGroteskFamily

@Composable
fun FlashcardDeckView(
    state: FlashcardsMistakesUiState,
    isWideLayout: Boolean,
    onSelectBucket: (SpacedIntervalBucket) -> Unit,
    onSelectSubjectFilter: (String?) -> Unit,
    onToggleFlip: () -> Unit,
    onRateCard: (cardId: String, rating: FlashcardRating) -> Unit,
    onResetSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isWideLayout) {
        Row(
            modifier = modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            FlashcardControlPane(
                state = state,
                onSelectBucket = onSelectBucket,
                onSelectSubjectFilter = onSelectSubjectFilter,
                modifier = Modifier
                    .weight(0.34f)
                    .fillMaxHeight(),
            )
            FlashcardStagePane(
                state = state,
                onToggleFlip = onToggleFlip,
                onRateCard = onRateCard,
                onResetSession = onResetSession,
                modifier = Modifier
                    .weight(0.66f)
                    .fillMaxHeight(),
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FlashcardControlPane(
                state = state,
                onSelectBucket = onSelectBucket,
                onSelectSubjectFilter = onSelectSubjectFilter,
                scrollable = false,
                modifier = Modifier.fillMaxWidth(),
            )
            FlashcardStagePane(
                state = state,
                onToggleFlip = onToggleFlip,
                onRateCard = onRateCard,
                onResetSession = onResetSession,
                compactStage = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlashcardControlPane(
    state: FlashcardsMistakesUiState,
    onSelectBucket: (SpacedIntervalBucket) -> Unit,
    onSelectSubjectFilter: (String?) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
) {
    val tokens = GhickinTokens.colors
    val primaryAccent = MaterialTheme.colorScheme.primary
    val scrollMod = if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier

    Column(
        modifier = modifier.then(scrollMod),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Oversized Kinetic Counter + Mastery Ring card
        StaggeredEntrance(delayMillis = 20L) {
            ObsidianCard(
                contentPadding = PaddingValues(16.dp),
                accentEdgeColor = primaryAccent.copy(alpha = 0.28f),
                surfaceWashColor = primaryAccent.copy(alpha = 0.06f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = stringResource(R.string.flashcards_due_remaining).uppercase(),
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                        KineticNumberText(
                            targetValue = state.remainingDeckQueue.size,
                            style = KineticDisplayXL,
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = "${state.goodOrEasyCount} solid · ${state.hardOrAgainCount} repeat",
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.inkSecondary,
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        AnimatedMasteryRing(
                            percentage = state.sessionRecallPercent,
                            accentColor = primaryAccent,
                            ringSize = 66.dp,
                            strokeWidth = 5.5.dp,
                        )
                        Text(
                            text = stringResource(R.string.flashcards_session_mastery),
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                    }
                }
            }
        }

        // Spaced Interval Schedule selector pills
        StaggeredEntrance(delayMillis = 70L) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SPACED SCHEDULE",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )

                SpacedIntervalBucket.entries.forEach { bucket ->
                    val selected = state.activeBucket == bucket
                    val remainingInBucket = state.cardsInBucket(bucket, state.selectedCardSubject)
                        .count { it.id !in state.completedCardIds }

                    IntervalSchedulePill(
                        bucket = bucket,
                        selected = selected,
                        remainingCount = remainingInBucket,
                        accentColor = bucketAccentColor(bucket, primaryAccent),
                        onClick = { onSelectBucket(bucket) },
                        modifier = Modifier.testTag("bucket_pill_${bucket.name.lowercase()}"),
                    )
                }
            }
        }

        // Subject color filter chips
        StaggeredEntrance(delayMillis = 120L) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SUBJECT FILTER",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    KineticFilterChip(
                        label = "All",
                        selected = state.selectedCardSubject == null,
                        onClick = { onSelectSubjectFilter(null) },
                        accentColor = primaryAccent,
                        modifier = Modifier.testTag("flashcard_subject_chip_all"),
                    )
                    Phase14MockData.subjectFilterOrder.forEach { (subject, mood) ->
                        KineticFilterChip(
                            label = subject,
                            selected = state.selectedCardSubject == subject,
                            onClick = { onSelectSubjectFilter(subject) },
                            accentColor = mood.accent,
                            leadingDotColor = mood.accent,
                            modifier = Modifier.testTag("flashcard_subject_chip_${subject.lowercase()}"),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IntervalSchedulePill(
    bucket: SpacedIntervalBucket,
    selected: Boolean,
    remainingCount: Int,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(16.dp)

    val bgColor by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.14f) else tokens.obsidianSurface,
        animationSpec = spring(dampingRatio = 0.84f, stiffness = 420f),
        label = "bucketBg",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.56f) else tokens.hairlineRest,
        animationSpec = spring(dampingRatio = 0.84f, stiffness = 420f),
        label = "bucketBorder",
    )

    Surface(
        shape = shape,
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.97f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accentColor.copy(alpha = 0.16f)),
                role = Role.RadioButton,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selected) accentColor.copy(alpha = 0.22f) else tokens.carbonSurface,
                    )
                    .padding(horizontal = 8.dp, vertical = 5.dp),
            ) {
                Text(
                    text = bucket.shortBadge,
                    style = MicroTagMono,
                    color = if (selected) accentColor else tokens.inkSecondary,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                Text(
                    text = bucket.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (selected) tokens.inkPrimary else tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = bucket.cadenceDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = remainingCount.toString(),
                style = MonoMetricMedium,
                color = if (selected) accentColor else tokens.inkSecondary,
            )
        }
    }
}

@Composable
private fun FlashcardStagePane(
    state: FlashcardsMistakesUiState,
    onToggleFlip: () -> Unit,
    onRateCard: (cardId: String, rating: FlashcardRating) -> Unit,
    onResetSession: () -> Unit,
    modifier: Modifier = Modifier,
    compactStage: Boolean = false,
) {
    val tokens = GhickinTokens.colors
    val currentCard = state.currentCard
    val bucketAccent = bucketAccentColor(state.activeBucket, MaterialTheme.colorScheme.primary)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Stage header bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AnodizedPill(
                    text = state.activeBucket.shortBadge,
                    accentColor = bucketAccent,
                    containerColor = bucketAccent.copy(alpha = 0.14f),
                    textColor = bucketAccent,
                )
                Text(
                    text = state.activeBucket.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (currentCard != null) {
                Text(
                    text = "QUEUE · ${state.remainingDeckQueue.size} LEFT",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
            }
        }

        // Physical 3D card stage OR Session Complete obsidian state
        val cardAreaModifier = if (compactStage) {
            Modifier
                .fillMaxWidth()
                .height(340.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .weight(1f)
        }

        AnimatedContent(
            targetState = Pair(state.deckNonce, currentCard?.id),
            transitionSpec = {
                val springSlide = spring<IntOffset>(dampingRatio = 0.82f, stiffness = 360f)
                (fadeIn(tween(220)) +
                    scaleIn(initialScale = 0.94f, animationSpec = spring(dampingRatio = 0.82f, stiffness = 360f)) +
                    slideInHorizontally(animationSpec = springSlide) { it / 10 }) togetherWith
                    (fadeOut(tween(150)) + slideOutHorizontally(animationSpec = springSlide) { -it / 10 })
            },
            label = "flashcardStageTransition",
            modifier = cardAreaModifier,
        ) { (_, _) ->
            if (currentCard != null) {
                PhysicalFlipFlashcard(
                    card = currentCard,
                    nextPeekCard = state.nextPeekCard,
                    isFlipped = state.isCardFlipped,
                    onToggleFlip = onToggleFlip,
                    onSwipeRate = { rating -> onRateCard(currentCard.id, rating) },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                SessionCompleteCard(
                    state = state,
                    accentColor = bucketAccent,
                    onResetSession = onResetSession,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // 4 Tactile Rating Buttons (Again / Hard / Good / Easy)
        if (currentCard != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                FlashcardRating.entries.forEach { rating ->
                    val ratingColor = when (rating) {
                        FlashcardRating.AGAIN -> FeedbackIncorrect
                        FlashcardRating.HARD -> AccentMood.AMBER.accent
                        FlashcardRating.GOOD -> FeedbackCorrect
                        FlashcardRating.EASY -> AccentMood.CYAN.accent
                    }
                    FlashcardRatingButton(
                        rating = rating,
                        accentColor = ratingColor,
                        onClick = { onRateCard(currentCard.id, rating) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("rate_button_${rating.name.lowercase()}"),
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashcardRatingButton(
    rating: FlashcardRating,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(16.dp)

    Surface(
        shape = shape,
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.36f)),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.945f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accentColor.copy(alpha = 0.20f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .background(accentColor.copy(alpha = 0.08f))
                .padding(horizontal = 8.dp, vertical = 11.dp),
        ) {
            Text(
                text = rating.label,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleSmall,
                color = tokens.inkPrimary,
                maxLines = 1,
            )
            Text(
                text = rating.nextInterval,
                style = MicroTagMono,
                color = accentColor,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SessionCompleteCard(
    state: FlashcardsMistakesUiState,
    accentColor: Color,
    onResetSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    ObsidianCard(
        accentEdgeColor = FeedbackCorrect.copy(alpha = 0.45f),
        surfaceWashColor = FeedbackCorrect.copy(alpha = 0.07f),
        contentPadding = PaddingValues(28.dp),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AccentMood.EMERALD.container),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = null,
                    tint = FeedbackCorrect,
                    modifier = Modifier.size(28.dp),
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.flashcards_session_complete_title),
                style = MaterialTheme.typography.headlineSmall,
                color = tokens.inkPrimary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.flashcards_session_complete_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.inkSecondary,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KineticNumberText(
                        targetValue = state.goodOrEasyCount,
                        style = KineticDisplayLarge,
                        color = FeedbackCorrect,
                    )
                    Text(
                        text = "GOOD / EASY",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(34.dp)
                        .background(tokens.hairlineRest),
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KineticNumberText(
                        targetValue = state.hardOrAgainCount,
                        style = KineticDisplayLarge,
                        color = AccentMood.AMBER.accent,
                    )
                    Text(
                        text = "HARD / AGAIN",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = onResetSession,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = InkOnAccentDark,
                ),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("flashcards_reset_session_button"),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.flashcards_reset_session),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

private fun bucketAccentColor(bucket: SpacedIntervalBucket, defaultAccent: Color): Color {
    return when (bucket) {
        SpacedIntervalBucket.TODAY_X1 -> AccentMood.CYAN.accent
        SpacedIntervalBucket.RECALL_X3 -> AccentMood.VIOLET.accent
        SpacedIntervalBucket.WEEKEND_HARD -> AccentMood.AMBER.accent
        SpacedIntervalBucket.COMPOUND_MIX -> defaultAccent
    }
}
