package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.Phase163Actions
import com.example.ui.navigation.Phase164Actions
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

            LaunchedEffect(uiState.appTheme.isLight) {
                if (uiState.appTheme.isLight) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                        navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
                    )
                } else {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
                        navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
                    )
                }
            }

            val phase163Actions = remember(workspaceViewModel) {
                Phase163Actions(
                    onToggleStandaloneAiPlanner = workspaceViewModel::toggleStandaloneAiPlanner,
                    onReopenPlannerZeroContextWindow = workspaceViewModel::reopenPlannerZeroContextWindow,
                    onSimulateFirstTimePlannerIngestion = workspaceViewModel::simulateFirstTimePlannerIngestion,
                    onRefreshPlannerRag2 = workspaceViewModel::refreshPlannerRag2,
                    onRequestConfirmDailySchedule = workspaceViewModel::requestConfirmDailySchedule,
                    onToggleStandaloneTaskCompleted = workspaceViewModel::toggleStandaloneTaskCompleted,
                    onRequestAddStandaloneTask = workspaceViewModel::requestAddStandaloneTask,
                    onRequestEditStandaloneTask = workspaceViewModel::requestEditStandaloneTask,
                    onRequestCancelStandaloneTask = workspaceViewModel::requestCancelStandaloneTask,
                    onUpdatePlannerGateTitle = workspaceViewModel::updatePlannerGateTitle,
                    onUpdatePlannerGateTimeWindow = workspaceViewModel::updatePlannerGateTimeWindow,
                    onUpdatePlannerGateReason = workspaceViewModel::updatePlannerGateReason,
                    onSimulatePlannerGateOutcome = workspaceViewModel::simulatePlannerGateOutcome,
                    onApplyPlannerGateApproved = workspaceViewModel::applyPlannerGateApproved,
                    onAcceptPlannerGateCounterProposal = workspaceViewModel::acceptPlannerGateCounterProposal,
                    onDismissPlannerGate = workspaceViewModel::dismissPlannerGate,
                    onDismissPlannerNotice = workspaceViewModel::dismissPlannerNotice,
                    onSelectTimerMode = workspaceViewModel::selectTimerMode,
                    onSelectPomodoroStyle = workspaceViewModel::selectPomodoroStyle,
                    onApplyPomodoroPreset = workspaceViewModel::applyPomodoroPreset,
                    onSwitchPomodoroPhase = workspaceViewModel::switchPomodoroPhase,
                    onAddFiveMinutesToTimer = workspaceViewModel::addFiveMinutesToTimer,
                    onRequestStartFromMainTimerButton = workspaceViewModel::requestStartFromMainTimerButton,
                    onQuickStartSubjectTimer = workspaceViewModel::quickStartSubjectTimer,
                    onConfirmStartTimerWithTag = workspaceViewModel::confirmStartTimerWithTag,
                    onSkipStartTimerTagless = workspaceViewModel::skipStartTimerTagless,
                    onDismissStartTagDialog = workspaceViewModel::dismissStartTagDialog,
                    onPauseOrResumeStudyTimer = workspaceViewModel::pauseOrResumeStudyTimer,
                    onResetStudyTimer = workspaceViewModel::resetStudyTimer,
                    onStopAndLogStudyTimer = workspaceViewModel::stopAndLogStudyTimer,
                    onSetTimerSplitRatio = workspaceViewModel::setTimerSplitRatio,
                    onToggleGroupStudySplit = workspaceViewModel::toggleGroupStudySplit,
                    onOpenTimerStatsSheet = workspaceViewModel::openTimerStatsSheet,
                    onCloseTimerStatsSheet = workspaceViewModel::closeTimerStatsSheet,
                    onJoinYptGroup = workspaceViewModel::joinYptGroup,
                    onOpenCreateYptGroupDialog = workspaceViewModel::openCreateYptGroupDialog,
                    onCloseCreateYptGroupDialog = workspaceViewModel::closeCreateYptGroupDialog,
                    onCreatePublicYptGroup = workspaceViewModel::createPublicYptGroup,
                    onSelectYptUserStatus = workspaceViewModel::selectYptUserStatus,
                    onDismissSingleTimerLockNotice = workspaceViewModel::dismissSingleTimerLockNotice,
                    onOpenVaultWhileTimerActive = workspaceViewModel::openVaultWhileTimerActive,
                    onReturnToActiveTimerFromVault = workspaceViewModel::returnToActiveTimerFromVault,
                    onSelectSettingsSection = workspaceViewModel::selectSettingsSection,
                    onOpenSettingsAtRagFile = workspaceViewModel::openSettingsAtRagFile,
                    onSelectSettingsRagFile = workspaceViewModel::selectSettingsRagFile,
                    onUpdateSettingsRagUserNote = workspaceViewModel::updateSettingsRagUserNote,
                    onRunSettingsRagAiOptimization = workspaceViewModel::runSettingsRagAiOptimization,
                )
            }

            val phase164Actions = remember(workspaceViewModel) {
                Phase164Actions(
                    onSelectClockTheme = workspaceViewModel::selectClockTheme,
                    onSelectMotionIntensity = workspaceViewModel::selectMotionIntensity,
                    onSelectLayoutDensity = workspaceViewModel::selectLayoutDensity,
                    onAdjustDefaultFocusMinutes = workspaceViewModel::adjustDefaultFocusMinutes,
                    onAdjustDefaultBreakMinutes = workspaceViewModel::adjustDefaultBreakMinutes,
                    onToggleAutoLogStudySessions = workspaceViewModel::toggleAutoLogStudySessions,
                    onSelectSubjectIndicatorStyle = workspaceViewModel::selectSubjectIndicatorStyle,
                    onAdjustManualTaskEditCap = workspaceViewModel::adjustManualTaskEditCap,
                    onRequestResetDemoState = workspaceViewModel::requestResetDemoState,
                    onConfirmResetDemoState = workspaceViewModel::confirmResetDemoState,
                    onDismissResetDemoDialog = workspaceViewModel::dismissResetDemoDialog,
                    onOpenHomeClockTagPicker = workspaceViewModel::openHomeClockTagPicker,
                    onDismissHomeClockTagPicker = workspaceViewModel::dismissHomeClockTagPicker,
                    onSelectHomeClockBatch = workspaceViewModel::selectHomeClockBatch,
                    onSelectHomeClockSubject = workspaceViewModel::selectHomeClockSubject,
                    onSelectHomeClockChapter = workspaceViewModel::selectHomeClockChapter,
                    onSelectHomeClockLecture = workspaceViewModel::selectHomeClockLecture,
                    onConfirmHomeClockTagSelection = workspaceViewModel::confirmHomeClockTagSelection,
                    onStartHomeClockUntagged = workspaceViewModel::startHomeClockUntagged,
                    onAdjustCustomTimerSeconds = workspaceViewModel::adjustCustomTimerSeconds,
                    onSetSplitPosture = workspaceViewModel::setSplitPosture,
                    onToggleSplitView = workspaceViewModel::toggleSplitView,
                    onInspectYptMember = workspaceViewModel::inspectYptMember,
                    onSendSilentBoost = workspaceViewModel::sendSilentBoost,
                    onOpenAllowedAppsSheet = workspaceViewModel::openAllowedAppsSheet,
                    onCloseAllowedAppsSheet = workspaceViewModel::closeAllowedAppsSheet,
                    onToggleAllowedApp = workspaceViewModel::toggleAllowedApp,
                    onSetPlannerAiSidePanelOpen = workspaceViewModel::setPlannerAiSidePanelOpen,
                    onOpenSimulatedRagSheet = workspaceViewModel::openSimulatedRagSheet,
                    onCloseSimulatedRagSheet = workspaceViewModel::closeSimulatedRagSheet,
                    onOpenAppealForBlock = workspaceViewModel::openAppealForBlock,
                    onSelectAppealPresetReason = workspaceViewModel::selectAppealPresetReason,
                    onUpdateAppealCustomReason = workspaceViewModel::updateAppealCustomReason,
                    onSubmitAppeal = workspaceViewModel::submitAppeal,
                    onApplyApprovedAppeal = workspaceViewModel::applyApprovedAppeal,
                    onDismissAppeal = workspaceViewModel::dismissAppeal,
                    onTogglePlannerUserTaskCompleted = workspaceViewModel::togglePlannerUserTaskCompleted,
                    onAddPlannerUserTask = workspaceViewModel::addPlannerUserTask,
                    onManualEditPlannerUserTask = workspaceViewModel::manualEditPlannerUserTask,
                    onRequestAiReauthorizeManualEdits = workspaceViewModel::requestAiReauthorizeManualEdits,
                    onUpdatePlannerAiSideComposer = workspaceViewModel::updatePlannerAiSideComposer,
                    onSendPlannerAiSidePrompt = workspaceViewModel::sendPlannerAiSidePrompt,
                )
            }

            GhickinTheme(
                appTheme = uiState.appTheme,
                accentMood = uiState.accentMood,
            ) {
                WorkspaceShell(
                    uiState = uiState,
                    clockTickFlow = workspaceViewModel.clockTick,
                    onSelectTab = workspaceViewModel::selectTab,
                    onSelectAppTheme = workspaceViewModel::selectAppTheme,
                    onOpenSettings = workspaceViewModel::openSettings,
                    onCloseSettings = workspaceViewModel::closeSettings,
                    onToggleFocusMode = workspaceViewModel::toggleFocusMode,
                    onSelectAccentMood = workspaceViewModel::selectAccentMood,
                    onCycleAccentMood = workspaceViewModel::cycleAccentMood,
                    onSetPrimaryBatch = workspaceViewModel::setPrimaryBatch,
                    onCyclePrimaryBatch = workspaceViewModel::cyclePrimaryBatch,
                    onOpenDailyPage = workspaceViewModel::openDailyPage,
                    onCloseDailyPage = workspaceViewModel::closeDailyPage,
                    onCompleteTodayRevision = workspaceViewModel::completeTodayRevision,
                    onFlipDailyFlashcard = workspaceViewModel::flipDailyFlashcard,
                    onStepDailyFlashcard = workspaceViewModel::stepDailyFlashcard,
                    onCompleteTodayFlashcards = workspaceViewModel::completeTodayFlashcards,
                    onToggleTodayRevisionCompleted = workspaceViewModel::toggleTodayRevisionCompleted,
                    onToggleTodayFlashcardsCompleted = workspaceViewModel::toggleTodayFlashcardsCompleted,
                    onToggleStudyGateTestOverride = workspaceViewModel::toggleStudyGateTestOverride,
                    onStartTodaysNewStudy = workspaceViewModel::startTodaysNewStudy,
                    onSelectBatch = workspaceViewModel::selectBatch,
                    onToggleExpandSubject = workspaceViewModel::toggleExpandSubject,
                    onToggleExpandChapterInBatches = workspaceViewModel::toggleExpandChapterInBatches,
                    onToggleChapterCompleted = workspaceViewModel::toggleChapterCompleted,
                    onToggleLectureCompleted = workspaceViewModel::toggleLectureCompleted,
                    onOpenBatchCrudDialog = workspaceViewModel::openBatchCrudDialog,
                    onCloseBatchCrudDialog = workspaceViewModel::closeBatchCrudDialog,
                    onEditBatch = workspaceViewModel::editBatch,
                    onDeleteBatch = workspaceViewModel::deleteBatch,
                    onAddSubjectToBatch = workspaceViewModel::addSubjectToBatch,
                    onEditSubject = workspaceViewModel::editSubject,
                    onDeleteSubject = workspaceViewModel::deleteSubject,
                    onAddChapterToSubject = workspaceViewModel::addChapterToSubject,
                    onEditChapter = workspaceViewModel::editChapter,
                    onDeleteChapter = workspaceViewModel::deleteChapter,
                    onAddLectureToChapter = workspaceViewModel::addLectureToChapter,
                    onEditLecture = workspaceViewModel::editLecture,
                    onDeleteLecture = workspaceViewModel::deleteLecture,
                    onOpenCreateBatchBuilder = workspaceViewModel::openCreateBatchBuilder,
                    onCloseCreateBatchBuilder = workspaceViewModel::closeCreateBatchBuilder,
                    onCreateCustomBatch = workspaceViewModel::createCustomBatch,
                    onSelectPwVaultSubject = workspaceViewModel::selectPwVaultSubject,
                    onSelectPwVaultChapter = workspaceViewModel::selectPwVaultChapter,
                    onSelectPwVaultLecture = workspaceViewModel::selectPwVaultLecture,
                    onSelectPwVaultSubView = workspaceViewModel::selectPwVaultSubView,
                    onToggleExpandedClassNote = workspaceViewModel::toggleExpandedClassNote,
                    onSelectPreviewFlashcard = workspaceViewModel::selectPreviewFlashcard,
                    onFlipPreviewFlashcard = workspaceViewModel::flipPreviewFlashcard,
                    onOpenVaultCommandPalette = workspaceViewModel::openVaultCommandPalette,
                    onCloseVaultCommandPalette = workspaceViewModel::closeVaultCommandPalette,
                    onUpdateVaultPaletteQuery = workspaceViewModel::updateVaultPaletteQuery,
                    onSelectVaultPaletteSubjectFilter = workspaceViewModel::selectVaultPaletteSubjectFilter,
                    onSelectVaultPaletteCategoryFilter = workspaceViewModel::selectVaultPaletteCategoryFilter,
                    onClearVaultPaletteFilters = workspaceViewModel::clearVaultPaletteFilters,
                    onJumpToVaultLectureFromPalette = workspaceViewModel::jumpToVaultLectureFromPalette,
                    onToggleTutorSidebar = workspaceViewModel::toggleTutorSidebar,
                    onCloseTutorSidebar = workspaceViewModel::closeTutorSidebar,
                    onStartNewTutorChat = workspaceViewModel::startNewTutorChat,
                    onSelectTutorSession = workspaceViewModel::selectTutorSession,
                    onOpenTutorChapterPicker = workspaceViewModel::openTutorChapterPicker,
                    onCloseTutorChapterPicker = workspaceViewModel::closeTutorChapterPicker,
                    onConfirmTutorLinkedChapters = workspaceViewModel::confirmTutorLinkedChapters,
                    onOpenTutorRagIndexDialog = workspaceViewModel::openTutorRagIndexDialog,
                    onCloseTutorRagIndexDialog = workspaceViewModel::closeTutorRagIndexDialog,
                    onUpdateTutorComposerText = workspaceViewModel::updateTutorComposerText,
                    onSendGrokTutorPrompt = workspaceViewModel::sendGrokTutorPrompt,
                    onOpenDiagramModal = workspaceViewModel::openDiagramModal,
                    onOpenDiagramSplitScreen = workspaceViewModel::openDiagramSplitScreen,
                    onPreviewCatalogDiagram = workspaceViewModel::previewCatalogDiagram,
                    onToggleDiagramSplitScreen = workspaceViewModel::toggleDiagramSplitScreen,
                    onCloseDiagramViewer = workspaceViewModel::closeDiagramViewer,
                    onZoomDiagram = workspaceViewModel::zoomDiagram,
                    onResetDiagramZoom = workspaceViewModel::resetDiagramZoom,
                    onCycleQuizArenaBatch = workspaceViewModel::cycleQuizArenaBatch,
                    onSelectQuizArenaSubject = workspaceViewModel::selectQuizArenaSubject,
                    onRequestStartChapterQuiz = workspaceViewModel::requestStartChapterQuiz,
                    onDismissQuizModeGate = workspaceViewModel::dismissQuizModeGate,
                    onConfirmStartQuizMode = workspaceViewModel::confirmStartQuizMode,
                    onSelectQuizArenaOption = workspaceViewModel::selectQuizArenaOption,
                    onClearQuizArenaOption = workspaceViewModel::clearQuizArenaOption,
                    onToggleQuizMarkForReview = workspaceViewModel::toggleQuizMarkForReview,
                    onJumpToQuizQuestion = workspaceViewModel::jumpToQuizQuestion,
                    onToggleQuizPalette = workspaceViewModel::toggleQuizPalette,
                    onSubmitQuizArenaSession = workspaceViewModel::submitQuizArenaSession,
                    onExitQuizArenaSession = workspaceViewModel::exitQuizArenaSession,
                    onRetryMissedQuizQuestions = workspaceViewModel::retryMissedQuizQuestions,
                    onRetakeFullQuiz = workspaceViewModel::retakeFullQuizArena,
                    onStudyWeakChapterInTutor = workspaceViewModel::jumpFromWeakTopicToTutor,
                    phase163Actions = phase163Actions,
                    phase164Actions = phase164Actions,
                    onNavigateBackToHome = workspaceViewModel::navigateBackToHome,
                )
            }
        }
    }
}
