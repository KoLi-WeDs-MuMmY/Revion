package com.example.ui.timer

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.SwapHoriz
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PomodoroPhase
import com.example.model.PomodoroRingStyle
import com.example.model.StudyTimerGroupUiState
import com.example.model.StudyTimerMode
import com.example.model.TimerSplitRatio
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

/**
 * Full-Screen Distraction-Free Synced Study Timer & YPT Group Study Workspace:
 * - Opens with all app navigation collapsed.
 * - Supports hold-and-drag 5-ratio split screen: 100:0, 75:25, 50:50, 25:75, 0:100.
 * - Auto-switches to 25:75 (Timer:Group) on timer start.
 * - Shows floating live clock in upper-left corner when in 0:100 pure Group Study.
 * - In high timer view (75:25 or 100:0), displays Subjects & today's study time on the right
 *   (stacked below on narrow portrait) with direct click-to-start.
 * - Enforces single-timer lock & Vault-only access while a timer is running.
 */
@Composable
fun StudyTimerAndGroupScreen(
    state: StudyTimerGroupUiState,
    subjects: List<VaultSubjectNode>,
    isWideLayout: Boolean,
    onExitToHome: () -> Unit,
    onOpenVaultWhileTimerActive: () -> Unit,
    onSelectMode: (StudyTimerMode) -> Unit,
    onSelectPomodoroStyle: (PomodoroRingStyle) -> Unit,
    onApplyPomodoroPreset: (focusMin: Int, breakMin: Int) -> Unit,
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
    onSetSplitRatio: (TimerSplitRatio) -> Unit,
    onToggleGroupStudySplit: () -> Unit,
    onOpenStatsSheet: () -> Unit,
    onCloseStatsSheet: () -> Unit,
    onJoinGroup: (String) -> Unit,
    onOpenCreateGroupDialog: () -> Unit,
    onCloseCreateGroupDialog: () -> Unit,
    onCreatePublicGroup: (name: String, category: String, targetHours: Int) -> Unit,
    onSelectUserStatus: (YptMemberStatus) -> Unit,
    onDismissLockNotice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    // While a manual timer is active, Back goes to Vault-only or shows lock notice rather than unlocking all tabs
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
        // Compact Full-Screen Top Bar
        TimerFullScreenTopBar(
            state = state,
            isWideLayout = isWideLayout,
            onExitToHome = onExitToHome,
            onOpenVault = onOpenVaultWhileTimerActive,
            onSelectMode = onSelectMode,
            onToggleGroupSplit = onToggleGroupStudySplit,
            onOpenStats = onOpenStatsSheet,
        )

        // Single-Timer Lock Warning Banner (if user attempted starting a 2nd timer)
        if (state.singleTimerLockNotice != null) {
            Surface(
                color = AccentMood.AMBER.accent.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, AccentMood.AMBER.accent.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("single_timer_lock_banner"),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                        text = state.singleTimerLockNotice,
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    val dismissInt = remember { MutableInteractionSource() }
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Dismiss lock warning",
                        tint = tokens.inkMuted,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                                interactionSource = dismissInt,
                                indication = null,
                                onClick = onDismissLockNotice,
                            ),
                    )
                }
            }
        }

        // Main Interactive 5-Ratio Draggable Split Stage
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
        ) {
            val density = LocalDensity.current
            val totalAxisPx = remember(maxWidth, maxHeight, isWideLayout, density) {
                with(density) { (if (isWideLayout) maxWidth else maxHeight).toPx() }.coerceAtLeast(1f)
            }
            var isDragging by remember { mutableStateOf(false) }
            var liveFraction by remember(state.splitRatio) { mutableFloatStateOf(state.splitRatio.timerFraction) }

            val activeFraction = if (isDragging) liveFraction else state.splitRatio.timerFraction
            val timerFrac = activeFraction.coerceIn(0f, 1f)
            val groupFrac = (1f - timerFrac).coerceIn(0f, 1f)

            val showTimerPane = timerFrac > 0.05f
            val showGroupPane = groupFrac > 0.05f

            if (isWideLayout) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (showTimerPane) {
                        TimerPrimaryPane(
                            state = state,
                            subjects = subjects,
                            isWideLayout = true,
                            isCompactPane = timerFrac <= 0.35f,
                            showSubjectsPanel = state.splitRatio.isHighTimerView && !isDragging,
                            onSelectPomodoroStyle = onSelectPomodoroStyle,
                            onApplyPomodoroPreset = onApplyPomodoroPreset,
                            onSwitchPomodoroPhase = onSwitchPomodoroPhase,
                            onAddFiveMinutes = onAddFiveMinutes,
                            onRequestStartFromMainButton = onRequestStartFromMainButton,
                            onQuickStartSubject = onQuickStartSubject,
                            onPauseResumeTimer = onPauseResumeTimer,
                            onResetTimer = onResetTimer,
                            onStopAndLogTimer = onStopAndLogTimer,
                            modifier = Modifier
                                .weight(timerFrac)
                                .fillMaxHeight(),
                        )
                    }

                    // Draggable Center Handle (always accessible to drag between 100:0 .. 0:100)
                    SplitDragHandle(
                        isHorizontalSplit = true,
                        currentRatioLabel = TimerSplitRatio.nearestTo(timerFrac).label,
                        isDragging = isDragging,
                        onDragStart = {
                            isDragging = true
                            liveFraction = state.splitRatio.timerFraction
                        },
                        onDragDelta = { deltaPx ->
                            liveFraction = (liveFraction + (deltaPx / totalAxisPx)).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            val snapped = TimerSplitRatio.nearestTo(liveFraction)
                            isDragging = false
                            liveFraction = snapped.timerFraction
                            onSetSplitRatio(snapped)
                        },
                        onCycleSnap = {
                            val entries = TimerSplitRatio.entries
                            val nextIdx = (entries.indexOf(state.splitRatio) + 1) % entries.size
                            onSetSplitRatio(entries[nextIdx])
                        },
                    )

                    if (showGroupPane) {
                        YptGroupStudyPane(
                            state = state,
                            isPureGroupScreen = state.splitRatio == TimerSplitRatio.RATIO_0_100,
                            isCompactWidth = groupFrac <= 0.35f,
                            onJoinGroup = onJoinGroup,
                            onOpenCreateGroupDialog = onOpenCreateGroupDialog,
                            onSelectUserStatus = onSelectUserStatus,
                            modifier = Modifier
                                .weight(groupFrac)
                                .fillMaxHeight(),
                        )
                    }
                }
            } else {
                // 9:16 Portrait: Vertical Draggable Split
                Column(modifier = Modifier.fillMaxSize()) {
                    if (showTimerPane) {
                        TimerPrimaryPane(
                            state = state,
                            subjects = subjects,
                            isWideLayout = false,
                            isCompactPane = timerFrac <= 0.35f,
                            showSubjectsPanel = state.splitRatio.isHighTimerView && !isDragging,
                            onSelectPomodoroStyle = onSelectPomodoroStyle,
                            onApplyPomodoroPreset = onApplyPomodoroPreset,
                            onSwitchPomodoroPhase = onSwitchPomodoroPhase,
                            onAddFiveMinutes = onAddFiveMinutes,
                            onRequestStartFromMainButton = onRequestStartFromMainButton,
                            onQuickStartSubject = onQuickStartSubject,
                            onPauseResumeTimer = onPauseResumeTimer,
                            onResetTimer = onResetTimer,
                            onStopAndLogTimer = onStopAndLogTimer,
                            modifier = Modifier
                                .weight(timerFrac)
                                .fillMaxWidth(),
                        )
                    }

                    SplitDragHandle(
                        isHorizontalSplit = false,
                        currentRatioLabel = TimerSplitRatio.nearestTo(timerFrac).label,
                        isDragging = isDragging,
                        onDragStart = {
                            isDragging = true
                            liveFraction = state.splitRatio.timerFraction
                        },
                        onDragDelta = { deltaPx ->
                            liveFraction = (liveFraction + (deltaPx / totalAxisPx)).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            val snapped = TimerSplitRatio.nearestTo(liveFraction)
                            isDragging = false
                            liveFraction = snapped.timerFraction
                            onSetSplitRatio(snapped)
                        },
                        onCycleSnap = {
                            val entries = TimerSplitRatio.entries
                            val nextIdx = (entries.indexOf(state.splitRatio) + 1) % entries.size
                            onSetSplitRatio(entries[nextIdx])
                        },
                    )

                    if (showGroupPane) {
                        YptGroupStudyPane(
                            state = state,
                            isPureGroupScreen = state.splitRatio == TimerSplitRatio.RATIO_0_100,
                            isCompactWidth = true,
                            onJoinGroup = onJoinGroup,
                            onOpenCreateGroupDialog = onOpenCreateGroupDialog,
                            onSelectUserStatus = onSelectUserStatus,
                            modifier = Modifier
                                .weight(groupFrac)
                                .fillMaxWidth(),
                        )
                    }
                }
            }

            // Floating Live Ticking Mini Clock in UPPER-LEFT corner when in Pure Group Study (0:100)
            if (state.splitRatio == TimerSplitRatio.RATIO_0_100 && state.isTimerActiveSession) {
                val pillInt = remember { MutableInteractionSource() }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.obsidianSurface.copy(alpha = 0.96f),
                    border = BorderStroke(1.dp, tokens.semanticSuccess.copy(alpha = 0.65f)),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 12.dp, top = 8.dp)
                        .testTag("pure_group_floating_upper_left_clock")
                        .tactileScale(pillInt, 0.95f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = pillInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = { onSetSplitRatio(TimerSplitRatio.RATIO_25_75) },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (state.isRunning) tokens.semanticSuccess else AccentMood.AMBER.accent),
                        )
                        Text(
                            text = formatDurationHms(state.displaySeconds),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum",
                            ),
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = state.activeSubjectName ?: "Tagless",
                            style = MicroTagMono,
                            color = tokens.themeAccent,
                            maxLines = 1,
                        )
                    }
                }
            }

            // Live Drag Snap Indicator Overlay
            if (isDragging) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp),
                ) {
                    Text(
                        text = "SNAP ${TimerSplitRatio.nearestTo(liveFraction).label}",
                        style = MicroTagMono,
                        color = tokens.onPrimaryCta,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    )
                }
            }
        }

        // Quick 5-Ratio Preset Bar at bottom edge (complements Hold & Drag for instant precision)
        SplitRatioQuickStrip(
            activeRatio = state.splitRatio,
            onSelectRatio = onSetSplitRatio,
        )
    }

    // Popups & Sheets
    if (state.showStartTagDialog) {
        TimerStartTagDialog(
            subjects = subjects,
            onStartWithSelection = onConfirmStartWithTag,
            onSkipTagless = onSkipStartTagless,
            onDismiss = onDismissStartTagDialog,
        )
    }

    if (state.showStatsSheet) {
        TimerStatsAndLeaderboardSheet(
            state = state,
            onDismiss = onCloseStatsSheet,
        )
    }

    if (state.showCreateGroupDialog) {
        CreateYptGroupDialog(
            onCreateGroup = onCreatePublicGroup,
            onDismiss = onCloseCreateGroupDialog,
        )
    }
}

@Composable
private fun TimerFullScreenTopBar(
    state: StudyTimerGroupUiState,
    isWideLayout: Boolean,
    onExitToHome: () -> Unit,
    onOpenVault: () -> Unit,
    onSelectMode: (StudyTimerMode) -> Unit,
    onToggleGroupSplit: () -> Unit,
    onOpenStats: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val backInt = remember { MutableInteractionSource() }
    val vaultInt = remember { MutableInteractionSource() }
    val groupToggleInt = remember { MutableInteractionSource() }
    val statsInt = remember { MutableInteractionSource() }

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
            if (!state.isManualTimerLocked) {
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .testTag("timer_exit_home_button")
                        .minimumInteractiveComponentSize()
                        .tactileScale(backInt, 0.94f)
                        .clip(RoundedCornerShape(11.dp))
                        .clickable(
                            interactionSource = backInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = onExitToHome,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Exit full-screen timer",
                            tint = tokens.inkPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                        if (isWideLayout) {
                            Text(
                                text = "Exit",
                                style = MaterialTheme.typography.labelMedium,
                                color = tokens.inkPrimary,
                            )
                        }
                    }
                }
            } else {
                // While timer is active: Vault-Only Access!
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = tokens.themeAccent.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, tokens.themeAccent.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .testTag("timer_active_open_vault_button")
                        .minimumInteractiveComponentSize()
                        .tactileScale(vaultInt, 0.95f)
                        .clip(RoundedCornerShape(11.dp))
                        .clickable(
                            interactionSource = vaultInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = onOpenVault,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FolderCopy,
                            contentDescription = "Open Vault while timer runs",
                            tint = tokens.themeAccent,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = if (isWideLayout) "Vault Only" else "Vault",
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.themeAccent,
                        )
                    }
                }
            }

            // Pomodoro | Stopwatch Mode Selector
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(11.dp))
                    .background(tokens.carbonSurface)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                StudyTimerMode.entries.forEach { m ->
                    val active = state.mode == m
                    val mInt = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .testTag("timer_mode_${m.name.lowercase()}")
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (active) tokens.primaryCta else Color.Transparent)
                            .clickable(
                                interactionSource = mInt,
                                indication = ripple(),
                                role = Role.Tab,
                                onClick = { onSelectMode(m) },
                            )
                            .padding(horizontal = if (isWideLayout) 12.dp else 9.dp, vertical = 7.dp),
                    ) {
                        Text(
                            text = m.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (active) tokens.onPrimaryCta else tokens.inkSecondary,
                            maxLines = 1,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Group Study Split Toggle (100:0/0:100 <-> 75:25/25:75)
            val isGroupVisible = state.splitRatio != TimerSplitRatio.RATIO_100_0
            Surface(
                shape = RoundedCornerShape(11.dp),
                color = if (isGroupVisible) tokens.themeAccent.copy(alpha = 0.16f) else tokens.carbonSurface,
                border = BorderStroke(1.dp, if (isGroupVisible) tokens.themeAccent else tokens.hairlineRest),
                modifier = Modifier
                    .testTag("timer_group_split_toggle")
                    .minimumInteractiveComponentSize()
                    .tactileScale(groupToggleInt, 0.95f)
                    .clip(RoundedCornerShape(11.dp))
                    .clickable(
                        interactionSource = groupToggleInt,
                        indication = ripple(),
                        role = Role.Button,
                        onClick = onToggleGroupSplit,
                    ),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Groups,
                        contentDescription = "Toggle Group Study split",
                        tint = if (isGroupVisible) tokens.themeAccent else tokens.inkSecondary,
                        modifier = Modifier.size(17.dp),
                    )
                    if (isWideLayout) {
                        Text(
                            text = "Group (${state.splitRatio.label})",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isGroupVisible) tokens.themeAccent else tokens.inkPrimary,
                        )
                    }
                }
            }

            // Stats & Leaderboard Button
            Surface(
                shape = RoundedCornerShape(11.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .testTag("timer_open_stats_button")
                    .minimumInteractiveComponentSize()
                    .tactileScale(statsInt, 0.95f)
                    .clip(RoundedCornerShape(11.dp))
                    .clickable(
                        interactionSource = statsInt,
                        indication = ripple(),
                        role = Role.Button,
                        onClick = onOpenStats,
                    ),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BarChart,
                        contentDescription = "Open timer stats and leaderboard",
                        tint = tokens.inkPrimary,
                        modifier = Modifier.size(17.dp),
                    )
                    if (isWideLayout) {
                        Text(
                            text = "Stats",
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.inkPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimerPrimaryPane(
    state: StudyTimerGroupUiState,
    subjects: List<VaultSubjectNode>,
    isWideLayout: Boolean,
    isCompactPane: Boolean,
    showSubjectsPanel: Boolean,
    onSelectPomodoroStyle: (PomodoroRingStyle) -> Unit,
    onApplyPomodoroPreset: (Int, Int) -> Unit,
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
    val activeAccent = if (state.activeSubjectName != null) state.activeSubjectMood.accent else tokens.themeAccent

    if (showSubjectsPanel && isWideLayout) {
        // High Timer % in 16:9 Landscape: Timer UI on left + Subjects Today panel on right
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            TimerInstrumentColumn(
                state = state,
                activeAccent = activeAccent,
                compact = false,
                onSelectPomodoroStyle = onSelectPomodoroStyle,
                onApplyPomodoroPreset = onApplyPomodoroPreset,
                onSwitchPomodoroPhase = onSwitchPomodoroPhase,
                onAddFiveMinutes = onAddFiveMinutes,
                onRequestStartFromMainButton = onRequestStartFromMainButton,
                onPauseResumeTimer = onPauseResumeTimer,
                onResetTimer = onResetTimer,
                onStopAndLogTimer = onStopAndLogTimer,
                modifier = Modifier.weight(0.60f),
            )
            SubjectsTodayQuickStartPanel(
                state = state,
                subjects = subjects,
                onQuickStartSubject = onQuickStartSubject,
                modifier = Modifier
                    .weight(0.40f)
                    .fillMaxHeight(),
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = if (isCompactPane) 10.dp else 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TimerInstrumentColumn(
                state = state,
                activeAccent = activeAccent,
                compact = isCompactPane,
                onSelectPomodoroStyle = onSelectPomodoroStyle,
                onApplyPomodoroPreset = onApplyPomodoroPreset,
                onSwitchPomodoroPhase = onSwitchPomodoroPhase,
                onAddFiveMinutes = onAddFiveMinutes,
                onRequestStartFromMainButton = onRequestStartFromMainButton,
                onPauseResumeTimer = onPauseResumeTimer,
                onResetTimer = onResetTimer,
                onStopAndLogTimer = onStopAndLogTimer,
                scrollable = false,
            )

            // High Timer % in 9:16 Portrait: Subjects Today stacked cleanly below the timer dial
            if (showSubjectsPanel && !isWideLayout) {
                SubjectsTodayQuickStartPanel(
                    state = state,
                    subjects = subjects,
                    onQuickStartSubject = onQuickStartSubject,
                    scrollable = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun TimerInstrumentColumn(
    state: StudyTimerGroupUiState,
    activeAccent: Color,
    compact: Boolean,
    onSelectPomodoroStyle: (PomodoroRingStyle) -> Unit,
    onApplyPomodoroPreset: (Int, Int) -> Unit,
    onSwitchPomodoroPhase: () -> Unit,
    onAddFiveMinutes: () -> Unit,
    onRequestStartFromMainButton: () -> Unit,
    onPauseResumeTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onStopAndLogTimer: () -> Unit,
    scrollable: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val baseMod = if (scrollable) {
        modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    } else {
        modifier.fillMaxWidth()
    }

    Column(
        modifier = baseMod,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp),
    ) {
        // Active Tag & Synced Lecture Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.45f)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(activeAccent),
                    )
                    Text(
                        text = state.activeSubjectName ?: "No Subject Tag",
                        style = MicroTagMono,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                    )
                }
            }
            if (state.syncedLectureTitle != null && !compact) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Link,
                            contentDescription = null,
                            tint = tokens.themeAccent,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = state.syncedLectureTitle,
                            style = MicroTagMono,
                            color = tokens.inkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        // Visual Instrument: Pomodoro 4-Style Circle OR Calendar/Flip Stopwatch
        if (state.mode == StudyTimerMode.POMODORO) {
            PomodoroCircleInstrument(
                style = state.pomodoroStyle,
                progress = state.pomodoroProgress,
                remainingSeconds = state.remainingSeconds,
                phase = state.pomodoroPhase,
                completedCycles = state.completedCycles,
                accentColor = activeAccent,
                dialSize = if (compact) 142.dp else 212.dp,
                compact = compact,
            )

            if (!compact) {
                // 4 Selectable Pomodoro Circle Designs
                PomodoroStyleSelectorStrip(
                    selectedStyle = state.pomodoroStyle,
                    accentColor = activeAccent,
                    onSelectStyle = onSelectPomodoroStyle,
                )

                // Quick Duration Presets + Custom steppers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    listOf(25 to 5, 50 to 10, 90 to 15).forEach { (f, b) ->
                        val activePreset = state.focusDurationMinutes == f && state.breakDurationMinutes == b
                        val pInt = remember { MutableInteractionSource() }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (activePreset) tokens.slateElevated else tokens.carbonSurface,
                            border = BorderStroke(1.dp, if (activePreset) activeAccent else tokens.hairlineSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pomodoro_preset_${f}_$b")
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(
                                    interactionSource = pInt,
                                    indication = ripple(),
                                    role = Role.Button,
                                    onClick = { onApplyPomodoroPreset(f, b) },
                                ),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 8.dp),
                            ) {
                                Text(
                                    text = "$f / $b",
                                    style = MicroTagMono,
                                    color = if (activePreset) activeAccent else tokens.inkSecondary,
                                )
                            }
                        }
                    }
                }
            }
        } else {
            CalendarFlipStopwatchView(
                elapsedSeconds = state.elapsedSeconds,
                accentColor = activeAccent,
                compact = compact,
            )
        }

        // Primary Transport Controls (Start / Pause-Resume / Stop & Log / Reset / +5m / Phase)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!state.isTimerActiveSession) {
                val startInt = remember { MutableInteractionSource() }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .testTag("timer_main_start_button")
                        .minimumInteractiveComponentSize()
                        .tactileScale(startInt, 0.96f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = startInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = onRequestStartFromMainButton,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = if (compact) 16.dp else 26.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Start timer",
                            tint = tokens.onPrimaryCta,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "Start",
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.onPrimaryCta,
                        )
                    }
                }
            } else {
                val pauseInt = remember { MutableInteractionSource() }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (state.isRunning) tokens.carbonSurface else tokens.primaryCta,
                    border = if (state.isRunning) BorderStroke(1.dp, activeAccent) else null,
                    modifier = Modifier
                        .testTag("timer_pause_resume_button")
                        .minimumInteractiveComponentSize()
                        .tactileScale(pauseInt, 0.96f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = pauseInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = onPauseResumeTimer,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = if (compact) 12.dp else 18.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            imageVector = if (state.isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (state.isRunning) "Pause timer" else "Resume timer",
                            tint = if (state.isRunning) activeAccent else tokens.onPrimaryCta,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = if (state.isRunning) "Pause" else "Resume",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (state.isRunning) activeAccent else tokens.onPrimaryCta,
                        )
                    }
                }

                val stopInt = remember { MutableInteractionSource() }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .testTag("timer_stop_and_log_button")
                        .minimumInteractiveComponentSize()
                        .tactileScale(stopInt, 0.96f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = stopInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = onStopAndLogTimer,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = if (compact) 11.dp else 16.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Stop,
                            contentDescription = "Finish and log session",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Finish",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }

        // Secondary controls (+5m, Switch Focus/Break, Reset)
        if (!compact) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (state.mode == StudyTimerMode.POMODORO) {
                    SmallTimerControlPill(
                        label = "+5m",
                        icon = Icons.Rounded.Add,
                        testTag = "timer_add_5m_button",
                        onClick = onAddFiveMinutes,
                    )
                    SmallTimerControlPill(
                        label = if (state.pomodoroPhase == PomodoroPhase.FOCUS) "Break" else "Focus",
                        icon = Icons.Rounded.SwapHoriz,
                        testTag = "timer_switch_phase_button",
                        onClick = onSwitchPomodoroPhase,
                    )
                }
                SmallTimerControlPill(
                    label = "Reset",
                    icon = Icons.Rounded.Refresh,
                    testTag = "timer_reset_button",
                    onClick = onResetTimer,
                )
            }
        }
    }
}

@Composable
private fun SubjectsTodayQuickStartPanel(
    state: StudyTimerGroupUiState,
    subjects: List<VaultSubjectNode>,
    onQuickStartSubject: (VaultSubjectNode) -> Unit,
    scrollable: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val contentMod = if (scrollable) {
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(14.dp)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier.testTag("timer_subjects_today_panel"),
    ) {
        Column(
            modifier = contentMod,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "SUBJECTS · STUDIED TODAY",
                        style = MicroTagMono,
                        color = tokens.themeAccent,
                    )
                    Text(
                        text = "Tap any subject to start immediately",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                    )
                }
                Text(
                    text = formatDurationCompactHm(state.todayTotalStudySeconds),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontFeatureSettings = "tnum",
                    ),
                    color = tokens.inkPrimary,
                )
            }

            subjects.forEach { subj ->
                val studiedSec = state.liveSubjectSecondsToday(subj.name)
                val isActive = state.isTimerActiveSession && state.activeSubjectName.equals(subj.name, ignoreCase = true)
                val subjInt = remember { MutableInteractionSource() }
                val accent = subj.accentMood.accent

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isActive) accent.copy(alpha = 0.14f) else tokens.carbonSurface,
                    border = BorderStroke(
                        1.dp,
                        if (isActive) accent else tokens.hairlineSubtle,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("timer_quick_subject_${subj.id}")
                        .minimumInteractiveComponentSize()
                        .tactileScale(subjInt, 0.97f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = subjInt,
                            indication = ripple(color = accent.copy(alpha = 0.2f)),
                            role = Role.Button,
                            onClick = { onQuickStartSubject(subj) },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(accent),
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = subj.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = tokens.inkPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = if (isActive) "LIVE · SYNCED TO GROUP" else "${subj.chapters.size} chapters in batch",
                                style = MicroTagMono,
                                color = if (isActive) accent else tokens.inkMuted,
                                maxLines = 1,
                            )
                        }
                        Text(
                            text = formatDurationHms(studiedSec),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontFeatureSettings = "tnum",
                            ),
                            color = if (isActive) accent else tokens.inkPrimary,
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(if (isActive) accent else tokens.slateElevated),
                        ) {
                            Icon(
                                imageVector = if (isActive) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = "Start ${subj.name} timer",
                                tint = if (isActive) Color.Black else tokens.inkPrimary,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }

            // Also show auto-logged Revision & Flashcards totals for today
            val revSec = state.todaySubjectSeconds["Revision"] ?: 0
            val fcSec = state.todaySubjectSeconds["Flashcards"] ?: 0
            if (revSec > 0 || fcSec > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AutoTagTodayChip(
                        label = "Revision (Auto)",
                        seconds = revSec,
                        accent = AccentMood.EMERALD.accent,
                        modifier = Modifier.weight(1f),
                    )
                    AutoTagTodayChip(
                        label = "Flashcards (Auto)",
                        seconds = fcSec,
                        accent = AccentMood.AMBER.accent,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AutoTagTodayChip(
    label: String,
    seconds: Int,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(11.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = label, style = MicroTagMono, color = accent, maxLines = 1)
            Text(
                text = formatDurationCompactHm(seconds),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontFeatureSettings = "tnum",
                ),
                color = tokens.inkPrimary,
            )
        }
    }
}

@Composable
private fun SplitDragHandle(
    isHorizontalSplit: Boolean,
    currentRatioLabel: String,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDragDelta: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onCycleSnap: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val handleBg = if (isDragging) tokens.themeAccent.copy(alpha = 0.25f) else tokens.obsidianSurface
    val pillColor = if (isDragging) tokens.themeAccent else tokens.inkMuted

    Box(
        contentAlignment = Alignment.Center,
        modifier = (if (isHorizontalSplit) {
            Modifier
                .fillMaxHeight()
                .width(24.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .height(24.dp)
        })
            .background(handleBg)
            .testTag("timer_split_drag_handle")
            .pointerInput(isHorizontalSplit) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDragDelta(if (isHorizontalSplit) dragAmount.x else dragAmount.y)
                    },
                )
            }
            .clickable(onClick = onCycleSnap),
    ) {
        // Center hairline
        Box(
            modifier = if (isHorizontalSplit) {
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(tokens.hairlineRest)
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(tokens.hairlineRest)
            },
        )
        // Tactile center grip pill
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, if (isDragging) tokens.themeAccent else tokens.hairlineRest),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = if (isHorizontalSplit) {
                    Modifier.padding(horizontal = 3.dp, vertical = 14.dp)
                } else {
                    Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                },
            ) {
                if (isHorizontalSplit) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(pillColor),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.DragHandle,
                        contentDescription = "Drag split screen ($currentRatioLabel)",
                        tint = pillColor,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SplitRatioQuickStrip(
    activeRatio: TimerSplitRatio,
    onSelectRatio: (TimerSplitRatio) -> Unit,
) {
    val tokens = GhickinTokens.colors

    Surface(
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "SPLIT",
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
            TimerSplitRatio.entries.forEach { ratio ->
                val selected = ratio == activeRatio
                val rInt = remember { MutableInteractionSource() }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (selected) tokens.themeAccent.copy(alpha = 0.18f) else tokens.carbonSurface,
                    border = BorderStroke(1.dp, if (selected) tokens.themeAccent else tokens.hairlineSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("timer_ratio_${ratio.label.replace(":", "_")}")
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = rInt,
                            indication = ripple(),
                            role = Role.RadioButton,
                            onClick = { onSelectRatio(ratio) },
                        ),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 6.dp),
                    ) {
                        Text(
                            text = ratio.label,
                            style = MicroTagMono.copy(fontSize = 10.sp),
                            color = if (selected) tokens.themeAccent else tokens.inkSecondary,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallTimerControlPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = Modifier
            .testTag(testTag)
            .tactileScale(interaction, 0.95f)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tokens.inkSecondary,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = tokens.inkSecondary,
            )
        }
    }
}
