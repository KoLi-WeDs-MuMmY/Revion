package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.WorkspaceShell
import com.example.ui.theme.GhickinTheme
import com.example.viewmodel.WorkspaceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            val workspaceViewModel: WorkspaceViewModel = viewModel()
            val uiState by workspaceViewModel.uiState.collectAsStateWithLifecycle()

            GhickinTheme(accentMood = uiState.accentMood) {
                WorkspaceShell(
                    uiState = uiState,
                    onSelectTab = workspaceViewModel::selectTab,
                    onToggleFocusMode = workspaceViewModel::toggleFocusMode,
                    onSelectAccentMood = workspaceViewModel::selectAccentMood,
                    onCycleAccentMood = workspaceViewModel::cycleAccentMood,
                    onOpenInspect = workspaceViewModel::openInspect,
                    onCloseInspect = workspaceViewModel::closeInspect,
                    onJumpFromInspect = workspaceViewModel::jumpFromInspectToTab,
                    onSelectBatch = workspaceViewModel::selectBatch,
                    onToggleExpandSubject = workspaceViewModel::toggleExpandSubject,
                    onToggleChapterCompleted = workspaceViewModel::toggleChapterCompleted,
                    onOpenCreateBatchBuilder = workspaceViewModel::openCreateBatchBuilder,
                    onCloseCreateBatchBuilder = workspaceViewModel::closeCreateBatchBuilder,
                    onCreateCustomBatch = workspaceViewModel::createCustomBatch,
                    onSelectVaultBatchFilter = workspaceViewModel::selectVaultBatchFilter,
                    onSelectVaultSubjectFilter = workspaceViewModel::selectVaultSubjectFilter,
                    onSelectVaultChapterFilter = workspaceViewModel::selectVaultChapterFilter,
                    onUpdateVaultSearch = workspaceViewModel::updateVaultSearchQuery,
                    onClearVaultFilters = workspaceViewModel::clearVaultFilters,
                    onPreviewVaultMaterial = workspaceViewModel::previewVaultMaterial,
                    onCloseVaultPreview = workspaceViewModel::closeVaultPreview,
                    onOpenVaultPdfReader = workspaceViewModel::openVaultPdfReader,
                    onCloseVaultPdfReader = workspaceViewModel::closeVaultPdfReader,
                    onToggleReaderInfo = workspaceViewModel::toggleReaderInfoDrawer,
                    onStepReaderPage = workspaceViewModel::stepReaderPage,
                    onJumpReaderToPage = workspaceViewModel::jumpReaderToPage,
                    onSelectRevisionQuizMode = workspaceViewModel::selectRevisionQuizMode,
                    onSelectRevisionSubject = workspaceViewModel::selectRevisionSubject,
                    onToggleCompoundRevisionMix = workspaceViewModel::toggleCompoundRevisionMix,
                    onToggleDiagramLabels = workspaceViewModel::toggleDiagramLabels,
                    onSelectQuizMode = workspaceViewModel::selectQuizMode,
                    onSelectQuizSubject = workspaceViewModel::selectQuizSubject,
                    onSelectQuizOption = workspaceViewModel::selectQuizOption,
                    onAdvanceOrSubmitQuiz = workspaceViewModel::advanceOrSubmitQuiz,
                    onRetryMissedQuestions = workspaceViewModel::retryMissedQuestions,
                    onRetakeFullQuiz = workspaceViewModel::retakeFullQuiz,
                    onJumpFromQuizToRevision = workspaceViewModel::jumpFromQuizToRevisionNotes,
                    onSelectFlashcardHubMode = workspaceViewModel::selectFlashcardHubMode,
                    onSelectSpacedBucket = workspaceViewModel::selectSpacedBucket,
                    onSelectFlashcardSubjectFilter = workspaceViewModel::selectFlashcardSubjectFilter,
                    onToggleFlashcardFlip = workspaceViewModel::toggleFlashcardFlip,
                    onRateFlashcard = workspaceViewModel::rateFlashcard,
                    onResetFlashcardSession = workspaceViewModel::resetFlashcardSession,
                    onSelectMistakeCategoryFilter = workspaceViewModel::selectMistakeCategoryFilter,
                    onSelectMistakeSubjectFilter = workspaceViewModel::selectMistakeSubjectFilter,
                    onSelectMistakeRecord = workspaceViewModel::selectMistakeRecord,
                    onSendMistakeToWeekendDeck = workspaceViewModel::sendMistakeToWeekendHardDeck,
                    onSelectTutorPlannerMode = workspaceViewModel::selectTutorPlannerMode,
                    onToggleCoPlanSplit = workspaceViewModel::toggleCoPlanSplit,
                    onSelectRagChapter = workspaceViewModel::selectRagChapter,
                    onSendTutorDoubt = workspaceViewModel::sendTutorDoubt,
                    onOpenScheduleSettings = workspaceViewModel::openPlannerScheduleSettings,
                    onCloseScheduleSettings = workspaceViewModel::closePlannerScheduleSettings,
                    onToggleAdaptiveSync = workspaceViewModel::toggleAdaptiveSync,
                    onStepManualWake = workspaceViewModel::stepManualWakeMinutes,
                    onStepManualSleep = workspaceViewModel::stepManualSleepMinutes,
                    onTogglePlannerBlockCompleted = workspaceViewModel::togglePlannerBlockCompleted,
                    onRequestAddPlannerBlock = workspaceViewModel::openPermissionRequestForAdd,
                    onRequestEditPlannerBlock = workspaceViewModel::openPermissionRequestForEdit,
                    onClosePermissionRequest = workspaceViewModel::closePermissionRequest,
                    onUpdatePermissionDraft = workspaceViewModel::updatePermissionRequestDraft,
                    onSubmitPermissionToAi = workspaceViewModel::submitPermissionRequestToAi,
                    onApplyApprovedPermission = workspaceViewModel::applyApprovedPermissionChange,
                    onAcceptCounterProposal = workspaceViewModel::acceptPermissionCounterProposal,
                    onNavigateBackToHome = workspaceViewModel::navigateBackToHome,
                )
            }
        }
    }
}
