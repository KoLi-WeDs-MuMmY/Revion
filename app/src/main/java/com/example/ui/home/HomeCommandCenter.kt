package com.example.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.DailyPageRoute
import com.example.model.HomeCommandState
import com.example.model.StudyBatch
import com.example.model.WorkspaceUiState
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.AnimatedMasteryRing
import com.example.ui.components.KineticNumberText
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.InkOnAccentDark
import com.example.ui.theme.KineticDisplayLarge
import com.example.ui.theme.KineticDisplayXL
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.MonoMetricMedium
import com.example.ui.theme.rememberScreenLayoutMetrics

@Composable
fun HomeCommandCenter(
    uiState: WorkspaceUiState,
    isWideLayout: Boolean,
    onToggleFocusMode: () -> Unit,
    onSelectAccentMood: (AccentMood) -> Unit,
    onCycleAccentMood: () -> Unit,
    onCyclePrimaryBatch: () -> Unit,
    onOpenDailyPage: (DailyPageRoute) -> Unit,
    onCloseDailyPage: () -> Unit,
    onCompleteTodayRevision: () -> Unit,
    onFlipDailyFlashcard: () -> Unit,
    onStepDailyFlashcard: (Int) -> Unit,
    onCompleteTodayFlashcards: () -> Unit,
    onToggleTodayRevisionCompleted: () -> Unit,
    onToggleTodayFlashcardsCompleted: () -> Unit,
    onToggleStudyGateTestOverride: () -> Unit,
    onStartTodaysNewStudy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val homeState = uiState.homeCommandState

    AnimatedContent(
        targetState = homeState.activeDailyPage,
        transitionSpec = {
            (fadeIn(tween(220)) + slideInVertically(spring(0.86f, 400f)) { it / 20 }) togetherWith
                (fadeOut(tween(150)) + slideOutVertically(spring(0.86f, 400f)) { -it / 24 })
        },
        label = "homeOrDailyPageTransition",
        modifier = modifier.fillMaxSize(),
    ) { dailyRoute ->
        when (dailyRoute) {
            DailyPageRoute.TODAY_REVISION -> TodayRevisionPage(
                state = homeState,
                isWideLayout = isWideLayout,
                onBackToHome = onCloseDailyPage,
                onMarkComplete = onCompleteTodayRevision,
            )

            DailyPageRoute.TODAY_FLASHCARDS -> TodayFlashcardsPage(
                state = homeState,
                isWideLayout = isWideLayout,
                onBackToHome = onCloseDailyPage,
                onFlipCard = onFlipDailyFlashcard,
                onStepCard = onStepDailyFlashcard,
                onMarkComplete = onCompleteTodayFlashcards,
            )

            null -> HomeCommandCanvas(
                uiState = uiState,
                isWideLayout = isWideLayout,
                onToggleFocusMode = onToggleFocusMode,
                onSelectAccentMood = onSelectAccentMood,
                onCycleAccentMood = onCycleAccentMood,
                onCyclePrimaryBatch = onCyclePrimaryBatch,
                onOpenDailyPage = onOpenDailyPage,
                onToggleTodayRevisionCompleted = onToggleTodayRevisionCompleted,
                onToggleTodayFlashcardsCompleted = onToggleTodayFlashcardsCompleted,
                onToggleStudyGateTestOverride = onToggleStudyGateTestOverride,
                onStartTodaysNewStudy = onStartTodaysNewStudy,
            )
        }
    }
}

@Composable
private fun HomeCommandCanvas(
    uiState: WorkspaceUiState,
    isWideLayout: Boolean,
    onToggleFocusMode: () -> Unit,
    onSelectAccentMood: (AccentMood) -> Unit,
    onCycleAccentMood: () -> Unit,
    onCyclePrimaryBatch: () -> Unit,
    onOpenDailyPage: (DailyPageRoute) -> Unit,
    onToggleTodayRevisionCompleted: () -> Unit,
    onToggleTodayFlashcardsCompleted: () -> Unit,
    onToggleStudyGateTestOverride: () -> Unit,
    onStartTodaysNewStudy: () -> Unit,
) {
    val primaryBatch = uiState.primaryBatch
    val homeState = uiState.homeCommandState
    val metrics = rememberScreenLayoutMetrics()
    var showLockedReminder by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_command_center"),
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
            verticalArrangement = Arrangement.spacedBy(metrics.sectionSpacing),
        ) {
            item(key = "home_header") {
                StaggeredEntrance(delayMillis = 0L) {
                    HomeHeaderBar(
                        streakDays = uiState.streakDays,
                        focusMode = uiState.focusMode,
                        activeMood = uiState.accentMood,
                        isWideLayout = isWideLayout,
                        onToggleFocusMode = onToggleFocusMode,
                        onSelectMood = onSelectAccentMood,
                        onCycleMood = onCycleAccentMood,
                    )
                }
            }

            // Row 1: Primary Batch Syllabus Progress + Clean Growth Stat Cards
            item(key = "home_primary_and_stats") {
                StaggeredEntrance(delayMillis = 55L) {
                    if (isWideLayout) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            PrimaryBatchSyllabusCard(
                                batch = primaryBatch,
                                batchCount = uiState.batchesState.batches.size,
                                onCyclePrimaryBatch = onCyclePrimaryBatch,
                                modifier = Modifier.weight(0.60f),
                            )
                            Column(
                                modifier = Modifier.weight(0.40f),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                GrowthMetricCard(
                                    title = stringResource(R.string.home_stat_improvement),
                                    metricValue = homeState.improvementPercentDelta,
                                    subtitle = homeState.improvementSubtitle,
                                    accentColor = AccentMood.EMERALD.accent,
                                    icon = Icons.AutoMirrored.Rounded.TrendingUp,
                                    testTag = "home_stat_improvement_card",
                                )
                                GrowthMetricCard(
                                    title = stringResource(R.string.home_stat_syllabus_gain),
                                    metricValue = homeState.syllabusCoverageGainPercent,
                                    subtitle = homeState.syllabusGainSubtitle,
                                    accentColor = MaterialTheme.colorScheme.primary,
                                    icon = Icons.Rounded.AutoGraph,
                                    testTag = "home_stat_syllabus_gain_card",
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            PrimaryBatchSyllabusCard(
                                batch = primaryBatch,
                                batchCount = uiState.batchesState.batches.size,
                                onCyclePrimaryBatch = onCyclePrimaryBatch,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                GrowthMetricCard(
                                    title = stringResource(R.string.home_stat_improvement),
                                    metricValue = homeState.improvementPercentDelta,
                                    subtitle = homeState.improvementSubtitle,
                                    accentColor = AccentMood.EMERALD.accent,
                                    icon = Icons.AutoMirrored.Rounded.TrendingUp,
                                    testTag = "home_stat_improvement_card",
                                    modifier = Modifier.weight(1f),
                                )
                                GrowthMetricCard(
                                    title = stringResource(R.string.home_stat_syllabus_gain),
                                    metricValue = homeState.syllabusCoverageGainPercent,
                                    subtitle = homeState.syllabusGainSubtitle,
                                    accentColor = MaterialTheme.colorScheme.primary,
                                    icon = Icons.Rounded.AutoGraph,
                                    testTag = "home_stat_syllabus_gain_card",
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }

            // Row 2: Materialized Illustrated Cards — Today's Revision & Today's Flashcards
            item(key = "home_daily_cards") {
                StaggeredEntrance(delayMillis = 110L) {
                    if (isWideLayout) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            DailyIllustratedGateCard(
                                title = stringResource(R.string.home_today_revision_title),
                                subtitle = stringResource(R.string.home_from_yesterday_ai),
                                itemCountLabel = "${homeState.todayRevisionTakeaways.size} Takeaways",
                                artworkRes = R.drawable.img_today_revision_art,
                                accentColor = AccentMood.CYAN.accent,
                                isCompleted = homeState.todayRevisionCompleted,
                                testTag = "home_today_revision_card",
                                onClick = { onOpenDailyPage(DailyPageRoute.TODAY_REVISION) },
                                modifier = Modifier.weight(1f),
                            )
                            DailyIllustratedGateCard(
                                title = stringResource(R.string.home_today_flashcards_title),
                                subtitle = stringResource(R.string.home_from_yesterday_ai),
                                itemCountLabel = "${homeState.todayFlashcards.size} Recall Cards",
                                artworkRes = R.drawable.img_today_flashcards_art,
                                accentColor = AccentMood.AMBER.accent,
                                isCompleted = homeState.todayFlashcardsCompleted,
                                testTag = "home_today_flashcards_card",
                                onClick = { onOpenDailyPage(DailyPageRoute.TODAY_FLASHCARDS) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            DailyIllustratedGateCard(
                                title = stringResource(R.string.home_today_revision_title),
                                subtitle = stringResource(R.string.home_from_yesterday_ai),
                                itemCountLabel = "${homeState.todayRevisionTakeaways.size} Takeaways",
                                artworkRes = R.drawable.img_today_revision_art,
                                accentColor = AccentMood.CYAN.accent,
                                isCompleted = homeState.todayRevisionCompleted,
                                testTag = "home_today_revision_card",
                                onClick = { onOpenDailyPage(DailyPageRoute.TODAY_REVISION) },
                            )
                            DailyIllustratedGateCard(
                                title = stringResource(R.string.home_today_flashcards_title),
                                subtitle = stringResource(R.string.home_from_yesterday_ai),
                                itemCountLabel = "${homeState.todayFlashcards.size} Recall Cards",
                                artworkRes = R.drawable.img_today_flashcards_art,
                                accentColor = AccentMood.AMBER.accent,
                                isCompleted = homeState.todayFlashcardsCompleted,
                                testTag = "home_today_flashcards_card",
                                onClick = { onOpenDailyPage(DailyPageRoute.TODAY_FLASHCARDS) },
                            )
                        }
                    }
                }
            }

            // Row 3: Honest Study Gate Lock Bar + Start Today's New Study CTA
            item(key = "home_study_gate") {
                StaggeredEntrance(delayMillis = 165L) {
                    StudyGateControlBar(
                        homeState = homeState,
                        primaryBatchName = primaryBatch.name,
                        showLockedReminder = showLockedReminder && !homeState.isStudyGateUnlocked,
                        isWideLayout = isWideLayout,
                        onToggleRevision = {
                            showLockedReminder = false
                            onToggleTodayRevisionCompleted()
                        },
                        onToggleFlashcards = {
                            showLockedReminder = false
                            onToggleTodayFlashcardsCompleted()
                        },
                        onToggleTestOverride = {
                            showLockedReminder = false
                            onToggleStudyGateTestOverride()
                        },
                        onStartNewStudyClick = {
                            if (homeState.isStudyGateUnlocked) {
                                showLockedReminder = false
                                onStartTodaysNewStudy()
                            } else {
                                showLockedReminder = true
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PrimaryBatchSyllabusCard(
    batch: StudyBatch,
    batchCount: Int,
    onCyclePrimaryBatch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val batchAccent = batch.accentMood.accent
    val switchInteraction = remember { MutableInteractionSource() }

    ObsidianCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_primary_batch_card"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(batchAccent),
                            )
                            Text(
                                text = stringResource(R.string.home_primary_batch_tag),
                                style = MicroTagMono,
                                color = tokens.inkPrimary,
                            )
                        }
                    }
                    Text(
                        text = batch.goalTag.uppercase(),
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                }

                if (batchCount > 1) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier
                            .testTag("home_switch_primary_batch_button")
                            .minimumInteractiveComponentSize()
                            .tactileScale(switchInteraction, pressedScale = 0.95f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = switchInteraction,
                                indication = ripple(color = batchAccent.copy(alpha = 0.16f)),
                                role = Role.Button,
                                onClick = onCyclePrimaryBatch,
                            ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SwapHoriz,
                                contentDescription = stringResource(R.string.home_switch_batch),
                                tint = tokens.inkSecondary,
                                modifier = Modifier.size(15.dp),
                            )
                            Text(
                                text = stringResource(R.string.home_switch_batch),
                                style = MaterialTheme.typography.labelMedium,
                                color = tokens.inkPrimary,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = batch.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${stringResource(R.string.home_syllabus_coverage)} · ${batch.completedChapters}/${batch.totalChapters} chapters · ${batch.totalHoursLogged}h logged",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        KineticNumberText(
                            targetValue = batch.overallProgressPercent,
                            suffix = "%",
                            style = KineticDisplayXL,
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = "OF PRIMARY SYLLABUS",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                AnimatedMasteryRing(
                    percentage = batch.overallProgressPercent,
                    accentColor = batchAccent,
                    ringSize = 82.dp,
                    strokeWidth = 6.dp,
                )
            }

            // Disciplined per-subject progress strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                batch.subjects.take(4).forEach { subject ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tokens.carbonSurface)
                            .padding(horizontal = 10.dp, vertical = 9.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = subject.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = tokens.inkPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "${subject.masteryPercent}%",
                                style = MicroTagMono,
                                color = tokens.inkSecondary,
                            )
                        }
                        AnimatedLinearProgressBar(
                            progressPercent = subject.masteryPercent,
                            accentColor = subject.accentMood.accent,
                            height = 4.dp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GrowthMetricCard(
    title: String,
    metricValue: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector,
    testTag: String,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    ObsidianCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title.uppercase(),
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
                Text(
                    text = metricValue,
                    style = KineticDisplayLarge,
                    color = accentColor,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tokens.carbonSurface),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(21.dp),
                )
            }
        }
    }
}

@Composable
private fun DailyIllustratedGateCard(
    title: String,
    subtitle: String,
    itemCountLabel: String,
    artworkRes: Int,
    accentColor: Color,
    isCompleted: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()
    val emerald = AccentMood.EMERALD.accent
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(20.dp)

    val borderColor by animateColorAsState(
        targetValue = if (isCompleted) emerald.copy(alpha = 0.36f) else tokens.hairlineRest,
        animationSpec = spring(stiffness = 380f),
        label = "dailyCardBorder_$testTag",
    )

    Surface(
        shape = shape,
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .height(metrics.dailyCardHeight)
            .testTag(testTag)
            .tactileScale(interactionSource, pressedScale = 0.98f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.14f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Custom bundled non-neon architectural artwork anchored cleanly to right half
            Image(
                painter = painterResource(id = artworkRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.56f)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd),
            )

            // Smooth matte obsidian scrim ensuring crisp readability on text side
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            0.0f to tokens.obsidianSurface,
                            0.48f to tokens.obsidianSurface.copy(alpha = 0.96f),
                            0.78f to tokens.obsidianSurface.copy(alpha = 0.46f),
                            1.0f to tokens.obsidianSurface.copy(alpha = 0.14f),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Top row: status pill + quiet Auto-Saved tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isCompleted) emerald.copy(alpha = 0.38f) else tokens.hairlineRest,
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = emerald,
                                    modifier = Modifier.size(12.dp),
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(accentColor),
                                )
                            }
                            Text(
                                text = if (isCompleted) "COMPLETED" else "REQUIRED FIRST",
                                style = MicroTagMono,
                                color = if (isCompleted) emerald else tokens.inkSecondary,
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tokens.obsidianSurface.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CloudDone,
                                contentDescription = null,
                                tint = tokens.inkMuted,
                                modifier = Modifier.size(12.dp),
                            )
                            Text(
                                text = stringResource(R.string.home_auto_saved_vault),
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                            )
                        }
                    }
                }

                // Middle title & yesterday AI context
                Column(
                    modifier = Modifier.fillMaxWidth(0.72f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
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
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Footer action cue
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = itemCountLabel,
                        style = MonoBadgeStyle,
                        color = tokens.inkPrimary,
                    )
                    Text(
                        text = "·",
                        style = MonoBadgeStyle,
                        color = tokens.inkMuted,
                    )
                    Text(
                        text = if (isCompleted) "Review Again" else "Open Daily Page",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isCompleted) emerald else accentColor,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = if (isCompleted) emerald else accentColor,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StudyGateControlBar(
    homeState: HomeCommandState,
    primaryBatchName: String,
    showLockedReminder: Boolean,
    isWideLayout: Boolean,
    onToggleRevision: () -> Unit,
    onToggleFlashcards: () -> Unit,
    onToggleTestOverride: () -> Unit,
    onStartNewStudyClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val emerald = AccentMood.EMERALD.accent
    val unlocked = homeState.isStudyGateUnlocked

    val gateBorder by animateColorAsState(
        targetValue = if (unlocked) tokens.inkPrimary.copy(alpha = 0.28f) else tokens.hairlineRest,
        animationSpec = spring(stiffness = 380f),
        label = "studyGateBorder",
    )
    val ctaColor by animateColorAsState(
        targetValue = if (unlocked) tokens.primaryCta else tokens.slateElevated,
        animationSpec = spring(stiffness = 380f),
        label = "studyGateCtaBg",
    )
    val ctaTextColor = if (unlocked) tokens.onPrimaryCta else tokens.inkSecondary

    ObsidianCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_study_gate_bar"),
        containerColor = tokens.obsidianSurface,
        accentEdgeColor = gateBorder,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (isWideLayout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Left: Honest Gate Status & Dynamic Calm Reason
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (unlocked) emerald.copy(alpha = 0.14f) else tokens.carbonSurface),
                        ) {
                            Icon(
                                imageVector = if (unlocked) Icons.Rounded.LockOpen else Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = if (unlocked) emerald else tokens.inkSecondary,
                                modifier = Modifier.size(19.dp),
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = if (unlocked) {
                                    stringResource(R.string.home_gate_unlocked_title)
                                } else {
                                    stringResource(R.string.home_gate_locked_title)
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (unlocked) emerald else tokens.inkPrimary,
                            )
                            Text(
                                text = if (unlocked) {
                                    "Next lecture ready in $primaryBatchName"
                                } else {
                                    homeState.studyGateLockedReason
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (showLockedReminder) AccentMood.AMBER.accent else tokens.inkSecondary,
                            )
                        }
                    }

                    // Middle: Discrete Quick Gate Test Switch + Individual Chips
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        GateStepCheckChip(
                            label = "Rev",
                            done = homeState.todayRevisionCompleted,
                            testTag = "gate_toggle_rev_chip",
                            onClick = onToggleRevision,
                        )
                        GateStepCheckChip(
                            label = "Cards",
                            done = homeState.todayFlashcardsCompleted,
                            testTag = "gate_toggle_cards_chip",
                            onClick = onToggleFlashcards,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(tokens.carbonSurface)
                                .padding(start = 10.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.home_gate_test_switch),
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                            )
                            Switch(
                                checked = unlocked,
                                onCheckedChange = { onToggleTestOverride() },
                                modifier = Modifier.testTag("home_study_gate_test_toggle"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = InkOnAccentDark,
                                    checkedTrackColor = emerald,
                                    uncheckedThumbColor = tokens.inkSecondary,
                                    uncheckedTrackColor = tokens.obsidianSurface,
                                    uncheckedBorderColor = tokens.hairlineRest,
                                ),
                            )
                        }
                    }

                    // Right: Start Today's New Study CTA
                    StartNewStudyButton(
                        unlocked = unlocked,
                        containerColor = ctaColor,
                        contentColor = ctaTextColor,
                        onClick = onStartNewStudyClick,
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = if (unlocked) Icons.Rounded.LockOpen else Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = if (unlocked) emerald else tokens.inkSecondary,
                            modifier = Modifier.size(19.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (unlocked) {
                                    stringResource(R.string.home_gate_unlocked_title)
                                } else {
                                    stringResource(R.string.home_gate_locked_title)
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (unlocked) emerald else tokens.inkPrimary,
                            )
                            Text(
                                text = homeState.studyGateLockedReason,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (showLockedReminder) AccentMood.AMBER.accent else tokens.inkSecondary,
                            )
                        }
                        Switch(
                            checked = unlocked,
                            onCheckedChange = { onToggleTestOverride() },
                            modifier = Modifier.testTag("home_study_gate_test_toggle"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = InkOnAccentDark,
                                checkedTrackColor = emerald,
                                uncheckedThumbColor = tokens.inkSecondary,
                                uncheckedTrackColor = tokens.carbonSurface,
                                uncheckedBorderColor = tokens.hairlineRest,
                            ),
                        )
                    }

                    StartNewStudyButton(
                        unlocked = unlocked,
                        containerColor = ctaColor,
                        contentColor = ctaTextColor,
                        onClick = onStartNewStudyClick,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun GateStepCheckChip(
    label: String,
    done: Boolean,
    testTag: String,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val emerald = AccentMood.EMERALD.accent
    val interaction = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (done) emerald.copy(alpha = 0.14f) else tokens.carbonSurface,
        border = BorderStroke(1.dp, if (done) emerald.copy(alpha = 0.36f) else tokens.hairlineSubtle),
        modifier = Modifier
            .testTag(testTag)
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.95f)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = emerald.copy(alpha = 0.16f)),
                role = Role.Checkbox,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = if (done) emerald else tokens.inkMuted,
                modifier = Modifier.size(13.dp),
            )
            Text(
                text = label,
                style = MicroTagMono,
                color = if (done) emerald else tokens.inkSecondary,
            )
        }
    }
}

@Composable
private fun StartNewStudyButton(
    unlocked: Boolean,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(14.dp)

    Surface(
        shape = shape,
        color = containerColor,
        border = if (!unlocked) BorderStroke(1.dp, tokens.hairlineRest) else null,
        modifier = modifier
            .testTag("home_start_new_study_button")
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.97f)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = contentColor.copy(alpha = 0.18f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = if (unlocked) Icons.AutoMirrored.Rounded.ArrowForward else Icons.Rounded.Lock,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(17.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.home_gate_start_new_study),
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                maxLines = 1,
            )
        }
    }
}
