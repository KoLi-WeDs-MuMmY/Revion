package com.example.ui.tutor

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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarViewDay
import androidx.compose.material.icons.rounded.ViewSidebar
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
import com.example.model.PermissionRequestAction
import com.example.model.PlannerBlockCategory
import com.example.model.TutorPlannerMode
import com.example.model.TutorPlannerUiState
import com.example.ui.components.tactileScale
import com.example.ui.planner.AiPermissionGateSheet
import com.example.ui.planner.DailyPlannerView
import com.example.ui.planner.PlannerScheduleSettingsSheet
import com.example.ui.theme.FeedbackCorrect
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import kotlinx.coroutines.launch

@Composable
fun TutorPlannerHubScreen(
    state: TutorPlannerUiState,
    isWideLayout: Boolean,
    onSelectMode: (TutorPlannerMode) -> Unit,
    onToggleCoPlanSplit: () -> Unit,
    onSelectRagChapter: (String) -> Unit,
    onSendTutorDoubt: (String) -> Unit,
    onOpenScheduleSettings: () -> Unit,
    onCloseScheduleSettings: () -> Unit,
    onToggleAdaptiveSync: (Boolean) -> Unit,
    onStepManualWake: (Int) -> Unit,
    onStepManualSleep: (Int) -> Unit,
    onToggleBlockCompleted: (String) -> Unit,
    onRequestAddBlock: () -> Unit,
    onRequestEditBlock: (String) -> Unit,
    onClosePermissionRequest: () -> Unit,
    onUpdatePermissionDraft: (
        action: PermissionRequestAction?,
        title: String?,
        category: PlannerBlockCategory?,
        startMinutes: Int?,
        durationMinutes: Int?,
        reasonText: String?,
    ) -> Unit,
    onSubmitPermissionToAi: () -> Unit,
    onApplyApprovedPermission: () -> Unit,
    onAcceptCounterProposal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val appliedMsg = stringResource(R.string.planner_snackbar_applied)
    val counterMsg = stringResource(R.string.planner_snackbar_counter)

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("tutor_planner_hub_screen"),
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
            // Top Hub Header + Segmented Switcher + Co-Plan Split Toggle
            if (isWideLayout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TutorPlannerTitleBlock(modifier = Modifier.weight(1f))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CoPlanSplitTogglePill(
                            active = state.isCoPlanSplitEnabled,
                            onClick = onToggleCoPlanSplit,
                        )
                        TutorPlannerModeSwitcher(
                            activeMode = state.activeMode,
                            isCoPlanActive = state.isCoPlanSplitEnabled,
                            onSelectMode = onSelectMode,
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TutorPlannerTitleBlock(modifier = Modifier.weight(1f))
                        CoPlanSplitTogglePill(
                            active = state.isCoPlanSplitEnabled,
                            onClick = onToggleCoPlanSplit,
                        )
                    }
                    TutorPlannerModeSwitcher(
                        activeMode = state.activeMode,
                        isCoPlanActive = state.isCoPlanSplitEnabled,
                        onSelectMode = onSelectMode,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Stage: Either Split Co-Plan Mode (Both Simultaneous) OR Segmented Single Mode
            val stageKey = if (state.isCoPlanSplitEnabled) "CO_PLAN_SPLIT" else state.activeMode.name
            AnimatedContent(
                targetState = stageKey,
                transitionSpec = {
                    val slideSpring = spring<IntOffset>(dampingRatio = 0.86f, stiffness = 380f)
                    (fadeIn(tween(220)) + slideInHorizontally(slideSpring) { it / 16 }) togetherWith
                        (fadeOut(tween(150)) + slideOutHorizontally(slideSpring) { -it / 16 })
                },
                label = "tutorPlannerStageTransition",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { key ->
                when (key) {
                    "CO_PLAN_SPLIT" -> {
                        if (isWideLayout) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("coplan_split_workspace"),
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                ChapterRagTutorView(
                                    state = state,
                                    isWideLayout = true,
                                    compactCoPlan = true,
                                    onSelectChapter = onSelectRagChapter,
                                    onSendDoubt = onSendTutorDoubt,
                                    modifier = Modifier
                                        .weight(0.46f)
                                        .fillMaxHeight(),
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp)
                                        .background(tokens.hairlineRest),
                                )
                                DailyPlannerView(
                                    state = state,
                                    isWideLayout = true,
                                    compactCoPlan = true,
                                    onOpenScheduleSettings = onOpenScheduleSettings,
                                    onToggleBlockCompleted = onToggleBlockCompleted,
                                    onRequestAddBlock = onRequestAddBlock,
                                    onRequestEditBlock = onRequestEditBlock,
                                    modifier = Modifier
                                        .weight(0.54f)
                                        .fillMaxHeight(),
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("coplan_split_workspace"),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                ChapterRagTutorView(
                                    state = state,
                                    isWideLayout = false,
                                    compactCoPlan = true,
                                    onSelectChapter = onSelectRagChapter,
                                    onSendDoubt = onSendTutorDoubt,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.48f),
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(tokens.hairlineRest),
                                )
                                DailyPlannerView(
                                    state = state,
                                    isWideLayout = false,
                                    compactCoPlan = true,
                                    onOpenScheduleSettings = onOpenScheduleSettings,
                                    onToggleBlockCompleted = onToggleBlockCompleted,
                                    onRequestAddBlock = onRequestAddBlock,
                                    onRequestEditBlock = onRequestEditBlock,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.52f),
                                )
                            }
                        }
                    }

                    TutorPlannerMode.DAILY_PLANNER.name -> DailyPlannerView(
                        state = state,
                        isWideLayout = isWideLayout,
                        compactCoPlan = false,
                        onOpenScheduleSettings = onOpenScheduleSettings,
                        onToggleBlockCompleted = onToggleBlockCompleted,
                        onRequestAddBlock = onRequestAddBlock,
                        onRequestEditBlock = onRequestEditBlock,
                    )

                    else -> ChapterRagTutorView(
                        state = state,
                        isWideLayout = isWideLayout,
                        compactCoPlan = false,
                        onSelectChapter = onSelectRagChapter,
                        onSendDoubt = onSendTutorDoubt,
                    )
                }
            }
        }

        // Wake/Sleep Settings & Adaptive Telemetry Modal
        if (state.isScheduleSettingsOpen) {
            PlannerScheduleSettingsSheet(
                state = state,
                onDismiss = onCloseScheduleSettings,
                onToggleAdaptiveSync = onToggleAdaptiveSync,
                onStepManualWake = onStepManualWake,
                onStepManualSleep = onStepManualSleep,
            )
        }

        // AI-Permission-Gated Add / Edit Block Modal
        val activeRequest = state.activePermissionRequest
        if (activeRequest != null) {
            AiPermissionGateSheet(
                request = activeRequest,
                onDismiss = onClosePermissionRequest,
                onUpdateDraft = onUpdatePermissionDraft,
                onSubmitToAi = onSubmitPermissionToAi,
                onApplyApproved = {
                    onApplyApprovedPermission()
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(appliedMsg, duration = SnackbarDuration.Short)
                    }
                },
                onAcceptCounterProposal = {
                    onAcceptCounterProposal()
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(counterMsg, duration = SnackbarDuration.Short)
                    }
                },
            )
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
private fun TutorPlannerTitleBlock(modifier: Modifier = Modifier) {
    val tokens = GhickinTokens.colors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = stringResource(R.string.tutor_planner_hub_title),
            style = MaterialTheme.typography.headlineMedium,
            color = tokens.inkPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(R.string.tutor_planner_hub_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = tokens.inkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CoPlanSplitTogglePill(
    active: Boolean,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val accent = if (active) FeedbackCorrect else MaterialTheme.colorScheme.primary
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(14.dp)

    val bg by animateColorAsState(
        targetValue = if (active) FeedbackCorrect.copy(alpha = 0.16f) else tokens.obsidianSurface,
        animationSpec = spring(dampingRatio = 0.84f, stiffness = 400f),
        label = "coPlanBg",
    )
    val border by animateColorAsState(
        targetValue = if (active) FeedbackCorrect.copy(alpha = 0.56f) else tokens.hairlineRest,
        animationSpec = spring(dampingRatio = 0.84f, stiffness = 400f),
        label = "coPlanBorder",
    )

    Surface(
        shape = shape,
        color = bg,
        border = BorderStroke(1.dp, border),
        modifier = Modifier
            .testTag("tutor_coplan_toggle_button")
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.95f)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = accent.copy(alpha = 0.18f)),
                role = Role.Switch,
                onClick = onClick,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.ViewSidebar,
                contentDescription = null,
                tint = if (active) FeedbackCorrect else tokens.inkSecondary,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = if (active) {
                    stringResource(R.string.tutor_coplan_active)
                } else {
                    stringResource(R.string.tutor_coplan_toggle)
                },
                style = MicroTagMono,
                color = if (active) FeedbackCorrect else tokens.inkPrimary,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun TutorPlannerModeSwitcher(
    activeMode: TutorPlannerMode,
    isCoPlanActive: Boolean,
    onSelectMode: (TutorPlannerMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier.testTag("tutor_planner_mode_switcher"),
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TutorModeSegment(
                label = stringResource(R.string.tutor_mode_ai),
                icon = Icons.Rounded.AutoAwesome,
                selected = !isCoPlanActive && activeMode == TutorPlannerMode.AI_TUTOR,
                accentColor = primary,
                onClick = { onSelectMode(TutorPlannerMode.AI_TUTOR) },
                modifier = Modifier.testTag("hub_mode_ai_tutor"),
            )
            TutorModeSegment(
                label = stringResource(R.string.tutor_mode_planner),
                icon = Icons.Rounded.CalendarViewDay,
                selected = !isCoPlanActive && activeMode == TutorPlannerMode.DAILY_PLANNER,
                accentColor = primary,
                onClick = { onSelectMode(TutorPlannerMode.DAILY_PLANNER) },
                modifier = Modifier.testTag("hub_mode_daily_planner"),
            )
        }
    }
}

@Composable
private fun TutorModeSegment(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(12.dp)

    val bg by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f),
        label = "tutorSegmentBg",
    )
    val border by animateColorAsState(
        targetValue = if (selected) accentColor.copy(alpha = 0.45f) else Color.Transparent,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f),
        label = "tutorSegmentBorder",
    )

    Surface(
        shape = shape,
        color = bg,
        border = BorderStroke(1.dp, border),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, pressedScale = 0.96f)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = accentColor.copy(alpha = 0.18f)),
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
                color = if (selected) tokens.inkPrimary else tokens.inkSecondary,
                maxLines = 1,
            )
        }
    }
}
