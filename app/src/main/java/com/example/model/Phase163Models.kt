package com.example.model

import androidx.compose.runtime.Immutable
import com.example.ui.theme.AccentMood

// ─────────────────────────────────────────────────────────────────────────────
// Phase 1.6.3 — Standalone Daily Planner (Dual RAGs, Zero-Context, AI Gate)
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class DailyTelemetryEntry(
    val dayIndex: Int,
    val dayLabel: String,
    val dateTag: String,
    val screenTimeHours: Float,
    val studyHours: Float,
)

@Immutable
data class PlannerRag1State(
    val daily20Entries: List<DailyTelemetryEntry>,
    val oneMonthAvgScreenHours: Float,
    val oneMonthAvgStudyHours: Float,
    val twoMonthAvgScreenHours: Float,
    val twoMonthAvgStudyHours: Float,
    val sixMonthAvgScreenHours: Float,
    val sixMonthAvgStudyHours: Float,
    val isFirstTimeCompacted: Boolean = true,
    val lastOptimizedLabel: String = "Auto-optimized every 3 days · Synced today",
    val compactedSummaryText: String,
    val rawEstimatedTokens: Int = 1840,
    val compactedEstimatedTokens: Int = 215,
)

@Immutable
data class PlannerRag2State(
    val activeHoursWindow: String,
    val peakFocusWindow: String,
    val taskCompletedPercent: Int,
    val focusEfficiencyNote: String,
    val lastGeneratedLabel: String = "App-generated via AI · Today 06:30",
)

@Immutable
data class StandalonePlannerTask(
    val id: String,
    val title: String,
    val subjectName: String,
    val accentMood: AccentMood,
    val timeWindow: String,
    val durationMinutes: Int,
    val priorityTag: String,
    val isCompleted: Boolean = false,
    val aiStatusNote: String = "Aligned with peak focus window",
)

enum class PlannerAiGateActionType(val label: String) {
    CONFIRM_SCHEDULE("Confirm Daily Schedule"),
    EDIT_TASK("Edit Scheduled Task"),
    CANCEL_TASK("Cancel Scheduled Task"),
    ADD_TASK("Add New Task"),
}

enum class PlannerAiDecisionOutcome(val badge: String) {
    PENDING("AWAITING AI CHECK"),
    APPROVED("AI APPROVED"),
    COUNTER_PROPOSAL("AI COUNTER-PROPOSAL"),
    DENIED("AI DENIED"),
}

@Immutable
data class PlannerAiGateRequest(
    val actionType: PlannerAiGateActionType,
    val targetTaskId: String? = null,
    val proposedTitle: String,
    val proposedSubject: String,
    val proposedTimeWindow: String,
    val proposedDurationMinutes: Int,
    val userReason: String = "",
    val aiOutcome: PlannerAiDecisionOutcome = PlannerAiDecisionOutcome.PENDING,
    val aiReasoning: String = "",
    val counterTitle: String? = null,
    val counterTimeWindow: String? = null,
    val counterDurationMinutes: Int? = null,
)

@Immutable
data class StandalonePlannerUiState(
    val isAiEnabled: Boolean = true,
    val isScheduleConfirmed: Boolean = false,
    val windowSessionNumber: Int = 1,
    val zeroContextTurnCount: Int = 0,
    val rag1: PlannerRag1State,
    val rag2: PlannerRag2State,
    val tasks: List<StandalonePlannerTask>,
    val pendingGate: PlannerAiGateRequest? = null,
    val statusNotice: String? = null,
) {
    val liveTaskCompletionPercent: Int
        get() {
            if (tasks.isEmpty()) return 0
            return (tasks.count { it.isCompleted } * 100) / tasks.size
        }
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 1.6.3 — Synced Study Timer & YPT Group Study
// ─────────────────────────────────────────────────────────────────────────────

enum class StudyTimerMode(val label: String) {
    POMODORO("Pomodoro"),
    STOPWATCH("Stopwatch"),
}

enum class PomodoroRingStyle(val label: String, val subtitle: String) {
    MINIMAL_HALO("Minimal Halo", "Clean continuous luminous arc"),
    SEGMENTED_DIAL("Tick Dial", "60-tick precision chronometer"),
    DUAL_ORBITAL("Dual Orbital", "Focus orbit + inner cycle ring"),
    ARCHITECTURAL_RING("Quadrant", "Crisp geometric cardinal frame"),
}

enum class PomodoroPhase(val label: String) {
    FOCUS("FOCUS"),
    BREAK("BREAK"),
}

enum class TimerSplitRatio(
    val timerFraction: Float,
    val label: String,
) {
    RATIO_100_0(1.0f, "100:0"),
    RATIO_75_25(0.75f, "75:25"),
    RATIO_50_50(0.50f, "50:50"),
    RATIO_25_75(0.25f, "25:75"),
    RATIO_0_100(0.0f, "0:100");

    val groupFraction: Float get() = (1f - timerFraction).coerceIn(0f, 1f)
    val isHighTimerView: Boolean get() = this == RATIO_100_0 || this == RATIO_75_25

    companion object {
        fun nearestTo(fraction: Float): TimerSplitRatio {
            val clamped = fraction.coerceIn(0f, 1f)
            return entries.minBy { kotlin.math.abs(it.timerFraction - clamped) }
        }
    }
}

enum class TimerSessionSource(val label: String) {
    MANUAL("Study Timer"),
    AUTO_REVISION("Auto · Revision"),
    AUTO_FLASHCARDS("Auto · Flashcards"),
}

enum class YptMemberStatus(val label: String) {
    FOCUSING("Focusing"),
    IN_REVISION("In Revision"),
    ON_BREAK("On Break"),
    IDLE("Idle"),
}

@Immutable
data class StudySessionLogEntry(
    val id: String,
    val subjectName: String,
    val accentMood: AccentMood,
    val syncedLectureId: String? = null,
    val syncedLectureTitle: String? = null,
    val durationSeconds: Int,
    val modeLabel: String,
    val source: TimerSessionSource,
    val timestampLabel: String,
)

@Immutable
data class RawDailyStudyPoint(
    val dayIndex: Int,
    val shortDate: String,
    val totalMinutes: Int,
)

@Immutable
data class StudyLogRagState(
    val past20DaysIndividualMinutes: List<RawDailyStudyPoint>,
    val oneMonthCombinedAvgMinutes: Int,
    val sixMonthCombinedAvgMinutes: Int,
    val lastAutoOptimizedLabel: String = "Auto-optimized every 3 days · Last: 1 day ago",
    val compactedSummary: String,
    val totalLoggedSessions: Int,
)

@Immutable
data class YptGroupMember(
    val id: String,
    val displayName: String,
    val handle: String,
    val status: YptMemberStatus,
    val subjectTag: String,
    val chapterTag: String,
    val todayFocusSeconds: Int,
    val accentMood: AccentMood,
    val isCurrentUser: Boolean = false,
)

@Immutable
data class YptStudyGroup(
    val id: String,
    val name: String,
    val examCategory: String,
    val goalSummary: String,
    val dailyTargetHours: Int,
    val isPublic: Boolean = true,
    val members: List<YptGroupMember>,
) {
    val activeMemberCount: Int
        get() = members.count { it.status == YptMemberStatus.FOCUSING || it.status == YptMemberStatus.IN_REVISION }

    val totalGroupSecondsToday: Int
        get() = members.sumOf { it.todayFocusSeconds }
}

@Immutable
data class StudyTimerGroupUiState(
    val mode: StudyTimerMode = StudyTimerMode.POMODORO,
    val pomodoroStyle: PomodoroRingStyle = PomodoroRingStyle.MINIMAL_HALO,
    val pomodoroPhase: PomodoroPhase = PomodoroPhase.FOCUS,
    val focusDurationMinutes: Int = 50,
    val breakDurationMinutes: Int = 10,
    val completedCycles: Int = 1,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val elapsedSeconds: Int = 0,
    val remainingSeconds: Int = 50 * 60,
    val timerSource: TimerSessionSource = TimerSessionSource.MANUAL,
    val activeSubjectId: String? = null,
    val activeSubjectName: String? = null,
    val activeSubjectMood: AccentMood = AccentMood.CYAN,
    val syncedLectureId: String? = null,
    val syncedLectureTitle: String? = null,
    val splitRatio: TimerSplitRatio = TimerSplitRatio.RATIO_75_25,
    val lastSplitRatioBeforeToggle: TimerSplitRatio = TimerSplitRatio.RATIO_25_75,
    val showStartTagDialog: Boolean = false,
    val showStatsSheet: Boolean = false,
    val showCreateGroupDialog: Boolean = false,
    val singleTimerLockNotice: String? = null,
    val todaySubjectSeconds: Map<String, Int> = emptyMap(),
    val lectureTrackedSeconds: Map<String, Int> = emptyMap(),
    val recentSessionLogs: List<StudySessionLogEntry> = emptyList(),
    val rawDailyStudyHistory: List<RawDailyStudyPoint> = emptyList(),
    val studyLogRag: StudyLogRagState,
    val groups: List<YptStudyGroup> = emptyList(),
    val joinedGroupId: String = "",
    val userStatus: YptMemberStatus = YptMemberStatus.IDLE,
    val userChapterTag: String = "Gauss's Law & Flux",
) {
    val isTimerActiveSession: Boolean
        get() = isRunning || isPaused

    /** True when the user started a manual study timer — enforces Vault-only access & collapsed nav. */
    val isManualTimerLocked: Boolean
        get() = isTimerActiveSession && timerSource == TimerSessionSource.MANUAL

    val isAutoTimerActive: Boolean
        get() = isRunning && timerSource != TimerSessionSource.MANUAL

    val displaySeconds: Int
        get() = if (mode == StudyTimerMode.POMODORO && timerSource == TimerSessionSource.MANUAL) {
            remainingSeconds.coerceAtLeast(0)
        } else {
            elapsedSeconds.coerceAtLeast(0)
        }

    val pomodoroProgress: Float
        get() {
            val totalSec = (if (pomodoroPhase == PomodoroPhase.FOCUS) focusDurationMinutes else breakDurationMinutes)
                .coerceAtLeast(1) * 60
            return (1f - (remainingSeconds.toFloat() / totalSec.toFloat())).coerceIn(0f, 1f)
        }

    val todayTotalStudySeconds: Int
        get() = todaySubjectSeconds.values.sum() + (if (isTimerActiveSession) elapsedSeconds else 0)

    val highestStudyDay: RawDailyStudyPoint?
        get() = rawDailyStudyHistory.maxByOrNull { it.totalMinutes }

    val lowestStudyDay: RawDailyStudyPoint?
        get() = rawDailyStudyHistory.minByOrNull { it.totalMinutes }

    val joinedGroup: YptStudyGroup?
        get() = groups.firstOrNull { it.id == joinedGroupId } ?: groups.firstOrNull()

    fun liveSubjectSecondsToday(subjectName: String): Int {
        val base = todaySubjectSeconds[subjectName] ?: 0
        return if (isTimerActiveSession && activeSubjectName.equals(subjectName, ignoreCase = true)) {
            base + elapsedSeconds
        } else {
            base
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase 1.6.3 — Restructured Settings & Centralized RAG AI Optimizer
// ─────────────────────────────────────────────────────────────────────────────

enum class SettingsSectionTab(val label: String) {
    THEMES_DISPLAY("Themes & Display"),
    RAG_FILES_AI("RAG Files & AI"),
}

enum class AppRagFileId(
    val displayName: String,
    val fileName: String,
    val categoryBadge: String,
    val cadenceNote: String,
) {
    PLANNER_RAG_1(
        displayName = "Planner RAG (1) · Multi-Horizon Telemetry",
        fileName = "planner_rag_1_telemetry.json",
        categoryBadge = "PLANNER RAG 1",
        cadenceNote = "20d individual + 1m/2m avg + 6m avg · Auto-optimized every 3d",
    ),
    PLANNER_RAG_2(
        displayName = "Planner RAG (2) · Live Performance",
        fileName = "planner_rag_2_performance.json",
        categoryBadge = "PLANNER RAG 2",
        cadenceNote = "App-generated via AI · Active hours, peak hours & task %",
    ),
    STUDY_LOG_RAG(
        displayName = "Study Log RAG · Sessions & Subject Totals",
        fileName = "study_log_rag_sessions.json",
        categoryBadge = "STUDY LOG RAG",
        cadenceNote = "Auto-optimized every 3d · Raw daily high/low preserved separately",
    ),
    CHAPTER_DIAGRAM_RAG(
        displayName = "Chapter Diagram RAG · Private WebView Index",
        fileName = "filesDir/diagrams/index.json",
        categoryBadge = "DIAGRAM RAG",
        cadenceNote = "Scoped chapter filenames for AI Tutor Image-Code Web Viewer",
    ),
}

@Immutable
data class RagAiOptimizationResult(
    val ragId: AppRagFileId,
    val optimizedAtLabel: String,
    val tokenReductionLabel: String,
    val dataFirstPriorityNote: String,
    val compactedSummary: String,
    val recommendations: List<String>,
)

@Immutable
data class SettingsRagHubState(
    val selectedSection: SettingsSectionTab = SettingsSectionTab.THEMES_DISPLAY,
    val selectedRagId: AppRagFileId = AppRagFileId.PLANNER_RAG_1,
    val optimizationResults: Map<AppRagFileId, RagAiOptimizationResult> = emptyMap(),
    val userNoteInput: String = "",
)

fun formatDurationHms(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val h = safe / 3600
    val m = (safe % 3600) / 60
    val s = safe % 60
    return if (h > 0) {
        "%02d:%02d:%02d".format(h, m, s)
    } else {
        "%02d:%02d".format(m, s)
    }
}

fun formatDurationCompactHm(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val h = safe / 3600
    val m = (safe % 3600) / 60
    val s = safe % 60
    return when {
        h > 0 -> "${h}h ${m.toString().padStart(2, '0')}m"
        m > 0 -> "${m}m ${s.toString().padStart(2, '0')}s"
        else -> "${s}s"
    }
}

fun formatMinutesHm(totalMinutes: Int): String {
    val safe = totalMinutes.coerceAtLeast(0)
    val h = safe / 60
    val m = safe % 60
    return if (h > 0) "${h}h ${m.toString().padStart(2, '0')}m" else "${m}m"
}
