package com.example.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.LoggedTopic
import com.example.model.SpacedReviewItem
import com.example.ui.components.GhickinMotion
import com.example.ui.components.ObsidianCard
import com.example.ui.components.tactileScale
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle
import kotlinx.coroutines.delay

@Composable
fun TopicsAndReviewSection(
    yesterdayTopics: List<LoggedTopic>,
    spacedReviews: List<SpacedReviewItem>,
    isWideLayout: Boolean,
    onTopicClick: (LoggedTopic) -> Unit,
    onReviewClick: (SpacedReviewItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isWideLayout) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            YesterdayTopicsPane(
                topics = yesterdayTopics,
                onTopicClick = onTopicClick,
                modifier = Modifier.weight(1f),
            )
            SpacedReviewPane(
                reviews = spacedReviews,
                onReviewClick = onReviewClick,
                modifier = Modifier.weight(1f),
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            YesterdayTopicsPane(
                topics = yesterdayTopics,
                onTopicClick = onTopicClick,
            )
            SpacedReviewPane(
                reviews = spacedReviews,
                onReviewClick = onReviewClick,
            )
        }
    }
}

@Composable
private fun YesterdayTopicsPane(
    topics: List<LoggedTopic>,
    onTopicClick: (LoggedTopic) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    ObsidianCard(
        modifier = modifier.testTag("yesterday_topics_pane"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                    Icon(
                        imageVector = Icons.Rounded.HistoryEdu,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = stringResource(R.string.section_yesterday_topics),
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.inkPrimary,
                    )
                }
                Text(
                    text = stringResource(R.string.section_yesterday_meta),
                    style = MonoBadgeStyle,
                    color = tokens.inkMuted,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                topics.forEach { topic ->
                    LoggedTopicRow(
                        topic = topic,
                        onClick = { onTopicClick(topic) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LoggedTopicRow(
    topic: LoggedTopic,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(14.dp)
    val accent = topic.accentMood.accent

    Surface(
        shape = shape,
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("logged_topic_row_${topic.id}")
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.985f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accent.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Crisp vertical architectural color bar
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(34.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(accent),
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = topic.subjectName.uppercase(),
                        style = MonoBadgeStyle,
                        color = accent,
                    )
                    Text(
                        text = "• ${topic.durationMinutes}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkMuted,
                    )
                }
                Text(
                    text = topic.chapterTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = topic.subtopicSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = tokens.slateElevated,
                border = BorderStroke(1.dp, tokens.hairlineRest),
            ) {
                Text(
                    text = topic.confidenceLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.inkSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = tokens.inkMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun SpacedReviewPane(
    reviews: List<SpacedReviewItem>,
    onReviewClick: (SpacedReviewItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    ObsidianCard(
        modifier = modifier.testTag("spaced_review_pane"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = stringResource(R.string.section_spaced_review),
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.inkPrimary,
                    )
                }
                Text(
                    text = stringResource(R.string.section_spaced_meta),
                    style = MonoBadgeStyle,
                    color = tokens.inkMuted,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                reviews.forEachIndexed { index, review ->
                    SpacedReviewRow(
                        review = review,
                        delayMillis = 150L + (index * 60L),
                        onClick = { onReviewClick(review) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SpacedReviewRow(
    review: SpacedReviewItem,
    delayMillis: Long,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(14.dp)
    val accent = review.accentMood.accent

    val barProgress = remember { Animatable(0f) }
    LaunchedEffect(review.retentionPercent) {
        delay(delayMillis)
        barProgress.animateTo(
            targetValue = (review.retentionPercent / 100f).coerceIn(0f, 1f),
            animationSpec = GhickinMotion.RingSpring,
        )
    }

    Surface(
        shape = shape,
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("spaced_review_row_${review.id}")
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.985f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accent.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Surface(
                        shape = RoundedCornerShape(7.dp),
                        color = review.accentMood.container,
                        border = BorderStroke(1.dp, accent.copy(alpha = 0.30f)),
                    ) {
                        Text(
                            text = review.intervalBadge,
                            style = MonoBadgeStyle,
                            color = review.accentMood.onContainer,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        )
                    }
                    Text(
                        text = review.topicTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = review.dueLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.inkMuted,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "${review.subjectName} · ${review.cardCount} cards",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(tokens.trackSubtle),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(barProgress.value)
                            .height(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(accent),
                    )
                }
                Text(
                    text = "${review.retentionPercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                )
            }
        }
    }
}
