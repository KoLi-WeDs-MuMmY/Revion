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
import com.example.model.FlashcardHubMode
import com.example.model.FlashcardRating
import com.example.model.InspectSelection
import com.example.model.MistakeCategory
import com.example.model.PermissionRequestAction
import com.example.model.PlannerBlockCategory
import com.example.model.PresetSubjectTemplate
import com.example.model.QuizMode
import com.example.model.RevisionQuizTabMode
import com.example.model.SpacedIntervalBucket
import com.example.model.TutorPlannerMode
import com.example.model.WorkspaceTab
import com.example.model.WorkspaceUiState
import com.example.ui.batches.BatchesScreen
import com.example.ui.components.AmbientMineralBackdrop
import com.example.ui.flashcards.FlashcardsHubScreen
import com.example.ui.home.HomeCommandCenter
import com.example.ui.revision.RevisionQuizHubScreen
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.tutor.TutorPlannerHubScreen
import com.example.ui.vault.VaultScreen

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
    // Phase 1.2 Batches callbacks
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
    // Phase 1.2 Vault callbacks
    onSelectVaultBatchFilter: (String?) -> Unit,
    onSelectVaultSubjectFilter: (String?) -> Unit,
    onSelectVaultChapterFilter: (String?) -> Unit,
    onUpdateVaultSearch: (String) -> Unit,
    onClearVaultFilters: () -> Unit,
    onPreviewVaultMaterial: (String) -> Unit,
    onCloseVaultPreview: () -> Unit,
    onOpenVaultPdfReader: (String) -> Unit,
    onCloseVaultPdfReader: () -> Unit,
    onToggleReaderInfo: () -> Unit,
    onStepReaderPage: (Int) -> Unit,
    onJumpReaderToPage: (Int) -> Unit,
    // Phase 1.3 Revision & Quiz callbacks
    onSelectRevisionQuizMode: (RevisionQuizTabMode) -> Unit,
    onSelectRevisionSubject: (String) -> Unit,
    onToggleCompoundRevisionMix: () -> Unit,
    onToggleDiagramLabels: () -> Unit,
    onSelectQuizMode: (QuizMode) -> Unit,
    onSelectQuizSubject: (String) -> Unit,
    onSelectQuizOption: (questionId: String, optionIndex: Int, elapsedSeconds: Int) -> Unit,
    onAdvanceOrSubmitQuiz: (elapsedSeconds: Int) -> Unit,
    onRetryMissedQuestions: () -> Unit,
    onRetakeFullQuiz: () -> Unit,
    onJumpFromQuizToRevision: (preferredSubjectId: String?) -> Unit,
    // Phase 1.4 Spaced Flashcards & Mistake Bank callbacks
    onSelectFlashcardHubMode: (FlashcardHubMode) -> Unit,
    onSelectSpacedBucket: (SpacedIntervalBucket) -> Unit,
    onSelectFlashcardSubjectFilter: (String?) -> Unit,
    onToggleFlashcardFlip: () -> Unit,
    onRateFlashcard: (cardId: String, rating: FlashcardRating) -> Unit,
    onResetFlashcardSession: () -> Unit,
    onSelectMistakeCategoryFilter: (MistakeCategory?) -> Unit,
    onSelectMistakeSubjectFilter: (String?) -> Unit,
    onSelectMistakeRecord: (String?) -> Unit,
    onSendMistakeToWeekendDeck: (String) -> Unit,
    // Phase 1.5 Chapter RAG AI Tutor & Adaptive Wake-to-Sleep Planner callbacks
    onSelectTutorPlannerMode: (TutorPlannerMode) -> Unit,
    onToggleCoPlanSplit: () -> Unit,
    onSelectRagChapter: (String) -> Unit,
    onSendTutorDoubt: (String) -> Unit,
    onOpenScheduleSettings: () -> Unit,
    onCloseScheduleSettings: () -> Unit,
    onToggleAdaptiveSync: (Boolean) -> Unit,
    onStepManualWake: (Int) -> Unit,
    onStepManualSleep: (Int) -> Unit,
    onTogglePlannerBlockCompleted: (String) -> Unit,
    onRequestAddPlannerBlock: () -> Unit,
    onRequestEditPlannerBlock: (String) -> Unit,
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
    onNavigateBackToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    val canHandleBack = uiState.activeTab != WorkspaceTab.HOME ||
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
                        onToggleFocusMode = onToggleFocusMode,
                        onSelectAccentMood = onSelectAccentMood,
                        onCycleAccentMood = onCycleAccentMood,
                        onOpenInspect = onOpenInspect,
                        onCloseInspect = onCloseInspect,
                        onJumpFromInspect = onJumpFromInspect,
                        onSelectBatch = onSelectBatch,
                        onToggleExpandSubject = onToggleExpandSubject,
                        onToggleChapterCompleted = onToggleChapterCompleted,
                        onOpenCreateBatchBuilder = onOpenCreateBatchBuilder,
                        onCloseCreateBatchBuilder = onCloseCreateBatchBuilder,
                        onCreateCustomBatch = onCreateCustomBatch,
                        onSelectVaultBatchFilter = onSelectVaultBatchFilter,
                        onSelectVaultSubjectFilter = onSelectVaultSubjectFilter,
                        onSelectVaultChapterFilter = onSelectVaultChapterFilter,
                        onUpdateVaultSearch = onUpdateVaultSearch,
                        onClearVaultFilters = onClearVaultFilters,
                        onPreviewVaultMaterial = onPreviewVaultMaterial,
                        onCloseVaultPreview = onCloseVaultPreview,
                        onOpenVaultPdfReader = onOpenVaultPdfReader,
                        onCloseVaultPdfReader = onCloseVaultPdfReader,
                        onToggleReaderInfo = onToggleReaderInfo,
                        onStepReaderPage = onStepReaderPage,
                        onJumpReaderToPage = onJumpReaderToPage,
                        onSelectRevisionQuizMode = onSelectRevisionQuizMode,
                        onSelectRevisionSubject = onSelectRevisionSubject,
                        onToggleCompoundRevisionMix = onToggleCompoundRevisionMix,
                        onToggleDiagramLabels = onToggleDiagramLabels,
                        onSelectQuizMode = onSelectQuizMode,
                        onSelectQuizSubject = onSelectQuizSubject,
                        onSelectQuizOption = onSelectQuizOption,
                        onAdvanceOrSubmitQuiz = onAdvanceOrSubmitQuiz,
                        onRetryMissedQuestions = onRetryMissedQuestions,
                        onRetakeFullQuiz = onRetakeFullQuiz,
                        onJumpFromQuizToRevision = onJumpFromQuizToRevision,
                        onSelectFlashcardHubMode = onSelectFlashcardHubMode,
                        onSelectSpacedBucket = onSelectSpacedBucket,
                        onSelectFlashcardSubjectFilter = onSelectFlashcardSubjectFilter,
                        onToggleFlashcardFlip = onToggleFlashcardFlip,
                        onRateFlashcard = onRateFlashcard,
                        onResetFlashcardSession = onResetFlashcardSession,
                        onSelectMistakeCategoryFilter = onSelectMistakeCategoryFilter,
                        onSelectMistakeSubjectFilter = onSelectMistakeSubjectFilter,
                        onSelectMistakeRecord = onSelectMistakeRecord,
                        onSendMistakeToWeekendDeck = onSendMistakeToWeekendDeck,
                        onSelectTutorPlannerMode = onSelectTutorPlannerMode,
                        onToggleCoPlanSplit = onToggleCoPlanSplit,
                        onSelectRagChapter = onSelectRagChapter,
                        onSendTutorDoubt = onSendTutorDoubt,
                        onOpenScheduleSettings = onOpenScheduleSettings,
                        onCloseScheduleSettings = onCloseScheduleSettings,
                        onToggleAdaptiveSync = onToggleAdaptiveSync,
                        onStepManualWake = onStepManualWake,
                        onStepManualSleep = onStepManualSleep,
                        onTogglePlannerBlockCompleted = onTogglePlannerBlockCompleted,
                        onRequestAddPlannerBlock = onRequestAddPlannerBlock,
                        onRequestEditPlannerBlock = onRequestEditPlannerBlock,
                        onClosePermissionRequest = onClosePermissionRequest,
                        onUpdatePermissionDraft = onUpdatePermissionDraft,
                        onSubmitPermissionToAi = onSubmitPermissionToAi,
                        onApplyApprovedPermission = onApplyApprovedPermission,
                        onAcceptCounterProposal = onAcceptCounterProposal,
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
                        onToggleFocusMode = onToggleFocusMode,
                        onSelectAccentMood = onSelectAccentMood,
                        onCycleAccentMood = onCycleAccentMood,
                        onOpenInspect = onOpenInspect,
                        onCloseInspect = onCloseInspect,
                        onJumpFromInspect = onJumpFromInspect,
                        onSelectBatch = onSelectBatch,
                        onToggleExpandSubject = onToggleExpandSubject,
                        onToggleChapterCompleted = onToggleChapterCompleted,
                        onOpenCreateBatchBuilder = onOpenCreateBatchBuilder,
                        onCloseCreateBatchBuilder = onCloseCreateBatchBuilder,
                        onCreateCustomBatch = onCreateCustomBatch,
                        onSelectVaultBatchFilter = onSelectVaultBatchFilter,
                        onSelectVaultSubjectFilter = onSelectVaultSubjectFilter,
                        onSelectVaultChapterFilter = onSelectVaultChapterFilter,
                        onUpdateVaultSearch = onUpdateVaultSearch,
                        onClearVaultFilters = onClearVaultFilters,
                        onPreviewVaultMaterial = onPreviewVaultMaterial,
                        onCloseVaultPreview = onCloseVaultPreview,
                        onOpenVaultPdfReader = onOpenVaultPdfReader,
                        onCloseVaultPdfReader = onCloseVaultPdfReader,
                        onToggleReaderInfo = onToggleReaderInfo,
                        onStepReaderPage = onStepReaderPage,
                        onJumpReaderToPage = onJumpReaderToPage,
                        onSelectRevisionQuizMode = onSelectRevisionQuizMode,
                        onSelectRevisionSubject = onSelectRevisionSubject,
                        onToggleCompoundRevisionMix = onToggleCompoundRevisionMix,
                        onToggleDiagramLabels = onToggleDiagramLabels,
                        onSelectQuizMode = onSelectQuizMode,
                        onSelectQuizSubject = onSelectQuizSubject,
                        onSelectQuizOption = onSelectQuizOption,
                        onAdvanceOrSubmitQuiz = onAdvanceOrSubmitQuiz,
                        onRetryMissedQuestions = onRetryMissedQuestions,
                        onRetakeFullQuiz = onRetakeFullQuiz,
                        onJumpFromQuizToRevision = onJumpFromQuizToRevision,
                        onSelectFlashcardHubMode = onSelectFlashcardHubMode,
                        onSelectSpacedBucket = onSelectSpacedBucket,
                        onSelectFlashcardSubjectFilter = onSelectFlashcardSubjectFilter,
                        onToggleFlashcardFlip = onToggleFlashcardFlip,
                        onRateFlashcard = onRateFlashcard,
                        onResetFlashcardSession = onResetFlashcardSession,
                        onSelectMistakeCategoryFilter = onSelectMistakeCategoryFilter,
                        onSelectMistakeSubjectFilter = onSelectMistakeSubjectFilter,
                        onSelectMistakeRecord = onSelectMistakeRecord,
                        onSendMistakeToWeekendDeck = onSendMistakeToWeekendDeck,
                        onSelectTutorPlannerMode = onSelectTutorPlannerMode,
                        onToggleCoPlanSplit = onToggleCoPlanSplit,
                        onSelectRagChapter = onSelectRagChapter,
                        onSendTutorDoubt = onSendTutorDoubt,
                        onOpenScheduleSettings = onOpenScheduleSettings,
                        onCloseScheduleSettings = onCloseScheduleSettings,
                        onToggleAdaptiveSync = onToggleAdaptiveSync,
                        onStepManualWake = onStepManualWake,
                        onStepManualSleep = onStepManualSleep,
                        onTogglePlannerBlockCompleted = onTogglePlannerBlockCompleted,
                        onRequestAddPlannerBlock = onRequestAddPlannerBlock,
                        onRequestEditPlannerBlock = onRequestEditPlannerBlock,
                        onClosePermissionRequest = onClosePermissionRequest,
                        onUpdatePermissionDraft = onUpdatePermissionDraft,
                        onSubmitPermissionToAi = onSubmitPermissionToAi,
                        onApplyApprovedPermission = onApplyApprovedPermission,
                        onAcceptCounterProposal = onAcceptCounterProposal,
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
    onSelectVaultBatchFilter: (String?) -> Unit,
    onSelectVaultSubjectFilter: (String?) -> Unit,
    onSelectVaultChapterFilter: (String?) -> Unit,
    onUpdateVaultSearch: (String) -> Unit,
    onClearVaultFilters: () -> Unit,
    onPreviewVaultMaterial: (String) -> Unit,
    onCloseVaultPreview: () -> Unit,
    onOpenVaultPdfReader: (String) -> Unit,
    onCloseVaultPdfReader: () -> Unit,
    onToggleReaderInfo: () -> Unit,
    onStepReaderPage: (Int) -> Unit,
    onJumpReaderToPage: (Int) -> Unit,
    onSelectRevisionQuizMode: (RevisionQuizTabMode) -> Unit,
    onSelectRevisionSubject: (String) -> Unit,
    onToggleCompoundRevisionMix: () -> Unit,
    onToggleDiagramLabels: () -> Unit,
    onSelectQuizMode: (QuizMode) -> Unit,
    onSelectQuizSubject: (String) -> Unit,
    onSelectQuizOption: (questionId: String, optionIndex: Int, elapsedSeconds: Int) -> Unit,
    onAdvanceOrSubmitQuiz: (elapsedSeconds: Int) -> Unit,
    onRetryMissedQuestions: () -> Unit,
    onRetakeFullQuiz: () -> Unit,
    onJumpFromQuizToRevision: (preferredSubjectId: String?) -> Unit,
    onSelectFlashcardHubMode: (FlashcardHubMode) -> Unit,
    onSelectSpacedBucket: (SpacedIntervalBucket) -> Unit,
    onSelectFlashcardSubjectFilter: (String?) -> Unit,
    onToggleFlashcardFlip: () -> Unit,
    onRateFlashcard: (cardId: String, rating: FlashcardRating) -> Unit,
    onResetFlashcardSession: () -> Unit,
    onSelectMistakeCategoryFilter: (MistakeCategory?) -> Unit,
    onSelectMistakeSubjectFilter: (String?) -> Unit,
    onSelectMistakeRecord: (String?) -> Unit,
    onSendMistakeToWeekendDeck: (String) -> Unit,
    onSelectTutorPlannerMode: (TutorPlannerMode) -> Unit,
    onToggleCoPlanSplit: () -> Unit,
    onSelectRagChapter: (String) -> Unit,
    onSendTutorDoubt: (String) -> Unit,
    onOpenScheduleSettings: () -> Unit,
    onCloseScheduleSettings: () -> Unit,
    onToggleAdaptiveSync: (Boolean) -> Unit,
    onStepManualWake: (Int) -> Unit,
    onStepManualSleep: (Int) -> Unit,
    onTogglePlannerBlockCompleted: (String) -> Unit,
    onRequestAddPlannerBlock: () -> Unit,
    onRequestEditPlannerBlock: (String) -> Unit,
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

            WorkspaceTab.BATCHES -> BatchesScreen(
                batchesState = uiState.batchesState,
                activeThemeMood = uiState.accentMood,
                isWideLayout = isWideLayout,
                onSelectBatch = onSelectBatch,
                onToggleExpandSubject = onToggleExpandSubject,
                onToggleChapterCompleted = onToggleChapterCompleted,
                onOpenBuilder = onOpenCreateBatchBuilder,
                onCloseBuilder = onCloseCreateBatchBuilder,
                onCreateBatch = onCreateCustomBatch,
            )

            WorkspaceTab.VAULT -> VaultScreen(
                vaultState = uiState.vaultState,
                batches = uiState.batchesState.batches,
                activeThemeMood = uiState.accentMood,
                isWideLayout = isWideLayout,
                onSelectBatchFilter = onSelectVaultBatchFilter,
                onSelectSubjectFilter = onSelectVaultSubjectFilter,
                onSelectChapterFilter = onSelectVaultChapterFilter,
                onSearchQueryChange = onUpdateVaultSearch,
                onClearFilters = onClearVaultFilters,
                onPreviewMaterial = onPreviewVaultMaterial,
                onClosePreview = onCloseVaultPreview,
                onOpenPdfReader = onOpenVaultPdfReader,
                onClosePdfReader = onCloseVaultPdfReader,
                onToggleReaderInfo = onToggleReaderInfo,
                onStepReaderPage = onStepReaderPage,
                onJumpReaderToPage = onJumpReaderToPage,
            )

            WorkspaceTab.REVISION_QUIZ -> RevisionQuizHubScreen(
                state = uiState.revisionQuizState,
                isWideLayout = isWideLayout,
                onSelectMode = onSelectRevisionQuizMode,
                onSelectRevisionSubject = onSelectRevisionSubject,
                onToggleCompoundMix = onToggleCompoundRevisionMix,
                onToggleDiagramLabels = onToggleDiagramLabels,
                onSelectQuizMode = onSelectQuizMode,
                onSelectQuizSubject = onSelectQuizSubject,
                onSelectQuizOption = onSelectQuizOption,
                onAdvanceOrSubmitQuiz = onAdvanceOrSubmitQuiz,
                onRetryMissedQuestions = onRetryMissedQuestions,
                onRetakeFullQuiz = onRetakeFullQuiz,
                onJumpFromQuizToRevision = onJumpFromQuizToRevision,
            )

            WorkspaceTab.FLASHCARDS -> FlashcardsHubScreen(
                state = uiState.flashcardsState,
                isWideLayout = isWideLayout,
                onSelectMode = onSelectFlashcardHubMode,
                onSelectBucket = onSelectSpacedBucket,
                onSelectCardSubjectFilter = onSelectFlashcardSubjectFilter,
                onToggleCardFlip = onToggleFlashcardFlip,
                onRateCard = onRateFlashcard,
                onResetCardSession = onResetFlashcardSession,
                onSelectMistakeCategory = onSelectMistakeCategoryFilter,
                onSelectMistakeSubject = onSelectMistakeSubjectFilter,
                onSelectMistakeRecord = onSelectMistakeRecord,
                onSendMistakeToWeekendDeck = onSendMistakeToWeekendDeck,
            )

            WorkspaceTab.TUTOR_PLANNER -> TutorPlannerHubScreen(
                state = uiState.tutorPlannerState,
                isWideLayout = isWideLayout,
                onSelectMode = onSelectTutorPlannerMode,
                onToggleCoPlanSplit = onToggleCoPlanSplit,
                onSelectRagChapter = onSelectRagChapter,
                onSendTutorDoubt = onSendTutorDoubt,
                onOpenScheduleSettings = onOpenScheduleSettings,
                onCloseScheduleSettings = onCloseScheduleSettings,
                onToggleAdaptiveSync = onToggleAdaptiveSync,
                onStepManualWake = onStepManualWake,
                onStepManualSleep = onStepManualSleep,
                onToggleBlockCompleted = onTogglePlannerBlockCompleted,
                onRequestAddBlock = onRequestAddPlannerBlock,
                onRequestEditBlock = onRequestEditPlannerBlock,
                onClosePermissionRequest = onClosePermissionRequest,
                onUpdatePermissionDraft = onUpdatePermissionDraft,
                onSubmitPermissionToAi = onSubmitPermissionToAi,
                onApplyApprovedPermission = onApplyApprovedPermission,
                onAcceptCounterProposal = onAcceptCounterProposal,
            )
        }
    }
}
