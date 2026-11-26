package com.example.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.InspectSelection
import com.example.model.WorkspaceTab
import com.example.model.WorkspaceUiState
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.AccentMood

@Composable
fun HomeCommandCenter(
    uiState: WorkspaceUiState,
    isWideLayout: Boolean,
    onToggleFocusMode: () -> Unit,
    onSelectAccentMood: (AccentMood) -> Unit,
    onCycleAccentMood: () -> Unit,
    onOpenInspect: (InspectSelection) -> Unit,
    onCloseInspect: () -> Unit,
    onJumpFromInspect: (WorkspaceTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_command_center"),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1280.dp),
            contentPadding = PaddingValues(
                horizontal = if (isWideLayout) 24.dp else 16.dp,
                vertical = if (isWideLayout) 20.dp else 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item(key = "header") {
                StaggeredEntrance(delayMillis = 0L) {
                    HomeHeaderBar(
                        streakDays = uiState.streakDays,
                        focusMode = uiState.focusMode,
                        activeMood = uiState.accentMood,
                        isWideLayout = isWideLayout,
                        onToggleFocusMode = onToggleFocusMode,
                        onSelectMood = onSelectAccentMood,
                        onCycleMood = onCycleAccentMood,
                    )
                }
            }

            item(key = "hero") {
                StaggeredEntrance(delayMillis = 65L) {
                    HeroRevisionCard(
                        brief = uiState.revisionBrief,
                        isWideLayout = isWideLayout,
                        onInspectHero = {
                            onOpenInspect(InspectSelection.HeroBrief(uiState.revisionBrief))
                        },
                    )
                }
            }

            item(key = "mastery") {
                StaggeredEntrance(delayMillis = 130L) {
                    SubjectMasterySection(
                        subjects = uiState.subjects,
                        isWideLayout = isWideLayout,
                        onSubjectClick = { subject ->
                            onOpenInspect(InspectSelection.Subject(subject))
                        },
                    )
                }
            }

            item(key = "topics_and_review") {
                StaggeredEntrance(delayMillis = 195L) {
                    TopicsAndReviewSection(
                        yesterdayTopics = uiState.yesterdayTopics,
                        spacedReviews = uiState.spacedReviews,
                        isWideLayout = isWideLayout,
                        onTopicClick = { topic ->
                            onOpenInspect(InspectSelection.Topic(topic))
                        },
                        onReviewClick = { review ->
                            onOpenInspect(InspectSelection.Review(review))
                        },
                    )
                }
            }
        }

        uiState.inspectSelection?.let { activeSelection ->
            InspectDetailSheet(
                selection = activeSelection,
                onDismiss = onCloseInspect,
                onJumpToTab = onJumpFromInspect,
            )
        }
    }
}
