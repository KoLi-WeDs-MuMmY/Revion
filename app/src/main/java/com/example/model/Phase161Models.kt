package com.example.model

import androidx.compose.runtime.Immutable
import com.example.ui.theme.AccentMood

enum class DailyPageRoute {
    TODAY_REVISION,
    TODAY_FLASHCARDS,
}

@Immutable
data class DailyRevisionTakeaway(
    val id: String,
    val subjectName: String,
    val accentMood: AccentMood,
    val chapterTitle: String,
    val lectureRef: String,
    val headline: String,
    val detail: String,
    val formula: String,
    val autoSavedToVault: Boolean = true,
)

@Immutable
data class DailyFlashcardItem(
    val id: String,
    val subjectName: String,
    val accentMood: AccentMood,
    val chapterTitle: String,
    val classLabel: String,
    val frontPrompt: String,
    val backAnswer: String,
    val formulaHint: String,
    val autoSavedToVault: Boolean = true,
)

@Immutable
data class HomeCommandState(
    // Honest Study Gate default: both start uncompleted until finished or toggled via test switch
    val todayRevisionCompleted: Boolean = false,
    val todayFlashcardsCompleted: Boolean = false,
    val activeDailyPage: DailyPageRoute? = null,
    val todayRevisionGeneratedLabel: String = "Generated from Yesterday's AI Log",
    val todayRevisionTakeaways: List<DailyRevisionTakeaway>,
    val todayFlashcards: List<DailyFlashcardItem>,
    val activeFlashcardIndex: Int = 0,
    val isActiveFlashcardFlipped: Boolean = false,
    val improvementPercentDelta: String = "+11.4%",
    val improvementSubtitle: String = "7-day recall & quiz accuracy lift",
    val syllabusCoverageGainPercent: String = "+6.8%",
    val syllabusGainSubtitle: String = "Weekly Primary Batch syllabus gain",
) {
    val isStudyGateUnlocked: Boolean
        get() = todayRevisionCompleted && todayFlashcardsCompleted

    val studyGateLockedReason: String
        get() = when {
            !todayRevisionCompleted && !todayFlashcardsCompleted ->
                "Complete Today's Revision & Flashcards first"
            !todayRevisionCompleted ->
                "Complete Today's Revision first"
            !todayFlashcardsCompleted ->
                "Complete Today's Flashcards first"
            else ->
                "Ready — both daily recall gates cleared"
        }

    val currentDailyFlashcard: DailyFlashcardItem?
        get() = todayFlashcards.getOrNull(activeFlashcardIndex.coerceIn(0, (todayFlashcards.size - 1).coerceAtLeast(0)))
}

enum class VaultCategorySubView(val label: String) {
    HIERARCHY_BROWSE("Browse"),
    CLASS_NOTES("Class Notes"),
    FLASHCARDS("Flashcards"),
    REVISION("Revision"),
}

enum class VaultPaletteFocus {
    SEARCH,
    FILTER,
}

@Immutable
data class VaultLectureNode(
    val id: String,
    val batchId: String,
    val subjectId: String,
    val subjectName: String,
    val subjectMood: AccentMood,
    val chapterId: String,
    val chapterTitle: String,
    val lectureNumber: Int,
    val lectureCode: String,
    val title: String,
    val durationLabel: String,
    val dateLabel: String,
    val isCompleted: Boolean,
    val isAutoSavedFromYesterday: Boolean = false,
    // Class Notes & Low-Key RAG Metadata
    val notesTitle: String,
    val notesPageCount: Int,
    val notesSummary: String,
    val notesHighlights: List<String>,
    val ragTutorAttachable: Boolean = true,
    val notebookLmUpcoming: Boolean = true,
    // Flashcards (Chapter -> Class)
    val classLabel: String,
    val flashcardDeck: List<DailyFlashcardItem>,
    // Revision (Subject -> Chapter -> Lecture)
    val revisionHeadline: String,
    val revisionFormula: String,
    val revisionKeyPoints: List<String>,
)

@Immutable
data class VaultChapterNode(
    val id: String,
    val batchId: String,
    val subjectId: String,
    val subjectName: String,
    val subjectMood: AccentMood,
    val chapterCode: String,
    val title: String,
    val lectures: List<VaultLectureNode>,
) {
    val completedLectures: Int
        get() = lectures.count { it.isCompleted }

    val totalFlashcards: Int
        get() = lectures.sumOf { it.flashcardDeck.size }
}

@Immutable
data class VaultSubjectNode(
    val id: String,
    val batchId: String,
    val name: String,
    val accentMood: AccentMood,
    val chapters: List<VaultChapterNode>,
) {
    val totalLectures: Int
        get() = chapters.sumOf { it.lectures.size }

    val completedLectures: Int
        get() = chapters.sumOf { it.completedLectures }
}

@Immutable
data class PwVaultUiState(
    val subjectsByBatch: Map<String, List<VaultSubjectNode>>,
    val selectedBatchId: String,
    val selectedSubjectId: String? = null,
    val selectedChapterId: String? = null,
    val selectedLectureId: String? = null,
    val activeSubView: VaultCategorySubView = VaultCategorySubView.HIERARCHY_BROWSE,
    // Tailored sub-view states
    val expandedClassNoteLectureId: String? = null,
    val expandedFlashcardChapterId: String? = null,
    val previewFlashcardId: String? = null,
    val isPreviewCardFlipped: Boolean = false,
    val selectedRevisionSubjectId: String? = null,
    // Per-mode Recents tracking (ordered most-recent first)
    val recentNotesLectureIds: List<String> = emptyList(),
    val recentFlashcardLectureIds: List<String> = emptyList(),
    val recentRevisionLectureIds: List<String> = emptyList(),
    // Expandable Command Palette state (Icon-triggered only)
    val isCommandPaletteOpen: Boolean = false,
    val paletteInitialFocus: VaultPaletteFocus = VaultPaletteFocus.SEARCH,
    val paletteSearchQuery: String = "",
    val paletteFilterSubjectId: String? = null,
    val paletteFilterCategory: VaultCategorySubView? = null,
) {
    val subjectsForSelectedBatch: List<VaultSubjectNode>
        get() = subjectsByBatch[selectedBatchId].orEmpty()

    val selectedSubject: VaultSubjectNode?
        get() = subjectsForSelectedBatch.firstOrNull { it.id == selectedSubjectId }

    val selectedChapter: VaultChapterNode?
        get() = selectedSubject?.chapters?.firstOrNull { it.id == selectedChapterId }

    val selectedLecture: VaultLectureNode?
        get() = selectedChapter?.lectures?.firstOrNull { it.id == selectedLectureId }

    val allLecturesInBatch: List<VaultLectureNode>
        get() = subjectsForSelectedBatch.flatMap { subj ->
            subj.chapters.flatMap { chap -> chap.lectures }
        }

    val allChaptersInBatch: List<VaultChapterNode>
        get() = subjectsForSelectedBatch.flatMap { it.chapters }

    val recentNotesLectures: List<VaultLectureNode>
        get() = resolveRecents(recentNotesLectureIds)

    val recentFlashcardLectures: List<VaultLectureNode>
        get() = resolveRecents(recentFlashcardLectureIds)

    val recentRevisionLectures: List<VaultLectureNode>
        get() = resolveRecents(recentRevisionLectureIds)

    private fun resolveRecents(ids: List<String>): List<VaultLectureNode> {
        val all = allLecturesInBatch
        val byId = all.associateBy { it.id }
        val explicit = ids.mapNotNull { byId[it] }
        val fallback = all.filter { lec -> explicit.none { it.id == lec.id } }
            .sortedByDescending { it.isAutoSavedFromYesterday }
        return (explicit + fallback).take(4)
    }

    val hasActivePaletteFilter: Boolean
        get() = paletteSearchQuery.isNotBlank() ||
            paletteFilterSubjectId != null ||
            paletteFilterCategory != null

    val paletteMatchingLectures: List<VaultLectureNode>
        get() {
            val q = paletteSearchQuery.trim()
            return allLecturesInBatch.filter { lec ->
                val matchSubj = paletteFilterSubjectId == null || lec.subjectId == paletteFilterSubjectId
                val matchQuery = q.isEmpty() ||
                    lec.title.contains(q, ignoreCase = true) ||
                    lec.chapterTitle.contains(q, ignoreCase = true) ||
                    lec.subjectName.contains(q, ignoreCase = true) ||
                    lec.notesTitle.contains(q, ignoreCase = true) ||
                    lec.revisionHeadline.contains(q, ignoreCase = true) ||
                    lec.classLabel.contains(q, ignoreCase = true)
                matchSubj && matchQuery
            }
        }
}

sealed interface BatchCrudDialog {
    data class EditBatch(
        val batchId: String,
        val initialName: String,
        val initialGoalTag: String,
        val initialTargetLabel: String,
        val initialMood: AccentMood,
    ) : BatchCrudDialog

    data class ConfirmDeleteBatch(
        val batchId: String,
        val batchName: String,
    ) : BatchCrudDialog

    data class AddSubject(
        val batchId: String,
    ) : BatchCrudDialog

    data class EditSubject(
        val batchId: String,
        val subjectId: String,
        val initialName: String,
        val initialMood: AccentMood,
    ) : BatchCrudDialog

    data class ConfirmDeleteSubject(
        val batchId: String,
        val subjectId: String,
        val subjectName: String,
    ) : BatchCrudDialog

    data class AddChapter(
        val batchId: String,
        val subjectId: String,
        val subjectName: String,
    ) : BatchCrudDialog

    data class EditChapter(
        val batchId: String,
        val subjectId: String,
        val chapterId: String,
        val initialTitle: String,
    ) : BatchCrudDialog

    data class ConfirmDeleteChapter(
        val batchId: String,
        val subjectId: String,
        val chapterId: String,
        val chapterTitle: String,
    ) : BatchCrudDialog

    data class AddLecture(
        val batchId: String,
        val subjectId: String,
        val chapterId: String,
        val chapterTitle: String,
    ) : BatchCrudDialog

    data class EditLecture(
        val batchId: String,
        val subjectId: String,
        val chapterId: String,
        val lectureId: String,
        val initialTitle: String,
        val initialDuration: String,
    ) : BatchCrudDialog

    data class ConfirmDeleteLecture(
        val batchId: String,
        val subjectId: String,
        val chapterId: String,
        val lectureId: String,
        val lectureTitle: String,
    ) : BatchCrudDialog
}
