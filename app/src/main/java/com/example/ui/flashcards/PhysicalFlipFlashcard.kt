package com.example.ui.flashcards

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.Swipe
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FlashcardRating
import com.example.model.SpacedFlashcard
import com.example.ui.components.AnodizedPill
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.FeedbackCorrect
import com.example.ui.theme.FeedbackIncorrect
import com.example.ui.theme.FlashcardPromptDisplay
import com.example.ui.theme.FormulaMonoDisplay
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.HumanistReadingBody
import com.example.ui.theme.MicroTagMono
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun PhysicalFlipFlashcard(
    card: SpacedFlashcard,
    nextPeekCard: SpacedFlashcard?,
    isFlipped: Boolean,
    onToggleFlip: () -> Unit,
    onSwipeRate: (FlashcardRating) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val offsetX = remember(card.id) { Animatable(0f) }
    val swipeThresholdPx = with(density) { 128.dp.toPx() }
    val flyOutPx = with(density) { 680.dp.toPx() }

    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = 0.78f,
            stiffness = 260f,
        ),
        label = "flashcardFlipRotationY",
    )

    val dragProgress = (offsetX.value / swipeThresholdPx).coerceIn(-1f, 1f)
    val absDrag = abs(dragProgress)

    val activeBorderColor by animateColorAsState(
        targetValue = when {
            dragProgress < -0.18f -> AccentMood.AMBER.accent.copy(alpha = 0.35f + 0.45f * absDrag)
            dragProgress > 0.18f -> FeedbackCorrect.copy(alpha = 0.35f + 0.45f * absDrag)
            isFlipped -> card.accentMood.accent.copy(alpha = 0.56f)
            else -> card.accentMood.accent.copy(alpha = 0.32f)
        },
        animationSpec = spring(stiffness = 420f),
        label = "flashcardActiveBorder",
    )

    val cardShape = RoundedCornerShape(24.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        // Recessed physical deck depth silhouette behind the active card
        if (nextPeekCard != null) {
            val peekLift = 0.945f + 0.035f * absDrag
            val peekOffsetY = with(density) { (14.dp - (6.dp * absDrag)).toPx() }
            Surface(
                shape = cardShape,
                color = tokens.obsidianSurface.copy(alpha = 0.68f),
                border = BorderStroke(1.dp, nextPeekCard.accentMood.accent.copy(alpha = 0.16f)),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = peekLift
                        scaleY = peekLift
                        translationY = peekOffsetY
                        alpha = 0.52f + 0.25f * absDrag
                    },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    Text(
                        text = "NEXT · ${nextPeekCard.subjectName.uppercase()} · ${nextPeekCard.intervalLabel}",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        // Foreground tactile 3D flippable & swipeable card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("flashcard_3d_card")
                .tactileScale(interactionSource, pressedScale = 0.985f, hoveredScale = 1.005f)
                .graphicsLayer {
                    translationX = offsetX.value
                    rotationZ = (offsetX.value / 40f).coerceIn(-9f, 9f)
                    this.rotationY = rotationY
                    cameraDistance = 14f * density.density
                }
                .clip(cardShape)
                .background(tokens.obsidianSurface, cardShape)
                .drawBehind {
                    // Subtle matte mineral surface wash (zero neon)
                    val washColor = when {
                        dragProgress < -0.18f -> AccentMood.AMBER.accent.copy(alpha = 0.10f * absDrag)
                        dragProgress > 0.18f -> FeedbackCorrect.copy(alpha = 0.10f * absDrag)
                        else -> card.accentMood.surfaceWash
                    }
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(washColor, Color.Transparent),
                            start = Offset(0f, 0f),
                            end = Offset(size.width * 0.78f, size.height * 0.88f),
                        ),
                        cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
                    )
                    // Anodized 1.25dp hairline stroke
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                activeBorderColor,
                                tokens.hairlineSubtle,
                            ),
                        ),
                        cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
                        style = Stroke(width = 1.25.dp.toPx()),
                    )
                }
                .pointerInput(card.id) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                when {
                                    offsetX.value <= -swipeThresholdPx -> {
                                        offsetX.animateTo(
                                            targetValue = -flyOutPx,
                                            animationSpec = tween(durationMillis = 190),
                                        )
                                        onSwipeRate(FlashcardRating.HARD)
                                    }

                                    offsetX.value >= swipeThresholdPx -> {
                                        offsetX.animateTo(
                                            targetValue = flyOutPx,
                                            animationSpec = tween(durationMillis = 190),
                                        )
                                        onSwipeRate(FlashcardRating.GOOD)
                                    }

                                    else -> {
                                        offsetX.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(
                                                dampingRatio = 0.72f,
                                                stiffness = 420f,
                                            ),
                                        )
                                    }
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = 0.72f,
                                        stiffness = 420f,
                                    ),
                                )
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount)
                            }
                        },
                    )
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = card.accentMood.accent.copy(alpha = 0.16f)),
                    role = Role.Button,
                    onClick = onToggleFlip,
                ),
        ) {
            if (rotationY <= 90f) {
                FlashcardFrontFace(
                    card = card,
                    dragProgress = dragProgress,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("flashcard_front_face"),
                )
            } else {
                FlashcardBackFace(
                    card = card,
                    dragProgress = dragProgress,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { this.rotationY = 180f }
                        .testTag("flashcard_back_face"),
                )
            }
        }
    }
}

@Composable
private fun FlashcardFrontFace(
    card: SpacedFlashcard,
    dragProgress: Float,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    Box(modifier = modifier.padding(24.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top structural metadata bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(card.accentMood.accent),
                    )
                    Text(
                        text = card.subjectName.uppercase(),
                        style = MicroTagMono,
                        color = card.accentMood.accent,
                    )
                    Text(
                        text = "·",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                    Text(
                        text = card.chapterTag,
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                AnodizedPill(
                    text = card.intervalLabel,
                    accentColor = card.accentMood.accent,
                    containerColor = card.accentMood.container,
                    textColor = card.accentMood.onContainer,
                    borderColor = card.accentMood.accent.copy(alpha = 0.36f),
                )
            }

            // Center prompt (oversized geometric display hierarchy)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "ACTIVE RECALL PROMPT",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = card.frontPrompt,
                    style = FlashcardPromptDisplay,
                    color = tokens.inkPrimary,
                )
            }

            // Bottom tactile cue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Flip,
                        contentDescription = null,
                        tint = card.accentMood.accent,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = stringResource(R.string.flashcards_tap_to_flip),
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.Swipe,
                    contentDescription = null,
                    tint = tokens.inkMuted,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        SwipeDirectionStamp(
            dragProgress = dragProgress,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun FlashcardBackFace(
    card: SpacedFlashcard,
    dragProgress: Float,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    Box(modifier = modifier.padding(24.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = card.accentMood.accent,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = "RECALL ANSWER · ${card.subjectName.uppercase()}",
                        style = MicroTagMono,
                        color = card.accentMood.accent,
                    )
                }

                AnodizedPill(
                    text = card.intervalLabel,
                    accentColor = card.accentMood.accent,
                    containerColor = tokens.carbonSurface,
                    textColor = tokens.inkSecondary,
                )
            }

            // Scrollable humanist body + monospace formula anchor + trap note
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = card.backAnswer,
                    style = HumanistReadingBody,
                    color = tokens.inkPrimary,
                )

                // Formula / Mnemonic callout box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, card.accentMood.accent.copy(alpha = 0.28f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Functions,
                                contentDescription = null,
                                tint = card.accentMood.accent,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = stringResource(R.string.flashcards_formula_hint_label),
                                style = MicroTagMono,
                                color = card.accentMood.accent,
                            )
                        }
                        Text(
                            text = card.formulaOrHint,
                            style = FormulaMonoDisplay,
                            color = tokens.inkPrimary,
                        )
                    }
                }

                // Exam trap note strip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AccentMood.AMBER.container.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, AccentMood.AMBER.accent.copy(alpha = 0.30f)),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WarningAmber,
                            contentDescription = null,
                            tint = AccentMood.AMBER.accent,
                            modifier = Modifier
                                .padding(top = 1.dp)
                                .size(15.dp),
                        )
                        Text(
                            text = card.trapNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentMood.AMBER.onContainer,
                        )
                    }
                }
            }

            // Bottom cue
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
                    text = stringResource(R.string.flashcards_tap_to_front),
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        SwipeDirectionStamp(
            dragProgress = dragProgress,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun SwipeDirectionStamp(
    dragProgress: Float,
    modifier: Modifier = Modifier,
) {
    val magnitude = abs(dragProgress)
    if (magnitude < 0.16f) return

    val isRight = dragProgress > 0f
    val label = if (isRight) "GOOD · x-3" else "HARD · x-1"
    val accent = if (isRight) FeedbackCorrect else FeedbackIncorrect
    val bg = if (isRight) AccentMood.EMERALD.container else AccentMood.AMBER.container

    Surface(
        shape = RoundedCornerShape(100.dp),
        color = bg.copy(alpha = (0.75f + 0.25f * magnitude).coerceIn(0f, 1f)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.72f)),
        modifier = modifier
            .padding(top = 4.dp)
            .graphicsLayer { alpha = ((magnitude - 0.16f) / 0.45f).coerceIn(0f, 1f) },
    ) {
        Text(
            text = label,
            style = MicroTagMono,
            color = accent,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
    }
}
