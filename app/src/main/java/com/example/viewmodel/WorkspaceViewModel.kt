package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AiPermissionRequest
import com.example.model.BatchChapter
import com.example.model.BatchCrudDialog
import com.example.model.BatchSubject
import com.example.model.BatchesUiState
import com.example.model.CounterProposal
import com.example.model.DailyFlashcardItem
import com.example.model.DailyPageRoute
import com.example.model.DailyRevisionBrief
import com.example.model.FlashcardHubMode
import com.example.model.FlashcardRating
import com.example.model.FlashcardsMistakesUiState
import com.example.model.FocusMode
import com.example.model.HomeCommandState
import com.example.model.InspectSelection
import com.example.model.LoggedTopic
import com.example.model.MaterialType
import com.example.model.MistakeCategory
import com.example.model.PermissionRequestAction
import com.example.model.PermissionVerdictPhase
import com.example.model.Phase12MockData
import com.example.model.Phase13MockData
import com.example.model.Phase14MockData
import com.example.model.Phase15MockData
import com.example.model.Phase161MockData
import com.example.model.PlannerApprovalStatus
import com.example.model.PlannerBlock
import com.example.model.PlannerBlockCategory
import com.example.model.PresetSubjectTemplate
import com.example.model.PwVaultUiState
import com.example.model.QuizMode
import com.example.model.RevisionQuizTabMode
import com.example.model.RevisionQuizUiState
import com.example.model.SpacedFlashcard
import com.example.model.SpacedIntervalBucket
import com.example.model.SpacedReviewItem
import com.example.model.StudyBatch
import com.example.model.SubjectMastery
import com.example.model.TutorMessage
import com.example.model.TutorPlannerMode
import com.example.model.TutorPlannerUiState
import com.example.model.VaultCategorySubView
import com.example.model.VaultChapterNode
import com.example.model.VaultLectureNode
import com.example.model.VaultMaterial
import com.example.model.VaultPaletteFocus
import com.example.model.VaultSubjectNode
import com.example.model.VaultUiState
import com.example.model.WorkspaceTab
import com.example.model.WorkspaceUiState
import com.example.model.formatClockMinutes
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AppThemeId
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkspaceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialMockState())
    val uiState: StateFlow<WorkspaceUiState> = _uiState.asStateFlow()

    private var tutorReplyJob: Job? = null
    private var permissionEvalJob: Job? = null

    // --- Phase 1.1 Workspace & Home Actions ---

    fun selectTab(tab: WorkspaceTab) {
        _uiState.update { state ->
            state.copy(
                activeTab = tab,
                isSettingsOpen = false,
                inspectSelection = null,
            )
        }
    }

    fun selectAppTheme(theme: AppThemeId) {
        _uiState.update { it.copy(appTheme = theme) }
    }

    fun openSettings() {
        _uiState.update { it.copy(isSettingsOpen = true) }
    }

    fun closeSettings() {
        _uiState.update { it.copy(isSettingsOpen = false) }
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
                    expandedChapterId = null,
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
                    expandedChapterId = null,
                ),
            )
        }
    }

    fun toggleExpandChapterInBatches(chapterId: String) {
        _uiState.update { state ->
            val current = state.batchesState.expandedChapterId
            state.copy(
                batchesState = state.batchesState.copy(
                    expandedChapterId = if (current == chapterId) null else chapterId,
                ),
            )
        }
    }

    fun toggleChapterCompleted(batchId: String, subjectId: String, chapterId: String) {
        mutateVaultSubjectsForBatch(batchId) { subjects ->
            subjects.map { subject ->
                if (subject.id != subjectId) return@map subject
                subject.copy(
                    chapters = subject.chapters.map { chapter ->
                        if (chapter.id != chapterId) return@map chapter
                        val allDone = chapter.lectures.isNotEmpty() && chapter.lectures.all { it.isCompleted }
                        val targetDone = !allDone
                        chapter.copy(
                            lectures = chapter.lectures.map { lec -> lec.copy(isCompleted = targetDone) },
                        )
                    },
                )
            }
        }
    }

    // --- Phase 1.6.1.1: Full Batches / Subjects / Chapters / Lectures CRUD ---

    fun openBatchCrudDialog(dialog: BatchCrudDialog) {
        _uiState.update { state ->
            state.copy(batchesState = state.batchesState.copy(activeCrudDialog = dialog))
        }
    }

    fun closeBatchCrudDialog() {
        _uiState.update { state ->
            state.copy(batchesState = state.batchesState.copy(activeCrudDialog = null))
        }
    }

    fun editBatch(
        batchId: String,
        name: String,
        goalTag: String,
        targetDateLabel: String,
        accentMood: AccentMood,
    ) {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return
        val cleanGoal = goalTag.trim().ifEmpty { "Custom Track" }
        val cleanTarget = targetDateLabel.trim().ifEmpty { "Active Track" }
        _uiState.update { state ->
            state.copy(
                batchesState = state.batchesState.copy(
                    batches = state.batchesState.batches.map { batch ->
                        if (batch.id != batchId) batch else batch.copy(
                            name = cleanName,
                            goalTag = cleanGoal,
                            targetDateLabel = cleanTarget,
                            accentMood = accentMood,
                        )
                    },
                    activeCrudDialog = null,
                ),
            )
        }
    }

    fun deleteBatch(batchId: String) {
        _uiState.update { state ->
            if (state.batchesState.batches.size <= 1) {
                return@update state.copy(batchesState = state.batchesState.copy(activeCrudDialog = null))
            }
            val remaining = state.batchesState.batches.filterNot { it.id == batchId }
            val fallbackBatch = remaining.first()
            val nextPrimaryId = if (state.primaryBatchId == batchId) fallbackBatch.id else state.primaryBatchId
            val nextSelectedId = if (state.batchesState.selectedBatchId == batchId) fallbackBatch.id else state.batchesState.selectedBatchId
            val nextVaultBatchId = if (state.pwVaultState.selectedBatchId == batchId) nextPrimaryId else state.pwVaultState.selectedBatchId
            val updatedMap = state.pwVaultState.subjectsByBatch - batchId
            val nextVaultSubjects = updatedMap[nextVaultBatchId].orEmpty()

            state.copy(
                primaryBatchId = nextPrimaryId,
                batchesState = state.batchesState.copy(
                    batches = remaining,
                    selectedBatchId = nextSelectedId,
                    expandedSubjectId = remaining.firstOrNull { it.id == nextSelectedId }?.subjects?.firstOrNull()?.id,
                    expandedChapterId = null,
                    activeCrudDialog = null,
                ),
                pwVaultState = state.pwVaultState.copy(
                    subjectsByBatch = updatedMap,
                    selectedBatchId = nextVaultBatchId,
                    selectedSubjectId = if (state.pwVaultState.selectedBatchId == batchId) null else state.pwVaultState.selectedSubjectId,
                    selectedChapterId = if (state.pwVaultState.selectedBatchId == batchId) null else state.pwVaultState.selectedChapterId,
                    selectedLectureId = if (state.pwVaultState.selectedBatchId == batchId) null else state.pwVaultState.selectedLectureId,
                    selectedRevisionSubjectId = nextVaultSubjects.firstOrNull()?.id,
                ),
            )
        }
    }

    fun addSubjectToBatch(
        batchId: String,
        name: String,
        accentMood: AccentMood,
    ) {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return
        val now = System.currentTimeMillis()
        val subjectId = "pw_${batchId}_sub_$now"
        val chapterId = "ch_${subjectId}_1"
        val lectureId = "lec_${chapterId}_1"
        val starterLecture = createStarterLecture(
            id = lectureId,
            batchId = batchId,
            subjectId = subjectId,
            subjectName = cleanName,
            subjectMood = accentMood,
            chapterId = chapterId,
            chapterTitle = "$cleanName Foundations",
            lectureNumber = 1,
            title = "$cleanName Core Concepts & Overview",
            durationLabel = "1h 00m",
        )
        val newSubject = VaultSubjectNode(
            id = subjectId,
            batchId = batchId,
            name = cleanName,
            accentMood = accentMood,
            chapters = listOf(
                VaultChapterNode(
                    id = chapterId,
                    batchId = batchId,
                    subjectId = subjectId,
                    subjectName = cleanName,
                    subjectMood = accentMood,
                    chapterCode = "CH 01",
                    title = "$cleanName Foundations",
                    lectures = listOf(starterLecture),
                ),
            ),
        )
        mutateVaultSubjectsForBatch(
            batchId = batchId,
            expandSubjectId = subjectId,
            expandChapterId = chapterId,
        ) { subjects ->
            subjects + newSubject
        }
    }

    fun editSubject(
        batchId: String,
        subjectId: String,
        name: String,
        accentMood: AccentMood,
    ) {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return
        mutateVaultSubjectsForBatch(batchId) { subjects ->
            subjects.map { subj ->
                if (subj.id != subjectId) return@map subj
                subj.copy(
                    name = cleanName,
                    accentMood = accentMood,
                    chapters = subj.chapters.map { ch ->
                        ch.copy(
                            subjectName = cleanName,
                            subjectMood = accentMood,
                            lectures = ch.lectures.map { lec ->
                                lec.copy(
                                    subjectName = cleanName,
                                    subjectMood = accentMood,
                                    flashcardDeck = lec.flashcardDeck.map { fc ->
                                        fc.copy(subjectName = cleanName, accentMood = accentMood)
                                    },
                                )
                            },
                        )
                    },
                )
            }
        }
    }

    fun deleteSubject(batchId: String, subjectId: String) {
        mutateVaultSubjectsForBatch(batchId) { subjects ->
            subjects.filterNot { it.id == subjectId }
        }
    }

    fun addChapterToSubject(
        batchId: String,
        subjectId: String,
        title: String,
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val now = System.currentTimeMillis()
        val chapterId = "ch_${subjectId}_$now"
        mutateVaultSubjectsForBatch(
            batchId = batchId,
            expandSubjectId = subjectId,
            expandChapterId = chapterId,
        ) { subjects ->
            subjects.map { subj ->
                if (subj.id != subjectId) return@map subj
                val nextIndex = subj.chapters.size + 1
                val code = "CH " + nextIndex.toString().padStart(2, '0')
                val starterLec = createStarterLecture(
                    id = "lec_${chapterId}_1",
                    batchId = batchId,
                    subjectId = subjectId,
                    subjectName = subj.name,
                    subjectMood = subj.accentMood,
                    chapterId = chapterId,
                    chapterTitle = cleanTitle,
                    lectureNumber = 1,
                    title = "$cleanTitle — Lecture 01",
                    durationLabel = "1h 10m",
                )
                subj.copy(
                    chapters = subj.chapters + VaultChapterNode(
                        id = chapterId,
                        batchId = batchId,
                        subjectId = subjectId,
                        subjectName = subj.name,
                        subjectMood = subj.accentMood,
                        chapterCode = code,
                        title = cleanTitle,
                        lectures = listOf(starterLec),
                    ),
                )
            }
        }
    }

    fun editChapter(
        batchId: String,
        subjectId: String,
        chapterId: String,
        title: String,
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        mutateVaultSubjectsForBatch(batchId) { subjects ->
            subjects.map { subj ->
                if (subj.id != subjectId) return@map subj
                subj.copy(
                    chapters = subj.chapters.map { ch ->
                        if (ch.id != chapterId) return@map ch
                        ch.copy(
                            title = cleanTitle,
                            lectures = ch.lectures.map { lec ->
                                lec.copy(
                                    chapterTitle = cleanTitle,
                                    flashcardDeck = lec.flashcardDeck.map { fc ->
                                        fc.copy(chapterTitle = cleanTitle)
                                    },
                                )
                            },
                        )
                    },
                )
            }
        }
    }

    fun deleteChapter(batchId: String, subjectId: String, chapterId: String) {
        mutateVaultSubjectsForBatch(batchId) { subjects ->
            subjects.map { subj ->
                if (subj.id != subjectId) return@map subj
                val remaining = subj.chapters.filterNot { it.id == chapterId }
                    .mapIndexed { idx, ch ->
                        ch.copy(chapterCode = "CH " + (idx + 1).toString().padStart(2, '0'))
                    }
                subj.copy(chapters = remaining)
            }
        }
    }

    fun addLectureToChapter(
        batchId: String,
        subjectId: String,
        chapterId: String,
        title: String,
        durationLabel: String,
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val cleanDuration = durationLabel.trim().ifEmpty { "1h 05m" }
        val lectureId = "lec_${chapterId}_${System.currentTimeMillis()}"
        mutateVaultSubjectsForBatch(
            batchId = batchId,
            expandSubjectId = subjectId,
            expandChapterId = chapterId,
        ) { subjects ->
            subjects.map { subj ->
                if (subj.id != subjectId) return@map subj
                subj.copy(
                    chapters = subj.chapters.map { ch ->
                        if (ch.id != chapterId) return@map ch
                        val nextNum = ch.lectures.size + 1
                        val newLecture = createStarterLecture(
                            id = lectureId,
                            batchId = batchId,
                            subjectId = subjectId,
                            subjectName = subj.name,
                            subjectMood = subj.accentMood,
                            chapterId = chapterId,
                            chapterTitle = ch.title,
                            lectureNumber = nextNum,
                            title = cleanTitle,
                            durationLabel = cleanDuration,
                        )
                        ch.copy(lectures = ch.lectures + newLecture)
                    },
                )
            }
        }
    }

    fun editLecture(
        batchId: String,
        subjectId: String,
        chapterId: String,
        lectureId: String,
        title: String,
        durationLabel: String,
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val cleanDuration = durationLabel.trim().ifEmpty { "1h 05m" }
        mutateVaultSubjectsForBatch(batchId) { subjects ->
            subjects.map { subj ->
                if (subj.id != subjectId) return@map subj
                subj.copy(
                    chapters = subj.chapters.map { ch ->
                        if (ch.id != chapterId) return@map ch
                        ch.copy(
                            lectures = ch.lectures.map { lec ->
                                if (lec.id != lectureId) return@map lec
                                val slug = cleanTitle.replace(Regex("[^A-Za-z0-9]+"), "_").take(26)
                                lec.copy(
                                    title = cleanTitle,
                                    durationLabel = cleanDuration,
                                    notesTitle = "${lec.lectureCode}_${slug}_Notes.pdf",
                                    classLabel = "Class ${lec.lectureNumber.toString().padStart(2, '0')} · ${cleanTitle.take(24)}",
                                )
                            },
                        )
                    },
                )
            }
        }
    }

    fun toggleLectureCompleted(
        batchId: String,
        subjectId: String,
        chapterId: String,
        lectureId: String,
    ) {
        mutateVaultSubjectsForBatch(batchId) { subjects ->
            subjects.map { subj ->
                if (subj.id != subjectId) return@map subj
                subj.copy(
                    chapters = subj.chapters.map { ch ->
                        if (ch.id != chapterId) return@map ch
                        ch.copy(
                            lectures = ch.lectures.map { lec ->
                                if (lec.id != lectureId) lec else lec.copy(isCompleted = !lec.isCompleted)
                            },
                        )
                    },
                )
            }
        }
    }

    fun deleteLecture(
        batchId: String,
        subjectId: String,
        chapterId: String,
        lectureId: String,
    ) {
        mutateVaultSubjectsForBatch(batchId) { subjects ->
            subjects.map { subj ->
                if (subj.id != subjectId) return@map subj
                subj.copy(
                    chapters = subj.chapters.map { ch ->
                        if (ch.id != chapterId) return@map ch
                        val remaining = ch.lectures.filterNot { it.id == lectureId }
                            .mapIndexed { idx, lec ->
                                val num = idx + 1
                                lec.copy(
                                    lectureNumber = num,
                                    lectureCode = "L" + num.toString().padStart(2, '0'),
                                )
                            }
                        ch.copy(lectures = remaining)
                    },
                )
            }
        }
    }

    private fun mutateVaultSubjectsForBatch(
        batchId: String,
        expandSubjectId: String? = null,
        expandChapterId: String? = null,
        transform: (List<VaultSubjectNode>) -> List<VaultSubjectNode>,
    ) {
        _uiState.update { state ->
            val currentSubjects = state.pwVaultState.subjectsByBatch[batchId].orEmpty()
            val updatedSubjects = transform(currentSubjects)
            val updatedMap = state.pwVaultState.subjectsByBatch + (batchId to updatedSubjects)
            val syncedBatchSubjects = buildBatchSubjectsFromVault(updatedSubjects)

            val updatedBatches = state.batchesState.batches.map { batch ->
                if (batch.id != batchId) batch else batch.copy(subjects = syncedBatchSubjects)
            }

            // Clean up any dangling Vault selection pointers if an item was deleted
            val validSubject = updatedSubjects.firstOrNull { it.id == state.pwVaultState.selectedSubjectId }
            val validChapter = validSubject?.chapters?.firstOrNull { it.id == state.pwVaultState.selectedChapterId }
            val validLecture = validChapter?.lectures?.firstOrNull { it.id == state.pwVaultState.selectedLectureId }

            state.copy(
                batchesState = state.batchesState.copy(
                    batches = updatedBatches,
                    expandedSubjectId = expandSubjectId ?: state.batchesState.expandedSubjectId?.takeIf { id ->
                        syncedBatchSubjects.any { it.id == id }
                    },
                    expandedChapterId = expandChapterId ?: state.batchesState.expandedChapterId,
                    activeCrudDialog = null,
                ),
                pwVaultState = state.pwVaultState.copy(
                    subjectsByBatch = updatedMap,
                    selectedSubjectId = if (state.pwVaultState.selectedBatchId == batchId) validSubject?.id else state.pwVaultState.selectedSubjectId,
                    selectedChapterId = if (state.pwVaultState.selectedBatchId == batchId) validChapter?.id else state.pwVaultState.selectedChapterId,
                    selectedLectureId = if (state.pwVaultState.selectedBatchId == batchId) validLecture?.id else state.pwVaultState.selectedLectureId,
                ),
            )
        }
    }

    private fun createStarterLecture(
        id: String,
        batchId: String,
        subjectId: String,
        subjectName: String,
        subjectMood: AccentMood,
        chapterId: String,
        chapterTitle: String,
        lectureNumber: Int,
        title: String,
        durationLabel: String,
    ): VaultLectureNode {
        val code = "L" + lectureNumber.toString().padStart(2, '0')
        val slug = title.replace(Regex("[^A-Za-z0-9]+"), "_").take(26)
        val classLabel = "Class ${lectureNumber.toString().padStart(2, '0')} · ${title.take(24)}"
        return VaultLectureNode(
            id = id,
            batchId = batchId,
            subjectId = subjectId,
            subjectName = subjectName,
            subjectMood = subjectMood,
            chapterId = chapterId,
            chapterTitle = chapterTitle,
            lectureNumber = lectureNumber,
            lectureCode = code,
            title = title,
            durationLabel = durationLabel,
            dateLabel = "Custom Lecture",
            isCompleted = false,
            notesTitle = "${code}_${slug}_Notes.pdf",
            notesPageCount = 16,
            notesSummary = "Class notes and structured derivations for $title ($chapterTitle).",
            notesHighlights = listOf(
                "Core concepts & solved examples for $title",
                "Indexed for chapter RAG attachment in AI Tutor",
            ),
            classLabel = classLabel,
            flashcardDeck = listOf(
                DailyFlashcardItem(
                    id = "fc_${id}_1",
                    subjectName = subjectName,
                    accentMood = subjectMood,
                    chapterTitle = chapterTitle,
                    classLabel = classLabel,
                    frontPrompt = "What is the key conceptual takeaway from $title?",
                    backAnswer = "Master the core relationship and boundary conditions introduced in $title before timed drills.",
                    formulaHint = "$code · $chapterTitle",
                ),
            ),
            revisionHeadline = "Focused revision takeaways for $title.",
            revisionFormula = "$subjectName · $code Key Relationship",
            revisionKeyPoints = listOf(
                "Review primary derivation steps from $title",
                "Check common boundary traps before advancing",
            ),
        )
    }

    private fun buildBatchSubjectsFromVault(vaultSubjects: List<VaultSubjectNode>): List<BatchSubject> {
        return vaultSubjects.map { vs ->
            val batchChapters = vs.chapters.map { ch ->
                val total = ch.lectures.size
                val done = ch.lectures.count { it.isCompleted }
                val pct = if (total == 0) 0 else (done * 100) / total
                BatchChapter(
                    id = ch.id,
                    title = ch.title,
                    lectureCount = total,
                    progressPercent = pct,
                    isCompleted = total > 0 && done == total,
                    materialCount = total.coerceAtLeast(1),
                    lectures = ch.lectures,
                )
            }
            val totalLecs = vs.totalLectures
            val doneLecs = vs.completedLectures
            val mastery = if (totalLecs == 0) 0 else (doneLecs * 100) / totalLecs
            BatchSubject(
                id = vs.id,
                name = vs.name,
                accentMood = vs.accentMood,
                lectureCount = totalLecs,
                completedLectures = doneLecs,
                masteryPercent = mastery,
                chapters = batchChapters,
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

        val starterPwSubjects = baseSubjects.mapIndexed { sIdx, template ->
            val subjectId = "pw_${batchId}_sub_$sIdx"
            val ch1Id = "${subjectId}_ch_1"
            val ch2Id = "${subjectId}_ch_2"
            val ch1Title = "${template.name} Foundations & Core Laws"
            val ch2Title = "${template.name} High-Yield Problem Patterns"
            VaultSubjectNode(
                id = subjectId,
                batchId = batchId,
                name = template.name,
                accentMood = template.accentMood,
                chapters = listOf(
                    VaultChapterNode(
                        id = ch1Id,
                        batchId = batchId,
                        subjectId = subjectId,
                        subjectName = template.name,
                        subjectMood = template.accentMood,
                        chapterCode = "CH 01",
                        title = ch1Title,
                        lectures = listOf(
                            createStarterLecture(
                                id = "pw_lec_${ch1Id}_01",
                                batchId = batchId,
                                subjectId = subjectId,
                                subjectName = template.name,
                                subjectMood = template.accentMood,
                                chapterId = ch1Id,
                                chapterTitle = ch1Title,
                                lectureNumber = 1,
                                title = "${template.name} Kickoff & Core Principles",
                                durationLabel = "1h 05m",
                            ),
                            createStarterLecture(
                                id = "pw_lec_${ch1Id}_02",
                                batchId = batchId,
                                subjectId = subjectId,
                                subjectName = template.name,
                                subjectMood = template.accentMood,
                                chapterId = ch1Id,
                                chapterTitle = ch1Title,
                                lectureNumber = 2,
                                title = "${template.name} Derivations & Boundary Cases",
                                durationLabel = "1h 12m",
                            ),
                        ),
                    ),
                    VaultChapterNode(
                        id = ch2Id,
                        batchId = batchId,
                        subjectId = subjectId,
                        subjectName = template.name,
                        subjectMood = template.accentMood,
                        chapterCode = "CH 02",
                        title = ch2Title,
                        lectures = listOf(
                            createStarterLecture(
                                id = "pw_lec_${ch2Id}_01",
                                batchId = batchId,
                                subjectId = subjectId,
                                subjectName = template.name,
                                subjectMood = template.accentMood,
                                chapterId = ch2Id,
                                chapterTitle = ch2Title,
                                lectureNumber = 1,
                                title = "${template.name} Speed Drills & PYQ Patterns",
                                durationLabel = "58m",
                            ),
                        ),
                    ),
                ),
            )
        }

        val createdSubjects = buildBatchSubjectsFromVault(starterPwSubjects)

        val newBatch = StudyBatch(
            id = batchId,
            name = trimmedName,
            goalTag = goalTag,
            accentMood = accentMood,
            targetDateLabel = "Custom Track",
            totalHoursLogged = 0,
            subjects = createdSubjects,
        )

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
                    expandedChapterId = firstSubject.chapters.firstOrNull()?.id,
                    isBuilderOpen = false,
                ),
                vaultState = state.vaultState.copy(
                    materials = listOf(starterMaterial) + state.vaultState.materials,
                ),
                pwVaultState = state.pwVaultState.copy(
                    subjectsByBatch = state.pwVaultState.subjectsByBatch + (batchId to starterPwSubjects),
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

    // --- Phase 1.4 Spaced Flashcards & Mistake Bank Actions ---

    fun selectFlashcardHubMode(mode: FlashcardHubMode) {
        _uiState.update { state ->
            state.copy(
                flashcardsState = state.flashcardsState.copy(activeMode = mode),
            )
        }
    }

    fun selectSpacedBucket(bucket: SpacedIntervalBucket) {
        _uiState.update { state ->
            state.copy(
                flashcardsState = state.flashcardsState.copy(
                    activeBucket = bucket,
                    isCardFlipped = false,
                    deckNonce = state.flashcardsState.deckNonce + 1,
                ),
            )
        }
    }

    fun selectFlashcardSubjectFilter(subjectName: String?) {
        _uiState.update { state ->
            state.copy(
                flashcardsState = state.flashcardsState.copy(
                    selectedCardSubject = subjectName,
                    isCardFlipped = false,
                    deckNonce = state.flashcardsState.deckNonce + 1,
                ),
            )
        }
    }

    fun toggleFlashcardFlip() {
        _uiState.update { state ->
            state.copy(
                flashcardsState = state.flashcardsState.copy(
                    isCardFlipped = !state.flashcardsState.isCardFlipped,
                ),
            )
        }
    }

    fun rateFlashcard(cardId: String, rating: FlashcardRating) {
        _uiState.update { state ->
            val current = state.flashcardsState
            val updatedGood = if (rating == FlashcardRating.GOOD || rating == FlashcardRating.EASY) {
                current.goodOrEasyCount + 1
            } else {
                current.goodOrEasyCount
            }
            val updatedHard = if (rating == FlashcardRating.AGAIN || rating == FlashcardRating.HARD) {
                current.hardOrAgainCount + 1
            } else {
                current.hardOrAgainCount
            }

            // "Again" pushes card to back of current session queue; Hard/Good/Easy marks reviewed
            val updatedCompleted = if (rating == FlashcardRating.AGAIN) {
                current.completedCardIds - cardId
            } else {
                current.completedCardIds + cardId
            }
            val updatedRequeued = if (rating == FlashcardRating.AGAIN) {
                (current.requeuedCardIds - cardId) + cardId
            } else {
                current.requeuedCardIds - cardId
            }

            state.copy(
                flashcardsState = current.copy(
                    completedCardIds = updatedCompleted,
                    requeuedCardIds = updatedRequeued,
                    isCardFlipped = false,
                    goodOrEasyCount = updatedGood,
                    hardOrAgainCount = updatedHard,
                    deckNonce = current.deckNonce + 1,
                ),
            )
        }
    }

    fun resetFlashcardSession() {
        _uiState.update { state ->
            val current = state.flashcardsState
            val bucketCardIds = current.cardsInBucket(current.activeBucket, current.selectedCardSubject)
                .map { it.id }
                .toSet()
            state.copy(
                flashcardsState = current.copy(
                    completedCardIds = current.completedCardIds - bucketCardIds,
                    requeuedCardIds = current.requeuedCardIds.filterNot { it in bucketCardIds },
                    isCardFlipped = false,
                    deckNonce = current.deckNonce + 1,
                ),
            )
        }
    }

    fun selectMistakeCategoryFilter(category: MistakeCategory?) {
        _uiState.update { state ->
            val updated = state.flashcardsState.copy(selectedMistakeCategory = category)
            state.copy(
                flashcardsState = updated.copy(
                    selectedMistakeId = updated.filteredMistakes.firstOrNull()?.id,
                ),
            )
        }
    }

    fun selectMistakeSubjectFilter(subjectName: String?) {
        _uiState.update { state ->
            val updated = state.flashcardsState.copy(selectedMistakeSubject = subjectName)
            state.copy(
                flashcardsState = updated.copy(
                    selectedMistakeId = updated.filteredMistakes.firstOrNull()?.id,
                ),
            )
        }
    }

    fun selectMistakeRecord(mistakeId: String?) {
        _uiState.update { state ->
            state.copy(
                flashcardsState = state.flashcardsState.copy(selectedMistakeId = mistakeId),
            )
        }
    }

    fun sendMistakeToWeekendHardDeck(mistakeId: String) {
        _uiState.update { state ->
            val current = state.flashcardsState
            val target = current.mistakes.firstOrNull { it.id == mistakeId } ?: return@update state
            if (target.addedToWeekendDeck) return@update state

            val promotedCard = SpacedFlashcard(
                id = "fc_from_mistake_${target.id}",
                bucket = SpacedIntervalBucket.WEEKEND_HARD,
                subjectName = target.subjectName,
                accentMood = target.accentMood,
                chapterTag = target.chapterTitle,
                intervalLabel = "WKND",
                frontPrompt = target.questionSummary,
                backAnswer = target.correctConcept,
                formulaOrHint = target.formulaAnchor,
                trapNote = "Previous Mistake (${target.category.label}): ${target.wrongPick}",
            )

            val updatedMistakes = current.mistakes.map { item ->
                if (item.id == mistakeId) item.copy(addedToWeekendDeck = true) else item
            }

            state.copy(
                flashcardsState = current.copy(
                    mistakes = updatedMistakes,
                    allCards = listOf(promotedCard) + current.allCards,
                    completedCardIds = current.completedCardIds - promotedCard.id,
                ),
            )
        }
    }

    // --- Phase 1.5 Chapter RAG AI Tutor & Adaptive Wake-to-Sleep Planner Actions ---

    fun selectTutorPlannerMode(mode: TutorPlannerMode) {
        _uiState.update { state ->
            state.copy(
                tutorPlannerState = state.tutorPlannerState.copy(
                    activeMode = mode,
                    isCoPlanSplitEnabled = false,
                ),
            )
        }
    }

    fun toggleCoPlanSplit() {
        _uiState.update { state ->
            val current = state.tutorPlannerState
            state.copy(
                tutorPlannerState = current.copy(
                    isCoPlanSplitEnabled = !current.isCoPlanSplitEnabled,
                ),
            )
        }
    }

    fun selectRagChapter(chapterId: String) {
        _uiState.update { state ->
            state.copy(
                tutorPlannerState = state.tutorPlannerState.copy(
                    selectedChapterId = chapterId,
                ),
            )
        }
    }

    fun sendTutorDoubt(rawPrompt: String) {
        val trimmed = rawPrompt.trim()
        if (trimmed.isEmpty()) return

        val chapter = _uiState.value.tutorPlannerState.selectedChapter
        val userMsg = TutorMessage(
            id = "usr_${System.nanoTime()}",
            isUser = true,
            text = trimmed,
            timestampLabel = "Just now",
        )

        _uiState.update { state ->
            val current = state.tutorPlannerState
            val existing = current.messagesByChapter[chapter.id].orEmpty()
            state.copy(
                tutorPlannerState = current.copy(
                    messagesByChapter = current.messagesByChapter + (chapter.id to (existing + userMsg)),
                    isTutorTyping = true,
                ),
            )
        }

        tutorReplyJob?.cancel()
        tutorReplyJob = viewModelScope.launch {
            delay(850L)
            val reply = Phase15MockData.generateMockCitedReply(
                chapter = chapter,
                userQuestion = trimmed,
                timestampLabel = "Just now",
            )
            _uiState.update { state ->
                val current = state.tutorPlannerState
                val existing = current.messagesByChapter[chapter.id].orEmpty()
                val updatedChapters = current.chapters.map { ctx ->
                    if (ctx.id == chapter.id) {
                        ctx.copy(memoryTurnsCount = ctx.memoryTurnsCount + 1)
                    } else {
                        ctx
                    }
                }
                state.copy(
                    tutorPlannerState = current.copy(
                        chapters = updatedChapters,
                        messagesByChapter = current.messagesByChapter + (chapter.id to (existing + reply)),
                        isTutorTyping = false,
                    ),
                )
            }
        }
    }

    fun openPlannerScheduleSettings() {
        _uiState.update { state ->
            state.copy(
                tutorPlannerState = state.tutorPlannerState.copy(isScheduleSettingsOpen = true),
            )
        }
    }

    fun closePlannerScheduleSettings() {
        _uiState.update { state ->
            state.copy(
                tutorPlannerState = state.tutorPlannerState.copy(isScheduleSettingsOpen = false),
            )
        }
    }

    fun toggleAdaptiveSync(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                tutorPlannerState = state.tutorPlannerState.copy(isAdaptiveSyncEnabled = enabled),
            )
        }
    }

    fun stepManualWakeMinutes(deltaMinutes: Int) {
        _uiState.update { state ->
            val current = state.tutorPlannerState
            val nextWake = (current.manualWakeMinutes + deltaMinutes).coerceIn(240, 600) // 04:00..10:00
            state.copy(
                tutorPlannerState = current.copy(
                    manualWakeMinutes = nextWake,
                    isAdaptiveSyncEnabled = false,
                ),
            )
        }
    }

    fun stepManualSleepMinutes(deltaMinutes: Int) {
        _uiState.update { state ->
            val current = state.tutorPlannerState
            val nextSleep = (current.manualSleepMinutes + deltaMinutes).coerceIn(1200, 1425) // 20:00..23:45
            state.copy(
                tutorPlannerState = current.copy(
                    manualSleepMinutes = nextSleep,
                    isAdaptiveSyncEnabled = false,
                ),
            )
        }
    }

    fun togglePlannerBlockCompleted(blockId: String) {
        _uiState.update { state ->
            val updatedBlocks = state.tutorPlannerState.blocks.map { block ->
                if (block.id == blockId) block.copy(isCompleted = !block.isCompleted) else block
            }
            state.copy(
                tutorPlannerState = state.tutorPlannerState.copy(blocks = updatedBlocks),
            )
        }
    }

    fun openPermissionRequestForAdd() {
        val current = _uiState.value.tutorPlannerState
        val defaultStart = (current.effectiveWakeMinutes + 600).coerceAtMost(current.effectiveSleepMinutes - 60)
        _uiState.update { state ->
            state.copy(
                tutorPlannerState = state.tutorPlannerState.copy(
                    activePermissionRequest = AiPermissionRequest(
                        targetBlockId = null,
                        targetBlockTitle = null,
                        targetIsCritical = false,
                        action = PermissionRequestAction.ADD_BLOCK,
                        proposedTitle = "Targeted Weak-Topic Drill",
                        proposedCategory = PlannerBlockCategory.REVISION,
                        proposedStartMinutes = defaultStart,
                        proposedDurationMinutes = 45,
                        reasonText = "Need extra focused slot for Gauss's Law cavity problems.",
                    ),
                ),
            )
        }
    }

    fun openPermissionRequestForEdit(blockId: String) {
        val block = _uiState.value.tutorPlannerState.blocks.firstOrNull { it.id == blockId } ?: return
        _uiState.update { state ->
            state.copy(
                tutorPlannerState = state.tutorPlannerState.copy(
                    activePermissionRequest = AiPermissionRequest(
                        targetBlockId = block.id,
                        targetBlockTitle = block.title,
                        targetIsCritical = block.isCriticalPriority,
                        action = PermissionRequestAction.EDIT_BLOCK,
                        proposedTitle = block.title,
                        proposedCategory = block.category,
                        proposedStartMinutes = block.startMinutes,
                        proposedDurationMinutes = block.durationMinutes,
                        reasonText = "",
                    ),
                ),
            )
        }
    }

    fun closePermissionRequest() {
        permissionEvalJob?.cancel()
        _uiState.update { state ->
            state.copy(
                tutorPlannerState = state.tutorPlannerState.copy(activePermissionRequest = null),
            )
        }
    }

    fun updatePermissionRequestDraft(
        action: PermissionRequestAction? = null,
        proposedTitle: String? = null,
        proposedCategory: PlannerBlockCategory? = null,
        proposedStartMinutes: Int? = null,
        proposedDurationMinutes: Int? = null,
        reasonText: String? = null,
    ) {
        _uiState.update { state ->
            val req = state.tutorPlannerState.activePermissionRequest ?: return@update state
            state.copy(
                tutorPlannerState = state.tutorPlannerState.copy(
                    activePermissionRequest = req.copy(
                        action = action ?: req.action,
                        proposedTitle = proposedTitle ?: req.proposedTitle,
                        proposedCategory = proposedCategory ?: req.proposedCategory,
                        proposedStartMinutes = (proposedStartMinutes ?: req.proposedStartMinutes).coerceIn(300, 1410),
                        proposedDurationMinutes = (proposedDurationMinutes ?: req.proposedDurationMinutes).coerceIn(15, 150),
                        reasonText = reasonText ?: req.reasonText,
                        phase = PermissionVerdictPhase.DRAFTING,
                        verdictHeadline = "",
                        verdictExplanation = "",
                        counterProposal = null,
                    ),
                ),
            )
        }
    }

    fun submitPermissionRequestToAi() {
        val currentState = _uiState.value.tutorPlannerState
        val req = currentState.activePermissionRequest ?: return

        _uiState.update { state ->
            state.copy(
                tutorPlannerState = state.tutorPlannerState.copy(
                    isCoPlanSplitEnabled = true,
                    activePermissionRequest = req.copy(phase = PermissionVerdictPhase.EVALUATING),
                ),
            )
        }

        permissionEvalJob?.cancel()
        permissionEvalJob = viewModelScope.launch {
            delay(720L)
            _uiState.update { state ->
                val planner = state.tutorPlannerState
                val latestReq = planner.activePermissionRequest ?: return@update state
                val wake = planner.effectiveWakeMinutes
                val sleep = planner.effectiveSleepMinutes
                val endMins = latestReq.proposedStartMinutes + latestReq.proposedDurationMinutes

                val evaluatedReq = when {
                    latestReq.action == PermissionRequestAction.DELETE_BLOCK && latestReq.targetIsCritical -> {
                        val safeStart = latestReq.proposedStartMinutes
                        latestReq.copy(
                            phase = PermissionVerdictPhase.BLOCKED,
                            verdictHeadline = "Protected Spaced-Recall Block",
                            verdictExplanation = "Skipping '${latestReq.targetBlockTitle ?: latestReq.proposedTitle}' breaks your x-1 / Quiz retention curve. Direct removal stays locked.",
                            counterProposal = CounterProposal(
                                title = "${latestReq.targetBlockTitle ?: latestReq.proposedTitle} (Micro-Sprint)",
                                category = latestReq.proposedCategory,
                                startMinutes = safeStart,
                                durationMinutes = 30,
                                summary = "Keep the slot intact as a focused 30 min sprint instead of skipping.",
                            ),
                        )
                    }

                    latestReq.action != PermissionRequestAction.DELETE_BLOCK && endMins > sleep -> {
                        val safeDuration = latestReq.proposedDurationMinutes.coerceAtMost(60)
                        val safeStart = (sleep - safeDuration - 15).coerceAtLeast(wake + 30)
                        latestReq.copy(
                            phase = PermissionVerdictPhase.BLOCKED,
                            verdictHeadline = "Sleep Boundary Violation",
                            verdictExplanation = "Proposed end time (${formatClockMinutes(endMins)}) crosses your ${formatClockMinutes(sleep)} Sleep Boundary. Direct apply stays locked to protect recovery.",
                            counterProposal = CounterProposal(
                                title = latestReq.proposedTitle.ifBlank { "Focused Study Block" },
                                category = latestReq.proposedCategory,
                                startMinutes = safeStart,
                                durationMinutes = safeDuration,
                                summary = "Shift to ${formatClockMinutes(safeStart)}–${formatClockMinutes(safeStart + safeDuration)} so you wind down 15 min before Sleep Anchor.",
                            ),
                        )
                    }

                    latestReq.action != PermissionRequestAction.DELETE_BLOCK && latestReq.proposedStartMinutes < wake -> {
                        val safeStart = wake + 15
                        latestReq.copy(
                            phase = PermissionVerdictPhase.BLOCKED,
                            verdictHeadline = "Precedes Wake Anchor",
                            verdictExplanation = "Start time (${formatClockMinutes(latestReq.proposedStartMinutes)}) is earlier than your ${formatClockMinutes(wake)} Wake Anchor.",
                            counterProposal = CounterProposal(
                                title = latestReq.proposedTitle.ifBlank { "Morning Study Block" },
                                category = latestReq.proposedCategory,
                                startMinutes = safeStart,
                                durationMinutes = latestReq.proposedDurationMinutes,
                                summary = "Start at ${formatClockMinutes(safeStart)} right after your observed wake-up buffer.",
                            ),
                        )
                    }

                    latestReq.action != PermissionRequestAction.DELETE_BLOCK &&
                        latestReq.proposedCategory == PlannerBlockCategory.BREAK_MEAL &&
                        latestReq.proposedDurationMinutes > 60 -> {
                        latestReq.copy(
                            phase = PermissionVerdictPhase.BLOCKED,
                            verdictHeadline = "Break Exceeds Focus Budget",
                            verdictExplanation = "A ${latestReq.proposedDurationMinutes} min break cuts into your ${planner.remainingSyllabusChapters}-chapter remaining syllabus pace. Direct edit remains locked.",
                            counterProposal = CounterProposal(
                                title = latestReq.proposedTitle.ifBlank { "Structured Recovery Break" },
                                category = PlannerBlockCategory.BREAK_MEAL,
                                startMinutes = latestReq.proposedStartMinutes,
                                durationMinutes = 30,
                                summary = "Approve a clean 30 min recovery break without slipping tonight's Maths sprint.",
                            ),
                        )
                    }

                    else -> {
                        latestReq.copy(
                            phase = PermissionVerdictPhase.APPROVED,
                            verdictHeadline = "Schedule Rule Check Passed",
                            verdictExplanation = "Fits cleanly inside your ${formatClockMinutes(wake)}–${formatClockMinutes(sleep)} awake window and preserves all protected x-1 & Quiz blocks. Edit controls are now unlocked.",
                            counterProposal = null,
                        )
                    }
                }

                // Append a synchronized AI Planner Guardrail audit note into the active Tutor stream
                val activeChapterId = planner.selectedChapter.id
                val auditNote = TutorMessage(
                    id = "audit_${System.nanoTime()}",
                    isUser = false,
                    text = if (evaluatedReq.phase == PermissionVerdictPhase.APPROVED) {
                        "Planner Permission Approved: '${evaluatedReq.proposedTitle}' (${formatClockMinutes(evaluatedReq.proposedStartMinutes)}, ${evaluatedReq.proposedDurationMinutes}m) satisfies your Wake-to-Sleep and x-1 rules. Confirm in the Planner pane to apply."
                    } else {
                        "Planner Permission Blocked (${evaluatedReq.verdictHeadline}): ${evaluatedReq.verdictExplanation}"
                    },
                    timestampLabel = "Co-Plan",
                    isPlannerAuditNote = true,
                )
                val existingMsgs = planner.messagesByChapter[activeChapterId].orEmpty()

                state.copy(
                    tutorPlannerState = planner.copy(
                        activePermissionRequest = evaluatedReq,
                        messagesByChapter = planner.messagesByChapter + (activeChapterId to (existingMsgs + auditNote)),
                    ),
                )
            }
        }
    }

    fun applyApprovedPermissionChange() {
        _uiState.update { state ->
            val planner = state.tutorPlannerState
            val req = planner.activePermissionRequest ?: return@update state
            if (req.phase != PermissionVerdictPhase.APPROVED) return@update state

            val updatedBlocks = when (req.action) {
                PermissionRequestAction.DELETE_BLOCK -> {
                    planner.blocks.filterNot { it.id == req.targetBlockId }
                }

                PermissionRequestAction.EDIT_BLOCK -> {
                    planner.blocks.map { blk ->
                        if (blk.id == req.targetBlockId) {
                            blk.copy(
                                title = req.proposedTitle.ifBlank { blk.title },
                                category = req.proposedCategory,
                                startMinutes = req.proposedStartMinutes,
                                durationMinutes = req.proposedDurationMinutes,
                                approvalStatus = PlannerApprovalStatus.AI_APPROVED,
                            )
                        } else {
                            blk
                        }
                    }.sortedBy { it.startMinutes }
                }

                PermissionRequestAction.ADD_BLOCK -> {
                    val mood = when (req.proposedCategory) {
                        PlannerBlockCategory.STUDY -> AccentMood.CYAN
                        PlannerBlockCategory.REVISION -> AccentMood.VIOLET
                        PlannerBlockCategory.QUIZ -> AccentMood.AMBER
                        PlannerBlockCategory.FLASHCARDS -> AccentMood.EMERALD
                        PlannerBlockCategory.BREAK_MEAL -> AccentMood.EMERALD
                    }
                    val newBlock = PlannerBlock(
                        id = "blk_ai_${System.nanoTime()}",
                        title = req.proposedTitle.ifBlank { "AI-Approved Study Block" },
                        subtitle = req.reasonText.ifBlank { "Added via AI Schedule Permission Gate" },
                        category = req.proposedCategory,
                        subjectName = req.proposedCategory.label,
                        accentMood = mood,
                        startMinutes = req.proposedStartMinutes,
                        durationMinutes = req.proposedDurationMinutes,
                        isCompleted = false,
                        isCriticalPriority = false,
                        approvalStatus = PlannerApprovalStatus.AI_APPROVED,
                    )
                    (planner.blocks + newBlock).sortedBy { it.startMinutes }
                }
            }

            state.copy(
                tutorPlannerState = planner.copy(
                    blocks = updatedBlocks,
                    activePermissionRequest = null,
                ),
            )
        }
    }

    fun acceptPermissionCounterProposal() {
        _uiState.update { state ->
            val planner = state.tutorPlannerState
            val req = planner.activePermissionRequest ?: return@update state
            val counter = req.counterProposal ?: return@update state

            val updatedBlocks = if (req.targetBlockId != null) {
                planner.blocks.map { blk ->
                    if (blk.id == req.targetBlockId) {
                        blk.copy(
                            title = counter.title,
                            category = counter.category,
                            startMinutes = counter.startMinutes,
                            durationMinutes = counter.durationMinutes,
                            approvalStatus = PlannerApprovalStatus.AI_APPROVED,
                        )
                    } else {
                        blk
                    }
                }.sortedBy { it.startMinutes }
            } else {
                val mood = when (counter.category) {
                    PlannerBlockCategory.STUDY -> AccentMood.CYAN
                    PlannerBlockCategory.REVISION -> AccentMood.VIOLET
                    PlannerBlockCategory.QUIZ -> AccentMood.AMBER
                    PlannerBlockCategory.FLASHCARDS -> AccentMood.EMERALD
                    PlannerBlockCategory.BREAK_MEAL -> AccentMood.EMERALD
                }
                val newBlock = PlannerBlock(
                    id = "blk_counter_${System.nanoTime()}",
                    title = counter.title,
                    subtitle = counter.summary,
                    category = counter.category,
                    subjectName = counter.category.label,
                    accentMood = mood,
                    startMinutes = counter.startMinutes,
                    durationMinutes = counter.durationMinutes,
                    isCompleted = false,
                    isCriticalPriority = false,
                    approvalStatus = PlannerApprovalStatus.AI_APPROVED,
                )
                (planner.blocks + newBlock).sortedBy { it.startMinutes }
            }

            state.copy(
                tutorPlannerState = planner.copy(
                    blocks = updatedBlocks,
                    activePermissionRequest = null,
                ),
            )
        }
    }

    // --- Phase 1.6.1: Primary Batch Sync, Study Gate, Dedicated Daily Pages & PW-Style Vault ---

    fun setPrimaryBatch(batchId: String) {
        _uiState.update { state ->
            val targetBatch = state.batchesState.batches.firstOrNull { it.id == batchId } ?: return@update state
            val nextSubjects = state.pwVaultState.subjectsByBatch[batchId].orEmpty()
            val firstSubject = nextSubjects.firstOrNull()
            val firstChapter = firstSubject?.chapters?.firstOrNull()
            state.copy(
                primaryBatchId = batchId,
                batchesState = state.batchesState.copy(
                    selectedBatchId = batchId,
                    expandedSubjectId = targetBatch.subjects.firstOrNull()?.id,
                ),
                pwVaultState = state.pwVaultState.copy(
                    selectedBatchId = batchId,
                    selectedSubjectId = null,
                    selectedChapterId = null,
                    selectedLectureId = null,
                    expandedClassNoteLectureId = null,
                    expandedFlashcardChapterId = firstChapter?.id,
                    previewFlashcardId = null,
                    isPreviewCardFlipped = false,
                    selectedRevisionSubjectId = firstSubject?.id,
                    paletteFilterSubjectId = null,
                ),
            )
        }
    }

    fun cyclePrimaryBatch() {
        val state = _uiState.value
        val batches = state.batchesState.batches
        if (batches.size <= 1) return
        val currentIdx = batches.indexOfFirst { it.id == state.primaryBatchId }.coerceAtLeast(0)
        val nextBatch = batches[(currentIdx + 1) % batches.size]
        setPrimaryBatch(nextBatch.id)
    }

    fun openDailyPage(route: DailyPageRoute) {
        _uiState.update { state ->
            state.copy(
                homeCommandState = state.homeCommandState.copy(
                    activeDailyPage = route,
                    activeFlashcardIndex = 0,
                    isActiveFlashcardFlipped = false,
                ),
            )
        }
    }

    fun closeDailyPage() {
        _uiState.update { state ->
            state.copy(
                homeCommandState = state.homeCommandState.copy(
                    activeDailyPage = null,
                    isActiveFlashcardFlipped = false,
                ),
            )
        }
    }

    fun completeTodayRevision() {
        _uiState.update { state ->
            state.copy(
                homeCommandState = state.homeCommandState.copy(
                    todayRevisionCompleted = true,
                    activeDailyPage = null,
                ),
            )
        }
    }

    fun flipDailyFlashcard() {
        _uiState.update { state ->
            state.copy(
                homeCommandState = state.homeCommandState.copy(
                    isActiveFlashcardFlipped = !state.homeCommandState.isActiveFlashcardFlipped,
                ),
            )
        }
    }

    fun stepDailyFlashcard(delta: Int) {
        _uiState.update { state ->
            val count = state.homeCommandState.todayFlashcards.size.coerceAtLeast(1)
            val nextIdx = (state.homeCommandState.activeFlashcardIndex + delta).coerceIn(0, count - 1)
            state.copy(
                homeCommandState = state.homeCommandState.copy(
                    activeFlashcardIndex = nextIdx,
                    isActiveFlashcardFlipped = false,
                ),
            )
        }
    }

    fun completeTodayFlashcards() {
        _uiState.update { state ->
            state.copy(
                homeCommandState = state.homeCommandState.copy(
                    todayFlashcardsCompleted = true,
                    activeDailyPage = null,
                    isActiveFlashcardFlipped = false,
                ),
            )
        }
    }

    fun toggleTodayRevisionCompleted() {
        _uiState.update { state ->
            state.copy(
                homeCommandState = state.homeCommandState.copy(
                    todayRevisionCompleted = !state.homeCommandState.todayRevisionCompleted,
                ),
            )
        }
    }

    fun toggleTodayFlashcardsCompleted() {
        _uiState.update { state ->
            state.copy(
                homeCommandState = state.homeCommandState.copy(
                    todayFlashcardsCompleted = !state.homeCommandState.todayFlashcardsCompleted,
                ),
            )
        }
    }

    fun toggleStudyGateTestOverride() {
        _uiState.update { state ->
            val nowUnlocked = !state.homeCommandState.isStudyGateUnlocked
            state.copy(
                homeCommandState = state.homeCommandState.copy(
                    todayRevisionCompleted = nowUnlocked,
                    todayFlashcardsCompleted = nowUnlocked,
                ),
            )
        }
    }

    fun startTodaysNewStudy() {
        _uiState.update { state ->
            if (!state.homeCommandState.isStudyGateUnlocked) return@update state
            val targetBatchId = state.primaryBatchId
            val subjects = state.pwVaultState.subjectsByBatch[targetBatchId].orEmpty()
            val allLectures = subjects.flatMap { s -> s.chapters.flatMap { c -> c.lectures } }
            val nextLecture = allLectures.firstOrNull { !it.isCompleted } ?: allLectures.firstOrNull()
            state.copy(
                activeTab = WorkspaceTab.VAULT,
                pwVaultState = state.pwVaultState.copy(
                    selectedBatchId = targetBatchId,
                    selectedSubjectId = nextLecture?.subjectId,
                    selectedChapterId = nextLecture?.chapterId,
                    selectedLectureId = nextLecture?.id,
                    activeSubView = VaultCategorySubView.HIERARCHY_BROWSE,
                    isCommandPaletteOpen = false,
                ),
            )
        }
    }

    // --- PW-Style Vault Actions ---

    fun selectPwVaultBatch(batchId: String) {
        // Keep Primary Batch cohesive across Home, Batches & Vault
        setPrimaryBatch(batchId)
    }

    fun selectPwVaultSubject(subjectId: String?) {
        _uiState.update { state ->
            state.copy(
                pwVaultState = state.pwVaultState.copy(
                    selectedSubjectId = subjectId,
                    selectedChapterId = null,
                    selectedLectureId = null,
                    selectedRevisionSubjectId = subjectId ?: state.pwVaultState.selectedRevisionSubjectId,
                ),
            )
        }
    }

    fun selectPwVaultChapter(chapterId: String?) {
        _uiState.update { state ->
            val current = state.pwVaultState
            val targetChapter = if (chapterId != null) {
                current.subjectsForSelectedBatch
                    .flatMap { it.chapters }
                    .firstOrNull { it.id == chapterId }
            } else null
            val resolvedSubjectId = targetChapter?.subjectId ?: current.selectedSubjectId
            val firstCardId = targetChapter?.lectures?.firstOrNull()?.flashcardDeck?.firstOrNull()?.id
            state.copy(
                pwVaultState = current.copy(
                    selectedSubjectId = resolvedSubjectId,
                    selectedChapterId = chapterId,
                    selectedLectureId = null,
                    expandedFlashcardChapterId = chapterId ?: current.expandedFlashcardChapterId,
                    previewFlashcardId = firstCardId ?: current.previewFlashcardId,
                    isPreviewCardFlipped = false,
                ),
            )
        }
    }

    fun selectPwVaultLecture(lectureId: String?) {
        _uiState.update { state ->
            val current = state.pwVaultState
            val lec = current.allLecturesInBatch.firstOrNull { it.id == lectureId }
            val updatedRecents = if (lec != null) {
                when (current.activeSubView) {
                    VaultCategorySubView.CLASS_NOTES -> current.copy(
                        recentNotesLectureIds = prependRecent(current.recentNotesLectureIds, lec.id),
                    )
                    VaultCategorySubView.FLASHCARDS -> current.copy(
                        recentFlashcardLectureIds = prependRecent(current.recentFlashcardLectureIds, lec.id),
                    )
                    VaultCategorySubView.REVISION -> current.copy(
                        recentRevisionLectureIds = prependRecent(current.recentRevisionLectureIds, lec.id),
                    )
                    VaultCategorySubView.HIERARCHY_BROWSE -> current
                }
            } else {
                current
            }
            state.copy(
                pwVaultState = updatedRecents.copy(
                    selectedSubjectId = lec?.subjectId ?: current.selectedSubjectId,
                    selectedChapterId = lec?.chapterId ?: current.selectedChapterId,
                    selectedLectureId = lectureId,
                ),
            )
        }
    }

    fun selectPwVaultSubView(subView: VaultCategorySubView) {
        _uiState.update { state ->
            val current = state.pwVaultState
            val defaultChapterId = current.expandedFlashcardChapterId
                ?: current.selectedChapterId
                ?: current.allChaptersInBatch.firstOrNull()?.id
            val defaultRevSubjectId = current.selectedRevisionSubjectId
                ?: current.selectedSubjectId
                ?: current.subjectsForSelectedBatch.firstOrNull()?.id
            state.copy(
                pwVaultState = current.copy(
                    activeSubView = subView,
                    // Open category modes at Subject Hierarchy + Recents cleanly
                    selectedSubjectId = null,
                    selectedChapterId = null,
                    selectedLectureId = null,
                    expandedFlashcardChapterId = defaultChapterId,
                    selectedRevisionSubjectId = defaultRevSubjectId,
                ),
            )
        }
    }

    fun toggleExpandedClassNote(lectureId: String) {
        _uiState.update { state ->
            val current = state.pwVaultState
            val nextExpanded = if (current.expandedClassNoteLectureId == lectureId) null else lectureId
            state.copy(
                pwVaultState = current.copy(
                    expandedClassNoteLectureId = nextExpanded,
                    recentNotesLectureIds = prependRecent(current.recentNotesLectureIds, lectureId),
                ),
            )
        }
    }

    fun toggleExpandedFlashcardChapter(chapterId: String) {
        _uiState.update { state ->
            val current = state.pwVaultState.expandedFlashcardChapterId
            state.copy(
                pwVaultState = state.pwVaultState.copy(
                    expandedFlashcardChapterId = if (current == chapterId) null else chapterId,
                ),
            )
        }
    }

    fun selectPreviewFlashcard(cardId: String?) {
        _uiState.update { state ->
            val current = state.pwVaultState
            val owningLectureId = current.allLecturesInBatch.firstOrNull { lec ->
                lec.flashcardDeck.any { it.id == cardId }
            }?.id
            state.copy(
                pwVaultState = current.copy(
                    previewFlashcardId = cardId,
                    isPreviewCardFlipped = false,
                    recentFlashcardLectureIds = if (owningLectureId != null) {
                        prependRecent(current.recentFlashcardLectureIds, owningLectureId)
                    } else {
                        current.recentFlashcardLectureIds
                    },
                ),
            )
        }
    }

    fun flipPreviewFlashcard() {
        _uiState.update { state ->
            state.copy(
                pwVaultState = state.pwVaultState.copy(
                    isPreviewCardFlipped = !state.pwVaultState.isPreviewCardFlipped,
                ),
            )
        }
    }

    fun selectPwVaultRevisionSubject(subjectId: String?) {
        _uiState.update { state ->
            state.copy(
                pwVaultState = state.pwVaultState.copy(
                    selectedRevisionSubjectId = subjectId,
                ),
            )
        }
    }

    fun openVaultCommandPalette(focus: VaultPaletteFocus) {
        _uiState.update { state ->
            state.copy(
                pwVaultState = state.pwVaultState.copy(
                    isCommandPaletteOpen = true,
                    paletteInitialFocus = focus,
                ),
            )
        }
    }

    fun closeVaultCommandPalette() {
        _uiState.update { state ->
            state.copy(
                pwVaultState = state.pwVaultState.copy(isCommandPaletteOpen = false),
            )
        }
    }

    fun updateVaultPaletteQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                pwVaultState = state.pwVaultState.copy(paletteSearchQuery = query),
            )
        }
    }

    fun selectVaultPaletteSubjectFilter(subjectId: String?) {
        _uiState.update { state ->
            state.copy(
                pwVaultState = state.pwVaultState.copy(paletteFilterSubjectId = subjectId),
            )
        }
    }

    fun selectVaultPaletteCategoryFilter(category: VaultCategorySubView?) {
        _uiState.update { state ->
            state.copy(
                pwVaultState = state.pwVaultState.copy(paletteFilterCategory = category),
            )
        }
    }

    fun clearVaultPaletteFilters() {
        _uiState.update { state ->
            state.copy(
                pwVaultState = state.pwVaultState.copy(
                    paletteSearchQuery = "",
                    paletteFilterSubjectId = null,
                    paletteFilterCategory = null,
                ),
            )
        }
    }

    fun jumpToVaultLectureFromPalette(
        lecture: VaultLectureNode,
        targetSubView: VaultCategorySubView,
    ) {
        _uiState.update { state ->
            val current = state.pwVaultState
            state.copy(
                pwVaultState = current.copy(
                    isCommandPaletteOpen = false,
                    selectedBatchId = lecture.batchId,
                    selectedSubjectId = lecture.subjectId,
                    selectedChapterId = lecture.chapterId,
                    selectedLectureId = lecture.id,
                    activeSubView = targetSubView,
                    expandedClassNoteLectureId = if (targetSubView == VaultCategorySubView.CLASS_NOTES) lecture.id else current.expandedClassNoteLectureId,
                    expandedFlashcardChapterId = if (targetSubView == VaultCategorySubView.FLASHCARDS) lecture.chapterId else current.expandedFlashcardChapterId,
                    previewFlashcardId = if (targetSubView == VaultCategorySubView.FLASHCARDS) lecture.flashcardDeck.firstOrNull()?.id else current.previewFlashcardId,
                    isPreviewCardFlipped = false,
                    selectedRevisionSubjectId = if (targetSubView == VaultCategorySubView.REVISION) lecture.subjectId else current.selectedRevisionSubjectId,
                    recentNotesLectureIds = if (targetSubView == VaultCategorySubView.CLASS_NOTES) prependRecent(current.recentNotesLectureIds, lecture.id) else current.recentNotesLectureIds,
                    recentFlashcardLectureIds = if (targetSubView == VaultCategorySubView.FLASHCARDS) prependRecent(current.recentFlashcardLectureIds, lecture.id) else current.recentFlashcardLectureIds,
                    recentRevisionLectureIds = if (targetSubView == VaultCategorySubView.REVISION) prependRecent(current.recentRevisionLectureIds, lecture.id) else current.recentRevisionLectureIds,
                ),
            )
        }
    }

    private fun prependRecent(existing: List<String>, id: String): List<String> {
        return (listOf(id) + existing.filterNot { it == id }).take(6)
    }

    // --- Layered Back Navigation ---

    fun navigateBackToHome() {
        _uiState.update { state ->
            when {
                state.isSettingsOpen ->
                    state.copy(isSettingsOpen = false)
                state.homeCommandState.activeDailyPage != null ->
                    state.copy(homeCommandState = state.homeCommandState.copy(activeDailyPage = null, isActiveFlashcardFlipped = false))
                state.inspectSelection != null -> state.copy(inspectSelection = null)
                state.activeTab == WorkspaceTab.BATCHES && state.batchesState.activeCrudDialog != null ->
                    state.copy(batchesState = state.batchesState.copy(activeCrudDialog = null))
                state.activeTab == WorkspaceTab.BATCHES && state.batchesState.isBuilderOpen ->
                    state.copy(batchesState = state.batchesState.copy(isBuilderOpen = false))
                state.activeTab == WorkspaceTab.VAULT && state.pwVaultState.isCommandPaletteOpen ->
                    state.copy(pwVaultState = state.pwVaultState.copy(isCommandPaletteOpen = false))
                state.activeTab == WorkspaceTab.VAULT && state.pwVaultState.selectedLectureId != null ->
                    state.copy(pwVaultState = state.pwVaultState.copy(selectedLectureId = null))
                state.activeTab == WorkspaceTab.VAULT && state.pwVaultState.selectedChapterId != null ->
                    state.copy(pwVaultState = state.pwVaultState.copy(selectedChapterId = null))
                state.activeTab == WorkspaceTab.VAULT && state.pwVaultState.selectedSubjectId != null ->
                    state.copy(pwVaultState = state.pwVaultState.copy(selectedSubjectId = null))
                state.activeTab == WorkspaceTab.VAULT && state.pwVaultState.activeSubView != VaultCategorySubView.HIERARCHY_BROWSE ->
                    state.copy(pwVaultState = state.pwVaultState.copy(activeSubView = VaultCategorySubView.HIERARCHY_BROWSE))
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

        val initialPwSubjectsByBatch = Phase161MockData.createPwVaultSubjectsByBatch()
        val initialBatches = Phase12MockData.createInitialBatches().map { baseBatch ->
            val vaultSubjects = initialPwSubjectsByBatch[baseBatch.id]
            if (vaultSubjects != null) {
                baseBatch.copy(subjects = buildBatchSubjectsFromVault(vaultSubjects))
            } else {
                baseBatch
            }
        }
        val firstBatch = initialBatches.first()
        val initialRevisionSheets = Phase13MockData.createRevisionSheets()
        val initialQuizQuestions = Phase13MockData.createQuizQuestions()
        val defaultQuizSubject = Phase13MockData.SUBJECT_PHYSICS_ID
        val firstPwSubject = initialPwSubjectsByBatch[firstBatch.id]?.firstOrNull()
        val firstPwChapter = firstPwSubject?.chapters?.firstOrNull()

        return WorkspaceUiState(
            activeTab = WorkspaceTab.HOME,
            accentMood = AccentMood.CYAN,
            focusMode = FocusMode.AI_READY,
            streakDays = 14,
            primaryBatchId = firstBatch.id,
            revisionBrief = brief,
            subjects = subjects,
            yesterdayTopics = yesterdayTopics,
            spacedReviews = spacedReviews,
            inspectSelection = null,
            homeCommandState = HomeCommandState(
                todayRevisionCompleted = false,
                todayFlashcardsCompleted = false,
                activeDailyPage = null,
                todayRevisionTakeaways = Phase161MockData.createYesterdayRevisionTakeaways(),
                todayFlashcards = Phase161MockData.createYesterdayFlashcards(),
            ),
            batchesState = BatchesUiState(
                batches = initialBatches,
                selectedBatchId = firstBatch.id,
                expandedSubjectId = firstBatch.subjects.firstOrNull()?.id,
                expandedChapterId = firstBatch.subjects.firstOrNull()?.chapters?.firstOrNull()?.id,
                isBuilderOpen = false,
            ),
            vaultState = VaultUiState(
                materials = Phase12MockData.createInitialVaultMaterials(),
                previewedMaterialId = "mat_phys_gauss_pdf",
            ),
            pwVaultState = PwVaultUiState(
                subjectsByBatch = initialPwSubjectsByBatch,
                selectedBatchId = firstBatch.id,
                expandedFlashcardChapterId = firstPwChapter?.id,
                previewFlashcardId = firstPwChapter?.lectures?.firstOrNull()?.flashcardDeck?.firstOrNull()?.id,
                selectedRevisionSubjectId = firstPwSubject?.id,
                recentNotesLectureIds = listOf("lec_phys_04", "lec_chem_03"),
                recentFlashcardLectureIds = listOf("lec_phys_04", "lec_chem_03"),
                recentRevisionLectureIds = listOf("lec_phys_04", "lec_chem_03"),
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
            flashcardsState = FlashcardsMistakesUiState(
                activeMode = FlashcardHubMode.FLASHCARD_DECK,
                allCards = Phase14MockData.createInitialFlashcards(),
                activeBucket = SpacedIntervalBucket.TODAY_X1,
                mistakes = Phase14MockData.createInitialMistakes(),
                selectedMistakeId = Phase14MockData.createInitialMistakes().firstOrNull()?.id,
            ),
            tutorPlannerState = TutorPlannerUiState(
                activeMode = TutorPlannerMode.AI_TUTOR,
                isCoPlanSplitEnabled = false,
                chapters = Phase15MockData.createChapters(),
                selectedChapterId = Phase15MockData.createChapters().first().id,
                messagesByChapter = Phase15MockData.createInitialMessages(),
                blocks = Phase15MockData.createInitialPlannerBlocks(),
            ),
        )
    }
}
