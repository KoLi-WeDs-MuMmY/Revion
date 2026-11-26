package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.DailyRevisionBrief
import com.example.model.FocusMode
import com.example.model.InspectSelection
import com.example.model.LoggedTopic
import com.example.model.SpacedReviewItem
import com.example.model.SubjectMastery
import com.example.model.WorkspaceTab
import com.example.model.WorkspaceUiState
import com.example.ui.theme.AccentMood
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WorkspaceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialMockState())
    val uiState: StateFlow<WorkspaceUiState> = _uiState.asStateFlow()

    fun selectTab(tab: WorkspaceTab) {
        _uiState.update { state ->
            state.copy(
                activeTab = tab,
                inspectSelection = null,
            )
        }
    }

    fun selectAccentMood(mood: AccentMood) {
        _uiState.update { it.copy(accentMood = mood) }
    }

    fun cycleAccentMood() {
        _uiState.update { it.copy(accentMood = it.accentMood.next()) }
    }

    fun toggleFocusMode() {
        _uiState.update { state ->
            val nextMode = when (state.focusMode) {
                FocusMode.AI_READY -> FocusMode.DEEP_REVIEW
                FocusMode.DEEP_REVIEW -> FocusMode.AI_READY
            }
            state.copy(focusMode = nextMode)
        }
    }

    fun openInspect(selection: InspectSelection) {
        _uiState.update { it.copy(inspectSelection = selection) }
    }

    fun closeInspect() {
        _uiState.update { it.copy(inspectSelection = null) }
    }

    fun jumpFromInspectToTab(targetTab: WorkspaceTab) {
        _uiState.update { state ->
            state.copy(
                inspectSelection = null,
                activeTab = targetTab,
            )
        }
    }

    fun navigateBackToHome() {
        _uiState.update { state ->
            when {
                state.inspectSelection != null -> state.copy(inspectSelection = null)
                state.activeTab != WorkspaceTab.HOME -> state.copy(activeTab = WorkspaceTab.HOME)
                else -> state
            }
        }
    }

    private fun createInitialMockState(): WorkspaceUiState {
        val brief = DailyRevisionBrief(
            headline = "Yesterday's Physics & Biology Synthesis",
            summary = "Distilled from Gauss's Law & Flux and DNA Replication notes logged yesterday. Ready for active recall checks and rapid diagram review.",
            sourceSubjects = listOf(
                "Physics · Gauss's Law" to AccentMood.CYAN,
                "Biology · DNA Replication" to AccentMood.EMERALD,
            ),
            estimatedMinutes = 18,
            checkCount = 12,
            targetRetentionPercent = 94,
            focusPoints = listOf(
                "Electric flux symmetry across hollow vs. solid conducting spheres",
                "Leading vs. lagging strand polarity and Okazaki fragment ligation",
                "3 high-yield recall traps flagged from yesterday's practice log",
            ),
        )

        val subjects = listOf(
            SubjectMastery(
                id = "physics",
                name = "Physics",
                accentMood = AccentMood.CYAN,
                masteryPercent = 84,
                chaptersCompleted = 11,
                totalChapters = 14,
                weeklyDelta = "+4.2%",
                activeChapter = "Electrostatics & Flux",
                focusNote = "Strong conceptual accuracy on Gaussian surfaces; reinforce shell field discontinuity graphs.",
                keyConcepts = listOf(
                    "Gauss's Law in symmetric charge distributions",
                    "Equipotential surfaces & potential gradient",
                    "Dielectric slab capacitance boundary shifts",
                ),
            ),
            SubjectMastery(
                id = "chemistry",
                name = "Chemistry",
                accentMood = AccentMood.VIOLET,
                masteryPercent = 71,
                chaptersCompleted = 9,
                totalChapters = 13,
                weeklyDelta = "+2.8%",
                activeChapter = "Chemical Kinetics",
                focusNote = "First-order half-life derivations are crisp; schedule x-3 drills on Arrhenius slope conversions.",
                keyConcepts = listOf(
                    "Pseudo first-order reaction approximations",
                    "Arrhenius activation energy & temperature plots",
                    "Collision theory steric factor interpretation",
                ),
            ),
            SubjectMastery(
                id = "biology",
                name = "Biology",
                accentMood = AccentMood.EMERALD,
                masteryPercent = 92,
                chaptersCompleted = 14,
                totalChapters = 16,
                weeklyDelta = "+5.6%",
                activeChapter = "Molecular Inheritance",
                focusNote = "Highest retention streak this week across enzymatic machinery and replication forks.",
                keyConcepts = listOf(
                    "DNA Polymerase III 5'->3' synthesis polarity",
                    "Meselson-Stahl semi-conservative density proof",
                    "Lac operon inducible regulation checkpoints",
                ),
            ),
            SubjectMastery(
                id = "maths",
                name = "Maths",
                accentMood = AccentMood.AMBER,
                masteryPercent = 66,
                chaptersCompleted = 8,
                totalChapters = 13,
                weeklyDelta = "+1.9%",
                activeChapter = "Definite Integration",
                focusNote = "Queued for weekend hard-topic consolidation on Leibniz rule and symmetric limit reductions.",
                keyConcepts = listOf(
                    "King's Property limit symmetry reductions",
                    "Newton-Leibniz differentiation under integral sign",
                    "Walli's formula & periodic function integrals",
                ),
            ),
        )

        val yesterdayTopics = listOf(
            LoggedTopic(
                id = "topic_phys_gauss",
                subjectName = "Physics",
                accentMood = AccentMood.CYAN,
                chapterTitle = "Gauss's Law & Electric Flux",
                subtopicSummary = "Spherical shells, infinite line charge & symmetry proofs",
                durationMinutes = 52,
                confidenceLabel = "Solid",
                loggedTimeLabel = "Yesterday · 19:40",
                keyTakeaways = listOf(
                    "Enclosed charge solely governs net outward flux through any closed surface",
                    "Field inside a uniformly charged hollow shell vanishes identically",
                ),
            ),
            LoggedTopic(
                id = "topic_bio_dna",
                subjectName = "Biology",
                accentMood = AccentMood.EMERALD,
                chapterTitle = "DNA Replication Fork",
                subtopicSummary = "Helicase unwinding, RNA primase & Okazaki ligation",
                durationMinutes = 44,
                confidenceLabel = "High",
                loggedTimeLabel = "Yesterday · 17:15",
                keyTakeaways = listOf(
                    "Replication proceeds bidirectionally from Ori with continuous leading synthesis",
                    "DNA Ligase seals phosphodiester nicks between lagging-strand fragments",
                ),
            ),
            LoggedTopic(
                id = "topic_chem_kinetics",
                subjectName = "Chemistry",
                accentMood = AccentMood.VIOLET,
                chapterTitle = "Arrhenius & Activation Energy",
                subtopicSummary = "Logarithmic slope plots & catalyst barrier shifts",
                durationMinutes = 36,
                confidenceLabel = "Review",
                loggedTimeLabel = "Yesterday · 14:20",
                keyTakeaways = listOf(
                    "Slope of ln(k) vs 1/T directly yields -Ea/R across temperature ranges",
                    "Catalysts lower activation threshold without altering net enthalpy",
                ),
            ),
            LoggedTopic(
                id = "topic_math_int",
                subjectName = "Maths",
                accentMood = AccentMood.AMBER,
                chapterTitle = "Definite Integral Symmetry",
                subtopicSummary = "King's Property & trigonometric limit pairing",
                durationMinutes = 48,
                confidenceLabel = "Hard",
                loggedTimeLabel = "Yesterday · 11:05",
                keyTakeaways = listOf(
                    "Substitute x -> (a + b - x) and sum paired integrals to collapse denominators",
                    "Check even/odd parity before splitting symmetric [-a, a] domains",
                ),
            ),
        )

        val spacedReviews = listOf(
            SpacedReviewItem(
                id = "rev_x1_gauss",
                subjectName = "Physics",
                accentMood = AccentMood.CYAN,
                topicTitle = "Gaussian Surface Flux Traps",
                intervalBadge = "x-1",
                dueLabel = "Today · 18:00",
                cardCount = 14,
                retentionPercent = 82,
                reviewNotes = listOf(
                    "First 24-hour consolidation pass from yesterday's Electrostatics session",
                    "Includes 4 conceptual boundary-case diagram recalls",
                ),
            ),
            SpacedReviewItem(
                id = "rev_x1_dna",
                subjectName = "Biology",
                accentMood = AccentMood.EMERALD,
                topicTitle = "Replication Enzyme Sequence",
                intervalBadge = "x-1",
                dueLabel = "Today · 19:30",
                cardCount = 10,
                retentionPercent = 91,
                reviewNotes = listOf(
                    "Rapid 24-hour enzyme role verification before moving to transcription",
                    "High retention confidence; estimated 6 minutes",
                ),
            ),
            SpacedReviewItem(
                id = "rev_x3_kinetics",
                subjectName = "Chemistry",
                accentMood = AccentMood.VIOLET,
                topicTitle = "Order of Reaction Half-Lives",
                intervalBadge = "x-3",
                dueLabel = "Tomorrow · 09:00",
                cardCount = 16,
                retentionPercent = 69,
                reviewNotes = listOf(
                    "Day-3 spaced recall checkpoint covering zero, first, and second-order curves",
                    "2 calculation mistakes tagged for Mistake Bank preview",
                ),
            ),
            SpacedReviewItem(
                id = "rev_wknd_maths",
                subjectName = "Maths",
                accentMood = AccentMood.AMBER,
                topicTitle = "Leibniz Rule & Hard Integrals",
                intervalBadge = "Weekend Hard",
                dueLabel = "Sat · 10:00",
                cardCount = 18,
                retentionPercent = 58,
                reviewNotes = listOf(
                    "Scheduled weekend deep-consolidation block for low-confidence derivations",
                    "Paired with step-by-step limit substitution prompts",
                ),
            ),
        )

        return WorkspaceUiState(
            activeTab = WorkspaceTab.HOME,
            accentMood = AccentMood.CYAN,
            focusMode = FocusMode.AI_READY,
            streakDays = 14,
            revisionBrief = brief,
            subjects = subjects,
            yesterdayTopics = yesterdayTopics,
            spacedReviews = spacedReviews,
            inspectSelection = null,
        )
    }
}
