package com.example.model

import androidx.compose.runtime.Immutable
import com.example.ui.theme.AccentMood

enum class RevisionQuizTabMode {
    REVISION_NOTES,
    SUBJECT_QUIZ,
}

enum class QuizMode {
    NORMAL,
    EXAM,
}

enum class DiagramKind {
    GAUSS_SPHERE,
    ARRHENIUS_CURVE,
    DNA_REPLICATION,
    COMPOUND_MAP,
}

@Immutable
data class FormulaCallout(
    val title: String,
    val expression: String,
    val note: String,
)

@Immutable
data class RevisionConceptCard(
    val id: String,
    val tag: String,
    val title: String,
    val summary: String,
    val trapAlert: String,
)

@Immutable
data class RevisionSubjectSheet(
    val id: String,
    val subjectName: String,
    val accentMood: AccentMood,
    val chapterTitle: String,
    val sourceLogLabel: String,
    val estimatedMinutes: Int,
    val diagramKind: DiagramKind,
    val diagramTitle: String,
    val diagramCaption: String,
    val formulaCallouts: List<FormulaCallout>,
    val conceptCards: List<RevisionConceptCard>,
)

@Immutable
data class QuizQuestion(
    val id: String,
    val subjectId: String,
    val subjectName: String,
    val accentMood: AccentMood,
    val subtopicTag: String,
    val prompt: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
)

@Immutable
data class WeakTopicBreakdownItem(
    val subtopicTag: String,
    val subjectId: String,
    val subjectName: String,
    val accentMood: AccentMood,
    val missedCount: Int,
    val focusHint: String,
)

@Immutable
data class RevisionQuizUiState(
    val activeMode: RevisionQuizTabMode = RevisionQuizTabMode.REVISION_NOTES,
    // Daily Revision Notes state
    val revisionSheets: List<RevisionSubjectSheet>,
    val compoundMixSheet: RevisionSubjectSheet,
    val selectedRevisionSubjectId: String,
    val isCompoundMixEnabled: Boolean = false,
    val showDiagramLabels: Boolean = true,
    // Interactive Subject Quiz state
    val allQuestions: List<QuizQuestion>,
    val quizMode: QuizMode = QuizMode.NORMAL,
    val selectedQuizSubjectId: String = "rev_physics",
    val activeQuestionIds: List<String>,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val questionElapsedSeconds: Map<String, Int> = emptyMap(),
    val isQuizSubmitted: Boolean = false,
    val isRetryMissedSession: Boolean = false,
    val sessionNonce: Int = 0,
) {
    val activeRevisionSheet: RevisionSubjectSheet
        get() = if (isCompoundMixEnabled) {
            compoundMixSheet
        } else {
            revisionSheets.firstOrNull { it.id == selectedRevisionSubjectId }
                ?: revisionSheets.first()
        }

    val activeQuestions: List<QuizQuestion>
        get() {
            val lookup = allQuestions.associateBy { it.id }
            return activeQuestionIds.mapNotNull { lookup[it] }
        }

    val currentQuestion: QuizQuestion?
        get() = activeQuestions.getOrNull(currentQuestionIndex)

    val correctCount: Int
        get() = activeQuestions.count { q -> selectedAnswers[q.id] == q.correctOptionIndex }

    val missedQuestions: List<QuizQuestion>
        get() = activeQuestions.filter { q ->
            val picked = selectedAnswers[q.id]
            picked != null && picked != q.correctOptionIndex
        }

    val weakTopics: List<WeakTopicBreakdownItem>
        get() = missedQuestions
            .groupBy { it.subtopicTag }
            .map { (subtopic, missedList) ->
                val sample = missedList.first()
                WeakTopicBreakdownItem(
                    subtopicTag = subtopic,
                    subjectId = sample.subjectId,
                    subjectName = sample.subjectName,
                    accentMood = sample.accentMood,
                    missedCount = missedList.size,
                    focusHint = sample.explanation,
                )
            }
}
