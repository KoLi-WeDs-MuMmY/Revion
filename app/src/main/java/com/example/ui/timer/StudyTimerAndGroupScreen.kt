package com.example.ui.timer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.ViewSidebar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClockThemeId
import com.example.model.LiveClockTick
import com.example.model.MotionIntensity
import com.example.model.PomodoroPhase
import com.example.model.SplitPosture
import com.example.model.StudyTimerGroupUiState
import com.example.model.StudyTimerMode
import com.example.model.VaultSubjectNode
import com.example.model.YptMemberStatus
import com.example.model.formatDurationCompactHm
import com.example.model.formatDurationHms
import com.example.ui.components.tactileScale
import com.example.ui.groupstudy.CreateYptGroupDialog
import com.example.ui.groupstudy.YptGroupStudyPane
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import kotlinx.coroutines.flow.StateFlow

/**
 * Full-Screen Distraction-Free 3D Flip-Clock Timer & Authentic YPT Group Study Workspace:
 * - Zero ratio numbers anywhere in the UI.
 * - Single Split-Screen toggle + smooth spring-snapping vertical/horizontal drag divider.
 * - Isolated 1-second leaf clockTickFlow updates (parent layout never recomposes per tick).
 * - Strict maxLines = 1, softWrap = false guards so narrow panes never wrap letters vertically.
 */
@Composable
fun StudyTimerAndGroupScreen(
    state: StudyTimerGroupUiState,
    clockTickFlow: StateFlow<LiveClockTick>,
    clockTheme: ClockThemeId,
    motionIntensity: MotionIntensity,
    subjects: List<VaultSubjectNode>,
    isWideLayout: Boolean,
    onExitToHome: () -> Unit,
    onOpenVaultWhileTimerActive: () -> Unit,
    onSelectMode: (StudyTimerMode) -> Unit,
    onApplyPomodoroPreset: (focusMin: Int, breakMin: Int) -> Unit,
    onAdjustCustomTimerSeconds: (deltaSeconds: Int) -> Unit,
    onSwitchPomodoroPhase: () -> Unit,
    onAddFiveMinutes: () -> Unit,
    onRequestStartFromMainButton: () -> Unit,
    onQuickStartSubject: (VaultSubjectNode) -> Unit,
    onConfirmStartWithTag: (
        subjectId: String?,
        subjectName: String?,
        subjectMood: AccentMood,
        lectureId: String?,
        lectureTitle: String?,
    ) -> Unit,
    onSkipStartTagless: () -> Unit,
    onDismissStartTagDialog: () -> Unit,
    onPauseResumeTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onStopAndLogTimer: () -> Unit,
    onSetSplitPosture: (SplitPosture) -> Unit,
    onToggleSplitView: () -> Unit,
    onOpenStatsSheet: () -> Unit,
    onCloseStatsSheet: () -> Unit,
    onJoinGroup: (String) -> Unit,
    onOpenCreateGroupDialog: () -> Unit,
    onCloseCreateGroupDialog: () -> Unit,
    onCreatePublicGroup: (name: String, category: String, targetHours: Int) -> Unit,
    onSelectUserStatus: (YptMemberStatus) -> Unit,
    onInspectYptMember: (String?) -> Unit,
    onSendSilentBoost: (String) -> Unit,
    onOpenAllowedAppsSheet: () -> Unit,
    onCloseAllowedAppsSheet: () -> Unit,
    onToggleAllowedApp: (String) -> Unit,
    onDismissLockNotice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    BackHandler {
        if (state.isManualTimerLocked) {
            onOpenVaultWhileTimerActive()
        } else {
            onExitToHome()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.amoledBackground)
            .testTag("study_timer_group_fullscreen"),
    ) {
        // 1. Clean Top Bar (Zero ratio numbers — Single Split-Screen toggle + Stats + Vault)
        TimerWorkspaceTopBar(
            state = state,
            isWideLayout = isWideLayout,
            onExitToHome = onExitToHome,
            onOpenVault = onOpenVaultWhileTimerActive,
            onToggleSplitView = onToggleSplitView,
            onOpenStatsSheet = onOpenStatsSheet,
        )

        // Single-Timer Lock Notice Banner
        AnimatedVisibility(visible = state.singleTimerLockNotice != null) {
            state.singleTimerLockNotice?.let { notice ->
                Surface(
                    color = AccentMood.AMBER.accent.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, AccentMood.AMBER.accent.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = AccentMood.AMBER.accent,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = notice,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onDismissLockNotice),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Dismiss notice",
                                tint = tokens.inkMuted,
                                modifier = Modifier.size(15.dp),
                            )
                        }
                    }
                }
            }
        }

        // 2. Main Smooth Split-Screen Stage with Tactile Drag Handle
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val density = LocalDensity.current
            val totalAxisPx = with(density) {
                if (isWideLayout) maxWidth.toPx() else maxHeight.toPx()
            }.coerceAtLeast(1f)

            var isDragging by remember { mutableStateOf(false) }
            var dragFraction by remember(state.splitPosture) {
                mutableFloatStateOf(state.splitPosture.timerFraction)
            }

            val targetFraction = if (isDragging) dragFraction else state.splitPosture.timerFraction
            val animatedFraction by animateFloatAsState(
                targetValue = targetFraction.coerceIn(0f, 1f),
                animationSpec = motionIntensity.springSpec(),
                label = "timerGroupSplitFraction",
            )

            val showTimerPane = animatedFraction > 0.04f
            val showGroupPane = animatedFraction < 0.96f
            val showDragHandle = showTimerPane && showGroupPane

            val timerContent: @Composable (Modifier) -> Unit = { paneMod ->
                FlipClockTimerPane(
                    state = state,
                    clockTickFlow = clockTickFlow,
                    clockTheme = clockTheme,
                    motionIntensity = motionIntensity,
                    subjects = subjects,
                    onSelectMode = onSelectMode,
                    onApplyPomodoroPreset = onApplyPomodoroPreset,
                    onAdjustCustomTimerSeconds = onAdjustCustomTimerSeconds,
                    onSwitchPomodoroPhase = onSwitchPomodoroPhase,
                    onAddFiveMinutes = onAddFiveMinutes,
                    onRequestStartFromMainButton = onRequestStartFromMainButton,
                    onQuickStartSubject = onQuickStartSubject,
                    onPauseResumeTimer = onPauseResumeTimer,
                    onResetTimer = onResetTimer,
                    onStopAndLogTimer = onStopAndLogTimer,
                    modifier = paneMod,
                )
            }

            val groupContent: @Composable (Modifier) -> Unit = { paneMod ->
                Box(modifier = paneMod) {
                    YptGroupStudyPane(
                        state = state,
                        clockTickFlow = clockTickFlow,
                        onJoinGroup = onJoinGroup,
                        onOpenCreateGroupDialog = onOpenCreateGroupDialog,
                        onSelectUserStatus = onSelectUserStatus,
                        onInspectMember = onInspectYptMember,
                        onSendSilentBoost = onSendSilentBoost,
                        onOpenAllowedAppsSheet = onOpenAllowedAppsSheet,
                        onCloseAllowedAppsSheet = onCloseAllowedAppsSheet,
                        onToggleAllowedApp = onToggleAllowedApp,
                        modifier = Modifier.fillMaxSize(),
                    )

                    // Floating compact live clock chip when in Full Group view
                    if (!showTimerPane) {
                        FloatingGroupLiveClockChip(
                            state = state,
                            clockTickFlow = clockTickFlow,
                            clockTheme = clockTheme,
                            onRestoreSplit = { onSetSplitPosture(SplitPosture.BALANCED_SPLIT) },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 12.dp, top = 12.dp),
                        )
                    }
                }
            }

            if (isWideLayout) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (showTimerPane) {
                        timerContent(
                            Modifier
                                .weight(animatedFraction.coerceAtLeast(0.01f))
                                .fillMaxHeight(),
                        )
                    }

                    if (showDragHandle) {
                        TactileSplitHandle(
                            isVerticalDivider = true,
                            isDragging = isDragging,
                            onDragStart = {
                                isDragging = true
                                dragFraction = state.splitPosture.timerFraction
                            },
                            onDragDeltaPx = { delta ->
                                dragFraction = (dragFraction + delta / totalAxisPx).coerceIn(0f, 1f)
                            },
                            onDragEnd = {
                                isDragging = false
                                onSetSplitPosture(SplitPosture.nearestTo(dragFraction))
                            },
                        )
                    }

                    if (showGroupPane) {
                        groupContent(
                            Modifier
                                .weight((1f - animatedFraction).coerceAtLeast(0.01f))
                                .fillMaxHeight(),
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (showTimerPane) {
                        timerContent(
                            Modifier
                                .weight(animatedFraction.coerceAtLeast(0.01f))
                                .fillMaxWidth(),
                        )
                    }

                    if (showDragHandle) {
                        TactileSplitHandle(
                            isVerticalDivider = false,
                            isDragging = isDragging,
                            onDragStart = {
                                isDragging = true
                                dragFraction = state.splitPosture.timerFraction
                            },
                            onDragDeltaPx = { delta ->
                                dragFraction = (dragFraction + delta / totalAxisPx).coerceIn(0f, 1f)
                            },
                            onDragEnd = {
                                isDragging = false
                                onSetSplitPosture(SplitPosture.nearestTo(dragFraction))
                            },
                        )
                    }

                    if (showGroupPane) {
                        groupContent(
                            Modifier
                                .weight((1f - animatedFraction).coerceAtLeast(0.01f))
                                .fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }

    // Main Start Subject Tag + Optional Lecture Sync Modal
    if (state.showStartTagDialog) {
        TimerStartTagDialog(
            subjects = subjects,
            onStartWithSelection = onConfirmStartWithTag,
            onSkipTagless = onSkipStartTagless,
            onDismiss = onDismissStartTagDialog,
        )
    }

    // Stats, Unoptimized Daily Graph & Group Leaderboard Modal
    if (state.showStatsSheet) {
        TimerStatsAndLeaderboardSheet(
            state = state,
            onDismiss = onCloseStatsSheet,
        )
    }

    // Create Public YPT Study Group Modal
    if (state.showCreateGroupDialog) {
        CreateYptGroupDialog(
            onCreate = onCreatePublicGroup,
            onDismiss = onCloseCreateGroupDialog,
        )
    }
}

@Composable
private fun TimerWorkspaceTopBar(
    state: StudyTimerGroupUiState,
    isWideLayout: Boolean,
    onExitToHome: () -> Unit,
    onOpenVault: () -> Unit,
    onToggleSplitView: () -> Unit,
    onOpenStatsSheet: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val splitActive = state.splitPosture != SplitPosture.FULL_TIMER

    Surface(
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Back / Vault button
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .minimumInteractiveComponentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = tokens.inkPrimary),
                        role = Role.Button,
                        onClick = {
                            if (state.isManualTimerLocked) onOpenVault() else onExitToHome()
                        },
                    )
                    .testTag("timer_top_exit_or_vault_button"),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = if (state.isManualTimerLocked) Icons.Rounded.FolderCopy else Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = if (state.isManualTimerLocked) "Open Vault" else "Back to Home",
                        tint = tokens.inkPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = if (state.isManualTimerLocked) "Vault" else "Home",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            // Title & Active Tag
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (state.isFullScreenHomeClockMode) "Focus Desk Clock" else "Study Timer & Group",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
                val tagSubtitle = buildString {
                    append(state.activeSubjectName ?: "Untagged")
                    state.syncedLectureTitle?.let { append(" · $it") }
                }
                Text(
                    text = tagSubtitle,
                    style = MicroTagMono,
                    color = if (state.isTimerActiveSession) state.activeSubjectMood.accent else tokens.inkMuted,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Single Clean Split-Screen Toggle (Zero Ratio Numbers!)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (splitActive) tokens.primaryCta else tokens.carbonSurface,
                border = BorderStroke(
                    1.dp,
                    if (splitActive) tokens.primaryCta else tokens.hairlineRest,
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .minimumInteractiveComponentSize()
                    .tactileScale()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = tokens.inkPrimary),
                        role = Role.Button,
                        onClick = onToggleSplitView,
                    )
                    .testTag("timer_single_split_toggle"),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = if (splitActive) Icons.Rounded.ViewSidebar else Icons.Rounded.Groups,
                        contentDescription = "Toggle Split Screen with YPT Group Study",
                        tint = if (splitActive) tokens.onPrimaryCta else tokens.inkPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                    if (isWideLayout) {
                        Text(
                            text = if (splitActive) "Split On" else "Group Split",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (splitActive) tokens.onPrimaryCta else tokens.inkPrimary,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }

            // Stats & Leaderboard Sheet Button
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .minimumInteractiveComponentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = tokens.inkPrimary),
                        role = Role.Button,
                        onClick = onOpenStatsSheet,
                    )
                    .testTag("timer_open_stats_button"),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BarChart,
                        contentDescription = "Study Stats & Leaderboard",
                        tint = tokens.inkPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                    if (isWideLayout) {
                        Text(
                            text = "Stats",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlipClockTimerPane(
    state: StudyTimerGroupUiState,
    clockTickFlow: StateFlow<LiveClockTick>,
    clockTheme: ClockThemeId,
    motionIntensity: MotionIntensity,
    subjects: List<VaultSubjectNode>,
    onSelectMode: (StudyTimerMode) -> Unit,
    onApplyPomodoroPreset: (Int, Int) -> Unit,
    onAdjustCustomTimerSeconds: (Int) -> Unit,
    onSwitchPomodoroPhase: () -> Unit,
    onAddFiveMinutes: () -> Unit,
    onRequestStartFromMainButton: () -> Unit,
    onQuickStartSubject: (VaultSubjectNode) -> Unit,
    onPauseResumeTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onStopAndLogTimer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.amoledBackground)
            .testTag("timer_flip_clock_pane"),
    ) {
        val isCompactPane = maxWidth < 360.dp
        val showSideSubjectsColumn = maxWidth >= 660.dp && !state.isFullScreenHomeClockMode
        val tileSize = when {
            maxWidth >= 560.dp && state.isFullScreenHomeClockMode -> FlipTileSize.HERO_FULLSCREEN
            maxWidth >= 460.dp -> FlipTileSize.STANDARD_SPLIT
            maxWidth >= 320.dp -> FlipTileSize.COMPACT_HOME
            else -> FlipTileSize.NARROW_SPLIT
        }

        if (showSideSubjectsColumn) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.45f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    TimerCenterDeck(
                        state = state,
                        clockTickFlow = clockTickFlow,
                        clockTheme = clockTheme,
                        motionIntensity = motionIntensity,
                        tileSize = tileSize,
                        isCompactPane = false,
                        onSelectMode = onSelectMode,
                        onApplyPomodoroPreset = onApplyPomodoroPreset,
                        onAdjustCustomTimerSeconds = onAdjustCustomTimerSeconds,
                        onSwitchPomodoroPhase = onSwitchPomodoroPhase,
                        onAddFiveMinutes = onAddFiveMinutes,
                        onRequestStartFromMainButton = onRequestStartFromMainButton,
                        onPauseResumeTimer = onPauseResumeTimer,
                        onResetTimer = onResetTimer,
                        onStopAndLogTimer = onStopAndLogTimer,
                    )
                }

                SubjectsQuickStartSidebar(
                    subjects = subjects,
                    todaySubjectSeconds = state.todaySubjectSeconds,
                    activeSubjectName = state.activeSubjectName,
                    onQuickStartSubject = onQuickStartSubject,
                    modifier = Modifier
                        .weight(0.85f)
                        .fillMaxHeight(),
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = if (isCompactPane) 10.dp else 16.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                TimerCenterDeck(
                    state = state,
                    clockTickFlow = clockTickFlow,
                    clockTheme = clockTheme,
                    motionIntensity = motionIntensity,
                    tileSize = tileSize,
                    isCompactPane = isCompactPane,
                    onSelectMode = onSelectMode,
                    onApplyPomodoroPreset = onApplyPomodoroPreset,
                    onAdjustCustomTimerSeconds = onAdjustCustomTimerSeconds,
                    onSwitchPomodoroPhase = onSwitchPomodoroPhase,
                    onAddFiveMinutes = onAddFiveMinutes,
                    onRequestStartFromMainButton = onRequestStartFromMainButton,
                    onPauseResumeTimer = onPauseResumeTimer,
                    onResetTimer = onResetTimer,
                    onStopAndLogTimer = onStopAndLogTimer,
                )

                if (!isCompactPane && subjects.isNotEmpty() && !state.isFullScreenHomeClockMode) {
                    SubjectsQuickStartHorizontalStrip(
                        subjects = subjects,
                        todaySubjectSeconds = state.todaySubjectSeconds,
                        activeSubjectName = state.activeSubjectName,
                        onQuickStartSubject = onQuickStartSubject,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerCenterDeck(
    state: StudyTimerGroupUiState,
    clockTickFlow: StateFlow<LiveClockTick>,
    clockTheme: ClockThemeId,
    motionIntensity: MotionIntensity,
    tileSize: FlipTileSize,
    isCompactPane: Boolean,
    onSelectMode: (StudyTimerMode) -> Unit,
    onApplyPomodoroPreset: (Int, Int) -> Unit,
    onAdjustCustomTimerSeconds: (Int) -> Unit,
    onSwitchPomodoroPhase: () -> Unit,
    onAddFiveMinutes: () -> Unit,
    onRequestStartFromMainButton: () -> Unit,
    onPauseResumeTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onStopAndLogTimer: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val accent = state.activeSubjectMood.accent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (isCompactPane) 10.dp else 14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Mode Selector Pill: Pomodoro vs Flip Stopwatch
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                StudyTimerMode.entries.forEach { mode ->
                    val selected = state.mode == mode
                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = if (selected) tokens.primaryCta else tokens.carbonSurface,
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .clickable(role = Role.Tab) { onSelectMode(mode) }
                            .testTag("timer_mode_${mode.name.lowercase()}"),
                    ) {
                        Text(
                            text = mode.label,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (selected) tokens.onPrimaryCta else tokens.inkSecondary,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(
                                horizontal = if (isCompactPane) 10.dp else 16.dp,
                                vertical = 7.dp,
                            ),
                        )
                    }
                }
            }
        }

        // Active Subject / Lecture Tag Chip
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(accent),
                )
                Text(
                    text = state.activeSubjectName ?: "Untagged Session",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
                state.syncedLectureTitle?.let { lec ->
                    Icon(
                        imageVector = Icons.Rounded.Link,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(13.dp),
                    )
                    Text(
                        text = lec,
                        style = MicroTagMono,
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        // Physical 3D Split-Flap Flip Clock (Isolated Leaf Composable!)
        FlipClockReadoutLeaf(
            clockTickFlow = clockTickFlow,
            clockTheme = clockTheme,
            motionIntensity = motionIntensity,
            tileSize = tileSize,
            forceStopwatchFormat = state.mode == StudyTimerMode.STOPWATCH,
            showSubLabels = !isCompactPane,
            modifier = Modifier.testTag("timer_flip_clock_display"),
        )

        // Pomodoro Presets + Custom Min/Sec Stepper
        if (state.mode == StudyTimerMode.POMODORO && !isCompactPane) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                listOf(25 to 5, 50 to 10, 90 to 15).forEach { (focus, brk) ->
                    val sel = state.focusDurationMinutes == focus && state.breakDurationMinutes == brk
                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = if (sel) tokens.primaryCta else tokens.carbonSurface,
                        border = BorderStroke(1.dp, if (sel) tokens.primaryCta else tokens.hairlineRest),
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .clickable { onApplyPomodoroPreset(focus, brk) }
                            .testTag("timer_preset_${focus}_${brk}"),
                    ) {
                        Text(
                            text = "$focus/$brk",
                            style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                            color = if (sel) tokens.onPrimaryCta else tokens.inkSecondary,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }

                // Switch Focus / Break Phase
                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = tokens.slateElevated,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .clickable(onClick = onSwitchPomodoroPhase)
                        .testTag("timer_switch_phase_button"),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SwapHoriz,
                            contentDescription = "Switch Focus and Break",
                            tint = tokens.inkPrimary,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = if (state.pomodoroPhase == PomodoroPhase.FOCUS) "Break" else "Focus",
                            style = MicroTagMono.copy(fontWeight = FontWeight.SemiBold),
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }

            // Custom Minutes / Seconds Adjuster Row (when idle)
            if (!state.isTimerActiveSession) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomStepChip(label = "−5m", icon = Icons.Rounded.Remove) { onAdjustCustomTimerSeconds(-300) }
                    CustomStepChip(label = "−30s", icon = Icons.Rounded.Remove) { onAdjustCustomTimerSeconds(-30) }
                    CustomStepChip(label = "+30s", icon = Icons.Rounded.Add) { onAdjustCustomTimerSeconds(30) }
                    CustomStepChip(label = "+5m", icon = Icons.Rounded.Add) { onAdjustCustomTimerSeconds(300) }
                }
            }
        }

        // Primary Transport Controls (Start / Pause / Resume / Reset / +5m / Stop)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!state.isTimerActiveSession) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .minimumInteractiveComponentSize()
                        .tactileScale()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = tokens.onPrimaryCta),
                            role = Role.Button,
                            onClick = onRequestStartFromMainButton,
                        )
                        .testTag("timer_main_start_button"),
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = if (isCompactPane) 16.dp else 26.dp,
                            vertical = 12.dp,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Start Study Timer",
                            tint = tokens.onPrimaryCta,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "Start",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = tokens.onPrimaryCta,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            } else {
                // Pause / Resume
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .minimumInteractiveComponentSize()
                        .tactileScale()
                        .clickable(role = Role.Button, onClick = onPauseResumeTimer)
                        .testTag("timer_pause_resume_button"),
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = if (isCompactPane) 12.dp else 20.dp,
                            vertical = 11.dp,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = if (state.isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (state.isRunning) "Pause" else "Resume",
                            tint = tokens.onPrimaryCta,
                            modifier = Modifier.size(18.dp),
                        )
                        if (!isCompactPane) {
                            Text(
                                text = if (state.isRunning) "Pause" else "Resume",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = tokens.onPrimaryCta,
                                maxLines = 1,
                                softWrap = false,
                            )
                        }
                    }
                }

                // +5m quick boost (Pomodoro)
                if (state.mode == StudyTimerMode.POMODORO && !isCompactPane) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .minimumInteractiveComponentSize()
                            .clickable(role = Role.Button, onClick = onAddFiveMinutes)
                            .testTag("timer_add_five_min_button"),
                    ) {
                        Text(
                            text = "+5m",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                        )
                    }
                }

                // Stop & Log (exits Full-Screen Home Clock mode back to Home cleanly!)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AccentMood.EMERALD.accent.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, AccentMood.EMERALD.accent.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .minimumInteractiveComponentSize()
                        .tactileScale()
                        .clickable(role = Role.Button, onClick = onStopAndLogTimer)
                        .testTag("timer_stop_and_log_button"),
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = if (isCompactPane) 12.dp else 18.dp,
                            vertical = 11.dp,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Stop,
                            contentDescription = "Stop and Log Session",
                            tint = AccentMood.EMERALD.accent,
                            modifier = Modifier.size(18.dp),
                        )
                        if (!isCompactPane) {
                            Text(
                                text = "Stop",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = AccentMood.EMERALD.accent,
                                maxLines = 1,
                                softWrap = false,
                            )
                        }
                    }
                }
            }

            // Reset button
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .minimumInteractiveComponentSize()
                    .clickable(role = Role.Button, onClick = onResetTimer)
                    .testTag("timer_reset_button"),
            ) {
                Box(
                    modifier = Modifier.padding(11.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Reset Timer",
                        tint = tokens.inkSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomStepChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Icon(icon, contentDescription = null, tint = tokens.inkMuted, modifier = Modifier.size(12.dp))
            Text(
                text = label,
                style = MicroTagMono,
                color = tokens.inkSecondary,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

@Composable
private fun SubjectsQuickStartSidebar(
    subjects: List<VaultSubjectNode>,
    todaySubjectSeconds: Map<String, Int>,
    activeSubjectName: String?,
    onQuickStartSubject: (VaultSubjectNode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "TODAY BY SUBJECT",
                    style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    softWrap = false,
                )
                Text(
                    text = "Tap to start",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    softWrap = false,
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(items = subjects, key = { it.id }) { subject ->
                    val sec = todaySubjectSeconds[subject.name] ?: 0
                    val isCurrent = activeSubjectName.equals(subject.name, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCurrent) subject.accentMood.accent.copy(alpha = 0.14f) else tokens.carbonSurface,
                        border = BorderStroke(
                            1.dp,
                            if (isCurrent) subject.accentMood.accent.copy(alpha = 0.55f) else tokens.hairlineSubtle,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .tactileScale()
                            .clickable(role = Role.Button) { onQuickStartSubject(subject) }
                            .testTag("timer_subject_quick_${subject.id}"),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(subject.accentMood.accent),
                            )
                            Text(
                                text = subject.name,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = tokens.inkPrimary,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = formatDurationCompactHm(sec),
                                style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                                color = subject.accentMood.accent,
                                maxLines = 1,
                                softWrap = false,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectsQuickStartHorizontalStrip(
    subjects: List<VaultSubjectNode>,
    todaySubjectSeconds: Map<String, Int>,
    activeSubjectName: String?,
    onQuickStartSubject: (VaultSubjectNode) -> Unit,
) {
    val tokens = GhickinTokens.colors
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "QUICK-START BY SUBJECT",
            style = MicroTagMono,
            color = tokens.inkMuted,
            maxLines = 1,
            softWrap = false,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(items = subjects, key = { it.id }) { subject ->
                val sec = todaySubjectSeconds[subject.name] ?: 0
                val isCurrent = activeSubjectName.equals(subject.name, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = if (isCurrent) subject.accentMood.accent.copy(alpha = 0.16f) else tokens.carbonSurface,
                    border = BorderStroke(
                        1.dp,
                        if (isCurrent) subject.accentMood.accent.copy(alpha = 0.55f) else tokens.hairlineRest,
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(11.dp))
                        .clickable { onQuickStartSubject(subject) },
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(subject.accentMood.accent),
                        )
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            softWrap = false,
                        )
                        Text(
                            text = formatDurationCompactHm(sec),
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TactileSplitHandle(
    isVerticalDivider: Boolean,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDragDeltaPx: (Float) -> Unit,
    onDragEnd: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val handleBg = if (isDragging) tokens.themeAccent.copy(alpha = 0.16f) else tokens.obsidianSurface
    val pillColor = if (isDragging) tokens.themeAccent else tokens.inkMuted.copy(alpha = 0.55f)

    Box(
        modifier = Modifier
            .then(
                if (isVerticalDivider) {
                    Modifier
                        .width(18.dp)
                        .fillMaxHeight()
                } else {
                    Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                },
            )
            .background(handleBg)
            .pointerInput(isVerticalDivider) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDragDeltaPx(if (isVerticalDivider) dragAmount.x else dragAmount.y)
                    },
                )
            }
            .testTag("timer_split_drag_handle"),
        contentAlignment = Alignment.Center,
    ) {
        if (isVerticalDivider) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(42.dp)
                    .clip(CircleShape)
                    .background(pillColor),
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(pillColor),
                )
                Icon(
                    imageVector = Icons.Rounded.DragIndicator,
                    contentDescription = "Drag to resize split panes",
                    tint = pillColor,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun FloatingGroupLiveClockChip(
    state: StudyTimerGroupUiState,
    clockTickFlow: StateFlow<LiveClockTick>,
    clockTheme: ClockThemeId,
    onRestoreSplit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = clockTheme.palette
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = palette.housingBackground,
        border = BorderStroke(1.dp, palette.accentColor.copy(alpha = 0.65f)),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onRestoreSplit)
            .testTag("group_floating_live_clock"),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(palette.accentColor),
            )
            LiveClockTickTextLeaf(
                clockTickFlow = clockTickFlow,
                textProvider = { tick -> formatDurationHms(tick.displaySeconds) },
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                ),
                color = palette.digitInk,
            )
            Text(
                text = state.activeSubjectName ?: "Timer",
                style = MicroTagMono,
                color = palette.subLabelInk,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
