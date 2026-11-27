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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.DailyPageRoute
import com.example.model.PresetSubjectTemplate
import com.example.model.VaultCategorySubView
import com.example.model.VaultLectureNode
import com.example.model.VaultPaletteFocus
import com.example.model.WorkspaceTab
import com.example.model.WorkspaceUiState
import com.example.ui.batches.BatchesScreen
import com.example.ui.components.AmbientMineralBackdrop
import com.example.ui.home.HomeCommandCenter
import com.example.ui.placeholders.PhasePlaceholderScreen
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.vault.PwVaultScreen

@Composable
fun WorkspaceShell(
    uiState: WorkspaceUiState,
    onSelectTab: (WorkspaceTab) -> Unit,
    onToggleFocusMode: () -> Unit,
    onSelectAccentMood: (AccentMood) -> Unit,
    onCycleAccentMood: () -> Unit,
    // Phase 1.6.1 Home Command Center & Dedicated Daily Pages callbacks
    onSetPrimaryBatch: (String) -> Unit,
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
    // Phase 1.2 + 1.6.1 Batches callbacks
    onSelectBatch: (String) -> Unit,
    onToggleExpandSubject: (String) -> Unit,
    onToggleChapterCompleted: (batchId: String, subjectId: String, chapterId: String) -> Unit,
    onOpenCreateBatchBuilder: () -> Unit,
    onCloseCreateBatchBuilder: () -> Unit,
    onCreateCustomBatch: (
        name: String,
        goalTag: String,
        accentMood: AccentMood,
        selectedSubjects: List<PresetSubjectTemplate>,
        customSubjectName: String,
    ) -> Unit,
    // Phase 1.6.1 PW-Style Vault callbacks
    onSelectPwVaultSubject: (String?) -> Unit,
    onSelectPwVaultChapter: (String?) -> Unit,
    onSelectPwVaultLecture: (String?) -> Unit,
    onSelectPwVaultSubView: (VaultCategorySubView) -> Unit,
    onToggleExpandedClassNote: (String) -> Unit,
    onToggleExpandedFlashcardChapter: (String) -> Unit,
    onSelectPreviewFlashcard: (String) -> Unit,
    onFlipPreviewFlashcard: () -> Unit,
    onSelectPwVaultRevisionSubject: (String) -> Unit,
    onOpenVaultCommandPalette: (VaultPaletteFocus) -> Unit,
    onCloseVaultCommandPalette: () -> Unit,
    onUpdateVaultPaletteQuery: (String) -> Unit,
    onSelectVaultPaletteSubjectFilter: (String?) -> Unit,
    onSelectVaultPaletteCategoryFilter: (VaultCategorySubView?) -> Unit,
    onClearVaultPaletteFilters: () -> Unit,
    onJumpToVaultLectureFromPalette: (VaultLectureNode, VaultCategorySubView) -> Unit,
    onNavigateBackToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    val canHandleBack = uiState.activeTab != WorkspaceTab.HOME ||
        uiState.homeCommandState.activeDailyPage != null ||
        uiState.inspectSelection != null

    BackHandler(enabled = canHandleBack) {
        onNavigateBackToHome()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.amoledBackground),
    ) {
        val isWideLayout = maxWidth >= 640.dp || maxWidth > maxHeight

        // Whisper-soft drifting mineral light wash on pure #000000 AMOLED void (non-neon)
        AmbientMineralBackdrop(
            accentMood = uiState.accentMood,
            modifier = Modifier.fillMaxSize(),
        )

        if (isWideLayout) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
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
                        onSelectTab = onSelectTab,
                        onToggleFocusMode = onToggleFocusMode,
                        onSelectAccentMood = onSelectAccentMood,
                        onCycleAccentMood = onCycleAccentMood,
                        onSetPrimaryBatch = onSetPrimaryBatch,
                        onCyclePrimaryBatch = onCyclePrimaryBatch,
                        onOpenDailyPage = onOpenDailyPage,
                        onCloseDailyPage = onCloseDailyPage,
                        onCompleteTodayRevision = onCompleteTodayRevision,
                        onFlipDailyFlashcard = onFlipDailyFlashcard,
                        onStepDailyFlashcard = onStepDailyFlashcard,
                        onCompleteTodayFlashcards = onCompleteTodayFlashcards,
                        onToggleTodayRevisionCompleted = onToggleTodayRevisionCompleted,
                        onToggleTodayFlashcardsCompleted = onToggleTodayFlashcardsCompleted,
                        onToggleStudyGateTestOverride = onToggleStudyGateTestOverride,
                        onStartTodaysNewStudy = onStartTodaysNewStudy,
                        onSelectBatch = onSelectBatch,
                        onToggleExpandSubject = onToggleExpandSubject,
                        onToggleChapterCompleted = onToggleChapterCompleted,
                        onOpenCreateBatchBuilder = onOpenCreateBatchBuilder,
                        onCloseCreateBatchBuilder = onCloseCreateBatchBuilder,
                        onCreateCustomBatch = onCreateCustomBatch,
                        onSelectPwVaultSubject = onSelectPwVaultSubject,
                        onSelectPwVaultChapter = onSelectPwVaultChapter,
                        onSelectPwVaultLecture = onSelectPwVaultLecture,
                        onSelectPwVaultSubView = onSelectPwVaultSubView,
                        onToggleExpandedClassNote = onToggleExpandedClassNote,
                        onToggleExpandedFlashcardChapter = onToggleExpandedFlashcardChapter,
                        onSelectPreviewFlashcard = onSelectPreviewFlashcard,
                        onFlipPreviewFlashcard = onFlipPreviewFlashcard,
                        onSelectPwVaultRevisionSubject = onSelectPwVaultRevisionSubject,
                        onOpenVaultCommandPalette = onOpenVaultCommandPalette,
                        onCloseVaultCommandPalette = onCloseVaultCommandPalette,
                        onUpdateVaultPaletteQuery = onUpdateVaultPaletteQuery,
                        onSelectVaultPaletteSubjectFilter = onSelectVaultPaletteSubjectFilter,
                        onSelectVaultPaletteCategoryFilter = onSelectVaultPaletteCategoryFilter,
                        onClearVaultPaletteFilters = onClearVaultPaletteFilters,
                        onJumpToVaultLectureFromPalette = onJumpToVaultLectureFromPalette,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
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
                        onSelectTab = onSelectTab,
                        onToggleFocusMode = onToggleFocusMode,
                        onSelectAccentMood = onSelectAccentMood,
                        onCycleAccentMood = onCycleAccentMood,
                        onSetPrimaryBatch = onSetPrimaryBatch,
                        onCyclePrimaryBatch = onCyclePrimaryBatch,
                        onOpenDailyPage = onOpenDailyPage,
                        onCloseDailyPage = onCloseDailyPage,
                        onCompleteTodayRevision = onCompleteTodayRevision,
                        onFlipDailyFlashcard = onFlipDailyFlashcard,
                        onStepDailyFlashcard = onStepDailyFlashcard,
                        onCompleteTodayFlashcards = onCompleteTodayFlashcards,
                        onToggleTodayRevisionCompleted = onToggleTodayRevisionCompleted,
                        onToggleTodayFlashcardsCompleted = onToggleTodayFlashcardsCompleted,
                        onToggleStudyGateTestOverride = onToggleStudyGateTestOverride,
                        onStartTodaysNewStudy = onStartTodaysNewStudy,
                        onSelectBatch = onSelectBatch,
                        onToggleExpandSubject = onToggleExpandSubject,
                        onToggleChapterCompleted = onToggleChapterCompleted,
                        onOpenCreateBatchBuilder = onOpenCreateBatchBuilder,
                        onCloseCreateBatchBuilder = onCloseCreateBatchBuilder,
                        onCreateCustomBatch = onCreateCustomBatch,
                        onSelectPwVaultSubject = onSelectPwVaultSubject,
                        onSelectPwVaultChapter = onSelectPwVaultChapter,
                        onSelectPwVaultLecture = onSelectPwVaultLecture,
                        onSelectPwVaultSubView = onSelectPwVaultSubView,
                        onToggleExpandedClassNote = onToggleExpandedClassNote,
                        onToggleExpandedFlashcardChapter = onToggleExpandedFlashcardChapter,
                        onSelectPreviewFlashcard = onSelectPreviewFlashcard,
                        onFlipPreviewFlashcard = onFlipPreviewFlashcard,
                        onSelectPwVaultRevisionSubject = onSelectPwVaultRevisionSubject,
                        onOpenVaultCommandPalette = onOpenVaultCommandPalette,
                        onCloseVaultCommandPalette = onCloseVaultCommandPalette,
                        onUpdateVaultPaletteQuery = onUpdateVaultPaletteQuery,
                        onSelectVaultPaletteSubjectFilter = onSelectVaultPaletteSubjectFilter,
                        onSelectVaultPaletteCategoryFilter = onSelectVaultPaletteCategoryFilter,
                        onClearVaultPaletteFilters = onClearVaultPaletteFilters,
                        onJumpToVaultLectureFromPalette = onJumpToVaultLectureFromPalette,
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
    onSelectTab: (WorkspaceTab) -> Unit,
    onToggleFocusMode: () -> Unit,
    onSelectAccentMood: (AccentMood) -> Unit,
    onCycleAccentMood: () -> Unit,
    onSetPrimaryBatch: (String) -> Unit,
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
    onSelectBatch: (String) -> Unit,
    onToggleExpandSubject: (String) -> Unit,
    onToggleChapterCompleted: (batchId: String, subjectId: String, chapterId: String) -> Unit,
    onOpenCreateBatchBuilder: () -> Unit,
    onCloseCreateBatchBuilder: () -> Unit,
    onCreateCustomBatch: (
        name: String,
        goalTag: String,
        accentMood: AccentMood,
        selectedSubjects: List<PresetSubjectTemplate>,
        customSubjectName: String,
    ) -> Unit,
    onSelectPwVaultSubject: (String?) -> Unit,
    onSelectPwVaultChapter: (String?) -> Unit,
    onSelectPwVaultLecture: (String?) -> Unit,
    onSelectPwVaultSubView: (VaultCategorySubView) -> Unit,
    onToggleExpandedClassNote: (String) -> Unit,
    onToggleExpandedFlashcardChapter: (String) -> Unit,
    onSelectPreviewFlashcard: (String) -> Unit,
    onFlipPreviewFlashcard: () -> Unit,
    onSelectPwVaultRevisionSubject: (String) -> Unit,
    onOpenVaultCommandPalette: (VaultPaletteFocus) -> Unit,
    onCloseVaultCommandPalette: () -> Unit,
    onUpdateVaultPaletteQuery: (String) -> Unit,
    onSelectVaultPaletteSubjectFilter: (String?) -> Unit,
    onSelectVaultPaletteCategoryFilter: (VaultCategorySubView?) -> Unit,
    onClearVaultPaletteFilters: () -> Unit,
    onJumpToVaultLectureFromPalette: (VaultLectureNode, VaultCategorySubView) -> Unit,
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
                onCyclePrimaryBatch = onCyclePrimaryBatch,
                onOpenDailyPage = onOpenDailyPage,
                onCloseDailyPage = onCloseDailyPage,
                onCompleteTodayRevision = onCompleteTodayRevision,
                onFlipDailyFlashcard = onFlipDailyFlashcard,
                onStepDailyFlashcard = onStepDailyFlashcard,
                onCompleteTodayFlashcards = onCompleteTodayFlashcards,
                onToggleTodayRevisionCompleted = onToggleTodayRevisionCompleted,
                onToggleTodayFlashcardsCompleted = onToggleTodayFlashcardsCompleted,
                onToggleStudyGateTestOverride = onToggleStudyGateTestOverride,
                onStartTodaysNewStudy = onStartTodaysNewStudy,
            )

            WorkspaceTab.BATCHES -> BatchesScreen(
                batchesState = uiState.batchesState,
                primaryBatchId = uiState.primaryBatchId,
                activeThemeMood = uiState.accentMood,
                isWideLayout = isWideLayout,
                onSelectBatch = onSelectBatch,
                onSetPrimaryBatch = onSetPrimaryBatch,
                onToggleExpandSubject = onToggleExpandSubject,
                onToggleChapterCompleted = onToggleChapterCompleted,
                onOpenBuilder = onOpenCreateBatchBuilder,
                onCloseBuilder = onCloseCreateBatchBuilder,
                onCreateBatch = onCreateCustomBatch,
            )

            WorkspaceTab.VAULT -> PwVaultScreen(
                state = uiState.pwVaultState,
                batches = uiState.batchesState.batches,
                isWideLayout = isWideLayout,
                onCycleBatch = onCyclePrimaryBatch,
                onSelectSubject = onSelectPwVaultSubject,
                onSelectChapter = onSelectPwVaultChapter,
                onSelectLecture = onSelectPwVaultLecture,
                onSelectSubView = onSelectPwVaultSubView,
                onToggleExpandClassNote = onToggleExpandedClassNote,
                onToggleExpandFlashcardChapter = onToggleExpandedFlashcardChapter,
                onSelectPreviewFlashcard = onSelectPreviewFlashcard,
                onFlipPreviewFlashcard = onFlipPreviewFlashcard,
                onSelectRevisionSubject = onSelectPwVaultRevisionSubject,
                onOpenCommandPalette = onOpenVaultCommandPalette,
                onCloseCommandPalette = onCloseVaultCommandPalette,
                onUpdatePaletteQuery = onUpdateVaultPaletteQuery,
                onSelectPaletteSubjectFilter = onSelectVaultPaletteSubjectFilter,
                onSelectPaletteCategoryFilter = onSelectVaultPaletteCategoryFilter,
                onClearPaletteFilters = onClearVaultPaletteFilters,
                onJumpToLectureFromPalette = onJumpToVaultLectureFromPalette,
            )

            WorkspaceTab.AI_TUTOR,
            WorkspaceTab.DAILY_PLANNER,
            WorkspaceTab.TIMER_GROUP,
            -> PhasePlaceholderScreen(
                tab = tab,
                activeMood = uiState.accentMood,
                isWideLayout = isWideLayout,
                onReturnHome = { onSelectTab(WorkspaceTab.HOME) },
                onCycleAccentMood = onCycleAccentMood,
            )
        }
    }
}
