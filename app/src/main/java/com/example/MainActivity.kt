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
                    onNavigateBackToHome = workspaceViewModel::navigateBackToHome,
                )
            }
        }
    }
}
