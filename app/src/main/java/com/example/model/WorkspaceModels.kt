package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FolderCopy
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Style
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.AccentMood

enum class WorkspaceTab(
    val routeId: String,
    val label: String,
    val shortLabel: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val phaseTag: String,
    val previewTitle: String,
    val previewSummary: String,
    val plannedCapabilities: List<String>,
    val suggestedMood: AccentMood,
) {
    HOME(
        routeId = "home",
        label = "Home",
        shortLabel = "Home",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
        phaseTag = "PHASE 1.1",
        previewTitle = "Home Command Center",
        previewSummary = "Daily revision readiness, subject mastery telemetry, and spaced repetition queues.",
        plannedCapabilities = emptyList(),
        suggestedMood = AccentMood.CYAN,
    ),
    BATCHES(
        routeId = "batches",
        label = "Batches",
        shortLabel = "Batches",
        selectedIcon = Icons.Filled.Layers,
        unselectedIcon = Icons.Outlined.Layers,
        phaseTag = "PHASE 1.2",
        previewTitle = "Custom Batch & Subject Maker",
        previewSummary = "Organize exam tracks, custom subject workspaces, chapter roadmaps, and target mastery milestones.",
        plannedCapabilities = listOf(
            "Custom batch & subject architect with per-subject M3 color signatures",
            "Chapter syllabus checklists with weightage and completion tracking",
            "Cross-subject workload balance and weekly study velocity view",
        ),
        suggestedMood = AccentMood.VIOLET,
    ),
    VAULT(
        routeId = "vault",
        label = "Vault",
        shortLabel = "Vault",
        selectedIcon = Icons.Filled.FolderCopy,
        unselectedIcon = Icons.Outlined.FolderCopy,
        phaseTag = "PHASE 1.2",
        previewTitle = "Study Material Vault",
        previewSummary = "Centralized library for chapter PDFs, annotated formula sheets, lecture logs, and quick visual previews.",
        plannedCapabilities = listOf(
            "Structured chapter folders with material type filters and metadata tags",
            "Split-pane document preview deck optimized for 16:9 landscape reading",
            "Instant log-to-revision bookmarking for studied chapters",
        ),
        suggestedMood = AccentMood.CYAN,
    ),
    REVISION_QUIZ(
        routeId = "revision_quiz",
        label = "Revision & Quiz",
        shortLabel = "Revision",
        selectedIcon = Icons.Filled.Quiz,
        unselectedIcon = Icons.Outlined.Quiz,
        phaseTag = "PHASE 1.3",
        previewTitle = "Daily Revision Notes & Interactive Quiz",
        previewSummary = "Synthesized daily revision digests, clean concept diagrams, and timed adaptive subject checks.",
        plannedCapabilities = listOf(
            "Structured daily revision sheets distilled from yesterday's logged topics",
            "Interactive concept diagrams and formula recall reveal cards",
            "Multi-mode subject quiz runner with confidence tagging and instant explanations",
        ),
        suggestedMood = AccentMood.EMERALD,
    ),
    FLASHCARDS(
        routeId = "flashcards",
        label = "Flashcards",
        shortLabel = "Cards",
        selectedIcon = Icons.Filled.Style,
        unselectedIcon = Icons.Outlined.Style,
        phaseTag = "PHASE 1.4",
        previewTitle = "Spaced Flashcards & Mistake Bank",
        previewSummary = "Precision x-1, x-3, and weekend hard-topic spaced repetition decks paired with a dedicated Mistake Bank.",
        plannedCapabilities = listOf(
            "3D tactile flip cards scheduled on x-1, x-3, and weekend consolidation cadence",
            "Confidence grading (Again, Hard, Solid) with kinetic progress bar",
            "Mistake Bank triage isolating recurring error patterns by chapter",
        ),
        suggestedMood = AccentMood.AMBER,
    ),
    TUTOR_PLANNER(
        routeId = "tutor_planner",
        label = "Tutor & Planner",
        shortLabel = "Tutor",
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome,
        phaseTag = "PHASE 1.5",
        previewTitle = "Chapter RAG AI Tutor & Daily Planner",
        previewSummary = "Chapter-grounded Socratic AI tutoring alongside a wake-to-sleep time-blocked study planner.",
        plannedCapabilities = listOf(
            "Side-by-side chapter context pane and grounded AI tutor conversation UI",
            "Wake-to-sleep daily block timeline with focus sprints and recovery breaks",
            "Weak-topic intervention slots automatically surfaced in the daily schedule",
        ),
        suggestedMood = AccentMood.VIOLET,
    ),
}

enum class FocusMode {
    AI_READY,
    DEEP_REVIEW,
}

@Immutable
data class DailyRevisionBrief(
    val headline: String,
    val summary: String,
    val sourceSubjects: List<Pair<String, AccentMood>>,
    val estimatedMinutes: Int,
    val checkCount: Int,
    val targetRetentionPercent: Int,
    val focusPoints: List<String>,
)

@Immutable
data class SubjectMastery(
    val id: String,
    val name: String,
    val accentMood: AccentMood,
    val masteryPercent: Int,
    val chaptersCompleted: Int,
    val totalChapters: Int,
    val weeklyDelta: String,
    val activeChapter: String,
    val focusNote: String,
    val keyConcepts: List<String>,
)

@Immutable
data class LoggedTopic(
    val id: String,
    val subjectName: String,
    val accentMood: AccentMood,
    val chapterTitle: String,
    val subtopicSummary: String,
    val durationMinutes: Int,
    val confidenceLabel: String,
    val loggedTimeLabel: String,
    val keyTakeaways: List<String>,
)

@Immutable
data class SpacedReviewItem(
    val id: String,
    val subjectName: String,
    val accentMood: AccentMood,
    val topicTitle: String,
    val intervalBadge: String,
    val dueLabel: String,
    val cardCount: Int,
    val retentionPercent: Int,
    val reviewNotes: List<String>,
)

sealed interface InspectSelection {
    data class HeroBrief(val brief: DailyRevisionBrief) : InspectSelection
    data class Subject(val subject: SubjectMastery) : InspectSelection
    data class Topic(val topic: LoggedTopic) : InspectSelection
    data class Review(val review: SpacedReviewItem) : InspectSelection
}

@Immutable
data class WorkspaceUiState(
    val activeTab: WorkspaceTab = WorkspaceTab.HOME,
    val accentMood: AccentMood = AccentMood.CYAN,
    val focusMode: FocusMode = FocusMode.AI_READY,
    val streakDays: Int = 14,
    val revisionBrief: DailyRevisionBrief,
    val subjects: List<SubjectMastery>,
    val yesterdayTopics: List<LoggedTopic>,
    val spacedReviews: List<SpacedReviewItem>,
    val inspectSelection: InspectSelection? = null,
    val batchesState: BatchesUiState,
    val vaultState: VaultUiState,
    val revisionQuizState: RevisionQuizUiState,
    val flashcardsState: FlashcardsMistakesUiState,
    val tutorPlannerState: TutorPlannerUiState,
)
