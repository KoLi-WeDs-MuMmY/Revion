package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
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

            GhickinTheme(
                appTheme = uiState.appTheme,
                accentMood = uiState.accentMood,
            ) {
                WorkspaceShell(
                    uiState = uiState,
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
                    onNavigateBackToHome = workspaceViewModel::navigateBackToHome,
                )
            }
        }
    }
}
