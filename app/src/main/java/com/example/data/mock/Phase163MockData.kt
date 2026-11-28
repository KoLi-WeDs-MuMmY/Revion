package com.example.data.mock

import com.example.model.AppRagFileId
import com.example.model.DailyTelemetryEntry
import com.example.model.PlannerAiSideMessage
import com.example.model.PlannerHeatmapDay
import com.example.model.PlannerRag1State
import com.example.model.PlannerRag2State
import com.example.model.PlannerUserTask
import com.example.model.PomodoroRingStyle
import com.example.model.RagAiOptimizationResult
import com.example.model.RawDailyStudyPoint
import com.example.model.SettingsRagHubState
import com.example.model.SplitPosture
import com.example.model.StandalonePlannerTask
import com.example.model.StandalonePlannerUiState
import com.example.model.StudyLogRagState
import com.example.model.StudySessionLogEntry
import com.example.model.StudyTimerGroupUiState
import com.example.model.StudyTimerMode
import com.example.model.TimerSessionSource
import com.example.model.TimerSplitRatio
import com.example.model.WorkspaceUiState
import com.example.model.YptAllowedApp
import com.example.model.YptGroupMember
import com.example.model.YptMemberStatus
import com.example.model.YptStudyGroup
import com.example.model.formatDurationCompactHm
import com.example.model.formatMinutesHm
import com.example.ui.theme.AccentMood

object Phase163MockData {

    fun createInitialPlannerState(): StandalonePlannerUiState {
        val daily20 = listOf(
            DailyTelemetryEntry(1, "D-20", "Nov 08", 5.8f, 4.1f),
            DailyTelemetryEntry(2, "D-19", "Nov 09", 4.9f, 5.2f),
            DailyTelemetryEntry(3, "D-18", "Nov 10", 6.2f, 3.8f),
            DailyTelemetryEntry(4, "D-17", "Nov 11", 4.5f, 5.6f),
            DailyTelemetryEntry(5, "D-16", "Nov 12", 4.1f, 6.1f),
            DailyTelemetryEntry(6, "D-15", "Nov 13", 3.9f, 6.4f),
            DailyTelemetryEntry(7, "D-14", "Nov 14", 6.8f, 3.2f),
            DailyTelemetryEntry(8, "D-13", "Nov 15", 5.1f, 5.0f),
            DailyTelemetryEntry(9, "D-12", "Nov 16", 4.4f, 5.8f),
            DailyTelemetryEntry(10, "D-11", "Nov 17", 4.0f, 6.5f),
            DailyTelemetryEntry(11, "D-10", "Nov 18", 3.6f, 7.1f),
            DailyTelemetryEntry(12, "D-09", "Nov 19", 4.2f, 5.9f),
            DailyTelemetryEntry(13, "D-08", "Nov 20", 5.5f, 4.6f),
            DailyTelemetryEntry(14, "D-07", "Nov 21", 3.4f, 7.75f),
            DailyTelemetryEntry(15, "D-06", "Nov 22", 6.9f, 1.85f),
            DailyTelemetryEntry(16, "D-05", "Nov 23", 4.3f, 5.5f),
            DailyTelemetryEntry(17, "D-04", "Nov 24", 3.9f, 6.2f),
            DailyTelemetryEntry(18, "D-03", "Nov 25", 4.1f, 5.9f),
            DailyTelemetryEntry(19, "D-02", "Nov 26", 3.7f, 6.4f),
            DailyTelemetryEntry(20, "D-01", "Nov 27", 3.5f, 6.8f),
        )

        val rag1 = PlannerRag1State(
            daily20Entries = daily20,
            oneMonthAvgScreenHours = 4.6f,
            oneMonthAvgStudyHours = 5.4f,
            twoMonthAvgScreenHours = 4.9f,
            twoMonthAvgStudyHours = 5.1f,
            sixMonthAvgScreenHours = 5.3f,
            sixMonthAvgStudyHours = 4.7f,
            isFirstTimeCompacted = true,
            lastOptimizedLabel = "Auto-optimized every 3 days · Last: Today 06:05",
            compactedSummaryText = "20d trend: Study rose +24% (avg 5.4h/d) while screen drift fell to 4.6h/d. " +
                "1-2m combined avg: 5.1h study / 4.9h screen. 6m baseline avg: 4.7h study / 5.3h screen. " +
                "Highest retention occurs when heavy Physics/Maths runs before 11:30 AM and screen time stays under 4.0h.",
            rawEstimatedTokens = 1860,
            compactedEstimatedTokens = 210,
        )

        val tasks = listOf(
            StandalonePlannerTask(
                id = "sp_task_1",
                title = "Yesterday's Revision + Flashcards Gate",
                subjectName = "Revision",
                accentMood = AccentMood.EMERALD,
                timeWindow = "06:45 – 07:20",
                durationMinutes = 35,
                priorityTag = "GATE FIRST",
                isCompleted = true,
                aiStatusNote = "Completed inside morning wake window (RAG 2)",
            ),
            StandalonePlannerTask(
                id = "sp_task_2",
                title = "Physics · Gauss's Law & Spherical Shells Drill",
                subjectName = "Physics",
                accentMood = AccentMood.CYAN,
                timeWindow = "07:30 – 09:10",
                durationMinutes = 100,
                priorityTag = "PEAK FOCUS",
                isCompleted = true,
                aiStatusNote = "Placed inside peak focus window 07:30–11:00",
            ),
            StandalonePlannerTask(
                id = "sp_task_3",
                title = "Chemistry · Chemical Kinetics Arrhenius Numericals",
                subjectName = "Chemistry",
                accentMood = AccentMood.VIOLET,
                timeWindow = "09:30 – 11:00",
                durationMinutes = 90,
                priorityTag = "CORE SYLLABUS",
                isCompleted = false,
                aiStatusNote = "Matched to 2nd peak slot before noon dip",
            ),
            StandalonePlannerTask(
                id = "sp_task_4",
                title = "Maths · Definite Integration King's Property PYQs",
                subjectName = "Maths",
                accentMood = AccentMood.AMBER,
                timeWindow = "18:30 – 20:00",
                durationMinutes = 90,
                priorityTag = "WEAK TOPIC",
                isCompleted = false,
                aiStatusNote = "Scheduled in evening peak to address 66% mastery",
            ),
            StandalonePlannerTask(
                id = "sp_task_5",
                title = "Biology · DNA Replication Enzyme Recall Quiz",
                subjectName = "Biology",
                accentMood = AccentMood.EMERALD,
                timeWindow = "20:30 – 21:20",
                durationMinutes = 50,
                priorityTag = "CONSOLIDATION",
                isCompleted = false,
                aiStatusNote = "Low-friction active recall before 23:05 sleep cutoff",
            ),
        )

        val rag2 = PlannerRag2State(
            activeHoursWindow = "06:15 – 23:05 (16h 50m awake)",
            peakFocusWindow = "07:30 – 11:00 & 18:30 – 21:00",
            taskCompletedPercent = (tasks.count { it.isCompleted } * 100) / tasks.size,
            focusEfficiencyNote = "91% completion during morning peaks vs. 54% after 21:30. Protect pre-noon numerical blocks.",
            lastGeneratedLabel = "App-generated via AI · Today 06:30",
        )

        val heatmap28 = listOf(
            PlannerHeatmapDay(1, "Oct 31", 4.2f, 75, 2),
            PlannerHeatmapDay(2, "Nov 01", 5.6f, 85, 3),
            PlannerHeatmapDay(3, "Nov 02", 2.1f, 45, 1),
            PlannerHeatmapDay(4, "Nov 03", 6.4f, 92, 4),
            PlannerHeatmapDay(5, "Nov 04", 5.1f, 80, 3),
            PlannerHeatmapDay(6, "Nov 05", 4.8f, 78, 2),
            PlannerHeatmapDay(7, "Nov 06", 6.9f, 95, 4),
            PlannerHeatmapDay(8, "Nov 07", 3.2f, 60, 1),
            PlannerHeatmapDay(9, "Nov 08", 4.1f, 72, 2),
            PlannerHeatmapDay(10, "Nov 09", 5.2f, 82, 3),
            PlannerHeatmapDay(11, "Nov 10", 3.8f, 65, 2),
            PlannerHeatmapDay(12, "Nov 11", 5.6f, 86, 3),
            PlannerHeatmapDay(13, "Nov 12", 6.1f, 90, 3),
            PlannerHeatmapDay(14, "Nov 13", 6.4f, 92, 4),
            PlannerHeatmapDay(15, "Nov 14", 3.2f, 58, 1),
            PlannerHeatmapDay(16, "Nov 15", 5.0f, 80, 3),
            PlannerHeatmapDay(17, "Nov 16", 5.8f, 88, 3),
            PlannerHeatmapDay(18, "Nov 17", 6.5f, 94, 4),
            PlannerHeatmapDay(19, "Nov 18", 7.1f, 96, 4),
            PlannerHeatmapDay(20, "Nov 19", 5.9f, 88, 3),
            PlannerHeatmapDay(21, "Nov 20", 4.6f, 76, 2),
            PlannerHeatmapDay(22, "Nov 21", 7.75f, 100, 4),
            PlannerHeatmapDay(23, "Nov 22", 1.85f, 40, 1),
            PlannerHeatmapDay(24, "Nov 23", 5.5f, 84, 3),
            PlannerHeatmapDay(25, "Nov 24", 6.2f, 90, 3),
            PlannerHeatmapDay(26, "Nov 25", 5.9f, 86, 3),
            PlannerHeatmapDay(27, "Nov 26", 6.4f, 92, 4),
            PlannerHeatmapDay(28, "Nov 27", 6.8f, 94, 4),
        )

        val userTasks = listOf(
            PlannerUserTask(
                id = "utask_1",
                title = "Solve 15 Gauss's Law PYQ numericals",
                subjectName = "Physics",
                accentMood = AccentMood.CYAN,
                slottedTimeWindow = "11:15 – 12:00",
                durationMinutes = 45,
                isCompleted = true,
                aiSlotNote = "AI-slotted right after Physics derivation block",
            ),
            PlannerUserTask(
                id = "utask_2",
                title = "Revise Arrhenius ln(k) vs 1/T slope traps",
                subjectName = "Chemistry",
                accentMood = AccentMood.VIOLET,
                slottedTimeWindow = "16:15 – 16:45",
                durationMinutes = 30,
                isCompleted = false,
                aiSlotNote = "AI-placed in light afternoon review pocket",
            ),
            PlannerUserTask(
                id = "utask_3",
                title = "Attempt 10 King's Property integral drills",
                subjectName = "Maths",
                accentMood = AccentMood.AMBER,
                slottedTimeWindow = "20:05 – 20:30",
                durationMinutes = 25,
                isCompleted = false,
                aiSlotNote = "AI-paired with evening Maths weak-topic sprint",
            ),
            PlannerUserTask(
                id = "utask_4",
                title = "Check Okazaki fragment & ligase flashcards",
                subjectName = "Biology",
                accentMood = AccentMood.EMERALD,
                slottedTimeWindow = "21:25 – 21:45",
                durationMinutes = 20,
                isCompleted = false,
                aiSlotNote = "Low-load recall wind-down before sleep cutoff",
            ),
        )

        val initialAiSideMessages = createZeroContextPlannerGreeting(
            sessionNumber = 1,
            rag1 = rag1,
            rag2 = rag2,
        )

        return StandalonePlannerUiState(
            isAiEnabled = true,
            isScheduleConfirmed = false,
            windowSessionNumber = 1,
            zeroContextTurnCount = 0,
            rag1 = rag1,
            rag2 = rag2,
            tasks = tasks,
            pendingGate = null,
            statusNotice = null,
            isAiSidePanelOpen = false,
            showSimulatedRagSheet = false,
            heatmapDays = heatmap28,
            activeAppeal = null,
            userTasks = userTasks,
            manualEditsUsed = 0,
            manualEditCap = 3,
            isManualTaskEditingLocked = false,
            aiInterventionBanner = null,
            aiSideMessages = initialAiSideMessages,
            aiSideComposerText = "",
        )
    }

    fun createZeroContextPlannerGreeting(
        sessionNumber: Int,
        rag1: PlannerRag1State,
        rag2: PlannerRag2State,
    ): List<PlannerAiSideMessage> {
        return listOf(
            PlannerAiSideMessage(
                id = "ai_sys_$sessionNumber",
                isUser = false,
                text = "Fresh Window #$sessionNumber · 0 prior chat turns carried over. Hydrated strictly from simulated RAG(1) (${rag1.oneMonthAvgStudyHours}h/d study avg) & RAG(2) peak focus (${rag2.peakFocusWindow}). Tap Appeal on any block or ask me to re-balance your plan.",
                tagLabel = "ZERO-CONTEXT · SIM RAG(1)+RAG(2)",
            ),
        )
    }

    fun createInitialYptAllowedApps(): List<YptAllowedApp> = listOf(
        YptAllowedApp("app_vault", "GHICKIN Vault", "Study Archive", "vault", true),
        YptAllowedApp("app_calc", "Calculator", "Scientific", "calculator", true),
        YptAllowedApp("app_dict", "Dictionary", "Reference", "dictionary", true),
        YptAllowedApp("app_pdf", "Notes PDF", "Reader", "pdf", true),
        YptAllowedApp("app_formula", "Formula Deck", "Quick Sheet", "formula", true),
        YptAllowedApp("app_music", "Lo-Fi Focus", "Audio", "audio", false),
    )

    fun createInitialTimerGroupState(): StudyTimerGroupUiState {
        // Unoptimized raw daily study history (20 days) so true Highest & Lowest day records stay intact
        val rawHistory = listOf(
            RawDailyStudyPoint(1, "Nov 08", 245),
            RawDailyStudyPoint(2, "Nov 09", 312),
            RawDailyStudyPoint(3, "Nov 10", 228),
            RawDailyStudyPoint(4, "Nov 11", 336),
            RawDailyStudyPoint(5, "Nov 12", 366),
            RawDailyStudyPoint(6, "Nov 13", 384),
            RawDailyStudyPoint(7, "Nov 14", 192),
            RawDailyStudyPoint(8, "Nov 15", 300),
            RawDailyStudyPoint(9, "Nov 16", 348),
            RawDailyStudyPoint(10, "Nov 17", 390),
            RawDailyStudyPoint(11, "Nov 18", 426),
            RawDailyStudyPoint(12, "Nov 19", 354),
            RawDailyStudyPoint(13, "Nov 20", 276),
            RawDailyStudyPoint(14, "Nov 21", 465), // Highest Day: 7h 45m
            RawDailyStudyPoint(15, "Nov 22", 110), // Lowest Day: 1h 50m
            RawDailyStudyPoint(16, "Nov 23", 330),
            RawDailyStudyPoint(17, "Nov 24", 372),
            RawDailyStudyPoint(18, "Nov 25", 354),
            RawDailyStudyPoint(19, "Nov 26", 384),
            RawDailyStudyPoint(20, "Nov 27", 408),
        )

        val studyLogRag = StudyLogRagState(
            past20DaysIndividualMinutes = rawHistory,
            oneMonthCombinedAvgMinutes = 326,
            sixMonthCombinedAvgMinutes = 284,
            lastAutoOptimizedLabel = "Auto-optimized every 3 days · Next cycle in 2d",
            compactedSummary = "20d avg: 5h 24m/day · 1m combined avg: 5h 26m · 6m combined avg: 4h 44m. " +
                "Physics accounts for 36% of deep-focus blocks; auto-logged Revision & Flashcards average 28m/day.",
            totalLoggedSessions = 48,
        )

        val todaySubjects = mapOf(
            "Physics" to 4680,      // 1h 18m
            "Chemistry" to 2820,    // 47m
            "Biology" to 2160,      // 36m
            "Maths" to 1980,        // 33m
            "Revision" to 960,      // 16m
            "Flashcards" to 600,    // 10m
        )

        val lectureTracked = mapOf(
            "lec_phys_04" to 3120,
            "lec_chem_03" to 2280,
            "lec_bio_02" to 1740,
        )

        val initialLogs = listOf(
            StudySessionLogEntry(
                id = "log_init_1",
                subjectName = "Physics",
                accentMood = AccentMood.CYAN,
                syncedLectureId = "lec_phys_04",
                syncedLectureTitle = "L04 · Gauss's Law & Spherical Shell Flux",
                durationSeconds = 3000,
                modeLabel = "Pomodoro 50/10",
                source = TimerSessionSource.MANUAL,
                timestampLabel = "Today · 08:20",
            ),
            StudySessionLogEntry(
                id = "log_init_2",
                subjectName = "Revision",
                accentMood = AccentMood.EMERALD,
                durationSeconds = 960,
                modeLabel = "Auto Count-Up",
                source = TimerSessionSource.AUTO_REVISION,
                timestampLabel = "Today · 07:05",
            ),
            StudySessionLogEntry(
                id = "log_init_3",
                subjectName = "Flashcards",
                accentMood = AccentMood.AMBER,
                durationSeconds = 600,
                modeLabel = "Auto Count-Up",
                source = TimerSessionSource.AUTO_FLASHCARDS,
                timestampLabel = "Today · 07:18",
            ),
        )

        val userTodayTotal = todaySubjects.values.sum()

        val groups = listOf(
            YptStudyGroup(
                id = "grp_jee_grind",
                name = "JEE / NEET Quiet Hall 2026",
                examCategory = "COMPETITIVE",
                goalSummary = "Daily 6h silent focus · Zero chat distractions",
                dailyTargetHours = 6,
                isPublic = true,
                members = listOf(
                    YptGroupMember(
                        id = "mem_you",
                        displayName = "You (Scholar)",
                        handle = "@scholar_you",
                        status = YptMemberStatus.IDLE,
                        subjectTag = "Physics",
                        chapterTag = "Gauss's Law & Flux",
                        todayFocusSeconds = userTodayTotal,
                        accentMood = AccentMood.CYAN,
                        isCurrentUser = true,
                    ),
                    YptGroupMember(
                        id = "mem_aarav",
                        displayName = "Aarav K.",
                        handle = "@aarav_phy",
                        status = YptMemberStatus.FOCUSING,
                        subjectTag = "Physics",
                        chapterTag = "Rotational Dynamics",
                        todayFocusSeconds = 18420, // 5h 07m
                        accentMood = AccentMood.CYAN,
                    ),
                    YptGroupMember(
                        id = "mem_meera",
                        displayName = "Meera S.",
                        handle = "@meera_bio",
                        status = YptMemberStatus.FOCUSING,
                        subjectTag = "Biology",
                        chapterTag = "Molecular Inheritance",
                        todayFocusSeconds = 15960, // 4h 26m
                        accentMood = AccentMood.EMERALD,
                    ),
                    YptGroupMember(
                        id = "mem_rohan",
                        displayName = "Rohan V.",
                        handle = "@rohan_chem",
                        status = YptMemberStatus.IN_REVISION,
                        subjectTag = "Chemistry",
                        chapterTag = "Chemical Kinetics",
                        todayFocusSeconds = 14100, // 3h 55m
                        accentMood = AccentMood.VIOLET,
                    ),
                    YptGroupMember(
                        id = "mem_ananya",
                        displayName = "Ananya D.",
                        handle = "@ananya_math",
                        status = YptMemberStatus.ON_BREAK,
                        subjectTag = "Maths",
                        chapterTag = "Definite Integration",
                        todayFocusSeconds = 11280, // 3h 08m
                        accentMood = AccentMood.AMBER,
                    ),
                    YptGroupMember(
                        id = "mem_kabir",
                        displayName = "Kabir M.",
                        handle = "@kabir_jee",
                        status = YptMemberStatus.FOCUSING,
                        subjectTag = "Physics",
                        chapterTag = "Electrostatics",
                        todayFocusSeconds = 9540, // 2h 39m
                        accentMood = AccentMood.CYAN,
                    ),
                ),
            ),
            YptStudyGroup(
                id = "grp_5am_club",
                name = "5 AM Early Sprint Club",
                examCategory = "MORNING TRACK",
                goalSummary = "Lock 3h before 9 AM · Strict Pomodoro discipline",
                dailyTargetHours = 5,
                isPublic = true,
                members = listOf(
                    YptGroupMember(
                        id = "mem_5am_1",
                        displayName = "Devansh R.",
                        handle = "@dev_5am",
                        status = YptMemberStatus.FOCUSING,
                        subjectTag = "Maths",
                        chapterTag = "Differential Equations",
                        todayFocusSeconds = 16200,
                        accentMood = AccentMood.AMBER,
                    ),
                    YptGroupMember(
                        id = "mem_5am_2",
                        displayName = "Sanya T.",
                        handle = "@sanya_med",
                        status = YptMemberStatus.IN_REVISION,
                        subjectTag = "Biology",
                        chapterTag = "Human Physiology",
                        todayFocusSeconds = 12840,
                        accentMood = AccentMood.EMERALD,
                    ),
                    YptGroupMember(
                        id = "mem_5am_3",
                        displayName = "Ishan P.",
                        handle = "@ishan_chem",
                        status = YptMemberStatus.FOCUSING,
                        subjectTag = "Chemistry",
                        chapterTag = "Electrochemistry",
                        todayFocusSeconds = 10920,
                        accentMood = AccentMood.VIOLET,
                    ),
                ),
            ),
            YptStudyGroup(
                id = "grp_board_ncert",
                name = "NCERT & Board Mastery Hall",
                examCategory = "BOARD EXAMS",
                goalSummary = "Line-by-line NCERT + Past 10Y Papers",
                dailyTargetHours = 4,
                isPublic = true,
                members = listOf(
                    YptGroupMember(
                        id = "mem_brd_1",
                        displayName = "Priya N.",
                        handle = "@priya_ncert",
                        status = YptMemberStatus.FOCUSING,
                        subjectTag = "Biology",
                        chapterTag = "Biotechnology",
                        todayFocusSeconds = 11400,
                        accentMood = AccentMood.EMERALD,
                    ),
                    YptGroupMember(
                        id = "mem_brd_2",
                        displayName = "Vikram J.",
                        handle = "@vikram_phy",
                        status = YptMemberStatus.IDLE,
                        subjectTag = "Physics",
                        chapterTag = "Ray Optics",
                        todayFocusSeconds = 7380,
                        accentMood = AccentMood.CYAN,
                    ),
                ),
            ),
        )

        return StudyTimerGroupUiState(
            mode = StudyTimerMode.POMODORO,
            pomodoroStyle = PomodoroRingStyle.MINIMAL_HALO,
            focusDurationMinutes = 50,
            breakDurationMinutes = 10,
            remainingSeconds = 50 * 60,
            splitRatio = TimerSplitRatio.RATIO_75_25,
            lastSplitRatioBeforeToggle = TimerSplitRatio.RATIO_25_75,
            todaySubjectSeconds = todaySubjects,
            lectureTrackedSeconds = lectureTracked,
            recentSessionLogs = initialLogs,
            rawDailyStudyHistory = rawHistory,
            studyLogRag = studyLogRag,
            groups = groups,
            joinedGroupId = groups.first().id,
            userStatus = YptMemberStatus.IDLE,
            userChapterTag = "Gauss's Law & Flux",
            splitPosture = SplitPosture.FULL_TIMER,
            isFullScreenHomeClockMode = false,
            allowedApps = createInitialYptAllowedApps(),
            showAllowedAppsSheet = false,
            inspectedMemberId = null,
            silentBoostCounts = mapOf("mem_aarav" to 3, "mem_diya" to 5),
        )
    }

    fun createInitialSettingsRagHubState(): SettingsRagHubState {
        return SettingsRagHubState()
    }

    /**
     * Generates a data-first AI optimization & recommendation report strictly prioritizing
     * recorded telemetry & study metrics over subjective user wording.
     */
    fun generateDataFirstOptimization(
        ragId: AppRagFileId,
        uiState: WorkspaceUiState,
        userNote: String,
    ): RagAiOptimizationResult {
        val planner = uiState.standalonePlannerState
        val timer = uiState.studyTimerGroupState
        val trimmedNote = userNote.trim()
        val dataFirstClause = if (trimmedNote.isNotEmpty()) {
            "Data-First Override: Weighted actual logged telemetry & study hours above your note (\"${trimmedNote.take(64)}\")."
        } else {
            "Data-First Priority: Generated strictly from recorded screen time, study hours, completion %, and chapter logs."
        }

        return when (ragId) {
            AppRagFileId.PLANNER_RAG_1 -> {
                val avg20Study = planner.rag1.daily20Entries.map { it.studyHours }.average()
                val avg20Screen = planner.rag1.daily20Entries.map { it.screenTimeHours }.average()
                val lowDays = planner.rag1.daily20Entries.count { it.studyHours < 4.0f }
                RagAiOptimizationResult(
                    ragId = ragId,
                    optimizedAtLabel = "Optimized just now · 3-day cadence synced",
                    tokenReductionLabel = "${planner.rag1.rawEstimatedTokens} → 188 tokens (−90%)",
                    dataFirstPriorityNote = dataFirstClause,
                    compactedSummary = "Compacted 20 individual days (avg %.1fh study / %.1fh screen) + 1–2m avg (%.1fh) + 6m avg (%.1fh). "
                        .format(avg20Study, avg20Screen, planner.rag1.twoMonthAvgStudyHours, planner.rag1.sixMonthAvgStudyHours) +
                        "Detected $lowDays low-output days strongly correlated with screen time > 6.0h.",
                    recommendations = listOf(
                        "Cap non-study screen time below 4.0h: on days under 4.0h screen time, your recorded study hours averaged 6.5h+.",
                        "Front-load 55% of target hours before 12:00 PM where 20-day variance is lowest.",
                        "Keep 20d individual + 1-2m & 6m averages compacted every 3 days to maintain zero-context planner speed.",
                    ),
                )
            }

            AppRagFileId.PLANNER_RAG_2 -> {
                val livePct = planner.liveTaskCompletionPercent
                RagAiOptimizationResult(
                    ragId = ragId,
                    optimizedAtLabel = "Regenerated just now from live task progress",
                    tokenReductionLabel = "420 → 96 tokens (−77%)",
                    dataFirstPriorityNote = dataFirstClause,
                    compactedSummary = "Active window ${planner.rag2.activeHoursWindow}; peak focus locked at ${planner.rag2.peakFocusWindow}. " +
                        "Live task completion sits at $livePct% (${planner.tasks.count { it.isCompleted }}/${planner.tasks.size} blocks).",
                    recommendations = listOf(
                        "Protect ${planner.rag2.peakFocusWindow} exclusively for high-weightage Physics/Maths problem solving.",
                        "Move passive revision & flashcards out of peak slots into your 06:45 or 20:30 low-friction windows.",
                        if (livePct < 60) {
                            "Completion is currently $livePct% — trim afternoon block durations by 15m rather than adding late-night overflow."
                        } else {
                            "Strong $livePct% completion rate — maintain current 90m block ceiling without extending past 23:05."
                        },
                    ),
                )
            }

            AppRagFileId.STUDY_LOG_RAG -> {
                val highest = timer.highestStudyDay
                val lowest = timer.lowestStudyDay
                val topSubject = timer.todaySubjectSeconds.maxByOrNull { it.value }
                val totalToday = formatDurationCompactHm(timer.todayTotalStudySeconds)
                RagAiOptimizationResult(
                    ragId = ragId,
                    optimizedAtLabel = "Auto-compacted just now · Raw high/low preserved",
                    tokenReductionLabel = "1,420 → 175 tokens (−88%)",
                    dataFirstPriorityNote = dataFirstClause,
                    compactedSummary = "Today logged $totalToday across ${timer.todaySubjectSeconds.size} subjects " +
                        "(lead: ${topSubject?.key ?: "None"}). Unoptimized records preserved: Highest ${highest?.let { "${formatMinutesHm(it.totalMinutes)} (${it.shortDate})" } ?: "—"}, " +
                        "Lowest ${lowest?.let { "${formatMinutesHm(it.totalMinutes)} (${it.shortDate})" } ?: "—"}.",
                    recommendations = listOf(
                        "Bridge your lowest-day dip (${lowest?.let { formatMinutesHm(it.totalMinutes) } ?: "—"}) by setting a minimum 45m floor block on high-fatigue weekends.",
                        "Sync lectures inside the Study Timer popup so exact per-lecture time feeds directly into Vault pacing.",
                        "Keep Revision & Flashcard auto-timers running on Home open/exit to capture 100% of active recall minutes.",
                    ),
                )
            }

            AppRagFileId.CHAPTER_DIAGRAM_RAG -> {
                val catalogCount = uiState.grokTutorState.diagramCatalog.size
                RagAiOptimizationResult(
                    ragId = ragId,
                    optimizedAtLabel = "Indexed just now · Private filesDir/diagrams/",
                    tokenReductionLabel = "680 → 112 tokens (−84%)",
                    dataFirstPriorityNote = dataFirstClause,
                    compactedSummary = "$catalogCount premade offline HTML/SVG diagrams indexed strictly by chapter filename. " +
                        "Invisible prompt suffix exposes only linked-chapter filenames so Tutor replies stay token-lean.",
                    recommendations = listOf(
                        "Link 1–2 focused chapters per AI Tutor thread to keep diagram retrieval precision near 100%.",
                        "Use Split-Screen WebView mode for derivation-heavy Physics & Maths chapters while asking step-by-step doubts.",
                        "Generate additional chapter HTML simulations on demand as new weak topics appear in Quiz Arena.",
                    ),
                )
            }
        }
    }
}
