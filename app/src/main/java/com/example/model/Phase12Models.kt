package com.example.model

import androidx.compose.runtime.Immutable
import com.example.ui.theme.AccentMood

@Immutable
data class BatchChapter(
    val id: String,
    val title: String,
    val lectureCount: Int,
    val progressPercent: Int,
    val isCompleted: Boolean,
    val materialCount: Int,
    val lectures: List<VaultLectureNode> = emptyList(),
)

@Immutable
data class BatchSubject(
    val id: String,
    val name: String,
    val accentMood: AccentMood,
    val lectureCount: Int,
    val completedLectures: Int,
    val masteryPercent: Int,
    val chapters: List<BatchChapter>,
)

@Immutable
data class StudyBatch(
    val id: String,
    val name: String,
    val goalTag: String,
    val accentMood: AccentMood,
    val targetDateLabel: String,
    val totalHoursLogged: Int,
    val subjects: List<BatchSubject>,
) {
    val totalChapters: Int
        get() = subjects.sumOf { it.chapters.size }

    val completedChapters: Int
        get() = subjects.sumOf { subject -> subject.chapters.count { it.isCompleted } }

    val overallProgressPercent: Int
        get() = if (subjects.isEmpty()) 0 else subjects.sumOf { it.masteryPercent } / subjects.size
}

@Immutable
data class PresetSubjectTemplate(
    val id: String,
    val name: String,
    val accentMood: AccentMood,
    val defaultLectures: Int,
)

enum class MaterialType(val label: String, val badgeCode: String) {
    PDF_NOTES("PDF Notes", "PDF"),
    FORMULA_SHEET("Formula Sheet", "SHEET"),
    LECTURE_DECK("Lecture Deck", "DECK"),
    PRACTICE_SET("Practice Set", "DRILL"),
}

@Immutable
data class VaultMaterial(
    val id: String,
    val batchId: String,
    val batchName: String,
    val subjectId: String,
    val subjectName: String,
    val subjectMood: AccentMood,
    val chapterTitle: String,
    val title: String,
    val type: MaterialType,
    val pageCount: Int,
    val fileSizeLabel: String,
    val lastOpenedLabel: String,
    val estimatedReadMinutes: Int,
    val summary: String,
    val outlineSections: List<String>,
)

@Immutable
data class BatchesUiState(
    val batches: List<StudyBatch>,
    val selectedBatchId: String,
    val expandedSubjectId: String? = null,
    val expandedChapterId: String? = null,
    val isBuilderOpen: Boolean = false,
    val activeCrudDialog: BatchCrudDialog? = null,
) {
    val selectedBatch: StudyBatch?
        get() = batches.firstOrNull { it.id == selectedBatchId } ?: batches.firstOrNull()
}

@Immutable
data class VaultUiState(
    val materials: List<VaultMaterial>,
    val selectedBatchId: String? = null,
    val selectedSubjectId: String? = null,
    val selectedChapter: String? = null,
    val searchQuery: String = "",
    val previewedMaterialId: String? = null,
    val openedReaderMaterialId: String? = null,
    val readerCurrentPage: Int = 1,
    val showReaderInfoDrawer: Boolean = false,
) {
    val previewedMaterial: VaultMaterial?
        get() = materials.firstOrNull { it.id == previewedMaterialId }

    val openedReaderMaterial: VaultMaterial?
        get() = materials.firstOrNull { it.id == openedReaderMaterialId }

    val isReaderOpen: Boolean
        get() = openedReaderMaterialId != null
}
