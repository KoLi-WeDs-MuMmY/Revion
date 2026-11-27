package com.example.model

import androidx.compose.runtime.Immutable
import com.example.ui.theme.AccentMood

enum class TutorPlannerMode {
    AI_TUTOR,
    DAILY_PLANNER,
}

@Immutable
data class RagChapterContext(
    val id: String,
    val subjectName: String,
    val accentMood: AccentMood,
    val chapterTitle: String,
    val pdfFileName: String,
    val pageCount: Int,
    val sectionTags: List<String>,
    val memoryTurnsCount: Int,
    val quickPrompts: List<String>,
    val summaryNote: String,
)

@Immutable
data class TutorCitation(
    val pageLabel: String,
    val sectionTag: String,
)

@Immutable
data class TutorMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestampLabel: String,
    val citations: List<TutorCitation> = emptyList(),
    val formulaBlock: String? = null,
    val isPlannerAuditNote: Boolean = false,
)

enum class PlannerBlockCategory(
    val label: String,
    val badgeCode: String,
) {
    STUDY("Study", "STUDY"),
    REVISION("Revision", "REV x-1"),
    QUIZ("Quiz", "QUIZ"),
    FLASHCARDS("Flashcards", "CARDS"),
    BREAK_MEAL("Break / Meal", "BREAK"),
}

enum class PlannerApprovalStatus(
    val label: String,
) {
    AI_SCHEDULED("AI Scheduled"),
    AI_APPROVED("AI Approved"),
    PENDING_REVIEW("Pending AI"),
}

@Immutable
data class PlannerBlock(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: PlannerBlockCategory,
    val subjectName: String,
    val accentMood: AccentMood,
    val startMinutes: Int,
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val isCriticalPriority: Boolean = false,
    val approvalStatus: PlannerApprovalStatus = PlannerApprovalStatus.AI_SCHEDULED,
) {
    val endMinutes: Int
        get() = startMinutes + durationMinutes

    val formattedTimeRange: String
        get() = "${formatClockMinutes(startMinutes)} – ${formatClockMinutes(endMinutes)}"
}

enum class PermissionRequestAction(val label: String) {
    ADD_BLOCK("Add New Block"),
    EDIT_BLOCK("Reschedule / Edit Block"),
    DELETE_BLOCK("Remove / Skip Block"),
}

enum class PermissionVerdictPhase {
    DRAFTING,
    EVALUATING,
    APPROVED,
    BLOCKED,
}

@Immutable
data class CounterProposal(
    val title: String,
    val category: PlannerBlockCategory,
    val startMinutes: Int,
    val durationMinutes: Int,
    val summary: String,
)

@Immutable
data class AiPermissionRequest(
    val targetBlockId: String?,
    val targetBlockTitle: String?,
    val targetIsCritical: Boolean,
    val action: PermissionRequestAction,
    val proposedTitle: String,
    val proposedCategory: PlannerBlockCategory,
    val proposedStartMinutes: Int,
    val proposedDurationMinutes: Int,
    val reasonText: String = "",
    val phase: PermissionVerdictPhase = PermissionVerdictPhase.DRAFTING,
    val verdictHeadline: String = "",
    val verdictExplanation: String = "",
    val counterProposal: CounterProposal? = null,
)

@Immutable
data class TutorPlannerUiState(
    val activeMode: TutorPlannerMode = TutorPlannerMode.AI_TUTOR,
    val isCoPlanSplitEnabled: Boolean = false,
    // Chapter RAG AI Tutor state
    val chapters: List<RagChapterContext>,
    val selectedChapterId: String,
    val messagesByChapter: Map<String, List<TutorMessage>>,
    val isTutorTyping: Boolean = false,
    // Wake-to-Sleep Adaptive Planner state
    val manualWakeMinutes: Int = 360, // 06:00 baseline from first-login onboarding
    val manualSleepMinutes: Int = 1380, // 23:00 baseline from first-login onboarding
    val observedUnlockMinutes: Int = 375, // 06:15 mock observed first unlock
    val observedScreenOffMinutes: Int = 1390, // 23:10 mock observed last screen-off
    val isAdaptiveSyncEnabled: Boolean = true,
    val isScheduleSettingsOpen: Boolean = false,
    val blocks: List<PlannerBlock>,
    val activePermissionRequest: AiPermissionRequest? = null,
    val remainingSyllabusChapters: Int = 14,
    val totalSyllabusChapters: Int = 56,
) {
    val selectedChapter: RagChapterContext
        get() = chapters.firstOrNull { it.id == selectedChapterId } ?: chapters.first()

    val currentMessages: List<TutorMessage>
        get() = messagesByChapter[selectedChapter.id].orEmpty()

    val effectiveWakeMinutes: Int
        get() = if (isAdaptiveSyncEnabled) observedUnlockMinutes else manualWakeMinutes

    val effectiveSleepMinutes: Int
        get() = if (isAdaptiveSyncEnabled) observedScreenOffMinutes else manualSleepMinutes

    val awakeDurationLabel: String
        get() {
            val totalMins = (effectiveSleepMinutes - effectiveWakeMinutes).coerceAtLeast(60)
            val hrs = totalMins / 60
            val mins = totalMins % 60
            return if (mins == 0) "${hrs}h awake" else "${hrs}h ${mins}m awake"
        }

    val completedBlockCount: Int
        get() = blocks.count { it.isCompleted }

    val plannerCompletionPercent: Int
        get() = if (blocks.isEmpty()) 0 else ((completedBlockCount * 100f) / blocks.size).toInt()

    val syllabusCompletedPercent: Int
        get() = if (totalSyllabusChapters == 0) {
            0
        } else {
            (((totalSyllabusChapters - remainingSyllabusChapters) * 100f) / totalSyllabusChapters).toInt()
        }
}

fun formatClockMinutes(totalMinutes: Int): String {
    val wrapped = ((totalMinutes % 1440) + 1440) % 1440
    val hours = wrapped / 60
    val mins = wrapped % 60
    return "%02d:%02d".format(hours, mins)
}
