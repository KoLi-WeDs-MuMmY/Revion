package com.example.model

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AccentMood

// ─────────────────────────────────────────────────────────────────────────────
// Phase 1.6.4 — Anti-Lag Leaf Clock Tick Stream, 3D Flip-Clock Themes,
// Structured Settings, Authentic YPT Group Study & Materialized Daily Planner
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Isolated 1-second tick snapshot emitted on a dedicated StateFlow<LiveClockTick>
 * in WorkspaceViewModel so root layouts, Navigation Rail, and non-clock cards
 * NEVER recompose on timer ticks.
 */
@Immutable
data class LiveClockTick(
    val elapsedSeconds: Int = 0,
    val remainingSeconds: Int = 50 * 60,
    val peerElapsedOffsetSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val mode: StudyTimerMode = StudyTimerMode.POMODORO,
    val pomodoroPhase: PomodoroPhase = PomodoroPhase.FOCUS,
    val focusDurationMinutes: Int = 50,
    val breakDurationMinutes: Int = 10,
    val timerSource: TimerSessionSource = TimerSessionSource.MANUAL,
) {
    val isActiveSession: Boolean get() = isRunning || isPaused

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
}

@Immutable
data class ClockThemePalette(
    val housingBackground: Color,
    val housingBorder: Color,
    val topHalfSurface: Color,
    val bottomHalfSurface: Color,
    val tileBorder: Color,
    val creaseLine: Color,
    val hingeNotch: Color,
    val digitInk: Color,
    val subLabelInk: Color,
    val accentColor: Color,
)

enum class ClockThemeId(
    val id: String,
    val label: String,
    val subtitle: String,
    val palette: ClockThemePalette,
) {
    OBSIDIAN_FLIP(
        id = "obsidian_flip",
        label = "Obsidian Flip",
        subtitle = "Matte carbon split-flaps & brushed steel crease",
        palette = ClockThemePalette(
            housingBackground = Color(0xFF0B0C0F),
            housingBorder = Color(0xFF252830),
            topHalfSurface = Color(0xFF191B20),
            bottomHalfSurface = Color(0xFF13151A),
            tileBorder = Color(0xFF2B2F38),
            creaseLine = Color(0xFF060709),
            hingeNotch = Color(0xFF3A3F4B),
            digitInk = Color(0xFFF4F5F7),
            subLabelInk = Color(0xFF9AA1AE),
            accentColor = Color(0xFF7DD3FC),
        ),
    ),
    WARM_TERRACOTTA_FLIP(
        id = "warm_terracotta_flip",
        label = "Warm Terracotta Flip",
        subtitle = "Espresso mechanical tiles & warm clay accent",
        palette = ClockThemePalette(
            housingBackground = Color(0xFF171311),
            housingBorder = Color(0xFF3A2E28),
            topHalfSurface = Color(0xFF261F1B),
            bottomHalfSurface = Color(0xFF1F1916),
            tileBorder = Color(0xFF47372F),
            creaseLine = Color(0xFF100C0A),
            hingeNotch = Color(0xFF6B4E3F),
            digitInk = Color(0xFFFAF3EB),
            subLabelInk = Color(0xFFC5B1A3),
            accentColor = Color(0xFFD97757),
        ),
    ),
    PAPER_IVORY_FLIP(
        id = "paper_ivory_flip",
        label = "Paper Ivory Flip",
        subtitle = "Tactile ivory parchment cards & deep graphite ink",
        palette = ClockThemePalette(
            housingBackground = Color(0xFFECE6DB),
            housingBorder = Color(0xFFD1C8B9),
            topHalfSurface = Color(0xFFFAF7F0),
            bottomHalfSurface = Color(0xFFF1ECE1),
            tileBorder = Color(0xFFD5CCB9),
            creaseLine = Color(0xFFC4B9A5),
            hingeNotch = Color(0xFFA89C87),
            digitInk = Color(0xFF1C1A17),
            subLabelInk = Color(0xFF635B50),
            accentColor = Color(0xFFC85A32),
        ),
    ),
    SLATE_STEEL_FLIP(
        id = "slate_steel_flip",
        label = "Slate Steel Flip",
        subtitle = "Cool gunmetal anodized plates & glacier readout",
        palette = ClockThemePalette(
            housingBackground = Color(0xFF0F1622),
            housingBorder = Color(0xFF28374E),
            topHalfSurface = Color(0xFF1B273A),
            bottomHalfSurface = Color(0xFF152030),
            tileBorder = Color(0xFF314460),
            creaseLine = Color(0xFF0A1019),
            hingeNotch = Color(0xFF476082),
            digitInk = Color(0xFFEAF2FA),
            subLabelInk = Color(0xFF94ABC4),
            accentColor = Color(0xFF38BDF8),
        ),
    ),
    AMOLED_PURE_FLIP(
        id = "amoled_pure_flip",
        label = "AMOLED Pure Flip",
        subtitle = "True #000000 zero-glow tiles & crisp white numerals",
        palette = ClockThemePalette(
            housingBackground = Color(0xFF000000),
            housingBorder = Color(0xFF1E1E22),
            topHalfSurface = Color(0xFF0C0C0E),
            bottomHalfSurface = Color(0xFF050507),
            tileBorder = Color(0xFF242429),
            creaseLine = Color(0xFF000000),
            hingeNotch = Color(0xFF383840),
            digitInk = Color(0xFFFFFFFF),
            subLabelInk = Color(0xFF8E8E96),
            accentColor = Color(0xFFE4E4E7),
        ),
    );
}

enum class MotionIntensity(
    val label: String,
    val subtitle: String,
    val dampingRatio: Float,
    val stiffness: Float,
) {
    CRISP("Crisp", "Snappy mechanical springs (recommended)", 0.84f, 420f),
    GENTLE("Gentle", "Softer relaxed spatial glide", 0.90f, 280f),
    REDUCED("Reduced", "Minimal motion for battery & focus", 1.0f, 900f);

    fun <T> springSpec(): SpringSpec<T> = spring(
        dampingRatio = dampingRatio,
        stiffness = stiffness,
    )
}

enum class LayoutDensityMode(val label: String, val subtitle: String) {
    ADAPTIVE_AUTO("Auto 16:9 / 9:16", "Switches automatically with screen orientation"),
    COMPACT_FOCUS("Compact Focus", "Tighter card spacing for more visible syllabus"),
}

enum class SubjectIndicatorStyle(val label: String, val subtitle: String) {
    DOT_PILL("Dot + Pill", "Color dot paired with soft subject tag"),
    LEFT_BAR("Left Accent Bar", "Subtle vertical stripe along subject rows"),
    MINIMAL_DOT("Minimal Dot", "Quiet 6dp mineral dot only"),
}

@Immutable
data class AppSettingsPreferences(
    val clockTheme: ClockThemeId = ClockThemeId.OBSIDIAN_FLIP,
    val motionIntensity: MotionIntensity = MotionIntensity.CRISP,
    val layoutDensity: LayoutDensityMode = LayoutDensityMode.ADAPTIVE_AUTO,
    val defaultFocusMinutes: Int = 50,
    val defaultBreakMinutes: Int = 10,
    val autoLogStudySessions: Boolean = true,
    val subjectIndicatorStyle: SubjectIndicatorStyle = SubjectIndicatorStyle.DOT_PILL,
    val manualTaskEditCap: Int = 3,
    val showResetDemoConfirmDialog: Boolean = false,
)

// ─────────────────────────────────────────────────────────────────────────────
// Home Rectangular Clock Cascading Tag Picker (Batch -> Subject -> Chapter -> Lecture)
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class HomeClockTagPickerState(
    val isOpen: Boolean = false,
    val launchFullScreenOnConfirm: Boolean = false,
    val selectedBatchId: String = "batch_jee_2026",
    val selectedSubjectId: String? = null,
    val selectedChapterId: String? = null,
    val selectedLectureId: String? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Zero-Ratio Smooth Split Postures & Authentic YPT Allowed Apps
// ─────────────────────────────────────────────────────────────────────────────

enum class SplitPosture(val timerFraction: Float, val semanticLabel: String) {
    FULL_TIMER(1.0f, "Timer Focus"),
    WIDE_TIMER(0.72f, "Wide Timer"),
    BALANCED_SPLIT(0.50f, "Split View"),
    WIDE_GROUP(0.28f, "Wide Group"),
    FULL_GROUP(0.0f, "Group Room");

    val isSplitActive: Boolean get() = this != FULL_TIMER && this != FULL_GROUP

    companion object {
        fun nearestTo(fraction: Float): SplitPosture {
            val clamped = fraction.coerceIn(0f, 1f)
            return entries.minBy { kotlin.math.abs(it.timerFraction - clamped) }
        }
    }
}

@Immutable
data class YptAllowedApp(
    val id: String,
    val name: String,
    val category: String,
    val iconKey: String,
    val isAllowed: Boolean = true,
)

// ─────────────────────────────────────────────────────────────────────────────
// Materialized Daily Planner — Heatmap, Appeals, 3-Edit Tasks & Zero-Context AI Drawer
// ─────────────────────────────────────────────────────────────────────────────

@Immutable
data class PlannerHeatmapDay(
    val dayIndex: Int,
    val shortDate: String,
    val studyHours: Float,
    val taskCompletionPercent: Int,
    /** 0 = rest, 1 = light, 2 = steady, 3 = strong, 4 = peak */
    val intensityLevel: Int,
)

enum class PlannerAppealOutcome(val label: String) {
    PENDING("Ready to Submit"),
    APPROVED_UPDATED("Approved & Updated"),
    DECLINED("Declined by AI"),
}

@Immutable
data class PlannerAppealRequest(
    val blockId: String,
    val blockTitle: String,
    val blockSubject: String,
    val blockTimeWindow: String,
    val blockDurationMinutes: Int,
    val selectedPresetReason: String = "Need lighter load in this slot",
    val customReason: String = "",
    val outcome: PlannerAppealOutcome = PlannerAppealOutcome.PENDING,
    val aiVerdictTitle: String = "",
    val aiVerdictExplanation: String = "",
    val updatedTimeWindow: String? = null,
    val updatedDurationMinutes: Int? = null,
)

@Immutable
data class PlannerUserTask(
    val id: String,
    val title: String,
    val subjectName: String,
    val accentMood: AccentMood,
    val slottedTimeWindow: String,
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val aiSlotNote: String = "AI-slotted inside peak window",
    val editCount: Int = 0,
)

@Immutable
data class PlannerAiSideMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val tagLabel: String,
)
