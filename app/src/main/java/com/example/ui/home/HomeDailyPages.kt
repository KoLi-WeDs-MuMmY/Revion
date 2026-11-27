package com.example.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Flip
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.DailyFlashcardItem
import com.example.model.DailyRevisionTakeaway
import com.example.model.HomeCommandState
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.FlashcardPromptDisplay
import com.example.ui.theme.FormulaMonoDisplay
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.HumanistReadingBody
import com.example.ui.theme.InkOnAccentDark
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.MonoMetricMedium

@Composable
fun TodayRevisionPage(
    state: HomeCommandState,
    isWideLayout: Boolean,
    onBackToHome: () -> Unit,
    onMarkComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBackToHome)

    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary
    val emerald = AccentMood.EMERALD.accent

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("today_revision_page"),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1220.dp)
                .padding(
                    horizontal = if (isWideLayout) 28.dp else 16.dp,
                    vertical = if (isWideLayout) 20.dp else 14.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DailyPageTopBar(
                title = stringResource(R.string.home_today_revision_title),
                subtitle = state.todayRevisionGeneratedLabel,
                onBackToHome = onBackToHome,
                backButtonTag = "today_revision_back_button",
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
            ) {
                item(key = "revision_hero_banner") {
                    StaggeredEntrance(delayMillis = 0L) {
                        DailyEditorialBanner(
                            drawableRes = R.drawable.img_today_revision_art,
                            contentDesc = stringResource(R.string.home_today_revision_title),
                            eyebrow = "YESTERDAY'S AI SYNTHESIS · ${state.todayRevisionTakeaways.size} HIGH-YIELD TAKEAWAYS",
                            headline = "Focused Active-Recall Digest",
                            caption = "Distilled strictly from yesterday's logged lectures and automatically archived under Vault → Revision.",
                            accentColor = primary,
                        )
                    }
                }

                itemsIndexed(
                    items = state.todayRevisionTakeaways,
                    key = { _, item -> item.id },
                ) { index, takeaway ->
                    StaggeredEntrance(delayMillis = 45L * (index + 1)) {
                        DailyRevisionTakeawayCard(
                            index = index + 1,
                            takeaway = takeaway,
                        )
                    }
                }
            }

            // Bottom completion CTA
            val completeInteraction = remember { MutableInteractionSource() }
            val ctaShape = RoundedCornerShape(16.dp)
            val ctaBg by animateColorAsState(
                targetValue = if (state.todayRevisionCompleted) emerald.copy(alpha = 0.16f) else emerald,
                animationSpec = spring(stiffness = 380f),
                label = "revCompleteBg",
            )
            val ctaInk = if (state.todayRevisionCompleted) emerald else InkOnAccentDark

            Surface(
                shape = ctaShape,
                color = ctaBg,
                border = if (state.todayRevisionCompleted) BorderStroke(1.dp, emerald.copy(alpha = 0.42f)) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("complete_today_revision_cta")
                    .minimumInteractiveComponentSize()
                    .tactileScale(completeInteraction, pressedScale = 0.985f)
                    .clip(ctaShape)
                    .clickable(
                        interactionSource = completeInteraction,
                        indication = ripple(color = Color.Black.copy(alpha = 0.18f)),
                        role = Role.Button,
                        onClick = onMarkComplete,
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = ctaInk,
                        modifier = Modifier.size(19.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.todayRevisionCompleted) {
                            stringResource(R.string.home_daily_revision_done_cta)
                        } else {
                            stringResource(R.string.home_daily_revision_complete_cta)
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = ctaInk,
                    )
                }
            }
        }
    }
}

@Composable
fun TodayFlashcardsPage(
    state: HomeCommandState,
    isWideLayout: Boolean,
    onBackToHome: () -> Unit,
    onFlipCard: () -> Unit,
    onStepCard: (Int) -> Unit,
    onMarkComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBackToHome)

    val tokens = GhickinTokens.colors
    val emerald = AccentMood.EMERALD.accent
    val cards = state.todayFlashcards
    val activeCard = state.currentDailyFlashcard
    val currentIndex = state.activeFlashcardIndex.coerceIn(0, (cards.size - 1).coerceAtLeast(0))

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("today_flashcards_page"),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1220.dp)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = if (isWideLayout) 28.dp else 16.dp,
                    vertical = if (isWideLayout) 20.dp else 14.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DailyPageTopBar(
                title = stringResource(R.string.home_today_flashcards_title),
                subtitle = state.todayRevisionGeneratedLabel,
                onBackToHome = onBackToHome,
                backButtonTag = "today_flashcards_back_button",
            )

            // Compact illustrated context header strip
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = tokens.obsidianSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_today_flashcards_art),
                        contentDescription = stringResource(R.string.home_today_flashcards_title),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(if (isWideLayout) 112.dp else 82.dp)
                            .height(62.dp)
                            .clip(RoundedCornerShape(12.dp)),
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = "YESTERDAY'S AI DECK · ${cards.size} ACTIVE RECALL CARDS",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                        Text(
                            text = "Tap the card to flip and verify formulas before stepping forward.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.inkSecondary,
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                    ) {
                        Text(
                            text = "${currentIndex + 1} / ${cards.size}",
                            style = MonoMetricMedium,
                            color = tokens.inkPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        )
                    }
                }
            }

            // 3D Tactile Flip Flashcard Stage
            if (activeCard != null) {
                AnimatedContent(
                    targetState = activeCard,
                    contentKey = { it.id },
                    transitionSpec = {
                        (fadeIn(tween(200)) + slideInHorizontally(spring(0.86f, 400f)) { it / 16 }) togetherWith
                            (fadeOut(tween(150)) + slideOutHorizontally(spring(0.86f, 400f)) { -it / 16 })
                    },
                    label = "dailyFlashcardStep",
                ) { card ->
                    DailyFlipFlashcardStage(
                        card = card,
                        isFlipped = state.isActiveFlashcardFlipped,
                        onFlip = onFlipCard,
                    )
                }
            }

            // Card navigation & completion footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val prevInteraction = remember { MutableInteractionSource() }
                val prevEnabled = currentIndex > 0
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, if (prevEnabled) tokens.hairlineRest else tokens.hairlineSubtle),
                    modifier = Modifier
                        .testTag("daily_flashcard_prev_button")
                        .minimumInteractiveComponentSize()
                        .tactileScale(prevInteraction, pressedScale = 0.96f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            enabled = prevEnabled,
                            interactionSource = prevInteraction,
                            indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.14f)),
                            role = Role.Button,
                            onClick = { onStepCard(-1) },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Previous card",
                            tint = if (prevEnabled) tokens.inkPrimary else tokens.inkMuted,
                            modifier = Modifier.size(17.dp),
                        )
                        Text(
                            text = "Prev",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (prevEnabled) tokens.inkPrimary else tokens.inkMuted,
                        )
                    }
                }

                val nextInteraction = remember { MutableInteractionSource() }
                val nextEnabled = currentIndex < cards.lastIndex
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, if (nextEnabled) tokens.hairlineRest else tokens.hairlineSubtle),
                    modifier = Modifier
                        .testTag("daily_flashcard_next_button")
                        .minimumInteractiveComponentSize()
                        .tactileScale(nextInteraction, pressedScale = 0.96f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            enabled = nextEnabled,
                            interactionSource = nextInteraction,
                            indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.14f)),
                            role = Role.Button,
                            onClick = { onStepCard(1) },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "Next",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (nextEnabled) tokens.inkPrimary else tokens.inkMuted,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = "Next card",
                            tint = if (nextEnabled) tokens.inkPrimary else tokens.inkMuted,
                            modifier = Modifier.size(17.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                val doneInteraction = remember { MutableInteractionSource() }
                val ctaShape = RoundedCornerShape(14.dp)
                val ctaBg = if (state.todayFlashcardsCompleted) emerald.copy(alpha = 0.16f) else emerald
                val ctaInk = if (state.todayFlashcardsCompleted) emerald else InkOnAccentDark
                Surface(
                    shape = ctaShape,
                    color = ctaBg,
                    border = if (state.todayFlashcardsCompleted) BorderStroke(1.dp, emerald.copy(alpha = 0.42f)) else null,
                    modifier = Modifier
                        .testTag("complete_today_flashcards_cta")
                        .minimumInteractiveComponentSize()
                        .tactileScale(doneInteraction, pressedScale = 0.97f)
                        .clip(ctaShape)
                        .clickable(
                            interactionSource = doneInteraction,
                            indication = ripple(color = Color.Black.copy(alpha = 0.18f)),
                            role = Role.Button,
                            onClick = onMarkComplete,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = ctaInk,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = if (state.todayFlashcardsCompleted) {
                                stringResource(R.string.home_daily_flashcards_done_cta)
                            } else {
                                stringResource(R.string.home_daily_flashcards_complete_cta)
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = ctaInk,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyPageTopBar(
    title: String,
    subtitle: String,
    onBackToHome: () -> Unit,
    backButtonTag: String,
) {
    val tokens = GhickinTokens.colors
    val emerald = AccentMood.EMERALD.accent
    val backInteraction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = tokens.obsidianSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .testTag(backButtonTag)
                .minimumInteractiveComponentSize()
                .tactileScale(backInteraction, pressedScale = 0.95f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = backInteraction,
                    indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.14f)),
                    role = Role.Button,
                    onClick = onBackToHome,
                ),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.home_daily_back_home),
                    tint = tokens.inkPrimary,
                    modifier = Modifier.size(17.dp),
                )
                Text(
                    text = stringResource(R.string.home_daily_back_home),
                    style = MaterialTheme.typography.labelLarge,
                    color = tokens.inkPrimary,
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = tokens.inkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Low-key "Auto-Saved to Vault" status pill
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, emerald.copy(alpha = 0.28f)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.CloudDone,
                    contentDescription = null,
                    tint = emerald,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = stringResource(R.string.home_auto_saved_vault),
                    style = MicroTagMono,
                    color = tokens.inkSecondary,
                )
            }
        }
    }
}

@Composable
private fun DailyEditorialBanner(
    drawableRes: Int,
    contentDesc: String,
    eyebrow: String,
    headline: String,
    caption: String,
    accentColor: Color,
) {
    val tokens = GhickinTokens.colors
    val shape = RoundedCornerShape(22.dp)

    Surface(
        shape = shape,
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = contentDesc,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .align(Alignment.CenterEnd)
                    .height(148.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            0.0f to tokens.obsidianSurface,
                            0.46f to tokens.obsidianSurface.copy(alpha = 0.96f),
                            0.78f to tokens.obsidianSurface.copy(alpha = 0.42f),
                            1.0f to tokens.obsidianSurface.copy(alpha = 0.18f),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.68f)
                    .padding(horizontal = 22.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = eyebrow,
                        style = MicroTagMono,
                        color = accentColor,
                    )
                }
                Text(
                    text = headline,
                    style = MaterialTheme.typography.headlineSmall,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun DailyRevisionTakeawayCard(
    index: Int,
    takeaway: DailyRevisionTakeaway,
) {
    val tokens = GhickinTokens.colors
    val subjectColor = takeaway.accentMood.accent

    ObsidianCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_revision_takeaway_${takeaway.id}"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(subjectColor),
                    )
                    Text(
                        text = "${takeaway.subjectName.uppercase()} · ${takeaway.chapterTitle}",
                        style = MonoBadgeStyle,
                        color = tokens.inkSecondary,
                    )
                }
                Text(
                    text = "0$index · ${takeaway.lectureRef}",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
            }

            Text(
                text = takeaway.headline,
                style = MaterialTheme.typography.titleMedium,
                color = tokens.inkPrimary,
            )

            Text(
                text = takeaway.detail,
                style = HumanistReadingBody,
                color = tokens.inkSecondary,
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(1.dp, tokens.hairlineSubtle),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = takeaway.formula,
                    style = FormulaMonoDisplay,
                    color = subjectColor,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun DailyFlipFlashcardStage(
    card: DailyFlashcardItem,
    isFlipped: Boolean,
    onFlip: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val accent = card.accentMood.accent
    val density = LocalDensity.current.density
    val interactionSource = remember { MutableInteractionSource() }

    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 320f),
        label = "dailyFlashcardRotation",
    )
    val showingBack = rotationY > 90f

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (showingBack) tokens.carbonSurface else tokens.obsidianSurface,
        border = BorderStroke(1.dp, if (showingBack) accent.copy(alpha = 0.42f) else tokens.hairlineRest),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 248.dp)
            .testTag("daily_flashcard_flip_stage")
            .tactileScale(interactionSource, pressedScale = 0.985f)
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 14f * density
            }
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accent.copy(alpha = 0.14f)),
                role = Role.Button,
                onClick = onFlip,
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .graphicsLayer {
                    if (showingBack) {
                        this.rotationY = 180f
                    }
                },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(accent),
                        )
                        Text(
                            text = "${card.subjectName.uppercase()} · ${card.chapterTitle}",
                            style = MonoBadgeStyle,
                            color = tokens.inkSecondary,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Flip,
                            contentDescription = null,
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = if (showingBack) "ANSWER · TAP TO FLIP BACK" else "PROMPT · TAP TO REVEAL",
                            style = MicroTagMono,
                            color = if (showingBack) accent else tokens.inkMuted,
                        )
                    }
                }

                if (!showingBack) {
                    Text(
                        text = card.frontPrompt,
                        style = FlashcardPromptDisplay,
                        color = tokens.inkPrimary,
                    )
                    Text(
                        text = card.classLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.inkMuted,
                    )
                } else {
                    Text(
                        text = card.backAnswer,
                        style = HumanistReadingBody,
                        color = tokens.inkPrimary,
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.obsidianSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Text(
                                text = "FORMULA ANCHOR",
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                            )
                            Text(
                                text = card.formulaHint,
                                style = FormulaMonoDisplay,
                                color = accent,
                            )
                        }
                    }
                }
            }
        }
    }
}
