package com.example.ui.mistakes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddTask
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FlashcardsMistakesUiState
import com.example.model.MistakeCategory
import com.example.model.MistakeRecord
import com.example.model.Phase14MockData
import com.example.ui.components.AnodizedPill
import com.example.ui.components.KineticFilterChip
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.AccentMood
import com.example.ui.theme.FeedbackCorrect
import com.example.ui.theme.FeedbackCorrectContainer
import com.example.ui.theme.FeedbackIncorrect
import com.example.ui.theme.FeedbackIncorrectContainer
import com.example.ui.theme.FormulaMonoDisplay
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.HumanistReadingBody
import com.example.ui.theme.InkOnAccentDark
import com.example.ui.theme.MicroTagMono

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MistakeBankView(
    state: FlashcardsMistakesUiState,
    isWideLayout: Boolean,
    onSelectCategory: (MistakeCategory?) -> Unit,
    onSelectSubject: (String?) -> Unit,
    onSelectMistake: (String?) -> Unit,
    onSendToWeekendDeck: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val primaryAccent = MaterialTheme.colorScheme.primary
    var showPortraitSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Filter Strip: Category + Subject
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KineticFilterChip(
                label = "All Traps",
                selected = state.selectedMistakeCategory == null,
                onClick = { onSelectCategory(null) },
                accentColor = primaryAccent,
                trailingBadge = state.mistakes.size.toString(),
                modifier = Modifier.testTag("mistake_category_chip_all"),
            )
            MistakeCategory.entries.forEach { category ->
                val catColor = categoryAccent(category)
                KineticFilterChip(
                    label = category.label,
                    selected = state.selectedMistakeCategory == category,
                    onClick = { onSelectCategory(category) },
                    accentColor = catColor,
                    leadingDotColor = catColor,
                    modifier = Modifier.testTag("mistake_category_chip_${category.name.lowercase()}"),
                )
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(1.dp)
                    .height(22.dp)
                    .background(tokens.hairlineRest),
            )

            KineticFilterChip(
                label = "All Subjects",
                selected = state.selectedMistakeSubject == null,
                onClick = { onSelectSubject(null) },
                accentColor = primaryAccent,
                modifier = Modifier.testTag("mistake_subject_chip_all"),
            )
            Phase14MockData.subjectFilterOrder.forEach { (subject, mood) ->
                KineticFilterChip(
                    label = subject,
                    selected = state.selectedMistakeSubject == subject,
                    onClick = { onSelectSubject(subject) },
                    accentColor = mood.accent,
                    leadingDotColor = mood.accent,
                    modifier = Modifier.testTag("mistake_subject_chip_${subject.lowercase()}"),
                )
            }
        }

        // Main Split Pane (Landscape) or Catalog + Sheet (Portrait)
        if (isWideLayout) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                MistakeCatalogList(
                    mistakes = state.filteredMistakes,
                    selectedId = state.selectedMistake?.id,
                    onClickMistake = { id -> onSelectMistake(id) },
                    modifier = Modifier
                        .weight(0.50f)
                        .fillMaxHeight(),
                )

                MistakeAutopsyInspectorPane(
                    mistake = state.selectedMistake,
                    onSendToWeekendDeck = onSendToWeekendDeck,
                    modifier = Modifier
                        .weight(0.50f)
                        .fillMaxHeight(),
                )
            }
        } else {
            MistakeCatalogList(
                mistakes = state.filteredMistakes,
                selectedId = state.selectedMistake?.id,
                onClickMistake = { id ->
                    onSelectMistake(id)
                    showPortraitSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )

            val activeMistake = state.selectedMistake
            if (showPortraitSheet && activeMistake != null) {
                ModalBottomSheet(
                    onDismissRequest = { showPortraitSheet = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = tokens.obsidianSurface,
                    contentColor = tokens.inkPrimary,
                ) {
                    MistakeAutopsyContent(
                        mistake = activeMistake,
                        onSendToWeekendDeck = onSendToWeekendDeck,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun MistakeCatalogList(
    mistakes: List<MistakeRecord>,
    selectedId: String?,
    onClickMistake: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    if (mistakes.isEmpty()) {
        ObsidianCard(
            contentPadding = PaddingValues(24.dp),
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Psychology,
                    contentDescription = null,
                    tint = tokens.inkMuted,
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.mistakes_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = tokens.inkPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.mistakes_empty_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 12.dp),
    ) {
        itemsIndexed(mistakes, key = { _, item -> item.id }) { index, item ->
            val selected = item.id == selectedId
            val catColor = categoryAccent(item.category)

            StaggeredEntrance(delayMillis = (index * 35L).coerceAtMost(180L)) {
                ObsidianCard(
                    accentEdgeColor = if (selected) item.accentMood.accent.copy(alpha = 0.62f) else tokens.hairlineRest,
                    surfaceWashColor = if (selected) item.accentMood.surfaceWash else Color.Transparent,
                    contentPadding = PaddingValues(15.dp),
                    onClick = { onClickMistake(item.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mistake_item_${item.id}"),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(item.accentMood.accent),
                                )
                                Text(
                                    text = item.subjectName.uppercase(),
                                    style = MicroTagMono,
                                    color = item.accentMood.accent,
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = catColor.copy(alpha = 0.14f),
                                    border = BorderStroke(1.dp, catColor.copy(alpha = 0.30f)),
                                ) {
                                    Text(
                                        text = item.category.badgeCode,
                                        style = MicroTagMono,
                                        color = catColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                if (item.addedToWeekendDeck) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = FeedbackCorrectContainer,
                                        border = BorderStroke(1.dp, FeedbackCorrect.copy(alpha = 0.36f)),
                                    ) {
                                        Text(
                                            text = "IN WKND DECK",
                                            style = MicroTagMono,
                                            color = FeedbackCorrect,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = FeedbackIncorrectContainer,
                                    border = BorderStroke(1.dp, FeedbackIncorrect.copy(alpha = 0.34f)),
                                ) {
                                    Text(
                                        text = "${item.mistakeCount}×",
                                        style = MicroTagMono,
                                        color = FeedbackIncorrect,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                    )
                                }
                            }
                        }

                        Text(
                            text = item.chapterTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            text = item.questionSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MistakeAutopsyInspectorPane(
    mistake: MistakeRecord?,
    onSendToWeekendDeck: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    ObsidianCard(
        accentEdgeColor = mistake?.accentMood?.accent?.copy(alpha = 0.36f) ?: tokens.hairlineRest,
        surfaceWashColor = mistake?.accentMood?.surfaceWash ?: Color.Transparent,
        contentPadding = PaddingValues(20.dp),
        modifier = modifier.testTag("mistake_autopsy_inspector"),
    ) {
        if (mistake == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.mistakes_empty_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.inkMuted,
                )
            }
        } else {
            AnimatedContent(
                targetState = mistake,
                transitionSpec = {
                    (fadeIn(tween(210)) +
                        slideInVertically(
                            animationSpec = spring(dampingRatio = 0.86f, stiffness = 380f),
                            initialOffsetY = { it / 16 },
                        )) togetherWith fadeOut(tween(130))
                },
                label = "mistakeAutopsyTransition",
                modifier = Modifier.fillMaxSize(),
            ) { activeItem ->
                MistakeAutopsyContent(
                    mistake = activeItem,
                    onSendToWeekendDeck = onSendToWeekendDeck,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                )
            }
        }
    }
}

@Composable
private fun MistakeAutopsyContent(
    mistake: MistakeRecord,
    onSendToWeekendDeck: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val ctaContainer by animateColorAsState(
        targetValue = if (mistake.addedToWeekendDeck) FeedbackCorrectContainer else mistake.accentMood.accent,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 360f),
        label = "mistakeCtaContainer",
    )
    val ctaContent by animateColorAsState(
        targetValue = if (mistake.addedToWeekendDeck) FeedbackCorrect else InkOnAccentDark,
        animationSpec = spring(stiffness = 360f),
        label = "mistakeCtaContent",
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Autopsy Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnodizedPill(
                text = stringResource(R.string.mistakes_inspector_badge),
                accentColor = mistake.accentMood.accent,
                containerColor = mistake.accentMood.container,
                textColor = mistake.accentMood.onContainer,
            )
            Text(
                text = mistake.sourceLabel,
                style = MaterialTheme.typography.labelSmall,
                color = tokens.inkMuted,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = mistake.chapterTitle,
                style = MaterialTheme.typography.headlineSmall,
                color = tokens.inkPrimary,
            )
            Text(
                text = mistake.questionSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.inkSecondary,
            )
        }

        // Comparison Card 1: Your Wrong Pick
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = FeedbackIncorrectContainer.copy(alpha = 0.78f),
            border = BorderStroke(1.dp, FeedbackIncorrect.copy(alpha = 0.36f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = FeedbackIncorrect,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = stringResource(R.string.mistakes_wrong_pick_label),
                        style = MicroTagMono,
                        color = FeedbackIncorrect,
                    )
                }
                Text(
                    text = mistake.wrongPick,
                    style = MaterialTheme.typography.titleSmall,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = mistake.wrongReason,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
            }
        }

        // Comparison Card 2: Correct Concept + Formula Anchor
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = FeedbackCorrectContainer.copy(alpha = 0.78f),
            border = BorderStroke(1.dp, FeedbackCorrect.copy(alpha = 0.36f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = FeedbackCorrect,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = stringResource(R.string.mistakes_correct_concept_label),
                        style = MicroTagMono,
                        color = FeedbackCorrect,
                    )
                }
                Text(
                    text = mistake.correctConcept,
                    style = HumanistReadingBody,
                    color = tokens.inkPrimary,
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = tokens.obsidianSurface.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, FeedbackCorrect.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Functions,
                            contentDescription = null,
                            tint = FeedbackCorrect,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = mistake.formulaAnchor,
                            style = FormulaMonoDisplay,
                            color = tokens.inkPrimary,
                        )
                    }
                }
            }
        }

        // Kinetic CTA: Send to Weekend Hard Deck
        Button(
            onClick = { onSendToWeekendDeck(mistake.id) },
            enabled = !mistake.addedToWeekendDeck,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ctaContainer,
                contentColor = ctaContent,
                disabledContainerColor = FeedbackCorrectContainer,
                disabledContentColor = FeedbackCorrect,
            ),
            border = if (mistake.addedToWeekendDeck) {
                BorderStroke(1.dp, FeedbackCorrect.copy(alpha = 0.45f))
            } else {
                null
            },
            modifier = Modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .testTag("send_mistake_to_weekend_button"),
        ) {
            Icon(
                imageVector = if (mistake.addedToWeekendDeck) Icons.Rounded.CheckCircle else Icons.Rounded.AddTask,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (mistake.addedToWeekendDeck) {
                    stringResource(R.string.mistakes_sent_weekend_cta)
                } else {
                    stringResource(R.string.mistakes_send_weekend_cta)
                },
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

private fun categoryAccent(category: MistakeCategory): Color {
    return when (category) {
        MistakeCategory.CONCEPT_GAP -> AccentMood.CYAN.accent
        MistakeCategory.CALCULATION_ERROR -> AccentMood.AMBER.accent
        MistakeCategory.SILLY_SLIP -> AccentMood.VIOLET.accent
    }
}
