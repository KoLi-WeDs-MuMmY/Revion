package com.example.model

import androidx.compose.runtime.Immutable

@Immutable
data class ChapterDiagramRef(
    val fileName: String,
    val title: String,
    val chapterKey: String,
    val chapterTitle: String,
    val summary: String,
    val keywords: List<String>,
    val isPreseeded: Boolean = true,
)

@Immutable
data class DiagramAttachment(
    val fileName: String,
    val title: String,
    val chapterTitle: String,
    val summary: String,
    val generatedOnTheFly: Boolean = false,
)

@Immutable
data class TutorChatMessage(
    val id: String,
    val isUser: Boolean,
    val visibleText: String,
    val formulaBlock: String? = null,
    val citations: List<String> = emptyList(),
    val diagramAttachment: DiagramAttachment? = null,
    val invisiblePromptUsed: String? = null,
    val timestampLabel: String = "Just now",
)

@Immutable
data class TutorChatSession(
    val id: String,
    val title: String,
    val batchId: String,
    val linkedChapterIds: List<String>,
    val messages: List<TutorChatMessage>,
    val updatedAtLabel: String,
    val updatedAtMillis: Long,
)

@Immutable
data class DiagramViewerState(
    val attachment: DiagramAttachment? = null,
    val isSplitScreen: Boolean = false,
    val isFullModalOpen: Boolean = false,
    val zoomPercent: Int = 100,
)

@Immutable
data class GrokTutorUiState(
    val sessions: List<TutorChatSession>,
    val activeSessionId: String? = null,
    val draftBatchId: String,
    val draftLinkedChapterIds: List<String> = emptyList(),
    val isSidebarExpanded: Boolean = false,
    val isChapterPickerOpen: Boolean = false,
    val isRagIndexDialogOpen: Boolean = false,
    val composerText: String = "",
    val isGeneratingReply: Boolean = false,
    val viewerState: DiagramViewerState = DiagramViewerState(),
    val diagramCatalog: List<ChapterDiagramRef> = emptyList(),
) {
    val activeSession: TutorChatSession?
        get() = sessions.firstOrNull { it.id == activeSessionId }

    val currentLinkedChapterIds: List<String>
        get() = activeSession?.linkedChapterIds ?: draftLinkedChapterIds
}

enum class TestArenaMode(
    val label: String,
    val badge: String,
) {
    EXAM(label = "Exam Mode", badge = "+4 / −1"),
    NORMAL(label = "Normal Mode", badge = "Instant"),
}

@Immutable
data class ArenaQuestion(
    val id: String,
    val chapterKey: String,
    val subjectName: String,
    val chapterTitle: String,
    val topicName: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val stepByStepExplanation: List<String>,
    val formulaAnchor: String,
    val difficulty: String = "Medium",
)

@Immutable
data class WeakTopicDiagnostic(
    val chapterId: String,
    val chapterTitle: String,
    val topicName: String,
    val missedCount: Int,
    val totalCount: Int,
)

@Immutable
data class QuizAttemptSummary(
    val id: String,
    val batchId: String,
    val subjectId: String,
    val chapterId: String,
    val subjectName: String,
    val chapterTitle: String,
    val mode: TestArenaMode,
    val correctCount: Int,
    val wrongCount: Int,
    val unattemptedCount: Int,
    val netScore: Int,
    val maxScore: Int,
    val accuracyPercent: Int,
    val weakTopics: List<WeakTopicDiagnostic>,
    val timestampLabel: String,
)

@Immutable
data class ActiveQuizSession(
    val sessionId: String,
    val batchId: String,
    val subjectId: String,
    val chapterId: String,
    val subjectName: String,
    val chapterTitle: String,
    val mode: TestArenaMode,
    val questions: List<ArenaQuestion>,
    val currentIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(),
    val markedForReview: Set<Int> = emptySet(),
    val revealedNormalIndices: Set<Int> = emptySet(),
    val remainingSeconds: Int = 0,
    val totalDurationSeconds: Int = 0,
    val isSubmitted: Boolean = false,
    val isPaletteOpen: Boolean = false,
    val summary: QuizAttemptSummary? = null,
) {
    val currentQuestion: ArenaQuestion
        get() = questions[currentIndex.coerceIn(0, (questions.size - 1).coerceAtLeast(0))]

    val answeredCount: Int
        get() = selectedAnswers.size

    val markedCount: Int
        get() = markedForReview.size
}

@Immutable
data class PwQuizArenaUiState(
    val selectedBatchId: String,
    val selectedSubjectId: String? = null,
    val selectedChapterId: String? = null,
    val pendingModeGateChapterId: String? = null,
    val pendingModeGateSubjectId: String? = null,
    val activeSession: ActiveQuizSession? = null,
    val recentAttempts: List<QuizAttemptSummary> = emptyList(),
)
