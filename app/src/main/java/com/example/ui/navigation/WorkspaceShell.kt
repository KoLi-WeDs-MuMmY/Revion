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
import com.example.model.BatchCrudDialog
import com.example.model.ChapterDiagramRef
import com.example.model.DailyPageRoute
import com.example.model.DiagramAttachment
import com.example.model.PresetSubjectTemplate
import com.example.model.TestArenaMode
import com.example.model.VaultCategorySubView
import com.example.model.VaultLectureNode
import com.example.model.VaultPaletteFocus
import com.example.model.WorkspaceTab
import com.example.model.WorkspaceUiState
import com.example.ui.batches.BatchesScreen
import com.example.ui.components.AmbientMineralBackdrop
import com.example.ui.home.HomeCommandCenter
import com.example.ui.placeholders.PhasePlaceholderScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.test.PwTestArenaScreen
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AppThemeId
import com.example.ui.theme.GhickinTokens
import com.example.ui.tutor.GrokAiTutorScreen
import com.example.ui.vault.PwVaultScreen

@Composable
fun WorkspaceShell(
    uiState: WorkspaceUiState,
    onSelectTab: (WorkspaceTab) -> Unit,
    onSelectAppTheme: (AppThemeId) -> Unit,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
    onToggleFocusMode: () -> Unit,
    onSelectAccentMood: (AccentMood) -> Unit,
    onCycleAccentMood: () -> Unit,
    // Home Command Center & Dedicated Daily Pages callbacks
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
    // Batches + Full CRUD callbacks
    onSelectBatch: (String) -> Unit,
    onToggleExpandSubject: (String) -> Unit,
    onToggleExpandChapterInBatches: (String) -> Unit,
    onToggleChapterCompleted: (batchId: String, subjectId: String, chapterId: String) -> Unit,
    onToggleLectureCompleted: (batchId: String, subjectId: String, chapterId: String, lectureId: String) -> Unit,
    onOpenBatchCrudDialog: (BatchCrudDialog) -> Unit,
    onCloseBatchCrudDialog: () -> Unit,
    onEditBatch: (batchId: String, name: String, goalTag: String, targetLabel: String, mood: AccentMood) -> Unit,
    onDeleteBatch: (batchId: String) -> Unit,
    onAddSubjectToBatch: (batchId: String, name: String, mood: AccentMood) -> Unit,
    onEditSubject: (batchId: String, subjectId: String, name: String, mood: AccentMood) -> Unit,
    onDeleteSubject: (batchId: String, subjectId: String) -> Unit,
    onAddChapterToSubject: (batchId: String, subjectId: String, title: String) -> Unit,
    onEditChapter: (batchId: String, subjectId: String, chapterId: String, title: String) -> Unit,
    onDeleteChapter: (batchId: String, subjectId: String, chapterId: String) -> Unit,
    onAddLectureToChapter: (batchId: String, subjectId: String, chapterId: String, title: String, duration: String) -> Unit,
    onEditLecture: (batchId: String, subjectId: String, chapterId: String, lectureId: String, title: String, duration: String) -> Unit,
    onDeleteLecture: (batchId: String, subjectId: String, chapterId: String, lectureId: String) -> Unit,
    onOpenCreateBatchBuilder: () -> Unit,
    onCloseCreateBatchBuilder: () -> Unit,
    onCreateCustomBatch: (
        name: String,
        goalTag: String,
        accentMood: AccentMood,
        selectedSubjects: List<PresetSubjectTemplate>,
        customSubjectName: String,
    ) -> Unit,
    // PW-Style Vault callbacks
    onSelectPwVaultSubject: (String?) -> Unit,
    onSelectPwVaultChapter: (String?) -> Unit,
    onSelectPwVaultLecture: (String?) -> Unit,
    onSelectPwVaultSubView: (VaultCategorySubView) -> Unit,
    onToggleExpandedClassNote: (String) -> Unit,
    onSelectPreviewFlashcard: (String) -> Unit,
    onFlipPreviewFlashcard: () -> Unit,
    onOpenVaultCommandPalette: (VaultPaletteFocus) -> Unit,
    onCloseVaultCommandPalette: () -> Unit,
    onUpdateVaultPaletteQuery: (String) -> Unit,
    onSelectVaultPaletteSubjectFilter: (String?) -> Unit,
    onSelectVaultPaletteCategoryFilter: (VaultCategorySubView?) -> Unit,
    onClearVaultPaletteFilters: () -> Unit,
    onJumpToVaultLectureFromPalette: (VaultLectureNode, VaultCategorySubView) -> Unit,
    // Phase 1.6.2: Grok AI Tutor & Image-Code Web Viewer callbacks
    onToggleTutorSidebar: () -> Unit,
    onCloseTutorSidebar: () -> Unit,
    onStartNewTutorChat: () -> Unit,
    onSelectTutorSession: (String) -> Unit,
    onOpenTutorChapterPicker: () -> Unit,
    onCloseTutorChapterPicker: () -> Unit,
    onConfirmTutorLinkedChapters: (batchId: String, chapterIds: List<String>) -> Unit,
    onOpenTutorRagIndexDialog: () -> Unit,
    onCloseTutorRagIndexDialog: () -> Unit,
    onUpdateTutorComposerText: (String) -> Unit,
    onSendGrokTutorPrompt: (String) -> Unit,
    onOpenDiagramModal: (DiagramAttachment) -> Unit,
    onOpenDiagramSplitScreen: (DiagramAttachment) -> Unit,
    onPreviewCatalogDiagram: (ChapterDiagramRef) -> Unit,
    onToggleDiagramSplitScreen: () -> Unit,
    onCloseDiagramViewer: () -> Unit,
    onZoomDiagram: (Int) -> Unit,
    onResetDiagramZoom: () -> Unit,
    // Phase 1.6.2: PW-Style Quizzes Arena callbacks
    onCycleQuizArenaBatch: () -> Unit,
    onSelectQuizArenaSubject: (String?) -> Unit,
    onRequestStartChapterQuiz: (subjectId: String, chapterId: String) -> Unit,
    onDismissQuizModeGate: () -> Unit,
    onConfirmStartQuizMode: (TestArenaMode) -> Unit,
    onSelectQuizArenaOption: (Int) -> Unit,
    onClearQuizArenaOption: () -> Unit,
    onToggleQuizMarkForReview: () -> Unit,
    onJumpToQuizQuestion: (Int) -> Unit,
    onToggleQuizPalette: () -> Unit,
    onSubmitQuizArenaSession: () -> Unit,
    onExitQuizArenaSession: () -> Unit,
    onRetryMissedQuizQuestions: () -> Unit,
    onRetakeFullQuiz: () -> Unit,
    onStudyWeakChapterInTutor: (batchId: String, chapterId: String) -> Unit,
    onNavigateBackToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    val canHandleBack = uiState.isSettingsOpen ||
        uiState.activeTab != WorkspaceTab.HOME ||
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
        // Strictly orientation-driven: Portrait (height >= width, including portrait tablets)
        // ALWAYS renders the clean 9:16 portrait layout + bottom navigation bar.
        // Landscape (width > height) renders the 16:9 side Navigation Rail + multi-column workspace.
        val isWideLayout = maxWidth > maxHeight

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
                    isSettingsOpen = uiState.isSettingsOpen,
                    onSelectTab = onSelectTab,
                    onOpenSettings = onOpenSettings,
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
                        onSelectAppTheme = onSelectAppTheme,
                        onOpenSettings = onOpenSettings,
                        onCloseSettings = onCloseSettings,
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
                        onToggleExpandChapterInBatches = onToggleExpandChapterInBatches,
                        onToggleChapterCompleted = onToggleChapterCompleted,
                        onToggleLectureCompleted = onToggleLectureCompleted,
                        onOpenBatchCrudDialog = onOpenBatchCrudDialog,
                        onCloseBatchCrudDialog = onCloseBatchCrudDialog,
                        onEditBatch = onEditBatch,
                        onDeleteBatch = onDeleteBatch,
                        onAddSubjectToBatch = onAddSubjectToBatch,
                        onEditSubject = onEditSubject,
                        onDeleteSubject = onDeleteSubject,
                        onAddChapterToSubject = onAddChapterToSubject,
                        onEditChapter = onEditChapter,
                        onDeleteChapter = onDeleteChapter,
                        onAddLectureToChapter = onAddLectureToChapter,
                        onEditLecture = onEditLecture,
                        onDeleteLecture = onDeleteLecture,
                        onOpenCreateBatchBuilder = onOpenCreateBatchBuilder,
                        onCloseCreateBatchBuilder = onCloseCreateBatchBuilder,
                        onCreateCustomBatch = onCreateCustomBatch,
                        onSelectPwVaultSubject = onSelectPwVaultSubject,
                        onSelectPwVaultChapter = onSelectPwVaultChapter,
                        onSelectPwVaultLecture = onSelectPwVaultLecture,
                        onSelectPwVaultSubView = onSelectPwVaultSubView,
                        onToggleExpandedClassNote = onToggleExpandedClassNote,
                        onSelectPreviewFlashcard = onSelectPreviewFlashcard,
                        onFlipPreviewFlashcard = onFlipPreviewFlashcard,
                        onOpenVaultCommandPalette = onOpenVaultCommandPalette,
                        onCloseVaultCommandPalette = onCloseVaultCommandPalette,
                        onUpdateVaultPaletteQuery = onUpdateVaultPaletteQuery,
                        onSelectVaultPaletteSubjectFilter = onSelectVaultPaletteSubjectFilter,
                        onSelectVaultPaletteCategoryFilter = onSelectVaultPaletteCategoryFilter,
                        onClearVaultPaletteFilters = onClearVaultPaletteFilters,
                        onJumpToVaultLectureFromPalette = onJumpToVaultLectureFromPalette,
                        onToggleTutorSidebar = onToggleTutorSidebar,
                        onCloseTutorSidebar = onCloseTutorSidebar,
                        onStartNewTutorChat = onStartNewTutorChat,
                        onSelectTutorSession = onSelectTutorSession,
                        onOpenTutorChapterPicker = onOpenTutorChapterPicker,
                        onCloseTutorChapterPicker = onCloseTutorChapterPicker,
                        onConfirmTutorLinkedChapters = onConfirmTutorLinkedChapters,
                        onOpenTutorRagIndexDialog = onOpenTutorRagIndexDialog,
                        onCloseTutorRagIndexDialog = onCloseTutorRagIndexDialog,
                        onUpdateTutorComposerText = onUpdateTutorComposerText,
                        onSendGrokTutorPrompt = onSendGrokTutorPrompt,
                        onOpenDiagramModal = onOpenDiagramModal,
                        onOpenDiagramSplitScreen = onOpenDiagramSplitScreen,
                        onPreviewCatalogDiagram = onPreviewCatalogDiagram,
                        onToggleDiagramSplitScreen = onToggleDiagramSplitScreen,
                        onCloseDiagramViewer = onCloseDiagramViewer,
                        onZoomDiagram = onZoomDiagram,
                        onResetDiagramZoom = onResetDiagramZoom,
                        onCycleQuizArenaBatch = onCycleQuizArenaBatch,
                        onSelectQuizArenaSubject = onSelectQuizArenaSubject,
                        onRequestStartChapterQuiz = onRequestStartChapterQuiz,
                        onDismissQuizModeGate = onDismissQuizModeGate,
                        onConfirmStartQuizMode = onConfirmStartQuizMode,
                        onSelectQuizArenaOption = onSelectQuizArenaOption,
                        onClearQuizArenaOption = onClearQuizArenaOption,
                        onToggleQuizMarkForReview = onToggleQuizMarkForReview,
                        onJumpToQuizQuestion = onJumpToQuizQuestion,
                        onToggleQuizPalette = onToggleQuizPalette,
                        onSubmitQuizArenaSession = onSubmitQuizArenaSession,
                        onExitQuizArenaSession = onExitQuizArenaSession,
                        onRetryMissedQuizQuestions = onRetryMissedQuizQuestions,
                        onRetakeFullQuiz = onRetakeFullQuiz,
                        onStudyWeakChapterInTutor = onStudyWeakChapterInTutor,
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
                        onSelectAppTheme = onSelectAppTheme,
                        onOpenSettings = onOpenSettings,
                        onCloseSettings = onCloseSettings,
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
                        onToggleExpandChapterInBatches = onToggleExpandChapterInBatches,
                        onToggleChapterCompleted = onToggleChapterCompleted,
                        onToggleLectureCompleted = onToggleLectureCompleted,
                        onOpenBatchCrudDialog = onOpenBatchCrudDialog,
                        onCloseBatchCrudDialog = onCloseBatchCrudDialog,
                        onEditBatch = onEditBatch,
                        onDeleteBatch = onDeleteBatch,
                        onAddSubjectToBatch = onAddSubjectToBatch,
                        onEditSubject = onEditSubject,
                        onDeleteSubject = onDeleteSubject,
                        onAddChapterToSubject = onAddChapterToSubject,
                        onEditChapter = onEditChapter,
                        onDeleteChapter = onDeleteChapter,
                        onAddLectureToChapter = onAddLectureToChapter,
                        onEditLecture = onEditLecture,
                        onDeleteLecture = onDeleteLecture,
                        onOpenCreateBatchBuilder = onOpenCreateBatchBuilder,
                        onCloseCreateBatchBuilder = onCloseCreateBatchBuilder,
                        onCreateCustomBatch = onCreateCustomBatch,
                        onSelectPwVaultSubject = onSelectPwVaultSubject,
                        onSelectPwVaultChapter = onSelectPwVaultChapter,
                        onSelectPwVaultLecture = onSelectPwVaultLecture,
                        onSelectPwVaultSubView = onSelectPwVaultSubView,
                        onToggleExpandedClassNote = onToggleExpandedClassNote,
                        onSelectPreviewFlashcard = onSelectPreviewFlashcard,
                        onFlipPreviewFlashcard = onFlipPreviewFlashcard,
                        onOpenVaultCommandPalette = onOpenVaultCommandPalette,
                        onCloseVaultCommandPalette = onCloseVaultCommandPalette,
                        onUpdateVaultPaletteQuery = onUpdateVaultPaletteQuery,
                        onSelectVaultPaletteSubjectFilter = onSelectVaultPaletteSubjectFilter,
                        onSelectVaultPaletteCategoryFilter = onSelectVaultPaletteCategoryFilter,
                        onClearVaultPaletteFilters = onClearVaultPaletteFilters,
                        onJumpToVaultLectureFromPalette = onJumpToVaultLectureFromPalette,
                        onToggleTutorSidebar = onToggleTutorSidebar,
                        onCloseTutorSidebar = onCloseTutorSidebar,
                        onStartNewTutorChat = onStartNewTutorChat,
                        onSelectTutorSession = onSelectTutorSession,
                        onOpenTutorChapterPicker = onOpenTutorChapterPicker,
                        onCloseTutorChapterPicker = onCloseTutorChapterPicker,
                        onConfirmTutorLinkedChapters = onConfirmTutorLinkedChapters,
                        onOpenTutorRagIndexDialog = onOpenTutorRagIndexDialog,
                        onCloseTutorRagIndexDialog = onCloseTutorRagIndexDialog,
                        onUpdateTutorComposerText = onUpdateTutorComposerText,
                        onSendGrokTutorPrompt = onSendGrokTutorPrompt,
                        onOpenDiagramModal = onOpenDiagramModal,
                        onOpenDiagramSplitScreen = onOpenDiagramSplitScreen,
                        onPreviewCatalogDiagram = onPreviewCatalogDiagram,
                        onToggleDiagramSplitScreen = onToggleDiagramSplitScreen,
                        onCloseDiagramViewer = onCloseDiagramViewer,
                        onZoomDiagram = onZoomDiagram,
                        onResetDiagramZoom = onResetDiagramZoom,
                        onCycleQuizArenaBatch = onCycleQuizArenaBatch,
                        onSelectQuizArenaSubject = onSelectQuizArenaSubject,
                        onRequestStartChapterQuiz = onRequestStartChapterQuiz,
                        onDismissQuizModeGate = onDismissQuizModeGate,
                        onConfirmStartQuizMode = onConfirmStartQuizMode,
                        onSelectQuizArenaOption = onSelectQuizArenaOption,
                        onClearQuizArenaOption = onClearQuizArenaOption,
                        onToggleQuizMarkForReview = onToggleQuizMarkForReview,
                        onJumpToQuizQuestion = onJumpToQuizQuestion,
                        onToggleQuizPalette = onToggleQuizPalette,
                        onSubmitQuizArenaSession = onSubmitQuizArenaSession,
                        onExitQuizArenaSession = onExitQuizArenaSession,
                        onRetryMissedQuizQuestions = onRetryMissedQuizQuestions,
                        onRetakeFullQuiz = onRetakeFullQuiz,
                        onStudyWeakChapterInTutor = onStudyWeakChapterInTutor,
                    )
                }

                GhickinBottomNavBar(
                    activeTab = uiState.activeTab,
                    isSettingsOpen = uiState.isSettingsOpen,
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
    onSelectAppTheme: (AppThemeId) -> Unit,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
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
    onToggleExpandChapterInBatches: (String) -> Unit,
    onToggleChapterCompleted: (batchId: String, subjectId: String, chapterId: String) -> Unit,
    onToggleLectureCompleted: (batchId: String, subjectId: String, chapterId: String, lectureId: String) -> Unit,
    onOpenBatchCrudDialog: (BatchCrudDialog) -> Unit,
    onCloseBatchCrudDialog: () -> Unit,
    onEditBatch: (batchId: String, name: String, goalTag: String, targetLabel: String, mood: AccentMood) -> Unit,
    onDeleteBatch: (batchId: String) -> Unit,
    onAddSubjectToBatch: (batchId: String, name: String, mood: AccentMood) -> Unit,
    onEditSubject: (batchId: String, subjectId: String, name: String, mood: AccentMood) -> Unit,
    onDeleteSubject: (batchId: String, subjectId: String) -> Unit,
    onAddChapterToSubject: (batchId: String, subjectId: String, title: String) -> Unit,
    onEditChapter: (batchId: String, subjectId: String, chapterId: String, title: String) -> Unit,
    onDeleteChapter: (batchId: String, subjectId: String, chapterId: String) -> Unit,
    onAddLectureToChapter: (batchId: String, subjectId: String, chapterId: String, title: String, duration: String) -> Unit,
    onEditLecture: (batchId: String, subjectId: String, chapterId: String, lectureId: String, title: String, duration: String) -> Unit,
    onDeleteLecture: (batchId: String, subjectId: String, chapterId: String, lectureId: String) -> Unit,
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
    onSelectPreviewFlashcard: (String) -> Unit,
    onFlipPreviewFlashcard: () -> Unit,
    onOpenVaultCommandPalette: (VaultPaletteFocus) -> Unit,
    onCloseVaultCommandPalette: () -> Unit,
    onUpdateVaultPaletteQuery: (String) -> Unit,
    onSelectVaultPaletteSubjectFilter: (String?) -> Unit,
    onSelectVaultPaletteCategoryFilter: (VaultCategorySubView?) -> Unit,
    onClearVaultPaletteFilters: () -> Unit,
    onJumpToVaultLectureFromPalette: (VaultLectureNode, VaultCategorySubView) -> Unit,
    onToggleTutorSidebar: () -> Unit,
    onCloseTutorSidebar: () -> Unit,
    onStartNewTutorChat: () -> Unit,
    onSelectTutorSession: (String) -> Unit,
    onOpenTutorChapterPicker: () -> Unit,
    onCloseTutorChapterPicker: () -> Unit,
    onConfirmTutorLinkedChapters: (batchId: String, chapterIds: List<String>) -> Unit,
    onOpenTutorRagIndexDialog: () -> Unit,
    onCloseTutorRagIndexDialog: () -> Unit,
    onUpdateTutorComposerText: (String) -> Unit,
    onSendGrokTutorPrompt: (String) -> Unit,
    onOpenDiagramModal: (DiagramAttachment) -> Unit,
    onOpenDiagramSplitScreen: (DiagramAttachment) -> Unit,
    onPreviewCatalogDiagram: (ChapterDiagramRef) -> Unit,
    onToggleDiagramSplitScreen: () -> Unit,
    onCloseDiagramViewer: () -> Unit,
    onZoomDiagram: (Int) -> Unit,
    onResetDiagramZoom: () -> Unit,
    onCycleQuizArenaBatch: () -> Unit,
    onSelectQuizArenaSubject: (String?) -> Unit,
    onRequestStartChapterQuiz: (subjectId: String, chapterId: String) -> Unit,
    onDismissQuizModeGate: () -> Unit,
    onConfirmStartQuizMode: (TestArenaMode) -> Unit,
    onSelectQuizArenaOption: (Int) -> Unit,
    onClearQuizArenaOption: () -> Unit,
    onToggleQuizMarkForReview: () -> Unit,
    onJumpToQuizQuestion: (Int) -> Unit,
    onToggleQuizPalette: () -> Unit,
    onSubmitQuizArenaSession: () -> Unit,
    onExitQuizArenaSession: () -> Unit,
    onRetryMissedQuizQuestions: () -> Unit,
    onRetakeFullQuiz: () -> Unit,
    onStudyWeakChapterInTutor: (batchId: String, chapterId: String) -> Unit,
) {
    val spatialSpring = spring<IntOffset>(
        dampingRatio = 0.88f,
        stiffness = 420f,
    )

    AnimatedContent(
        targetState = if (uiState.isSettingsOpen) null else uiState.activeTab,
        transitionSpec = {
            val initOrd = initialState?.ordinal ?: 99
            val targetOrd = targetState?.ordinal ?: 99
            val direction = if (targetOrd >= initOrd) 1 else -1
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
            null -> SettingsScreen(
                activeTheme = uiState.appTheme,
                activeMood = uiState.accentMood,
                focusMode = uiState.focusMode,
                isWideLayout = isWideLayout,
                onSelectTheme = onSelectAppTheme,
                onSelectAccentMood = onSelectAccentMood,
                onToggleFocusMode = onToggleFocusMode,
                onCloseSettings = onCloseSettings,
            )

            WorkspaceTab.HOME -> HomeCommandCenter(
                uiState = uiState,
                isWideLayout = isWideLayout,
                onOpenSettings = onOpenSettings,
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
                onQuickOpenAiTutor = { onSelectTab(WorkspaceTab.AI_TUTOR) },
                onQuickOpenQuizzes = { onSelectTab(WorkspaceTab.QUIZZES) },
            )

            WorkspaceTab.BATCHES -> BatchesScreen(
                batchesState = uiState.batchesState,
                primaryBatchId = uiState.primaryBatchId,
                activeThemeMood = uiState.accentMood,
                isWideLayout = isWideLayout,
                onOpenSettings = onOpenSettings,
                onSelectBatch = onSelectBatch,
                onSetPrimaryBatch = onSetPrimaryBatch,
                onToggleExpandSubject = onToggleExpandSubject,
                onToggleExpandChapter = onToggleExpandChapterInBatches,
                onToggleChapterCompleted = onToggleChapterCompleted,
                onToggleLectureCompleted = onToggleLectureCompleted,
                onOpenCrudDialog = onOpenBatchCrudDialog,
                onCloseCrudDialog = onCloseBatchCrudDialog,
                onEditBatch = onEditBatch,
                onDeleteBatch = onDeleteBatch,
                onAddSubject = onAddSubjectToBatch,
                onEditSubject = onEditSubject,
                onDeleteSubject = onDeleteSubject,
                onAddChapter = onAddChapterToSubject,
                onEditChapter = onEditChapter,
                onDeleteChapter = onDeleteChapter,
                onAddLecture = onAddLectureToChapter,
                onEditLecture = onEditLecture,
                onDeleteLecture = onDeleteLecture,
                onOpenBuilder = onOpenCreateBatchBuilder,
                onCloseBuilder = onCloseCreateBatchBuilder,
                onCreateBatch = onCreateCustomBatch,
            )

            WorkspaceTab.VAULT -> PwVaultScreen(
                state = uiState.pwVaultState,
                batches = uiState.batchesState.batches,
                isWideLayout = isWideLayout,
                onOpenSettings = onOpenSettings,
                onCycleBatch = onCyclePrimaryBatch,
                onSelectSubject = onSelectPwVaultSubject,
                onSelectChapter = onSelectPwVaultChapter,
                onSelectLecture = onSelectPwVaultLecture,
                onSelectSubView = onSelectPwVaultSubView,
                onToggleExpandClassNote = onToggleExpandedClassNote,
                onSelectPreviewFlashcard = onSelectPreviewFlashcard,
                onFlipPreviewFlashcard = onFlipPreviewFlashcard,
                onOpenCommandPalette = onOpenVaultCommandPalette,
                onCloseCommandPalette = onCloseVaultCommandPalette,
                onUpdatePaletteQuery = onUpdateVaultPaletteQuery,
                onSelectPaletteSubjectFilter = onSelectVaultPaletteSubjectFilter,
                onSelectPaletteCategoryFilter = onSelectVaultPaletteCategoryFilter,
                onClearPaletteFilters = onClearVaultPaletteFilters,
                onJumpToLectureFromPalette = onJumpToVaultLectureFromPalette,
            )

            WorkspaceTab.QUIZZES -> PwTestArenaScreen(
                state = uiState.pwQuizArenaState,
                batches = uiState.batchesState.batches,
                subjectsByBatch = uiState.pwVaultState.subjectsByBatch,
                isWideLayout = isWideLayout,
                onOpenSettings = onOpenSettings,
                onCycleBatch = onCycleQuizArenaBatch,
                onSelectSubject = onSelectQuizArenaSubject,
                onRequestStartChapterQuiz = onRequestStartChapterQuiz,
                onDismissModeGate = onDismissQuizModeGate,
                onConfirmStartMode = onConfirmStartQuizMode,
                onSelectOption = onSelectQuizArenaOption,
                onClearOption = onClearQuizArenaOption,
                onToggleMarkForReview = onToggleQuizMarkForReview,
                onJumpToQuestion = onJumpToQuizQuestion,
                onTogglePalette = onToggleQuizPalette,
                onSubmitTest = onSubmitQuizArenaSession,
                onExitSession = onExitQuizArenaSession,
                onRetryMissed = onRetryMissedQuizQuestions,
                onRetakeFull = onRetakeFullQuiz,
                onStudyWeakChapterInTutor = onStudyWeakChapterInTutor,
            )

            WorkspaceTab.AI_TUTOR -> GrokAiTutorScreen(
                state = uiState.grokTutorState,
                batches = uiState.batchesState.batches,
                subjectsByBatch = uiState.pwVaultState.subjectsByBatch,
                isWideLayout = isWideLayout,
                onOpenSettings = onOpenSettings,
                onToggleSidebar = onToggleTutorSidebar,
                onCloseSidebar = onCloseTutorSidebar,
                onStartNewChat = onStartNewTutorChat,
                onSelectSession = onSelectTutorSession,
                onOpenChapterPicker = onOpenTutorChapterPicker,
                onCloseChapterPicker = onCloseTutorChapterPicker,
                onConfirmLinkedChapters = onConfirmTutorLinkedChapters,
                onOpenRagIndexDialog = onOpenTutorRagIndexDialog,
                onCloseRagIndexDialog = onCloseTutorRagIndexDialog,
                onUpdateComposerText = onUpdateTutorComposerText,
                onSendPrompt = onSendGrokTutorPrompt,
                onOpenDiagramModal = onOpenDiagramModal,
                onOpenDiagramSplitScreen = onOpenDiagramSplitScreen,
                onPreviewCatalogDiagram = onPreviewCatalogDiagram,
                onToggleDiagramSplitScreen = onToggleDiagramSplitScreen,
                onCloseDiagramViewer = onCloseDiagramViewer,
                onZoomDiagram = onZoomDiagram,
                onResetDiagramZoom = onResetDiagramZoom,
            )

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
