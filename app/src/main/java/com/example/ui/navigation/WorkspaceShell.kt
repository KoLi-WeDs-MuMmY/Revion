package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.InspectSelection
import com.example.model.WorkspaceTab
import com.example.model.WorkspaceUiState
import com.example.ui.home.HomeCommandCenter
import com.example.ui.placeholders.PhasePlaceholderScreen
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens

@Composable
fun WorkspaceShell(
    uiState: WorkspaceUiState,
    onSelectTab: (WorkspaceTab) -> Unit,
    onToggleFocusMode: () -> Unit,
    onSelectAccentMood: (AccentMood) -> Unit,
    onCycleAccentMood: () -> Unit,
    onOpenInspect: (InspectSelection) -> Unit,
    onCloseInspect: () -> Unit,
    onJumpFromInspect: (WorkspaceTab) -> Unit,
    onNavigateBackToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    // Hardware/gesture Back returns cleanly to Home from any Phase 1.2-1.5 tab
    BackHandler(enabled = uiState.activeTab != WorkspaceTab.HOME) {
        onNavigateBackToHome()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.amoledBackground),
    ) {
        // Landscape-first responsive decision based on actual container constraints
        val isWideLayout = maxWidth >= 640.dp || maxWidth > maxHeight

        if (isWideLayout) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tokens.amoledBackground),
            ) {
                GhickinNavRail(
                    activeTab = uiState.activeTab,
                    onSelectTab = onSelectTab,
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .windowInsetsPadding(
                            WindowInsets.statusBars
                                .union(WindowInsets.navigationBars)
                                .union(WindowInsets.displayCutout),
                        ),
                ) {
                    WorkspaceStage(
                        uiState = uiState,
                        isWideLayout = true,
                        onToggleFocusMode = onToggleFocusMode,
                        onSelectAccentMood = onSelectAccentMood,
                        onCycleAccentMood = onCycleAccentMood,
                        onOpenInspect = onOpenInspect,
                        onCloseInspect = onCloseInspect,
                        onJumpFromInspect = onJumpFromInspect,
                        onReturnHome = { onSelectTab(WorkspaceTab.HOME) },
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tokens.amoledBackground),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .windowInsetsPadding(
                            WindowInsets.statusBars.union(WindowInsets.displayCutout),
                        ),
                ) {
                    WorkspaceStage(
                        uiState = uiState,
                        isWideLayout = false,
                        onToggleFocusMode = onToggleFocusMode,
                        onSelectAccentMood = onSelectAccentMood,
                        onCycleAccentMood = onCycleAccentMood,
                        onOpenInspect = onOpenInspect,
                        onCloseInspect = onCloseInspect,
                        onJumpFromInspect = onJumpFromInspect,
                        onReturnHome = { onSelectTab(WorkspaceTab.HOME) },
                    )
                }

                GhickinBottomNavBar(
                    activeTab = uiState.activeTab,
                    onSelectTab = onSelectTab,
                )
            }
        }
    }
}

@Composable
private fun WorkspaceStage(
    uiState: WorkspaceUiState,
    isWideLayout: Boolean,
    onToggleFocusMode: () -> Unit,
    onSelectAccentMood: (AccentMood) -> Unit,
    onCycleAccentMood: () -> Unit,
    onOpenInspect: (InspectSelection) -> Unit,
    onCloseInspect: () -> Unit,
    onJumpFromInspect: (WorkspaceTab) -> Unit,
    onReturnHome: () -> Unit,
) {
    val spatialSpring = spring<IntOffset>(
        dampingRatio = 0.88f,
        stiffness = 420f,
    )

    AnimatedContent(
        targetState = uiState.activeTab,
        transitionSpec = {
            val forward = targetState.ordinal >= initialState.ordinal
            val direction = if (forward) 1 else -1
            (fadeIn(animationSpec = tween(220)) +
                slideInHorizontally(animationSpec = spatialSpring) { fullWidth ->
                    direction * (fullWidth / 14)
                }) togetherWith
                (fadeOut(animationSpec = tween(160)) +
                    slideOutHorizontally(animationSpec = spatialSpring) { fullWidth ->
                        -direction * (fullWidth / 18)
                    })
        },
        label = "workspaceStageTransition",
    ) { tab ->
        when (tab) {
            WorkspaceTab.HOME -> HomeCommandCenter(
                uiState = uiState,
                isWideLayout = isWideLayout,
                onToggleFocusMode = onToggleFocusMode,
                onSelectAccentMood = onSelectAccentMood,
                onCycleAccentMood = onCycleAccentMood,
                onOpenInspect = onOpenInspect,
                onCloseInspect = onCloseInspect,
                onJumpFromInspect = onJumpFromInspect,
            )

            else -> PhasePlaceholderScreen(
                tab = tab,
                activeMood = uiState.accentMood,
                isWideLayout = isWideLayout,
                onReturnHome = onReturnHome,
                onCycleAccentMood = onCycleAccentMood,
            )
        }
    }
}
