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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Timer
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
import com.example.model.HomeLiveAnalytics
import com.example.model.LayoutDensityMode
import com.example.model.LiveClockTick
import com.example.model.StudyBatch
import com.example.model.TimerSessionSource
import com.example.model.WorkspaceUiState
import com.example.model.formatDurationCompactHm
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.AnimatedMasteryRing
import com.example.ui.components.KineticNumberText
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.rememberScreenLayoutMetrics
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeCommandCenter(
    uiState: WorkspaceUiState,
    clockTickFlow: StateFlow<LiveClockTick>,
    isWideLayout: Boolean,
    onOpenSettings: () -> Unit,
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
    onQuickOpenAiTutor: () -> Unit = {},
    onQuickOpenQuizzes: () -> Unit = {},
    onQuickOpenStudyTimer: () -> Unit = {},
    onQuickOpenPlanner: () -> Unit = {},
    onOpenHomeClockTagPicker: (launchFullScreenOnConfirm: Boolean) -> Unit = {},
    onDismissHomeClockTagPicker: () -> Unit = {},
    onSelectHomeClockBatch: (String) -> Unit = {},
    onSelectHomeClockSubject: (String?) -> Unit = {},
    onSelectHomeClockChapter: (String?) -> Unit = {},
    onSelectHomeClockLecture: (String?) -> Unit = {},
    onConfirmHomeClockTagSelection: () -> Unit = {},
    onStartHomeClockUntagged: () -> Unit = {},
    onTogglePlannerUserTask: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val homeState = uiState.homeCommandState
    val timerState = uiState.studyTimerGroupState

    val isAutoRevisionActive = timerState.isAutoTimerActive && timerState.timerSource == TimerSessionSource.AUTO_REVISION
    val isAutoFlashcardsActive = timerState.isAutoTimerActive && timerState.timerSource == TimerSessionSource.AUTO_FLASHCARDS

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
                appTheme = uiState.appTheme,
                isWideLayout = isWideLayout,
                clockTickFlow = clockTickFlow,
                isAutoTimerActive = isAutoRevisionActive,
                onBackToHome = onCloseDailyPage,
                onMarkComplete = onCompleteTodayRevision,
            )

            DailyPageRoute.TODAY_FLASHCARDS -> TodayFlashcardsPage(
                state = homeState,
                appTheme = uiState.appTheme,
                isWideLayout = isWideLayout,
                clockTickFlow = clockTickFlow,
                isAutoTimerActive = isAutoFlashcardsActive,
                onBackToHome = onCloseDailyPage,
                onFlipCard = onFlipDailyFlashcard,
                onStepCard = onStepDailyFlashcard,
                onMarkComplete = onCompleteTodayFlashcards,
            )

            null -> HomeCommandCanvas(
                uiState = uiState,
                clockTickFlow = clockTickFlow,
                isWideLayout = isWideLayout,
                onOpenSettings = onOpenSettings,
                onToggleFocusMode = onToggleFocusMode,
                onCyclePrimaryBatch = onCyclePrimaryBatch,
                onOpenDailyPage = onOpenDailyPage,
                onToggleTodayRevisionCompleted = onToggleTodayRevisionCompleted,
                onToggleTodayFlashcardsCompleted = onToggleTodayFlashcardsCompleted,
                onToggleStudyGateTestOverride = onToggleStudyGateTestOverride,
                onStartTodaysNewStudy = onStartTodaysNewStudy,
                onQuickOpenAiTutor = onQuickOpenAiTutor,
                onQuickOpenQuizzes = onQuickOpenQuizzes,
                onQuickOpenStudyTimer = onQuickOpenStudyTimer,
                onQuickOpenPlanner = onQuickOpenPlanner,
                onOpenHomeClockTagPicker = onOpenHomeClockTagPicker,
                onDismissHomeClockTagPicker = onDismissHomeClockTagPicker,
                onSelectHomeClockBatch = onSelectHomeClockBatch,
                onSelectHomeClockSubject = onSelectHomeClockSubject,
                onSelectHomeClockChapter = onSelectHomeClockChapter,
                onSelectHomeClockLecture = onSelectHomeClockLecture,
                onConfirmHomeClockTagSelection = onConfirmHomeClockTagSelection,
                onStartHomeClockUntagged = onStartHomeClockUntagged,
                onTogglePlannerUserTask = onTogglePlannerUserTask,
            )
        }
    }
}

@Composable
private fun HomeCommandCanvas(
    uiState: WorkspaceUiState,
    clockTickFlow: StateFlow<LiveClockTick>,
    isWideLayout: Boolean,
    onOpenSettings: () -> Unit,
    onToggleFocusMode: () -> Unit,
    onCyclePrimaryBatch: () -> Unit,
    onOpenDailyPage: (DailyPageRoute) -> Unit,
    onToggleTodayRevisionCompleted: () -> Unit,
    onToggleTodayFlashcardsCompleted: () -> Unit,
    onToggleStudyGateTestOverride: () -> Unit,
    onStartTodaysNewStudy: () -> Unit,
    onQuickOpenAiTutor: () -> Unit,
    onQuickOpenQuizzes: () -> Unit,
    onQuickOpenStudyTimer: () -> Unit,
    onQuickOpenPlanner: () -> Unit,
    onOpenHomeClockTagPicker: (launchFullScreenOnConfirm: Boolean) -> Unit,
    onDismissHomeClockTagPicker: () -> Unit,
    onSelectHomeClockBatch: (String) -> Unit,
    onSelectHomeClockSubject: (String?) -> Unit,
    onSelectHomeClockChapter: (String?) -> Unit,
    onSelectHomeClockLecture: (String?) -> Unit,
    onConfirmHomeClockTagSelection: () -> Unit,
    onStartHomeClockUntagged: () -> Unit,
    onTogglePlannerUserTask: (String) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val primaryBatch = uiState.primaryBatch
    val homeState = uiState.homeCommandState
    val analytics = uiState.liveHomeAnalytics
    val metrics = rememberScreenLayoutMetrics()
    var showLockedReminder by remember { mutableStateOf(false) }

    val remainingLabel = if (analytics.remainingLectures > 0) {
        stringResource(R.string.home_analytics_remaining_format, analytics.remainingLectures)
    } else {
        stringResource(R.string.home_analytics_all_done_label)
    }
    val readinessSubtitle = stringResource(
        R.string.home_analytics_gate_status_format,
        analytics.dailyGatesCleared,
        analytics.batchFlashcardCount,
    )

    val effectiveSectionSpacing = if (uiState.settingsPrefs.layoutDensity == LayoutDensityMode.COMPACT_FOCUS) {
        (metrics.sectionSpacing - 4.dp).coerceAtLeast(10.dp)
    } else {
        metrics.sectionSpacing
    }

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
            verticalArrangement = Arrangement.spacedBy(effectiveSectionSpacing),
        ) {
            item(key = "home_header") {
                StaggeredEntrance(delayMillis = 0L) {
                    HomeHeaderBar(
                        streakDays = uiState.streakDays,
                        focusMode = uiState.focusMode,
                        activeTheme = uiState.appTheme,
                        activeMood = uiState.accentMood,
                        isWideLayout = isWideLayout,
                        onToggleFocusMode = onToggleFocusMode,
                        onOpenSettings = onOpenSettings,
                    )
                }
            }

            // Section 1: Primary Batch Syllabus Overview + Live Computed Analytics
            item(key = "home_primary_and_stats") {
                StaggeredEntrance(delayMillis = 50L) {
                    if (isWideLayout) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            PrimaryBatchSyllabusCard(
                                batch = primaryBatch,
                                analytics = analytics,
                                batchCount = uiState.batchesState.batches.size,
                                isWideLayout = true,
                                onCyclePrimaryBatch = onCyclePrimaryBatch,
                                modifier = Modifier.weight(0.60f),
                            )
                            Column(
                                modifier = Modifier.weight(0.40f),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                LiveAnalyticsMetricCard(
                                    title = stringResource(R.string.home_analytics_lectures_title),
                                    metricValue = "${analytics.completedLectures}/${analytics.totalLectures}",
                                    subtitle = remainingLabel,
                                    progressPercent = analytics.masteryPercent,
                                    accentColor = tokens.themeAccent,
                                    icon = Icons.Rounded.MenuBook,
                                    testTag = "home_stat_lectures_card",
                                )
                                LiveAnalyticsMetricCard(
                                    title = stringResource(R.string.home_analytics_readiness_title),
                                    metricValue = "${analytics.dailyRecallReadinessPercent}%",
                                    subtitle = readinessSubtitle,
                                    progressPercent = analytics.dailyRecallReadinessPercent,
                                    accentColor = tokens.semanticSuccess,
                                    icon = Icons.Rounded.Bolt,
                                    testTag = "home_stat_readiness_card",
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            PrimaryBatchSyllabusCard(
                                batch = primaryBatch,
                                analytics = analytics,
                                batchCount = uiState.batchesState.batches.size,
                                isWideLayout = false,
                                onCyclePrimaryBatch = onCyclePrimaryBatch,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                LiveAnalyticsMetricCard(
                                    title = stringResource(R.string.home_analytics_lectures_title),
                                    metricValue = "${analytics.completedLectures}/${analytics.totalLectures}",
                                    subtitle = remainingLabel,
                                    progressPercent = analytics.masteryPercent,
                                    accentColor = tokens.themeAccent,
                                    icon = Icons.Rounded.MenuBook,
                                    testTag = "home_stat_lectures_card",
                                    compact = true,
                                    modifier = Modifier.weight(1f),
                                )
                                LiveAnalyticsMetricCard(
                                    title = stringResource(R.string.home_analytics_readiness_title),
                                    metricValue = "${analytics.dailyRecallReadinessPercent}%",
                                    subtitle = readinessSubtitle,
                                    progressPercent = analytics.dailyRecallReadinessPercent,
                                    accentColor = tokens.semanticSuccess,
                                    icon = Icons.Rounded.Bolt,
                                    testTag = "home_stat_readiness_card",
                                    compact = true,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }

            // Section 1.2: Modern Rectangular 3D Flip-Clock Card + Today's Plan & Tasks Card
            item(key = "home_flip_clock_and_plan") {
                val onPlayClick = {
                    if (uiState.studyTimerGroupState.isTimerActiveSession) {
                        onQuickOpenStudyTimer()
                    } else {
                        onOpenHomeClockTagPicker(true)
                    }
                }
                if (isWideLayout) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        HomeRectangularFlipClockCard(
                            timerState = uiState.studyTimerGroupState,
                            clockTickFlow = clockTickFlow,
                            clockTheme = uiState.settingsPrefs.clockTheme,
                            motionIntensity = uiState.settingsPrefs.motionIntensity,
                            onClickTagSelector = { onOpenHomeClockTagPicker(false) },
                            onClickPlayClock = onPlayClick,
                            modifier = Modifier.weight(0.48f),
                        )
                        HomeTodaysPlanTasksCard(
                            plannerState = uiState.standalonePlannerState,
                            onToggleUserTask = onTogglePlannerUserTask,
                            onOpenPlanner = onQuickOpenPlanner,
                            modifier = Modifier.weight(0.52f),
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        HomeRectangularFlipClockCard(
                            timerState = uiState.studyTimerGroupState,
                            clockTickFlow = clockTickFlow,
                            clockTheme = uiState.settingsPrefs.clockTheme,
                            motionIntensity = uiState.settingsPrefs.motionIntensity,
                            onClickTagSelector = { onOpenHomeClockTagPicker(false) },
                            onClickPlayClock = onPlayClick,
                        )
                        HomeTodaysPlanTasksCard(
                            plannerState = uiState.standalonePlannerState,
                            onToggleUserTask = onTogglePlannerUserTask,
                            onOpenPlanner = onQuickOpenPlanner,
                        )
                    }
                }
            }

            // Section 1.5: Studio Quick-Launch Strip (Study Timer, AI Tutor & Quiz Arena)
            item(key = "home_studio_quick_launch") {
                val timerState = uiState.studyTimerGroupState
                val timerBadge = if (timerState.isTimerActiveSession) {
                    "SESSION LIVE"
                } else {
                    "${formatDurationCompactHm(timerState.todayTotalStudySeconds)} TODAY"
                }
                val timerSubtitle = if (timerState.isTimerActiveSession) {
                    "Synced with YPT Group · ${timerState.activeSubjectName ?: "Tagless"}"
                } else {
                    "3D Flip Clock & YPT Group Study"
                }

                StaggeredEntrance(delayMillis = 75L) {
                    if (isWideLayout) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            StudioQuickLaunchTile(
                                title = stringResource(R.string.home_quick_launch_timer),
                                subtitle = timerSubtitle,
                                badge = timerBadge,
                                icon = Icons.Rounded.Timer,
                                accent = tokens.semanticSuccess,
                                testTag = "home_quick_launch_timer",
                                onClick = onQuickOpenStudyTimer,
                                modifier = Modifier.weight(1f),
                            )
                            StudioQuickLaunchTile(
                                title = stringResource(R.string.home_quick_launch_tutor),
                                subtitle = "Chapter RAG & Interactive Diagrams",
                                badge = "${uiState.grokTutorState.diagramCatalog.size} DIAGRAMS",
                                icon = Icons.Rounded.AutoAwesome,
                                accent = tokens.themeAccent,
                                testTag = "home_quick_launch_tutor",
                                onClick = onQuickOpenAiTutor,
                                modifier = Modifier.weight(1f),
                            )
                            StudioQuickLaunchTile(
                                title = stringResource(R.string.home_quick_launch_quizzes),
                                subtitle = "Exam Mode (+4/−1) & Weak Topics",
                                badge = "${uiState.pwQuizArenaState.recentAttempts.size} RECENT",
                                icon = Icons.Rounded.Quiz,
                                accent = AccentMood.AMBER.accent,
                                testTag = "home_quick_launch_quizzes",
                                onClick = onQuickOpenQuizzes,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            StudioQuickLaunchTile(
                                title = stringResource(R.string.home_quick_launch_timer),
                                subtitle = timerSubtitle,
                                badge = timerBadge,
                                icon = Icons.Rounded.Timer,
                                accent = tokens.semanticSuccess,
                                testTag = "home_quick_launch_timer",
                                onClick = onQuickOpenStudyTimer,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                StudioQuickLaunchTile(
                                    title = stringResource(R.string.home_quick_launch_tutor),
                                    subtitle = "Chapter RAG & Diagrams",
                                    badge = "${uiState.grokTutorState.diagramCatalog.size}",
                                    icon = Icons.Rounded.AutoAwesome,
                                    accent = tokens.themeAccent,
                                    testTag = "home_quick_launch_tutor",
                                    onClick = onQuickOpenAiTutor,
                                    modifier = Modifier.weight(1f),
                                )
                                StudioQuickLaunchTile(
                                    title = stringResource(R.string.home_quick_launch_quizzes),
                                    subtitle = "Exam Mode (+4/−1)",
                                    badge = "${uiState.pwQuizArenaState.recentAttempts.size}",
                                    icon = Icons.Rounded.Quiz,
                                    accent = AccentMood.AMBER.accent,
                                    testTag = "home_quick_launch_quizzes",
                                    onClick = onQuickOpenQuizzes,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Theme-Matched Daily Cards — Today's Revision & Today's Flashcards
            item(key = "home_daily_cards") {
                StaggeredEntrance(delayMillis = 100L) {
                    val revArtwork = uiState.appTheme.revisionArtworkRes
                    val cardsArtwork = uiState.appTheme.flashcardsArtworkRes

                    if (isWideLayout) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            DailyIllustratedGateCard(
                                title = stringResource(R.string.home_today_revision_title),
                                subtitle = stringResource(R.string.home_from_yesterday_ai),
                                itemCountLabel = "${homeState.todayRevisionTakeaways.size} Takeaways",
                                artworkRes = revArtwork,
                                accentColor = tokens.themeAccent,
                                isCompleted = homeState.todayRevisionCompleted,
                                testTag = "home_today_revision_card",
                                onClick = { onOpenDailyPage(DailyPageRoute.TODAY_REVISION) },
                                modifier = Modifier.weight(1f),
                            )
                            DailyIllustratedGateCard(
                                title = stringResource(R.string.home_today_flashcards_title),
                                subtitle = stringResource(R.string.home_from_yesterday_ai),
                                itemCountLabel = "${homeState.todayFlashcards.size} Recall Cards",
                                artworkRes = cardsArtwork,
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
                                artworkRes = revArtwork,
                                accentColor = tokens.themeAccent,
                                isCompleted = homeState.todayRevisionCompleted,
                                testTag = "home_today_revision_card",
                                onClick = { onOpenDailyPage(DailyPageRoute.TODAY_REVISION) },
                            )
                            DailyIllustratedGateCard(
                                title = stringResource(R.string.home_today_flashcards_title),
                                subtitle = stringResource(R.string.home_from_yesterday_ai),
                                itemCountLabel = "${homeState.todayFlashcards.size} Recall Cards",
                                artworkRes = cardsArtwork,
                                accentColor = AccentMood.AMBER.accent,
                                isCompleted = homeState.todayFlashcardsCompleted,
                                testTag = "home_today_flashcards_card",
                                onClick = { onOpenDailyPage(DailyPageRoute.TODAY_FLASHCARDS) },
                            )
                        }
                    }
                }
            }

            // Section 3: Honest Study Gate Lock Bar + Start Today's New Study CTA
            item(key = "home_study_gate") {
                StaggeredEntrance(delayMillis = 150L) {
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

        // Cascading Batch -> Subject -> Chapter -> Lecture Picker Modal for Home Flip Clock
        if (uiState.homeClockTagPicker.isOpen) {
            HomeCascadingClockTagModal(
                pickerState = uiState.homeClockTagPicker,
                batches = uiState.batchesState.batches,
                subjectsByBatch = uiState.pwVaultState.subjectsByBatch,
                onSelectBatch = onSelectHomeClockBatch,
                onSelectSubject = onSelectHomeClockSubject,
                onSelectChapter = onSelectHomeClockChapter,
                onSelectLecture = onSelectHomeClockLecture,
                onConfirmSelection = onConfirmHomeClockTagSelection,
                onStartUntagged = onStartHomeClockUntagged,
                onDismiss = onDismissHomeClockTagPicker,
            )
        }
    }
}

@Composable
private fun PrimaryBatchSyllabusCard(
    batch: StudyBatch,
    analytics: HomeLiveAnalytics,
    batchCount: Int,
    isWideLayout: Boolean,
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
        accentEdgeColor = tokens.hairlineSpecularTop,
        cornerRadius = 22.dp,
        contentPadding = PaddingValues(if (isWideLayout) 22.dp else 18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
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
                                maxLines = 1,
                            )
                        }
                    }
                    Text(
                        text = batch.goalTag.uppercase(),
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (batchCount > 1) {
                    Spacer(modifier = Modifier.width(8.dp))
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
                                indication = ripple(color = tokens.themeAccent.copy(alpha = 0.16f)),
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
                                maxLines = 1,
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
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        text = batch.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = tokens.inkPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${analytics.completedChapters}/${analytics.totalChapters} chapters · ${analytics.completedLectures}/${analytics.totalLectures} lectures done",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        KineticNumberText(
                            targetValue = analytics.masteryPercent,
                            suffix = "%",
                            style = MaterialTheme.typography.displayLarge,
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = "LIVE SYLLABUS\nMASTERY",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                            modifier = Modifier.padding(bottom = 7.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                AnimatedMasteryRing(
                    percentage = analytics.masteryPercent,
                    accentColor = tokens.themeAccent,
                    ringSize = if (isWideLayout) 86.dp else 76.dp,
                    strokeWidth = 6.dp,
                )
            }

            // Disciplined per-subject progress grid (4-across in landscape, crisp 2x2 in portrait)
            val displaySubjects = batch.subjects.take(4)
            if (isWideLayout || displaySubjects.size <= 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    displaySubjects.forEach { subject ->
                        SubjectMiniProgressTile(
                            name = subject.name,
                            percent = subject.masteryPercent,
                            accent = subject.accentMood.accent,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    displaySubjects.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            pair.forEach { subject ->
                                SubjectMiniProgressTile(
                                    name = subject.name,
                                    percent = subject.masteryPercent,
                                    accent = subject.accentMood.accent,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioQuickLaunchTile(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accent: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(18.dp)

    Surface(
        shape = shape,
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier
            .testTag(testTag)
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.97f)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = accent.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tokens.carbonSurface),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = tokens.carbonSurface,
                    ) {
                        Text(
                            text = badge,
                            style = MicroTagMono,
                            color = accent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = tokens.inkMuted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun SubjectMiniProgressTile(
    name: String,
    percent: Int,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(tokens.carbonSurface)
            .padding(horizontal = 11.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = tokens.inkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$percent%",
                style = MicroTagMono,
                color = tokens.inkSecondary,
            )
        }
        AnimatedLinearProgressBar(
            progressPercent = percent,
            accentColor = accent,
            height = 4.dp,
        )
    }
}

@Composable
private fun LiveAnalyticsMetricCard(
    title: String,
    metricValue: String,
    subtitle: String,
    progressPercent: Int,
    accentColor: Color,
    icon: ImageVector,
    testTag: String,
    compact: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    ObsidianCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(
            horizontal = if (compact) 15.dp else 18.dp,
            vertical = if (compact) 15.dp else 16.dp,
        ),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title.uppercase(),
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(tokens.carbonSurface),
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Text(
                text = metricValue,
                style = if (compact) MaterialTheme.typography.displaySmall else MaterialTheme.typography.displayMedium,
                color = tokens.inkPrimary,
                maxLines = 1,
            )

            AnimatedLinearProgressBar(
                progressPercent = progressPercent,
                accentColor = accentColor,
                height = 4.dp,
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
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
    val successColor = tokens.semanticSuccess
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(22.dp)

    val borderColor by animateColorAsState(
        targetValue = if (isCompleted) successColor.copy(alpha = 0.42f) else tokens.hairlineRest,
        animationSpec = spring(stiffness = 380f),
        label = "dailyCardBorder_$testTag",
    )

    Surface(
        shape = shape,
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = metrics.dailyCardHeight)
            .testTag(testTag)
            .tactileScale(interactionSource, pressedScale = 0.98f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = tokens.themeAccent.copy(alpha = 0.14f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Theme-matched bundled artwork anchored cleanly to the right half
            Image(
                painter = painterResource(id = artworkRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.52f)
                    .matchParentSize()
                    .align(Alignment.CenterEnd),
            )

            // Theme-matched gradient scrim for razor-sharp contrast in both dark & Paper Light themes
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            0.0f to tokens.obsidianSurface,
                            0.48f to tokens.obsidianSurface.copy(alpha = 0.97f),
                            0.74f to tokens.obsidianSurface.copy(alpha = 0.45f),
                            1.0f to tokens.obsidianSurface.copy(alpha = 0.08f),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
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
                            if (isCompleted) successColor.copy(alpha = 0.42f) else tokens.hairlineRest,
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
                                    tint = successColor,
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
                                color = if (isCompleted) successColor else tokens.inkSecondary,
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tokens.obsidianSurface.copy(alpha = 0.90f),
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
                                maxLines = 1,
                            )
                        }
                    }
                }

                // Middle title & yesterday AI context
                Column(
                    modifier = Modifier.fillMaxWidth(0.70f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = tokens.inkPrimary,
                        maxLines = 2,
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

                // Footer action cue — never clips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Text(
                        text = itemCountLabel,
                        style = MonoBadgeStyle,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                    )
                    Text(
                        text = "·",
                        style = MonoBadgeStyle,
                        color = tokens.inkMuted,
                    )
                    Text(
                        text = if (isCompleted) "Review Again" else "Open Daily Page",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isCompleted) successColor else tokens.themeAccent,
                        maxLines = 1,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = if (isCompleted) successColor else tokens.themeAccent,
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
    val successColor = tokens.semanticSuccess
    val unlocked = homeState.isStudyGateUnlocked

    val gateBorder by animateColorAsState(
        targetValue = if (unlocked) successColor.copy(alpha = 0.36f) else tokens.hairlineRest,
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
        cornerRadius = 22.dp,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
    ) {
        if (isWideLayout) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (unlocked) successColor.copy(alpha = 0.14f) else tokens.carbonSurface),
                    ) {
                        Icon(
                            imageVector = if (unlocked) Icons.Rounded.LockOpen else Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = if (unlocked) successColor else tokens.inkSecondary,
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
                            color = if (unlocked) successColor else tokens.inkPrimary,
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
                                checkedThumbColor = tokens.obsidianSurface,
                                checkedTrackColor = successColor,
                                uncheckedThumbColor = tokens.inkSecondary,
                                uncheckedTrackColor = tokens.obsidianSurface,
                                uncheckedBorderColor = tokens.hairlineRest,
                            ),
                        )
                    }
                }

                StartNewStudyButton(
                    unlocked = unlocked,
                    containerColor = ctaColor,
                    contentColor = ctaTextColor,
                    onClick = onStartNewStudyClick,
                )
            }
        } else {
            // Crisp, compact 9:16 portrait Study Gate layout
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (unlocked) successColor.copy(alpha = 0.14f) else tokens.carbonSurface),
                    ) {
                        Icon(
                            imageVector = if (unlocked) Icons.Rounded.LockOpen else Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = if (unlocked) successColor else tokens.inkSecondary,
                            modifier = Modifier.size(19.dp),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = if (unlocked) {
                                stringResource(R.string.home_gate_unlocked_title)
                            } else {
                                stringResource(R.string.home_gate_locked_title)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = if (unlocked) successColor else tokens.inkPrimary,
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "TEST",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                        Switch(
                            checked = unlocked,
                            onCheckedChange = { onToggleTestOverride() },
                            modifier = Modifier.testTag("home_study_gate_test_toggle"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = tokens.obsidianSurface,
                                checkedTrackColor = successColor,
                                uncheckedThumbColor = tokens.inkSecondary,
                                uncheckedTrackColor = tokens.carbonSurface,
                                uncheckedBorderColor = tokens.hairlineRest,
                            ),
                        )
                    }
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

@Composable
private fun GateStepCheckChip(
    label: String,
    done: Boolean,
    testTag: String,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val successColor = tokens.semanticSuccess
    val interaction = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (done) successColor.copy(alpha = 0.14f) else tokens.carbonSurface,
        border = BorderStroke(1.dp, if (done) successColor.copy(alpha = 0.38f) else tokens.hairlineSubtle),
        modifier = Modifier
            .testTag(testTag)
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.95f)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = successColor.copy(alpha = 0.16f)),
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
                tint = if (done) successColor else tokens.inkMuted,
                modifier = Modifier.size(13.dp),
            )
            Text(
                text = label,
                style = MicroTagMono,
                color = if (done) successColor else tokens.inkSecondary,
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
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
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
