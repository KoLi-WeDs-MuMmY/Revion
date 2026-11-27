package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FolderCopy
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Timer
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
        phaseTag = "PHASE 1.6.1",
        previewTitle = "PW-Style Study Material Vault",
        previewSummary = "Batch -> Subject -> Chapter -> Lecture archive organizing Class Notes/RAG, Flashcards, and Revision digests.",
        plannedCapabilities = listOf(
            "Collapsible breadcrumb navigation across Batches, Subjects, Chapters & Lectures",
            "Icon-triggered Command Palette for distraction-free search & filtering",
            "Morphing sub-views for Class Notes/RAG, Flashcards (Chapter -> Class), and Revision",
        ),
        suggestedMood = AccentMood.CYAN,
    ),
    QUIZZES(
        routeId = "quizzes",
        label = "Quizzes",
        shortLabel = "Quizzes",
        selectedIcon = Icons.Filled.Quiz,
        unselectedIcon = Icons.Outlined.Quiz,
        phaseTag = "PHASE 1.6.2",
        previewTitle = "PW-Style Quiz Arena & Exam Mode",
        previewSummary = "Batch -> Subject -> Chapter practice drills with Exam Mode (+4/-1, timer, mark-for-review, palette) and Normal instant explanations.",
        plannedCapabilities = emptyList(),
        suggestedMood = AccentMood.AMBER,
    ),
    AI_TUTOR(
        routeId = "ai_tutor",
        label = "AI Tutor",
        shortLabel = "Tutor",
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome,
        phaseTag = "PHASE 1.6.2",
        previewTitle = "Grok-Style Chapter AI Tutor & Tests",
        previewSummary = "Collapsible chat history grouped by batch & chapter, code-based Image-Code Web Viewer, and Exam vs. Normal test modes.",
        plannedCapabilities = listOf(
            "Grok-style collapsible chat history grouped cleanly by Batch & Chapter",
            "Image-Code Web Viewer rendering visual revision diagrams matched in chapter RAG",
            "Dual-mode Normal vs. Exam tests with step-by-step concept explanations",
        ),
        suggestedMood = AccentMood.VIOLET,
    ),
    DAILY_PLANNER(
        routeId = "daily_planner",
        label = "Daily Planner",
        shortLabel = "Planner",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        phaseTag = "PHASE 1.6.3",
        previewTitle = "Standalone AI Daily Planner",
        previewSummary = "AI-governed daily schedule with single daily-updated RAG summary (every 3 days), zero-context carryover per open, and AI task approval.",
        plannedCapabilities = listOf(
            "Dedicated AI toggle with zero-context carryover per planner session",
            "Single daily-updated RAG progress file summarized cleanly every 3 days",
            "AI rule approval to confirm your day or authorize task edits and cancellations",
        ),
        suggestedMood = AccentMood.EMERALD,
    ),
    TIMER_GROUP(
        routeId = "timer_group",
        label = "Timer & Group",
        shortLabel = "Timer",
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer,
        phaseTag = "PHASE 1.6.3",
        previewTitle = "Customizable Study Timer & YPT Group Study",
        previewSummary = "Focused customizable study timers paired with YPT-style accountability rooms and subject time tracking.",
        plannedCapabilities = listOf(
            "Customizable Pomodoro, deep-sprint, and exam countdown timers",
            "YPT-style live accountability study groups with daily study leaderboards",
            "Automatic subject session logging linked to your Primary Batch syllabus",
        ),
        suggestedMood = AccentMood.AMBER,
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
data class HomeLiveAnalytics(
    val completedLectures: Int,
    val totalLectures: Int,
    val remainingLectures: Int,
    val masteryPercent: Int,
    val completedChapters: Int,
    val totalChapters: Int,
    val dailyGatesCleared: Int,
    val dailyRecallReadinessPercent: Int,
    val batchFlashcardCount: Int,
)

@Immutable
data class WorkspaceUiState(
    val activeTab: WorkspaceTab = WorkspaceTab.HOME,
    val appTheme: com.example.ui.theme.AppThemeId = com.example.ui.theme.AppThemeId.GROK_BLACK,
    val isSettingsOpen: Boolean = false,
    val accentMood: AccentMood = AccentMood.CYAN,
    val focusMode: FocusMode = FocusMode.AI_READY,
    val streakDays: Int = 14,
    // Single Source of Truth for Primary Batch synced across Home, Batches & Vault
    val primaryBatchId: String = "batch_jee_2026",
    val revisionBrief: DailyRevisionBrief,
    val subjects: List<SubjectMastery>,
    val yesterdayTopics: List<LoggedTopic>,
    val spacedReviews: List<SpacedReviewItem>,
    val inspectSelection: InspectSelection? = null,
    val homeCommandState: HomeCommandState,
    val batchesState: BatchesUiState,
    val vaultState: VaultUiState,
    val pwVaultState: PwVaultUiState,
    val revisionQuizState: RevisionQuizUiState,
    val flashcardsState: FlashcardsMistakesUiState,
    val tutorPlannerState: TutorPlannerUiState,
    val grokTutorState: GrokTutorUiState,
    val pwQuizArenaState: PwQuizArenaUiState,
) {
    val primaryBatch: StudyBatch
        get() = batchesState.batches.firstOrNull { it.id == primaryBatchId }
            ?: batchesState.batches.first()

    val liveHomeAnalytics: HomeLiveAnalytics
        get() {
            val batch = primaryBatch
            val vaultSubjects = pwVaultState.subjectsByBatch[batch.id].orEmpty()
            val totalLecs = if (vaultSubjects.isNotEmpty()) {
                vaultSubjects.sumOf { it.totalLectures }
            } else {
                batch.subjects.sumOf { it.lectureCount }
            }
            val doneLecs = if (vaultSubjects.isNotEmpty()) {
                vaultSubjects.sumOf { it.completedLectures }
            } else {
                batch.subjects.sumOf { it.completedLectures }
            }
            val remainingLecs = (totalLecs - doneLecs).coerceAtLeast(0)
            val mastery = if (totalLecs == 0) 0 else (doneLecs * 100) / totalLecs
            val gatesCleared = (if (homeCommandState.todayRevisionCompleted) 1 else 0) +
                (if (homeCommandState.todayFlashcardsCompleted) 1 else 0)
            val batchCards = vaultSubjects.sumOf { subj ->
                subj.chapters.sumOf { it.totalFlashcards }
            } + homeCommandState.todayFlashcards.size

            return HomeLiveAnalytics(
                completedLectures = doneLecs,
                totalLectures = totalLecs,
                remainingLectures = remainingLecs,
                masteryPercent = mastery,
                completedChapters = batch.completedChapters,
                totalChapters = batch.totalChapters,
                dailyGatesCleared = gatesCleared,
                dailyRecallReadinessPercent = gatesCleared * 50,
                batchFlashcardCount = batchCards,
            )
        }
}
