package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.BatchChapter
import com.example.model.BatchSubject
import com.example.model.BatchesUiState
import com.example.model.DailyRevisionBrief
import com.example.model.FocusMode
import com.example.model.InspectSelection
import com.example.model.LoggedTopic
import com.example.model.MaterialType
import com.example.model.Phase12MockData
import com.example.model.Phase13MockData
import com.example.model.PresetSubjectTemplate
import com.example.model.QuizMode
import com.example.model.RevisionQuizTabMode
import com.example.model.RevisionQuizUiState
import com.example.model.SpacedReviewItem
import com.example.model.StudyBatch
import com.example.model.SubjectMastery
import com.example.model.VaultMaterial
import com.example.model.VaultUiState
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

    // --- Phase 1.1 Workspace & Home Actions ---

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

    // --- Phase 1.2 Batches & Subject Maker Actions ---

    fun selectBatch(batchId: String) {
        _uiState.update { state ->
            val targetBatch = state.batchesState.batches.firstOrNull { it.id == batchId }
            val firstSubjectId = targetBatch?.subjects?.firstOrNull()?.id
            state.copy(
                batchesState = state.batchesState.copy(
                    selectedBatchId = batchId,
                    expandedSubjectId = firstSubjectId,
                ),
            )
        }
    }

    fun toggleExpandSubject(subjectId: String) {
        _uiState.update { state ->
            val current = state.batchesState.expandedSubjectId
            state.copy(
                batchesState = state.batchesState.copy(
                    expandedSubjectId = if (current == subjectId) null else subjectId,
                ),
            )
        }
    }

    fun toggleChapterCompleted(batchId: String, subjectId: String, chapterId: String) {
        _uiState.update { state ->
            val updatedBatches = state.batchesState.batches.map { batch ->
                if (batch.id != batchId) return@map batch
                val updatedSubjects = batch.subjects.map { subject ->
                    if (subject.id != subjectId) return@map subject
                    val updatedChapters = subject.chapters.map { chapter ->
                        if (chapter.id != chapterId) return@map chapter
                        val nowCompleted = !chapter.isCompleted
                        chapter.copy(
                            isCompleted = nowCompleted,
                            progressPercent = if (nowCompleted) 100 else 55,
                        )
                    }
                    val avgMastery = if (updatedChapters.isEmpty()) {
                        subject.masteryPercent
                    } else {
                        updatedChapters.sumOf { it.progressPercent } / updatedChapters.size
                    }
                    val doneLectures = updatedChapters
                        .filter { it.isCompleted }
                        .sumOf { it.lectureCount }
                        .coerceAtMost(subject.lectureCount)
                    subject.copy(
                        chapters = updatedChapters,
                        masteryPercent = avgMastery,
                        completedLectures = doneLectures,
                    )
                }
                batch.copy(subjects = updatedSubjects)
            }
            state.copy(
                batchesState = state.batchesState.copy(batches = updatedBatches),
            )
        }
    }

    fun openCreateBatchBuilder() {
        _uiState.update { state ->
            state.copy(batchesState = state.batchesState.copy(isBuilderOpen = true))
        }
    }

    fun closeCreateBatchBuilder() {
        _uiState.update { state ->
            state.copy(batchesState = state.batchesState.copy(isBuilderOpen = false))
        }
    }

    fun createCustomBatch(
        name: String,
        goalTag: String,
        accentMood: AccentMood,
        selectedTemplates: List<PresetSubjectTemplate>,
        customSubjectName: String,
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return

        val batchId = "batch_custom_${System.currentTimeMillis()}"
        val baseSubjects = selectedTemplates.ifEmpty {
            listOf(Phase12MockData.presetSubjectTemplates.first())
        }.toMutableList()

        val customTrimmed = customSubjectName.trim()
        if (customTrimmed.isNotEmpty() && baseSubjects.none { it.name.equals(customTrimmed, ignoreCase = true) }) {
            baseSubjects.add(
                PresetSubjectTemplate(
                    id = "tmpl_custom_${System.currentTimeMillis()}",
                    name = customTrimmed,
                    accentMood = accentMood,
                    defaultLectures = 12,
                ),
            )
        }

        val createdSubjects = baseSubjects.mapIndexed { idx, template ->
            val subjectId = "${batchId}_sub_$idx"
            BatchSubject(
                id = subjectId,
                name = template.name,
                accentMood = template.accentMood,
                lectureCount = template.defaultLectures,
                completedLectures = 2,
                masteryPercent = 24,
                chapters = listOf(
                    BatchChapter(
                        id = "${subjectId}_ch_1",
                        title = "${template.name} Foundations & Core Laws",
                        lectureCount = (template.defaultLectures / 3).coerceAtLeast(3),
                        progressPercent = 45,
                        isCompleted = false,
                        materialCount = 1,
                    ),
                    BatchChapter(
                        id = "${subjectId}_ch_2",
                        title = "${template.name} High-Yield Problem Patterns",
                        lectureCount = (template.defaultLectures / 3).coerceAtLeast(3),
                        progressPercent = 18,
                        isCompleted = false,
                        materialCount = 1,
                    ),
                    BatchChapter(
                        id = "${subjectId}_ch_3",
                        title = "${template.name} Synthesis & Timed Recall",
                        lectureCount = (template.defaultLectures / 3).coerceAtLeast(3),
                        progressPercent = 8,
                        isCompleted = false,
                        materialCount = 1,
                    ),
                ),
            )
        }

        val newBatch = StudyBatch(
            id = batchId,
            name = trimmedName,
            goalTag = goalTag,
            accentMood = accentMood,
            targetDateLabel = "Custom Track",
            totalHoursLogged = 0,
            subjects = createdSubjects,
        )

        // Also seed a starter mock Vault material so the new batch appears live in Vault
        val firstSubject = createdSubjects.first()
        val starterMaterial = VaultMaterial(
            id = "mat_starter_${System.currentTimeMillis()}",
            batchId = batchId,
            batchName = trimmedName,
            subjectId = firstSubject.id,
            subjectName = firstSubject.name,
            subjectMood = firstSubject.accentMood,
            chapterTitle = firstSubject.chapters.first().title,
            title = "$trimmedName — Starter Syllabus Roadmap",
            type = MaterialType.PDF_NOTES,
            pageCount = 10,
            fileSizeLabel = "1.6 MB",
            lastOpenedLabel = "Just created",
            estimatedReadMinutes = 9,
            summary = "Auto-structured starter overview for $trimmedName covering ${createdSubjects.joinToString { it.name }}.",
            outlineSections = createdSubjects.mapIndexed { index, sub ->
                "0${index + 1} · ${sub.name} Track Blueprint"
            },
        )

        _uiState.update { state ->
            state.copy(
                batchesState = state.batchesState.copy(
                    batches = listOf(newBatch) + state.batchesState.batches,
                    selectedBatchId = batchId,
                    expandedSubjectId = firstSubject.id,
                    isBuilderOpen = false,
                ),
                vaultState = state.vaultState.copy(
                    materials = listOf(starterMaterial) + state.vaultState.materials,
                ),
            )
        }
    }

    // --- Phase 1.2 Study Material Vault Actions ---

    fun selectVaultBatchFilter(batchId: String?) {
        _uiState.update { state ->
            state.copy(
                vaultState = state.vaultState.copy(
                    selectedBatchId = batchId,
                    selectedSubjectId = null,
                    selectedChapter = null,
                ),
            )
        }
    }

    fun selectVaultSubjectFilter(subjectId: String?) {
        _uiState.update { state ->
            state.copy(
                vaultState = state.vaultState.copy(
                    selectedSubjectId = subjectId,
                    selectedChapter = null,
                ),
            )
        }
    }

    fun selectVaultChapterFilter(chapterTitle: String?) {
        _uiState.update { state ->
            state.copy(
                vaultState = state.vaultState.copy(selectedChapter = chapterTitle),
            )
        }
    }

    fun updateVaultSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(vaultState = state.vaultState.copy(searchQuery = query))
        }
    }

    fun clearVaultFilters() {
        _uiState.update { state ->
            state.copy(
                vaultState = state.vaultState.copy(
                    selectedBatchId = null,
                    selectedSubjectId = null,
                    selectedChapter = null,
                    searchQuery = "",
                ),
            )
        }
    }

    fun previewVaultMaterial(materialId: String) {
        _uiState.update { state ->
            val toggled = if (state.vaultState.previewedMaterialId == materialId) null else materialId
            state.copy(
                vaultState = state.vaultState.copy(
                    previewedMaterialId = toggled,
                    openedReaderMaterialId = null,
                    showReaderInfoDrawer = false,
                ),
            )
        }
    }

    fun closeVaultPreview() {
        _uiState.update { state ->
            state.copy(
                vaultState = state.vaultState.copy(previewedMaterialId = null),
            )
        }
    }

    /**
     * Opens the mock PDF reader and collapses the right-hand split preview panel cleanly.
     */
    fun openVaultPdfReader(materialId: String) {
        _uiState.update { state ->
            state.copy(
                vaultState = state.vaultState.copy(
                    previewedMaterialId = null, // Collapse preview panel on open
                    openedReaderMaterialId = materialId,
                    readerCurrentPage = 1,
                    showReaderInfoDrawer = false,
                ),
            )
        }
    }

    fun closeVaultPdfReader() {
        _uiState.update { state ->
            state.copy(
                vaultState = state.vaultState.copy(
                    openedReaderMaterialId = null,
                    showReaderInfoDrawer = false,
                ),
            )
        }
    }

    fun toggleReaderInfoDrawer() {
        _uiState.update { state ->
            state.copy(
                vaultState = state.vaultState.copy(
                    showReaderInfoDrawer = !state.vaultState.showReaderInfoDrawer,
                ),
            )
        }
    }

    fun stepReaderPage(delta: Int) {
        _uiState.update { state ->
            val maxPages = state.vaultState.openedReaderMaterial?.pageCount ?: 1
            val nextPage = (state.vaultState.readerCurrentPage + delta).coerceIn(1, maxPages)
            state.copy(
                vaultState = state.vaultState.copy(readerCurrentPage = nextPage),
            )
        }
    }

    fun jumpReaderToPage(page: Int) {
        _uiState.update { state ->
            val maxPages = state.vaultState.openedReaderMaterial?.pageCount ?: 1
            state.copy(
                vaultState = state.vaultState.copy(
                    readerCurrentPage = page.coerceIn(1, maxPages),
                    showReaderInfoDrawer = false,
                ),
            )
        }
    }

    // --- Phase 1.3 Daily Revision Notes & Dual-Mode Subject Quiz Actions ---

    fun selectRevisionQuizMode(mode: RevisionQuizTabMode) {
        _uiState.update { state ->
            state.copy(
                revisionQuizState = state.revisionQuizState.copy(activeMode = mode),
            )
        }
    }

    fun selectRevisionSubject(subjectId: String) {
        _uiState.update { state ->
            state.copy(
                revisionQuizState = state.revisionQuizState.copy(
                    selectedRevisionSubjectId = subjectId,
                    isCompoundMixEnabled = false,
                ),
            )
        }
    }

    fun toggleCompoundRevisionMix() {
        _uiState.update { state ->
            state.copy(
                revisionQuizState = state.revisionQuizState.copy(
                    isCompoundMixEnabled = !state.revisionQuizState.isCompoundMixEnabled,
                ),
            )
        }
    }

    fun toggleDiagramLabels() {
        _uiState.update { state ->
            state.copy(
                revisionQuizState = state.revisionQuizState.copy(
                    showDiagramLabels = !state.revisionQuizState.showDiagramLabels,
                ),
            )
        }
    }

    fun selectQuizMode(mode: QuizMode) {
        _uiState.update { state ->
            val current = state.revisionQuizState
            val questionIds = Phase13MockData.questionIdsForSubject(
                current.allQuestions,
                current.selectedQuizSubjectId,
            )
            state.copy(
                revisionQuizState = current.copy(
                    quizMode = mode,
                    activeQuestionIds = questionIds,
                    currentQuestionIndex = 0,
                    selectedAnswers = emptyMap(),
                    questionElapsedSeconds = emptyMap(),
                    isQuizSubmitted = false,
                    isRetryMissedSession = false,
                    sessionNonce = current.sessionNonce + 1,
                ),
            )
        }
    }

    fun selectQuizSubject(subjectId: String) {
        _uiState.update { state ->
            val current = state.revisionQuizState
            val questionIds = Phase13MockData.questionIdsForSubject(current.allQuestions, subjectId)
            state.copy(
                revisionQuizState = current.copy(
                    selectedQuizSubjectId = subjectId,
                    activeQuestionIds = questionIds,
                    currentQuestionIndex = 0,
                    selectedAnswers = emptyMap(),
                    questionElapsedSeconds = emptyMap(),
                    isQuizSubmitted = false,
                    isRetryMissedSession = false,
                    sessionNonce = current.sessionNonce + 1,
                ),
            )
        }
    }

    fun selectQuizOption(questionId: String, optionIndex: Int, elapsedSeconds: Int) {
        _uiState.update { state ->
            val current = state.revisionQuizState
            if (current.isQuizSubmitted) return@update state
            // In Normal Mode, once answered the choice is locked to reveal instant explanation
            if (current.quizMode == QuizMode.NORMAL && current.selectedAnswers.containsKey(questionId)) {
                return@update state
            }
            val updatedAnswers = current.selectedAnswers + (questionId to optionIndex)
            val updatedTimes = if (current.quizMode == QuizMode.NORMAL) {
                current.questionElapsedSeconds + (questionId to elapsedSeconds)
            } else {
                current.questionElapsedSeconds
            }
            state.copy(
                revisionQuizState = current.copy(
                    selectedAnswers = updatedAnswers,
                    questionElapsedSeconds = updatedTimes,
                ),
            )
        }
    }

    fun advanceOrSubmitQuiz(currentQuestionElapsedSeconds: Int) {
        _uiState.update { state ->
            val current = state.revisionQuizState
            val question = current.currentQuestion ?: return@update state
            val recordedTimes = if (current.questionElapsedSeconds.containsKey(question.id)) {
                current.questionElapsedSeconds
            } else {
                current.questionElapsedSeconds + (question.id to currentQuestionElapsedSeconds)
            }
            val isLast = current.currentQuestionIndex >= current.activeQuestionIds.lastIndex
            if (isLast) {
                state.copy(
                    revisionQuizState = current.copy(
                        questionElapsedSeconds = recordedTimes,
                        isQuizSubmitted = true,
                    ),
                )
            } else {
                state.copy(
                    revisionQuizState = current.copy(
                        questionElapsedSeconds = recordedTimes,
                        currentQuestionIndex = current.currentQuestionIndex + 1,
                    ),
                )
            }
        }
    }

    fun retryMissedQuestions() {
        _uiState.update { state ->
            val current = state.revisionQuizState
            val missedIds = current.missedQuestions.map { it.id }
            if (missedIds.isEmpty()) return@update state
            state.copy(
                revisionQuizState = current.copy(
                    activeQuestionIds = missedIds,
                    currentQuestionIndex = 0,
                    selectedAnswers = emptyMap(),
                    questionElapsedSeconds = emptyMap(),
                    isQuizSubmitted = false,
                    isRetryMissedSession = true,
                    sessionNonce = current.sessionNonce + 1,
                ),
            )
        }
    }

    fun retakeFullQuiz() {
        _uiState.update { state ->
            val current = state.revisionQuizState
            val fullIds = Phase13MockData.questionIdsForSubject(
                current.allQuestions,
                current.selectedQuizSubjectId,
            )
            state.copy(
                revisionQuizState = current.copy(
                    activeQuestionIds = fullIds,
                    currentQuestionIndex = 0,
                    selectedAnswers = emptyMap(),
                    questionElapsedSeconds = emptyMap(),
                    isQuizSubmitted = false,
                    isRetryMissedSession = false,
                    sessionNonce = current.sessionNonce + 1,
                ),
            )
        }
    }

    fun jumpFromQuizToRevisionNotes(preferredSubjectId: String?) {
        _uiState.update { state ->
            val current = state.revisionQuizState
            val targetId = preferredSubjectId ?: current.selectedQuizSubjectId
            val useCompound = targetId == Phase13MockData.SUBJECT_COMPOUND_ID
            state.copy(
                revisionQuizState = current.copy(
                    activeMode = RevisionQuizTabMode.REVISION_NOTES,
                    isCompoundMixEnabled = useCompound,
                    selectedRevisionSubjectId = if (useCompound) {
                        current.selectedRevisionSubjectId
                    } else {
                        targetId
                    },
                ),
            )
        }
    }

    // --- Layered Back Navigation ---

    fun navigateBackToHome() {
        _uiState.update { state ->
            when {
                state.inspectSelection != null -> state.copy(inspectSelection = null)
                state.activeTab == WorkspaceTab.BATCHES && state.batchesState.isBuilderOpen ->
                    state.copy(batchesState = state.batchesState.copy(isBuilderOpen = false))
                state.activeTab == WorkspaceTab.VAULT && state.vaultState.showReaderInfoDrawer ->
                    state.copy(vaultState = state.vaultState.copy(showReaderInfoDrawer = false))
                state.activeTab == WorkspaceTab.VAULT && state.vaultState.isReaderOpen ->
                    state.copy(vaultState = state.vaultState.copy(openedReaderMaterialId = null))
                state.activeTab == WorkspaceTab.VAULT && state.vaultState.previewedMaterialId != null ->
                    state.copy(vaultState = state.vaultState.copy(previewedMaterialId = null))
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

        val initialBatches = Phase12MockData.createInitialBatches()
        val firstBatch = initialBatches.first()
        val initialRevisionSheets = Phase13MockData.createRevisionSheets()
        val initialQuizQuestions = Phase13MockData.createQuizQuestions()
        val defaultQuizSubject = Phase13MockData.SUBJECT_PHYSICS_ID

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
            batchesState = BatchesUiState(
                batches = initialBatches,
                selectedBatchId = firstBatch.id,
                expandedSubjectId = firstBatch.subjects.firstOrNull()?.id,
                isBuilderOpen = false,
            ),
            vaultState = VaultUiState(
                materials = Phase12MockData.createInitialVaultMaterials(),
                previewedMaterialId = "mat_phys_gauss_pdf",
            ),
            revisionQuizState = RevisionQuizUiState(
                activeMode = RevisionQuizTabMode.REVISION_NOTES,
                revisionSheets = initialRevisionSheets,
                compoundMixSheet = Phase13MockData.createCompoundMixSheet(),
                selectedRevisionSubjectId = Phase13MockData.SUBJECT_PHYSICS_ID,
                isCompoundMixEnabled = false,
                showDiagramLabels = true,
                allQuestions = initialQuizQuestions,
                quizMode = QuizMode.NORMAL,
                selectedQuizSubjectId = defaultQuizSubject,
                activeQuestionIds = Phase13MockData.questionIdsForSubject(
                    initialQuizQuestions,
                    defaultQuizSubject,
                ),
            ),
        )
    }
}
